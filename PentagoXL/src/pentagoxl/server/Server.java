/* Generated by Together */
//Bug in server, als client verbindt, en connectie verbreekt zonder quit command, blijft de naam in gebruik
//Andere spelers winnen niet als iemand quit, waarschijnlijk ook memory leak.
package pentagoxl.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import pentagoxl.Client;
import pentagoxl.NetHandler;
import pentagoxl.ProtocolEndpoint;
import pentagoxl.spel.Spel;

public class Server extends Thread {

    private static boolean stop = false;
    private static int port;
    private static ServerSocket servSock;
    private static List<ServerClient> clientList = Collections.synchronizedList(new ArrayList<ServerClient>());
    private static Server instance;

    static void chat(String naam, String string) {
        for (Client c : getClientsWithCaps(ServerClient.CHAT))
            c.HANDLER.addMessage(ProtocolEndpoint.BCST_CHAT, naam, string);
    }

    /**
     * Creates an instance of Server, only needed for the run method.
     *
     * @param port The TCP port to listen on.
     */
    public Server(int port) {
        if (Server.instance != null)
            throw new IllegalStateException("I already exist somewhere!");
        Server.port = port;
    }

    /**
     * Called by the Java VM on startup.
     *
     * @param args the commandline arguments
     */
    public static void main(String[] args) {
        try {
            instance = new Server(Integer.parseInt(args[0]));
            instance.start();
        } catch (NumberFormatException e) {
            System.out.println("Enter a valid port number!");
            System.exit(1);
        }
    }

    @Override
    public void run() {
        try {
            servSock = new ServerSocket(port);
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE,
                    "Error while starting up. Exiting.", ex);
            System.exit(1);
        }
        while (!stop)
            try {
                clientList.add(new ServerClient(new NetHandler(servSock.accept())));
            } catch (IOException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
    }

    /**
     * Kills a client by its handler. All clients with this specific handler are
     * removed from the clients list and have their socket closed.
     *
     * @param handler The handler of the client to kill.
     */
    public static void killClient(NetHandler handler) {
        try {
            if (clientList.isEmpty())
                throw new IllegalArgumentException("There are no clients, wtf dude");

            for (Iterator<ServerClient> it = clientList.iterator(); it.hasNext();) {
                Client c = it.next();
                if (c.HANDLER == handler) {
                    if (c.getSpel() != null)
                        c.getSpel().kickClient(c);
                    it.remove();
                }
            }
            handler.getSocket().close();
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, "Unable to close connection", ex);
        }
    }

    /**
     * Requests a list of specific connected clients. <br /><br />First
     * argument: the type of game they are either waiting for or playing right
     * now<br /> Constraints: <tt>players >= -1 && players &lt;= 4 && players !=
     * 1</tt> <br /><br />When the argument is -1, a list of players opted in
     * for a random game is returned<br />When the argument is 0, a list of
     * players that are not playing any game is returned.<br /> When the
     * argument is 2..4 a list of players that are currently assembling, or are
     * currently playing a 2..4 player game.<br />Sending this command with -1,
     * 0, 2,3,4 and adding the result together results in a list of all
     * connected players.<br />
     *
     * @param optedIn list of players to return
     * @return list of players
     */
    public static List<Client> getClients(int optedIn) {
        List<Client> toRet = new ArrayList<Client>();
        for (Client c : clientList)
            if (c.clientsInGame == optedIn)
                toRet.add(c);

        return toRet;
    }
    
    /**
     * Returns a list with clients which have certain capabilities.
     * 
     * @param caps The capabilities bit-flag
     * @return A list with the clients.
     */
    public static List<Client> getClientsWithCaps(int caps) {
        List<Client> toRet = new ArrayList<Client>(clientList.size());
        for (ServerClient c : clientList)
            if ((c.getCaps() & caps) != 0)
                toRet.add(c);
        
        return toRet;
    }

    private static List<Client> getWaitingClients(int optedIn) {
        List<Client> toRet = Server.getClients(optedIn);
        for (Iterator<Client> it = toRet.iterator(); it.hasNext();) {
            Client c = it.next();
            if (c.getSpel() != null)
                it.remove();
        }
        return toRet;
    }

    /**
     * Returns a list with specific player names. See {@link #getClients(int) }
     * for more detailed information.
     *
     * @param optedIn the type of player to return
     * @return A list containing player names.
     */
    public static List<String> listPlayers(int optedIn) {
        List<Client> clients = Server.getClients(optedIn);
        List<String> toRet = new ArrayList<String>();
        for (Client c : clients)
            toRet.add(c.getNaam());

        return toRet;
    }

    /**
     * Returns all clients currently connected to this server.
     *
     * @return All clients currently connected to this server
     */
    public static List<String> getPlayerList() {
        List<String> toRet = new ArrayList<String>();
        for (Client c : clientList)
            toRet.add(c.getNaam());

        return toRet;
    }

    /**
     * Prints a message on the console
     *
     * @param msg
     */
    public static void logMessage(String msg) {
        Logger.getLogger(Server.class.getName()).info(msg);
    }

    static synchronized void createGames() {
        for (int numP = 4; numP >= 2; numP--) {
            List<Client> randP = Server.getWaitingClients(-1);
            List<Client> otherP = Server.getWaitingClients(numP);
            while (otherP.size() >= numP) {
                Spel s = new Spel();
                for (int i = 0; i < numP; i++) {
                    Client c = otherP.remove(0);
                    s.addClient(c);
                    c.setSpel(s);
                }
                s.start();
            }
            if (otherP.size() + randP.size() >= numP) { //otherP.size() < numP
                Spel s = new Spel();
                for (int i = 0; i < otherP.size(); i++) {
                    Client c = otherP.remove(0);
                    s.addClient(c);
                    c.setSpel(s);
                }
                while (s.getClients().size() < numP) { //fill rest of game
                    Client c = randP.remove(0);
                    s.addClient(c);
                    c.setSpel(s);
                }
                s.start();
            }
        }
    }
}

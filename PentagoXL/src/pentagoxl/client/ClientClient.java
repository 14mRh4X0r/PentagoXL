package pentagoxl.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import pentagoxl.Client;
import pentagoxl.NetHandler;
import pentagoxl.ProtocolEndpoint;
import pentagoxl.ProtocolError;
import pentagoxl.spel.Bord;
import pentagoxl.spel.Speler;
import pentagoxl.spel.Veld;

public class ClientClient extends Client {

    private List<Listener> listeners = new ArrayList<Listener>();
    private Bord bord;
    private Speler[] spelers;
    public final static String[] SUPPORTEDCMDS = {};

    /**
     * Constructs a new ClientClient, which uses handler to interact with a server
     * @param handler Hander to use to interact with server 
     * @require handler != null
     * @require pentagoxl.ProtocolEndpoint.CMD_HELLO is sent, after which an ack has returned
     */
    public ClientClient(String name, NetHandler handler) {
        super(handler);
        this.setNaam(name);
    }

    /**
     * Sends any commands it receives from handler to its listeners.
     */
    @Override
    public void onReceive(String cmd, String[] args) {
        if (ProtocolEndpoint.SRV_NACK.equals(cmd))
            handleNack(args);
        else if (ProtocolEndpoint.BCST_STARTGAME.equals(cmd))
            handleStartGame(args);
        else if (ProtocolEndpoint.BCST_TURN.equals(cmd))
            handleTurn(args);
        else if (ProtocolEndpoint.BCST_MOVE.equals(cmd))
            handleMove(args);
        else if (ProtocolEndpoint.BCST_ROTATE.equals(cmd))
            handleRotate(args);
        else if (ProtocolEndpoint.BCST_GAMEOVER.equals(cmd))
            handleGameOver(args);
        else if (ProtocolEndpoint.BCST_CHAT.equals(cmd))
            handleChat(args);

    }

    /**
     * Returns a deepcopy of the bord used by this client
     *
     * @return A deepcopy of bord
     */
    public Bord getBord() {
        return bord.deepcopy();
    }

    /*
     * Checks whether a ProtocolError.INVALID_MOVE was received, updates every listener if it was.
     */
    private void handleNack(String[] args) {
        if (args[0].equals(ProtocolError.INVALID_MOVE.getCode() + "")) {
            List<Speler> winnaars = new ArrayList<Speler>(spelers.length - 1);
            for (Speler s : spelers)
                if (!s.getNaam().equals(this.getNaam()))
                    winnaars.add(s);
            updateGameOver(winnaars.toArray(new Speler[0]));
        }
    }

    /*
     * Stops the connection
     */
    private void closeSocket() {
        try {
            HANDLER.getSocket().close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
     * Sets a Veld for every other player.
     */
    private void handleStartGame(String[] args) {
        bord = new Bord();
        if (args.length > 4 || args.length < 2)
            closeSocket();
        spelers = new Speler[args.length];
        for (int i = 0; i < args.length; i++) {
            spelers[i] = new Speler(args[i]);
            spelers[i].setVeld(Veld.byIndex(i));
        }
        updateStart();

    }

    /*
     * Closes the socket if this this.spelers == null
     * executes updateTurn if the argument received was this.getNaam()
     */
    private void handleTurn(String[] args) {
        if (spelers == null)
            closeSocket();
        if (this.getNaam().equals(args[0]))
            updateTurn();


    }

    /*
     * Sets the move received on the bord.
     * updateBord() afterwards
     */
    private void handleMove(String[] args) {
        for (Speler s : spelers)
            if (s.getNaam().equals(args[0]))
                bord.doeMove(s.getVeld(), Integer.parseInt(args[1]));
        updateBord();
    }

    /*
     * Sets the rotate received on the bord.
     * updateBord() afterwards
     */
    private void handleRotate(String[] args) {
        int vak = Integer.parseInt(args[0]) + 1;
        vak *= (args[1].equals(ProtocolEndpoint.DIRECTION_COUNTERCLOCKWISE) ? -1 : 1);
        bord.doeRotate(vak);
        updateBord();
    }

    /*
     * Adds every argument to an array and updateGameOver(array) afterwards
     */
    private void handleGameOver(String[] args) {
        List<Speler> winnaars = new ArrayList<Speler>(4);
        if (args != null)
            for (String s : args)
                for (Speler sp : spelers)
                    if (sp.getNaam().equals(s))
                        winnaars.add(sp);
        updateGameOver(winnaars.toArray(new Speler[0]));
    }

    /*
     * Puts args[0] to a player, then updates every listener with chatReceived(speler, args[1])
     */
    private void handleChat(String[] args) {
        if (args != null && args.length >= 2) {
            Speler zegger = null;
            for (Speler sp : spelers)
                if (sp.getNaam().equals(args[0]))
                    zegger = sp;

            if (zegger != null)
                for (Listener l : listeners)
                    l.chatReceived(zegger, args[1]);
        }
    }

    /*
     * updates every listener with doTurn()
     */
    private void updateTurn() {
        for (Listener l : listeners)
            l.doTurn();
    }

    /*
     * updates every listener with gameStarting(spelers)
     */
    private void updateStart() {
        for (Listener l : listeners)
            l.gameStarting(spelers);
    }

    /*
     * updates every listener with bordChanged()
     */
    private void updateBord() {
        for (Listener l : listeners)
            l.bordChanged();
    }

    /*
     * updates every listener with gameOver(winnaars)
     */
    private void updateGameOver(Speler[] winnaars) {
        for (int i = 0; i < listeners.size(); i++) {
            Listener l = listeners.get(i);
            l.gameOver(winnaars);
        }
    }

    /**
     * Adds <tt>listener</tt> to the list which will be updated with every method in onReceive 
     * @param listener Listener to be added
     */
    public void addListener(Listener listener) {
        listeners.add(listener);
    }

    /**
     * Removes <tt>listener</tt> from the list which will be updated with every method in onReceive
     * @param listener Listener to be added
     */
    public void removeListener(Listener listener) {
        listeners.remove(listener);
    }

    /**
     * Listener interface for ClientClient
     *
     * @author Wilco Wolters
     *
     */
    public interface Listener {

        /**
         * Called when the listener should send a move to the server
         */
        public void doTurn();

        /**
         * Called when a game is starting
         *
         * @param spelers Spelers in the game
         */
        public void gameStarting(Speler[] spelers);

        /**
         * Called when the bord changes
         */
        public void bordChanged();

        /**
         * Called when the game is over.
         *
         * @param winnaars Winners of the game.
         */
        public void gameOver(Speler[] winnaars);

        /**
         * Called when a chatmessage is received
         *
         * @param whosaid Player who sent the message
         * @param text Text received as chat
         */
        public void chatReceived(Speler whosaid, String text);
    }

    /**
     * Sends a command to the server to join
     *
     * @param naam Name to use when joining
     * @deprecated
     */
    public void sendHello(String naam) {
        List<String> args = new ArrayList<String>();
        args.add(naam);
        args.addAll(Arrays.asList(SUPPORTEDCMDS));
        HANDLER.addMessage(ProtocolEndpoint.CMD_HELLO, args.toArray(new String[0]));
    }

    /**
     * Send a move to the server. Does a clientside check whether the move is
     * allowed. <BR> (<tt>That is, getBord().isLeegVeld(vak)</tt>)
     * @param vak Where to place the move.
     * @return true when move is allowed
     */
    public boolean sendMove(int vak) {
        if (bord.isLeegVeld(vak)) {
            HANDLER.addMessage(ProtocolEndpoint.CMD_MOVE, vak + "");
            return true;
        } else
            return false;
    }

    /**
     * Sends a rotate command to the server.
     *
     * @param vak Vak to rotate
     * @param direction Direction to rotate in.
     * @see pentagoxl.ProtocolEndpoint#CMD_ROTATE
     */
    public void sendRotate(int vak, String direction) {
        HANDLER.addMessage(ProtocolEndpoint.CMD_ROTATE, vak + "", direction);
    }

    /**
     * Sends a quitcommand to the server.
     */
    public void sendQuit() {
        HANDLER.addMessage(ProtocolEndpoint.CMD_QUIT);
    }

    /**
     * Sends a chat message to the server
     * @param message Message to be sent
     */
    public void sendChat(String message) {
        HANDLER.addMessage(ProtocolEndpoint.CMD_CHAT, message);
    }
}

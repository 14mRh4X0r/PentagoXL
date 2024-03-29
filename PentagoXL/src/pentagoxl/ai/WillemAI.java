package pentagoxl.ai;

import java.net.InetAddress;
import java.net.Socket;
import pentagoxl.ProtocolEndpoint;
import pentagoxl.spel.Bord;

public class WillemAI extends AI {

    public WillemAI(Socket sock, String playWith, String name) {
        super(name, sock, playWith);
    }

    @Override
    public void doTurn() {
        int prevTurn = 0;
        Bord b = myClient.getBord();
        while (!b.isLeegVeld(Bord.convertVeld(prevTurn)))
            prevTurn++;
        myClient.sendMove(Bord.convertVeld(prevTurn++));
        myClient.sendRotate((int) (Math.random() * 9), Math.random() > .5 ? ProtocolEndpoint.DIRECTION_CLOCKWISE : ProtocolEndpoint.DIRECTION_COUNTERCLOCKWISE);
    }

    /**
     * Starts an ai. Usage: <ip> <port> <amountofplayerstoplaywith>
     */
    public static void main(String[] args) {
        if (args.length == 4 && (args[2].equals("-1") || args[2].equals("2") || args[2].equals("3") || args[2].equals("4")))
            try {
                InetAddress ia = InetAddress.getByName(args[0]);
                Socket sock = new Socket(ia, Integer.parseInt(args[1]));
                new WillemAI(sock, args[2], args[3]);
            } catch (Exception e) {
                usage();
            }
        else
            usage();
    }

    private static void usage() {
        System.out.println("Usage: <ip> <port> <amountofplayerstoplaywith>");
    }
}

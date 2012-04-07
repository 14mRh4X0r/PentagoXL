package pentagoxl.ai;

import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import pentagoxl.ProtocolEndpoint;
import pentagoxl.spel.Bord;

public class RandomAI extends AI {
	
	public RandomAI(Socket sock, String playWith, String name) {
		super(name, sock, playWith);
	}

	/**
	 * Sends a move and a rotate to the server.
	 * The moves are randomly chosen
	 */
	@Override
	public void doTurn() {
		Bord b = myClient.getBord();
		List<Integer> legeVakken = new ArrayList<Integer>();
		for (int i = 0; i < 81; i++) {
			if (b.isLeegVeld(i)) {
				legeVakken.add(i);
			}
		}
		myClient.sendMove(legeVakken.get((int)(Math.random() * legeVakken.size())));
		myClient.sendRotate((int)(Math.random() * 9), Math.random() > .5 ? ProtocolEndpoint.DIRECTION_CLOCKWISE : ProtocolEndpoint.DIRECTION_COUNTERCLOCKWISE);
	}
	
	/**
	 * Starts an ai.
	 * Usage: <ip> <port> <amountofplayerstoplaywith>
	 */
	public static void main(String[] args) {
		if (args.length == 4 && (args[2].equals("-1") || args[2].equals("2") || args[2].equals("3") || args[2].equals("4"))) {		
			try {
				InetAddress ia = InetAddress.getByName(args[0]);
				Socket sock = new Socket(ia, Integer.parseInt(args[1]));
				new RandomAI(sock, args[2], args[3]);
			} catch (Exception e) {
				usage();
			}
		} else {
			usage();
		}
	}
	
	private static void usage(){
		System.out.println("Usage: <ip> <port> <amountofplayerstoplaywith> <name>");
	}

}

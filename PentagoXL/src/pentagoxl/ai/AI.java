package pentagoxl.ai;

import java.io.IOException;
import java.net.Socket;

import pentagoxl.NetHandler;
import pentagoxl.ProtocolEndpoint;
import pentagoxl.ProtocolError;
import pentagoxl.client.ClientClient;
import pentagoxl.spel.Speler;

public abstract class AI implements NetHandler.Listener, ClientClient.Listener{

    private boolean nacked = false;
    private boolean acked = false;
    private ProtocolError nack;
    protected ClientClient myClient;
	
    /**
     * Constructs a new AI and connects to a server.
     * @param name Name to use to connect with the server
     * @param sock Socket to connect to the server with
     * @param playWith Amount of people to play with, -1 for don't care.
     */
	public AI(String name, Socket sock, String playWith) {
		try {
			NetHandler nh = new NetHandler(sock);
			nh.addListener(this);
			nh.addMessage(ProtocolEndpoint.CMD_HELLO, name);
			while (!acked && !nacked) {
				synchronized(this) {
					this.wait();
				}
			}
			if (nacked) {
				System.out.println("We didn't get accepted by the server.");
				System.out.println(nack.getDescription());
			} else {
				nh.addMessage(ProtocolEndpoint.CMD_JOIN, playWith);
			}
			nh.removeListener(this);
			myClient = new ClientClient(name, nh);
			myClient.addListener(this);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void gameStarting(Speler[] spelers) {
		//We don't give a damn
	}

	@Override
	public void bordChanged() {
		//Meh
		
	}
	
	@Override
	public void gameOver(Speler[] winnaars){
		myClient.removeListener(this);
		boolean won = false;
		for (Speler s : winnaars) {
			if (s.getNaam().equals(myClient.getNaam())) {
				System.out.println("We won");
				won = true;
			}
		}
		if (!won) {
			System.out.println("We lost");
		}
	}

	@Override
	public synchronized void onReceive(String cmd, String[] args) {
		if (cmd.equals(ProtocolEndpoint.SRV_NACK)) {
			nacked = true;
			if (args.length > 0) {
				try {
					int errCode = Integer.parseInt(args[0]);
					nack = ProtocolError.lookupCode(errCode);
				} catch (NumberFormatException e) {
					nack = ProtocolError.UNSPECIFIED;
				}
			}
			notifyAll();
		} else if (cmd.equals(ProtocolEndpoint.SRV_ACK)) {
			acked = true;
			notifyAll();
		}
		
	}
	
	@Override
	public void chatReceived(Speler whosaid, String text) {
		//This is the amount of fucks I give
	}

}

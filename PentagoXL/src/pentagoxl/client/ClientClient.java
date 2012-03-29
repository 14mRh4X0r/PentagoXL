package pentagoxl.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import pentagoxl.Client;
import pentagoxl.NetHandler;
import pentagoxl.ProtocolEndpoint;
import pentagoxl.spel.Bord;
import pentagoxl.spel.Speler;
import pentagoxl.spel.Veld;

public class ClientClient extends Client {

	private List<Listener> listeners = new ArrayList<Listener>();
	
	private Bord bord;
	private Speler[] spelers;
	private int currentTurn;
	private int myTurn;
	
	public ClientClient(NetHandler handler) {
		super(handler);
	}

	@Override
	public void onReceive(String cmd, String[] args) {
		if (ProtocolEndpoint.SRV_NACK.equals(cmd)) {
			handleNack(args);
		} else if (ProtocolEndpoint.BCST_STARTGAME.equals(cmd)) {
			handleStartGame(args);
		} else if (ProtocolEndpoint.BCST_TURN.equals(cmd)) {
			handleTurn(args);
		} else if (ProtocolEndpoint.BCST_MOVE.equals(cmd)) {
			handleMove(args);
		} else if (ProtocolEndpoint.BCST_ROTATE.equals(cmd)) {
			handleRotate(args);
		} else if (ProtocolEndpoint.BCST_GAMEOVER.equals(cmd)) {
			handleGameOver(args);
		}

	}
	
	public Bord getBord() {
		return bord;
	}
	
	private void handleNack(String[] args) {
		
	}
	
	private void closeSocket() {
		try {
			HANDLER.getSocket().close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void handleStartGame(String[] args) {
		bord = new Bord();
		if (args.length > 4 || args.length < 2)
			closeSocket();
		spelers = new Speler[args.length];
		for (int i = 0; i < args.length; i++) {
			spelers[i] = new Speler(args[i]);
			spelers[i].setVeld(Veld.byIndex(i));
			if (args[i].equals(getNaam())) {
				myTurn = i;
			}
		}
		updateStart();
		
	}
	
	private void handleTurn(String[] args) {
		if (spelers == null)
			closeSocket();
		for (int i = 0; i < spelers.length; i++) {
			if (spelers[i].getNaam().equals(args[0])) {
				currentTurn = i;
			}
		}
		if (currentTurn == myTurn) {
			updateTurn();
		}
		
	}
	
	private void handleMove(String[] args) {
		for (Speler s : spelers) {
			if (s.getNaam().equals(args[0])) {
				bord.doeMove(s.getVeld(), Integer.parseInt(args[1]));
			}
		}
		updateBord();
	}
	
	private void handleRotate(String[] args) {
		int vak = Integer.parseInt(args[0]) + 1;
		vak *= (args[1].equals(ProtocolEndpoint.DIRECTION_COUNTERCLOCKWISE) ? -1 : 1);
		bord.doeRotate(vak);
		updateBord();
	}
	
	private void handleGameOver(String[] args) {
		
	}
	
	private void updateTurn(){
		for (Listener l : listeners) {
			l.doTurn();
		}
	}
	
	private void updateStart() {
		for (Listener l : listeners) {
			l.gameStarting(spelers);
		}
	}
	
	private void updateBord() {
		for (Listener l : listeners) {
			l.bordChanged();
		}
	}
	
	private void updateGameOver(Speler[] winnaars) {
		for (Listener l : listeners) {
			
		}
	}
	
	public void addListener(Listener listener) {
		listeners.add(listener);
	}
	
	public interface Listener {
		public void doTurn();
		public void gameStarting(Speler[] spelers);
		public void bordChanged();
	}

}

/* Generated by Together */

package pentagoxl.spel;

import java.util.List;
import java.util.Observable;
import pentagoxl.*;

public class Spel extends Observable {
	
	public Spel() {
		bord = new Bord();
		zetIsAan = 0;
		clients.get(zetIsAan).canMove = true;
		speel();
	}
	
    public Bord getBord() { return bord; }

    public List<Client> getClients(){ return clients; }

    public void addClient(Client client){ 
    	this.clients.add(client);
    }

    public boolean isOver() {
    	return false;
    }

    public void doeMove(int plek) {
    	
    }
    
    /**
     * Rotates one of the fields. <BR />
     * The fields are numbered: <BR /><tt>
     * 1 | 2 | 3 <BR />
     * 4 | 5 | 6 <BR />
     * 7 | 8 | 9 <BR /></tt>
     * Negativeness is used to rotate CCW.
     * @param rotate Field to rotate
     */
    public void doeRotate(int rotate) {
    	int hok = rotate * Integer.signum(rotate) - 1;
    	boolean klokmee = Integer.signum(rotate) == 1;
    	bord.getHok(hok).draai(klokmee);
    }
    
    public void kickClient(Client client) {
    	clients.remove(client);
    	//TODO end game
    }
    
    
    private void speel(){
    	broadcast(ProtocolEndpoint.BCST_TURN, clients.get(zetIsAan).getNaam());
    	clients.get(zetIsAan).doeZet(bord);
    }
    
    public void broadcast(String cmd, String... args) {
    	for (Client c : clients) {
    		c.HANDLER.addMessage(cmd, args);
    	}
    }
    
    public boolean zetIsAanClient(Client client) {
    	return clients.get(zetIsAan) == client;
    }

    private Bord bord;
    private List<Client> clients;
    private int zetIsAan;
}

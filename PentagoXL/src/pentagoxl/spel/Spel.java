/* Generated by Together */
package pentagoxl.spel;

import java.util.List;
import java.util.Observable;
import pentagoxl.Client;
import pentagoxl.ProtocolEndpoint;

public class Spel extends Observable {

    private Bord bord;
    private List<Client> clients;
    private int zetIsAan;

    public Spel() {
        bord = new Bord();
        zetIsAan = 0;
        clients.get(zetIsAan).canMove = true;
        speel();
    }

    /**
     * Returns the <tt>Bord</tt> of this Spel.
     * @return 
     */
    public Bord getBord() {
        return bord;
    }

    /**
     * Returns a list of clients connec
     * @return 
     */
    public List<Client> getClients() {
        return clients;
    }

    public void addClient(Client client) {
        this.clients.add(client);
    }

    public boolean isOver() {
        return false;
    }

    private void doeMove(int plek) {
    }

    /**
     * Rotates one of the fields. <BR /> The fields are numbered: <BR /><tt> 1 |
     * 2 | 3 <BR /> 4 | 5 | 6 <BR /> 7 | 8 | 9 <BR /></tt> Negativeness is used
     * to rotate CCW.
     *
     * @param rotate Field to rotate
     */
    private void doeRotate(int rotate) {
        int hok = rotate * Integer.signum(rotate) - 1;
        boolean klokmee = Integer.signum(rotate) == 1;
        bord.getHok(hok).draai(klokmee);
    }

    /**
     * Kicks the given client
     *
     * @param client
     */
    public void kickClient(Client client) {
        clients.remove(client);
        this.broadcast(ProtocolEndpoint.BCST_GAMEOVER);
        for (Client c : this.getClients())
            c.setSpel(null);
    }

    private void speel() {
        broadcast(ProtocolEndpoint.BCST_TURN, clients.get(zetIsAan).getNaam());
        int zet[] = clients.get(zetIsAan).doeZet(bord);
        
    }

    public void broadcast(String cmd, String... args) {
        for (Client c : clients)
            c.HANDLER.addMessage(cmd, args);
    }

    public boolean zetIsAanClient(Client client) {
        return clients.get(zetIsAan) == client;
    }
}

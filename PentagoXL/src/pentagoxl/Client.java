/* Generated by Together */
package pentagoxl;

import pentagoxl.spel.Bord;
import pentagoxl.spel.Speler;

public class Client extends Speler implements NetHandler.Listener {

    private String name;
    private boolean canChat, canBeChallenged;

    public Client(NetHandler handler) {
        super(handler);
    }

    @Override
    public int[] doeZet(Bord bord) {
        // TODO add logic
        return new int[]{0, 0};
    }

    @Override
    public String getNaam() {
        return name;
    }

    @Override
    public void onReceive(String cmd, String[] args) {
        // TODO add logic.
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pentagoxl.ai;

import java.util.Random;
import pentagoxl.NetHandler;
import pentagoxl.spel.Bord;

/**
 *
 * @author willem
 */
public class RandomAI extends AI {
    private Random random = new Random();

    public RandomAI(NetHandler handler) {
        super(handler);
    }

    @Override
    public int[] doeZet(Bord bord) {
        int vakje, hok;
        do {
            vakje = random.nextInt(Bord.VELDEN);
        } while (!bord.isLeegVeld(vakje));
        hok = random.nextInt(Bord.HOKKEN);
        
        // We're lazy and use negativeness for CCW, thus requiring hok != 0
        hok++;
        if (random.nextBoolean())
            hok = -hok;
        
        return new int[] {vakje, hok};
    }

    @Override
    public String getNaam() {
        return "I'm fucking random!";
    }

    @Override
    public void onReceive(String cmd, String[] args) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}

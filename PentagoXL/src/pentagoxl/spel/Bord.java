/* Generated by Together */

package pentagoxl.spel;

public class Bord {
    public Hok[] getHokken(){ return hokken; }

    public Hok getHokken(int index){ return hokken[index]; }

    public boolean heeftWinnaar() {
		return false;
    }

    public Speler getWinnaar() {
		return null;
    }
    
    private Hok[] hokken;
}

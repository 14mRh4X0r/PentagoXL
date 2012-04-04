/* Generated by Together */
package pentagoxl.spel;

public class Hok {

    private Veld[] velden;

    public Hok() {
        velden = new Veld[9];
        for (int i = 0; i < 9; i++)
            velden[i] = Veld.LEEG;
    }

    public Veld getVeld(int index) {
        return velden[index];
    }

    public Veld[] getVelden() {
        return velden;
    }

    public void draai(boolean klokMee) {
        Veld[] oud = velden.clone();
        if (klokMee) {
            velden[0] = oud[6];
            velden[1] = oud[3];
            velden[2] = oud[0];
            velden[3] = oud[7];
            velden[5] = oud[1];
            velden[6] = oud[8];
            velden[7] = oud[5];
            velden[8] = oud[2];
        } else {
            velden[0] = oud[2];
            velden[1] = oud[5];
            velden[2] = oud[8];
            velden[3] = oud[1];
            velden[5] = oud[7];
            velden[6] = oud[0];
            velden[7] = oud[3];
            velden[8] = oud[6];
        }

    }

    @Override
    public String toString() {
        String toRet = "";
        for (Veld v : velden)
            toRet += ", " + v.toString();
        return toRet.substring(2);
    }
}

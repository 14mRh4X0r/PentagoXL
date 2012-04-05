/* Generated by Together */
package pentagoxl.spel;

import java.util.ArrayList;
import java.util.List;
import pentagoxl.server.Server;

public class Bord {

    public Bord() {
        hokken = new Hok[9];
        for (int i = 0; i < HOKKEN; i++)
            hokken[i] = new Hok();
    }
    private Hok[] hokken;
    public static final int HOKKEN = 9,
            VELDEN_PER_HOK = 9,
            VELDEN = HOKKEN * VELDEN_PER_HOK;

    public Hok[] getHokken() {
        return hokken;
    }

    public Hok getHok(int index) {
        return hokken[index];
    }

    public boolean heeftWinnaar() {
        return getWinnaars().length > 0;
    }

    public Veld[] getWinnaars() {
        List<Veld> winnaars = new ArrayList<Veld>();
        for (Veld v : Veld.values())
            if (heeftDiagonaal(v) || heeftVerticaal(v) || heeftHorizontaal(v))
                winnaars.add(v);
        return winnaars.toArray(new Veld[0]);
    }

    public boolean isLeegVeld(int i) {
        return this.getHok(i / VELDEN_PER_HOK).getVeld(i % HOKKEN) == Veld.LEEG;
    }

    public Veld getVeld(int i) {
        return this.getHok(i / VELDEN_PER_HOK).getVeld(i % HOKKEN);
    }

    public Bord deepcopy() {
        Bord b = new Bord();
        for (int i = 0; i < HOKKEN; i++)
            System.arraycopy(this.hokken[i].getVelden(), 0, b.hokken[i].getVelden(), 0, VELDEN_PER_HOK);
        return b;
    }

    /**
     * Doet een zet.
     *
     * @param v Het veld om te zetten.
     * @param veld De index van het veld om te zetten.
     */
    public void doeMove(Veld v, int veld) {
        this.getHok(veld / VELDEN_PER_HOK).getVelden()[veld % HOKKEN] = v;
    }

    /**
     * Rotates a <tt>Hok</tt> <br> Uses our custom notation
     *
     * @param rotate hok+1 to rotate. Negativeness is used for CCW.
     */
    public void doeRotate(int rotate) {
        int hok = Math.abs(rotate) - 1;
        this.getHok(hok).draai(rotate > 0);
    }

    private boolean heeftHorizontaal(Veld v) {
        int maxOpvolgend = 0;
        for (int i = 0; i < VELDEN_PER_HOK; i++) {
            int opvolgend = 0;
            for (int j = 0; j < VELDEN_PER_HOK; j++) {
                int veldNummer = VELDEN_PER_HOK * i + j;
                if (getVeld(convertVeld(veldNummer)) == v)
                    opvolgend += 1;
                else {
                    if (opvolgend > maxOpvolgend)
                        maxOpvolgend = opvolgend;
                    opvolgend = 0;
                }
            }
        }
        return maxOpvolgend >= 5;
    }

    private boolean heeftVerticaal(Veld v) {
        int maxOpvolgend = 0;
        for (int i = 0; i < VELDEN_PER_HOK; i++) {
            int opvolgend = 0;
            for (int j = 0; j < VELDEN_PER_HOK; j++) {
                int veldNummer = VELDEN_PER_HOK * j + i;
                if (getVeld(convertVeld(veldNummer)) == v)
                    opvolgend += 1;
                else {
                    if (opvolgend > maxOpvolgend)
                        maxOpvolgend = opvolgend;
                    opvolgend = 0;
                }
            }
        }
        return maxOpvolgend >= 5;
    }

    private boolean heeftDiagonaal(Veld v) {
        int maxOpvolgend = 0;
        for (int i = 0; i < VELDEN; i++) {
            int opvolgend = 0;
            if (i / 9 == 0 || i % 9 == 0) {
                int opvveld = 0;
                while (opvveld + 8 < VELDEN && opvveld % 9 != 8) {
                    opvveld += 8;
                    if (getVeld(convertVeld(opvveld)) == v)
                        opvolgend++;
                    else {
                        if (opvolgend > maxOpvolgend)
                            maxOpvolgend = opvolgend;
                        opvolgend = 0;
                    }
                }
                while (opvveld + 10 < VELDEN && opvveld % 9 != 8) {
                    opvveld += 10;
                    if (getVeld(convertVeld(opvveld)) == v)
                        opvolgend++;
                    else {
                        if (opvolgend > maxOpvolgend)
                            maxOpvolgend = opvolgend;
                        opvolgend = 0;
                    }
                }
            }
        }
        return maxOpvolgend >= 5;
    }

    /**
     * Converts a number from a row-based numbering to the official numbering
     *
     * @param v value to convert
     * @return A number based on the official numbering
     */
    public static int convertVeld(int v) {
        int hokRow = v / 27;
        int vakRow = v % 27 / 9;
        int linePos = v % 9;
        int hokCol = linePos / 3;
        int vakCol = linePos % 3;
        return VELDEN_PER_HOK * (3 * hokRow + hokCol) + 3 * vakRow + vakCol;
    }
}

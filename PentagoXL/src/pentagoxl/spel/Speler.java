/* Generated by Together */
package pentagoxl.spel;

public class Speler {

    public Speler() {
    }
    
    public Speler(String naam) {
        this.name = naam;
    }

    public String getNaam() {
    	return name;
    }
    
    public void setNaam(String name) {
    	this.name = name;
    }

    public int getAantalKnikkers() {
        return aantalKnikkers;
    }

    public Veld getVeld() {
        return veld;
    }

    public void setVeld(Veld veld) {
        this.veld = veld;
    }
    
    public void setSpel(Spel spel) {
    	this.spel = spel;
    }

    public Spel getSpel() {
        return spel;
    }

    private Veld veld;
    protected int aantalKnikkers;
    private String name = null;
    protected Spel spel;
}

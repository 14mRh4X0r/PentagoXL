/* Generated by Together */
package pentagoxl;

import pentagoxl.spel.Speler;

public abstract class Client extends Speler implements NetHandler.Listener {

    public Client(NetHandler handler) {
        this.HANDLER = handler;
        this.HANDLER.addListener(this);
    }
    
    /** Used to detect whether this client can do a move. */
    public boolean canMove = false;
    /** Used to detect whether this client can rotate. */
    public boolean canRotate = false;
    
    /** The <tt>NetHandler</tt> for this client. */
    public final NetHandler HANDLER;
    /**
     * Holds with how many other players this client wants to play a game. <BR />
     * This should be 0 when this client is waiting in the lobby. <BR />
     * This should be -1 when this client opted in for a game with a random amount of players <BR />
     * This should be 1 when this client has not set a name yet. <BR />
     * This should hold 2, 3, or 4 when this client opted in for a game with respectively 2, 3 or 4 players.
     */
    public int clientsInGame = 1;
}

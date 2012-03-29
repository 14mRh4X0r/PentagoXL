package pentagoxl.server;

import java.util.logging.Level;
import java.util.logging.Logger;
import pentagoxl.Client;
import pentagoxl.NetHandler;
import pentagoxl.ProtocolEndpoint;
import pentagoxl.ProtocolError;
import pentagoxl.spel.Bord;

public class ServerClient extends Client {
    private int lastMove = -1, lastRotate = -1;

    public ServerClient(NetHandler handler) {
        super(handler);
    }

    @Override
    public synchronized void onReceive(String cmd, String[] args) {
        if (this.getNaam() == null)
            if (ProtocolEndpoint.CMD_HELLO.equals(cmd))
                this.handleHello(args);
            else
                this.HANDLER.addNack(ProtocolError.UNEXPECTED_CMD);
        else if (ProtocolEndpoint.CMD_CHALLENGE_ACCEPT.equals(cmd)
                || ProtocolEndpoint.CMD_CHALLENGE_DECLINE.equals(cmd))
            this.handleChallengeResponse(ProtocolEndpoint.CMD_CHALLENGE_ACCEPT.equals(cmd), args);
        else if (ProtocolEndpoint.CMD_CHAT.equals(cmd))
            this.handleChat(args);
        else if (ProtocolEndpoint.CMD_LIST.equals(cmd))
            this.handleList(args);
        else if (ProtocolEndpoint.CMD_QUIT.equals(cmd))
            this.handleQuit(args);
        else if (this.spel == null)
            if (this.clientsInGame == 1)
                if (ProtocolEndpoint.CMD_JOIN.equals(cmd))
                    this.handleJoin(args);
                else if (ProtocolEndpoint.CMD_OBSERVE.equals(cmd))
                    this.handleObserve(args);
                else if (ProtocolEndpoint.CMD_CHALLENGE.equals(cmd))
                    this.handleChallenge(args);
                else
                    this.HANDLER.addNack(ProtocolError.UNEXPECTED_CMD);
            else
                this.HANDLER.addNack(ProtocolError.UNEXPECTED_CMD);
        else if (this.spel.zetIsAanClient(this))
            if (ProtocolEndpoint.CMD_MOVE.equals(cmd) && this.canMove)
                this.handleMove(args);
            else if (ProtocolEndpoint.CMD_ROTATE.equals(cmd) && this.canRotate)
                this.handleRotate(args);
            else
                this.HANDLER.addNack(ProtocolError.UNEXPECTED_CMD);
        else
            this.HANDLER.addNack(ProtocolError.COMMAND_UNSUPPORTED);


        /*
        if (ProtocolEndpoint.CMD_HELLO.equals(cmd) && getNaam() == null)				//CMD_HELLO logic
            if (args.length == 1)														//Check if name was sent
                if (args[0].equalsIgnoreCase(ProtocolEndpoint.SERVER_NAME) || args[0].length() > 25)		//Check for length of name
                    HANDLER.addNack(ProtocolError.INVALID_USER);
                else if (Server.getPlayerList().contains(args[0]))				//Check if name is already taken
                    HANDLER.addNack(ProtocolError.USERNAME_TAKEN);
                else
                    this.setNaam(args[0]); //Set name
            else
                HANDLER.addNack(ProtocolError.INVALID_ARGUMENT_COUNT);
        else if (ProtocolEndpoint.CMD_HELLO.equals(cmd) || //Check if an unexpected command is sent.
                (!ProtocolEndpoint.CMD_HELLO.equals(cmd) && getNaam() == null))
            HANDLER.addNack(ProtocolError.UNEXPECTED_CMD);
        else if (ProtocolEndpoint.CMD_MOVE.equals(cmd) || ProtocolEndpoint.CMD_ROTATE.equals(cmd)) //Move command
            if (spel.zetIsAanClient(this))												//Check if the turn is this client's
                if (ProtocolEndpoint.CMD_MOVE.equals(cmd) && canMove)					//Check if this client can do a move
                    if (args.length == 1)												//Check if amount of arguments is correct
                        try {
                            int zet = Integer.parseInt(args[0]);
                            if (zet >= 0 && zet <= 80)								//Check if argument is in range
                                if (spel.getBord().isLeegVeld(zet))
                                    spel.doeMove(zet); //Make the move
                                else
                                    throw new NumberFormatException();
                            else
                                throw new NumberFormatException();
                        } catch (NumberFormatException e) {
                            HANDLER.addNack(ProtocolError.INVALID_MOVE);
                            spel.kickClient(this);
                        }
                    else
                        HANDLER.addNack(ProtocolError.INVALID_ARGUMENT_COUNT); //Send error
                else if (ProtocolEndpoint.CMD_ROTATE.equals(cmd) && canRotate) {		//Check if this client can do a rotate
                    if (args.length == 2)												//Check if amount of arguments is correct
                        try {
                            int vak = Integer.parseInt(args[0]);
                            if (vak >= 0 && vak <= 8) {
                                vak++;
                                if (args[1].equals(ProtocolEndpoint.DIRECTION_CLOCKWISE)) {
                                } else if (args[1].equals(ProtocolEndpoint.DIRECTION_COUNTERCLOCKWISE))
                                    vak *= -1;
                                else
                                    throw new NumberFormatException();
                                spel.doeRotate(vak);
                            } else
                                throw new NumberFormatException();
                        } catch (NumberFormatException e) {
                            HANDLER.addNack(ProtocolError.INVALID_MOVE);
                            spel.kickClient(this);
                        }
                } else																//Send error
                    HANDLER.addNack(ProtocolError.UNEXPECTED_CMD);
            else
                HANDLER.addNack(ProtocolError.UNEXPECTED_CMD);
        else if (ProtocolEndpoint.CMD_JOIN.equals(cmd))
            if (args.length == 1)
                try {
                    int players = Integer.parseInt(args[0]);
                    if (players <= 4 && players >= -1 && players != 0) {
                    } else
                        throw new NumberFormatException();
                } catch (NumberFormatException e) {
                    HANDLER.addNack(ProtocolError.INCORRECT_PLAYER_AMOUNT);
                }
            else
                HANDLER.addNack(ProtocolError.INVALID_ARGUMENT_COUNT);
                */
    }

    @Override
    public synchronized int[] doeZet(Bord bord) {
        this.canMove = true;
        int[] toRet = new int[]{-1,-1};
        try {
            this.wait(); // Wait for a MOVE
        } catch (InterruptedException ex) {
            Logger.getLogger(ServerClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        toRet[0] = this.lastMove;
        try {
            this.wait(); // Wait for a ROTATE
        } catch (InterruptedException ex) {
            Logger.getLogger(ServerClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        toRet[1] = this.lastRotate;
        return toRet;
    }

    private void handleHello(String[] args) {
        if (args == null || args[0].length() > 25
                || args[0].equalsIgnoreCase(ProtocolEndpoint.SERVER_NAME))
            this.HANDLER.addNack(ProtocolError.INVALID_USER);
        else if (Server.getPlayerList().contains(args[0]))
            this.HANDLER.addNack(ProtocolError.USERNAME_TAKEN);
        else {
            this.setNaam(args[0]);
            this.HANDLER.addMessage(ProtocolEndpoint.SRV_ACK);
            // TODO: detect extra functions
        }
    }

    private void handleChallenge(String[] args) {
        // TODO: implement challenges
        this.HANDLER.addNack(ProtocolError.COMMAND_UNSUPPORTED);
    }

    private void handleChallengeResponse(boolean accepted, String[] args) {
        // TODO: implement challenges
        this.HANDLER.addNack(ProtocolError.COMMAND_UNSUPPORTED);
    }

    private void handleChat(String[] args) {
        // TODO: implement chat
        this.HANDLER.addNack(ProtocolError.COMMAND_UNSUPPORTED);
    }

    private void handleList(String[] args) {
        // TODO: implement list
        this.HANDLER.addNack(ProtocolError.COMMAND_UNSUPPORTED);
    }

    private void handleQuit(String[] args) {
        Server.logMessage(String.format("%s (%s) disconnected", this.getNaam(),
                this.HANDLER.getSocket().getInetAddress().getHostAddress()));
        Server.killClient(this.HANDLER);
    }

    private void handleJoin(String[] args) {
        if (args.length != 1)
            this.HANDLER.addNack(ProtocolError.INVALID_ARGUMENT_COUNT);
        else if (!args[0].matches("-1|[2-4]"))
            this.HANDLER.addNack(ProtocolError.INCORRECT_PLAYER_AMOUNT);
        else {
            this.clientsInGame = Integer.parseInt(args[0]);
            Server.createGames();
        }
    }

    private void handleObserve(String[] args) {
        // TODO: implement observe
        this.HANDLER.addNack(ProtocolError.COMMAND_UNSUPPORTED);
    }

    private void handleMove(String[] args) {
        if (args.length != 1)
            this.HANDLER.addNack(ProtocolError.INVALID_ARGUMENT_COUNT);
        else if (!args[0].matches("[0-80]")) {
            this.HANDLER.addNack(ProtocolError.INVALID_MOVE);
            this.spel.kickClient(this);
        } else {
            int vakje = Integer.parseInt(args[0]);
            if (!this.spel.getBord().isLeegVeld(vakje)) {
                this.HANDLER.addNack(ProtocolError.INVALID_MOVE);
                this.spel.kickClient(this);
            } else {
                this.lastMove = vakje;
                synchronized (this) {
                    this.notifyAll();
                }
                this.spel.broadcast(ProtocolEndpoint.BCST_MOVE, this.getNaam(), args[0]);
            }
        }
    }

    private void handleRotate(String[] args) {
        if (args.length != 2)
            this.HANDLER.addNack(ProtocolError.INVALID_ARGUMENT_COUNT);
        else if (!args[0].matches("[0-8]")
                || !(args[1].equals(ProtocolEndpoint.DIRECTION_CLOCKWISE)
                || args[1].equals(ProtocolEndpoint.DIRECTION_COUNTERCLOCKWISE))) {
            this.HANDLER.addNack(ProtocolError.INVALID_MOVE);
            this.spel.kickClient(this);
        } else {
            this.lastRotate = ((Integer.parseInt(args[0]) + 1)
                    * (args[1].equals(ProtocolEndpoint.DIRECTION_CLOCKWISE) ? 1 : -1));
            synchronized (this) {
                this.notifyAll();
            }
            this.spel.broadcast(ProtocolEndpoint.BCST_ROTATE, args[0], args[1]);
        }
    }
}

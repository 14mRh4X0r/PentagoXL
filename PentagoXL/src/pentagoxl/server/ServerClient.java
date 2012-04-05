package pentagoxl.server;

import java.util.logging.Level;
import java.util.logging.Logger;
import pentagoxl.Client;
import pentagoxl.NetHandler;
import pentagoxl.ProtocolEndpoint;
import pentagoxl.ProtocolError;
import pentagoxl.spel.Bord;

public class ServerClient extends Client {

    public ServerClient(NetHandler handler) {
        super(handler);
    }

    @Override
    public synchronized void onReceive(String cmd, String[] args) {
        if (this.clientsInGame == 1)
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
            if (this.clientsInGame == 0)
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
    }

    private void handleHello(String[] args) {
        if (args == null || args[0].length() > 25
                || args[0].equalsIgnoreCase(ProtocolEndpoint.SERVER_NAME))
            this.HANDLER.addNack(ProtocolError.INVALID_USER);
        else if (Server.getPlayerList().contains(args[0]))
            this.HANDLER.addNack(ProtocolError.USERNAME_TAKEN);
        else {
            this.setNaam(args[0]);
            this.clientsInGame = 0;
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
        else if (!args[0].matches("[1-7]?[0-9]|80")) {
            this.HANDLER.addNack(ProtocolError.INVALID_MOVE);
            this.spel.kickClient(this);
        } else {
            int vakje = Integer.parseInt(args[0]);
            if (!this.spel.getBord().isLeegVeld(vakje)) {
                this.HANDLER.addNack(ProtocolError.INVALID_MOVE);
                this.spel.kickClient(this);
                Server.logMessage("Client kicked because a non-empty field was entered");
            } else {
                this.spel.broadcast(ProtocolEndpoint.BCST_MOVE, this.getNaam(), args[0]);
                this.spel.doeMove(vakje);
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
            this.spel.broadcast(ProtocolEndpoint.BCST_ROTATE, args[0], args[1]);
            this.spel.doeRotate(((Integer.parseInt(args[0]) + 1)
                    * (args[1].equals(ProtocolEndpoint.DIRECTION_CLOCKWISE) ? 1 : -1)));
        }
    }
}

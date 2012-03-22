package pentagoxl.server;

import pentagoxl.Client;
import pentagoxl.NetHandler;
import pentagoxl.ProtocolEndpoint;
import pentagoxl.ProtocolError;
import pentagoxl.spel.Bord;

public class ServerClient extends Client{

	public ServerClient(NetHandler handler) {
		super(handler);
	}
	

    
    @Override
	public synchronized void onReceive(String cmd, String[] args) {
    	
    	
    	
		if (ProtocolEndpoint.CMD_HELLO.equals(cmd) && getNaam() == null) {				//CMD_HELLO logic
			if (args.length == 1) {														//Check if name was sent
				if (args[0].equalsIgnoreCase("server") || args[0].length() > 25) {		//Check for length of name
					HANDLER.addNack(ProtocolError.INVALID_USER);
				} else if (!Server.listAllPlayers().contains(args[0])) {				//Check if name is already taken
					HANDLER.addNack(ProtocolError.USERNAME_TAKEN);			
				} else {
					setNaam(args[0]);													//Set name
				}
			} else {
				HANDLER.addNack(ProtocolError.INVALID_ARGUMENT_COUNT);
			}
		} else if (ProtocolEndpoint.CMD_HELLO.equals(cmd) || 							//Check if an unexpected command is sent.
					(!ProtocolEndpoint.CMD_HELLO.equals(cmd) && getNaam() == null)) {
			HANDLER.addNack(ProtocolError.UNEXPECTED_CMD);
		} else if (ProtocolEndpoint.CMD_MOVE.equals(cmd) || ProtocolEndpoint.CMD_ROTATE.equals(cmd)) { //Move command
			if (spel.zetIsAanClient(this)){												//Check if the turn is this client's
				if (ProtocolEndpoint.CMD_MOVE.equals(cmd) && canMove) {					//Check if this client can do a move
					if (args.length == 1) {												//Check if amount of arguments is correct
						try {
							int zet = Integer.parseInt(args[0]);
							if (zet >= 0 && zet <= 80) {								//Check if argument is in range
								if (spel.getBord().isLeegVakje(zet)) {
									spel.doeMove(zet);									//Make the move
								} else {
									throw new NumberFormatException();
								}
							} else {
								throw new NumberFormatException();
							}
						} catch (NumberFormatException e) {
							HANDLER.addNack(ProtocolError.INVALID_MOVE);
							spel.kickClient(this);
						}
					} else {
						HANDLER.addNack(ProtocolError.INVALID_ARGUMENT_COUNT);			//Send error
					}
				} else if (ProtocolEndpoint.CMD_ROTATE.equals(cmd) && canRotate) {		//Check if this client can do a rotate
					if (args.length == 2) {												//Check if amount of arguments is correct
						try {
							int vak = Integer.parseInt(args[0]);
							if (vak >= 0 && vak <= 8) {
								vak++;
								if (args[1].equals(ProtocolEndpoint.DIRECTION_CLOCKWISE)) {
									
								} else if (args[1].equals(ProtocolEndpoint.DIRECTION_COUNTERCLOCKWISE)){
									vak *= -1;
								} else {
									throw new NumberFormatException();
								}
								spel.doeRotate(vak);
							} else {
								throw new NumberFormatException();
							}
						} catch (NumberFormatException e) {
							HANDLER.addNack(ProtocolError.INVALID_MOVE);
							spel.kickClient(this);
						}
					}
				} else {																//Send error
					HANDLER.addNack(ProtocolError.UNEXPECTED_CMD);
				}
			} else {
				HANDLER.addNack(ProtocolError.UNEXPECTED_CMD);
			}
		} else if (ProtocolEndpoint.CMD_JOIN.equals(cmd)) {
			if (args.length == 1) {
				try {
					int players = Integer.parseInt(args[0]);
					if (players <= 4 && players >= -1 && players != 0) {
						
					} else {
						throw new NumberFormatException();
					}
				} catch (NumberFormatException e) {
					HANDLER.addNack(ProtocolError.INCORRECT_PLAYER_AMOUNT);
				}
			} else {
				HANDLER.addNack(ProtocolError.INVALID_ARGUMENT_COUNT);
			}
		}
		
	}

	@Override
	public int[] doeZet(Bord bord) {
		return null;
	}
	
	

}

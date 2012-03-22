package pentagoxl;

/**
 * An enumeration representing the errors<br>
 * <br>
 * example: String errorDescription = Error.INVALID_USER.getDescription();<br>
 * example: String errorDescription = Error.lookupCode(101).getDescription();<br>
 * example: int errorCode = Error.INVALID_USER.getCode();<br>
 * 
 * @author Jeroen Vollenbrock
 * @version 1.0
 */
public enum ProtocolError {
	
	/**
	 * This error is returned when the userName requirements are not met.
	 * @see ProtocolEndpoint#CMD_HELLO
	 */
	INVALID_USER(101, "This username is invalid."),
	
	/**
	 * This error is returned when the specified username was already taken.
	 * @see ProtocolEndpoint#CMD_HELLO
	 */
	USERNAME_TAKEN(102, "The specified username is already taken."),
	
	/**
	 * An incorrect number of players has been specified
	 * @see ProtocolEndpoint#CMD_JOIN
	 */
	INCORRECT_PLAYER_AMOUNT(103, "The amount of players is incorrect."),
	
	/**
	 * An invalid number of arguments has been specified.
	 */
	INVALID_ARGUMENT_COUNT(201,"The specified number of arguments is out of range."),
	
	/**
	 * The requested command is not supported by this server implementation.
	 * 
	 */
	COMMAND_UNSUPPORTED(202,"The requested command is not supported by this implementation."),
	
	/**
	 * An command was received while its prerequisites were not met.
	 */
	UNEXPECTED_CMD(203,"An unexpected command was received."),
	
	/**
	 * You have tried to make the server broadcast an invalid move and therefore you have been kicked.
	 * @see ProtocolEndpoint#CMD_MOVE
	 * @see ProtocolEndpoint#CMD_ROTATE
	 */
	INVALID_MOVE(301,"You have been kicked because you tried an invalid move."),
	
	/**
	 * One or more challenged players have declined your challenge.
	 * @see ProtocolEndpoint#CMD_CHALLENGE
	 */
	CHALLENGE_NACK(302,"The challenge was not accepted by one or more challenged players."),
	
	/**
	 * You have tried to challenge one or more clients who do not support challenges.
	 * @see ProtocolEndpoint#CMD_CHALLENGE
	 */
	CHALLENGE_UNSUPPORTED(303,"One or more challenged clients do not support challenges."),
	
	/**
	 * You have tried to challenge one or more clients who are currently waiting for another challenge.
	 * @see ProtocolEndpoint#CMD_CHALLENGE
	 */
	CLIENT_BUSY(304,"One or more challenged clients are waiting on another challenge."),
	
	/**
	 * This error is returned when an undocumented scenario has occurred.
	 */
	UNSPECIFIED(400, "An unspecified error has occured.");

	private final int errorCode;
	private final String errorDescription;

	/**
	 * @param errorCode the code of the <tt>Error</tt>
	 * @param errorDescription the description of the <tt>Error</tt>
	 */
	ProtocolError(int errorCode, String errorDescription) {
		this.errorCode = errorCode;
		this.errorDescription = errorDescription;
	}

	/**
	 * Returns the errors 3-digit integer representation
	 * @return the code of this <tt>Error</tt>
	 */
	public int getCode() {
		return errorCode;
	}

	/**
	 * returns a human readable description of this error.
	 * @return the description of this <tt>Error</tt>.
	 */
	public String getDescription() {
		return errorDescription;
	}

	/**
	 * searches for the <tt>Error</tt> which matches the specified code.
	 * @param code the error code to lookup
	 * @return the <tt>Error</tt> which belongs to the specified code
	 */
	public static ProtocolError lookupCode(int code) {
		ProtocolError result = ProtocolError.UNSPECIFIED;
		boolean found = false;
		ProtocolError[] errors = ProtocolError.values();
		for (int i = 0; i < errors.length && !found; i++) {
			if (errors[i].errorCode == code) {
				result = errors[i];
				found = true;
			}
		}
		return result;
	}

}

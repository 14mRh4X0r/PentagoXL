package pentagoxl;


/**
 * The base of a socket connection. This class contains a description of the
 * protocol The easiest way of implementing the protocol is by extending this
 * class.<br>
 * <br>
 * The all identifiers use a uniform naming format:<br>
 * - All client commands have the CMD_ prefix.<br>
 * - Client specific server commands have the SRV_ prefix.<br>
 * - Server broadcasts have the BCST_ prefix<br>
 * <br>
 * The turns, moves and rotates use numbers to indicate compartments on a
 * playing board. The compartments are numbered like this. inner-compartments:
 * 
 * <pre>
 *  0 | 1 | 2 || 9 | 10| 11|| 18| 19| 20
 * ---+---+---++---+---+---++---+---+---
 *  3 | 4 | 5 || 12| 13| 14|| 21| 22| 23
 * ---+---+---++---+---+---++---+---+---
 *  6 | 7 | 8 || 15| 16| 17|| 24| 25| 26
 * ===========++===========++===========
 *  27| 28| 29|| 36| 37| 38|| 45| 46| 47
 * ---+---+---++---+---+---++---+---+---
 *  30| 31| 32|| 39| 40| 41|| 48| 49| 50
 * ---+---+---++---+---+---++---+---+---
 *  33| 34| 35|| 42| 43| 44|| 51| 52| 53
 * ===========++===========++===========
 *  54| 55| 56|| 63| 64| 65|| 72| 73| 74
 * ---+---+---++---+---+---++---+---+---
 *  57| 58| 59|| 66| 67| 68|| 75| 76| 77
 * ---+---+---++---+---+---++---+---+---
 *  60| 61| 62|| 69| 70| 71|| 78| 79| 80
 * </pre>
 * 
 * <br>
 * <br>
 * outer-compartments:
 * 
 * <pre>
 *    |   |   ||   |   |   ||   |   |   
 * ---+---+---++---+---+---++---+---+---
 *    | 0 |   ||   | 1 |   ||   | 2 |   
 * ---+---+---++---+---+---++---+---+---
 *    |   |   ||   |   |   ||   |   |   
 * ===========++===========++===========
 *    |   |   ||   |   |   ||   |   |   
 * ---+---+---++---+---+---++---+---+---
 *    | 3 |   ||   | 4 |   ||   | 5 |   
 * ---+---+---++---+---+---++---+---+---
 *    |   |   ||   |   |   ||   |   |   
 * ===========++===========++===========
 *    |   |   ||   |   |   ||   |   |   
 * ---+---+---++---+---+---++---+---+---
 *    | 6 |   ||   | 7 |   ||   | 8 |   
 * ---+---+---++---+---+---++---+---+---
 *    |   |   ||   |   |   ||   |   |
 * </pre>
 * 
 * <br>
 * <br>
 * hint: you can use innerCompartment%9 to determine the relative position of an
 * inner compartment within an outer-compartment and (int)innerCompartment/9 to
 * determine the number of the inner compartment <br>
 * <br>
 * NOTE: this class uses the names John, Steve, Bob and Richard as example
 * names. These names can be substituted by any other name, as long as the name
 * meets the requirements specified in <tt>CMD_HELLO</tt>
 * 
 * @author Jeroen Vollenbrock
 * @version 0.2 review
 */
public abstract class ProtocolEndpoint {

	/**
	 * the argument and list separator. We advise not using this character in
	 * any argument except for lists.
	 */
	public static final String DELIMITER = "|";

	/**
	 * HELO command. <br>
	 * <br>
	 * By sending this command we acknowledge we are a client and send the
	 * server our preferred nickname.<br>
	 * <br>
	 * first argument: player name<br>
	 * constraints: <i>playerName.size() <= 25 &&
	 * !playerName.contains(DELEMITER) && !playerName.contains('\n') &&
	 * !playerName.equalsIgnoreCase("server")</i><br>
	 * <br>
	 * second argument: list of supported optional broadcasts, separated by
	 * <tt>DELEMITER</tt><br>
	 * constraints: the list can only contain the following command names:<br>
	 * - <tt>BCST_CHALLENGE</tt><br>
	 * - <tt>BCST_CHAT</tt><br>
	 * <br>
	 * reply: either <tt>SRV_ACK</tt> or <tt>SRV_NACK</tt><br>
	 * <br>
	 * example -->: HELO|Steve|CHAT|CHALLENGE<br>
	 * example -->: HELO|Steve|CHAT<br>
	 * example -->: HELO|Steve|CHALLENGE<br>
	 * example -->: HELO|Steve<br>
	 * 
	 * @see ProtocolEndpoint#SRV_ACK
	 * @see ProtocolEndpoint#SRV_NACK
	 */
	public static final String CMD_HELLO = "HELO";

	/**
	 * ACK response.<br>
	 * <br>
	 * this indicates the server has accepted your previous command.<br>
	 * this reply can be expected after a <tt>CMD_HELLO</tt>, a
	 * <tt>CMD_JOIN</tt>, and a <tt>CMD_OBSERVE</tt><br>
	 * <br>
	 * no arguments<br>
	 * <br>
	 * example <--: ACK<br>
	 * <br>
	 * prerequisite: a connection has been set up and the user has sent a
	 * <tt>CMD_HELLO</tt> to the server<br>
	 * 
	 * @see ProtocolEndpoint#CMD_HELLO
	 * @see ProtocolEndpoint#CMD_JOIN
	 * @see ProtocolEndpoint#CMD_OBSERVE
	 */
	public static final String SRV_ACK = "ACK";

	/**
	 * NACK response.<br>
	 * <br>
	 * this indicates the server has rejected your previous command.<br>
	 * <br>
	 * first argument: error code<br>
	 * constraints: the error code is a 3 digit integer as specified in
	 * <tt>Error</tt><br>
	 * <br>
	 * example <--: NACK|101<br>
	 * example <--: NACK|200<br>
	 * <br>
	 * prerequisites: a connection has been set up and the user has sent a
	 * <tt>CMD_HELLO</tt> to the server<br>
	 * 
	 * @see Error
	 */
	public static final String SRV_NACK = "NACK";

	/**
	 * JOIN command<br>
	 * <br>
	 * Tells the server we want to join a game.<br>
	 * <br>
	 * first argument: amount of players<br>
	 * constraints: <i>amount == -1 || (amount >= 2 && amount <= 4)</i><br>
	 * amount = -1 means we don't care about the number of players and just want
	 * to join a game ASAP. <br>
	 * reply: either <tt>SRV_ACK</tt> followed by a <tt>BCST_STARTGAME</tt> or
	 * just <tt>SRV_NACK</tt><br>
	 * <br>
	 * example -->: JOIN|-1<br>
	 * example -->: JOIN|2<br>
	 * example -->: JOIN|3<br>
	 * example -->: JOIN|4<br>
	 * <br>
	 * prerequisite: the user has sent a <tt>CMD_HELLO</tt> to the server and
	 * the server replied with <tt>SRV_ACK</tt><br>
	 * 
	 * @see ProtocolEndpoint#SRV_ACK
	 * @see ProtocolEndpoint#SRV_NACK
	 * @see ProtocolEndpoint#CMD_HELLO
	 * @see ProtocolEndpoint#BCST_STARTGAME
	 */
	public static final String CMD_JOIN = "JOIN";

	/**
	 * START broadcast<br>
	 * <br>
	 * this indicates the game has been assembled and we are ready to start
	 * playing.<br>
	 * <br>
	 * first argument: list of names of the players in your game.<br>
	 * constraints: <i>list.length >=2 && list.length <= 4 && name.exists() </i><br>
	 * <br>
	 * the order in which the names are returned represent the colors of the
	 * players. the first player uses red, the second blue, the third green, the
	 * fourth yellow.<br>
	 * After this broadcast, the server starts announcing the turns by using
	 * <tt>BCST_TURN</tt><br>
	 * <br>
	 * example <<--: START|John|Steve<br>
	 * example <<--: START|John|Steve|Bob<br>
	 * example <<--: START|John|Steve|Bob|Richard
	 * 
	 * @see ProtocolEndpoint#BCST_TURN
	 */
	public static final String BCST_STARTGAME = "START";

	/**
	 * TURN broadcast<br>
	 * <br>
	 * this indicates it is now up to a player to send his/her turn.<br>
	 * <br>
	 * first argument: the name of the player who has to send his/her turn.<br>
	 * constraints: <i>name.exists() </i><br>
	 * <br>
	 * The player specified in the first argument is expected to send a
	 * <tt>CMD_MOVE</tt> after receiving this.<br>
	 * <br>
	 * example <<--: TURN|John<br>
	 * example <<--: TURN|Steve<br>
	 * <br>
	 * prerequisites: the game has been started and has no winner yet.
	 * 
	 * @see ProtocolEndpoint#CMD_MOVE
	 */
	public static final String BCST_TURN = "TURN";

	/**
	 * MOVE command. <br>
	 * <br>
	 * By sending this command we announce our turn to the server.<br>
	 * <br>
	 * first argument: inner-compartment number<br>
	 * constraints: <i>number >= 0 && number <= 80 && number is unoccupied.</i><br>
	 * <br>
	 * reply: <tt>BCST_MOVE</tt> or a <tt>SRV_NACK</tt> and a game kick when the
	 * constraints were not met.<br>
	 * <br>
	 * example -->: MOVE|18<br>
	 * example -->: MOVE|0<br>
	 * example -->: MOVE|80<br>
	 * <br>
	 * prerequisites: the last recieved<tt>BCST_TURN</tt> contained the name of
	 * the current player, this command can only be used once after each turn
	 * broadcast.
	 * 
	 * @see ProtocolEndpoint#BCST_MOVE
	 * @see ProtocolEndpoint#SRV_NACK
	 */
	public static final String CMD_MOVE = "MOVE";

	/**
	 * MOVE broadcast<br>
	 * <br>
	 * this broadcast informs all players of the latest turn.<br>
	 * <br>
	 * first argument: the name of the player who has to send his/her turn.<br>
	 * constraints: <i>name.exists() </i><br>
	 * <br>
	 * second argument: inner-compartment number<br>
	 * constraints: <i>number >= 0 && number <= 80 && number is unoccupied.</i><br>
	 * <br>
	 * If there is no winner after this broadcast, the player who's turn it is
	 * is expected to send a <tt>CMD_ROTATE</tt><br>
	 * <br>
	 * example <<--: MOVE|Steve|18<br>
	 * example <<--: MOVE|Steve|0<br>
	 * example <<--: MOVE|Steve|80<br>
	 * 
	 * @see ProtocolEndpoint#CMD_ROTATE
	 * @see ProtocolEndpoint#BCST_GAMEOVER
	 */
	public static final String BCST_MOVE = "MOVE";

	/**
	 * Implies a clockwise rotation of a certain outer-compartment.
	 * 
	 * @see ProtocolEndpoint#CMD_ROTATE
	 * @see ProtocolEndpoint#BCST_ROTATE
	 */
	public static final String DIRECTION_CLOCKWISE = "R";

	/**
	 * implies a counterclockwise rotation of a certain outer-compartment.
	 * 
	 * @see ProtocolEndpoint#CMD_ROTATE
	 * @see ProtocolEndpoint#BCST_ROTATE
	 */
	public static final String DIRECTION_COUNTERCLOCKWISE = "L";

	/**
	 * ROTATE command. <br>
	 * <br>
	 * By sending this command we announce our turn to the server.<br>
	 * <br>
	 * first argument: outer-compartment number<br>
	 * constraints: <i>number >= 0 && number <= 8.</i><br>
	 * <br>
	 * second argument: orientation<br>
	 * constraints: either <tt>DIRECTION_CLOCKWISE</tt> or
	 * <tt>DIRECTION_COUNTERCLOCKWISE</tt></i><br>
	 * <br>
	 * reply: <tt>BCST_ROTATE</tt> or a NACK and a game kick when the
	 * constraints were not met.<br>
	 * <br>
	 * example -->: ROTATE|Steve|L<br>
	 * example -->: ROTATE|Steve|R<br>
	 * <br>
	 * prerequisites: the last recieved<tt>BCST_TURN</tt> contained the name of
	 * the current player, a CMD_MOVE has been sent, the game has no winner,
	 * this command can only be used once after each turn broadcast.
	 * 
	 * @see ProtocolEndpoint#SRV_NACK
	 * @see ProtocolEndpoint#BCST_ROTATE
	 * @see ProtocolEndpoint#DIRECTION_CLOCKWISE
	 * @see ProtocolEndpoint#DIRECTION_COUNTERCLOCKWISE
	 */
	public static final String CMD_ROTATE = "ROTATE";

	/**
	 * ROTATE broadcast<br>
	 * <br>
	 * this broadcast informs all players of the latest turn.<br>
	 * <br>
	 * first argument: outer-compartment number<br>
	 * constraints: <i>number >= 0 && number <= 8.</i><br>
	 * <br>
	 * second argument: orientation<br>
	 * constraints: either <tt>DIRECTION_CLOCKWISE</tt> or
	 * <tt>DIRECTION_COUNTERCLOCKWISE</tt></i><br>
	 * <br>
	 * example <<--: ROTATE|Steve|R<br>
	 * example <<--: ROTATE|Steve|L
	 * 
	 * @see ProtocolEndpoint#DIRECTION_CLOCKWISE
	 * @see ProtocolEndpoint#DIRECTION_COUNTERCLOCKWISE
	 */
	public static final String BCST_ROTATE = "ROTATE";

	/**
	 * GAMEOVER broadcast<br>
	 * <br>
	 * this broadcast informs the players about the winner of the game.<br>
	 * <br>
	 * first argument: list of names of winners<br>
	 * constraints: <i>list.length >= 0 && list.length <= 4</i><br>
	 * <br>
	 * Either the game has ended because someone was kicked/disconnected. This
	 * means all remaining players won. There could also be one or more real
	 * winners. We could also have run out of colors without any winner. <br>
	 * <br>
	 * example <<--: GAMEOVER|Steve|BOB <br>
	 * example <<--: GAMEOVER
	 */
	public static final String BCST_GAMEOVER = "GAMEOVER";

	/**
	 * QUIT command<br>
	 * <br>
	 * Informs the server a client wants to shut down.<br>
	 * The server expects a closed socket after receiving this command <br>
	 * reply: none, the server closes its socket with the client.<br>
	 * <br>
	 * example -->: QUIT<br>
	 * <br>
	 * prerequisite: the user is connected.<br>
	 */
	public static final String CMD_QUIT = "QUIT";

	// / v optional parts
	/**
	 * OBSERVE command *optional*<br>
	 * <br>
	 * Informs the server a client wants observe another client.<br>
	 * The server expects a closed socket after receiving this command <br>
	 * <br>
	 * first argument: the name of the player who has to send his/her turn.<br>
	 * constraints: <i>name.exists() </i><br>
	 * <br>
	 * reply: either <tt>SRV_ACK</tt> followed by a <tt>BCST_STARTGAME</tt>
	 * followed by many <tt>BCST_MOVE</tt>s or just <tt>SRV_NACK</tt> when
	 * observing is unsupported by the server, or the constraints were not met<br>
	 * <br>
	 * example -->: OBSERVE|Steve<br>
	 * <br>
	 * prerequisite: the user is connected and informed the server about a
	 * preferred name using <tt>CMD_HELLO</tt><br>
	 * 
	 * @see ProtocolEndpoint#SRV_ACK
	 * @see ProtocolEndpoint#SRV_NACK
	 * @see ProtocolEndpoint#BCST_STARTGAME
	 * @see ProtocolEndpoint#BCST_MOVE
	 * 
	 */
	public static final String CMD_OBSERVE = "OBSERVE";

	/**
	 * CHAT command *optional*<br>
	 * <br>
	 * Sends a chat message to the server<br>
	 * <br>
	 * first argument: the chat message<br>
	 * constraints: <i>!msg.contains('\n') && !msg.contains(DELIMITER) &&
	 * msg.length() <= 1000</i><br>
	 * <br>
	 * reply: either a <tt>BCST_CHAT</tt> or just <tt>SRV_NACK</tt> when the
	 * constraints were not met or chat is unsupported by the server. (does not
	 * result in a kick)<br>
	 * <br>
	 * example -->: CHAT|Hello, i'm a message with contents.<br>
	 * <br>
	 * prerequisite: the user is connected and informed the server about a
	 * preferred name using <tt>CMD_HELLO</tt><br>
	 * 
	 * @see ProtocolEndpoint#BCST_CHAT
	 * @see ProtocolEndpoint#DIRECTION_COUNTERCLOCKWISE
	 */
	public static final String CMD_CHAT = "CHAT";

	/**
	 * BCST_CHAT broadcast *optional*<br>
	 * <br>
	 * this broadcast informs the players about an incoming chat message.<br>
	 * <br>
	 * first argument: name of sender<br>
	 * constraints: <i>name.exists</i><br>
	 * <br>
	 * second argument: the chat message<br>
	 * constraints: <i>!msg.contains('\n') && !msg.contains(DELIMITER) &&
	 * msg.length() <= 1000</i><br>
	 * <br>
	 * For messages sent by the server, the username server is reserved.
	 * <br>
	 * example <<--: CHAT|Bob|Hello, My name is Bob! <br>
	 * example <<--: CHAT|Steve|Hi Bob, what is your problem?<br>
	 * example <<--: CHAT|John|I've got an alcohol problem! I don't have any
	 * beverage right now... :(<br>
	 * <br>
	 * prerequisite: All recievers support the BCST_CHAT command as indicated by
	 * <tt>CMD_HELO</tt>
	 * @see ProtocolEndpoint#CMD_HELLO
	 * @see ProtocolEndpoint#CMD_CHAT
	 */
	public static final String BCST_CHAT = "CHAT";

	/**
	 * LIST command *optional*<br>
	 * <br>
	 * Requests a list of specific connected clients.<br>
	 * <br>
	 * first argument: the type of game they are either waiting for or playing
	 * right now<br>
	 * constraints: <i>players >= -1 && players <= 4 && players != 2</i><br>
	 * <br>
	 * When the first argument is -1, a list of players opted in for a random
	 * game is returned<br>
	 * When the first argument is 0, a list of players that are not playing any
	 * game is returned.<br>
	 * When the first argument is 2..4 a list of players that are currently
	 * assembling, or are currently playing a 2..4 player game.<br>
	 * sending this command with -1, 2,3,4 and adding the result together
	 * results in a list of all connected players.<br>
	 * sending this command with 0 results in a list of all players in the
	 * lobby. <br>
	 * reply: either a <tt>SRV_LIST</tt> or just <tt>SRV_NACK</tt> when the
	 * constraints were not met. (does not result in a kick)<br>
	 * <br>
	 * example -->: LIST|-1<br>
	 * example -->: LIST|0<br>
	 * example -->: LIST|2<br>
	 * example -->: LIST|3<br>
	 * example -->: LIST|4<br>
	 * <br>
	 * prerequisite: the user is connected and informed the server about a
	 * preferred name using <tt>CMD_HELLO</tt><br>
	 * @see ProtocolEndpoint#SRV_LIST
	 */
	public static final String CMD_LIST = "LIST";

	/**
	 * LIST response *optional*<br>
	 * <br>
	 * this informs the client about connected clients.<br>
	 * <br>
	 * first argument: a list of client names.<br>
	 * constraints: <i>name.exists()</i><br>
	 * <br>
	 * example <<--: LIST|Bob|Steve|John|Richard<br>
	 * example <<--: LIST<br>
	 * example <<--: LIST|Steve<br>
	 * @see ProtocolEndpoint#CMD_LIST
	 */
	public static final String SRV_LIST = "LIST";

	/**
	 * CHALLENGE command *optional*<br>
	 * <br>
	 * Informs the server we want to challenge certain people.<br>
	 * <br>
	 * first argument: a list of names of the players you want to challenge<br>
	 * constraints: <i>list.length >= 1 && list.length <= 3 && names.exist() &&
	 * names.inlobby()</i><br>
	 * <br>
	 * Calling <tt>CMD_CHALLENGE</tt> always results in either a <tt>BCST_CHALLENGE</tt>
	 * broadcast or a <tt>NACK|COMMAND_UNSUPPORTED</tt> or a
	 * <tt>NACK|CHALLENGE_UNSUPPORTED</tt><br>
	 * <br>
	 * example -->: CHALLENGE|Steve|John|Bob<br>
	 * example -->: CHALLENGE|Steve|John<br>
	 * example -->: CHALLENGE|Steve<br>
	 * <br>
	 * prerequisite: the user is connected and informed the server about a
	 * preferred name using <tt>CMD_HELLO</tt><br>
	 * @see ProtocolEndpoint#SRV_NACK
	 * @see ProtocolEndpoint#BCST_CHALLENGE
	 */
	public static final String CMD_CHALLENGE = "CHALLENGE";

	/**
	 * CHALLENGE broadcast *optional*<br>
	 * <br>
	 * Broadcasts a list of challenged players.<br>
	 * <br>
	 * first argument: a list of names of the players that are being challenged<br>
	 * constraints: <i>list.length >= 2 && list.length <= 4 && names.exist() &&
	 * names.inlobby()</i><br>
	 * The first name on the list is the challenger.<br>
	 * <br>
	 * After a challenge broadcast a <tt>NACK|CHALLENGE_NACK</tt>, a
	 * <tt>NACK|CLIENT_BUSY</tt>, or a <tt>BCST_STARTGAME</tt> is broadcasted<br>
	 * <br>
	 * example <<--: CHALLENGE|Richard|Steve|John|Bob<br>
	 * example <<--: CHALLENGE|Richard|Steve|John<br>
	 * example <<--: CHALLENGE|Richard|Steve<br>
	 * <br>
	 * prerequisite: all users support the challenge broadcast as indicated with
	 * <tt>CMD_HELLO</tt>
	 * @see ProtocolEndpoint#SRV_NACK
	 * @see ProtocolEndpoint#BCST_STARTGAME
	 * @see ProtocolEndpoint#CMD_HELLO
	 */
	public static final String BCST_CHALLENGE = "CHALLENGE";

	/**
	 * ACCEPT Command *optional*<br>
	 * <br>
	 * Tells the server the client accepts the challenge.<br>
	 * <br>
	 * example -->: ACCEPT<br>
	 * <br>
	 * prerequisite: a <tt>BCST_CHALLENGE</tt> has been received.
	 * @see ProtocolEndpoint#BCST_CHALLENGE
	 */
	public static final String CMD_CHALLANGE_ACCEPT = "ACCEPT";

	/**
	 * DECLINE Command *optional*<br>
	 * <br>
	 * Tells the server the client declines the challenge.<br>
	 * <br>
	 * example -->: DECLINE<br>
	 * <br>
	 * prerequisite: a <tt>BCST_CHALLENGE</tt> has been received.
	 * @see ProtocolEndpoint#BCST_CHALLENGE
	 */
	public static final String CMD_CHALLENGE_DECLINE = "DECLINE";
}

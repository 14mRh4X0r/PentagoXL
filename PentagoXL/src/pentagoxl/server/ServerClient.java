package pentagoxl.server;

import pentagoxl.Client;
import pentagoxl.NetHandler;
import pentagoxl.spel.Bord;

public class ServerClient extends Client {

	public ServerClient(NetHandler handler) {
		super(handler);
	}

	@Override
	public int[] doeZet(Bord bord) {
		
		return null;
	}

}

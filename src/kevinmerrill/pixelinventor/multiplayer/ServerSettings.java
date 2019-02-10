package kevinmerrill.pixelinventor.multiplayer;

import java.io.Serializable;

public class ServerSettings implements Serializable {
	private static final long serialVersionUID = 1L;
	public int port;
	public String ip;
	public String name;
	public ServerSettings (int port, String ip, String name) {
		this.port = port;
		this.ip = ip;
		this.name = name;
	}
}

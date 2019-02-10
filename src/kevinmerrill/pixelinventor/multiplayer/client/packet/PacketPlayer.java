package kevinmerrill.pixelinventor.multiplayer.client.packet;

public class PacketPlayer implements IPacket {

	public float x;
	public float y;
	public int frame;
	public boolean right_facing;
	public String username;
	
	public PacketPlayer(float x, float y, int frame, boolean right_facing, String username) {
		this.x = x;
		this.y = y;
		this.frame = frame;
		this.right_facing = right_facing;
		this.username = username;
	}
	
	public PacketPlayer(String[] data) {
		this.x = Float.parseFloat(data[1]);
		this.y = Float.parseFloat(data[2]);
		this.frame = Integer.parseInt(data[3]);
		this.right_facing = Boolean.parseBoolean(data[4]);
		this.username = data[5];
	}
	
	public byte[] getData() {
		return ("player\n"+x+"\n"+y+"\n"+frame+"\n"+right_facing+"\n"+username).getBytes();
	}

}

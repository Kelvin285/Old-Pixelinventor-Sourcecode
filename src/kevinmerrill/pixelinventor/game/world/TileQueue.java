package kevinmerrill.pixelinventor.game.world;

public class TileQueue {
	public long x, y;
	public int id;
	public boolean foreground;
	public TileQueue(long x, long y, int id, boolean foreground) {
		this.x = x;
		this.y = y;
		this.id = id;
		this.foreground = foreground;
	}
}

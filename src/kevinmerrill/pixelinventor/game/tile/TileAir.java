package kevinmerrill.pixelinventor.game.tile;

public class TileAir extends Tile {
	public TileAir()
	{
		super(Tiles.Air.getId(), "");
	}
	
	@Override
	public boolean isVisible() {
		return false;
	}
	
	@Override
	public boolean isSolid() {
		return false;
	}
}

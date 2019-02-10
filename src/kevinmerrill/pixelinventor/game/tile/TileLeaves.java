package kevinmerrill.pixelinventor.game.tile;

import kevinmerrill.pixelinventor.game.entity.Entity;
import kevinmerrill.pixelinventor.game.entity.EntityFallingTile;
import kevinmerrill.pixelinventor.game.world.World;
import kevinmerrill.pixelinventor.resources.Resources;

public class TileLeaves extends Tile {

	public TileLeaves(int id, String spritename) {
		super(id, spritename);
	}
	
	public void onWalkedUpon(Entity entity, World world, long x,
			long y, float posX, float posY) {
		
	}
	
	public void onUpdated(World world, long x, long y) {
		
	}
	
}

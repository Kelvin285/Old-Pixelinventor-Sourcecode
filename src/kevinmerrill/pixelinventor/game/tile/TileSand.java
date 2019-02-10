package kevinmerrill.pixelinventor.game.tile;

import kevinmerrill.pixelinventor.game.entity.Entity;
import kevinmerrill.pixelinventor.game.entity.EntityFallingTile;
import kevinmerrill.pixelinventor.game.world.World;
import kevinmerrill.pixelinventor.resources.ArrayMapHolder;
import kevinmerrill.pixelinventor.resources.Resources;

public class TileSand extends Tile {

	public TileSand(int id, String spritename) {
		super(id, spritename);
	}
	public void onWalkedUpon(Entity entity, World world, long x,
			long y, float posX, float posY) {
		float X = (float)x - (Resources.maxChunks / 2) * 16;
		float Y = (float)y - (Resources.maxChunks / 2) * 16;
		if (world.getTile(x, y - 1) == Tiles.Air.getId()) {

			world.setTile(x, y, Tiles.Air.getId());
			ArrayMapHolder.entities.add(new EntityFallingTile(X * 32, Y * 32, Tiles.Sand.getId(), world));
			
			int yy = 1;
			while (world.getTile(x, y + yy) == Tiles.Sand.getId() || world.getTile(x, y + yy) == Tiles.Dirt.getId() || world.getTile(x, y + yy) == Tiles.Snow.getId()) {
				if (world.getTile(x, y + yy) == Tiles.Sand.getId())
					ArrayMapHolder.entities.add(new EntityFallingTile(X * 32, (Y+yy) * 32, Tiles.Sand.getId(), world));
				if (world.getTile(x, y + yy) == Tiles.Dirt.getId())
					ArrayMapHolder.entities.add(new EntityFallingTile(X * 32, (Y+yy) * 32, Tiles.Dirt.getId(), world));
				world.setTile(x, y+yy, Tiles.Air.getId());
				yy++;
			}
			
		}
		if (world.getTile(x + 1, y - 1) == Tiles.Air.getId())
		world.getTileState(x + 1, y).onWalkedUpon(entity, world, x + 1, y, posX + 32, posY);
		if (world.getTile(x - 1, y - 1) == Tiles.Air.getId())
		world.getTileState(x - 1, y).onWalkedUpon(entity, world, x - 1, y, posX - 32, posY);
		if (world.getTile(x, y - 2) == Tiles.Air.getId())
		world.getTileState(x, y - 1).onWalkedUpon(entity, world, x, y - 1, posX, posY - 32);
		
	}
	
	public void onUpdated(World world, long x, long y) {
		onWalkedUpon(null, world, x, y, 0, 0);
	}
}

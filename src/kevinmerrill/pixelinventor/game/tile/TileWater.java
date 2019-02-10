package kevinmerrill.pixelinventor.game.tile;

import java.util.Random;

import kevinmerrill.pixelinventor.game.entity.Entity;
import kevinmerrill.pixelinventor.game.world.World;
import kevinmerrill.pixelinventor.resources.Resources;

public class TileWater extends Tile {

	public TileWater(int id, String spritename) {
		super(id, spritename);
		
	}
	public void onWalkedUpon(Entity entity, World world, long x,
			long y, float posX, float posY) {
		
	}
	
	public void onUpdated(World world, long x, long y) {

	}
	
	
	public void tick(World world, long x, long y) {
		
		x += (Resources.maxChunks / 2) * 16;
		y += (Resources.maxChunks / 2) * 16;
		
//		System.out.println(lastTime + ", " + System.currentTimeMillis() / 1000L);
		
				if (world.getTile(x, y - 1) == Tiles.Air.getId()) {
					world.setTile(x, y - 1, Tiles.Water.getId());
					world.setTile(x, y, Tiles.Air.getId());
				} else {
					
					if (world.getTile(x + 2, y) == Tiles.Water.getId() && world.getTile(x + 1, y) == Tiles.Air.getId()) {
						world.setTile(x + 1, y, Tiles.Water.getId());
						world.setTile(x, y, Tiles.Air.getId());
						return;
					}
					
					if (world.getTile(x - 1, y) == Tiles.Air.getId() && world.getTile(x + 1, y) == Tiles.Air.getId()) {
						if (new Random().nextInt(160) <= 4)
						world.setTile(x, y, Tiles.Air.getId());
					}
					
					if (world.getTile(x - 1, y) == Tiles.Water.getId() || world.getTileState(x - 1, y).isSolid() == true) {
						if (world.getTile(x + 1, y) == Tiles.Air.getId()) {
							world.setTile(x + 1, y, Tiles.Water.getId());
							world.setTile(x, y, Tiles.Air.getId());
						}
					}
					else if (world.getTile(x + 1, y) == Tiles.Water.getId() || world.getTileState(x + 1, y).isSolid() == true) {
						if (world.getTile(x - 1, y) == Tiles.Air.getId()) {
							world.setTile(x - 1, y, Tiles.Water.getId());
							world.setTile(x, y, Tiles.Air.getId());
						}
					}
					else {
						if (world.getTile(x, y - 1) == Tiles.Water.getId() || world.getTileState(x, y + 1).isSolid() == true) {
							
							
							
							if (new Random().nextInt(100) < 10) {
								world.setTile(x, y, Tiles.Air.getId());
							} else {
								world.setTile(x + new Random().nextInt(2) * 2 - 1, y, Tiles.Water.getId());
								world.setTile(x, y, Tiles.Air.getId());
							}
						} else {
							if (new Random().nextInt(100) == 0) {
								boolean left = false;
								for (int i = -5; i < 5; i++) {
									if (world.getTile(x + i, y - 1) == Tiles.Air.getId()) {
										left = true;
									}
								}
								if (left == true) {
									world.setTile(x, y, Tiles.Air.getId());
									world.setTile(x - 1, y, Tiles.Water.getId());
								}
								if (left == false) {
									world.setTile(x, y, Tiles.Air.getId());
									world.setTile(x + 1, y, Tiles.Water.getId());
								}
								
								if (new Random().nextInt(100) < 10) {
									world.setTile(x, y, Tiles.Air.getId());
								}
							}
						}
					}
					
					
				}
		
			
		
		
		
		
	}
	
	public boolean isSolid() {
		return false;
	}
	
	public boolean canShape() {
		return true;
	}
}

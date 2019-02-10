package kevinmerrill.pixelinventor.game.entity;

import kevinmerrill.pixelinventor.game.PixelInventor;
import kevinmerrill.pixelinventor.game.tile.Tile;
import kevinmerrill.pixelinventor.game.tile.Tiles;
import kevinmerrill.pixelinventor.game.world.World;
import kevinmerrill.pixelinventor.resources.Resources;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class EntityFallingTile extends Entity {

	public int tileID;
	public World world;
	
	public EntityFallingTile(float posX, float posY, int tileID, World world) {
		super(posX, posY, Resources.TILESIZE, Resources.TILESIZE);
		this.health = 1;
		this.maxHealth = 1;
		this.tileID = tileID;
		this.world = world;
	}

	@Override
	public void updateAlive() {
		
		if (this.collisionWithTile(posX + 8, posY - 1) == false) {
			if (velY > -6f) {
				velY -= 0.4f; 
			}
//			posY -= 6f;
		} else {
			this.isDead = true;
		}
		posX += (int)velX;
		posY += (int)velY;
	}

	@Override
	public void updateDead() {
		try {
			long x = (long)Math.round((posX + 8) / 32 - 0.5f) + (Resources.maxChunks / 2) * 16;
			long y = (long)Math.round((posY + 8) / 32 - 0.5f) + (Resources.maxChunks / 2) * 16;
			
			if (world != null) {
				if (world.getTile(x, y) == Tiles.Air.getId())
					world.setTile(x, y, tileID);
				else
					if (world.getTile(x, y + 1) == Tiles.Air.getId())
						world.setTile(x, y + 1, tileID);
			}
			
		}catch (Exception e){}
	}

	@Override
	public void render(SpriteBatch batch) {
		Tile.getTileStateFromId(tileID).render(batch, posX, posY, 0);
	}

}

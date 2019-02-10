package kevinmerrill.pixelinventor.game.tile;

import kevinmerrill.pixelinventor.game.Cam;
import kevinmerrill.pixelinventor.game.entity.Entity;
import kevinmerrill.pixelinventor.game.textures.LoadedTextures;
import kevinmerrill.pixelinventor.game.world.World;
import kevinmerrill.pixelinventor.resources.Resources;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class TileLilypad extends Tile {

	private String animation;
	
	public TileLilypad(int id, String spritename, String animation) {
		super(id, spritename);
		this.animation = animation;
	}

	public void onWalkedUpon(Entity entity, World world, long x,
			long y, float posX, float posY) {
		
	}
	
	public void onUpdated(World world, long x, long y) {
		
	}
	
	public boolean renderBack(long x, long y) {
		return true;
	}
	
	public void tick(World world, long X, long Y) {
		long x = X + (Resources.maxChunks / 2) * 16;
		long y = Y + (Resources.maxChunks / 2) * 16;
		if (world.getTile(x, y - 1) != Tiles.Air.getId() || world.getWater(x, y - 1) < 14 || world.getWater(x, y) > 0) {
			world.setTile(x, y, Tiles.Air.getId());
		}
	}
	
	public boolean isSolid() {
		return false;
	}
	
	public void render(SpriteBatch batch, long x, long y, int shape, float tileOffset) {
		if (isVisible() == false)
			return;
		batch.draw(LoadedTextures.LOADED.getTextureRegion(animation)[(int) ((System.currentTimeMillis() / 100) % 11)][0], (float)((float)x - Cam.x), (float)((float)(y + tileOffset - 16) - Cam.y), Resources.TILESIZE, Resources.TILESIZE);
	}
}

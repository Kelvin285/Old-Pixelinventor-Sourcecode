package kevinmerrill.pixelinventor.game.tile;

import imported.ImprovedNoise;
import kevinmerrill.pixelinventor.game.Cam;
import kevinmerrill.pixelinventor.game.entity.Entity;
import kevinmerrill.pixelinventor.game.textures.LoadedTextures;
import kevinmerrill.pixelinventor.game.world.World;
import kevinmerrill.pixelinventor.resources.Resources;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class TileTree extends Tile {

	private String animation;
		
	public TileTree(int id, String spritename, String animation) {
		super(id, spritename);
		this.animation = animation;
	}

	public void onWalkedUpon(Entity entity, World world, long x,
			long y, float posX, float posY) {
		
	}
	
	public void onUpdated(World world, long x, long y) {
		
	}
	
	public boolean renderBack(long x, long y) {
		return ImprovedNoise.noise(x / 2f, y / 2f, 0) < 0.0f;
	}
	
	public void tick(World world, long X, long Y) {
		long x = X + (Resources.maxChunks / 2) * 16;
		long y = Y + (Resources.maxChunks / 2) * 16;
		
		if (world.getTileState(x, y - 1).isSolid() == false || world.getTileState(x - 1, y - 1).isSolid() == false || world.getTileState(x + 1, y - 1).isSolid() == false || world.getTileState(x + 2, y - 1).isSolid() == false) {
			world.setTile(x, y, Tiles.Air.getId());
		}
	}
	
	public boolean isSolid() {
		return false;
	}
	public void render(SpriteBatch batch, long x, long y, int shape) {
		if (isVisible() == false)
			return;
		float mul = 2f;
		boolean flip = false;
		if (ImprovedNoise.noise(y / 2f, x / 2f, 0) < 0.0f)
			flip = true;
		if (flip == false)
			batch.draw(LoadedTextures.LOADED.getTexture(animation), (float)((float)x - Cam.x) - ((Resources.TILESIZE * 3) / 2) * mul, (float)((float)(y - 16) - Cam.y) + 12, Resources.TILESIZE * 4 * mul, Resources.TILESIZE * 4 * mul);
		else
			batch.draw(LoadedTextures.LOADED.getTexture(animation), (float)((float)x - Cam.x) - ((Resources.TILESIZE * 3) / 2) * mul + Resources.TILESIZE * 4 * mul, (float)((float)(y - 16) - Cam.y) + 12, -Resources.TILESIZE * 4 * mul, Resources.TILESIZE * 4 * mul);
	}
	
	public void render(SpriteBatch batch, long x, long y, int shape, float tileOffset) {
		if (isVisible() == false)
			return;
		float mul = 2f;
		
		boolean flip = false;
		if (ImprovedNoise.noise(y / 2f, x / 2f, 0) < 0.0f)
			flip = true;
		if (flip == false)
			batch.draw(LoadedTextures.LOADED.getTexture(animation), (float)((float)x - Cam.x) - ((Resources.TILESIZE * 3) / 2) * mul, (float)((float)(y - 16) - Cam.y) + 12, Resources.TILESIZE * 4 * mul, Resources.TILESIZE * 4 * mul);
		else
			batch.draw(LoadedTextures.LOADED.getTexture(animation), (float)((float)x - Cam.x) - ((Resources.TILESIZE * 3) / 2) * mul + Resources.TILESIZE * 4 * mul, (float)((float)(y - 16) - Cam.y) + 12, -Resources.TILESIZE * 4 * mul, Resources.TILESIZE * 4 * mul);
	}
}

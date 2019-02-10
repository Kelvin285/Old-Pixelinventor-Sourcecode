package kevinmerrill.pixelinventor.game.tile;

import imported.ImprovedNoise;
import kevinmerrill.pixelinventor.game.Cam;
import kevinmerrill.pixelinventor.game.entity.Entity;
import kevinmerrill.pixelinventor.game.textures.LoadedTextures;
import kevinmerrill.pixelinventor.game.world.World;
import kevinmerrill.pixelinventor.resources.Resources;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class TileCactus extends Tile {

	private String animation;
		
	private int frame = 0;
	private int max = 5;
	
	private long lastTime = System.currentTimeMillis() / 1000;
	
	public TileCactus(int id, String spritename, String animation) {
		super(id, spritename);
		this.animation = animation;
	}

	public void onWalkedUpon(Entity entity, World world, long x,
			long y, float posX, float posY) {
		
	}
	
	public void onUpdated(World world, long x, long y) {
		
	}
	
	public boolean renderBack(long x, long y) {
		frame = (int)Math.abs(ImprovedNoise.noise(x / 2f, y / 2f, 0) * 5) % 5;
		return ImprovedNoise.noise(x / 2f, y / 2f, 0) < 0.0f;
	}
	
	public void tick(World world, long X, long Y) {
		long x = X + (Resources.maxChunks / 2) * 16;
		long y = Y + (Resources.maxChunks / 2) * 16;
		
		
		//frame %= max;
		if (world.getTileState(x, y - 1).isSolid() == false || world.getTileState(x - 1, y - 1).isSolid() == false || world.getTileState(x + 1, y - 1).isSolid() == false) {
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
		batch.draw(LoadedTextures.LOADED.getTextureRegion(animation)[frame][0], (float)((float)x - Cam.x) - Resources.TILESIZE / 2, (float)((float)(y - 4) - Cam.y), Resources.TILESIZE * 2, Resources.TILESIZE * 2);
	}
	
	public void render(SpriteBatch batch, long x, long y, int shape, float tileOffset) {
		if (isVisible() == false)
			return;
		float mul = 2f;
		
		boolean flip = false;
		
		batch.draw(LoadedTextures.LOADED.getTextureRegion(animation)[frame][0], (float)((float)x - Cam.x) - Resources.TILESIZE / 2, (float)((float)(y - 4) - Cam.y), Resources.TILESIZE * 2, Resources.TILESIZE * 2);
	}
}

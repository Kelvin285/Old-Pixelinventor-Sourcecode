package kevinmerrill.pixelinventor.game.tile;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import kevinmerrill.pixelinventor.game.Cam;
import kevinmerrill.pixelinventor.game.entity.Entity;
import kevinmerrill.pixelinventor.game.textures.LoadedTextures;
import kevinmerrill.pixelinventor.game.world.World;
import kevinmerrill.pixelinventor.resources.Resources;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Tile implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public int id;
		
	public String spritename;
		
	private static Map<Integer, Tile> tiles = new HashMap<Integer, Tile>();
		
	public Tile(int id, String spritename) {
		this.id = id;
		this.spritename = spritename;
	}
	
	public boolean isVisible() {
		return true;
	}

	public boolean isSolid() {
		return true;
	}
	
	public boolean canShape() {
		return isSolid();
	}
	
	public static void incrementShape(int shape) {
		if (shape < 16) {
			shape ++;
		} else {
			shape = 0;
		}
		
		if (shape == 11 || shape == 7 || shape == 13 || shape == 14)
			shape++;
	}
	
	public static int updateShapeFromConnections(boolean u, boolean d, boolean l, boolean r) {
		int shape = (d ? 1 : 0) + (l ? 2 : 0) + (u ? 4 : 0) + (r ? 8 : 0);
		if (shape == 11)
			shape = 1;
		if (shape == 7)
			shape = 2;
		if (shape == 13)
			shape = 8;
		if (shape == 14)
			shape = 4;
		return shape;
	}
	
	public void render(SpriteBatch batch, float x, float y, int shape) {
		if (isVisible() == false)
			return;
		shape = 0;
//		Sprite spr = LoadedTextures.LOADED.getTextureAsSprite(spritename, shape * 16, 0, 16, 16);
//		batch.draw(spr, (float)((float)x - Cam.x), (float)((float)y - Cam.y), Resources.TILESIZE, Resources.TILESIZE);
		batch.draw(LoadedTextures.LOADED.getTextureRegion(spritename)[0][shape], (float)((float)x - Cam.x), (float)((float)y - Cam.y), Resources.TILESIZE, Resources.TILESIZE);
	}
	
	public void render(SpriteBatch batch, long x, long y, int shape) {
		if (isVisible() == false)
			return;
//		System.out.println("Length: " + LoadedTextures.LOADED.getTextureRegion(spritename)[0].length);
		batch.draw(LoadedTextures.LOADED.getTextureRegion(spritename)[0][shape], (float)((float)x - Cam.x), (float)((float)y - Cam.y), Resources.TILESIZE, Resources.TILESIZE);
//		Sprite spr = LoadedTextures.LOADED.getTextureAsSprite(spritename, shape * 16, 0, 16, 16);
//		batch.draw(spr, (float)((float)x - Cam.x), (float)((float)y - Cam.y), Resources.TILESIZE, Resources.TILESIZE);
	}
	
	public Tile getTileState() {
		return Tile.getTileStateFromId(id);
	}
	
	public static Tile getTileStateFromId(int id) {
		return tiles.get(id);
	}
	
	public void onWalkedUpon(Entity entity, World world, long x,
			long y, float posX, float posY) {
		
	}
	
	public void onUpdated(World world, long x, long y) {
		
	}
	
	public void tick(World world, long x, long y) {
		
	}
	
	public boolean renderBack(long x, long y) {
		return false;
	}
	
	public static void loadTiles() {
		tiles.put(Tiles.Dirt.getId(), new TileDirt(Tiles.Dirt.getId(), "TileDirt"));
		tiles.put(Tiles.Stone.getId(), new TileStone(Tiles.Stone.getId(), "TileStone"));
		tiles.put(Tiles.Grass.getId(), new TileGrass(Tiles.Grass.getId(), "TileGrass"));
		tiles.put(Tiles.Sand.getId(), new TileSand(Tiles.Sand.getId(), "TileSand"));
		tiles.put(Tiles.Air.getId(), new TileAir());
		tiles.put(Tiles.Snow.getId(), new TileSnow(Tiles.Snow.getId(), "TileSnow"));
		tiles.put(Tiles.SnowyGrass.getId(), new TileSnowGrass(Tiles.SnowyGrass.getId(), "TileSnowGrass"));
		tiles.put(Tiles.DeadGrass.getId(), new TileDeadGrass(Tiles.DeadGrass.getId(), "TileDeadGrass"));
		tiles.put(Tiles.Sandstone.getId(), new TileSandstone(Tiles.Sandstone.getId(), "TileSandstone"));
		tiles.put(Tiles.Water.getId(), new TileWater(Tiles.Water.getId(), "TileWater"));
		tiles.put(Tiles.Wood.getId(), new TileWood(Tiles.Wood.getId(), "TileWood"));
		tiles.put(Tiles.Leaves.getId(), new TileLeaves(Tiles.Leaves.getId(), "TileLeaves"));
		tiles.put(Tiles.Lilypad.getId(), new TileLilypad(Tiles.Lilypad.getId(), "TileLilypad", "LilyPadGreen"));
		tiles.put(Tiles.PurpleLilypad.getId(), new TileLilypad(Tiles.PurpleLilypad.getId(), "TileLilypadPurple", "LilyPad"));
		tiles.put(Tiles.Broadleaf.getId(), new TileTree(Tiles.Broadleaf.getId(), "TileBroadleaf", "Broadleaf"));
		tiles.put(Tiles.Spruce.getId(), new TileTree(Tiles.Spruce.getId(), "TileSpruce", "Spruce"));
		tiles.put(Tiles.Broadleaf_2.getId(), new TileTree(Tiles.Broadleaf_2.getId(), "TileBroadleaf_2", "Broadleaf_2"));
		tiles.put(Tiles.Tallgrass.getId(), new TileTallGrass(Tiles.Tallgrass.getId(), "Tallgrass", "Tallgrass"));
		tiles.put(Tiles.Tallgrass_Green.getId(), new TileTallGrass(Tiles.Tallgrass_Green.getId(), "Tallgrass_2", "Tallgrass_2"));
		tiles.put(Tiles.Cactus.getId(), new TileCactus(Tiles.Cactus.getId(), "Cactus", "Cactus"));
		tiles.put(Tiles.Acacia.getId(), new TileTree(Tiles.Acacia.getId(), "Acacia", "Acacia"));
		tiles.put(Tiles.Rubber.getId(), new TileWood(Tiles.Rubber.getId(), "TileRubber"));
		tiles.put(Tiles.Fiberglass.getId(), new TileWood(Tiles.Fiberglass.getId(), "TileFiberglass"));
	}

	public void render(SpriteBatch batch, long x, long y, int shape, float tileOffset) {
		if (isVisible() == false)
			return;
//		System.out.println("Length: " + LoadedTextures.LOADED.getTextureRegion(spritename)[0].length);
		batch.draw(LoadedTextures.LOADED.getTextureRegion(spritename)[0][shape], (float)((float)x - Cam.x), (float)((float)(y + tileOffset) - Cam.y), Resources.TILESIZE, Resources.TILESIZE);
//		Sprite spr = LoadedTextures.LOADED.getTextureAsSprite(spritename, shape * 16, 0, 16, 16);
//		batch.draw(spr, (float)((float)x - Cam.x), (float)((float)y - Cam.y), Resources.TILESIZE, Resources.TILESIZE);
	}

	
}

package kevinmerrill.pixelinventor.resources;

import kevinmerrill.pixelinventor.game.world.biome.BiomeSurface;

import com.badlogic.gdx.graphics.g2d.BitmapFont;

public class Resources {
	public static final int TITLESIZE1 = 420, TITLESIZE2 = 205;
	public static final int TILESIZE = 32;
	
	public static BiomeSurface flatBiome = BiomeSurface.GRASSLAND;
	
	public static BitmapFont font = new BitmapFont();

	public static String currentWorldName;
	
	public static boolean inWorld = false;
	
	public static final long maxChunks = 30000000L / 16L;

	public static final float soundVolumeMultiplier = 0.05f;
	
}

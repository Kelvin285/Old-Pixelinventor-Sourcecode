package kevinmerrill.pixelinventor.game.world;

import java.io.Serializable;

import kevinmerrill.pixelinventor.game.world.biome.BiomeSurface;
import kevinmerrill.pixelinventor.game.world.gen.IWorldGenerator;
import kevinmerrill.pixelinventor.game.world.gen.WorldGeneratorMain;

public class WorldSettings implements Serializable {
	private static final long serialVersionUID = 1L;
	public IWorldGenerator generator = new WorldGeneratorMain(BiomeSurface.GRASSLAND);
}	

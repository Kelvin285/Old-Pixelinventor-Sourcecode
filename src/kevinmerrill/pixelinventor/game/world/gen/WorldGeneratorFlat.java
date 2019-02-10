package kevinmerrill.pixelinventor.game.world.gen;

import kevinmerrill.pixelinventor.game.tile.Tiles;
import kevinmerrill.pixelinventor.game.world.World;
import kevinmerrill.pixelinventor.game.world.biome.BiomeSurface;
import kevinmerrill.pixelinventor.game.world.chunk.Chunk;
import kevinmerrill.pixelinventor.resources.Resources;

public class WorldGeneratorFlat implements IWorldGenerator {

	private static final long serialVersionUID = 1L;
	private BiomeSurface defaultBiome;
	
	public WorldGeneratorFlat(BiomeSurface defaultBiome) {
		this.defaultBiome = defaultBiome;
	}
	
	public void generateChunk(Chunk chunk, World worldIn) {
		if (chunk == null)
			return;
		if (chunk.isLoaded() == true)
			return;
		chunk.setBiome(defaultBiome);
		if(chunk.getChunkY() == Resources.maxChunks / 2) {
			for (int x = 0; x < 16; x++) {
				for (int y = 0; y < 16; y++) {
					if (y == 8) {
						chunk.setTile(x, y, Tiles.Grass.getId(), worldIn);
					}else
					if (y < 8) {
						chunk.setTile(x, y, Tiles.Dirt.getId(), worldIn);
					}else
						chunk.setTile(x, y, Tiles.Air.getId(), worldIn);

				}
			}
		} else {
			if (chunk.getChunkY() < Resources.maxChunks / 2) {
				for (int x = 0; x < 16; x++) {
					for (int y = 0; y < 16; y++) {
						
						chunk.setTile(x, y, Tiles.Dirt.getId(), worldIn);
						
					}
				}
			} else {
				for (int x = 0; x < 16; x++) {
					for (int y = 0; y < 16; y++) {
						
						chunk.setTile(x, y, Tiles.Air.getId(), worldIn);
						
					}
				}
			}
		}
		chunk.setLoaded(true);
	}
	
}

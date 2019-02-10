package kevinmerrill.pixelinventor.game.world.biome;

import java.io.Serializable;

import kevinmerrill.pixelinventor.game.tile.Tiles;

public enum BiomeSurface implements Serializable {
	NULL_BIOME(0, 0), GRASSLAND(0, 8), DESERT(-5, 25, Tiles.Sand.getId(), Tiles.Sand.getId(), Tiles.Sandstone.getId()),
	SNOW(-5, -25, Tiles.SnowyGrass.getId(), Tiles.Dirt.getId(), Tiles.Stone.getId()),
	SAVANNAH(-5, -25, Tiles.DeadGrass.getId(), Tiles.Dirt.getId(), Tiles.Stone.getId()),
	;
	public float minHeight;
	public float maxHeight;
	
	public int surfaceTile = Tiles.Grass.getId();
	public int groundTile = Tiles.Dirt.getId();
	public int stoneTile = Tiles.Stone.getId();
	
	BiomeSurface(float minHeight, float maxHeight) {
		this.minHeight = minHeight;
		this.maxHeight = maxHeight;
	}
	
	BiomeSurface(float minHeight, float maxHeight, int surface, int ground, int stone) {
		this.minHeight = minHeight;
		this.maxHeight = maxHeight;
		this.surfaceTile = surface;
		this.groundTile = ground;
		this.stoneTile = stone;
	}
	
	
}

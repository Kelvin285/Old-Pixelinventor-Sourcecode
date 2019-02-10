package kevinmerrill.pixelinventor.game.world.gen;

import java.io.Serializable;

import kevinmerrill.pixelinventor.game.world.World;
import kevinmerrill.pixelinventor.game.world.chunk.Chunk;

public interface IWorldGenerator extends Serializable {
	
	abstract void generateChunk(Chunk chunk, World worldIn);
}

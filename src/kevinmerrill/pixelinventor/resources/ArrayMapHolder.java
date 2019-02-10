package kevinmerrill.pixelinventor.resources;

import java.util.ArrayList;
import java.util.List;

import kevinmerrill.pixelinventor.game.entity.Entity;
import kevinmerrill.pixelinventor.game.world.chunk.Chunk;

import com.badlogic.gdx.utils.ArrayMap;

public class ArrayMapHolder {
	public static ArrayMap<Long, Chunk> loadedChunks = new ArrayMap<Long, Chunk>();
	public static ArrayMap<Long, Boolean> loaded = new ArrayMap<Long, Boolean>();
	public static List<Entity> entities = new ArrayList<Entity>();
	public static void reset() {
		loadedChunks = new ArrayMap<Long, Chunk>();
		loaded = new ArrayMap<Long, Boolean>();
		entities = new ArrayList<Entity>();
	}
}

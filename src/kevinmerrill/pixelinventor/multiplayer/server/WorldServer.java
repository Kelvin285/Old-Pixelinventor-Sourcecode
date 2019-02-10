package kevinmerrill.pixelinventor.multiplayer.server;

import java.util.ArrayList;

import kevinmerrill.pixelinventor.game.world.World;
import kevinmerrill.pixelinventor.game.world.biome.BiomeSurface;
import kevinmerrill.pixelinventor.game.world.chunk.Chunk;
import kevinmerrill.pixelinventor.game.world.gen.IWorldGenerator;
import kevinmerrill.pixelinventor.multiplayer.client.entities.EntityPlayerMP;
import kevinmerrill.pixelinventor.resources.ArrayMapHolder;
import kevinmerrill.pixelinventor.resources.Resources;

import com.badlogic.gdx.utils.ArrayMap;

public class WorldServer extends World {
	
	
		
	public WorldServer(IWorldGenerator worldGenerator) {
		super(worldGenerator);
		this.isServerWorld = true;
		ArrayMapHolder.reset();
	}
	
	
	@Override
	public void update() {
		
	}
	
	public void update(ArrayList<EntityPlayerMP> players) {
		
		ArrayMapHolder.loaded.clear();
		try {
		for (int i = 0; i < players.size(); i++) {
			long pChunkX = (long)(players.get(i).posX / Resources.TILESIZE) / 16;
			long pChunkY = (long)(players.get(i).posY / Resources.TILESIZE) / 16;
			for (long x = pChunkX - view; x < pChunkX + view; x++) {
				for (long y = pChunkY - view; y < pChunkY + view; y++) {
					
					
					
					if (ArrayMapHolder.loaded.containsKey(x + y * Resources.maxChunks) == false) {
						ArrayMapHolder.loaded.put(x + y * Resources.maxChunks, false);
					} else {
						ArrayMapHolder.loaded.put(x + y * Resources.maxChunks, true);
					}
					if (ArrayMapHolder.loadedChunks.containsKey(x + y * Resources.maxChunks) == false) {
						Chunk load = Chunk.loadChunk(x, y, true);
						if (load == null) {
							Chunk chunk = new Chunk(x, y, BiomeSurface.NULL_BIOME, worldGenerator);
							ArrayMapHolder.loadedChunks.put(x + y * Resources.maxChunks, chunk);
						} else {
							ArrayMapHolder.loadedChunks.put(x + y * Resources.maxChunks, load);
						}
					} else {
						worldGenerator.generateChunk(ArrayMapHolder.loadedChunks.get(x + y * Resources.maxChunks), this);
					}
					
					

				}
			}
		}
		
		for (int j = 0; j < players.size(); j++) {
			long pChunkX = (long)(players.get(j).posX / Resources.TILESIZE) / 16;
			long pChunkY = (long)(players.get(j).posY / Resources.TILESIZE) / 16;
			if (savingChunks == false) {
				for (int i = 0; i < ArrayMapHolder.loadedChunks.size; i++) {
					Chunk chunk = ArrayMapHolder.loadedChunks.getValueAt(i);
					
					if (ArrayMapHolder.loaded.getValueAt(i) == false) {
						if (unloadedChunks.size() < 4) {
							unloadedChunks.add(chunk);
							ArrayMapHolder.loadedChunks.removeIndex(i);
						} else {
							break;
						}
					}
				}
				if (unloadedChunks.size() >= 4 || savingChunks == true) {
					savingChunks = true;
					if (unloadedChunks.size() > 0) {
						unloadedChunks.get(unloadedChunks.size() - 1).saveChunk(true);
						unloadedChunks.remove(unloadedChunks.size() - 1);
					} else {
						savingChunks = false;
					}
					
			}
			}
		}
		}catch (Exception e){}
		for (int i = 0; i  <ArrayMapHolder.loadedChunks.size; i++) {
			ArrayMapHolder.loadedChunks.getValueAt(i).update(this);
		}
	}
	
	@Override
	public boolean isServerWorld() {
		return true;
	}
	
	public void dispose() {
		Chunk.saveSeed(getSeed(), true);
		
		for (int i = 0; i < ArrayMapHolder.loadedChunks.size; i++) {
			Chunk chunk = ArrayMapHolder.loadedChunks.getValueAt(i);
			if (chunk != null)
			chunk.saveChunk(true);
		}
	}
	
	
}

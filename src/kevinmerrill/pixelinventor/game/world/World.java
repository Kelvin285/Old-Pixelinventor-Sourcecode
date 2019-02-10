package kevinmerrill.pixelinventor.game.world;

import imported.ImprovedNoise;

import java.awt.Point;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import kevinmerrill.pixelinventor.game.Cam;
import kevinmerrill.pixelinventor.game.PixelInventor;
import kevinmerrill.pixelinventor.game.entity.Entity;
import kevinmerrill.pixelinventor.game.tile.Tile;
import kevinmerrill.pixelinventor.game.tile.Tiles;
import kevinmerrill.pixelinventor.game.world.biome.BiomeSurface;
import kevinmerrill.pixelinventor.game.world.chunk.Chunk;
import kevinmerrill.pixelinventor.game.world.gen.IWorldGenerator;
import kevinmerrill.pixelinventor.resources.ArrayMapHolder;
import kevinmerrill.pixelinventor.resources.Resources;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class World implements Serializable {

	public IWorldGenerator worldGenerator;
	
	
	public List<Chunk> unloadedChunks = new ArrayList<Chunk>();
	
	private long seed;
	
	public Random random;
	
	public boolean savingChunks;
	
	protected int view = 10;

	public boolean isServerWorld = false;
		
	public World(IWorldGenerator worldGenerator) {
		this.worldGenerator = worldGenerator;
		this.random = new Random();
		seed = Chunk.loadSeed(this.isServerWorld());
		System.out.println("new world!");
		ArrayMapHolder.reset();
	}
	
	long SX = 0;
	long SY = 0;
	
	long second = System.currentTimeMillis() / 1000L;
	long lastSecond = System.currentTimeMillis() / 1000L;
	
	public int getWorldHeight(long x, long y) {
		double heightMod = 64;
		double distMod = 1d;
		double heightmap = ImprovedNoise.noise((double)x / (distMod * 1000d), 0, getSeed() / 999999d) * heightMod;
		
		int heightMul = 1;
		
		if (heightmap < -20) {
			heightMul += 10;
		}
		if (heightmap < -10) {
			heightMul += 10;
		}
		if (heightmap < -5) {
			heightMul += 5;
		}
		
		heightmap *= heightMul;
		
		
		
		double interp = 0.5f;
		double roughness = ImprovedNoise.noise((double)x / (distMod * 800d), (double)y / (15d), getSeed() / 999999d);
		double detail = ImprovedNoise.noise((double)x / 20d, 0, getSeed() / 999999d);
		
		double noise = heightmap + (roughness + interp * (detail - roughness)) * (heightMod / 2d);
		return (int)noise;
	}
	
	public void update() {
		long camChunkX = (long)(Cam.x / Resources.TILESIZE) / 16;
		long camChunkY = (long)(Cam.y / Resources.TILESIZE) / 16;
		
		camChunkX += Resources.maxChunks / 2;
		camChunkY += Resources.maxChunks / 2;
		
		long mouseX = (long)Math.round(((PixelInventor.mouse.x + Cam.x)) / 32d - 0.5d);
		long mouseY = (long)Math.round(((PixelInventor.mouse.y + Cam.y)) / 32d - 0.5d);
		
		long cx = (long)Math.round(((Cam.x)) / 32d - 0.5d) + (Resources.maxChunks / 2) * 16;
		long cy = (long)Math.round(((Cam.y)) / 32d - 0.5d) + (Resources.maxChunks / 2) * 16;
		
		
		mouseX += (Resources.maxChunks / 2) * 16;
		mouseY += (Resources.maxChunks / 2) * 16;
		
		long MX = 10;
		long MY = 5;
		int dist = 23;
		int height = getWorldHeight(cx - (Resources.maxChunks / 2) * 16 + dist / 2, cy - (Resources.maxChunks / 2) * 16 + dist);
		if (height <= -20) {
			
			if (PixelInventor.turquoise.isPlaying() == false) {
				
				PixelInventor.turquoise.play();
				PixelInventor.titleMusic.stop();
			}
		} else {
			PixelInventor.turquoise.stop();
		}
		

		if (second > lastSecond) {
			lastSecond = System.currentTimeMillis() / 1000L;
			if (PixelInventor.client != null)
				for (long x = -30; x < 30; x++) {
					for (long y = -10; y < 20; y++) {
						PixelInventor.client.sendData(("tilerequest\n"+(long)(x + cx + 10)+"\n"+(long)(y + cy)).getBytes());
						PixelInventor.client.sendData(("waterrequest\n"+(long)(x + cx + 10)+"\n"+(long)(y + cy)).getBytes());

					}
				}
		}
		second = System.currentTimeMillis() / 1000L;


		if (Gdx.input.isButtonPressed(0)) {
			if (this.isServerWorld() == true) {
				if (this.getTile(mouseX, mouseY) != Tiles.Water.getId())
				PixelInventor.client.sendData(("tilemod\n"+mouseX+"\n"+mouseY+"\n"+Tiles.Air.getId()).getBytes());
			} else {
				if (this.getTile(mouseX, mouseY) != Tiles.Water.getId()) {
					this.setTile(mouseX, mouseY, Tiles.Air.getId());
					
					for (int xx = -1; xx < 2; xx++) {
						for (int yy = -1; yy < 2; yy++) {
							this.getTileState(mouseX + xx, mouseY + yy).onUpdated(this, mouseX + xx, mouseY + yy);
						}
					}
				}
				
			}
		}
		else
			if (Gdx.input.isButtonPressed(1) && System.currentTimeMillis() % 100 < 20 && this.getTile(mouseX, mouseY) == Tiles.Air.getId()) {
				if (this.isServerWorld() == true) {
					
					PixelInventor.client.sendData(("tilemod\n"+mouseX+"\n"+mouseY+"\n"+Tiles.Stone.getId()).getBytes());
				} else 
				{
					this.setTile(mouseX, mouseY, Tiles.Stone.getId());
					this.getTileState(mouseX, mouseY).onUpdated(this, mouseX, mouseY);
				}
			}
		
		if (Gdx.input.isButtonPressed(2) && System.currentTimeMillis() % 100 < 20) {
			if (this.isServerWorld() == true) {
				PixelInventor.client.sendData(("watermod\n"+mouseX+"\n"+mouseY+"\n"+16).getBytes());

//				PixelInventor.client.sendData(("tilemod\n"+mouseX+"\n"+mouseY+"\n"+Tiles.Water.getId()).getBytes());
			} else 
			{
				this.setWater(mouseX, mouseY, 16);
				this.getTileState(mouseX, mouseY).onUpdated(this, mouseX, mouseY);
			}
		}
		
//		if (System.currentTimeMillis() % 10000 < 9800)
//			return;
		
//		System.out.println("saving chunks...");
		
		
		
		if (this.isServerWorld() == false) {
			view = 3;
		}else{
			view = 3;
		}
//		if (System.currentTimeMillis() % 100 < 10 || isRemote() == false)
		for (long x = camChunkX - view; x < camChunkX + view; x++) {
			for (long y = camChunkY - view; y < camChunkY + view; y++) {
				
				
				
				
				if (x >= 0 && x <= Resources.maxChunks)
					if (y >= 0 && y <= Resources.maxChunks)
					{
						
						
						
						
						if (this.isServerWorld() == false) {

							if (ArrayMapHolder.loadedChunks.containsKey(x + y * Resources.maxChunks) == false) {
								Chunk load = Chunk.loadChunk(x, y, this.isServerWorld());
								if (load == null) {
									Chunk chunk = new Chunk(x, y, BiomeSurface.NULL_BIOME, worldGenerator);
									ArrayMapHolder.loadedChunks.put(x + y * Resources.maxChunks, chunk);
								} else {
									ArrayMapHolder.loadedChunks.put(x + y * Resources.maxChunks, load);
								}
							} else {
								worldGenerator.generateChunk(ArrayMapHolder.loadedChunks.get(x + y * Resources.maxChunks), this);
							}
						} else {
							if (ArrayMapHolder.loadedChunks.containsKey(x + y * Resources.maxChunks) == false) {
								{
									Chunk chunk = new Chunk(x, y, BiomeSurface.NULL_BIOME, worldGenerator);
									ArrayMapHolder.loadedChunks.put(x + y * Resources.maxChunks, chunk);
	//								if (ArrayMapHolder.loadedChunks.get(x + y * Resources.maxChunks) == null) {
									PixelInventor.client.sendData(("reqchunk\n"+x+"\n"+y).getBytes());
									PixelInventor.client.sendData(("tilerequest\n"+(long)(MX + cx)+"\n"+(long)(MY + cy)).getBytes());
								}
//								Chunk chunk = new Chunk(x, y, BiomeSurface.NULL_BIOME, worldGenerator);
//								ArrayMapHolder.loadedChunks.put(x + y * Resources.maxChunks, chunk);
								
								
							}
						}
						
					}
			}
		}
		if (savingChunks == false) {
			for (int i = 0; i < ArrayMapHolder.loadedChunks.size; i++) {
				Chunk chunk = ArrayMapHolder.loadedChunks.getValueAt(i);
				if (Point.distance(camChunkX, camChunkY, chunk.getChunkX(), chunk.getChunkY()) >= view) {
					if (unloadedChunks.size() < 4) {
						if (this.isServerWorld() == false)
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
					unloadedChunks.get(unloadedChunks.size() - 1).saveChunk(this.isServerWorld());
					unloadedChunks.remove(unloadedChunks.size() - 1);
				} else {
					savingChunks = false;
				}
				
		}
		}
	}
	
	public Chunk genChunk(long x, long y) {
		Chunk load = Chunk.loadChunk(x, y, true);
		if (load == null) {
			Chunk chunk = new Chunk(x, y, BiomeSurface.NULL_BIOME, worldGenerator);
			ArrayMapHolder.loadedChunks.put(x + y * Resources.maxChunks, chunk);
			load = chunk;
		} else {
			ArrayMapHolder.loadedChunks.put(x + y * Resources.maxChunks, load);
		}
		
		worldGenerator.generateChunk(ArrayMapHolder.loadedChunks.get(x + y * Resources.maxChunks), this);
		return load;
		
	}
	
	public void renderBG(SpriteBatch batch) {
		view = (int)5;
		
		long camChunkX = (long)((Cam.x + 500) / Resources.TILESIZE) / 16;
		long camChunkY = (long)((Cam.y) / Resources.TILESIZE) / 16;
		
		camChunkX += Resources.maxChunks / 2;
		camChunkY += Resources.maxChunks / 2;
		
		
		for (long x = camChunkX - view / 2; x < camChunkX + view / 2; x++) {
			for (long y = camChunkY - view / 2; y < camChunkY + view / 2; y++) {
				if (x >= 0 && x <= Resources.maxChunks)
					if (y >= 0 && y <= Resources.maxChunks)
					{
						
						if (ArrayMapHolder.loadedChunks.containsKey(x + y * Resources.maxChunks) == true) {
							Chunk chunk = ArrayMapHolder.loadedChunks.get(x + y * Resources.maxChunks);
							if (chunk != null)
							chunk.renderBG(batch, this);
							
						}
						
					}
			}
		}
		
		
		
	}
	
	public void render(SpriteBatch batch) {
		
		view = (int)5;
		for (int i = 0; i < ArrayMapHolder.entities.size(); i++) {
			if (i >= ArrayMapHolder.entities.size())
				break;
			if (ArrayMapHolder.entities.get(i).isDead == false) {
				ArrayMapHolder.entities.get(i).updateAlive();
				ArrayMapHolder.entities.get(i).render(batch);
			}
			else
			{
				ArrayMapHolder.entities.get(i).updateDead();
				ArrayMapHolder.entities.remove(i);
			}
		}
		
		long camChunkX = (long)((Cam.x + 500) / Resources.TILESIZE) / 16;
		long camChunkY = (long)((Cam.y) / Resources.TILESIZE) / 16;
		
		camChunkX += Resources.maxChunks / 2;
		camChunkY += Resources.maxChunks / 2;
		
		
		for (long x = camChunkX - view / 2; x < camChunkX + view / 2; x++) {
			for (long y = camChunkY - view / 2; y < camChunkY + view / 2; y++) {
				if (x >= 0 && x <= Resources.maxChunks)
					if (y >= 0 && y <= Resources.maxChunks)
					{
						
						if (ArrayMapHolder.loadedChunks.containsKey(x + y * Resources.maxChunks) == true) {
							Chunk chunk = ArrayMapHolder.loadedChunks.get(x + y * Resources.maxChunks);
							if (chunk != null)
							chunk.render(batch, this);
							
						}
						
					}
			}
		}
		
		
		
	}
	
	public boolean isServerWorld() {
		return isServerWorld;
	}
	
	public void save() {
		Chunk.saveWorld(this, isServerWorld);
		Chunk.saveSeed(getSeed(), this.isServerWorld());
		
		for (int i = 0; i < ArrayMapHolder.loadedChunks.size; i++) {
			Chunk chunk = ArrayMapHolder.loadedChunks.getValueAt(i);
			if (chunk != null)
			chunk.saveChunk(this.isServerWorld());
		}
	}
	
	public void dispose() {
		Chunk.saveSeed(getSeed(), this.isServerWorld());
		
		for (int i = 0; i < ArrayMapHolder.loadedChunks.size; i++) {
			Chunk chunk = ArrayMapHolder.loadedChunks.getValueAt(i);
			if (chunk != null)
			chunk.saveChunk(this.isServerWorld());
		}
	}
	
	public Tile getTileState(int x, int y, long cx, long cy) {
		long chunkX = (long)x + cx * 16L;
		long chunkY = (long)y + cy * 16L;
		
		
		
		return getTileState(chunkX, chunkY);
	}
	
	public int getTile(int x, int y, long cx, long cy) {
		long chunkX = (long)x + cx * 16L;
		long chunkY = (long)y + cy * 16L;
		
		
		
		return getTile(chunkX, chunkY);
	}
	
	public Tile getTileState(long x, long y) {
		
		return Tile.getTileStateFromId(getTile(x, y));
	}
	
	public float getLight(long x, long y) {
		long chunkX = x / 16L;
		long chunkY = y / 16L;
		
		if (x < 0)
			chunkX = (x + 1) / 16L - 1;
		if (y < 0)
			chunkY -= 1;
		
		int j = (int)(x % 16L);
		int k = (int)(y % 16L);
		
		if (j < 0)
			j += 16;
		if (k < 0)
			k += 16;
		
		if (ArrayMapHolder.loadedChunks.containsKey(chunkX + chunkY * Resources.maxChunks) == true) {
			Chunk chunk = ArrayMapHolder.loadedChunks.get(chunkX + chunkY * Resources.maxChunks);

			if (chunk != null)
			return chunk.getLight(j, k);
		}
		
		return 0;
	}
	
	public int getWater(long x, long y) {
		long chunkX = x / 16L;
		long chunkY = y / 16L;
		
		if (x < 0)
			chunkX = (x + 1) / 16L - 1;
		if (y < 0)
			chunkY -= 1;
		
		int j = (int)(x % 16L);
		int k = (int)(y % 16L);
		
		if (j < 0)
			j += 16;
		if (k < 0)
			k += 16;
		
		if (ArrayMapHolder.loadedChunks.containsKey(chunkX + chunkY * Resources.maxChunks) == true) {
			Chunk chunk = ArrayMapHolder.loadedChunks.get(chunkX + chunkY * Resources.maxChunks);

			if (chunk != null)
			return chunk.getWater(j, k);
		}
		
		return 0;
	}
	public int getTileBG(long x, long y) {
		long chunkX = x / 16L;
		long chunkY = y / 16L;
		
		if (x < 0)
			chunkX = (x + 1) / 16L - 1;
		if (y < 0)
			chunkY -= 1;
		
		int j = (int)(x % 16L);
		int k = (int)(y % 16L);
		
		if (j < 0)
			j += 16;
		if (k < 0)
			k += 16;
		
		if (ArrayMapHolder.loadedChunks.containsKey(chunkX + chunkY * Resources.maxChunks) == true) {
			Chunk chunk = ArrayMapHolder.loadedChunks.get(chunkX + chunkY * Resources.maxChunks);

			if (chunk != null)
			return chunk.getTileBG(j, k);
		}
		
		return 0;
	}
	public double getTileOffset(long x, long y) {
		long chunkX = x / 16L;
		long chunkY = y / 16L;
		
		if (x < 0)
			chunkX = (x + 1) / 16L - 1;
		if (y < 0)
			chunkY -= 1;
		
		int j = (int)(x % 16L);
		int k = (int)(y % 16L);
		
		if (j < 0)
			j += 16;
		if (k < 0)
			k += 16;
		
		if (ArrayMapHolder.loadedChunks.containsKey(chunkX + chunkY * Resources.maxChunks) == true) {
			Chunk chunk = ArrayMapHolder.loadedChunks.get(chunkX + chunkY * Resources.maxChunks);

			if (chunk != null)
			return chunk.getTileOffset(j, k);
		}
		
		return 0;
	}
	
	public int getTile(long x, long y) {
		long chunkX = x / 16L;
		long chunkY = y / 16L;
		
		if (x < 0)
			chunkX = (x + 1) / 16L - 1;
		if (y < 0)
			chunkY -= 1;
		
		int j = (int)(x % 16L);
		int k = (int)(y % 16L);
		
		if (j < 0)
			j += 16;
		if (k < 0)
			k += 16;
		
		if (ArrayMapHolder.loadedChunks.containsKey(chunkX + chunkY * Resources.maxChunks) == true) {
			Chunk chunk = ArrayMapHolder.loadedChunks.get(chunkX + chunkY * Resources.maxChunks);

			if (chunk != null)
			return chunk.getTile(j, k);
		}
		
		return 0;
	}

	public void setTile(int x, int y, long cx, long cy, int tile) {
		long chunkX = x + cx * 16L;
		long chunkY = y + cy * 16L;
		
		
		
		setTile(chunkX, chunkY, tile);
	}
	
	public boolean hasSpawnedPlayer = false;
	
	public void setLight(long x, long y, float value) {
	
		
		long chunkX = x / 16L;
		long chunkY = y / 16L;
		
		
		
		if (x < 0)
			chunkX -= 1;
		if (y < 0)
			chunkY -= 1;
		
		
		int j = (int)(x % 16L);
		int k = (int)(y % 16L);
		
		if (j < 0)
			j += 16;
		if (k < 0)
			k += 16;
		
		if (ArrayMapHolder.loadedChunks.containsKey(chunkX + chunkY * Resources.maxChunks) == true) {
			Chunk chunk = ArrayMapHolder.loadedChunks.get(chunkX + chunkY * Resources.maxChunks);

			if (chunk != null)
				chunk.setLight(j, k, value);
			else
			{
				chunk = genChunk(chunkX, chunkY);
				if (chunk != null)
					chunk.setLight(j, k, value);
				ArrayMapHolder.loadedChunks.put(chunkX + chunkY * Resources.maxChunks, chunk);
			}
		} else {
			Chunk chunk = genChunk(chunkX, chunkY);
			if (chunk != null)
				chunk.setLight(j, k, value);
			ArrayMapHolder.loadedChunks.put(chunkX + chunkY * Resources.maxChunks, chunk);
		}
	}
	
	public void setWater(long x, long y, int pressure) {
	
		
		long chunkX = x / 16L;
		long chunkY = y / 16L;
		
		
		
		if (x < 0)
			chunkX -= 1;
		if (y < 0)
			chunkY -= 1;
		
		
		int j = (int)(x % 16L);
		int k = (int)(y % 16L);
		
		if (j < 0)
			j += 16;
		if (k < 0)
			k += 16;
		
		if (ArrayMapHolder.loadedChunks.containsKey(chunkX + chunkY * Resources.maxChunks) == true) {
			Chunk chunk = ArrayMapHolder.loadedChunks.get(chunkX + chunkY * Resources.maxChunks);

			if (chunk != null)
				chunk.setWater(j, k, pressure);
			else
			{
				chunk = genChunk(chunkX, chunkY);
				if (chunk != null)
					chunk.setWater(j, k, pressure);
				ArrayMapHolder.loadedChunks.put(chunkX + chunkY * Resources.maxChunks, chunk);
			}
		} else {
			Chunk chunk = genChunk(chunkX, chunkY);
			if (chunk != null)
				chunk.setWater(j, k, pressure);
			ArrayMapHolder.loadedChunks.put(chunkX + chunkY * Resources.maxChunks, chunk);
		}
	}
	

	public void setTileBG(long x, long y, int tile) {
	
		
		long chunkX = x / 16L;
		long chunkY = y / 16L;
		
		
		
		if (x < 0)
			chunkX -= 1;
		if (y < 0)
			chunkY -= 1;
		
		
		int j = (int)(x % 16L);
		int k = (int)(y % 16L);
		
		if (j < 0)
			j += 16;
		if (k < 0)
			k += 16;
		
		if (ArrayMapHolder.loadedChunks.containsKey(chunkX + chunkY * Resources.maxChunks) == true) {
			Chunk chunk = ArrayMapHolder.loadedChunks.get(chunkX + chunkY * Resources.maxChunks);

			if (chunk != null)
				chunk.setTileBG(j, k, tile, this);
			else
			{
				chunk = genChunk(chunkX, chunkY);
				if (chunk != null)
					chunk.setTileBG(j, k, tile, this);
				ArrayMapHolder.loadedChunks.put(chunkX + chunkY * Resources.maxChunks, chunk);
			}
		} else {
			Chunk chunk = genChunk(chunkX, chunkY);
			if (chunk != null) {
				chunk.setTileBG(j, k, tile, this);
				System.out.println("true");
				System.out.println(chunk.getTileBG(j, k));
			}
			ArrayMapHolder.loadedChunks.put(chunkX + chunkY * Resources.maxChunks, chunk);
		}
	}
	
	public void setTile(long x, long y, int tile) {
	
		
		long chunkX = x / 16L;
		long chunkY = y / 16L;
		
		
		
		if (x < 0)
			chunkX -= 1;
		if (y < 0)
			chunkY -= 1;
		
		
		int j = (int)(x % 16L);
		int k = (int)(y % 16L);
		
		if (j < 0)
			j += 16;
		if (k < 0)
			k += 16;
		
		if (ArrayMapHolder.loadedChunks.containsKey(chunkX + chunkY * Resources.maxChunks) == true) {
			Chunk chunk = ArrayMapHolder.loadedChunks.get(chunkX + chunkY * Resources.maxChunks);

			if (chunk != null)
				chunk.setTile(j, k, tile, this);
			else
			{
				chunk = genChunk(chunkX, chunkY);
				if (chunk != null)
					chunk.setTile(j, k, tile, this);
				ArrayMapHolder.loadedChunks.put(chunkX + chunkY * Resources.maxChunks, chunk);
			}
		} else {
			Chunk chunk = genChunk(chunkX, chunkY);
			if (chunk != null) {
				chunk.setTile(j, k, tile, this);
				System.out.println("true");
			}
			ArrayMapHolder.loadedChunks.put(chunkX + chunkY * Resources.maxChunks, chunk);
		}
	}

	public long getSeed() {
		return seed;
	}

	public void setSeed(long seed) {
		this.seed = seed;
	}

	public Tile getTileStateBG(long x, long y) {
		
		return Tile.getTileStateFromId(getTileBG(x, y));
	}
	
	public Tile getTileStateBG(int x, int y, long cx, long cy) {
		long chunkX = (long)x + cx * 16L;
		long chunkY = (long)y + cy * 16L;
		
		
		
		return getTileStateBG(chunkX, chunkY);
	}
	
}

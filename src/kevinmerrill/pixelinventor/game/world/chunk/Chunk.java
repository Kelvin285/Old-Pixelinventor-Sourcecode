package kevinmerrill.pixelinventor.game.world.chunk;

import imported.ImprovedNoise;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.InetAddress;
import java.util.Random;

import kevinmerrill.pixelinventor.game.Cam;
import kevinmerrill.pixelinventor.game.PixelInventor;
import kevinmerrill.pixelinventor.game.textures.LoadedTextures;
import kevinmerrill.pixelinventor.game.tile.Tile;
import kevinmerrill.pixelinventor.game.tile.Tiles;
import kevinmerrill.pixelinventor.game.world.World;
import kevinmerrill.pixelinventor.game.world.biome.BiomeSurface;
import kevinmerrill.pixelinventor.game.world.gen.IWorldGenerator;
import kevinmerrill.pixelinventor.multiplayer.server.NetworkMain;
import kevinmerrill.pixelinventor.multiplayer.server.NetworkServer;
import kevinmerrill.pixelinventor.resources.Resources;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Chunk implements Serializable {
	private static final long serialVersionUID = 1L;
	public long chunkX;
	public long chunkY;
	
	private BiomeSurface biome;
	
	private IWorldGenerator worldGenerator;
	
	private boolean loaded = false;
	
	public boolean loaded2 = false;
	
	public int[] tiles;
	public int[] bgTiles;
	
	public double[] tileOffset = new double[16 * 16];
	
	public int[] waterPressure;
	
	public final int maxWater = 16;
	
	public float[] light;
	
	public Chunk(long chunkX, long chunkY, BiomeSurface biome, IWorldGenerator worldGenerator) {
		this.chunkX = chunkX;
		this.chunkY = chunkY;
		this.biome = biome;
		this.worldGenerator = worldGenerator;
		waterPressure = new int[16 * 16];
		light = new float[16 * 16];
		bgTiles = new int[16 * 16];
		setTiles(new int[16 * 16]);
	}
	
	public void update(World worldIn) {
//		if (loaded == false)
//			return;
		
		long offsX = -Resources.maxChunks / 2;
		long offsY = -Resources.maxChunks / 2;
		long delay = 25L;
		
		if (System.currentTimeMillis() / delay > lastTime + 1) {
			lastTime = System.currentTimeMillis() / delay;
			for (int x = 0; x < 16; x++) {
				for (int y = 0; y < 16; y++) {
					
					
					
					//Water
					
					int X = (int) (x + ((chunkX)) * 16);
					int Y = (int) (y + ((chunkY)) * 16);
					
					
					if (waterPressure != null) {
						
						if (waterPressure[x + y * 16] > 0) {							
							
							
							if (worldIn.getTile(X, Y - 1) == Tiles.Air.getId()) {
								
								if (worldIn.getWater(X, Y - 1) + waterPressure[x + y * 16] <= 16) {
									
									worldIn.setWater(X, Y - 1, worldIn.getWater(X, Y - 1) + waterPressure[x + y * 16]);
									waterPressure[x + y * 16] = 0;
								} else {
									
									if (worldIn.getWater(X, Y - 1) + 1 <= 16) {
										worldIn.setWater(X, Y - 1, worldIn.getWater(X, Y - 1) + 1);
										waterPressure[x + y * 16] -= 1;
									}
									
									
									waterFlow(x, y, X, Y, worldIn);
								}
							} else {
								waterFlow(x, y, X, Y, worldIn);
							}
						}
					} else {
						waterPressure = new int[16*16];
					}
					
					//End water
					
					
					
					if (Tile.getTileStateFromId(getTile(x, y)) == null)
						continue;
					Tile.getTileStateFromId(getTile(x, y)).tick(worldIn, x + ((chunkX + offsX) * 16), y + ((chunkY + offsY) * 16));
				}
			}
		}
	}
	
	long lastTime;
	
	
	public void renderBG(SpriteBatch batch, World worldIn) {
		
		if (loaded == false)
			return;
		if (light == null)
			light = new float[16*16];
		if (bgTiles == null) {
			bgTiles = new int[16*16];
		}
		long offsX = -Resources.maxChunks / 2;
		long offsY = -Resources.maxChunks / 2;
		for (int x = 0; x < 16; x++) {
			for (int y = 0; y < 16; y++) {
				
				
				
				
				if (Tile.getTileStateFromId(getTileBG(x, y)) != null)
				{
					
					int shape1 = -1;
					if (Tile.getTileStateFromId(getTile(x, y)) != null) {
						
						int shape = Tile.updateShapeFromConnections(worldIn.getTileState(x, y + 1, chunkX, chunkY).canShape(), worldIn.getTileState(x, y - 1, chunkX, chunkY).canShape(), worldIn.getTileState(x - 1, y, chunkX, chunkY).canShape(), worldIn.getTileState(x + 1, y, chunkX, chunkY).canShape());
						if (shape == 15) {
							continue;
						} else {
							shape1 = shape;
						}
						if (Tile.getTileStateFromId(tiles[x + y * 16]).renderBack(x + chunkX * 16, y + chunkY * 16) == true) {
							double tileOffset = this.tileOffset[x + y * 16];
							batch.setColor(0.75f * ((float)light[x + y * 16] / 16f), 0.75f * ((float)light[x + y * 16] / 16f), 0.75f * ((float)light[x + y * 16] / 16f), 1);
							Tile.getTileStateFromId(getTile(x, y)).render(batch, (x + ((chunkX + offsX) * 16)) * Resources.TILESIZE, (y + ((chunkY + offsY) * 16)) * Resources.TILESIZE, shape1, (float)tileOffset);
						}
					}
					
					batch.setColor(0.5f * ((float)light[x + y * 16] / 16f), 0.5f * ((float)light[x + y * 16] / 16f), 0.5f * ((float)light[x + y * 16] / 16f), 1);
					int shape = Tile.updateShapeFromConnections(worldIn.getTileStateBG(x, y + 1, chunkX, chunkY).canShape(), worldIn.getTileStateBG(x, y - 1, chunkX, chunkY).canShape(), worldIn.getTileStateBG(x - 1, y, chunkX, chunkY).canShape(), worldIn.getTileStateBG(x + 1, y, chunkX, chunkY).canShape());
					
//					if (shape != shape1) {
						Tile.getTileStateFromId(getTileBG(x, y)).render(batch, (x + ((chunkX + offsX) * 16)) * Resources.TILESIZE, (y + ((chunkY + offsY) * 16)) * Resources.TILESIZE, shape);
//					}
					

				}
				int X = (int) (x + ((chunkX)) * 16);
				int Y = (int) (y + ((chunkY)) * 16);
				if (waterPressure != null) {
					if (waterPressure[x + y * 16] > 0) {
						
						
						if (worldIn.getWater(X, Y + 1) == 0) {
							batch.setColor(1 * ((float)light[x + y * 16] / 16f), ((float)Math.abs(ImprovedNoise.noise(X / 5d, System.currentTimeMillis() / 1000d, Y / 5d)) / 1.2f) * ((float)light[x + y * 16] / 16f), 1 * ((float)light[x + y * 16] / 16f), 0.5f + 0.5f * (1 - (float)light[x + y * 16] / 16f));
							batch.draw(LoadedTextures.LOADED.getTextureRegion("WaterTexture")[0][0], (x + ((chunkX + offsX)) * 16) * Resources.TILESIZE - Cam.x, (y + ((chunkY + offsY) * 16)) * Resources.TILESIZE - Cam.y, Resources.TILESIZE, Resources.TILESIZE * (float)((float)waterPressure[x + y * 16] / 16f) + (float)ImprovedNoise.noise(X / 5d, System.currentTimeMillis() / 1000d, System.currentTimeMillis() / 10000d) * 5);

							
							batch.setColor(1 * ((float)light[x + y * 16] / 16f), 1 * ((float)light[x + y * 16] / 16f), 1 * ((float)light[x + y * 16] / 16f), 1f - (float)Math.abs(ImprovedNoise.noise(X / 5d, System.currentTimeMillis() / 1000d, Y / 5d)));
							batch.draw(LoadedTextures.LOADED.getTextureRegion("WaterTexture")[0][1], (x + ((chunkX + offsX)) * 16) * Resources.TILESIZE - Cam.x, Resources.TILESIZE * (float)((float)(waterPressure[x + y * 16] + 1) / 16f) - 2 + ((y) + ((chunkY + offsY) * 16)) * Resources.TILESIZE - Cam.y, Resources.TILESIZE, Resources.TILESIZE * (float)(1f / 16f) + (float)ImprovedNoise.noise(X / 5d, System.currentTimeMillis() / 1000d, System.currentTimeMillis() / 10000d) * 5);
							batch.setColor(1, 1, 1, 1);
							
						} 
						else {
							batch.setColor(1 * ((float)light[x + y * 16] / 16f), ((float)Math.abs(ImprovedNoise.noise(X / 5d, System.currentTimeMillis() / 1000d, Y / 5d)) / 1.2f) * ((float)light[x + y * 16] / 16f), 1 * ((float)light[x + y * 16] / 16f), 0.5f + 0.5f * (1 - (float)light[x + y * 16] / 16f));
							batch.draw(LoadedTextures.LOADED.getTextureRegion("WaterTexture")[0][0], (x + ((chunkX + offsX)) * 16) * Resources.TILESIZE - Cam.x, (y + ((chunkY + offsY) * 16)) * Resources.TILESIZE - Cam.y, Resources.TILESIZE, Resources.TILESIZE * (float)((float)waterPressure[x + y * 16] / 16f));

							batch.setColor(1, 1, 1, 1);
							
						}
						
					}
				} else {
					waterPressure = new int[16*16];
				}
				
			}
		}
	}
	
	public void render(SpriteBatch batch, World worldIn) {
		
		if (loaded == false)
			return;
		
		if (light == null)
			light = new float[16*16];
		
		if (tileOffset == null)
			tileOffset = new double[16*16];
		
		long offsX = -Resources.maxChunks / 2;
		long offsY = -Resources.maxChunks / 2;
		
		for (int x = 0; x < 16; x++) {
			
			for (int y = 0; y < 16; y++) {
				int X = (int) (x + ((chunkX)) * 16);
				int Y = (int) (y + ((chunkY)) * 16);
				
				tileOffset(x, y, X, Y, worldIn);
//				if (false)
				
				
				
				
				
//				if (false)
//				{
				if (Tile.getTileStateFromId(getTile(x, y)) == null)
					continue;
				
				
				if (waterPressure[x + y * 16] == 0)
				if (worldIn.getWater(X, Y + 1) > 0 && tiles[x + y * 16] != Tiles.Air.getId()) {
					batch.setColor(1 * ((float)light[x + y * 16] / 16f), ((float)Math.abs(ImprovedNoise.noise(X / 5d, System.currentTimeMillis() / 1000d, (Y) / 5d)) / 1.2f) * ((float)light[x + y * 16] / 16f), 1 * ((float)light[x + y * 16] / 16f), 0.5f + 0.5f * (1 - (float)light[x + y * 16] / 16f));
					
					batch.draw(LoadedTextures.LOADED.getTextureRegion("WaterTexture")[0][0], ((x) + ((chunkX + offsX)) * 16) * Resources.TILESIZE - Cam.x, (float)tileOffset[x + y * 16] + ((y) + ((chunkY + offsY) * 16)) * Resources.TILESIZE - Cam.y, Resources.TILESIZE, Resources.TILESIZE * (float)((float)16f / 16f));

					batch.setColor(1, 1, 1, 1);
				}
				else
				if (worldIn.getWater(X + 1, Y) > 0 && tiles[x + y * 16] != Tiles.Air.getId()) {
					batch.setColor(1 * ((float)light[x + y * 16] / 16f), ((float)Math.abs(ImprovedNoise.noise(X / 5d, System.currentTimeMillis() / 1000d, (Y) / 5d)) / 1.2f) * ((float)light[x + y * 16] / 16f), 1 * ((float)light[x + y * 16] / 16f), 0.5f + 0.5f * (1 - (float)light[x + y * 16] / 16f));
					
					batch.draw(LoadedTextures.LOADED.getTextureRegion("WaterTexture")[0][0], ((x) + ((chunkX + offsX)) * 16) * Resources.TILESIZE - Cam.x, (float)tileOffset[x + y * 16] + ((y) + ((chunkY + offsY) * 16)) * Resources.TILESIZE - Cam.y, Resources.TILESIZE, Resources.TILESIZE * (float)((float)worldIn.getWater(X + 1, Y) / 16f));

					batch.setColor(1, 1, 1, 1);
				}
				
				else if (worldIn.getWater(X - 1, Y) > 0 && tiles[x + y * 16] != Tiles.Air.getId()) {
					batch.setColor(1 * ((float)light[x + y * 16] / 16f), ((float)Math.abs(ImprovedNoise.noise(X / 5d, System.currentTimeMillis() / 1000d, (Y) / 5d)) / 1.2f) * ((float)light[x + y * 16] / 16f), 1 * ((float)light[x + y * 16] / 16f), 0.5f + 0.5f * (1 - (float)light[x + y * 16] / 16f));
					
					batch.draw(LoadedTextures.LOADED.getTextureRegion("WaterTexture")[0][0], ((x) + ((chunkX + offsX)) * 16) * Resources.TILESIZE - Cam.x, (float)tileOffset[x + y * 16] + ((y) + ((chunkY + offsY) * 16)) * Resources.TILESIZE - Cam.y, Resources.TILESIZE, Resources.TILESIZE * (float)((float)worldIn.getWater(X - 1, Y) / 16f));

					batch.setColor(1, 1, 1, 1);
				}
				
				int shape = Tile.updateShapeFromConnections(worldIn.getTileState(x, y + 1, chunkX, chunkY).canShape(), worldIn.getTileState(x, y - 1, chunkX, chunkY).canShape(), worldIn.getTileState(x - 1, y, chunkX, chunkY).canShape(), worldIn.getTileState(x + 1, y, chunkX, chunkY).canShape());
				

				batch.setColor(1 * ((float)light[x + y * 16] / 16f), 1 * ((float)light[x + y * 16] / 16f), 1 * ((float)light[x + y * 16] / 16f), 1);
				
				if (Tile.getTileStateFromId(tiles[x + y * 16]).renderBack(x + chunkX * 16, y + chunkY * 16) == false)
				Tile.getTileStateFromId(getTile(x, y)).render(batch, (x + ((chunkX + offsX) * 16)) * Resources.TILESIZE, (y + ((chunkY + offsY) * 16)) * Resources.TILESIZE, shape, (float)tileOffset[x + y * 16]);

				
				
				
				
				batch.setColor(1, 1, 1, 1);
//				}
				
				if (worldIn.hasSpawnedPlayer == false) {
					if (y + 1 < 16) {
						
						
						if (this.waterPressure[8 + y * 16] > 5 && Tile.getTileStateFromId(tiles[x]).isSolid() == false && this.waterPressure[8] >= 15 && this.waterPressure[8 + (y + 1) * 16] == 0) {
							
							if (PixelInventor.player != null) {
								PixelInventor.player.posY = ((chunkY - Resources.maxChunks / 2) * 16 * 32 + (y + 1) * 16) + Resources.TILESIZE * 4;
								PixelInventor.player.posX = ((chunkX - Resources.maxChunks / 2) * 16 * 32 + (8) * 16) + Resources.TILESIZE * 4;
								PixelInventor.player.fallStart = System.currentTimeMillis();
								worldIn.hasSpawnedPlayer = true;

										if (y - 1 >= 0) {
											for (int b = -5; b < 5; b++) { 
												if (b != -2)
													if (y + 1 < 16)
												this.tiles[8 + b + (y + 1) * 16] = Tiles.Rubber.getId();
												for (int a = 1; a < 5; a++) {
													if (y + a + 1 < 16)
													if (b == -4 || b == 3 || a == 4) {
														this.tiles[8 + b + (y + a) * 16] = Tiles.Fiberglass.getId();
														this.tiles[8 + b + ((y + 1) + a) * 16] = Tiles.Fiberglass.getId();
													}
												}
											}
											
										}
										
									
							}
							
						}
						
						else if (Tile.getTileStateFromId(tiles[8 + y * 16]).isSolid() == true && Tile.getTileStateFromId(tiles[8 + (y + 1) * 16]).isSolid() == false) {
							
							if (PixelInventor.player != null) {
								PixelInventor.player.posY = ((chunkY - Resources.maxChunks / 2) * 16 * 32 + (y + 1) * 16) + Resources.TILESIZE * 5;
								PixelInventor.player.posX = ((chunkX - Resources.maxChunks / 2) * 16 * 32 + (8) * 16) + Resources.TILESIZE * 4;
								PixelInventor.player.fallStart = System.currentTimeMillis();
								worldIn.hasSpawnedPlayer = true;
								if (y - 1 >= 0) {
									for (int b = -5; b < 5; b++) { 
										this.tiles[8 + b + (y) * 16] = Tiles.Stone.getId();
										for (int a = 1; a < 5; a++) {
											if (y + a < 16)
												if(a > 2)
											if (b == -4 || b == 3 || a == 4) {
												this.tiles[8 + b + (y + a) * 16] = Tiles.Wood.getId();
											}
										}
									}
									
								}
							}
						}
						
						
						
					}
					
				}
			}
		}
		
//		if (true)
//			return;
		long delay = 25L;
		
		int lightingSpeed = 80;
		
		if (System.currentTimeMillis() / delay > lastTime + 1) {
			lastTime = System.currentTimeMillis() / delay;
			for (int x = 0; x < 16; x++) {
				for (int y = 0; y < 16; y++) {
					
					//Water
					
					int X = (int) (x + ((chunkX)) * 16);
					int Y = (int) (y + ((chunkY)) * 16);
					
					if (worldIn.random.nextInt(100) <= lightingSpeed) {
						if (light != null) {
							
							
							//
							if (tiles[x + y * 16] != Tiles.Air.getId() || waterPressure[x + y * 16] > 0) {
								if (worldIn.getTile(X, Y + 1) == Tiles.Air.getId()) {
									if (worldIn.getWater(X, Y + 1) == 0 && worldIn.getTileBG(X, Y + 1) == Tiles.Air.getId()) {
										light[x + y * 16] = 16;
									} else {
										float abs = (worldIn.getLight(X - 1, Y) + worldIn.getLight(X + 1, Y) + worldIn.getLight(X, Y - 1) + worldIn.getLight(X, Y + 1)) / 4;
										light[x + y * 16] = (abs * 0.999f);
										
										if (light[x + y * 16] < 0)
											light[x + y * 16] = 0;
									}
								} else {
									if (worldIn.getTile(X, Y + 1) != Tiles.Air.getId() || worldIn.getTileBG(X, Y + 1) != Tiles.Air.getId()) {
										float abs = (worldIn.getLight(X - 1, Y) + worldIn.getLight(X + 1, Y) + worldIn.getLight(X, Y - 1) + worldIn.getLight(X, Y + 1)) / 4;
										light[x + y * 16] = (abs * 0.9f);
										
										if (light[x + y * 16] < 0)
											light[x + y * 16] = 0;
									} else {
										if (worldIn.getWater(X, Y + 1) == 0 && worldIn.getTileBG(X, Y + 1) == Tiles.Air.getId()) {
											light[x + y * 16] = 16;
										} else {
											float abs = (worldIn.getLight(X - 1, Y) + worldIn.getLight(X + 1, Y) + worldIn.getLight(X, Y - 1) + worldIn.getLight(X, Y + 1)) / 4;
											light[x + y * 16] = (abs * 0.999f);
											
											if (light[x + y * 16] < 0)
												light[x + y * 16] = 0;
										}
									}
									
								}
							} else {
								light[x + y * 16] = 16;
							}
							for (int a = -1; a < 2; a++) 
							{
								for (int b = -1; b < 2; b++) 
								{
									if (a != 0 && b != 0) {
										if (worldIn.getTile(X + a, Y + b) == Tiles.Air.getId() && worldIn.getTileBG(X + a, Y + b) == Tiles.Air.getId() && worldIn.getWater(X + a, Y + b) == 0) {
											light[x + y * 16] = 16;
										}
									}
								}
							}
							//
							
						}
					}
					
					if (waterPressure != null) {
												
						if (waterPressure[x + y * 16] > 0) {							
							
							
							if (worldIn.getTile(X, Y - 1) == Tiles.Air.getId()) {
								
								if (worldIn.getWater(X, Y - 1) + waterPressure[x + y * 16] <= 16) {
									
									worldIn.setWater(X, Y - 1, worldIn.getWater(X, Y - 1) + waterPressure[x + y * 16]);
									waterPressure[x + y * 16] = 0;
								} else {
									
									if (worldIn.getWater(X, Y - 1) + 1 <= 16) {
										worldIn.setWater(X, Y - 1, worldIn.getWater(X, Y - 1) + 1);
										waterPressure[x + y * 16] -= 1;
									}
									
									
									waterFlow(x, y, X, Y, worldIn);
								}
							} else {
								waterFlow(x, y, X, Y, worldIn);
							}
						}
					} else {
						waterPressure = new int[16*16];
					}
					
					//End water
					
					
					
					if (Tile.getTileStateFromId(getTile(x, y)) == null)
						continue;
					Tile.getTileStateFromId(getTile(x, y)).tick(worldIn, x + ((chunkX + offsX) * 16), y + ((chunkY + offsY) * 16));
				}
			}
		}
		
		
	}
	
	public void tileOffset(int x, int y, int X, int Y, World worldIn) {
		
		
		
		if (worldIn.getTile(X, Y - 1) != Tiles.Air.getId() && worldIn.getWater(X, Y - 1) != 16) {
			tileOffset[x + y * 16] = worldIn.getTileOffset(X, Y - 1);
		} else {
			if (waterPressure[x + y * 16] == 16 || worldIn.getWater(X, Y - 1) == 16) {
				tileOffset[x + y * 16] = ImprovedNoise.noise(X / 5d, System.currentTimeMillis() / 1000d, System.currentTimeMillis() / 10000d) * 5;
			} else {
				float offset = 0;
				int i = 0;
				for (int xx = -1; xx < 2; xx++) {
					for (int yy = -1; yy < 2; yy++) {
						if (worldIn.getTile(X + xx, Y + yy) != Tiles.Air.getId()) {
							offset += worldIn.getTileOffset(X + xx, Y + yy);
							i++;
						}
						
					}
				}
				i += 1;
				if (i != 0)
				tileOffset[x + y * 16] = offset / (float)i;
			}
		}
		
		if (worldIn.getTile(X, Y - 1) != Tiles.Air.getId()) {
			tileOffset[x + y * 16] = worldIn.getTileOffset(X, Y - 1);
		}
		
			
			
		
	}
	
	public Tile getTileState(int x, int y) {
		return Tile.getTileStateFromId(getTile(x, y));
	}
	
	public void waterFlow(int x, int y, int X, int Y, World worldIn) {
		if (worldIn.getTile(X - 1, Y) == Tiles.Air.getId() && worldIn.getTile(X + 1, Y) == Tiles.Air.getId()) {
			if (worldIn.getWater(X - 1, Y) <= 8 && worldIn.getWater(X + 1, Y) <= 8 || waterPressure[x + y * 16] > (worldIn.getWater(X + 1, Y) + worldIn.getWater(X - 1, Y) / 2))
			if (new Random().nextInt(1600) < 10) {
				waterPressure[x + y * 16] -= 1;
			}
		}
		if (worldIn.getTile(X - 1, Y) == Tiles.Air.getId() && worldIn.getTile(X + 1, Y) == Tiles.Air.getId()) {
			if (worldIn.getWater(X - 1, Y) + 1 < worldIn.getWater(X + 1, Y) + 1) {
				if (worldIn.getWater(X - 1, Y) + 1 <= 16 && worldIn.getWater(X - 1, Y) + 1 < waterPressure[x + y * 16]) {
					worldIn.setWater(X - 1, Y, worldIn.getWater(X - 1, Y) + 1);
					waterPressure[x + y * 16] -= 1;
				} else {
					if (worldIn.getWater(X + 1, Y) + 1 <= 16 && worldIn.getWater(X + 1, Y) + 1 < waterPressure[x + y * 16]) {
						worldIn.setWater(X + 1, Y, worldIn.getWater(X + 1, Y) + 1);
						waterPressure[x + y * 16] -= 1;
					}
				}
			} else
			if (worldIn.getWater(X - 1, Y) + 1 > worldIn.getWater(X + 1, Y) + 1) {
				if (worldIn.getWater(X + 1, Y) + 1 <= 16 && worldIn.getWater(X + 1, Y) + 1 < waterPressure[x + y * 16]) {
					worldIn.setWater(X + 1, Y, worldIn.getWater(X + 1, Y) + 1);
					waterPressure[x + y * 16] -= 1;
				} else {
					if (worldIn.getWater(X - 1, Y) + 1 <= 16 && worldIn.getWater(X - 1, Y) + 1 < waterPressure[x + y * 16]) {
						worldIn.setWater(X - 1, Y, worldIn.getWater(X - 1, Y) + 1);
						waterPressure[x + y * 16] -= 1;
					}
				}
			} else {
				if (worldIn.getWater(X + 1, Y) + 1 <= 16 && worldIn.getWater(X + 1, Y) + 1 < waterPressure[x + y * 16]) {
					worldIn.setWater(X + 1, Y, worldIn.getWater(X + 1, Y) + 1);
					waterPressure[x + y * 16] -= 1;
				}
				if (worldIn.getWater(X - 1, Y) + 1 <= 16 && worldIn.getWater(X - 1, Y) + 1 < waterPressure[x + y * 16]) {
					worldIn.setWater(X - 1, Y, worldIn.getWater(X - 1, Y) + 1);
					waterPressure[x + y * 16] -= 1;
				}
			}
		} else {
			if (worldIn.getTile(X - 1, Y) == Tiles.Air.getId()) {
				if (worldIn.getWater(X - 1, Y) + 1 <= 16 && worldIn.getWater(X - 1, Y) + 1 < waterPressure[x + y * 16]) {
					worldIn.setWater(X - 1, Y, worldIn.getWater(X - 1, Y) + 1);
					waterPressure[x + y * 16] -= 1;
				}
			}
			if (worldIn.getTile(X + 1, Y) == Tiles.Air.getId()) {
				if (worldIn.getWater(X + 1, Y) + 1 <= 16 && worldIn.getWater(X + 1, Y) + 1 < waterPressure[x + y * 16]) {
					worldIn.setWater(X + 1, Y, worldIn.getWater(X + 1, Y) + 1);
					waterPressure[x + y * 16] -= 1;
				}
			}
		}
	}
	
	public void setLight(int x, int y, float level) {
		
		if (x >= 0 && y >= 0 && x < 16 && y < 16)
		light[(x % 16) + (y % 16) * 16] = level;
		setLoaded(true);
	}
	
	public float getLight(int x, int y) {
		setLoaded(true);
		if (x >= 0 && y >= 0 && x < 16 && y < 16)
		return light[(x % 16) + (y % 16) * 16];
		return 0;
	}
	
	public void setWater(int x, int y, int pressure) {
		
		if (x >= 0 && y >= 0 && x < 16 && y < 16)
		waterPressure[(x % 16) + (y % 16) * 16] = pressure;
		setLoaded(true);
	}
	
	public int getWater(int x, int y) {
		setLoaded(true);
		if (waterPressure == null)
			waterPressure = new int[16*16];
		if (x >= 0 && y >= 0 && x < 16 && y < 16)
		return waterPressure[(x % 16) + (y % 16) * 16];
		return 0;
	}
	
	public int getTileBG(int x, int y) {
		setLoaded(true);
		if (x >= 0 && y >= 0 && x < 16 && y < 16)
		return bgTiles[(x % 16) + (y % 16) * 16];
		return 0;
	}
	
	public void setTileBG(int x, int y, int id, World worldIn) {
		
		if (x >= 0 && y >= 0 && x < 16 && y < 16)
			bgTiles[(x % 16) + (y % 16) * 16] = id;
//		if (worldIn.isRemote() == true && PixelInventor.client != null) {
//			PixelInventor.client.sendData(("tilemod\n"+(x%16)+"\n"+(y%16)+"\n"+id+"\n"+chunkX+"\n"+chunkY).getBytes());
//		}
		setLoaded(true);
	}
	
	public double getTileOffset(int x, int y) {
		setLoaded(true);
		if (tileOffset==null)
			tileOffset = new double[16*16];
		if (x >= 0 && y >= 0 && x < 16 && y < 16)
		return tileOffset[(x % 16) + (y % 16) * 16];
		return 0;
	}
	
	public int getTile(int x, int y) {
		setLoaded(true);
		if (x >= 0 && y >= 0 && x < 16 && y < 16)
		return getTiles()[(x % 16) + (y % 16) * 16];
		return 0;
	}
	
	public void setTile(int x, int y, int id, World worldIn) {
		
		if (x >= 0 && y >= 0 && x < 16 && y < 16)
		getTiles()[(x % 16) + (y % 16) * 16] = id;
//		if (worldIn.isRemote() == true && PixelInventor.client != null) {
//			PixelInventor.client.sendData(("tilemod\n"+(x%16)+"\n"+(y%16)+"\n"+id+"\n"+chunkX+"\n"+chunkY).getBytes());
//		}
		setLoaded(true);
	}

	public long getChunkX() {
		return chunkX;
	}

	public void setChunkX(long chunkX) {
		this.chunkX = chunkX;
	}

	public long getChunkY() {
		return chunkY;
	}

	public void setChunkY(long chunkY) {
		this.chunkY = chunkY;
	}

	public BiomeSurface getBiome() {
		return biome;
	}

	public void setBiome(BiomeSurface biome) {
		this.biome = biome;
	}

	public IWorldGenerator getWorldGenerator() {
		return worldGenerator;
	}

	public void setWorldGenerator(IWorldGenerator worldGenerator) {
		this.worldGenerator = worldGenerator;
	}

	public boolean isLoaded() {
		return loaded;
	}

	public void setLoaded(boolean loaded) {
		this.loaded = loaded;
	}

	public static void saveSeed(long seed, boolean remote) {
		String name;
		if (remote == true)
			name = NetworkServer.currentWorldName;
		else
			name = Resources.currentWorldName;
		File file = new File("saves/worlds/"+name+"/seed.txt");
		if (!file.exists())
			try {
				file.createNewFile();
			}catch (Exception e){
				e.printStackTrace();
			}
		FileOutputStream fos;
		ObjectOutputStream out;
		try {
			fos = new FileOutputStream(file);
			out = new ObjectOutputStream(fos);
			out.writeLong(seed);
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static long loadSeed(boolean remote) {
		String name;
		if (remote == true)
			name = NetworkServer.currentWorldName;
		else
			name = Resources.currentWorldName;
		File file = new File("saves/worlds/"+name+"/seed.txt");
		if (!file.exists())
			return new Random().nextLong();
		long c = 0;
		FileInputStream fis;
		ObjectInputStream in;
		try {
			fis = new FileInputStream(file);
			in = new ObjectInputStream(fis);
			c = in.readLong();
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return c;
	}
	
	
	public void saveChunk(boolean remote) {
		String name;
		if (remote == true)
			name = NetworkServer.currentWorldName;
		else
			name = Resources.currentWorldName;
		File file = new File("saves/worlds/"+name+"/chunk"+chunkX+"."+chunkY+".ch");
		if (!file.exists())
			try {
				File file2 = new File("saves/worlds/"+name+"/");
				file2.mkdirs();
				file.createNewFile();
			}catch (Exception e){
				e.printStackTrace();
			}
		FileOutputStream fos;
		ObjectOutputStream out;
		try {
			fos = new FileOutputStream(file);
			out = new ObjectOutputStream(fos);
			out.writeObject(this);
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		 
	}
	
	public static Chunk loadChunk(long chunkX, long chunkY, boolean remote) {
		String name;
		if (remote == true)
			name = NetworkServer.currentWorldName;
		else
			name = Resources.currentWorldName;
		File file = new File("saves/worlds/"+name+"/chunk"+chunkX+"."+chunkY+".ch");
		if (file.exists() == false)
			return null;
		Chunk c = null;
		FileInputStream fis;
		ObjectInputStream in;
		try {
			fis = new FileInputStream(file);
			in = new ObjectInputStream(fis);
			c = (Chunk)in.readObject();
			in.close();
			
		} catch (Exception e) {
//			e.printStackTrace();
		}
		return c;
	}

	public void sendServerData(InetAddress address, int port) {
//		("tilemod\n"+mouseX+"\n"+mouseY+"\n"+Tiles.Air.getId()).getBytes()
		if (NetworkMain.server != null) {
			for (int x = 0; x < 16; x++) {
				for (int y = 0; y < 16; y++) {
					byte[] data = ("tilemod\n"+(x + chunkX * 16)+"\n"+(y + chunkY * 16)+"\n"+Tiles.Air.getId()).getBytes();

					NetworkMain.server.sendData(data, address, port);
				}
				
			}
		}
	}

	public int[] getTiles() {
		return tiles;
	}

	public void setTiles(int[] tiles) {
		this.tiles = tiles;
	}

	public static World loadWorld(boolean remote) {
		World world = null;
		String name;
		if (remote == true)
			name = NetworkServer.currentWorldName;
		else
			name = Resources.currentWorldName;
		if (name == null) {
			return world;
		}
		if (name.isEmpty() == true) {
			return world;
		}
		File file = new File("saves/worlds/"+name+"/"+name+".world");
		if (!file.exists())
			try {
				File file2 = new File("saves/worlds/"+name+"/");
				file2.mkdirs();
				file.createNewFile();
			}catch (Exception e){
				e.printStackTrace();
			}
		FileInputStream fos;
		ObjectInputStream out;
		try {
			fos = new FileInputStream(file);
			out = new ObjectInputStream(fos);
			world = (World)out.readObject();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return world;
	}
	
	public static void saveWorld(World world, boolean remote) {
		String name;
		if (remote == true)
			name = NetworkServer.currentWorldName;
		else
			name = Resources.currentWorldName;
		
		if (name == null) {
			return;
		}
		if (name.isEmpty() == true) {
			return;
		}
		File file = new File("saves/worlds/"+name+"/"+name+".world");
		if (!file.exists())
			try {
				File file2 = new File("saves/worlds/"+name+"/");
				file2.mkdirs();
				file.createNewFile();
			}catch (Exception e){
				e.printStackTrace();
			}
		FileOutputStream fos;
		ObjectOutputStream out;
		try {
			fos = new FileOutputStream(file);
			out = new ObjectOutputStream(fos);
			out.writeObject(world);
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}
	
	
}

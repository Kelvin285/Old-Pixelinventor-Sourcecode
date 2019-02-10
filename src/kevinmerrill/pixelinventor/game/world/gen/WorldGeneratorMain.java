package kevinmerrill.pixelinventor.game.world.gen;

import imported.ImprovedNoise;
import kevinmerrill.pixelinventor.game.tile.Tiles;
import kevinmerrill.pixelinventor.game.world.TileQueue;
import kevinmerrill.pixelinventor.game.world.World;
import kevinmerrill.pixelinventor.game.world.biome.BiomeSurface;
import kevinmerrill.pixelinventor.game.world.chunk.Chunk;
import kevinmerrill.pixelinventor.resources.Resources;

public class WorldGeneratorMain implements IWorldGenerator {

	private static final long serialVersionUID = 1L;
	private BiomeSurface defaultBiome;
	
	
	public WorldGeneratorMain(BiomeSurface defaultBiome) {
		this.defaultBiome = defaultBiome;
	}
	
	public void generateChunk(Chunk chunk, World worldIn) {
		
//		System.out.println(temperature);
		
		if (chunk == null)
			return;
		if (chunk.isLoaded() == true && chunk.loaded2 == true) {
			return;
		}
		if (chunk.isLoaded() == true) {
			chunk.loaded2 = true;
		}
		chunk.setBiome(defaultBiome);
//		if (temperature > 0.75f)
//		chunk.setBiome(BiomeSurface.DESERT);
//		if (temperature > 0.5f)
//			chunk.setBiome(BiomeSurface.SAVANNAH);
//		if (temperature < -0.5f)
//			chunk.setBiome(BiomeSurface.SNOW);
		
			for (int x = 0; x < 16; x++) {
				for (int y = 0; y < 16; y++) {
					
					long X = x + chunk.getChunkX() * 16 - (Resources.maxChunks / 2) * 16;
					long Y = y + chunk.getChunkY() * 16 - (Resources.maxChunks / 2) * 16;
					
					double heightMod = 64;
					double distMod = 1d;
					double heightmap = ImprovedNoise.noise((double)X / (distMod * 1000d), 0, worldIn.getSeed() / 999999d) * heightMod;
					
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
					double roughness = ImprovedNoise.noise((double)X / (distMod * 800d), (double)Y / (15d), worldIn.getSeed() / 999999d);
					double detail = ImprovedNoise.noise((double)X / 20d, 0, worldIn.getSeed() / 999999d);
					
					double noise = heightmap + (roughness + interp * (detail - roughness)) * (heightMod / 2d);
					
					double temperature = ImprovedNoise.noise((double)X / (distMod * 800d), 0, worldIn.getSeed() / 999999d);
					
					double forestmap = ImprovedNoise.noise(worldIn.getSeed() / 999999d, (double)X / (distMod * 350d), 0);

					if (Y < noise - 1) {
						if (heightmap > 0) {
							
							chunk.setTile(x, y, Tiles.Grass.getId(), worldIn);
							chunk.setTileBG(x, y, Tiles.Grass.getId(), worldIn);
							
							if (worldIn.random.nextInt(100) <= 75) {
								chunk.setTile(x, y + 1, Tiles.Tallgrass_Green.getId(), worldIn);
							}
							
							if (Y < noise - 3) {
								chunk.setTile(x, y, Tiles.Dirt.getId(), worldIn);
								chunk.setTileBG(x, y, Tiles.Dirt.getId(), worldIn);
							} else {
								
								if (temperature < -0.5f) {
									if (worldIn.random.nextInt(100) <= 90) {
										if (worldIn.random.nextInt(100) <= 30 + forestmap * 25) {
											chunk.setTile(x, y + 1, Tiles.Spruce.getId(), worldIn);
										}
										
									} else {
										if (worldIn.random.nextInt(100) <= 15 + forestmap * 25) {
											chunk.setTile(x, y + 1, Tiles.Spruce.getId(), worldIn);
										}
									}
								} else {
									if (worldIn.random.nextInt(100) <= 15 + forestmap * 25) {
										chunk.setTile(x, y + 1, Tiles.Broadleaf.getId(), worldIn);
										if (worldIn.random.nextBoolean() == true) {
											chunk.setTile(x, y + 1, Tiles.Broadleaf_2.getId(), worldIn);
										}
									}
								}
							}
							
							
							if (temperature < -0.8f) {
								chunk.setTile(x, y, Tiles.Snow.getId(), worldIn);
								chunk.setTileBG(x, y, Tiles.Snow.getId(), worldIn);
								if (Y < noise - 3) {
									chunk.setTile(x, y, Tiles.Snow.getId(), worldIn);
									chunk.setTileBG(x, y, Tiles.Snow.getId(), worldIn);
								}
							} else
							if (temperature < -0.5f) {
								chunk.setTile(x, y, Tiles.SnowyGrass.getId(), worldIn);
								chunk.setTileBG(x, y, Tiles.SnowyGrass.getId(), worldIn);
								if (Y < noise - 3) {
									chunk.setTile(x, y, Tiles.Dirt.getId(), worldIn);
									chunk.setTileBG(x, y, Tiles.Dirt.getId(), worldIn);
								}
							}
							
							if (temperature > 0.7) {
								chunk.setTile(x, y, Tiles.Sand.getId(), worldIn);
								chunk.setTileBG(x, y, Tiles.Sand.getId(), worldIn);
								if (worldIn.random.nextInt(100) <= 50) {
									chunk.setTile(x, y + 1, Tiles.Cactus.getId(), worldIn);
								}
								if (Y < noise - 3) {
									chunk.setTile(x, y, Tiles.Sand.getId(), worldIn);
									chunk.setTileBG(x, y, Tiles.Sand.getId(), worldIn);
								}
							} else {
								if (temperature > 0.5) {
									chunk.setTile(x, y, Tiles.DeadGrass.getId(), worldIn);
									chunk.setTileBG(x, y, Tiles.DeadGrass.getId(), worldIn);
									
									if (worldIn.random.nextInt(100) <= 75) {
										chunk.setTile(x, y + 1, Tiles.Tallgrass.getId(), worldIn);
									}
									if (worldIn.random.nextInt(100) <= 10) {
										chunk.setTile(x, y + 1, Tiles.Acacia.getId(), worldIn);
									}
									if (Y < noise - 3) {
										chunk.setTile(x, y, Tiles.Dirt.getId(), worldIn);
										chunk.setTileBG(x, y, Tiles.Dirt.getId(), worldIn);
									}
								}
							}
							
						} else {
							if (temperature > 0.2 || heightmap <= -10) {
								chunk.setTile(x, y, Tiles.Sand.getId(), worldIn);
								chunk.setTileBG(x, y, Tiles.Sand.getId(), worldIn);
								if (worldIn.random.nextInt(100) <= 50) {
									chunk.setTile(x, y + 1, Tiles.Cactus.getId(), worldIn);
								}
								if (Y < noise - 3) {
									chunk.setTile(x, y, Tiles.Sand.getId(), worldIn);
									chunk.setTileBG(x, y, Tiles.Sand.getId(), worldIn);
								}
							} else {
								if (temperature > -0.2) {
									chunk.setTile(x, y, Tiles.DeadGrass.getId(), worldIn);
									chunk.setTileBG(x, y, Tiles.DeadGrass.getId(), worldIn);
									
									if (worldIn.random.nextInt(100) <= 75) {
										chunk.setTile(x, y + 1, Tiles.Tallgrass.getId(), worldIn);
									}
									if (worldIn.random.nextInt(100) <= 10) {
										chunk.setTile(x, y + 1, Tiles.Acacia.getId(), worldIn);
									}
									if (Y < noise - 3) {
										chunk.setTile(x, y, Tiles.Dirt.getId(), worldIn);
										chunk.setTileBG(x, y, Tiles.Dirt.getId(), worldIn);
									}
								} else {
									chunk.setTile(x, y, Tiles.Grass.getId(), worldIn);
									chunk.setTileBG(x, y, Tiles.Grass.getId(), worldIn);
									if (Y < noise - 3) {
										chunk.setTile(x, y, Tiles.Dirt.getId(), worldIn);
										chunk.setTileBG(x, y, Tiles.Dirt.getId(), worldIn);
									}
								}
							}
						}
						
						
						if (Y < noise - 8) {
							chunk.setTile(x, y, Tiles.Stone.getId(), worldIn);
							chunk.setTileBG(x, y, Tiles.Stone.getId(), worldIn);
						}
					} else {
//						chunk.setTile(x, y, Tiles.Air.getId(), worldIn);
//						chunk.setTileBG(x, y, Tiles.Air.getId(), worldIn);
						if (Y < -10) {
							chunk.setWater(x, y, 16);
							if (Y < -20)
							chunk.setTileBG(x, y, Tiles.Dirt.getId(), worldIn);
						}
					}
					
					
					
				}
			}
		
		chunk.setLoaded(true);
	}
//	
//	public void generateTree(int X, int Y, int baseTile, World worldIn, Chunk chunk) {
//		
//		if (baseTile == Tiles.Grass.getId() || baseTile == Tiles.DeadGrass.getId() || baseTile == Tiles.SnowyGrass.getId()) {
//			System.out.println("tree");
//			long XX = X + chunk.getChunkX() * 16 - (Resources.maxChunks / 2) * 16;
//			long YY = Y + chunk.getChunkY() * 16 - (Resources.maxChunks / 2) * 16;
//			
//			//trunk
//			
//			//rand((max + 1) - min) + min
//			int width = worldIn.random.nextInt(4 - 3) + 3;
//			int height = worldIn.random.nextInt(12 - 8) + 8 + width;
//			
//			for (int h = 0; h < height; h++) {
//				double widthMul = 1 - (h / height);
//				int newWidth = (int)(width * widthMul);
//				for (int x = -newWidth; x < newWidth; x++) {
//					
//					if (h == height - 1)
//					for (int a = -5; a < 5; a++) {
//						for (int b = -5; b < 5; b++) {
//							if (Math.sqrt(a * a + b * b) <= 5) {
//								chunk.setTileBG(X + x + a, Y + h + b, Tiles.Leaves.getId(), worldIn);
//								worldIn.setTileBG(XX + X + x + a, YY + Y + h + b, Tiles.Leaves.getId());
//								
//							}
//						}
//					}
//					
//					chunk.setTileBG(X + x, Y + h, Tiles.Wood.getId(), worldIn);
//					worldIn.setTileBG(XX + X + x, YY + Y + h, Tiles.Wood.getId());
//					
//					
//					
//					
//					if (h > height - 4) {
//						
//						boolean right = x > 0;
//						
//						int length = worldIn.random.nextInt(4 - 3) + 3;
//						int xx = 0;
//						int yy = 0;
//						
//						
//						
//						for (int i = 0; i < length; i++) {
//							chunk.setTileBG(X + x + xx, Y + h + yy, Tiles.Wood.getId(), worldIn);
//							worldIn.setTileBG(XX + X + xx + x, YY + Y + h + yy, Tiles.Wood.getId());
//							
////							if (worldIn.random.nextBoolean() == true) {
//								if (worldIn.random.nextBoolean() == true) {
//									if (right == true) {
//										xx++;
//									}
//									else
//										xx--;
//									
//								} else {
//									yy += 1;
//								}
////							}
//						}
//					}
//				}
//			}
//		}
//	}
	
}

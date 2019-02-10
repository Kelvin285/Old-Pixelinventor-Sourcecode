package kevinmerrill.pixelinventor.game;

import imported.FrameRate;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import kevinmerrill.pixelinventor.game.entity.player.EntityPlayer;
import kevinmerrill.pixelinventor.game.textures.LoadedAnimations;
import kevinmerrill.pixelinventor.game.textures.LoadedTextures;
import kevinmerrill.pixelinventor.game.tile.Tile;
import kevinmerrill.pixelinventor.game.ui.screen.IScreen;
import kevinmerrill.pixelinventor.game.world.World;
import kevinmerrill.pixelinventor.multiplayer.client.NetworkClient;
import kevinmerrill.pixelinventor.resources.ColorHSVA;
import kevinmerrill.pixelinventor.resources.Input;
import kevinmerrill.pixelinventor.resources.Resources;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.TextInputListener;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector3;

public class PixelInventor extends ApplicationAdapter implements TextInputListener {

	SpriteBatch batch;
	ShapeRenderer shapes;
	Texture titleBackground;
	OrthographicCamera camera;
	public static IScreen currentGuiScreen = IScreen.TITLE;
	float elapsed;
	float elapsed2 = 0;
	long startTime = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
	long seconds = 0;
	int titleFrame = 0;
	public static Music titleMusic;
	public static Music turquoise;
	
	public static NetworkClient client;
	
	public static boolean title = true;
	
	boolean debug = false;
	
	float currentMusicVolume = 0.0f;
	
	public static Vector3 mouse = new Vector3(0, 0, 0);
	
	public static World world;
	
	public static EntityPlayer player;
	public static EntityPlayer basePlayer = new EntityPlayer(0, 0);
	public static String username = "player"+new Random().nextInt(9999);
	
	
	public void create() {
		
		batch = new SpriteBatch();
		shapes = new ShapeRenderer();
		camera = new OrthographicCamera(1920.0f / 2f, 1080.0f / 2f);
		try {
			
			titleBackground = new Texture(Gdx.files.internal("assets/textures/title/titleBG.png"));
		
		}catch (Exception e){}
		PixelInventor.titleMusic = Gdx.audio.newMusic(Gdx.files.internal("assets/music/title.mp3"));
		PixelInventor.titleMusic.setVolume(Resources.soundVolumeMultiplier);
		PixelInventor.turquoise = Gdx.audio.newMusic(Gdx.files.internal("assets/music/turquoise.mp3"));
		PixelInventor.turquoise.setVolume(Resources.soundVolumeMultiplier);
		LoadedTextures.LOADED.load();
		LoadedAnimations.LOADED.load();
		new Resources();
		Tile.loadTiles();
		PixelInventor.framerate = new FrameRate();
		
		init();
		
		
		
		if (debug == true)
			elapsed = 20;
		new Thread() {
			public void run() {
				while (true) {
					try {
//						tick();
						Thread.sleep(5);
					}catch (Exception e){}
				}
			}
		}.start();
	}

	public void init() {
		basePlayer = basePlayer.loadSkin();
	}
	
	public void dispose() {
		if (client != null)
			PixelInventor.client.sendData(("disconnect\n"+PixelInventor.client.username).getBytes());
		if (world != null)
		world.dispose();
		batch.dispose();
		titleMusic.dispose();
		LoadedAnimations.LOADED.dispose();
		LoadedTextures.LOADED.dispose();
		Resources.font.dispose();
		framerate.dispose();
	}

	public void pause() {
		
	}
	
	
	public void tick() {
		

		try {
			if (Resources.inWorld == true) {
				if (PixelInventor.world != null) {
					PixelInventor.world.update();
				}
				
			}
		}catch (Exception e){}
	}
	
	public void updateKeys() {
		Input.rightClick = false;
		Input.leftClick = false;
		Input.isLeftClicked();
		Input.isRightClicked();
//		mouse.x = Gdx.input.getX() + 1920.0f / 4.0f;
//		mouse.y = Gdx.input.getY() - 1080.0f / 4.0f;
		mouse.x = Gdx.input.getX();
		mouse.y = ((1080.0f / 2.0f) - Gdx.input.getY()) + (Gdx.graphics.getHeight() - (1080.0f / 2.0f));
		mouse.x /= (Gdx.graphics.getWidth() / (1920.0f / 2.0f));
		mouse.y /= (Gdx.graphics.getHeight() / (1080.0f / 2.0f));
		
		
//		camera.unproject(mouse);
	}
	
	public static FrameRate framerate;
	
	int p = 0;
	public void render() {
		updateKeys();
		tick();
		framerate.update();
		seconds = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()) - startTime;
		
		boolean drawCharacterStuff = false;
		
		if (title == true) {
			
			if (world == null) {
				
				if (PixelInventor.titleMusic.isPlaying() == false) {
					PixelInventor.titleMusic.play();
					startTime = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
				}
				if (currentMusicVolume < 1.0f) {
					currentMusicVolume += 0.005f;
				}
				PixelInventor.titleMusic.setVolume(currentMusicVolume * Resources.soundVolumeMultiplier);
				PixelInventor.turquoise.stop();
			} else {
				if (player != null) {
					player.shirt_color = basePlayer.shirt_color;
					player.pants_color = basePlayer.pants_color;
					player.eyes_color = basePlayer.eyes_color;
					player.shoes_color = basePlayer.shoes_color;
					player.hair_color = basePlayer.hair_color;
					player.hair_outline_color = basePlayer.hair_outline_color;
					player.skin_color = basePlayer.skin_color;
					player.outline_color = basePlayer.outline_color;
					player.hairType = basePlayer.hairType;
				}
			}
			
			elapsed += Gdx.graphics.getDeltaTime();
			elapsed2 += Gdx.graphics.getDeltaTime() * 0.2f;
			if (elapsed2 > 6*0.025f)
				elapsed2 = 0;
			if (debug == false) {
				if (elapsed >= 15 && elapsed < 20) {
					Gdx.gl.glClearColor((elapsed - 15.0f) / 5.0f,  (elapsed - 15.0f) / 5.0f,  (elapsed - 15.0f) / 5.0f,  1);
				} else {
					Gdx.gl.glClearColor(0,  0,  0,  0);
				}
			} else {
				Gdx.gl.glClearColor(0,  0,  0,  0);
			}
			if (world != null) {
				Gdx.gl.glClearColor(0.4f,  0.7f,  0.9f,  1);
			}
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
			batch.begin();
			
			
			
			
			if (elapsed >= 20) {
				if (Resources.inWorld == false) {
					
					batch.draw(titleBackground, 0.0f, 0.0f, 1920 / 2, 1080 / 2);
					batch.setColor(1.0f, 1.0f, 1.0f, 1.0f);
					batch.draw(LoadedTextures.LOADED.getTexture("pixelinventor"), 1920.0f / 38.0f + 25, 1080.0f / 5.0f + 150 + 10f * (float)Math.sin(Math.toRadians((System.currentTimeMillis() / 10) % 360)));
					if (PixelInventor.currentGuiScreen != null) {
						if (PixelInventor.currentGuiScreen == IScreen.TITLE)
							drawCharacterStuff = true;
						PixelInventor.currentGuiScreen.update(batch);
					}
				} else {
					world.renderBG(batch);
					if (world != null) {
						world.render(batch);
						
						if (client != null) {
							for (int i = 0; i < client.players.size(); i++) {
								client.players.get(i).render(batch);
							}
						}
						
						if (debug == true) {
							Resources.font.draw(batch, "x: " + (long)player.posX / 32 + ", y: " + (long)player.posY / 32, 50, 500);
						}
					}
				}
			} else {
				if (elapsed <= 4 && elapsed > 0) {
					batch.setColor(1.0f, 1.0f, 1.0f, elapsed / 4.0f);
					batch.draw(LoadedTextures.LOADED.getTexture("vfglogo"), 1920.0f / 8.0f + 100, 1080.0f / 6.0f + 50);
				}
				else if (elapsed <= 8 && elapsed > 0) {
					batch.setColor(1.0f, 1.0f, 1.0f, 1.0f - ((elapsed - 4.0f) / 4.0f));
					batch.draw(LoadedTextures.LOADED.getTexture("vfglogo"), 1920.0f / 8.0f + 100, 1080.0f / 6.0f + 50);
				}
				else if (elapsed <= 12 && elapsed > 0) {
					batch.setColor(1.0f, 1.0f, 1.0f, ((elapsed - 8.0f) / 4.0f));
					batch.draw(LoadedTextures.LOADED.getTexture("pixelinventor"), 1920.0f / 38.0f + 25, 1080.0f / 5.0f);
				}
				else if (elapsed <= 20 && elapsed > 0) {
					batch.setColor(1.0f, 1.0f, 1.0f, 1f - ((elapsed - 12.0f) / 8.0f));
					batch.draw(LoadedTextures.LOADED.getTexture("pixelinventor"), 1920.0f / 38.0f + 25, 1080.0f / 5.0f);
				}
			}
			
			/**DRAW INGAME GUI**/
			
			if (Resources.inWorld == true) {
				float healthX = 20;

				batch.setColor(Color.RED);
				batch.draw(LoadedTextures.LOADED.getTexture("rectangle"), healthX + 24,1080/2-11*3 + 2,61*2 - 30,11*2 - 4);
				batch.setColor(Color.GREEN);
				if (player.health > 0)
				batch.draw(LoadedTextures.LOADED.getTexture("rectangle"), healthX + 24,1080/2-11*3 + 2,(61*2 - 30) * (player.health / player.maxHealth),11*2 - 4);
				batch.setColor(Color.WHITE);
				
				Animation healthbar = LoadedAnimations.LOADED.getAnimation("healthbar_anim");
				batch.draw(healthbar.getKeyFrame(((System.currentTimeMillis() / 70)) % (4 * 3)), healthX, 1080/2 - 11 * 3, 61 * 2, 11 * 2);
			}
			
			if (drawCharacterStuff == true) {
				
				
				if (charSelect == false) {
					batch.draw(LoadedTextures.LOADED.getTexture("SelectChar"), 881, 400 - 8, LoadedTextures.LOADED.getTexture("SelectChar").getWidth() * 2, LoadedTextures.LOADED.getTexture("SelectChar").getHeight() * 2 + 8);
					basePlayer.render(batch, 850, 400, 128, 128);
					if (mouse.x > 881 && mouse.x < 881 + LoadedTextures.LOADED.getTexture("SelectChar").getWidth() * 2 &&
							mouse.y > 400 - 8 && mouse.y < 400 - 8 + LoadedTextures.LOADED.getTexture("SelectChar").getHeight() * 2 + 8) {
						if (Input.leftClick == true) {
							charSelect = true;
						}
					}
				}
				else {
					batch.draw(LoadedTextures.LOADED.getTexture("CharCreation"), 570, 200, LoadedTextures.LOADED.getTexture("CharCreation").getWidth() * 2, LoadedTextures.LOADED.getTexture("CharCreation").getHeight() * 2);
					if (mouse.x > 778 && mouse.y > 341 && mouse.x < 837 && mouse.y < 460) {
						if (basePlayer.currentAnimation != basePlayer.A_WALK)
							basePlayer.doAnimation(basePlayer.A_WALK, 1f);
					} else {
						basePlayer.doAnimation(basePlayer.A_STANDING, 1f);
					}
					basePlayer.render(batch, 748, 345, 120, 120);
					
					
//					drawPlayerSprite("player_outline", frame, outline_color, batch, x, y, width, height);
//					drawPlayerSprite("player_skin", frame, skin_color, batch, x, y, width, height);
//					if (hairType != 0) {
//						drawPlayerSprite("hair_"+hairType+"_outline", frame, hair_outline_color, batch, x, y, width, height);
//						drawPlayerSprite("hair_"+hairType, frame, hair_color, batch, x, y, width, height);
//					}
//					drawPlayerSprite("player_eyes", frame, eyes_color, batch, x, y, width, height);
//					drawPlayerSprite("player_shoes", frame, shoes_color, batch, x, y, width, height);
//					drawPlayerSprite("player_pants", frame, pants_color, batch, x, y, width, height);
//					drawPlayerSprite("player_shirt", frame, shirt_color, batch, x, y, width, height);
					
					batch.setColor(basePlayer.outline_color.HSBtoRGB());
					batch.draw(LoadedTextures.LOADED.getTexture("player_outline"), 675, 345, 128, 128, 0, 1 / 30f, 1, 0);
					batch.setColor(basePlayer.eyes_color.HSBtoRGB());
					batch.draw(LoadedTextures.LOADED.getTexture("player_eyes"), 630, 345, 128, 128, 0, 1 / 30f, 1, 0);
					batch.setColor(basePlayer.shoes_color.HSBtoRGB());
					batch.draw(LoadedTextures.LOADED.getTexture("player_shoes"), 630, 345, 128, 128, 0, 1 / 30f, 1, 0);
					batch.setColor(basePlayer.shirt_color.HSBtoRGB());
					batch.draw(LoadedTextures.LOADED.getTexture("player_shirt"), 570, 370, 128, 128, 0, 1 / 30f, 1, 0);
					batch.setColor(basePlayer.pants_color.HSBtoRGB());
					batch.draw(LoadedTextures.LOADED.getTexture("player_pants"), 570, 345, 128, 128, 0, 1 / 30f, 1, 0);
					
					if (basePlayer.hairType != 0) {
						batch.setColor(basePlayer.hair_color.HSBtoRGB());
						batch.draw(LoadedTextures.LOADED.getTexture("hair_"+basePlayer.hairType), 570, 400, 128, 128, 0, 1 / 30f, 1, 0);
						batch.setColor(basePlayer.hair_outline_color.HSBtoRGB());
						batch.draw(LoadedTextures.LOADED.getTexture("hair_"+basePlayer.hairType + "_outline"), 650, 400, 128, 128, 0, 1 / 30f - 0.001f, 1, 0);
					}
					
					
					
					Resources.font.getData().setScale(1.3f);
					Resources.font.getData().setScale(1.6f, 1.3f);
					Resources.font.draw(batch, "Hue", 750, 310);
					Resources.font.draw(batch, "Value", 750, 310 - 25);
					Resources.font.draw(batch, "Saturation", 750, 310 - 50);
					Resources.font.draw(batch, "Alpha", 750, 310 - 75);
					Resources.font.draw(batch, "skin", 875, 420);
					
					int num_hairs = 1;
					
					
					if (Input.leftClick == true) {
						System.out.println(mouse.x + ", " + mouse.y);
						if (mouse.x > 846 && mouse.x > 404 && mouse.x < 866 && mouse.y < 424) {
							selectedPart = "skin";
						}
						
						if (mouse.x > 614 && mouse.y > 484 && mouse.x < 655 && mouse.y < 518) {
							selectedPart = "hair";
						}
						
						if (mouse.x > 690 && mouse.y > 479 && mouse.x < 737 && mouse.y < 520) {
							selectedPart = "hair_outline";
						}
						
						if (mouse.x > 682 && mouse.y > 351 && mouse.x < 717 && mouse.y < 378) {
							selectedPart = "shoes";
						}
						
						if (mouse.x > 686 && mouse.y > 433 && mouse.x < 711 && mouse.y < 455) {
							selectedPart = "eyes";
						}
						
						if (mouse.x > 724 && mouse.y > 352 && mouse.x < 756 + 5 && mouse.y < 459) {
							selectedPart = "outline";
						}
						
						if (mouse.x > 621 && mouse.y > 406 && mouse.x < 647 && mouse.y < 452) {
							selectedPart = "shirt";
						}
						
						if (mouse.x > 622 && mouse.y > 359 && mouse.x < 648 && mouse.y < 389) {
							selectedPart = "pants";
						}
						
						if (mouse.x > 586 && mouse.y > 489 && mouse.x < 604 && mouse.y < 512) {
							if (basePlayer.hairType > 0) {
								basePlayer.hairType --;
							}
						}
						
						if (mouse.x > 746 && mouse.y > 489 && mouse.x < 764 && mouse.y < 512) {
							if (basePlayer.hairType < num_hairs) {
								basePlayer.hairType ++;
							}
						}
						
						if (mouse.x > 893 && mouse.y > 343 && mouse.x < 918 && mouse.y < 367) {
							
							try {
								FileOutputStream fos = new FileOutputStream("player.dat");
								ObjectOutputStream oos = new ObjectOutputStream(fos);
								
								oos.writeObject(basePlayer);
								
								oos.close();
							} catch (Exception e) {
								e.printStackTrace();
							}
							
							charSelect = false;
						}
						
						if (mouse.x > 923 && mouse.y > 341 && mouse.x < 949 && mouse.y < 366) {
							charSelect = false;
							basePlayer.loadSkin();
						}
					}
					
					ColorHSVA currentColor = basePlayer.skin_color;
					
					
					
					if (selectedPart.equals("skin")) {
						currentColor = basePlayer.skin_color;
					}
					if (selectedPart.equals("shirt")) {
						currentColor = basePlayer.shirt_color;
					}
					if (selectedPart.equals("pants")) {
						currentColor = basePlayer.pants_color;
					}
					if (selectedPart.equals("outline")) {
						currentColor = basePlayer.outline_color;
					}
					if (selectedPart.equals("hair")) {
						currentColor = basePlayer.hair_color;
					}
					if (selectedPart.equals("hair_outline")) {
						currentColor = basePlayer.hair_outline_color;
					}
					if (selectedPart.equals("eyes")) {
						currentColor = basePlayer.eyes_color;
					}
					if (selectedPart.equals("shoes")) {
						currentColor = basePlayer.shoes_color;
					}
					Color testColor = currentColor.HSBtoRGB();
					batch.end();
					
					batch.begin();
					shapes.setAutoShapeType(true);
					
					shapes.begin(ShapeType.Filled);
					drawHueBar(590, 295, 150, 16, shapes);
					drawValueBar(590, 295 - 25, 150, 16, testColor, shapes);
					drawSaturationBar(590, 295 - 50, 150, 16, testColor, shapes);
					if (selectedPart.equals("outline") == false && selectedPart.equals("hair_outline") == false)
						drawValueBar(590, 295 - 75, 150, 16, Color.WHITE, shapes);
					
					
					
					shapes.setColor(1f - testColor.r, 1f - testColor.g, 1f - testColor.b, 1.0f);
					shapes.rect(590 + 150 * currentColor.h, 295, 5, 20);
					shapes.rect(590 + 150 * currentColor.v, 295 - 25, 5, 20);
					shapes.rect(590 + 150 * currentColor.s, 295 - 50, 5, 20);
					shapes.rect(590 + 150 * currentColor.a, 295 - 75, 5, 20);
					
					shapes.setColor(new Color(1, 1, 0, 0.5f));
					
					shapes.end();
					shapes.begin(ShapeType.Line);
					if (selectedPart.equals("skin")) {
						shapes.rect(846, 404, 866 - 846 + 1, 424 - 404 + 1);
					}
					if (selectedPart.equals("shirt")) {
						shapes.rect(621, 406, 647 - 621, 452 - 406);
					}
					if (selectedPart.equals("pants")) {
						shapes.rect(622, 359, 648 - 622, 389 - 359);
					}
					if (selectedPart.equals("outline")) {
						shapes.rect(724, 352, 756 - 724 + 5, 459 - 352);
					}
					if (selectedPart.equals("hair")) {
						shapes.rect(614, 484, 655 - 614, 518 - 484);
					}
					if (selectedPart.equals("hair_outline")) {
						shapes.rect(690, 479, 737 - 690, 520 - 479);
					}
					if (selectedPart.equals("eyes")) {
						shapes.rect(686, 433, 711 - 686, 455 - 433);
					}
					if (selectedPart.equals("shoes")) {
						shapes.rect(682, 351, 717 - 682, 378 - 351);
					}
					shapes.end();
					shapes.begin(ShapeType.Filled);
					
					if (Gdx.input.isButtonPressed(0)) {
						if (mouse.x > 590 && mouse.y > 295 && mouse.x < 590 + 150 && mouse.y < 295 + 16) {
							currentColor.h = (mouse.x - 590) / 150f;
						}
						
						if (mouse.x > 590 && mouse.y > 295 - 25 && mouse.x < 590 + 150 && mouse.y < 295 + 16 - 25) {
							currentColor.v = (mouse.x - 590) / 150f;
						}
						
						if (mouse.x > 590 && mouse.y > 295 - 50 && mouse.x < 590 + 150 && mouse.y < 295 + 16 - 50) {
							currentColor.s = (mouse.x - 590) / 150f;
						}
						if (selectedPart.equals("outline") == false && selectedPart.equals("hair_outline") == false)
						if (mouse.x > 590 && mouse.y > 295 - 75 && mouse.x < 590 + 150 && mouse.y < 295 + 16 - 75) {
							currentColor.a = (mouse.x - 590) / 150f;
						}
					}
					
					Color color = basePlayer.skin_color.HSBtoRGB();
					shapes.setColor(color);
					shapes.rect(846, 404, 20, 20);
					shapes.end();
					
					batch.setColor(Color.WHITE);
				}
				
				
				
				
				
				
			}
			batch.end();
		}
		
	}
	
	boolean charSelect = false;
	
	public static boolean accessToButtons = true;
	public static String selectedPart = "skin";
	
	public void drawSaturationBar(float x, float y, float width, float height, Color color, ShapeRenderer shapes) {
		shapes.rect(x, y, width, height, Color.WHITE, color, color, Color.WHITE);
	}
	
	public void drawValueBar(float x, float y, float width, float height, Color color, ShapeRenderer shapes) {
		shapes.rect(x, y, width, height, Color.BLACK, color, color, Color.BLACK);
	}
	
	public void drawHueBar(float x, float y, float width, float height, ShapeRenderer shapes) {
		float size = width / 6f;
		shapes.rect(x, y, size, height, new Color(1f, 0, 0, 1f), new Color(1f, 1f, 0, 1f), new Color(1f, 1f, 0, 1f), new Color(1f, 0, 0, 1f));
		shapes.rect(x + size * 1, y, size, height, new Color(1f, 1f, 0, 1f), new Color(0, 1f, 0, 1f), new Color(0, 1f, 0, 1f), new Color(1f, 1f, 0, 1f));
		shapes.rect(x + size * 2, y, size, height, new Color(0, 1f, 0, 1f), new Color(0, 1f, 1f, 1f), new Color(0, 1f, 1f, 1f), new Color(0, 1f, 0, 1f));
		shapes.rect(x + size * 3, y, size, height, new Color(0, 1f, 1f, 1f), new Color(0, 0, 1f, 1f), new Color(0, 0, 1f, 1f), new Color(0, 1f, 1f, 1f));
		shapes.rect(x + size * 4, y, size, height, new Color(0, 0, 1f, 1f), new Color(1f, 0, 1f, 1f), new Color(1f, 0, 1f, 1f), new Color(0, 0, 1f, 1f));
		shapes.rect(x + size * 5, y, size, height, new Color(1f, 0, 1f, 1f), new Color(1f, 0, 0, 1f), new Color(1f, 0, 0, 1f), new Color(1f, 0, 1f, 1f));
	}

	public void resize(int w, int h) {
		
	}

	public void resume() {
		
	}

	public void canceled() {
		// TODO Auto-generated method stub
		
	}

	public void input(String arg0) {
		// TODO Auto-generated method stub
		
	}

	
	
}


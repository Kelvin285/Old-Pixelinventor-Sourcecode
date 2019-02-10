package kevinmerrill.pixelinventor.game.entity.player;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;

import kevinmerrill.pixelinventor.game.Cam;
import kevinmerrill.pixelinventor.game.PixelInventor;
import kevinmerrill.pixelinventor.game.entity.Entity;
import kevinmerrill.pixelinventor.game.textures.LoadedTextures;
import kevinmerrill.pixelinventor.game.tile.Tile;
import kevinmerrill.pixelinventor.game.ui.screen.TitleScreen;
import kevinmerrill.pixelinventor.game.world.World;
import kevinmerrill.pixelinventor.multiplayer.client.packet.PacketPlayer;
import kevinmerrill.pixelinventor.resources.ColorHSVA;
import kevinmerrill.pixelinventor.resources.Input;
import kevinmerrill.pixelinventor.resources.PMath;
import kevinmerrill.pixelinventor.resources.Resources;

import org.lwjgl.input.Keyboard;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class EntityPlayer extends Entity implements Serializable {

	
	public final int[] A_STANDING = {0}, A_WALLDROP = {1, 2, 3}, A_WALLHOP = {3, 2, 1},
			A_WALK = {4, 5, 6, 7, 8, 9, 10, 11}, A_BACKWALK = {11, 10, 9, 8, 7, 6, 5, 4},
			A_DOWNHIT = {12, 12, 12, 13, 14, 15, 16, 17, 17}, A_UPHIT = {17, 16, 15, 14, 13, 12, 12, 12},
			A_KICKOUT = {17, 18, 19, 20}, A_KICKIN = {20, 19, 18, 17}, A_PUNCHOUT = {21, 21, 21, 22, 23, 24},
			A_PUNCHIN = {24, 23, 22, 21}, A_SHIELD = {25, 26, 27}, A_SHIELDUP = {27, 26, 25},
			A_HOLDUP = {28}, A_HOLDMIDDLE = {29}, A_HOLDDOWN = {30}, A_ROLL = {31};
	
	public long fallStart;
	public long fallEnd;
	
	public int spriteSize = 64;
	public int frames = 32;
	public float frameCounter = 0;
	public float frameSpeed = 0;
	public float maxCounter = 10;
	public int currentFrame;
	public int[] currentAnimation = A_STANDING;
	public boolean animationRepeats = true;
	
	public boolean right_facing = true;
	
	public boolean roll = false;
	public int rollTimer = 0;
	public int rollCooldown = 0;
	public float rollDeg = 0;
	public float rollDir = 0;

	public float playerOffset = 0;
	
	public ColorHSVA hair_color = new ColorHSVA(0, 0, 1, 1);
	public ColorHSVA hair_outline_color = new ColorHSVA(0, 0, 0, 1);
	public ColorHSVA outline_color = new ColorHSVA(0, 0, 0, 1);
	public ColorHSVA skin_color = new ColorHSVA(0, 0, 1, 1);
	public ColorHSVA shirt_color = new ColorHSVA(0, 0, 1, 1);
	public ColorHSVA pants_color = new ColorHSVA(0, 0, 1, 1);
	public ColorHSVA eyes_color = new ColorHSVA(0, 0, 0, 1);
	public ColorHSVA shoes_color = new ColorHSVA(0, 0, 0, 1);
	
	public int hairType = 1;
	
	public EntityPlayer() {
		super(0, 0, 64, 64);
	}
	
	public EntityPlayer(long posX, long posY) {
		super(posX, posY, 64, 64);
		Cam.x = posX - (1920 / 4);
		Cam.y = posY - (1080 / 7);
		renderX = posX;
		renderY = posY;
	}

	private boolean canSwitchAnimation = true;

	
	
	@Override
	public void updateAlive() {
		
		
		
		if (this.health < this.maxHealth && this.health > 0) {
			this.health += 0.01f;
		}
		if (flingTime > 0) {
			flingTime -= 1;
		} else {
			fling = false;
		}
		
		if (Keyboard.isKeyDown(Input.MENU)) {
			PixelInventor.world.save();

			PixelInventor.currentGuiScreen = new TitleScreen();
			PixelInventor.world = null;
			Resources.inWorld = false;
			if (PixelInventor.client != null) {
				PixelInventor.client.connected = false;
				PixelInventor.client.sendData(("disconnect\n"+PixelInventor.client.username).getBytes());

				PixelInventor.client.players.clear();
				PixelInventor.client.tryToConnect = false;
				PixelInventor.client = null;
			}
			
			return;
		}
		
		
		if (PixelInventor.client != null) {
//			PixelInventor.client.pingServer();
		}
		World world = PixelInventor.world;
		
		
		long xx = (long)Math.round(posX / 32d - 0.5f);
		long yy = (long)Math.round(posY / 32d - 0.5f);
		
		xx += (Resources.maxChunks / 2) * 16;
		yy += (Resources.maxChunks / 2) * 16;
		
		
		
		if (world == null)
			return;
		

//		for (long x = xx - 5; x < xx + 5; x++) {
//			for (long y = yy - 5; y < yy + 5; y++) {
//				world.setLight(x, y, 16);
//			}
//		}
		
		if (world.isServerWorld() == true) {
			if (PixelInventor.client.connected == false) {
				return;
			}
		}
		
		Cam.x = PMath.lerp(Cam.x, posX - (1920 / 4), 0.1f);
		Cam.y = PMath.lerp(Cam.y, posY - (1080 / 7), 0.1f);
		renderX = PMath.lerp(renderX, posX, 0.4f);
		renderY = PMath.lerp(renderY, posY, 0.4f);
		
		
		boolean attack = false;
		if (Input.ATTACK[0] != Input.NO_KEY && fling == false) { 
			
			if (Gdx.input.isKeyPressed(Input.ATTACK[0])) {
				attack = true;
			}
		} else {
			if (Gdx.input.isButtonPressed(Input.ATTACK[1])) {
				attack = true;
			}
		}
		
		if (Keyboard.isKeyDown(Input.UP) == true && Keyboard.isKeyDown(Input.RIGHT) == false && Keyboard.isKeyDown(Input.LEFT) == false || onGround == false && Keyboard.isKeyDown(Input.UP)) {
			if (attack == true && currentAnimation != A_UPHIT) {
				doAnimation(this.A_UPHIT, 4.5f, false);
			}
//			if (attack == false && currentAnimation == A_KICKOUT || currentAnimation == A_KICKOUT && currentFrame == currentAnimation.length - 1) {
//				doAnimation(this.A_KICKIN, 4.5f, false);
//			}
			if (currentAnimation == A_UPHIT && currentFrame == A_UPHIT.length - 1) {
				doAnimation(this.A_STANDING, 0.0f);
			}
		}
		else
		if (Keyboard.isKeyDown(Input.DOWN) == true) {
			if (this.onGround == false) {
				if (attack == true && currentAnimation != A_DOWNHIT) {
					doAnimation(this.A_DOWNHIT, 4.5f, false);
				}
				
			} else {
				
					if (attack == true && currentAnimation != A_KICKIN && currentAnimation != A_KICKOUT) {
						doAnimation(this.A_KICKOUT, 7f, false);
					}
					if (attack == false && currentAnimation == A_KICKOUT || currentAnimation == A_KICKOUT && currentFrame == currentAnimation.length - 1) {
						doAnimation(this.A_KICKIN, 4.5f, false);
					}
					
				}
			
			
		} else {
			
			
				if (attack == true && currentAnimation != A_PUNCHIN && currentAnimation != A_PUNCHOUT) {
					doAnimation(this.A_PUNCHOUT, 7f, false);
				}
				if (attack == false && currentAnimation == A_PUNCHOUT || currentAnimation == A_PUNCHOUT && currentFrame == currentAnimation.length - 1) {
					doAnimation(this.A_PUNCHIN, 4.5f, false);
				}
				
			
		}
		
		if (currentAnimation == A_DOWNHIT && currentFrame == A_DOWNHIT.length - 1) {
			doAnimation(this.A_STANDING, 0.0f);
		}
		if (currentAnimation == A_KICKIN && currentFrame == A_KICKIN.length - 1) {
			doAnimation(this.A_STANDING, 0.0f);
		}
		if (currentAnimation == A_PUNCHIN && currentFrame == A_PUNCHIN.length - 1) {
			doAnimation(this.A_STANDING, 0.0f);
		}
		
//		canSwitchAnimation = attack == false;
		if (fling == false)
		if (currentAnimation != A_SHIELD) {
			if (Keyboard.isKeyDown(Input.JUMP) && onGround == true && world.getTileState(xx, yy + 2).isSolid() == false) {
				velY = 9.8f;
				doAnimation(A_WALK, 7f);
			}
			
			
			
			if (Keyboard.isKeyDown(Input.RIGHT)) {
				right_facing = true;
				
					if (currentAnimation != A_WALK && onGround == true && currentAnimation != A_PUNCHOUT && currentAnimation != A_PUNCHIN && currentAnimation != A_KICKOUT && currentAnimation != A_KICKIN && currentAnimation != A_DOWNHIT)
						doAnimation(A_WALK, 4.5f);
					if (velX < 3.5f) {
						velX += 0.4f;
						
					}
					if (roll == true)
						velX += 0.9f;
				
				velX *= 0.9f;
				if (currentAnimation == A_KICKOUT || currentAnimation == A_KICKIN || currentAnimation == A_PUNCHOUT || currentAnimation == A_PUNCHIN) {
					velX *= 0.8f;
				}
			} else
			
			if (Keyboard.isKeyDown(Input.LEFT)) {
				right_facing = false;
				
					if (currentAnimation != A_WALK && onGround == true && currentAnimation != A_PUNCHOUT && currentAnimation != A_PUNCHIN && currentAnimation != A_KICKOUT && currentAnimation != A_KICKIN && currentAnimation != A_DOWNHIT)
						doAnimation(A_WALK, 4.5f);
					if (velX > -3.5f) {
						velX -= 0.4f;
						
					}
					if (roll == true)
						velX -= 0.9f;
				velX *= 0.9f;
				if (currentAnimation == A_KICKOUT || currentAnimation == A_KICKIN || currentAnimation == A_PUNCHOUT || currentAnimation == A_PUNCHIN) {
					velX *= 0.8f;
				}
			} else {
				if (onGround == true)
				velX *= 0.7f;
			}
			
			
		}
		
		
		
		if (velY > 0 && world.getTileState(xx, yy + 2).isSolid() == true) {
			velY *= 0.65f;
		}
		if (this.currentAnimation != A_WALLHOP) {
			if (velX > 0) {
				if (this.collisionWithTile(posX + 8, posY + 4f) || this.collisionWithTile(posX + 8, posY + 48f) & roll == false) {
					if (this.collisionWithTile(posX + 8, posY + 48f) == false) {
						if (this.onGround == true)
							this.posY += 16;
						else
						{
							if (this.collisionWithTile(posX, posY - 16) == false & roll == false)
							if (this.currentAnimation != A_WALLHOP)
							doAnimation(A_WALLHOP, 1f, false);
						}
					}
					else {
						velX *= -0.1f;
					}
					
					if (this.collisionWithTile(posX + 8, posY + 48f + 16) == false & roll == false) {
						if (this.collisionWithTile(posX, posY - 16) == false)
							if (this.currentAnimation != A_WALLHOP)
							doAnimation(A_WALLHOP, 1f, false);
					}
				}
				
			}
			if (velX < 0) {
				if (this.collisionWithTile(posX - 8, posY + 4f) || this.collisionWithTile(posX - 8, posY + 48f) & roll == false) {
					if (this.collisionWithTile(posX - 8, posY + 48f) == false) {
						if (this.onGround == true)
							this.posY += 16;
						else
						{
							if (this.collisionWithTile(posX, posY - 16) == false & roll == false)
							if (this.currentAnimation != A_WALLHOP)
							doAnimation(A_WALLHOP, 1f, false);
						}
					}
					else {
						velX *= -0.1f;
					}
					
					if (this.collisionWithTile(posX - 8, posY + 48f + 16) == false & roll == false) {
						if (this.collisionWithTile(posX, posY - 16) == false)
							if (this.currentAnimation != A_WALLHOP)
							doAnimation(A_WALLHOP, 1f, false);
					}
				
				}
				
			}
		} else {
			this.velX = 0;
			this.velY = 4f;
			if (currentFrame == A_WALLHOP.length - 1) {
				doAnimation(A_STANDING, 0f);
			}
		}
		
		if (fling == false)
		if (roll == false) {
			if (Keyboard.isKeyDown(Input.DOWN) && currentAnimation != A_PUNCHOUT && currentAnimation != A_PUNCHIN && currentAnimation != A_KICKOUT && currentAnimation != A_KICKIN && currentAnimation != A_DOWNHIT) {
				if (onGround == true) {
					velX *= 0.95f;
				}
				if (currentAnimation == A_SHIELD) {
					if (currentFrame == 27) {
						frameSpeed = 0;
					}
				} else {
					doAnimation(A_SHIELD, 2.5f, false);
				}
				if (Keyboard.isKeyDown(Input.RIGHT) || Keyboard.isKeyDown(Input.LEFT)) { 
					if (rollCooldown == 0) {
						roll = true;
						rollTimer = 16;
						rollCooldown = 50;
					}
				}
			} else {
				if (currentAnimation == A_SHIELD) {
					doAnimation(A_SHIELDUP, 2.5f, false);
				}
				if (currentAnimation == A_SHIELDUP) {
					if (currentFrame == 25) {
						doAnimation(A_STANDING, 0);
					}
				}
			}
			rollDeg = 0;
		} else {
			if (rollTimer > 0) {
				rollTimer -= 1;
			} else 
			{
				if ((int)Math.abs(rollDeg) % 360 <= 10 || Math.abs(rollDir) < 0.3f) {
					roll = false;
					rollDir = 0;
					rollDeg = 0;
				}
			}
			if (Keyboard.isKeyDown(Input.RIGHT)) {
				if (velX < 4) {
					velX += 0.5f;
				}
				if (rollDir < 1) {
					rollDir += 0.1f;
				}
			}
			if (Keyboard.isKeyDown(Input.LEFT)) {
				if (velX > -4) {
					velX -= 0.5f;
				}
				
				if (rollDir > -1) {
					rollDir -= 0.1f;
				}
			}
			rollDeg += 15 * rollDir;
		}
		
		if (rollCooldown > 0) {
			rollCooldown -= 1;
		}
		
		if (this.collisionWithTile(posX, posY) == false) {
			if (onGround == true || velY > 0) {
				this.fallStart = System.currentTimeMillis();
			}
			
			
			onGround = false;
			if (velY > -9.8f) {
				velY -= 0.4f; 
			}
			
			this.fallEnd = System.currentTimeMillis() - fallStart;			
			
		} else {
			this.playerOffset = (float) world.getTileOffset((long)Math.round((posX) / 32 - 0.5f) + (Resources.maxChunks / 2) * 16, (long)Math.round((posY) / 32 - 0.5f) + (Resources.maxChunks / 2) * 16);
			
			if (Math.abs(velX) < 0.1f && currentAnimation == A_WALK) {
				doAnimation(A_STANDING, 0);
			}
			if (onGround == false) {
//				System.out.println(this.fallEnd);
			}
			onGround = true;

			long X = (long)Math.round((posX) / 32 - 0.5f) + (Resources.maxChunks / 2) * 16;
			long Y = (long)Math.round((posY) / 32 - 0.5f) + (Resources.maxChunks / 2) * 16;
//			System.out.println(X + ", " + Y);
			if (PixelInventor.world != null) {
				Tile.getTileStateFromId(PixelInventor.world.getTile(X, Y)).onWalkedUpon(this, PixelInventor.world, X, Y, posX - 16, posY);
			}
			
			if (fallEnd > 1000) {
				if (roll == false)
					this.hurt(fallEnd / 50, 0, velY );
				else if (fallEnd < 2000)
					this.hurt(fallEnd / 100, 0, 0 );
				else
					this.hurt(fallEnd / 50, 0, 0 );
				velX = 0;
			} else {
				if (fallEnd > 500) {
					if (roll == true)
					this.hurt(fallEnd / 50, 0, 0 );
					
				}
			}
			this.fallEnd = 0;
			if (velY < 0) {
				
				velY = 0;
				if (Math.abs(velX) <= 0.1f)
				doAnimation(A_SHIELDUP, 1f, false);
			}
			
		}
		
		if (fling == true && velY == 3.6)
			velY = 0;
		posX += (int)velX;
		posY += (int)velY;
		
		if (world.isServerWorld() == true) {
			PixelInventor.client.sendData(new PacketPlayer(posX, posY, currentAnimation[currentFrame], right_facing, PixelInventor.client.username).getData());
//			PixelInventor.client.pingServer();
		}
		if (hurtTime > 0)
			hurtTime -= 1;
		
		if (world.getWater(xx, yy + 1) > 10) {
			if (Keyboard.isKeyDown(Input.DOWN) == false) {
				if (this.velY < 0) {
					this.velY *= 0.9f;
				}
			} else {
				if (this.velY < 0) {
					this.velY *= 0.95f;
				}
			}
			if (Keyboard.isKeyDown(Input.JUMP)) {
				if (this.velY < 4) {
					this.velY += 0.65f;
				}
			}
			
			this.fallEnd = 0;
		}
		
	}
	
	
	
	@Override
	public void updateDead() {
		World world = PixelInventor.world;
		if (world == null)
			return;
	}
	
	
	public void render(SpriteBatch batch, float x, float y, float width, float height) {
		
		
		
		if (roll == true) {
			doAnimation(A_ROLL, 0);
		}
		if (currentAnimation == A_ROLL && Math.abs(velX) <= 0.1f) {
			doAnimation(A_STANDING, 0);
		}
		int frame = 0;
		if (frameCounter < maxCounter)
			frameCounter += frameSpeed;
		else
		{
			frameCounter = 0;
			if (currentFrame < currentAnimation.length - 1) {
				currentFrame++;
			} else {
				if (animationRepeats == true)
				currentFrame = 0;
			}
		}
		
		if (currentFrame < currentAnimation.length && frame >= 0)
			frame = currentAnimation[currentFrame];
//
//		if (currentAnimation.length > 1)
//		{
//			if (currentAnimation[0] < currentAnimation[1])
//				if (currentFrame >= currentAnimation[currentAnimation.length - 1]) {
//					if (animationRepeats == true)
//						currentFrame = currentAnimation[0];
//					else {
//						currentFrame = currentAnimation[currentAnimation.length - 1];
//					}
//				}
//			else if (currentAnimation[0] > currentAnimation[1]) {
//				
//				if (currentFrame <= currentAnimation[currentAnimation.length - 1]) {
//					if (animationRepeats == true)
//						currentFrame = currentAnimation[0];
//					else {
//						currentFrame = currentAnimation[currentAnimation.length - 1];
//					}
//				}
//			}
//		}
		
		
		
		drawPlayerSprite("player_outline", frame, outline_color, batch, x, y, width, height);
		drawPlayerSprite("player_skin", frame, skin_color, batch, x, y, width, height);
		if (hairType != 0) {
			drawPlayerSprite("hair_"+hairType+"_outline", frame, hair_outline_color, batch, x, y, width, height);
			drawPlayerSprite("hair_"+hairType, frame, hair_color, batch, x, y, width, height);
		}
		drawPlayerSprite("player_eyes", frame, eyes_color, batch, x, y, width, height);
		drawPlayerSprite("player_shoes", frame, shoes_color, batch, x, y, width, height);
		drawPlayerSprite("player_pants", frame, pants_color, batch, x, y, width, height);
		drawPlayerSprite("player_shirt", frame, shirt_color, batch, x, y, width, height);
	}
	
	
	@Override
	public void render(SpriteBatch batch) {
		
		
		
		if (roll == true) {
			doAnimation(A_ROLL, 0);
		}
		if (currentAnimation == A_ROLL && Math.abs(velX) <= 0.1f) {
			doAnimation(A_STANDING, 0);
		}
		int frame = 0;
		if (frameCounter < maxCounter)
			frameCounter += frameSpeed;
		else
		{
			frameCounter = 0;
			if (currentFrame < currentAnimation.length - 1) {
				currentFrame++;
			} else {
				if (animationRepeats == true)
				currentFrame = 0;
			}
		}
		
		if (currentFrame < currentAnimation.length && frame >= 0)
			frame = currentAnimation[currentFrame];
//
//		if (currentAnimation.length > 1)
//		{
//			if (currentAnimation[0] < currentAnimation[1])
//				if (currentFrame >= currentAnimation[currentAnimation.length - 1]) {
//					if (animationRepeats == true)
//						currentFrame = currentAnimation[0];
//					else {
//						currentFrame = currentAnimation[currentAnimation.length - 1];
//					}
//				}
//			else if (currentAnimation[0] > currentAnimation[1]) {
//				
//				if (currentFrame <= currentAnimation[currentAnimation.length - 1]) {
//					if (animationRepeats == true)
//						currentFrame = currentAnimation[0];
//					else {
//						currentFrame = currentAnimation[currentAnimation.length - 1];
//					}
//				}
//			}
//		}
		
//		loadTexture("assets/textures/entities/player.png", "player");
//		loadTexture("assets/textures/entities/player_eyes.png", "player_eyes");
//		loadTexture("assets/textures/entities/player_outline.png", "player_outline");
//		loadTexture("assets/textures/entities/player_pants.png", "player_pants");
//		loadTexture("assets/textures/entities/player_shirt.png", "player_shirt");
//		loadTexture("assets/textures/entities/player_shoes.png", "player_shoes");
//		loadTexture("assets/textures/entities/player_skin.png", "player_skin");
		//draw order: outline, skin, hair, eyes, shoes, pants, shirt
		drawPlayerSprite("player_outline", frame, outline_color, batch, renderX - 32 - Cam.x, renderY - Cam.y - 3 + playerOffset, 64, 64);
		drawPlayerSprite("player_skin", frame, skin_color, batch, renderX - 32 - Cam.x, renderY - Cam.y - 3 + playerOffset, 64, 64);
		if (hairType != 0) {
			drawPlayerSprite("hair_"+hairType+"_outline", frame, hair_outline_color, batch, renderX - 32 - Cam.x, renderY - Cam.y - 3 + playerOffset, 64, 64);
			drawPlayerSprite("hair_"+hairType, frame, hair_color, batch, renderX - 32 - Cam.x, renderY - Cam.y - 3 + playerOffset, 64, 64);
		}
		drawPlayerSprite("player_eyes", frame, eyes_color, batch, renderX - 32 - Cam.x, renderY - Cam.y - 3 + playerOffset, 64, 64);
		drawPlayerSprite("player_shoes", frame, shoes_color, batch, renderX - 32 - Cam.x, renderY - Cam.y - 3 + playerOffset, 64, 64);
		drawPlayerSprite("player_pants", frame, pants_color, batch, renderX - 32 - Cam.x, renderY - Cam.y - 3 + playerOffset, 64, 64);
		drawPlayerSprite("player_shirt", frame, shirt_color, batch, renderX - 32 - Cam.x, renderY - Cam.y - 3 + playerOffset, 64, 64);
	}
	
	public void drawPlayerSprite(String sprite, int frame, ColorHSVA color, SpriteBatch batch, float x, float y, float width, float height) {
		Sprite spr = LoadedTextures.LOADED.getTextureAsSprite(sprite, 0, frame * spriteSize, 64, 64);
		spr.setColor(color.HSBtoRGB());
		
		
		if (right_facing == false)
			spr.flip(true, false);
		
		spr.setRotation(0);
		spr.setRotation(-rollDeg);
				
		
		spr.setBounds(x, y, width, height);
		
//		flingVelX = velX;
//		flingVelY = velY;
		if (fling == true) {
			if (Math.abs(flingVelX) > Math.abs(flingVelY)) {
				if (flingVelX < 0) {
					right_facing = false;
				} else {
					right_facing = true;
				}
				
			}
			
			if (Math.abs(flingVelX) > Math.abs(flingVelY)) {
				spr.setRotation(-90);
				if (velX < 0) {
					spr.setRotation(90);
				}
				spr.flip(new Random().nextBoolean(), false);
//				spr.translateY(-16);
			}
			
			if (Math.abs(flingVelY) > Math.abs(flingVelX)) {
				if (flingVelY < 0) {
					spr.setRotation(90);
					spr.translateY(-8);
				} else {
					spr.setRotation(-90);
					spr.translateY(8);
				}
			}
			
			if (Math.abs(velY) > 0.1f && Math.abs(flingVelY) > Math.abs(flingVelX)) {
				if (velY < 0) {
					spr.setRotation(180);
				} else {
					spr.setRotation(0);
				}
				spr.flip(new Random().nextBoolean(), false);
			}
			
			if (onGround == true) {
				spr.setRotation(-90);
				spr.translateY(-16);
			}
		} else {
			if (roll == true) {
				spr.translateY(-16);
			}
		}
		
		
		
		spr.setOriginCenter();
		
		spr.draw(batch);
	}
	
	public void doAnimation(int[] animation, float frameSpeed) {
		if (canSwitchAnimation == false) return;
		this.frameCounter = 0;
		this.currentAnimation = animation;
		this.currentFrame = 0;
		this.frameSpeed = frameSpeed;
		this.animationRepeats = true;
	}
	
	public void doAnimation(int[] animation, float frameSpeed, boolean animationRepeats) {
		if (canSwitchAnimation == false) return;
		this.frameCounter = 0;
		this.currentAnimation = animation;
		this.currentFrame = 0;
		this.frameSpeed = frameSpeed;
		this.animationRepeats = animationRepeats;
	}



	public EntityPlayer loadSkin() {
		EntityPlayer p = new EntityPlayer(0, 0);
		try {
			FileInputStream fos = new FileInputStream("player.dat");
			ObjectInputStream oos = new ObjectInputStream(fos);
			
			p = (EntityPlayer)oos.readObject();
			
			oos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return p;
	}
	
}

package kevinmerrill.pixelinventor.multiplayer.client.entities;

import java.net.InetAddress;

import org.lwjgl.input.Keyboard;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import kevinmerrill.pixelinventor.game.Cam;
import kevinmerrill.pixelinventor.game.PixelInventor;
import kevinmerrill.pixelinventor.game.entity.Entity;
import kevinmerrill.pixelinventor.game.textures.LoadedTextures;
import kevinmerrill.pixelinventor.game.world.World;
import kevinmerrill.pixelinventor.resources.Input;
import kevinmerrill.pixelinventor.resources.Resources;

public class EntityPlayerMP extends Entity {
	public int spriteSize = 64;
	public int frames = 31;
	public float frameCounter = 0;
	public float frameSpeed = 0;
	public float maxCounter = 10;
	public int currentFrame;
	public String name;
	public InetAddress address;
	
	
//	public InetAddress address;
	
	public boolean right_facing = true;
	public int port;
	
	public EntityPlayerMP(long posX, long posY) {
		super(posX, posY, 64, 64);
	}

	@Override
	public void updateAlive() {
		
		World world = PixelInventor.world;
		if (world == null)
			return;
		
	}
	
	@Override
	public void updateDead() {
		World world = PixelInventor.world;
		if (world == null)
			return;
	}
	
	@Override
	public void render(SpriteBatch batch) {
		Sprite spr = LoadedTextures.LOADED.getTextureAsSprite("player", 0, currentFrame * spriteSize, 64, 64);
		if (right_facing == false)
			spr.flip(true, false);
		batch.draw(spr, posX - 32 - Cam.x, posY - Cam.y - 3, 64, 64);
		if (name != null)
			if (name.length() > 0)
		Resources.font.draw(batch, name, posX - 32 - Cam.x - name.length() * 2, posY - Cam.y - 3 + 90);
	}
}

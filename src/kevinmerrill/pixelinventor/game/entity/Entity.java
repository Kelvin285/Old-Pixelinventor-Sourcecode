package kevinmerrill.pixelinventor.game.entity;

import java.io.Serializable;

import kevinmerrill.pixelinventor.game.PixelInventor;
import kevinmerrill.pixelinventor.game.world.World;
import kevinmerrill.pixelinventor.resources.PMath;
import kevinmerrill.pixelinventor.resources.Resources;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public abstract class Entity implements Serializable{
	
	public float posX, posY;
	public float renderX, renderY;
	
	public float velX, velY;
	
	public float width, height;
	
	public float health = 100;
	public float maxHealth = 100;
	
	public boolean fling = false;
	public int flingTime = 0;
	public int hurtTime = 0;
	
	public float flingVelX, flingVelY;
	
	public boolean isDead;
	
	public boolean onGround = false;
	
	public Entity(float posX, float posY, float width, float height) {
		this.posX = posX;
		this.posY = posY;
		this.width = width;
		this.height = height;
	}
	
	protected boolean collisionWithTile(float x, float y) {
		
		World world = PixelInventor.world;
		try {
		if (world.getTileState((long)Math.round((x) / 32 - 0.5f) + (Resources.maxChunks / 2) * 16, (long)Math.round((y) / 32 - 0.5f) + (Resources.maxChunks / 2) * 16).isSolid() == true) {
			if (PMath.areaContains(Math.round(x), Math.round(y), 16, 16, x, y) == true) {
				if (flingTime > 100)
					flingTime = 100;
				return true;
			}
		}
		}catch (Exception e){}
		return false;
	}
	
	
	
	public void hurt(float damage, float knockbackX, float knockbackY) {
		flingVelX = knockbackX;
		flingVelY = knockbackY;
		if (hurtTime == 0) {
			health -= damage;
			velX += knockbackX;
			velY += knockbackY;
			System.out.println(Math.sqrt(velX * velX + velY * velY));
			if (Math.sqrt(velX * velX + velY * velY) > 20) {
				fling = true;
				flingTime = 500;
			}
			hurtTime = 30;
		}
	}
	
	public abstract void updateAlive();
	
	public abstract void updateDead();
	
	public abstract void render(SpriteBatch batch);
}

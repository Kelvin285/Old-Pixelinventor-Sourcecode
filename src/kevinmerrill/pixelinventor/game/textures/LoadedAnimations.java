package kevinmerrill.pixelinventor.game.textures;

import java.util.HashMap;
import java.util.Map;

import kevinmerrill.pixelinventor.resources.Resources;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public enum LoadedAnimations {
LOADED;

private Map<String, Animation> animations;

public void load() {
	animations = new HashMap<String, Animation>();
	
//	loadTextureAsAnimation(LoadedTextures.LOADED.getTexture("vfglogo"), Resources.TITLESIZE1, Resources.TITLESIZE2, 0.025f, true, "vfglogo");
	loadTextureAsAnimation(LoadedTextures.LOADED.getTexture("healthbar"), 61, 11, 4, true, "healthbar_anim");
}

public void dispose() {
	animations.clear();
}

private void loadTextureAsAnimation(Texture texture, int WIDTH, int HEIGHT, float frameDuration, boolean looping, String name) {
	TextureRegion[][] region = TextureRegion.split(texture, WIDTH, HEIGHT);
	TextureRegion[] frames = new TextureRegion[region.length * region[0].length];
	int index = 0;
	for (int i = 0; i < region.length; i++) {
		for (int j = 0; j < region[0].length; j++) {
			frames[index++] = region[i][j];
		}
	}
	Animation anim = new Animation(frameDuration, frames);
	anim.setPlayMode(looping ? PlayMode.LOOP : PlayMode.NORMAL);
	animations.put(name, new Animation(frameDuration, frames));
}

public Animation getAnimation(String name) {
	Animation anim = animations.get(name);
	if (anim == null) {
		try {
			throw new Exception("Can not find animation: {" + name + "}");
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	return anim;
}
}

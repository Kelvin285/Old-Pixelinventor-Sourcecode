package kevinmerrill.pixelinventor.game.textures;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public enum LoadedTextures {
LOADED;

private Map<String, Texture> textures;

private Map<String, Sprite> sprites;

private Map<String, TextureRegion[][]> textureRegions;

public void load() {
	textures = new HashMap<String, Texture>();
	sprites = new HashMap<String, Sprite>();
	textureRegions = new HashMap<String, TextureRegion[][]>();
	
	loadTexture("assets/textures/title/title.png", "vfglogo");
	loadTexture("assets/textures/title/title2.png", "pixelinventor");
	loadTexture("assets/textures/misc/rectangle.png", "rectangle");
	loadTexture("assets/textures/tiles/Dirt.png", "TileDirt");
	loadTextureRegion("TileDirt", 16, 16);
	loadTexture("assets/textures/tiles/Grass.png", "TileGrass");
	loadTextureRegion("TileGrass", 16, 16);
	loadTexture("assets/textures/tiles/Stone.png", "TileStone");
	loadTextureRegion("TileStone", 16, 16);
	loadTexture("assets/textures/tiles/Snow.png", "TileSnow");
	loadTextureRegion("TileSnow", 16, 16);
	loadTexture("assets/textures/tiles/SnowyGrass.png", "TileSnowGrass");
	loadTextureRegion("TileSnowGrass", 16, 16);
	loadTexture("assets/textures/tiles/DeadGrass.png", "TileDeadGrass");
	loadTextureRegion("TileDeadGrass", 16, 16);
	loadTexture("assets/textures/tiles/Water.png", "TileWater");
	loadTextureRegion("TileWater", 16, 16);
	loadTexture("assets/textures/tiles/WaterTexture.png", "WaterTexture");
	loadTextureRegion("WaterTexture", 8, 8);
	loadTexture("assets/textures/tiles/Wood.png", "TileWood");
	loadTextureRegion("TileWood", 16, 16);
	loadTexture("assets/textures/tiles/Leaves.png", "TileLeaves");
	loadTextureRegion("TileLeaves", 16, 16);
	loadTexture("assets/textures/tiles/Sand.png", "TileSand");
	loadTextureRegion("TileSand", 16, 16);
	loadTexture("assets/textures/entities/player.png", "player");
	loadTexture("assets/textures/entities/player/player_eyes.png", "player_eyes");
	loadTexture("assets/textures/entities/player/player_outline.png", "player_outline");
	loadTexture("assets/textures/entities/player/player_pants.png", "player_pants");
	loadTexture("assets/textures/entities/player/player_shirt.png", "player_shirt");
	loadTexture("assets/textures/entities/player/player_shoes.png", "player_shoes");
	loadTexture("assets/textures/entities/player/player_skin.png", "player_skin");
	
	loadTexture("assets/textures/entities/player/hair/hair_1.png", "hair_1");
	loadTexture("assets/textures/entities/player/hair/hair_1_outline.png", "hair_1_outline");
	
	loadTexture("assets/textures/ui/health.png", "healthbar");
	loadTexture("assets/textures/tiles/lilypad.png", "LilyPad");
	loadTextureRegion("LilyPad", 32, 32);
	loadTexture("assets/textures/tiles/lilypad_green.png", "LilyPadGreen");
	loadTextureRegion("LilyPadGreen", 32, 32);
	loadTexture("assets/textures/tiles/plants/broadleaf.png", "Broadleaf");
	loadTexture("assets/textures/tiles/plants/spruce.png", "Spruce");
	loadTexture("assets/textures/tiles/plants/broadleaf_2.png", "Broadleaf_2");
	loadTexture("assets/textures/tiles/plants/tallgrass.png", "Tallgrass");
	loadTextureRegion("Tallgrass", 16, 16);
	loadTexture("assets/textures/tiles/plants/tallgrass_2.png", "Tallgrass_2");
	loadTextureRegion("Tallgrass_2", 16, 16);
	loadTexture("assets/textures/tiles/plants/cactus.png", "Cactus");
	loadTextureRegion("Cactus", 32, 32);
	loadTexture("assets/textures/tiles/plants/acacia.png", "Acacia");
	loadTexture("assets/textures/tiles/Rubber.png", "TileRubber");
	loadTextureRegion("TileRubber", 16, 16);
	loadTexture("assets/textures/tiles/Fiberglass.png", "TileFiberglass");
	loadTextureRegion("TileFiberglass", 16, 16);
	loadTexture("assets/textures/title/charcreate_select.png", "SelectChar");
	loadTexture("assets/textures/title/character_creation.png", "CharCreation");
}

public void dispose() {
	sprites.clear();
	for (Texture t : textures.values())
		t.dispose();
	textures.clear();
}

public void loadTextureRegion(String name, int width, int height) {
	Texture texture = getTexture(name);
	TextureRegion[][] region = new TextureRegion(texture).split(width, height);
	System.out.println(region[0].length + ", " + name);
	textureRegions.put(name, region);
}

public TextureRegion[][] getTextureRegion(String name) {
	TextureRegion[][] texture = textureRegions.get(name);
	if (texture == null) {
		try {
			throw new Exception("Can not find texture region: {" + name + "}");
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	return texture;
}

public void loadTexture(String filename, String name) {
	Texture texture = new Texture(Gdx.files.internal(filename));
	textures.put(name, texture);
}

public Texture getTexture(String name) {
	Texture texture = textures.get(name);
	if (texture == null) {
		try {
			throw new Exception("Can not find texture: {" + name + "}");
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	return texture;
}
public Sprite getTextureAsSprite(String name) {
	if (!sprites.containsKey(name)) {
		Sprite sprite = new Sprite(getTexture(name));
		sprites.put(name, sprite);
		return sprite;
	}
	return sprites.get(name);
}

public Sprite getTextureAsSprite(String name, float x, float y, float width, float height) {
	if (!sprites.containsKey(name)) {
		Sprite sprite = new Sprite(getTexture(name));
		sprite.setRegion((int)x, (int)y, (int)width, (int)height);
		sprites.put(name, sprite);
		return sprite;
	}
	Sprite sprite = sprites.get(name);
	sprite.setRegion((int)x, (int)y, (int)width, (int)height);
	return sprite;
}

}

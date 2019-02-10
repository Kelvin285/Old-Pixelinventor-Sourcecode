package kevinmerrill.pixelinventor.game.ui;


import java.util.ArrayList;

import kevinmerrill.pixelinventor.game.PixelInventor;
import kevinmerrill.pixelinventor.game.textures.LoadedTextures;
import kevinmerrill.pixelinventor.resources.Input;
import kevinmerrill.pixelinventor.resources.PMath;
import kevinmerrill.pixelinventor.resources.Resources;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class TextButton {
	
	public float x, y, width, height;
	public float offsX, offsY;
	public String text;
	public boolean debug;
	
	public ArrayList<TextButton> buttons = new ArrayList<TextButton>();
	
	public TextButton(float x, float y, float width, float height, String text) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.text = text;
		addMoreButtons();
	}
	
	public void onClicked() {
		
	}
	
	public void updateOffset() {
		
	}
	
	public void update(SpriteBatch batch) {
		for (int i = 0; i < buttons.size(); i++) {
			buttons.get(i).update(batch);
		}
		updateOffset();
		if (batch == null) {
			return;
		}
		float highlight = 0.7f;
		
		float shade = PMath.areaContains(x, y, width, height, PixelInventor.mouse.x, PixelInventor.mouse.y) ? highlight : 0.5f;
		
		float border = 4.0f;
		
		if (PixelInventor.accessToButtons == true)
		if (shade == highlight && Input.leftClick == true) {
			onClicked();
		}
		
		
		batch.setColor((shade + 0.5f) * (37f/255f), (shade + 0.5f) * (35f/255f), (shade + 0.5f) * (35f/255f), 1.0f);
		batch.draw(LoadedTextures.LOADED.getTexture("rectangle"), x - border - 4, y - border - 4, width + border * 2 + 4, height + border * 2 + 4);
		
		batch.setColor((shade + 0.5f) * (74f/255f), (shade + 0.5f) * (103f/255f), (shade + 0.5f) * (138f/255f), 1.0f);
		batch.draw(LoadedTextures.LOADED.getTexture("rectangle"), x - border, y - border, width + border * 2 + 4, height + border * 2 + 4);
		
		batch.setColor((shade + 0.5f) * (141f/255f), (shade + 0.5f) * (165f/255f), (shade + 0.5f) * (193f/255f), 1.0f);
		batch.draw(LoadedTextures.LOADED.getTexture("rectangle"), x - border, y - border, width + border * 2, height + border * 2);
		
		batch.setColor(shade * 2, shade * 2, (1.0f - shade) * 2, 1.0f);
		batch.draw(LoadedTextures.LOADED.getTexture("rectangle"), x - border + 4, y - border + height, width, 4);
		batch.draw(LoadedTextures.LOADED.getTexture("rectangle"), x - border + width, y, 4, height);
		
		batch.setColor(1.0f, 1.0f, 1.0f, 1.0f);
		
		Resources.font.getData().setScale(2.0f);
		Resources.font.setColor(1.0f, 1.0f, 1.0f, 1.0f);
		Resources.font.draw(batch, text, x + offsX, y + offsY);
		
	}

	public void addMoreButtons() {
		
	}
}

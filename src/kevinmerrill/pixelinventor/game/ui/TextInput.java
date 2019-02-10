package kevinmerrill.pixelinventor.game.ui;


import kevinmerrill.pixelinventor.game.PixelInventor;
import kevinmerrill.pixelinventor.game.textures.LoadedTextures;
import kevinmerrill.pixelinventor.resources.Input;
import kevinmerrill.pixelinventor.resources.PMath;
import kevinmerrill.pixelinventor.resources.Resources;

import org.lwjgl.input.Keyboard;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class TextInput extends TextButton {
	
	public String defaultText;
	public boolean debug;
	public int maxChars;
	public boolean selected = false;
	
	public TextInput(float x, float y, float width, float height, String defaultText, String text) {
		super(x, y, width, height, text);
		this.defaultText = defaultText;
	}
	
	public TextInput(float x, float y, float width, float height, String defaultText) {
		super(x, y, width, height, "");
		this.defaultText = defaultText;
	}
	
	public void onUnclicked() {
		selected = false;
	}
	
	public void onClicked() {
		selected = true;
	}
	
	public void updateOffset() {
		
	}
	
	public void update(SpriteBatch batch) {
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
		if (shade != highlight && Input.leftClick == true || selected == true && Gdx.input.isKeyJustPressed(Keys.ENTER)) {
			onUnclicked();
		}
		
		
		batch.setColor((shade + 0.5f) * (37f/255f), (shade + 0.5f) * (35f/255f) * 0.8f, (shade + 0.5f) * (35f/255f) * 0.8f, 1.0f);
		batch.draw(LoadedTextures.LOADED.getTexture("rectangle"), x - border - 4, y - border - 4, width + border * 2 + 4, height + border * 2 + 4);
		
		batch.setColor((shade + 0.5f) * (74f/255f), (shade + 0.5f) * (103f/255f) * 0.8f, (shade + 0.5f) * (138f/255f) * 0.8f, 1.0f);
		batch.draw(LoadedTextures.LOADED.getTexture("rectangle"), x - border, y - border, width + border * 2 + 4, height + border * 2 + 4);
		
		batch.setColor((shade + 0.5f) * (141f/255f), (shade + 0.5f) * (165f/255f) * 0.8f, (shade + 0.5f) * (193f/255f) * 0.8f, 1.0f);
		batch.draw(LoadedTextures.LOADED.getTexture("rectangle"), x - border, y - border, width + border * 2, height + border * 2);
		
		batch.setColor(shade * 2, shade * 2, (1.0f - shade) * 2, 1.0f);
		batch.draw(LoadedTextures.LOADED.getTexture("rectangle"), x - border + 4, y - border + height, width, 4);
		batch.draw(LoadedTextures.LOADED.getTexture("rectangle"), x - border + width, y, 4, height);
		
		batch.setColor(1.0f, 1.0f, 1.0f, 1.0f);
		
		Resources.font.getData().setScale(2.0f);
		Resources.font.setColor(1.0f, 1.0f, 1.0f, 1.0f);
		if (selected == false) {
			if (text.isEmpty())
				Resources.font.draw(batch, defaultText, x + offsX, y + offsY);
			else
				Resources.font.draw(batch, text, x + offsX, y + offsY);
		} else {
			boolean line = (System.currentTimeMillis() / 1000) % 2 == 0;
			
			if (line == true)
				Resources.font.draw(batch, text + "|", x + offsX, y + offsY);
			else
				Resources.font.draw(batch, text, x + offsX, y + offsY);
			
			if (Gdx.input.isKeyJustPressed(Keys.BACKSPACE)) {
				
				final String t = text;
				if (text.length() > 0)
				text = text.substring(0, text.length() - 1).trim();
				
			}
//			System.out.println(Keys.A + ", " + (char)(Keys.A + (90 - 29 - 25)) + ", " + (Keys.A + (90 - 29 - 25) - 36));
			
//			System.out.println((char)(Keys.A + (90 - 29 - 25)));

//			System.out.print("\n");
//			Keyboard.poll();
//			while (Keyboard.next()) {
				
//				if (Keyboard.getEventKeyState()) {
//					if (Input.isKeyJustPressed(Keyboard.getEventKey())) {
			char c = 0;
			
			for (int i = 0; i < 255; i++) {
				
				if (Gdx.input.isKeyJustPressed(i)) {
					if (Keys.toString(i).length() == 1)
					c = (char)Keys.toString(i).toCharArray()[0];
				}
			}
			
			
			
			
			
//			if (text.length() + 1 <= maxChars) {
//				if (Keyboard.getEventCharacter() != '/' && Keyboard.getEventCharacter() != '\\')
//				if (Gdx.input.isKeyPressed(Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Keys.SHIFT_RIGHT)) {
//					text += ("" + Keyboard.getEventCharacter()).trim();
//				} else {
//					text +=  ("" + Keyboard.getEventCharacter()).toLowerCase().trim();
//				}
//				if (Keyboard.getEventKey() == Keyboard.KEY_SPACE)
//					text += " ";
//				
//			}
			if (text.length() + 1 <= maxChars) {
				if (c != '/' && c != '\\')
				if (Gdx.input.isKeyPressed(Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Keys.SHIFT_RIGHT)) {
					text += ("" + c).trim();
				} else {
					text +=  ("" + c).toLowerCase().trim();
				}
				if (Gdx.input.isKeyJustPressed(Keys.SPACE))
					text += " ";
				
			}
						
//					}
//				}
		}
			
//		}
		
	}
}

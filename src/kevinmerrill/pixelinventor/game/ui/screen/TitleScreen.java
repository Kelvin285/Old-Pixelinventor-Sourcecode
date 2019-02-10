package kevinmerrill.pixelinventor.game.ui.screen;

import java.util.List;
import java.util.ArrayList;

import kevinmerrill.pixelinventor.game.PixelInventor;
import kevinmerrill.pixelinventor.game.ui.TextButton;
import kevinmerrill.pixelinventor.resources.Input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class TitleScreen extends ScreenBase {
			
	public TitleScreen() {
		
		buttons.add(new TextButton(400 - 12 + 32, 50, 88, 40, "Quit") {
			@Override
			public void onClicked() {
				Gdx.app.exit();
			}
			
			@Override
			public void updateOffset() {

				offsX = 17;
				offsY = 31;
			}
		});
		
		buttons.add(new TextButton(400 - 32 + 32, 115, 128, 40, "Options") {
			@Override
			public void onClicked() {
				
			}
			
			@Override
			public void updateOffset() {
				
				offsX = 17;
				offsY = 31;
			}
		});
		
		buttons.add(new TextButton(400 - 53 + 32, 115 + 65, 168, 40, "Multiplayer") {
			@Override
			public void onClicked() {
				PixelInventor.currentGuiScreen = IScreen.SERVER_SELECT;
				((ServerSelect)IScreen.SERVER_SELECT).refresh();
			}
			
			@Override
			public void updateOffset() {
				offsX = 20;
				offsY = 31;
			}
		});
		
		buttons.add(new TextButton(400 - 64 + 32, 115 + 65 * 2, 192, 40, "Singleplayer") {
			@Override
			public void onClicked() {
				PixelInventor.currentGuiScreen = IScreen.WORLD_SELECT;
				((WorldSelect)IScreen.WORLD_SELECT).refresh();
			}
			
			@Override
			public void updateOffset() {
				offsX = 20;
				offsY = 31;
			}
		});
		
	}
	
	public void update(SpriteBatch batch) {
		for (TextButton button : buttons) {
			button.update(batch);
		}
	}
}

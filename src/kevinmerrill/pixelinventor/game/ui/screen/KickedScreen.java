package kevinmerrill.pixelinventor.game.ui.screen;

import java.util.List;
import java.util.ArrayList;

import kevinmerrill.pixelinventor.game.PixelInventor;
import kevinmerrill.pixelinventor.game.ui.TextButton;
import kevinmerrill.pixelinventor.resources.Input;
import kevinmerrill.pixelinventor.resources.Resources;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class KickedScreen extends ScreenBase {
			
	public static String kickedText = "Kicked from the server!";
	public static String reason;
	
	public KickedScreen() {
		
		buttons.add(new TextButton(400 - 12 + 32 - (88 / 2), 50, 88, 40, "Back") {
			@Override
			public void onClicked() {
				PixelInventor.currentGuiScreen = IScreen.SERVER_SELECT;
				((ServerSelect)IScreen.SERVER_SELECT).refresh();
			}
			
			@Override
			public void updateOffset() {

				offsX = 17;
				offsY = 31;
			}
		});
		
	}
	
	public void update(SpriteBatch batch) {
		
		for (TextButton button : buttons) {
			button.update(batch);
		}
		buttons.get(0).x = 400;
		Resources.font.draw(batch, kickedText, 200, 250);
		if (reason.isEmpty() == false)
		Resources.font.draw(batch, "reason: " + reason, 200, 220);
	}
}

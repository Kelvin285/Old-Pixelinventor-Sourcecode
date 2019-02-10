package kevinmerrill.pixelinventor.game.ui.screen;

import java.util.ArrayList;
import java.util.List;

import kevinmerrill.pixelinventor.game.ui.TextButton;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public abstract class ScreenBase implements IScreen {
	protected List<TextButton> buttons = new ArrayList<TextButton>();
}

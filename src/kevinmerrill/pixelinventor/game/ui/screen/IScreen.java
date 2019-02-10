package kevinmerrill.pixelinventor.game.ui.screen;

import java.util.ArrayList;
import java.util.List;

import kevinmerrill.pixelinventor.game.ui.TextButton;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public interface IScreen {
	public static IScreen TITLE = new TitleScreen();
	public static IScreen WORLD_SELECT = new WorldSelect();
	public static IScreen WORLD_CREATE = new WorldCreate();
	public static IScreen SERVER_SELECT = new ServerSelect();
	public static IScreen SERVER_CREATE = new ServerCreate();
	public static IScreen SERVER_EDIT = new ServerEdit();
	public static IScreen KICKED = new KickedScreen();
	
	public abstract void update(SpriteBatch batch);
	
	
}

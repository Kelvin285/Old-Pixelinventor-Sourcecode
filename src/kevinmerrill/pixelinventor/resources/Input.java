package kevinmerrill.pixelinventor.resources;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import com.badlogic.gdx.Gdx;

public class Input {
	private static boolean leftClicked;
	private static boolean rightClicked;
	
	public static boolean leftClick;
	public static boolean rightClick;
	
	private static boolean[] keysDown = new boolean[Keyboard.getKeyCount()];
		
	
	public static final int MOUSE_LEFT = 0;
	public static final int MOUSE_RIGHT = 1;
	
	public static final int NO_KEY = -1, NO_BUTTON = -1;
	
	
	public static int LEFT = Keyboard.KEY_A;
	public static int RIGHT = Keyboard.KEY_D;
	public static int UP = Keyboard.KEY_W;
	public static int DOWN = Keyboard.KEY_S;
	public static int JUMP = Keyboard.KEY_SPACE;
	public static int MENU = Keyboard.KEY_ESCAPE;
	
	public static int[] ATTACK = {NO_KEY, MOUSE_LEFT};
	public static int[] USE_ITEM = {NO_KEY, MOUSE_RIGHT};
	
	public static boolean isButtonPressed(int button) {
		if (button == NO_BUTTON)
			return false;
		if (button == 0) {
			return isLeftClicked();
		}
		return isRightClicked();
	}
	
	public static boolean isLeftClicked() {
		if (Mouse.isButtonDown(0)) {
			if (leftClicked == false) {
				leftClicked = true;
				leftClick = true;
				return true;
			}
			leftClicked = true;
		} else {
			leftClicked = false;
		}
		return false;
	}
	
	public static boolean isRightClicked() {
		if (Mouse.isButtonDown(1)) {

			if (rightClicked == false) {
				rightClicked = true;
				rightClick = true;
				return true;
			}
			rightClicked = true;
			
		} else {
			rightClicked = false;
		}
		return false;
	}
	
	public static boolean isKeyJustPressed(int key) {
		
		if (key == NO_KEY)
			return false;
		try {
			if (Keyboard.isKeyDown(key)) {
				if (keysDown[key] == false) {
					keysDown[key] = true;
					return true;
				}
				keysDown[key] = true;
			} else {
				keysDown[key] = false;
			}
		}catch (Exception e){}
		return false;
	}
}

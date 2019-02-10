package kevinmerrill.pixelinventor.main;


import kevinmerrill.pixelinventor.game.PixelInventor;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		System.setProperty("org.lwjgl.opengl.Display.allowSoftwareOpenGL", "true");
		config.width = 1920 / 2;
		config.height = 1080 / 2;
		config.foregroundFPS = 60;
		config.backgroundFPS = 60;
		//config.fullscreen = true;
		config.title = "Pixel Inventor v1.0";
		config.vSyncEnabled = false;
		config.useGL30 = false;
		config.allowSoftwareMode = false;
		
		new LwjglApplication(new PixelInventor(), config);
	}
}

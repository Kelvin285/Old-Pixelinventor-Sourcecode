package kevinmerrill.pixelinventor.game.ui.screen;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import kevinmerrill.pixelinventor.game.PixelInventor;
import kevinmerrill.pixelinventor.game.ui.TextButton;
import kevinmerrill.pixelinventor.game.ui.TextInput;
import kevinmerrill.pixelinventor.game.world.WorldSettings;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class WorldCreate extends ScreenBase {
	
	private String worldName = "";
	
	private boolean canMake = true;
	
	private WorldSettings settings;
	
	public WorldCreate() {
		
		refresh();
	}
	
	public void refresh() {
		worldName = "";
		settings = new WorldSettings();
		buttons.clear();
		
		buttons.add(new TextInput(400 - 12 + 32 - 88, 115, 315, 40, "World Name") {
			@Override
			public void updateOffset() {
				
				width = 315;
				offsX = 17;
				offsY = 31;
				maxChars = 23;
				worldName = text;
			}
		});
		
		buttons.add(new TextButton(400 - 12 + 32 - (88 / 2) + 88 * 1.5f, 50, 97, 40, "Make") {
			@Override
			public void onClicked() {
				
				
				if (worldName.isEmpty() == true) {
					if (canMake == true) {
						canMake = false;
						buttons.add(new TextButton(400 - 12 + 32 - 88, 115, 315, 40, "Name cannot be blank") {
							@Override
							public void onClicked() {
								canMake = true;
								buttons.remove(this);
							}
							
							@Override
							public void updateOffset() {
								offsX = 17;
								offsY = 31;
							}
						});
					}
				} else {
					/**
					 * TODO: Create world and save to file
					 */
					if (new File("saves/worlds/" + worldName).exists()) {
						if (canMake == true) {
							canMake = false;
							buttons.add(new TextButton(400 - 12 + 32 - 88, 115, 315, 40, "World already exists") {
								@Override
								public void onClicked() {
									canMake = true;
									buttons.remove(this);
								}
								
								@Override
								public void updateOffset() {
									offsX = 17;
									offsY = 31;
								}
							});
						}
					} else {
						File world = new File("saves/worlds/" + worldName + "/");
						world.mkdirs();
						
						File setFile = new File("saves/worlds/" + worldName + "/settings.s");
						try {
							setFile.createNewFile();
						} catch (IOException e) {
							e.printStackTrace();
						}
						
						try {
							FileOutputStream fos = new FileOutputStream(setFile);
							ObjectOutputStream out = new ObjectOutputStream(fos);
							out.writeObject(settings);
							out.close();
						}catch (Exception e) {}
						
						PixelInventor.currentGuiScreen = IScreen.WORLD_SELECT;
						((WorldSelect)IScreen.WORLD_SELECT).refresh();
					}
				}
				
			}
			
			@Override
			public void updateOffset() {
				
				offsX = 17;
				offsY = 31;
			}
		});
		
		buttons.add(new TextButton(400 - 12 + 32 - (88 / 2), 50, 88, 40, "Back") {
			@Override
			public void onClicked() {
				PixelInventor.currentGuiScreen = IScreen.WORLD_SELECT;
				((WorldSelect)IScreen.WORLD_SELECT).refresh();
			}
			
			@Override
			public void updateOffset() {

				offsX = 17;
				offsY = 31;
			}
		});
		
		
		File file = new File("saves/worlds/");
		if (file.isDirectory() == false) {
			file.mkdirs();
		}
		List<String> worlds = new ArrayList<String>();
		String[] str = file.list();
		for (String s : str) {
			System.out.println(s);
		}
	}
	
	public void update(SpriteBatch batch) {
		for (int i = 0; i < buttons.size(); i++) {
			TextButton button = buttons.get(i);
			if (button != null)
				button.update(batch);
		}
	}
}

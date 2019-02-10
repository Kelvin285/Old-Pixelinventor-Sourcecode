package kevinmerrill.pixelinventor.game.ui.screen;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

import kevinmerrill.pixelinventor.game.PixelInventor;
import kevinmerrill.pixelinventor.game.entity.player.EntityPlayer;
import kevinmerrill.pixelinventor.game.ui.TextButton;
import kevinmerrill.pixelinventor.game.world.World;
import kevinmerrill.pixelinventor.game.world.WorldSettings;
import kevinmerrill.pixelinventor.game.world.chunk.Chunk;
import kevinmerrill.pixelinventor.resources.ArrayMapHolder;
import kevinmerrill.pixelinventor.resources.Resources;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class WorldSelect extends ScreenBase {
	
	private boolean delete = false;
	
	private int showing = 3;
	
	private List<TextButton> buttons2 = new ArrayList<TextButton>();
	
	public WorldSelect() {
		
		refresh();
	}
	
	public void refresh() {
		buttons.clear();
		buttons2.clear();
		
		buttons.add(new TextButton(400 - 12 + 32, 50, 88, 40, "Back") {
			@Override
			public void onClicked() {
				PixelInventor.currentGuiScreen = IScreen.TITLE;
			}
			
			@Override
			public void updateOffset() {
				
				offsX = 17;
				offsY = 31;
			}
		});
		
		
		
		buttons.add(new TextButton(400 - 12 + 32 + 88 * 1.3f, 50, 88, 40, "New") {
			@Override
			public void onClicked() {
				PixelInventor.currentGuiScreen = IScreen.WORLD_CREATE;
				((WorldCreate)IScreen.WORLD_CREATE).refresh();
			}
			
			@Override
			public void updateOffset() {
				offsX = 17;
				offsY = 31;
			}
		});
		
		if (delete == false) {
			buttons.add(new TextButton(400 - 12 + 32 - 88 * 1.5f, 50, 104, 40, "Delete") {
				@Override
				public void onClicked() {
					delete = true;
					refresh();
				}
				
				@Override
				public void updateOffset() {
					
					offsX = 12;
					offsY = 31;
				}
			});
		} else {
			buttons.add(new TextButton(400 - 12 + 32 - 88 * 1.5f, 50, 104, 40, "Cancel") {
				@Override
				public void onClicked() {
					delete = false;
					refresh();
				}
				
				@Override
				public void updateOffset() {
					
					offsX = 12;
					offsY = 31;
				}
			});
		}
		
		
		File file = new File("saves/worlds/");
		if (file.isDirectory() == false) {
			file.mkdirs();
		}
		List<String> worlds = new ArrayList<String>();
		String[] str = file.list();
		for (int i = 0; i < str.length; i++) {
			buttons.add(new TextButton(400 - 12 + 32 - 120, 115 + (115 - 50) * (i % 4), 315, 40, str[i]) {
				
				void deleteDir(File file) {
				    File[] contents = file.listFiles();
				    if (contents != null) {
				        for (File f : contents) {
				            deleteDir(f);
				        }
				    }
				    file.delete();
				}
				
				@Override
				public void onClicked() {
					if (delete == false) {
						System.out.println("loading world " + text);
						/***
						 * TODO: LOAD / GENERATE WORLD FROM FILE
						 */
						File setFile = new File("saves/worlds/" + text + "/settings.s");
						setFile.setReadable(true);
						WorldSettings settings = null;
						try {
							FileInputStream fis = new FileInputStream(setFile);
							ObjectInputStream in = new ObjectInputStream(fis);
							settings = (WorldSettings)in.readObject();
							in.close();
						}catch (Exception e) {
							e.printStackTrace();
						}
						
						PixelInventor.world = new World(settings.generator);
						Resources.currentWorldName = text;
						World w = Chunk.loadWorld(false);
						System.out.println("WORLD: " + w);
						if (w != null) {
							PixelInventor.world = w;
						}
						PixelInventor.player = new EntityPlayer(0, 64 * 7);
						
						ArrayMapHolder.entities.add(PixelInventor.player);
						
						Resources.inWorld = true;
						
						PixelInventor.currentGuiScreen = IScreen.TITLE;
						
					} else {
						System.out.println("deleting world " + text);
						File file = new File("saves/worlds/" + text + "/");
						if (file.exists() == true || file.isDirectory() == true) {
							System.out.println("currently deleting");
							deleteDir(file);
						}
						System.out.println("deleted file");
						delete = false;
						refresh();
						
					}
				}
				
				@Override
				public void updateOffset() {
					offsX = 12;
					offsY = 31;
				}
			});
		}
		
		
		
		if (buttons.size() - 3 > showing - 3 + 4) {

			buttons2.add(new TextButton(400 - 12 + 32 + 88 * 1.3f, 50, 97, 40, "Next") {
				@Override
				public void onClicked() {
					showing += 4;
					refresh();
				}
				
				@Override
				public void updateOffset() {
					
					x = 400 -12 + 32 + 88 * 2.5f;
					y = 50 + (115 - 50) * 2.5f;
					offsX = 17;
					offsY = 31;
				}
			});
			
		}
		
		if (showing - 3 > 0) {
			buttons2.add(new TextButton(400 - 12 + 32 + 88 * 1.3f, 50, 97, 40, "Prev") {
				@Override
				public void onClicked() {
					showing -= 4;
					refresh();
				}
				
				@Override
				public void updateOffset() {
					x = 400 -12 + 32 - 88 * 2.8f;
					y = 50 + (115 - 50) * 2.5f;
					offsX = 17;
					offsY = 31;
				}
			});
		}
	}
	
	public void update(SpriteBatch batch) {

		if (showing >= buttons.size()) {
			showing -= 4;
		}
		if (showing < 3)
			showing = 3;
		if (buttons.size() - 3 >= showing - 3 + 4) {
			
			if (Gdx.input.isKeyJustPressed(Keys.RIGHT)) {
				showing += 4;
			}
			
		}
		if (showing - 3 > 0) {
			if (Gdx.input.isKeyJustPressed(Keys.LEFT)) {
				showing -= 4;
			}
		}
		
		for (int i = 0; i < buttons2.size(); i++) {
			
			TextButton button = buttons2.get(i);
			if (button != null)
				button.update(batch);
		}
		
		for (int i = 0; i < 3; i++) {
			
			TextButton button = buttons.get(i);
			if (button != null)
				button.update(batch);
		}
		for (int i = showing; i < showing + 4; i++) {
			if (i < buttons.size() && i >= 0) {
				TextButton button = buttons.get(i);
				if (button != null)
					button.update(batch);
			} else {
				break;
			}
		}
	}
}

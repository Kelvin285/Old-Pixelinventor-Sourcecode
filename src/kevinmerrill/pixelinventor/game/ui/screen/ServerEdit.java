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
import kevinmerrill.pixelinventor.multiplayer.ServerSettings;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class ServerEdit extends ScreenBase {
	
	private String serverName = "";
	private String port = "";
	private String ip = "";
	
	public String defName = "";
	public String defPort = "";
	public String defIp = "";
	
	private boolean canMake = true;
	
	private ServerSettings settings;
	
	public ServerEdit() {
		
		refresh();
	}
	
	public void refresh() {
		serverName = "";
		port = "";
		ip = "";
		buttons.clear();
		
		buttons.add(new TextInput(400 - 12 + 32 - 88, 115 + (115 - 50) * 2, 315, 40, "Server IP", defIp) {
			
			@Override
			public void updateOffset() {
				
				width = 315;
				offsX = 17;
				offsY = 31;
				maxChars = 23;
				ip = text;
			}
		});
		
		buttons.add(new TextInput(400 - 12 + 32 - 88, 115 + (115 - 50), 315, 40, "Server Port", defPort) {
			@Override
			public void updateOffset() {
				
				width = 315;
				offsX = 17;
				offsY = 31;
				maxChars = 23;
				port = text;
			}
		});
		
		buttons.add(new TextInput(400 - 12 + 32 - 88, 115, 315, 40, "Server Name", defName) {
			@Override
			public void updateOffset() {
				
				width = 315;
				offsX = 17;
				offsY = 31;
				maxChars = 23;
				serverName = text;
			}
		});
		
		buttons.add(new TextButton(400 - 12 + 32 - (88 / 2) + 88 * 1.5f, 50, 97, 40, "Save") {
			
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
				
				
				if (serverName.isEmpty() == true || port.isEmpty() == true || ip.isEmpty() == true) {
					if (canMake == true) {
						canMake = false;
						if (serverName.isEmpty() == true)
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
						if (port.isEmpty() == true)
							buttons.add(new TextButton(400 - 12 + 32 - 88, 115 + (115 - 50), 315, 40, "Port cannot be blank") {
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
						if (ip.isEmpty() == true)
							buttons.add(new TextButton(400 - 12 + 32 - 88, 115 + (115 - 50), 315, 40, "IP cannot be blank") {
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
					if (new File("saves/serverlist/" + serverName).exists()) {
						System.out.println("deleting world " + serverName);
						File file = new File("saves/serverlist/" + serverName + "/");
						if (file.exists() == true || file.isDirectory() == true) {
							System.out.println("currently deleting");
							deleteDir(file);
						}
					} 
					{
						settings = new ServerSettings(Integer.parseInt(port), ip, serverName);
						File world = new File("saves/serverlist/" + serverName + "/");
						world.mkdirs();
						
						File setFile = new File("saves/serverlist/" + serverName + "/info.txt");
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
						
						PixelInventor.currentGuiScreen = IScreen.SERVER_SELECT;
						((ServerSelect)IScreen.SERVER_SELECT).refresh();
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
				PixelInventor.currentGuiScreen = IScreen.SERVER_SELECT;
				((ServerSelect)IScreen.SERVER_SELECT).refresh();
			}
			
			@Override
			public void updateOffset() {

				offsX = 17;
				offsY = 31;
			}
		});
		
		
		File file = new File("saves/serverlist/");
		if (file.isDirectory() == false) {
			file.mkdirs();
		}
		List<String> worlds = new ArrayList<String>();
		String[] str = file.list();
		for (String s : str) {
			System.out.println(s);
		}
		defIp = "";
		defPort = "";
		defName = "";
	}
	
	public void update(SpriteBatch batch) {
		for (int i = 0; i < buttons.size(); i++) {
			TextButton button = buttons.get(i);
			if (button != null)
				button.update(batch);
		}
	}
}

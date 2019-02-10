package kevinmerrill.pixelinventor.multiplayer.server;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.Scanner;

import kevinmerrill.pixelinventor.game.world.biome.BiomeSurface;
import kevinmerrill.pixelinventor.game.world.chunk.Chunk;
import kevinmerrill.pixelinventor.game.world.gen.WorldGeneratorMain;
import kevinmerrill.pixelinventor.multiplayer.client.entities.EntityPlayerMP;
import kevinmerrill.pixelinventor.multiplayer.client.packet.PacketPlayer;
import kevinmerrill.pixelinventor.resources.ArrayMapHolder;
import kevinmerrill.pixelinventor.resources.Resources;

public class NetworkServer extends Thread {

	private DatagramSocket socket;
	
	private int port;
	
	private ArrayList<EntityPlayerMP> clients = new ArrayList<EntityPlayerMP>();
	
	private WorldServer worldServer;
	
	public static String currentWorldName = "serverworld";
	
	private HashMap<String, InetAddress> bannedPlayers = new HashMap<String, InetAddress>();
	
	public NetworkServer(int port) {
		
		worldServer = new WorldServer(new WorldGeneratorMain(BiomeSurface.GRASSLAND));
		
		this.port = port;
		try {
			this.socket = new DatagramSocket(port);
		}catch (Exception e) {
			e.printStackTrace();
		}
		new Thread(this).start();
		new Thread() {
			public void run() {
				while (true) {
					worldServer.update(clients);
				}
			}
		}.start();
		System.out.println("Server started");
	}
	
	public void run() {
		while (true) {
			byte[] data = new byte[1024];
			DatagramPacket packet = new DatagramPacket(data, data.length);
			try {
				socket.receive(packet);
			}catch (Exception e) {
				e.printStackTrace();
			}
			String message = new String(packet.getData()).trim();
			String[] msgdata = message.split("\n");
			
				if(msgdata[0].equalsIgnoreCase("ping"))
				{					
					sendData("ping".getBytes(), packet.getAddress(), packet.getPort());
				}
				if(msgdata[0].equalsIgnoreCase("connect"))
				{
					
					File bannedIPs = new File("banned.txt");
					if (bannedIPs.exists() == false) {
						try {
							bannedIPs.createNewFile();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					
					boolean banned = false;
					
					if (msgdata.length > 1) {
						String username = msgdata[1];
						
						try {
							Scanner scanner = new Scanner(bannedIPs);
							while (scanner.hasNext()) {
								String s = scanner.nextLine().trim();
								if (s.contains(packet.getAddress().getHostAddress())) {
									banned = true;
									break;
								}
							}
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						}
						
					}
					
					if (msgdata.length > 1 && banned == false) {
						System.out.println("player trying to connect: " + packet.getAddress());
						sendData("connect".getBytes(), packet.getAddress(), packet.getPort());

						System.out.println("adding client [" + msgdata[1] + "] to connected addresses");
						String username = msgdata[1];
						
						
						
						
						EntityPlayerMP player = new EntityPlayerMP(0, 0);
						player.name = username;
						player.address = packet.getAddress();
						player.port = packet.getPort();
//						
						boolean alreadyConnected = false;
						for (EntityPlayerMP p : this.clients) {
							if (player.name.equalsIgnoreCase(p.name) == true) {
								if (p.address == null) {
									p.address = player.address;
								}
								alreadyConnected = true;
							} else {
								sendData("connect".getBytes(), packet.getAddress(), packet.getPort());
								
							}
						}
						for (int i = 0; i < this.clients.size(); i++) {
							sendData(("playerconnect"+player.name).getBytes(), this.clients.get(i).address, this.clients.get(i).port);
						}
						clients.add(player);
						sendData("connect".getBytes(), packet.getAddress(), packet.getPort());
						
						
						
						
						
						
					}
					if (banned == true && msgdata.length > 1) 
					{
						String username = msgdata[1];

						EntityPlayerMP player = new EntityPlayerMP(0, 0);
						player.name = username;
						player.address = packet.getAddress();
						player.port = packet.getPort();
						clients.add(player);
						this.processCommand("ban " + username);
					}
				}
				if(msgdata[0].equalsIgnoreCase("disconnect"))
				{
					if (msgdata.length > 1) {
						String username = msgdata[1];
	//					int ii = -1;
	//					for (int i = 0; i < clients.size(); i++) {
	//						if (clients.get(i).name.equals(msgdata[1]))
	//						{
	//							username = clients.get(i).name;
	//							ii = i;
	//							break;
	//						}
	//					}
	//					
						System.out.println("client [" + username + "] disconnected");
	//
						for (int i = 0; i < clients.size(); i++) {
							sendData(("disconnect\n"+username).getBytes(), clients.get(i).address, clients.get(i).port);
						}
					} else {
						System.out.println("A player has left the server.");
						for (int i = 0; i < clients.size(); i++) {
							sendData(("disconnect\n"+"disconnect").getBytes(), clients.get(i).address, clients.get(i).port);
						}
					}
//					clients.clear();

//					if (ii != -1) {
//						clients.remove(ii);
//					}
				}
				
				if(msgdata[0].equalsIgnoreCase("player"))
				{
					
					
					String username = msgdata[5];
					boolean connected = false;
					
					EntityPlayerMP p = new EntityPlayerMP(0, 0);
					
					for (int i = 0; i < clients.size(); i++) {
						if (clients.get(i).address != null)
						if (clients.get(i).address.getHostAddress().equals(packet.getAddress().getHostAddress()) == true) {
							connected = true;
							p = clients.get(i);
							break;
						}
					}
					
					
					PacketPlayer pack = new PacketPlayer(msgdata);
					p.posX = pack.x;
					p.posY = pack.y;
					p.currentFrame = pack.frame;
					p.right_facing = pack.right_facing;
					p.name = pack.username;
					p.address = packet.getAddress();
//					System.out.println(pack.username + ", " + username);
					if (connected == false) {
						clients.add(p);
					}
					
					PacketPlayer player = new PacketPlayer(msgdata);
					boolean hasPlayer = false;
					for (int i = 0; i < clients.size(); i++) {
						if (player.username.equals(clients.get(i).name) == false)
						sendData(data, clients.get(i).address, clients.get(i).port);
						
					}
					
				}
				if (msgdata[0].equalsIgnoreCase("reqchunk")) {

					long chunkX = Long.parseLong(msgdata[1]);
					long chunkY = Long.parseLong(msgdata[2]);
					
					
					Chunk chunk = ArrayMapHolder.loadedChunks.get(chunkX + chunkY * Resources.maxChunks);
					

//					worldServer.update(clients);
					if (chunk == null) {	
						chunk = worldServer.genChunk(chunkX, chunkY);
					}
					String blocks = Arrays.toString(chunk.getTiles()).replace('[', '\n').replace("]","\n").replace(", ", "\n");
					String water = Arrays.toString(chunk.waterPressure).replace('[', '\n').replace("]","\n").replace(", ", "\n");
					String bg = Arrays.toString(chunk.bgTiles).replace('[', '\n').replace("]","").replace(", ", "\n");

					
//					chunk.sendServerData(packet.getAddress(), packet.getPort());
					sendData(("chunk\n"+chunkX+"\n"+chunkY+blocks+water+bg).getBytes(), packet.getAddress(), packet.getPort());
					
//
//					
					
					
				}
				
				if (msgdata[0].equals("tilerequest")) {
					long x = Long.parseLong(msgdata[1]);
					long y = Long.parseLong(msgdata[2]);
					int id = worldServer.getTile(x, y);
					String tile = ("tilemod\n"+x+"\n"+y+"\n"+id);
//					for (int i = 0; i < clients.size(); i++) {
						sendData(tile.getBytes(), packet.getAddress(), packet.getPort());
//					}
				}
				
				if (msgdata[0].equals("waterrequest")) {
					long x = Long.parseLong(msgdata[1]);
					long y = Long.parseLong(msgdata[2]);
					int id = worldServer.getWater(x, y);
					String tile = ("watermod\n"+x+"\n"+y+"\n"+id);
//					for (int i = 0; i < clients.size(); i++) {
						sendData(tile.getBytes(), packet.getAddress(), packet.getPort());
//					}
				}
				
				if (msgdata[0].equals("bgrequest")) {
					long x = Long.parseLong(msgdata[1]);
					long y = Long.parseLong(msgdata[2]);
					int id = worldServer.getTileBG(x, y);
					String tile = ("bgmod\n"+x+"\n"+y+"\n"+id);
//					for (int i = 0; i < clients.size(); i++) {
						sendData(tile.getBytes(), packet.getAddress(), packet.getPort());
//					}
				}
				
				if (msgdata[0].equals("bgmod")) {
					long x = Long.parseLong(msgdata[1]);
					long y = Long.parseLong(msgdata[2]);
					int id = Integer.parseInt(msgdata[3]);
						
						worldServer.setTileBG(x, y, id);
						
					for (int i = 0; i < clients.size(); i++) {
						sendData(data, clients.get(i).address, clients.get(i).port);
					}
				}
				
				if (msgdata[0].equals("tilemod")) {
					long x = Long.parseLong(msgdata[1]);
					long y = Long.parseLong(msgdata[2]);
					int id = Integer.parseInt(msgdata[3]);
						
						worldServer.setTile(x, y, id);
						
					for (int i = 0; i < clients.size(); i++) {
						sendData(data, clients.get(i).address, clients.get(i).port);
					}
				}
				
				if (msgdata[0].equals("watermod")) {
					long x = Long.parseLong(msgdata[1]);
					long y = Long.parseLong(msgdata[2]);
					int id = Integer.parseInt(msgdata[3]);
						
						worldServer.setWater(x, y, id);
						
					for (int i = 0; i < clients.size(); i++) {
						sendData(data, clients.get(i).address, clients.get(i).port);
					}
				}
			
//			System.out.println(getClients().size);
//			
//			System.out.println("["+ packet.getAddress().getHostAddress() + "] " + message);
		}
	}
	
    /** Read the object from Base64 string. */
   private static Object fromString( String s ) throws IOException ,
                                                       ClassNotFoundException {
        byte [] data = Base64.getDecoder().decode( s );
        ObjectInputStream ois = new ObjectInputStream( 
                                        new ByteArrayInputStream(  data ) );
        Object o  = ois.readObject();
        ois.close();
        return o;
   }

    /** Write the object to a Base64 string. */
    private static String toString( Serializable o ) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream( baos );
        oos.writeObject( o );
        oos.close();
        return Base64.getEncoder().encodeToString(baos.toByteArray()); 
    }
	
	public void sendData(byte[] data, InetAddress ipAddress, int port) {
		DatagramPacket packet = new DatagramPacket(data, data.length, ipAddress, port);
		try {
			socket.send(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void processCommand(String text) {
		if (text.toCharArray()[0] == '/') {
			text = text.substring(1);
		}
		String[] split = text.split(" ");
		
		
		
		switch(split[0]) {
		
			case "help": {
				System.out.println(
						  "\nSHOWING A LIST OF COMMANDS\n"+
						  "==========================\n"+
						  "HELP -- Displays a list of commands available\n"
						+ "LIST -- Lists all of the players that are currently online\n"
						+ "KICK [player] <reason> -- Kicks a player off of the server\n"
						+ "BAN [player] <reason> -- Bans a player's IP from the server\n"
						+ "TP [player] [x] [y] -- Teleports a player to a location\n"
						+ "TP [player1] [player2] -- Teleports player1 to player2\n"
						+ "OP [player] -- Gives a player operating powers\n"
						+ "DEOP [player] -- Removes operating powers from a player"
						
						);
				break;
			
			}	
			
			case "kick": {
				String reason = "";
				if (split.length > 2) {
					for (int i = 2; i < split.length; i++) {
						reason += split[i] + " ";
								
					}
				}
				
				if (split.length > 1) {
					for (int i = 0; i < clients.size(); i++) {
						if (clients.get(i).name.equals(split[1]) || clients.get(i).address.getHostAddress().equals(split[1])) {
							System.out.println("\nkicked " + clients.get(i).name + " from the game.");
							if (reason.isEmpty() == false)
								sendData(("kick\n"+reason).getBytes(), clients.get(i).address, clients.get(i).port);
							else
								sendData(("kick").getBytes(), clients.get(i).address, clients.get(i).port);
							clients.remove(i);
							break;
						}
					}
				} else {
					System.out.println("\nThe player name entered is either invalid or corresponds to a player that is not online.");
				}
				clients.clear();
				break;
			}
			case "unban": {
				File bannedIPs = new File("banned.txt");
				if (bannedIPs.exists() == false) {
					try {
						bannedIPs.createNewFile();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				if (split.length > 1) {
					boolean invalid = true;
					String ips = "";
					try {
						Scanner scanner = new Scanner(bannedIPs);
						while (scanner.hasNext()) {
							if (scanner.nextLine().equals(split[1])) {
								invalid = false;
								continue;
							}
							ips += scanner.nextLine()+"\n";
						}
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}
					if (invalid == true) {
						System.out.println("\nIP address entered is either invalid or not on the banned players list.");
					} else {
						System.out.println("\nSuccessfully unbanned IP " + split[1]);
					}	
					try {
						bannedIPs.delete();
						bannedIPs.createNewFile();
						FileWriter writer = new FileWriter(bannedIPs);
						writer.write(ips);
						writer.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
					
				}
				
			}
			case "ban": {
				File bannedIPs = new File("banned.txt");
				if (bannedIPs.exists() == false) {
					try {
						bannedIPs.createNewFile();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				String reason = "";
				if (split.length > 2) {
					for (int i = 2; i < split.length; i++) {
						reason += split[i] + " ";
								
					}
				}
				
				if (split.length > 1) {
					for (int i = 0; i < clients.size(); i++) {
						if (clients.get(i).name.equals(split[1]) || clients.get(i).address.getHostAddress().equals(split[1])) {
							System.out.println("\nbanned " + clients.get(i).name + " from the game.");
							try {
								FileWriter writer = new FileWriter(bannedIPs);
								writer.write(clients.get(i).address.getHostAddress());
								writer.close();
							} catch (IOException e) {
								e.printStackTrace();
							}
							
							
							if (reason.isEmpty() == false)
								sendData(("ban\n"+reason).getBytes(), clients.get(i).address, clients.get(i).port);
							else
								sendData(("ban").getBytes(), clients.get(i).address, clients.get(i).port);
							clients.remove(i);
							break;
						}
					}
				} else {
					System.out.println("\nThe player name entered is either invalid or corresponds to a player that is not online.");
				}
				clients.clear();
				break;
			}
			
			case "list": {
				if (clients.size() > 1) {
					System.out.println("\nShowing a list of all online players ("+clients.size()+")");
					for (int i = 0; i < clients.size(); i++) {
						System.out.print(clients.get(i).name + ", ");
					}
					System.out.print("\n");
				} else {
					if (clients.size() == 1) {
						System.out.println("\nShowing a list of all online players ("+clients.size()+")");
						System.out.println(clients.get(0).name + " is the only player online.");
					} else {
						System.out.println("\nShowing a list of all online players ("+clients.size()+")\nThere are no players online at this time.");
					}
				}
				break;
			
			}	
				
			default:
				System.out.println("Invalid command recieved: " + text);
				break;
		}
	}

	public void closeServer() {
		for (int i = 0; i < clients.size(); i++) {
			processCommand("kick " + clients.get(i).name + " server closed.");
		}
	}

}

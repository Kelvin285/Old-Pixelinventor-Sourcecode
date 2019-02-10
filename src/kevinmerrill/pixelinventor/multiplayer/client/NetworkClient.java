package kevinmerrill.pixelinventor.multiplayer.client;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Base64;

import kevinmerrill.pixelinventor.game.PixelInventor;
import kevinmerrill.pixelinventor.game.ui.screen.IScreen;
import kevinmerrill.pixelinventor.game.ui.screen.KickedScreen;
import kevinmerrill.pixelinventor.game.ui.screen.TitleScreen;
import kevinmerrill.pixelinventor.game.world.biome.BiomeSurface;
import kevinmerrill.pixelinventor.game.world.chunk.Chunk;
import kevinmerrill.pixelinventor.multiplayer.client.entities.EntityPlayerMP;
import kevinmerrill.pixelinventor.multiplayer.client.packet.PacketPlayer;
import kevinmerrill.pixelinventor.resources.ArrayMapHolder;
import kevinmerrill.pixelinventor.resources.Resources;

public class NetworkClient extends Thread {

	private InetAddress ipAddress;
	private DatagramSocket socket;
	
	private int port;
	
	
	private long timePinged;
	private long timeSinceLastPing;
	
	private long timeConnectionTried;
	private long timeSinceConnectionTried;
	
	public boolean tryToConnect = false;
	public boolean connected = false;
	
	public String username = "player"+System.currentTimeMillis()%1000;
	
	public ArrayList<EntityPlayerMP> players = new ArrayList<EntityPlayerMP>();
	
	
	public NetworkClient() {
		
	}
	
	public void set(String ipAddress, int port, String username) {
		this.port = port;
		try {
			this.socket = new DatagramSocket();
			this.ipAddress = InetAddress.getByName(ipAddress);
		}catch (Exception e) {
			e.printStackTrace();
		}
		this.username = username;
		pingServer();
		new Thread(this).start();
	}
	
	public NetworkClient(String ipAddress, int port) {
		this.port = port;
		try {
			this.socket = new DatagramSocket();
			this.ipAddress = InetAddress.getByName(ipAddress);
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		pingServer();
		new Thread(this).start();
	}
	
	
	
	
	/**Hey out there to whoever may make hacked clients for this game.  If that's what you're doing I can't really
	 * do anything about it, but try not to spoil the fun of the game for everyone else.  If you want to make the
	 * game boring and less of an experience for yourself do it on your own time.**/
	public void run() {
		while (true) {
			timeSinceLastPing = System.currentTimeMillis() - timePinged;
			byte[] data = new byte[1024];
			DatagramPacket packet = new DatagramPacket(data, data.length);
			try {
				socket.receive(packet);
			}catch (Exception e) {
				e.printStackTrace();
			}
			String message = new String(packet.getData()).trim();
//			System.out.println(message);
			String[] msgdata = message.split("\n");
			if(msgdata[0].equalsIgnoreCase("ping"))
			{
					System.out.println("Reply from server: time=" + timeSinceLastPing + "ms");
				}
			if(msgdata[0].equalsIgnoreCase("connect"))
				{
					tryToConnect = false;
					connected = true;
					System.out.println("Connection to server successful!");
				}
			if(msgdata[0].equalsIgnoreCase("changename"))
				{
					System.out.println("Your username was already in use, and was changed to: " + msgdata[1]);
//					this.username = msgdata[1];
				}
			if(msgdata[0].equalsIgnoreCase("kick"))
				{
					
					sendData("disconnect".getBytes());
					leaveServer();
					KickedScreen.kickedText = "You have been kicked from the server!";
					KickedScreen.reason = "";
					if (msgdata.length > 1) {
						KickedScreen.reason=msgdata[1];
					}
					PixelInventor.currentGuiScreen = IScreen.KICKED;
				}
			if(msgdata[0].equalsIgnoreCase("ban"))
			{
				
				sendData("disconnect".getBytes());
				leaveServer();
				KickedScreen.kickedText = "You have been banned from the server!";
				KickedScreen.reason = "";
				if (msgdata.length > 1) {
					KickedScreen.reason=msgdata[1];
				}
				PixelInventor.currentGuiScreen = IScreen.KICKED;
			}
			if (msgdata[0].equalsIgnoreCase("playerconnect")) {
				System.out.println("adding client [" + msgdata[1] + "] to connected addresses");

				if (msgdata.length > 1) {
					System.out.println("player trying to connect: " + packet.getAddress());

					String username = msgdata[1];
					EntityPlayerMP player = new EntityPlayerMP(0, 0);
					player.name = username;
					player.address = packet.getAddress();
					player.port = packet.getPort();
//					
					boolean alreadyConnected = false;
					for (EntityPlayerMP p : this.players) {
						if (player.name.equalsIgnoreCase(p.name) == true) {
							if (p.address == null) {
								p.address = player.address;
							}
							alreadyConnected = true;
						}
					}
//					
					players.add(player);
					
				}
			}
			if(msgdata[0].equalsIgnoreCase("player"))
				{
//					String username = msgdata[5];
					boolean connected = false;
					
					EntityPlayerMP p = new EntityPlayerMP(0, 0);
					
					for (int i = 0; i < players.size(); i++) {
						if (players.get(i).address != null)
						if (players.get(i).address.getHostAddress().equals(packet.getAddress().getHostAddress()) == true) {
							connected = true;
							p = players.get(i);
							break;
						}
					}
//					
//					
					PacketPlayer pack = new PacketPlayer(msgdata);
					p.posX = pack.x;
					p.posY = pack.y;
					p.currentFrame = pack.frame;
					p.right_facing = pack.right_facing;
					p.name = pack.username;
					p.address = packet.getAddress();
	//				System.out.println(pack.username + ", " + username);
					if (connected == false) {
						players.add(p);
					}
					
//					PacketPlayer player = new PacketPlayer(msgdata);
//					boolean hasPlayer = false;
//					for (int i = 0; i < clients.size(); i++) {
//						if (player.username.equals(clients.get(i).name) == false)
//						sendData(data, clients.get(i).address, clients.get(i).port);
//						
//					}
				}
			
			
			
			
			if(msgdata[0].equalsIgnoreCase("disconnect"))
				{
				String username = msgdata[1];
				
				players.clear();
//				for (int i = 0; i < players.size(); i++) {
//					if (players.get(i).name.equals(username) == true) {
//						players.remove(i);
//						break;
//					}
//				}
				System.out.println("someone has disconnected!");
				}
			
			if (msgdata[0].equalsIgnoreCase("chunk")) {
//				System.out.println("tilemod");
				long x = Long.parseLong(msgdata[1]);
				long y = Long.parseLong(msgdata[2]);
				
				int X = 0;
				int Y = 0;
				try {
				for (int i = 0; i < 16*16; i++) {
					
					Chunk chunk = ArrayMapHolder.loadedChunks.get(x + y * Resources.maxChunks);
					if (chunk == null) {
						chunk = new Chunk(x, y, BiomeSurface.NULL_BIOME, PixelInventor.world.worldGenerator);
						ArrayMapHolder.loadedChunks.put(x + y * Resources.maxChunks, chunk);
					}
					if (chunk != null)
						chunk.tiles[X + Y * 16] = Integer.parseInt(msgdata[i + 3]);
					if (X < 16) {
						X++;
					} else {
						X = 0;
						Y++;
					}
				}
				
				for (int i = 0; i < 16*16; i++) {
					
					Chunk chunk = ArrayMapHolder.loadedChunks.get(x + y * Resources.maxChunks);
					if (chunk == null) {
						chunk = new Chunk(x, y, BiomeSurface.NULL_BIOME, PixelInventor.world.worldGenerator);
						ArrayMapHolder.loadedChunks.put(x + y * Resources.maxChunks, chunk);
					}
					if (chunk != null)
						chunk.waterPressure[X + Y * 16] = Integer.parseInt(msgdata[i + 3 + (16 * 16)]);
					if (X < 16) {
						X++;
					} else {
						X = 0;
						Y++;
					}
				}
				
				for (int i = 0; i < 16*16; i++) {
					
					Chunk chunk = ArrayMapHolder.loadedChunks.get(x + y * Resources.maxChunks);
					if (chunk == null) {
						chunk = new Chunk(x, y, BiomeSurface.NULL_BIOME, PixelInventor.world.worldGenerator);
						ArrayMapHolder.loadedChunks.put(x + y * Resources.maxChunks, chunk);
					}
					if (chunk != null)
						chunk.bgTiles[X + Y * 16] = Integer.parseInt(msgdata[i + 3 + (16 * 16) + (16 * 16)]);
					if (X < 16) {
						X++;
					} else {
						X = 0;
						Y++;
					}
				}
				}catch (Exception e){}
				
			}
			
			
			if (msgdata[0].equalsIgnoreCase("tilemod")) {
//				System.out.println("tilemod");
				long x = Long.parseLong(msgdata[1]);
				long y = Long.parseLong(msgdata[2]);
				int id = Integer.parseInt(msgdata[3]);
				try {
					
					PixelInventor.world.setTile(x, y, id);
					
				}catch (Exception e){}
			}
			
			if (msgdata[0].equalsIgnoreCase("watermod")) {
//				System.out.println("tilemod");
				long x = Long.parseLong(msgdata[1]);
				long y = Long.parseLong(msgdata[2]);
				int id = Integer.parseInt(msgdata[3]);
				try {
					
					PixelInventor.world.setWater(x, y, id);
					
				}catch (Exception e){}
			}
			
			if (msgdata[0].equalsIgnoreCase("bgmod")) {
//				System.out.println("tilemod");
				long x = Long.parseLong(msgdata[1]);
				long y = Long.parseLong(msgdata[2]);
				int id = Integer.parseInt(msgdata[3]);
				try {
					
					PixelInventor.world.setTileBG(x, y, id);
					
				}catch (Exception e){}
			}
			
//			System.out.println("[SERVER] " + message);
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
	
	public void sendData(byte[] data) {
		DatagramPacket packet = new DatagramPacket(data, data.length, ipAddress, port);
		try {
			socket.send(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void connectToServer() {
		pingServer();
		System.out.println("trying to connect to server: 0s");
		sendData(("connect\n"+username).getBytes());
		timeConnectionTried = System.currentTimeMillis() / 1000L;
		timeSinceConnectionTried = 0;
		tryToConnect = true;
		new Thread() {
			public void run() {
				long I = System.currentTimeMillis() / 1000L - timeConnectionTried;
				long J = 0;
				while (tryToConnect == true) {
					
					timeSinceConnectionTried = System.currentTimeMillis() / 1000L - timeConnectionTried;
					
					if (timeSinceConnectionTried > 30) {
						System.err.println("Could not connect to the server: took too long");
						tryToConnect = false;
						connected = false;
						
					}
					
					if (timeSinceConnectionTried > I) {
						I = System.currentTimeMillis() / 1000L - timeConnectionTried;
						J++;
						System.out.println("trying to connect to server: " + (J) + "s");
						sendData(("connect\n" + username).getBytes());
					}
				}
			}
		}.start();
	}
	
	public static void leaveServer() {
		PixelInventor.currentGuiScreen = new TitleScreen();
		PixelInventor.world = null;
		Resources.inWorld = false;
		if (PixelInventor.client != null) {
			PixelInventor.client.connected = false;
			PixelInventor.client.sendData(("disconnect\n"+PixelInventor.client.username).getBytes());

			PixelInventor.client.players.clear();
			PixelInventor.client.tryToConnect = false;
			PixelInventor.client = null;
			
		}
	}
	
	public void pingServer() {
		timePinged = System.currentTimeMillis();
		timeSinceLastPing = System.currentTimeMillis();
		sendData("ping".getBytes());
	}
}

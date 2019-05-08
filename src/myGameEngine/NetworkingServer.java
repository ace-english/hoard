package myGameEngine;

import java.io.IOException;
import ray.networking.IGameConnection.ProtocolType;

public class NetworkingServer{
	private GameServerUDP thisUDPServer;
	public NetworkingServer(int serverPort, String protocol){ 
		try {
			System.out.println("Starting server...");
			thisUDPServer = new GameServerUDP(serverPort);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public static void main(String[] args){ 
		if(args.length > 1){ 
			NetworkingServer app = new NetworkingServer(Integer.parseInt(args[0]), args[1]);
		} 
	} 
}
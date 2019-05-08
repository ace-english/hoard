package myGameEngine;

import java.io.IOException;
import java.net.InetAddress;
import java.util.UUID;

import ray.networking.server.GameConnectionServer;
import ray.networking.server.IClientInfo;

public class GameServerUDP extends GameConnectionServer<UUID> {
	private UUID[] players;
	private int numberOfPlayers;
	final private static int MAX_PLAYERS=2;

	public GameServerUDP(int localPort) throws IOException{ 
		super(localPort, ProtocolType.UDP); 
		System.out.println("Sever established.");
		players=new UUID[MAX_PLAYERS];
		for(int i=0; i<MAX_PLAYERS; i++) {
			players[i]=null;
		}
		numberOfPlayers=0;
	}
	
	public int getNumberOfPlayers() {
		return numberOfPlayers;
	}
	
	@Override
	public void processPacket(Object o, InetAddress senderIP, int sndPort)	{
		String message = (String) o;
		String[] msgTokens = message.split(",");
		if(msgTokens.length > 0)	{
			//System.out.println("Server processing: "+message);
				// case where server receives a JOIN message
				// format: join,localid
			if(msgTokens[0].compareTo("join") == 0)	{ 
				try	{ 
					UUID clientID = UUID.fromString(msgTokens[1]);
					if(numberOfPlayers<MAX_PLAYERS) {
						IClientInfo ci;
						ci = getServerSocket().createClientInfo(senderIP, sndPort);
						addClient(ci, clientID);
						sendJoinedMessage(clientID, true);
						sendWantsDetailsMessages(clientID);
						numberOfPlayers++;
					}
					else {
						System.err.println("Server is full.");
						sendJoinedMessage(clientID, false);
					}
					
				}
				catch (IOException e)	{ 
					e.printStackTrace();
				}
			} 
			// case where server receives a CREATE message
			// format: create,localid,x,y,z
			if(msgTokens[0].compareTo("create") == 0)	{ 
				UUID clientID = UUID.fromString(msgTokens[1]);
			
				String[] pos = {msgTokens[2], msgTokens[3], msgTokens[4]};
 				String[] rot = {msgTokens[5], msgTokens[6], msgTokens[7], msgTokens[8]};
				sendCreateMessages(clientID, pos, rot);
				sendWantsDetailsMessages(clientID);
			}
		
			// case where server receives a BYE message
			// format: bye,localid
			if(msgTokens[0].compareTo("bye") == 0)	{ 
				UUID clientID = UUID.fromString(msgTokens[1]);
				sendByeMessages(clientID);
				removeClient(clientID);
				numberOfPlayers--;
			}
				// case where server receives a DETAILS-FOR message
			if(msgTokens[0].compareTo("dsfr") == 0)	{ 
				UUID clientID = UUID.fromString(msgTokens[1]);
				try {
					forwardPacketToAll(message, clientID);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
				// case where server receives a WANTS message
			if(msgTokens[0].compareTo("wants") == 0)	{ 
				UUID clientID = UUID.fromString(msgTokens[1]);
				sendWantsDetailsMessages(clientID);
			}
				// case where server receives a MOVE message
			if(msgTokens[0].compareTo("move") == 0)	{ 
				UUID clientID = UUID.fromString(msgTokens[1]);
			
				String[] pos = {msgTokens[2], msgTokens[3], msgTokens[4]};
				sendMoveMessages(clientID, pos);
				sendWantsDetailsMessages(clientID);
			} 
 			
 			if(msgTokens[0].compareTo("rotate") == 0)
 			{ // etc….. }
 				System.out.println("Server getting a rotate message");
 				/*UUID clientID = UUID.fromString(msgTokens[1]);
 				sendMoveMessages(clientID, msgTokens);*/
 				
 				
 				UUID clientID = UUID.fromString(msgTokens[1]);
 				//char c = msgTokens[2];
 				//char c = 'a';
 				//String[] pos = {msgTokens[2], msgTokens[3], msgTokens[4]};
 				sendRotateMessages(clientID, msgTokens[2], msgTokens[3], msgTokens[4], msgTokens[5], msgTokens[6]);
 				sendWantsDetailsMessages(clientID);
 			}	
		}
	}
	
	private void sendWantsDetailsMessages(UUID clientID) {
		try	{ 
			String message = new String("wants,"+clientID.toString());
			sendPacket(message, clientID);
		}
		catch (IOException e) { e.printStackTrace(); }
	}
 	public void sendCreateMessages(UUID clientID, String[] position, String[] rotation){
 		System.out.println("Made it to sendCreateMessages from server");
 		try
 		{ 
 			String message = new String("create," + clientID.toString());
 			message += "," + position[0];
 			message += "," + position[1];
 			message += "," + position[2];
 			message += "," + rotation[0];
 			message += "," + rotation[1];
 			message += "," + rotation[2];
 			message += "," + rotation[3];
 			
 			forwardPacketToAll(message, clientID);
 		}
 		catch (IOException e)
 		{
 			e.printStackTrace();
 		}
 	}

	private void sendMoveMessages(UUID clientID, String[] pos) {
		try	{ 
			String message = new String("move,"+clientID+","+pos[0]+","+pos[1]+","+pos[2]);
			forwardPacketToAll(message, clientID);
		}
		catch (IOException e) { e.printStackTrace(); }
		
	}

	private void sendByeMessages(UUID clientID) {
		try	{ 
			String message = new String("bye,"+clientID.toString());
			sendPacket(message, clientID);
		}
		catch (IOException e) { e.printStackTrace(); }
		
	}

	public void sendJoinedMessage(UUID clientID, boolean success)	{ // format: join, success or join, failure
		try	{ 
			String message = new String("join,");
			if (success) 
				message += "success";
			else 
				message += "failure";
			sendPacket(message, clientID);
		}
		catch (IOException e) { e.printStackTrace(); }
	}
 	
 	
 	public void sendRotateMessages(UUID clientID, String axis, String v1, String v2, String v3, String v4)
 	{ // format: create, remoteId, x, y, z
 		System.out.println("Made it to sendRotateMessages from server");
 		try
 		{ 
 			String message = new String("rotate," + clientID.toString());
 			message +="," + axis;
 			message += "," + v1;
 			message += "," + v2;
 			message += "," + v3;
 			message += "," + v4;
 			//message += "," + position[2];
 			forwardPacketToAll(message, clientID);
 		}
 		catch (IOException e)
 		{
 			e.printStackTrace();
 		}
 	}
}

package a3;

import java.io.IOException;
import java.net.InetAddress;
import java.util.UUID;
import ray.networking.server.GameConnectionServer;
import ray.networking.server.IClientInfo;
import ray.rml.Angle;

public class GameServerUDP extends GameConnectionServer<UUID>
{
	public GameServerUDP(int localPort) throws IOException
	{ 
		super(localPort, ProtocolType.UDP); 
	}
 	@Override
 	public void processPacket(Object o, InetAddress senderIP, int sndPort)
 	{
 		String message = (String) o;
 		String[] msgTokens = message.split(",");
 		if(msgTokens.length > 0)
 		{
 			// case where server receives a JOIN message
 			// format: join,localid
 			if(msgTokens[0].compareTo("join") == 0)
 			{ 
 				System.out.println("Server received a join message");
 				try
 				{ 
 					IClientInfo ci;
 					//ci = getServerSocket().createClientInfo(senderIP, senderPort);
 					//UUID clientID = UUID.fromString(messageTokens[1]);
 					ci = getServerSocket().createClientInfo(senderIP, sndPort);
 					UUID clientID = UUID.fromString(msgTokens[1]);
 					addClient(ci, clientID);
 					sendJoinedMessage(clientID, true);
 				}
 				catch (IOException e)
 				{ 
 					e.printStackTrace();
 				}
 			}
 			// case where server receives a CREATE message
 			// format: create,localid,x,y,z
 			if(msgTokens[0].compareTo("create") == 0)
 			{ 
 				System.out.println("Creating a connection from server or create ghost avatar");
 				UUID clientID = UUID.fromString(msgTokens[1]);
 				String[] pos = {msgTokens[2], msgTokens[3], msgTokens[4]};
 				String[] rot = {msgTokens[5], msgTokens[6], msgTokens[7], msgTokens[8]};
 				sendCreateMessages(clientID, pos,rot, msgTokens[9], msgTokens[10]);
 				sendWantsDetailsMessages(clientID);
 				
 				System.out.println("pos: " + pos[0] + ", " + pos[1] + ", " + pos[2] );
 			}
 			// case where server receives a BYE message
 			// format: bye,localid
 			if(msgTokens[0].compareTo("bye") == 0)
 			{
 				System.out.println("Made it here too?");
 				UUID clientID = UUID.fromString(msgTokens[1]);
 				sendByeMessages(clientID);
 				removeClient(clientID);
 			}
 			// case where server receives a DETAILS-FOR message
 			if(msgTokens[0].compareTo("dsfr") == 0)
 			{ // etc….. 
 				UUID clientID = UUID.fromString(msgTokens[1]);
 				String message2 = new String("dsfr," + clientID.toString());
 	 			message2 += "," + msgTokens[2];
 	 			message2 += "," + msgTokens[3];
 	 			message2 += "," + msgTokens[4];
 	 			message2 += "," + msgTokens[5];
 	 			message2 += "," + msgTokens[6];
 	 			message2 += "," + msgTokens[7];
 	 			message2 += "," + msgTokens[8];
 	 			try {
					forwardPacketToAll(message2, clientID);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
 			}
 			// case where server receives a MOVE message
 			if(msgTokens[0].compareTo("move") == 0)
 			{ // etc….. }
 				System.out.println("Server getting a move message");
 				/*UUID clientID = UUID.fromString(msgTokens[1]);
 				sendMoveMessages(clientID, msgTokens);*/
 				
 				
 				UUID clientID = UUID.fromString(msgTokens[1]);
 				String[] pos = {msgTokens[2], msgTokens[3], msgTokens[4]};
 				sendMoveMessages(clientID, pos);
 				//sendWantsDetailsMessages(clientID);
 			}	 
 			
 			if(msgTokens[0].compareTo("createDungeon") == 0)
 			{ // etc….. }
 				System.out.println("Server getting a createDungeon message");
 				System.out.println("Here it is: " + message);
 				
 				
 			
 				
 				try {
					//sendPacket(message, UUID.fromString(msgTokens[1]));
					forwardPacketToAll(message, UUID.fromString(msgTokens[1]));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
 				
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
 				//sendWantsDetailsMessages(clientID);
 			}	 
 			
 			if(msgTokens[0].compareTo("moveF") == 0)
 			{ // etc….. }
 			}	 
 		}
 	}


 	public void sendJoinedMessage(UUID clientID, boolean success)
 	{ // format: join, success or join, failure
 		System.out.println("Made it to sendJoinedMessages from server");
 		System.out.println("From client: " + clientID.toString());
 		try
 		{ 	
 			String message = new String("join,");
 			if (success) message += "success";
 			else message += "failure";
 			sendPacket(message, clientID);
 		}
 		catch (IOException e) 
 		{ 
 			e.printStackTrace(); 
 		}
 	}
 	
 	public void sendCreateMessages(UUID clientID, String[] position, String[] rotation, String type, String obj)
 	{ // format: create, remoteId, x, y, z
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
 			message += "," + type;
 			message += "," + obj;
 			
 			forwardPacketToAll(message, clientID);
 		}
 		catch (IOException e)
 		{
 			e.printStackTrace();
 		}
 	}
 	
 	public void sndDetailsMsg(UUID clientID, UUID remoteId, String[] position)
 	{ // etc….. 
	
 	}
 	public void sendWantsDetailsMessages(UUID clientID)
 	{ // etc…..
	
 	}
 	/*public void sendMoveMessages(UUID clientID, String[] position)
 	{ // etc….. 
	
 	}*/
 	
 	/*public void sendMoveMessages(UUID clientID, String[] message)
 	{ // etc….. 
 		
 		try
 		{ 
 			String message2 = new String(message[0] + ", " + clientID.toString() + "," + message[2]);
 			forwardPacketToAll(message2, clientID);
 		}
 		catch (IOException e)
 		{
 			e.printStackTrace();
 		}
 	}*/
 	
 	
 	public void sendMoveMessages(UUID clientID, String[] position)
 	{ // format: create, remoteId, x, y, z
 		System.out.println("Made it to sendMoveMessages from server");
 		try
 		{ 
 			String message = new String("move," + clientID.toString());
 			message += "," + position[0];
 			message += "," + position[1];
 			message += "," + position[2];
 			forwardPacketToAll(message, clientID);
 		}
 		catch (IOException e)
 		{
 			e.printStackTrace();
 		}
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
 	
 	public void sendByeMessages(UUID clientID)
 	{ // etc….. 
	
 	}
}
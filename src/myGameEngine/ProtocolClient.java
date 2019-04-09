package myGameEngine;

import java.net.InetAddress;
import java.util.UUID;
import java.util.Vector;

import ray.rml.Angle;
import ray.rml.Degreef;
import ray.rml.Vector3;
import ray.rml.Vector3f;

import java.io.IOException;

import hoardPVPGame.MyGame;
import ray.networking.client.GameConnectionClient;

public class ProtocolClient extends GameConnectionClient {
	private MyGame game;
	private UUID id;
	private Vector<GhostAvatar> ghostAvatars;
	
	public ProtocolClient(InetAddress remAddr, int remPort,
			ProtocolType pType, MyGame game) throws IOException{
		super(remAddr, remPort, pType);
		System.out.println("Client started.");
		this.game = game;
		this.id = UUID.randomUUID();
		this.ghostAvatars = new Vector<GhostAvatar>();
	}
	
	@Override
	protected void processPacket(Object msg) {
		//System.out.println("Client processing: "+msg);
		String strMessage = (String) msg;
		String[] msgTokens = strMessage.split(",");
		if(msgTokens.length > 0)
		{
			if(msgTokens[0].compareTo("join") == 0) { // receive “join" 
			// format: join, success or join, failure
				if(msgTokens[1].compareTo("success") == 0)	{ 
					game.setIsConnected(true);
					sendCreateMessage(game.getPlayerPosition());
					sendWantsMessage();
				}
				if(msgTokens[1].compareTo("failure") == 0) { 
					game.setIsConnected(false);
				} 
			}
			if(msgTokens[0].compareTo("bye") == 0){ // receive “bye”
				// format: bye, remoteId
				UUID ghostID = UUID.fromString(msgTokens[1]);
				removeGhostAvatar(ghostID);
			}
			if ((msgTokens[0].compareTo("dsfr") == 0 ) // receive “dsfr”
			|| (msgTokens[0].compareTo("create")==0)){ 
				// format: create, remoteId, x,y,z or dsfr, remoteId, x,y,z
				UUID ghostID = UUID.fromString(msgTokens[1]);
				Vector3 ghostPosition = Vector3f.createFrom(
						Float.parseFloat(msgTokens[2]),
						Float.parseFloat(msgTokens[3]),
						Float.parseFloat(msgTokens[4]));
				
				if(this.containsGhostAvatar(ghostID)) {
					getGhostAvatar(ghostID).setPos(ghostPosition);
				}
				else {
					try {
						createGhostAvatar(ghostID, ghostPosition);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			if(msgTokens[0].compareTo("create") == 0){ // rec. “create…”
				UUID ghostID = UUID.fromString(msgTokens[1]);
				Vector3 ghostPosition = Vector3f.createFrom(
						Float.parseFloat(msgTokens[2]),
						Float.parseFloat(msgTokens[3]),
						Float.parseFloat(msgTokens[4]));
				try	{ 
					createGhostAvatar(ghostID, ghostPosition);
				} catch (IOException e){ 
					System.out.println("error creating ghost avatar");
				} 
			}
			if(msgTokens[0].compareTo("wants") == 0){ // rec. “wants…”
				UUID clientID = UUID.fromString(msgTokens[1]);
				this.sendDetailsForMessage(clientID, game.getPlayerPosition());
			}
			if(msgTokens[0].compareTo("move") == 0){ // format: move,ghostID,x,y,z
				UUID ghostID=UUID.fromString(msgTokens[1]);
				Vector3 ghostPosition = Vector3f.createFrom(
						Float.parseFloat(msgTokens[2]),
						Float.parseFloat(msgTokens[3]),
						Float.parseFloat(msgTokens[4]));
				for(GhostAvatar ga :ghostAvatars) {
					if(ga.getID().equals(ghostID)){
						System.out.println("Moving "+ghostID);
						ga.setPos(ghostPosition );
					}
				}
				
			} 
			if(msgTokens[0].compareTo("rotate") == 0){ // format: rotate,ghostID,axis,angle
				UUID ghostID=UUID.fromString(msgTokens[1]);
				Angle angle = Degreef.createFrom(Float.parseFloat(msgTokens[3]));
				for(GhostAvatar ga :ghostAvatars) {
					if(ga.getID().equals(ghostID)){
						System.out.println("Rotating "+ghostID);
						ga.rotate(msgTokens[2].charAt(0), angle);
					}
				}
				
			} 
		}
	}
	
	private void createGhostAvatar(UUID ghostID, Vector3 pos) throws IOException {// format: create, ghostID, x,y,z
		try	{ 
			GhostAvatar ga=new GhostAvatar(ghostID, pos);
			if(!containsGhostAvatar(ghostID)) {
				game.addGhostAvatarToGameWorld(ga);
				ghostAvatars.add(ga);
			}
		}
		catch (Exception e) { 
			e.printStackTrace();
		} 
		
	}

	private void removeGhostAvatar(UUID ghostID) {// format: remove, ghostID
		game.removeGhostAvatarFromGameWorld(getGhostAvatar(ghostID));
		removeGhostAvatar(ghostID);
		
	}
	
	private GhostAvatar getGhostAvatar(UUID id) {
		for(GhostAvatar avatar : ghostAvatars) {
			if(avatar.getID().equals(id))
				return avatar;
		}
		return null;
	}

	
	private boolean containsGhostAvatar(UUID id) {
		for(GhostAvatar avatar : ghostAvatars) {
			if(avatar.getID().equals(id))
				return true;
		}
		return false;
	}
	
	public void sendJoinMessage() { // format: join, localId
		try	{ 
			sendPacket(new String("join," + id.toString()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void sendWantsMessage() { // format: wants, localId
		try	{ 
			sendPacket(new String("wants," + id.toString()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void sendCreateMessage(Vector3 pos){ // format: (create, localId, x,y,z)
		try	{ 
			String message = new String("create," + id.toString());
			message += "," + pos.x()+"," + pos.y() + "," + pos.z();
			sendPacket(message);
		}
		catch (IOException e) { 
			e.printStackTrace();
		} 
	}
	
	public void sendByeMessage(){// format: (bye, localID)
		try	{ 
			sendPacket(new String("bye," + id.toString()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void sendDetailsForMessage(UUID remId, Vector3 pos){ 
		try	{ 
			String message=new String("dsfr," + id.toString());
			message += "," + pos.x()+"," + pos.y() + "," + pos.z();
			sendPacket(message);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void sendMoveMessage(UUID ghostID, Vector3 pos) {
		try	{ 
			String message = new String("move," + ghostID.toString());
			message += "," + pos.x()+"," + pos.y() + "," + pos.z();
			sendPacket(message);
		}
		catch (IOException e) { 
			e.printStackTrace();
		} 
		
	}

	public void sendMoveMessage(Vector3 pos) {
		try	{ 
			String message = new String("move,"+id);
			message += "," + pos.x()+"," + pos.y() + "," + pos.z();
			sendPacket(message);
		}
		catch (IOException e) { 
			e.printStackTrace();
		} 
		
	}

	public void sendRotateMessage(UUID id2, char axis, Angle angle) {
		try	{ 
			String message = new String("rotate,"+id);
			message += "," + axis+"," + angle;
			sendPacket(message);
		}
		catch (IOException e) { 
			e.printStackTrace();
		} 
		
	}
	
	public UUID getID() {
		return id;	
	}
	
}

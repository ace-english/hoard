package myGameEngine;

import java.net.InetAddress;
import java.util.UUID;
import java.util.Vector;

import ray.rml.Angle;
import ray.rml.Degreef;
import ray.rml.Matrix3;
import ray.rml.Matrix3f;
import ray.rml.Vector3;
import ray.rml.Vector3f;

import java.io.IOException;

import hoardPVPGame.MyGame;
import ray.networking.client.GameConnectionClient;
import ray.rage.scene.SceneNode;

public class ProtocolClient extends GameConnectionClient {
	private MyGame game;
	private UUID id;
	private Vector<GhostAvatar> ghostAvatars;
	private boolean connectedToServerButWaitingForPlayer = true;
	
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
					sendCreateMessage(game.getPlayerPosition(),game.getRotation());
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
			if (/*(msgTokens[0].compareTo("dsfr") == 0 ) // receive “dsfr”
			|| */(msgTokens[0].compareTo("create")==0)){ 
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
						float v1 = Float.parseFloat(msgTokens[5]);
						float v2 = Float.parseFloat(msgTokens[6]);
						float v3 = Float.parseFloat(msgTokens[7]);
						float v4 = Float.parseFloat(msgTokens[8]);
						
						float flo[] = {v1,0,v2,0,1,0,v3,0,v4};
						Matrix3f mtx = (Matrix3f) Matrix3f.createFrom(flo);
						createGhostAvatar(ghostID, ghostPosition, mtx);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			if (msgTokens[0].compareTo("dsfr") == 0 )
			{
				 UUID ghostID=UUID.fromString(msgTokens[1]);
				 Vector3 ghostPosition = Vector3f.createFrom(
				 Float.parseFloat(msgTokens[2]),
				 Float.parseFloat(msgTokens[3]),
				 Float.parseFloat(msgTokens[4]));
				 float v1 = Float.parseFloat(msgTokens[5]);
				 float v2 = Float.parseFloat(msgTokens[6]);
				 float v3 = Float.parseFloat(msgTokens[7]);
				 float v4 = Float.parseFloat(msgTokens[8]);
				 float flo[] = {v1,0,v2,0,1,0,v3,0,v4};
				 Matrix3f mtx = (Matrix3f) Matrix3f.createFrom(flo);
				 
				 for(GhostAvatar ga :ghostAvatars) {
						if(ga.getID().equals(ghostID)){
							System.out.println("Rotating "+ghostID);
							//ga.rotate(msgTokens[2].charAt(0), angle);
							ga.getNode().setLocalRotation(mtx);
							ga.setPos(ghostPosition );
						}
					}
			}
			if(msgTokens[0].compareTo("create") == 0){ // rec. “create…”
				UUID ghostID = UUID.fromString(msgTokens[1]);
				Vector3 ghostPosition = Vector3f.createFrom(
						Float.parseFloat(msgTokens[2]),
						Float.parseFloat(msgTokens[3]),
						Float.parseFloat(msgTokens[4]));
				
				float v1 = Float.parseFloat(msgTokens[5]);
				float v2 = Float.parseFloat(msgTokens[6]);
				float v3 = Float.parseFloat(msgTokens[7]);
				float v4 = Float.parseFloat(msgTokens[8]);
				
				float flo[] = {v1,0,v2,0,1,0,v3,0,v4};
				Matrix3f mtx = (Matrix3f) Matrix3f.createFrom(flo);
				System.out.println("Reconstructed matrix: " + mtx);
				try	{ 
					createGhostAvatar(ghostID, ghostPosition,mtx);
				} catch (IOException e){ 
					System.out.println("error creating ghost avatar");
				} 
			}
			if(msgTokens[0].compareTo("wants") == 0){ // rec. “wants…”
				UUID clientID = UUID.fromString(msgTokens[1]);
				this.sendDetailsForMessage(clientID, game.getPlayerNode());
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
				System.out.println("recieving a rotate message");
				System.out.println("here is the message: ");
				System.out.println(strMessage);
				UUID ghostID=UUID.fromString(msgTokens[1]);
				
				float v1 = Float.parseFloat(msgTokens[3]);
				float v2 = Float.parseFloat(msgTokens[4]);
				float v3 = Float.parseFloat(msgTokens[5]);
				float v4 = Float.parseFloat(msgTokens[6]);
				
				float flo[] = {v1,0,v2,0,1,0,v3,0,v4};
				Matrix3f mtx = (Matrix3f) Matrix3f.createFrom(flo);
				//UUID, char,1,2,3,4//0,1,2,3,4,5
				//Angle angle = Degreef.createFrom(Float.parseFloat(msgTokens[3]));
				//Matrix3 = (Matrix3)msgTokens[3]); Matrix3f.
				Angle angle = Degreef.createFrom(1);
				System.out.println("Reconstructed matrix2: " + mtx);
				//Angle angle = (Angle) msgTokens[2]);
				for(GhostAvatar ga :ghostAvatars) {
					if(ga.getID().equals(ghostID)){
						System.out.println("Rotating "+ghostID);
						//ga.rotate(msgTokens[2].charAt(0), angle);
						ga.getNode().setLocalRotation(mtx);
					}
				}
				
			} 
		}
	}
	
	private void createGhostAvatar(UUID ghostID, Vector3 pos, Matrix3f rotMat) throws IOException {// format: create, ghostID, x,y,z
		try	{ 
			System.out.println("from inside create ghost avatar" + rotMat);
			GhostAvatar ga=new GhostAvatar(ghostID, pos, rotMat);
			//ga.getNode().setLocalRotation(rotMat);
			//ga.getNode().
			if(!containsGhostAvatar(ghostID)) {
				game.addGhostAvatarToGameWorld(ga);
				ghostAvatars.add(ga);
			}
			if(connectedToServerButWaitingForPlayer)
			{
				
				sendCreateMessage(game.getPlayerPosition(), game.getRotation());
				connectedToServerButWaitingForPlayer = false;
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
	
	public void sendCreateMessage(Vector3 pos, Matrix3 matRot){ // format: (create, localId, x,y,z)
		float ft = 2;
		float flo[] = {0,1,ft,3,4,5,6,7,8};
		Matrix3f mtx = (Matrix3f) Matrix3f.createFrom(flo);
		//mtx.
		Matrix3f.createFrom(flo);
		//create matrix string
		String matStr = Float.toString(game.getRotation().row(0).x()) + "," +
				Float.toString(game.getRotation().row(2).x()) + "," +
				Float.toString(game.getRotation().row(0).z()) + "," +
				Float.toString(game.getRotation().row(2).z());
		try	{ 
			String message = new String("create," + id.toString());
			message += "," + pos.x()+"," + pos.y() + "," + pos.z() +  "," + matStr;
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
	public void sendDetailsForMessage(UUID remId, SceneNode playerNode){ 
		try	{ 
			String message=new String("dsfr," + id.toString());
			message += "," + playerNode.getWorldPosition().x()+"," 
			+ playerNode.getWorldPosition().y() + "," 
			+ playerNode.getWorldPosition().z();
			
			String matStr = Float.toString(playerNode.getLocalRotation().row(0).x()) + "," +
					Float.toString(playerNode.getLocalRotation().row(2).x()) + "," +
					Float.toString(playerNode.getLocalRotation().row(0).z()) + "," +
					Float.toString(playerNode.getLocalRotation().row(2).z());
			
			message += "," + matStr;
			sendPacket(message);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void sendMoveMessage(UUID ghostID, Vector3 pos) {
		try	{ 
			String message = new String("move," + ghostID.toString());
			message += "," + pos.x()+"," + pos.y() + "," + pos.z();
			System.out.println("Sending a move message!");
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

	public void sendRotateMessage(UUID id2, char axis, String matStr) {//UUID, char,1,2,3,4//0,1,2,3,4,5,
		try	{ System.out.println("Sending a rotate message");
			//String message = new String("rotate,"+id);
			String message = new String("rotate,"+ id2.toString());
			message += "," + axis+"," + matStr;
			System.out.println("Sending a rotate message");
			System.out.println("Here is the message");
			System.out.println(message);//0,1,2,3,4,5,6
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

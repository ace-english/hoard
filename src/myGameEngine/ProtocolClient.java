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

import hoardPVPGame.GameUtil;
import hoardPVPGame.MyGame;
//import hoardPVPGame.PLAYER_TYPE;
import ray.networking.client.GameConnectionClient;
import ray.rage.scene.SceneNode;

enum PLAYER_TYPE2{
	DRAGON, KNIGHT;
}

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
		System.out.println("recieved a message from server");
		if(msgTokens.length > 0)
		{
			if(msgTokens[0].compareTo("join") == 0) { // receive “join" 
				System.out.println("recieved join message from server");
			// format: join, success or join, failure
				if(msgTokens[1].compareTo("success") == 0)	{ 
					System.out.println("Success connecting from server");
					
					game.setIsConnected(true);
					sendCreateMessage(game.getPlayerPosition(), game.getRotation());
					sendWantsMessage();
				}
				if(msgTokens[1].compareTo("failure") == 0) { 
					System.out.println("failed connecting to server");
					game.setIsConnected(false);
				} 
			}
			if(msgTokens[0].compareTo("bye") == 0){ // receive “bye”
				// format: bye, remoteId
				UUID ghostID = UUID.fromString(msgTokens[1]);
				removeGhostAvatar(ghostID);
			}
			
			if(msgTokens[0].compareTo("createDungeon") == 0){ // receive “bye”
				// format: bye, remoteId
				
				
				//game.setDungeon(dungeon);
				System.out.println("recieving create dungeon message");
				
				for(String msg2 : msgTokens)
				{
					System.out.println(msg2);
				}
				//System.out.println("recieving create dungeon message");
				//System.out.println(msgTokens[0]);
				game.buildDungeonFromString(msgTokens);
				
			}
			/*if (/*(msgTokens[0].compareTo("dsfr") == 0 ) // receive “dsfr”
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
						float v1 = Float.parseFloat(msgTokens[5]);
						float v2 = Float.parseFloat(msgTokens[6]);
						float v3 = Float.parseFloat(msgTokens[7]);
						float v4 = Float.parseFloat(msgTokens[8]);
						
						float flo[] = {v1,0,v2,0,1,0,v3,0,v4};
						Matrix3f mtx = (Matrix3f) Matrix3f.createFrom(flo);
						String charType = msgTokens[9];
						createGhostAvatar(ghostID, ghostPosition,mtx, charType );
					
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}*/
			if (msgTokens[0].compareTo("dsfr"/*and not this client id*/) == 0 )
			{
				//System.out.println("getting a details for message frpm client: ");
				//System.out.println(msgTokens[1]);
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
					 //System.out.println("Here's the id for the ghost "+ ga.getID());
						if(ga.getID().equals(ghostID)){
							//System.out.println("Rotating "+ghostID);
							//ga.rotate(msgTokens[2].charAt(0), angle);
							ga.getNode().setLocalRotation(mtx);
							ga.setPos(ghostPosition );
						}
					}
			}
			if(msgTokens[0].compareTo("create") == 0){ // rec. “create…”
				System.out.println("message from client: " + UUID.fromString(msgTokens[1]));
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
						String charType = msgTokens[9];
						String objType = msgTokens[10];

				try	{ 
					createGhostAvatar(ghostID, ghostPosition, mtx, charType, objType);
				} catch (IOException e){ 
					System.out.println("error creating ghost avatar");
				} 
			}
			if(msgTokens[0].compareTo("wants") == 0){ // rec. “wants…”
				UUID clientID = UUID.fromString(msgTokens[1]);
				//this.sendDetailsForMessage(clientID, game.getPlayerPosition());
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
	
	private void createGhostAvatar(UUID ghostID, Vector3 pos, Matrix3f rotMat, String type, String obj) throws IOException {// format: create, ghostID, x,y,z
		try	{ 
			System.out.println("from inside create ghost avatar" + rotMat);
			GhostAvatar ga=new GhostAvatar(ghostID, pos, rotMat);
			//ga.getNode().setLocalRotation(rotMat);
			//ga.getNode().
			if(!containsGhostAvatar(ghostID)) {
				game.addGhostAvatarToGameWorld(ga, type, obj);
				ghostAvatars.add(ga);
			}
			if(connectedToServerButWaitingForPlayer)
			{
				System.out.println("Made it here");
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
		System.out.println("Removing ghost avatar");
		
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
		System.out.println("Sending join message to server");
		System.out.println("From: " + id.toString());
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
		String charaType = "";
		String objType = "";
		System.out.println("sendingCreateMessage");
		//if(game.getPlayerObjType() == PLAYER_TYPE2.KNIGHT)
		if(game.getPlayerType() == GameUtil.SKIN.KNIGHT)
		{
			charaType = "knight";
			objType = "knight";
		}
		
		if(game.getPlayerType() == GameUtil.SKIN.WHITE_KNIGHT)
		{
			charaType = "wKnight";
			objType = "knight";
		}
		
		if(game.getPlayerType() == GameUtil.SKIN.BLACK_KNIGHT)
		{
			charaType = "bKnight";
			objType = "knight";
		}
		
		if(game.getPlayerType() == GameUtil.SKIN.GOLD_KNIGHT)
		{
			charaType = "gKnight";
			objType = "knight";
		}
		
		
		if(game.getPlayerType() == GameUtil.SKIN.GREEN_DRAGON)
		{
			charaType = "gDragon";
			objType = "dragon";
		}
		
		if(game.getPlayerType() == GameUtil.SKIN.RED_DRAGON)
		{
			charaType = "rDragon";
			objType = "dragon";
		}
		
		if(game.getPlayerType() == GameUtil.SKIN.PURPLE_DRAGON)
		{
			charaType = "pDragon";
			objType = "dragon";
		}
		
		if(game.getPlayerType() == GameUtil.SKIN.BLACK_DRAGON)
		{
			charaType = "bDragon";
			objType = "dragon";
		}
		
			
			
		
		System.out.println("skinType of Knight is "  + game.getPlayerType());
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
			message += "," + charaType + "," + objType;
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
	/*public void sendDetailsForMessage(UUID remId, Vector3 pos){ 
		try	{ 
			String message=new String("dsfr," + id.toString());
			message += "," + pos.x()+"," + pos.y() + "," + pos.z();
			sendPacket(message);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}*/
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
	
	
	
	
	public void sendDungeonMessage(String dungeon) {//UUID, char,1,2,3,4//0,1,2,3,4,5,
		try	{ System.out.println("Sending a rotate message");
			//String message = new String("rotate,"+id);
		String message = new String("createDungeon," + id.toString());
		message += "," + dungeon;
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

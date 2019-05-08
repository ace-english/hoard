package myGameEngine;

import java.io.IOException;
import java.util.UUID;

import hoardPVPGame.Dungeon;
import hoardPVPGame.GameUtil;
import myGameEngine.MoveAction.Direction;
import net.java.games.input.Controller;
import net.java.games.input.Event;
import ray.input.InputManager;
import ray.input.action.Action;
import ray.rage.Engine;
import ray.rage.scene.Camera;
import ray.rage.scene.SceneManager;
import ray.rage.scene.SceneNode;
import ray.rage.scene.SkeletalEntity;
import ray.rage.scene.Tessellation;
import ray.rage.scene.SkeletalEntity.EndType;
import ray.rml.Vector3;
import ray.rml.Vector3f;

public abstract class Player{

	float speed;
	boolean boostActive;
	private SceneNode node;
	protected SkeletalEntity skeleton;
	private Camera camera;
	private ProtocolClient protClient;
	private UUID id;
	SceneManager sm;
	GameUtil.SKIN skin;
	Dungeon dungeon;
	SceneManager sm2;

	int score;
	
	private float acceleration = 0.025f;/////////////////////
	private float velocity = 0.0f;///////////////
	private boolean jumped = false;//////////////
	private float jumpPosition = 0;///////////////
	private float displacement = 0;//////////////
	private float jumpHeight = 0;////////////////
	
	public Player(SceneManager sm, ProtocolClient pc, GameUtil.SKIN skin) {
		speed=0.08f;
		boostActive=false;
		score=0;
		this.protClient=pc;
		this.setCamera(sm.getCamera("MainCamera"));
		if(pc!=null)
			id=pc.getID();
		this.skin=skin;
		try {
			setupNodes(sm);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.sm=sm;
		
	}
	
	
	public void setJumpHeight()/////////////////////////////////
	{
		SceneNode dolphinN =
				sm2.
				getSceneNode("playerNode");
			SceneNode tessN =
			sm2.
			getSceneNode("tessN");
		Tessellation tessE = ((Tessellation) tessN.getAttachedObject("tessE"));
		// Figure out Avatar's position relative to plane
		Vector3 worldAvatarPosition = dolphinN.getWorldPosition();
		Vector3 localAvatarPosition = dolphinN.getLocalPosition();
		// use avatar World coordinates to get coordinates for height
		jumpHeight = tessE.getWorldHeight(
				worldAvatarPosition.x(),
				worldAvatarPosition.z());
	}
	
	
	
	
	
	
	
	
	
	
	
	public void updateVerticalPosition()///////////////////
	{ //SceneNode dolphinN =
	//this.getEngine().getSceneManager().
	//getSceneNode("myDolphinNode");
		
		
		SceneNode dolphinN =
		sm2.
		getSceneNode("playerNode");
	SceneNode tessN =
	sm2.
	getSceneNode("tessN");
	Tessellation tessE = ((Tessellation) tessN.getAttachedObject("tessE"));
	// Figure out Avatar's position relative to plane
	Vector3 worldAvatarPosition = dolphinN.getWorldPosition();
	Vector3 localAvatarPosition = dolphinN.getLocalPosition();
	
	if(jumped)
		System.out.println("Velocity: " + velocity);
	
	if(jumped)
	{
		velocity = velocity - acceleration;
		displacement = jumpHeight - tessE.getWorldHeight(
				worldAvatarPosition.x(),
				worldAvatarPosition.z());
	}
	
	if(jumped)
	{
		jumpPosition = jumpPosition + velocity;
	System.out.println("Jump Position: " + jumpPosition);
	System.out.println("Jump Displacement: " + displacement);
	System.out.println("Jump Height: " + jumpHeight);
	System.out.println("heightmap Height: " + 
	tessE.getWorldHeight(
			worldAvatarPosition.x(),
			worldAvatarPosition.z()) );
	}
	// use avatar World coordinates to get coordinates for height
	Vector3 newAvatarPosition = Vector3f.createFrom(
	 // Keep the X coordinate
	 localAvatarPosition.x(),
	 // The Y coordinate is the varying height
	 tessE.getWorldHeight(
	worldAvatarPosition.x(),
	worldAvatarPosition.z()) + jumpPosition + displacement,
	 //Keep the Z coordinate
	 localAvatarPosition.z()
	);
	// use avatar Local coordinates to set position, including height
	dolphinN.setLocalPosition(newAvatarPosition );
	
	
	
	
	if ( dolphinN.getLocalPosition().y() <  tessE.getWorldHeight(
				worldAvatarPosition.x(),
				worldAvatarPosition.z()))
			{
		
		jumped = false;
		 velocity = 0.0f;
		 jumpPosition = 0;
		 displacement = 0;
		 newAvatarPosition = Vector3f.createFrom(
				 // Keep the X coordinate
				 localAvatarPosition.x(),
				 // The Y coordinate is the varying height
				 tessE.getWorldHeight(
						 worldAvatarPosition.x(),
						 worldAvatarPosition.y()),
				 //Keep the Z coordinate
				 localAvatarPosition.z()
				);
		 dolphinN.setLocalPosition(newAvatarPosition );
			}
	 //jumpTest = jumpTest - 0.01f;
	 //if ( jumpTest < 0 )
		// jumpTest = 0;
	}
	
	protected abstract void setupNodes(SceneManager sm) throws IOException;
	
	public void update(float elapsTime) {
		skeleton.update();
	}

	public float getSpeed() {
		return speed;
	}

	public GameUtil.SKIN getSkin() {
		return skin;
	}

	public SceneNode getNode() {
		return node;
	}

	public void setNode(SceneNode node) {
		this.node = node;
	}
	
	ProtocolClient getProtocolClient() {
		return protClient;
	}

	public void scoreUp() {
		score++;
	}
	
	public int getScore() {
		return score;
	}
	
	public abstract void setupInputs(InputManager im, Controller controller);

	public Camera getCamera() {
		return camera;
	}

	public void setCamera(Camera camera) {
		this.camera = camera;
	}
	
	public UUID getID() {
		return id;
	}

	public void teleport(Vector3 worldPosition) {
		node.setLocalPosition(worldPosition);
		
	}
	
	public Dungeon getDungeon() {
		return dungeon;
	}
	
	public void setDungeon(Dungeon dungeon) {
		this.dungeon=dungeon;
	}
	
	
	public boolean move(float arg0, Event arg1) {
		Direction dir; float value=arg1.getValue();
		if(arg1.getComponent().getName().equals("W")||
				(arg1.getComponent().getName().equals("Y Axis")&&value<-0.1f))
			dir=Direction.fwd;
		else if(arg1.getComponent().getName().equals("S")||
				(arg1.getComponent().getName().equals("Y Axis")&&value>0.1f))
			dir=Direction.back;
		else if(arg1.getComponent().getName().equals("A")||
				(arg1.getComponent().getName().equals("X Axis")&&value<-0.1f))
			dir=Direction.left;
		else if(arg1.getComponent().getName().equals("D")||
				(arg1.getComponent().getName().equals("X Axis")&&value>0.1f))
			dir=Direction.right;
		else return false;
		
		value=Math.abs(value);
		
		
		System.out.print(node.getWorldPosition());
		
			switch(dir) {
			case fwd:
				//node.moveForward(getSpeed()*value);
				node.moveRight(getSpeed()*value);////////////////
				break;
			case back:
				//node.moveBackward(getSpeed()*value);
				node.moveLeft(getSpeed()*value);/////////////////
				break;
			case right:
				//node.moveLeft(getSpeed()*value);
				node.moveForward(getSpeed()*value);////////////////
				break;
			case left:
				//node.moveRight(getSpeed()*value);
				node.moveBackward(getSpeed()*value);///////////////
				
				break;
			}
				
			//if(protClient!=null)
				//protClient.sendMoveMessage(id, node.getWorldPosition());
			
			return true;
			
	}


	public void setVelocity(float v)///////
	{
		velocity = v;
	}
	
	public void setJumped(boolean jump)///////
	{
		this.jumped = jump;
	}
	
	public boolean getJumped()///////
	{
		return jumped;
	}
	
}

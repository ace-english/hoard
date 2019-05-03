package myGameEngine;

import java.io.IOException;
import java.util.UUID;

import net.java.games.input.Controller;
import ray.input.InputManager;
import ray.input.action.Action;
import ray.rage.scene.Camera;
import ray.rage.scene.SceneManager;
import ray.rage.scene.SceneNode;
import ray.rage.scene.Tessellation;
import ray.rml.Vector3;
import ray.rml.Vector3f;

public abstract class Player{

	float speed;
	boolean boostActive;
	private SceneNode node;
	private Camera camera;
	private ProtocolClient protClient;
	private float jumpTest = 5;
	//private float acceleration = 0.05f;
	private float acceleration = 0.025f;
	private float velocity = 0.0f;
	private boolean jumped = false;
	private float jumpPosition = 0;
	private float displacement = 0;
	private float jumpHeight = 0;
	private UUID id;
	SceneManager sm2;

	int score;
	
	public Player(SceneManager sm, ProtocolClient pc) {
		speed=0.08f;
		boostActive=false;
		score=0;
		this.protClient=pc;
		this.setCamera(sm.getCamera("MainCamera"));
		id=pc.getID();
		try {
			setupNodes(sm);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void setJumpHeight()
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
	
	
	public void updateVerticalPosition()
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
	/*Vector3 newAvatarPosition;
	// use avatar World coordinates to get coordinates for height
	if(!jumped)
	{
	 newAvatarPosition = Vector3f.createFrom(
	 // Keep the X coordinate
	 localAvatarPosition.x(),
	 // The Y coordinate is the varying height
	 tessE.getWorldHeight(
	worldAvatarPosition.x(),
	worldAvatarPosition.z()) + jumpPosition,
	 //Keep the Z coordinate
	 localAvatarPosition.z()
	);
	}
	else {
		
		newAvatarPosition = Vector3f.createFrom(
				 // Keep the X coordinate
				 localAvatarPosition.x(),
				 // The Y coordinate is the varying height
				 worldAvatarPosition.y() + jumpPosition,
				 //Keep the Z coordinate
				 localAvatarPosition.z()
				);
		
	}
	// use avatar Local coordinates to set position, including height
	dolphinN.setLocalPosition(newAvatarPosition );*/
	
/* working bad version	public void updateVerticalPosition()
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
	// use avatar World coordinates to get coordinates for height
	Vector3 newAvatarPosition = Vector3f.createFrom(
	 // Keep the X coordinate
	 localAvatarPosition.x(),
	 // The Y coordinate is the varying height
	 tessE.getWorldHeight(
	worldAvatarPosition.x(),
	worldAvatarPosition.z()) + velocity,
	 //Keep the Z coordinate
	 localAvatarPosition.z()
	);
	// use avatar Local coordinates to set position, including height
	dolphinN.setLocalPosition(newAvatarPosition );
	
	
	if(jumped)
		System.out.println("Velocity: " + velocity);
	
	if(jumped)
		velocity = velocity - acceleration;
	
	
	if ( dolphinN.getLocalPosition().y() <  tessE.getWorldHeight(
				worldAvatarPosition.x(),
				worldAvatarPosition.z()))
			{
		
		jumped = false;
		 velocity = 0.0f;
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
	}*/
	
	
	/*public void updateVerticalPosition2()
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
	// use avatar World coordinates to get coordinates for height
	Vector3 newAvatarPosition = Vector3f.createFrom(
	 // Keep the X coordinate
	 localAvatarPosition.x(),
	 // The Y coordinate is the varying height
	 tessE.getWorldHeight(
	worldAvatarPosition.x(),
	worldAvatarPosition.z()),
	 //Keep the Z coordinate
	 localAvatarPosition.z()
	);
	// use avatar Local coordinates to set position, including height
	dolphinN.setLocalPosition(newAvatarPosition );
	 jumpTest = jumpTest - 0.01f;
	 if ( jumpTest < 0 )
		 jumpTest = 0;
	}*/
	
	/*public void updateJumpPosition()
	{
		//System.out.println("PlayerJumped");
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
			Vector3 newAvatarPosition = Vector3f.createFrom(
			 // Keep the X coordinate
			 localAvatarPosition.x(),
			 // The Y coordinate is the varying height
			 localAvatarPosition.y() + velocity,
			 //Keep the Z coordinate
			 localAvatarPosition.z()
			);
			// use avatar Local coordinates to set position, including height
			dolphinN.setLocalPosition(newAvatarPosition );
			
			//if(jumped)
			//velocity = velocity - acceleration;
			
			if ( dolphinN.getLocalPosition().y() <=  tessE.getWorldHeight(
						worldAvatarPosition.x(),
						worldAvatarPosition.z()))
					{
				// jumped = false;
				 //velocity = 0;
				 newAvatarPosition = Vector3f.createFrom(
						 // Keep the X coordinate
						 localAvatarPosition.x(),
						 // The Y coordinate is the varying height
						 tessE.getWorldHeight(
								 localAvatarPosition.x(),
								 localAvatarPosition.x()),
						 //Keep the Z coordinate
						 localAvatarPosition.z()
						);
				 
				 dolphinN.setLocalPosition(newAvatarPosition );
					}
	}*/

	public void setVelocity(float v)
	{
		velocity = v;
	}
	
	public void setJumped(boolean jump)
	{
		this.jumped = jump;
	}
	
	public boolean getJumped()
	{
		return jumped;
	}
	
	protected abstract void setupNodes(SceneManager sm) throws IOException;
	
	public void update(float elapsTime) {
		//TODO: stub
	}

	public float getSpeed() {
		return speed;
	}

	public boolean isBoostActive() {
		return boostActive;
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
	
}

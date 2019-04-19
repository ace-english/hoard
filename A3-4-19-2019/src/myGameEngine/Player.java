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
	dolphinN.setLocalPosition(newAvatarPosition);
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

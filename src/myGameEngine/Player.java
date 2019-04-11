package myGameEngine;

import java.util.UUID;

import net.java.games.input.Controller;
import ray.input.InputManager;
import ray.input.action.Action;
import ray.rage.scene.Camera;
import ray.rage.scene.SceneManager;
import ray.rage.scene.SceneNode;

public abstract class Player{

	float speed;
	boolean boostActive;
	private SceneNode node;
	private Camera camera;
	private ProtocolClient protClient;
	private UUID id;

	int score;
	
	public Player(SceneManager sm, Camera camera, ProtocolClient pc) {
		speed=0.08f;
		boostActive=false;
		score=0;
		this.protClient=pc;
		this.setCamera(camera);
		id=pc.getID();
		setupNodes(sm);
		
	}
	
	protected abstract void setupNodes(SceneManager sm);
	
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

package myGameEngine;

import java.io.IOException;
import java.util.UUID;

import hoardPVPGame.GameUtil;
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
	SceneManager sm;
	GameUtil.SKIN skin;

	int score;
	
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
	
	protected abstract void setupNodes(SceneManager sm) throws IOException;
	
	public void update(float elapsTime) {
		//TODO: stub
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
	
}

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
				node.moveForward(getSpeed()*value);
				break;
			case back:
				node.moveBackward(getSpeed()*value);
				break;
			case right:
				node.moveLeft(getSpeed()*value);
				break;
			case left:
				node.moveRight(getSpeed()*value);
				break;
			}
				
			if(protClient!=null)
				protClient.sendMoveMessage(id, node.getWorldPosition());
			
			return true;
			
	}
	
}

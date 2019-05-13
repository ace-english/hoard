package hoardPVPGame;

import ray.rage.scene.SceneNode;
import ray.rml.Vector3;
import ray.rml.Vector3f;

public abstract class Trap {
	
	private SceneNode trapNode;
	private Room room;
	private int cost=100;
	private static int numTraps=0;

	public Trap() {
		numTraps++;
	}
	
	public SceneNode getTrapNode() {
		return trapNode;
	}

	public void setTrapNode(SceneNode trapNode) {
		this.trapNode = trapNode;
	}

	public void delete() {
		room.getRoomNode().detachChild(trapNode);
		
	}
	
	public abstract boolean isColliding(Vector3 vector3);

	public int getCost() {
		return cost;
	}

	public void setCost(int cost) {
		this.cost = cost;
	}

	public static int getNumTraps() {
		return numTraps;
	}

	public abstract String getType();

	public abstract void update(float elapsTime);
	
	

}

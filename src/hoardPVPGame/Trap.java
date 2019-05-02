package hoardPVPGame;

import ray.rage.scene.SceneNode;

public abstract class Trap {
	
	private SceneNode trapNode;
	private Room room;
	private int cost;

	public Trap(Room room) {
		this.room=room;
		
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

}

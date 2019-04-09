package hoardPVPGame;

import ray.rage.Engine;
import ray.rage.scene.Node;
import ray.rage.scene.SceneManager;
import ray.rage.scene.SceneNode;

public class Dungeon {
	
	SceneNode roomGroup;
	private SceneManager sm;
	private Engine eng;
	
	public Dungeon(SceneManager sm, Engine eng) {
		this.sm = sm;
		this.eng = eng;
		roomGroup=sm.getRootSceneNode().createChildSceneNode("dungeon");
	}
	
	public static Dungeon load(String filename) {
		return null;
	}
	
	public void save(String filename) {
		
	}
	
	public SceneNode getNode() {
		return roomGroup;
	}
	
	public void addRoom() {
		new Room(sm, eng, this);
	}
	
	public Node getRoom(int id) {
		return roomGroup.getChild(id);
	}
	
	public int getRoomCount() {
		return Room.getRoomCount();
	}
	
	public Node getLastRoom() {
		return roomGroup.getChild(Room.getRoomCount()-1);
	}
	
	
}

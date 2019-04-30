package hoardPVPGame;

import java.io.IOException;
import java.util.ArrayList;

import ray.rage.Engine;
import ray.rage.scene.Node;
import ray.rage.scene.SceneManager;
import ray.rage.scene.SceneNode;

public class Dungeon {
	
	SceneNode roomGroup;
	ArrayList<Room> rooms;
	private SceneManager sm;
	private Engine eng;
	
	public Dungeon(SceneManager sm, Engine eng) {
		this.sm = sm;
		this.eng = eng;
		rooms=new ArrayList<Room>();
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
		rooms.add(new Room(sm, eng, this));
	}
	
	public Room getRoom(int id) {
		return rooms.get(id);
	}
	
	public int getRoomCount() {
		return Room.getRoomCount();
	}
	
	public Room getLastRoom() {
		return rooms.get(getRoomCount()-1);
	}
	
	public void finish() throws IOException {
		getLastRoom().close();
	}
	
	
}

package hoardPVPGame;

import java.io.IOException;
import java.util.ArrayList;

import ray.rage.Engine;
import ray.rage.scene.Node;
import ray.rage.scene.SceneManager;
import ray.rage.scene.SceneNode;
import ray.rml.Vector3;
import ray.rml.Vector3f;

public class Dungeon {
	
	private SceneNode roomGroup;
	private ArrayList<Room> rooms;
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
		System.out.println(getRoomCount());
		return rooms.get(getRoomCount()-1);
	}
	
	public void finish() throws IOException {
		getLastRoom().close();
	}

	public int getCurrentRoom(Vector3 localPosition){
		float x = localPosition.x();
		float center=roomGroup.getWorldPosition().x();
		if(x<center)
			return 0;
		int i=0;
		for(i=0; i<rooms.size(); i++) {
			center=rooms.get(i).getRoomNode().getWorldPosition().x();
			
			if (x<(center-(GameUtil.getRoomSize()/2))&&
					x>(center+(GameUtil.getRoomSize()/2))) {
				return i;
				
			}
		}
		return i;
	}

	public void removeLastRoom() {
		Room lastRoom=getLastRoom();
		roomGroup.detachChild(lastRoom.getRoomNode());
		lastRoom.delete();
		rooms.remove(rooms.size()-1);
		
	}
	/*
	public Vector3f getBounds() {
		float x, y, z;
		z=GameUtil.getRoomSize();
		y=GameUtil.getRoomSize()*2;
		x=GameUtil.getRoomSize()*2*getRoomCount();
		
		
		return (Vector3f) Vector3f.createFrom(x,y,z);
	}
	*/
	
	public boolean isInBounds(Vector3 vector3) {
		double minX, minZ, maxX, maxZ;
		maxX=(GameUtil.getRoomSize()-0.7f);
		minX=-maxX;
		minZ=minX;
		maxZ=((GameUtil.getRoomSize()*getRoomCount())-1f);
		double x=vector3.x();
		double z=vector3.z();
		//System.out.println("\nmaxz: "+maxZ+"room count: "+getRoomCount()+"minz: "+minZ+"z"+z);
		
		if(x<minX||x>maxX||z<minZ||z>maxZ)
			return false;
		
		return true;
	}
	
	
}

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
	private int cost;
	
	enum TRAP_TYPE{
		Spike, Pit, Swinging;
	}
	
	public Dungeon(SceneManager sm, Engine eng) {
		this.sm = sm;
		this.eng = eng;
		rooms=new ArrayList<Room>();
		roomGroup=sm.getRootSceneNode().createChildSceneNode("dungeon");
		cost=0;
	}
	
	public static Dungeon load(String filename) {
		return null;
	}
	
	public void save(String filename) {
		
	}
	
	public boolean addTrap(int roomNum, TRAP_TYPE type) {
		Room room=rooms.get(roomNum);
		if(room.HasTrap()) {
			removeTrap(roomNum);
		}
		if(cost+100>1000) {
			System.out.println("Over Budget");
			return false;
		}
		Trap trap;
		switch(type) {
		case Pit:
			trap=new PitTrap(room);
			break;
		case Spike:
			trap=new SpikeTrap(room);
			break;
		case Swinging:
			trap=new SwingingTrap(room);
			break;
		default:
			return false;
		}
		rooms.get(roomNum).setTrap(trap);
		cost+=100;
		return true;
	
	}
	
	public void removeTrap(int roomNum) {
		if(rooms.get(roomNum).HasTrap()) {
			rooms.get(roomNum).clear();
			cost-=100;
		}
	
	}
	
	public SceneNode getNode() {
		return roomGroup;
	}
	
	public void addRoom() {
		rooms.add(new Room(sm, eng, this));
		cost+=10;
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
	
	public boolean isInBounds(Vector3 vector3) {
		double minX, minZ, maxX, maxZ;
		maxX=(GameUtil.getRoomSize()-0.7f);
		minX=-maxX;
		minZ=minX;
		maxZ=((GameUtil.getRoomSize()*getRoomCount())-1f);
		double x=vector3.x();
		double z=vector3.z();
		
		if(x<minX||x>maxX||z<minZ||z>maxZ)
			return false;
		
		return true;
	}
	
	
}

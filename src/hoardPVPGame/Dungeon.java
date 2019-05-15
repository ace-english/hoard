package hoardPVPGame;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;

import net.java.games.input.AbstractController;
import ray.physics.PhysicsEngine;
import ray.physics.PhysicsObject;
import ray.rage.Engine;
import ray.rage.asset.texture.Texture;
import ray.rage.rendersystem.RenderSystem;
import ray.rage.rendersystem.Renderable.DataSource;
import ray.rage.rendersystem.shader.GpuShaderProgram;
import ray.rage.rendersystem.states.FrontFaceState;
import ray.rage.rendersystem.states.RenderState;
import ray.rage.rendersystem.states.TextureState;
import ray.rage.rendersystem.states.ZBufferState;
import ray.rage.scene.ManualObject;
import ray.rage.scene.ManualObjectSection;
import ray.rage.scene.Node;
import ray.rage.scene.SceneManager;
import ray.rage.scene.SceneNode;
import ray.rage.scene.controllers.RotationController;
import ray.rage.util.BufferUtil;
import ray.rml.Vector3;
import ray.rml.Vector3f;

public class Dungeon {
	
	private SceneNode roomGroup;
	private ArrayList<Room> rooms;
	private SceneManager sm;
	private Engine eng;
	private int cost;
	PhysicsObject physObj;
	
	public Dungeon(SceneManager sm, Engine eng) {
		this.sm = sm;
		this.eng = eng;
		rooms=new ArrayList<Room>();
		roomGroup=sm.getRootSceneNode().createChildSceneNode("dungeon");
		cost=0;
	}
	
	public int getCost() {
		return cost;
	}
	
	public boolean addTrap(int roomNum, GameUtil.TRAP_TYPE type, PhysicsEngine pe) {
		if(roomNum==0) {
			System.out.println("Can't trap the first room");
			return false;
		}
		Room room=rooms.get(roomNum);
		if(room.HasTrap()) {
			System.out.println("Already has trap");
			return false;
			//removeTrap(roomNum);
		}
		if(cost+100>1000) {
			System.out.println("Over Budget");
			return false;
		}
		Trap trap;
		switch(type) {
		case Pit:
			trap=new PitTrap(room, sm);
			break;
		case Spike:
			trap=new SpikeTrap(room, sm, pe);
			break;
		case Swinging:
			trap=new SwingingTrap(room, sm, pe);
			break;
		default:
			return false;
		}
		rooms.get(roomNum).setTrap(trap);
		cost+=100;
		return true;
	
	}
	
	public void update(float elapsTime) {
		for(Room room:rooms) {
			if(room.HasTrap())
				room.getTrap().update(elapsTime);
		}
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
		if(cost+10<=1000) {
		rooms.add(new Room(sm, eng, this));
		cost+=10;
		}
		else {
			System.out.println("Over Budget");
		}
	}
	
	public Room getRoom(int id) {
		return rooms.get(id);
	}
	
	public int getRoomCount() {
		return rooms.size();
	}
	
	public Room getLastRoom() {
		return rooms.get(getRoomCount()-1);
	}
	
	public void finish() throws IOException {
		addRoom();
		getLastRoom().close();
		try {
			createGem();
		}
		catch(Exception e) {
			
		}
	}

	public int getCurrentRoom(Vector3 localPosition){
		float z = localPosition.z();
		float center=roomGroup.getWorldPosition().z();
		if(z<center)
			return 0;
		int i=0;
		float min, max;
		for(i=0; i<rooms.size(); i++) {
			center=rooms.get(i).getRoomNode().getWorldPosition().z();
			min=center-(GameUtil.getRoomSize());
			max=center+(GameUtil.getRoomSize());
			
			if (z>(min)&&z<(max)) {
				return i;
				
			}
		}
		return i-1;
	}

	public void removeLastRoom() {
		Room lastRoom=getLastRoom();
		sm.getRootSceneNode().detachChild(lastRoom.getRoomNode());
		roomGroup.detachChild(lastRoom.getRoomNode());
		lastRoom.delete();
		rooms.remove(rooms.size()-1);
		
	}
	
	public boolean isInBounds(Vector3 vector3) {
		double minX, minZ, maxX, maxZ;
		maxX=(GameUtil.getRoomSize()-0.7f);
		minX=-maxX;
		minZ=minX;
		maxZ=((GameUtil.getRoomSize()*2*getRoomCount())-1f);
		double x=vector3.x();
		double z=vector3.z();
		
		if(x<minX||x>maxX||z<minZ||z>maxZ)
			return false;
		
		return true;
	}
	
	public void createGem() throws IOException {
		ManualObject gem = sm.createManualObject("Gem");
		ManualObjectSection gemSec =
		gem.createManualSection("GemSection");
		gem.setGpuShaderProgram(sm.getRenderSystem().
		getGpuShaderProgram(GpuShaderProgram.Type.RENDERING));
		float[] vertices = new float[]{
				//top
				1,1,3, 0,1.3f,0, -1,1,3, 
				-1,1,3, 0,1.3f,0, -3,1,1, 
				-3,1,1, 0,1.3f,0, -3,1,-1, 
				-3,1,-1, 0,1.3f,0, -1,1,-3, 
				-1,1,-3, 0,1.3f,0, 1,1,-3, 
				1,1,-3, 0,1.3f,0, 3,1,-1,
				3,1,-1, 0,1.3f,0, 3,1,1,
				3,1,1, 0,1.3f,0, 1,1,3,
				//sides
				1,1,3, -1,1,3, 0,0,4,
				0,0,4, -1,1,3, -3,0,3,
				-3,0,3, -1,1,3, -3,1,1,
				-3,1,1, -4,0,0, -3,0,3,
				-4,0,0, -3,1,1, -3,1,-1,
				-3,1,-1, -3,0,-3, -4,0,0,
				-3,1,-1, -1,1,-3, -3,0,-3,
				-3,0,-3, -1,1,-3, 0,0,-4,
				-1,1,-3, 1,1,-3, 0,0,-4,
				3,0,-3, 0,0,-4, 1,1,-3,
				1,1,-3, 3,1,-1, 3,0,-3,
				3,0,-3, 3,1,-1, 4,0,0,
				3,1,-1, 3,1,1, 4,0,0,
				4,0,0, 3,1,1, 3,0,3,
				3,1,1, 1,1,3, 3,0,3,
				0,0,4, 3,0,3, 1,1,3,
				
				//bottom
				
				3,0,-3, 4,0,0, 0,-4,0,
				0,0,-4, 3,0,-3, 0,-4,0,
				-3,0,-3, 0,0,-4, 0,-4,0,
				-3,0,-3, 0,-4,0, -4,0,0,
				-4,0,0, 0,-4,0, -3,0,3,
				-3,0,3, 0,-4,0, 0,0,4,
				3,0,3, 0,0,4, 0,-4,0,
				3,0,3, 0,-4,0, 4,0,0
				
				
		};
		float[] normals = new float[] {
				//top
				0,1,0, 0,1,0, 0,1,0,
				0,1,0, 0,1,0, 0,1,0,
				0,1,0, 0,1,0, 0,1,0,
				0,1,0, 0,1,0, 0,1,0,
				0,1,0, 0,1,0, 0,1,0,
				0,1,0, 0,1,0, 0,1,0,
				0,1,0, 0,1,0, 0,1,0,
				0,1,0, 0,1,0, 0,1,0,
				//sides
				0,1,1, 0,1,1, 0,1,1,
				-1,2,3, -1,2,3, -1,2,3,
				-1,2,1, -1,2,1, -1,2,1,
				-3,2,1, -3,2,1, -3,2,1,
				-1,1,0, -1,1,0, -1,1,0, 
				-3,2,-1, -3,2,-1, -3,2,-1, 
				-1,-4,-1, -1,-4,-1, -1,-4,-1, 
				-7,-4,-3, -7,-4,-3, -7,-4,-3, 
				0,1,-1, 0,1,-1, 0,1,-1, 
				1,2,-3, 1,2,-3, 1,2,-3, 
				1,2,-1, 1,2,-1, 1,2,-1,
				3,2,-1, 3,2,-1, 3,2,-1, 
				1,1,0, 1,1,0, 1,1,0, 
				3,2,1, 3,2,1, 3,2,1, 
				1,2,1, 1,2,1, 1,2,1, 
				1,2,3, 1,2,3, 1,2,3, 
				
				//bottom
				3,-3,-1, 3,-3,-1, 3,-3,-1, 
				1,-3,-3, 1,-3,-3, 1,-3,-3, 
				-1,-3,-3, -1,-3,-3, -1,-3,-3, 
				-3,-3,-1, -3,-3,-1, -3,-3,-1, 
				-3,-3,1, -3,-3,1, -3,-3,1,
				-1,-3,3, -1,-3,3, -1,-3,3, 
				1,-3,3, 1,-3,3, 1,-3,3, 
				3,-3,1, 3,-3,1, 3,-3,1

				
		};
		int[] indices = new int[vertices.length/3];
		for(int i=0; i<indices.length; i++) {
			indices[i]=i;
		}
        
        FloatBuffer vertBuf = BufferUtil.directFloatBuffer(vertices);
        //FloatBuffer texBuf = BufferUtil.directFloatBuffer(texcoords);
        FloatBuffer normBuf = BufferUtil.directFloatBuffer(normals);
		IntBuffer indexBuf = BufferUtil.directIntBuffer(indices);
		gemSec.setVertexBuffer(vertBuf);
		gemSec.setNormalsBuffer(normBuf);
		gemSec.setIndexBuffer(indexBuf);
		Texture tex = eng.getTextureManager().getAssetByPath("gem.png");
		TextureState texState = (TextureState)sm.getRenderSystem().
		createRenderState(RenderState.Type.TEXTURE);
		texState.setTexture(tex);
		FrontFaceState faceState = (FrontFaceState) sm.getRenderSystem().
		createRenderState(RenderState.Type.FRONT_FACE);
		gem.setDataSource(DataSource.INDEX_BUFFER);
		gem.setRenderState(texState);
		gem.setRenderState(faceState);
		
        SceneNode gemNode = roomGroup.createChildSceneNode("GemNode");
        gemNode.attachObject(gem);
        
        gemNode.moveUp(3f);
        gemNode.scale(.3f,.3f,.3f);
        
		
		RenderSystem rs = sm.getRenderSystem();
	     ZBufferState zstate = (ZBufferState) rs.createRenderState(RenderState.Type.ZBUFFER);
	     zstate.setTestEnabled(true);
	     gem.setRenderState(zstate);
	          
        /*
         * controllers
         */
    	
        RotationController rc = new RotationController(Vector3f.createUnitVectorY(), .05f);
        rc.addNode(gemNode); 
        sm.addController(rc);
	}
	
	public boolean isCollidingWithGem() {
		return false;
	}
	
	public boolean isCollidingWithTrap() {
		return false;
	}
	
	public ArrayList<Trap> getTraps(){
		ArrayList<Trap> traps=new ArrayList<Trap>();
		
		for(Room room:rooms) {
			if(room.HasTrap()) {
				traps.add(room.getTrap());
			}
		}
		return traps;
	}
	
	public String stringify() {
		System.out.println("Hello sending dungeon stuff");
		String ret="";
		ret+=rooms.size()+",";
		for(Room room:rooms) {
			if(room.HasTrap()) 
			{
				ret+=room.getTrap().getType();
			}
			else 
			{
				ret+="none";
			}
			ret+=",";
		}
		return ret;
	}

	public boolean touchingGem(Vector3 knight) {
		Vector3 gem=roomGroup.getWorldPosition().add(0, 3f, 0);
		if(((Math.abs(knight.x()-gem.x()))<.5)
			&&(Math.abs(knight.z()-gem.z())<.5))
				return true;
		return false;
	}
	
}

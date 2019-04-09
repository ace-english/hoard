package hoardPVPGame;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import ray.rage.Engine;
import ray.rage.asset.texture.Texture;
import ray.rage.rendersystem.Renderable.DataSource;
import ray.rage.rendersystem.Renderable.Primitive;
import ray.rage.rendersystem.shader.GpuShaderProgram;
import ray.rage.rendersystem.states.FrontFaceState;
import ray.rage.rendersystem.states.RenderState;
import ray.rage.rendersystem.states.TextureState;
import ray.rage.scene.*;
import ray.rage.util.BufferUtil;
import ray.rml.Degreef;
import ray.rml.Vector3f;

public class Room{
	private SceneNode roomNode;
	private Trap trap;
	private boolean hasTrap;
	private SceneManager sm;
	private Engine eng;
	private static int roomNum=0;
	private int planeNum;
	
	public Room(SceneManager sm, Engine eng, Dungeon dungeon) {
		this.sm = sm;
		this.eng = eng;
		hasTrap=false;
		roomNode=dungeon.getNode();
		planeNum=0;
		try {
			setupRoom();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Room.roomNum++;
	}
	
	public static int getRoomCount() {
		return roomNum;
	}
	
	private SceneNode returnPlane() throws IOException {
    	ManualObject plane=sm.createManualObject("planeobj"+roomNum+'-'+planeNum);
    	planeNum++;
        ManualObjectSection psec=plane.createManualSection("psec");
        plane.setGpuShaderProgram(sm.getRenderSystem().getGpuShaderProgram(GpuShaderProgram.Type.RENDERING));
        
        float[] vertices= new float[] {
        	-1,0,-1,
        	-1,0,1,
        	1,0,1,
            1,0,-1
        	
        };
        float[] texcoords = new float[]
        		{ 0,0,
        		  0,1,
        		  1,1,
        		  1,0,
        		  0,1,
        		  1,1
        		  
        		};
        		
        				
		int[] indices = new int[] { 0,1,2,3,0,2};
        
        FloatBuffer vertBuf = BufferUtil.directFloatBuffer(vertices);
        FloatBuffer texBuf = BufferUtil.directFloatBuffer(texcoords);
		IntBuffer indexBuf = BufferUtil.directIntBuffer(indices);
		psec.setVertexBuffer(vertBuf);
		psec.setTextureCoordsBuffer(texBuf);
		psec.setIndexBuffer(indexBuf);
		Texture tex =
		eng.getTextureManager().getAssetByPath("floor.png");
		TextureState texState = (TextureState)sm.getRenderSystem().
		createRenderState(RenderState.Type.TEXTURE);
		texState.setTexture(tex);
		FrontFaceState faceState = (FrontFaceState) sm.getRenderSystem().
		createRenderState(RenderState.Type.FRONT_FACE);
		plane.setDataSource(DataSource.INDEX_BUFFER);
		plane.setRenderState(texState);
		plane.setRenderState(faceState);
		
		plane.setPrimitive(Primitive.TRIANGLES);
		
        SceneNode planeNode = roomNode.createChildSceneNode("plane" + roomNum + "-" + planeNum);
        planeNum++;
        planeNode.scale(GameUtil.getRoomSize(), GameUtil.getRoomSize(), GameUtil.getRoomSize());
        planeNode.attachObject(plane);
        
        return planeNode;
    }
    
    private SceneNode returnWall() throws IOException {
    	ManualObject plane=sm.createManualObject("planeobj"+roomNum+'-'+planeNum);
    	planeNum++;
        ManualObjectSection psec=plane.createManualSection("psec");
        plane.setGpuShaderProgram(sm.getRenderSystem().getGpuShaderProgram(GpuShaderProgram.Type.RENDERING));
        
        float[] vertices= new float[] {
        	-1,0,0,
        	-1,0,1,
        	1,0,1,
            1,0,0
        	
        };
        float[] texcoords = new float[]
        		{ 0,0,
        		  0,1,
        		  1,1,
        		  1,0,
        		  0,1,
        		  1,1
        		  
        		};
        		
        				
		int[] indices = new int[] { 0,1,2,3,0,2};
        
        FloatBuffer vertBuf = BufferUtil.directFloatBuffer(vertices);
        FloatBuffer texBuf = BufferUtil.directFloatBuffer(texcoords);
		IntBuffer indexBuf = BufferUtil.directIntBuffer(indices);
		psec.setVertexBuffer(vertBuf);
		psec.setTextureCoordsBuffer(texBuf);
		psec.setIndexBuffer(indexBuf);
		Texture tex =
		eng.getTextureManager().getAssetByPath("wall.png");
		TextureState texState = (TextureState)sm.getRenderSystem().
		createRenderState(RenderState.Type.TEXTURE);
		texState.setTexture(tex);
		FrontFaceState faceState = (FrontFaceState) sm.getRenderSystem().
		createRenderState(RenderState.Type.FRONT_FACE);
		plane.setDataSource(DataSource.INDEX_BUFFER);
		plane.setRenderState(texState);
		plane.setRenderState(faceState);
		
		plane.setPrimitive(Primitive.TRIANGLES);
		
        SceneNode planeNode = roomNode.createChildSceneNode("plane"+roomNum+'-' + planeNum);
        planeNum++;
        planeNode.scale(GameUtil.getRoomSize(), GameUtil.getRoomSize(), GameUtil.getRoomSize());
        planeNode.attachObject(plane);
        
        return planeNode;
    }
    
    private void setupRoom() throws IOException {
    	planeNum=0;
    	roomNode = sm.getRootSceneNode().createChildSceneNode("room"+roomNum);
    	SceneNode floor, ceiling, left, right;
    	
    	floor=returnPlane();
    	right=returnWall();
    	right.translate(Vector3f.createFrom(GameUtil.getRoomSize(), 0f, 0f));
    	right.rotate(Degreef.createFrom(90f), Vector3f.createFrom(0f, 0f, 1f));
    	right.rotate(Degreef.createFrom(90f), Vector3f.createFrom(0f, 1f, 0f));
    	left=returnWall();
    	left.translate(Vector3f.createFrom(-GameUtil.getRoomSize(), GameUtil.getRoomSize(), 0f));
    	left.rotate(Degreef.createFrom(90f), Vector3f.createFrom(0f, 0f, -1f));
    	left.rotate(Degreef.createFrom(90f), Vector3f.createFrom(0f, 1f, 0f));
    	ceiling=returnPlane();
    	ceiling.rotate(Degreef.createFrom(180f), Vector3f.createFrom(0f,0f,1f));
    	ceiling.translate(Vector3f.createFrom(0f,GameUtil.getRoomSize(), 0f));
    	
    	//if first room, give back wall for hoard
    	if(roomNum==0) {
    		SceneNode back=returnWall();
        	back.translate(Vector3f.createFrom(0f, GameUtil.getRoomSize(), -GameUtil.getRoomSize()));
        	back.rotate(Degreef.createFrom(90f), Vector3f.createFrom(1f, 0f, 0f));
    	}
    	
    	roomNode.translate(Vector3f.createFrom(0f,0f,GameUtil.getRoomSize()*roomNum));
    	roomNum++;
    		
    	
    }

	public SceneNode getRoomNode() {
		return roomNode;
	}

	public Trap getTrap() {
		if(hasTrap)
			return trap;
		else return null;
	}

	public void setTrap(Trap trap) {
		this.trap = trap;
		trap.addToSceneNode(this);
		hasTrap=true;
	}

	public boolean HasTrap() {
		return hasTrap;
	}
	
	
	
	
	
}

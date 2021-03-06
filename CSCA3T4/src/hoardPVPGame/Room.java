package hoardPVPGame;

import java.awt.Color;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import ray.rage.Engine;
import ray.rage.asset.texture.Texture;
import ray.rage.asset.texture.TextureManager;
import ray.rage.rendersystem.RenderSystem;
import ray.rage.rendersystem.Renderable.DataSource;
import ray.rage.rendersystem.Renderable.Primitive;
import ray.rage.rendersystem.shader.GpuShaderProgram;
import ray.rage.rendersystem.states.FrontFaceState;
import ray.rage.rendersystem.states.RenderState;
import ray.rage.rendersystem.states.TextureState;
import ray.rage.rendersystem.states.ZBufferState;
import ray.rage.scene.*;
import ray.rage.util.BufferUtil;
import ray.rml.Angle;
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
	private Light light;
	
	private SceneNode floor, ceiling, leftWall, rightWall;
	
	
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
	}
	/*
	public static int getRoomCount() {
		return roomNum;
	}
	*/
	
	private SceneNode returnPlane() throws IOException {
    	ManualObject plane=sm.createManualObject("planeobj"+roomNum+'-'+planeNum);
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
		
		RenderSystem rs = sm.getRenderSystem();
	    ZBufferState zstate = (ZBufferState) rs.createRenderState(RenderState.Type.ZBUFFER);
	    zstate.setTestEnabled(true);
	    plane.setRenderState(zstate);
		
        SceneNode planeNode = roomNode.createChildSceneNode("plane" + roomNum + "-" + planeNum);
        planeNum++;
        planeNode.scale(GameUtil.getRoomSize(), GameUtil.getRoomSize(), GameUtil.getRoomSize());
        planeNode.attachObject(plane);
        
        return planeNode;
    }
    
    private SceneNode returnWall() throws IOException {
    	ManualObject plane=sm.createManualObject("planeobj"+roomNum+'-'+planeNum);
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
		
		RenderSystem rs = sm.getRenderSystem();
	    ZBufferState zstate = (ZBufferState) rs.createRenderState(RenderState.Type.ZBUFFER);
	    zstate.setTestEnabled(true);
	    plane.setRenderState(zstate);
		
        SceneNode planeNode = roomNode.createChildSceneNode("plane"+roomNum+'-' + planeNum);
        
        planeNum++;
        planeNode.scale(GameUtil.getRoomSize(), GameUtil.getRoomSize(), GameUtil.getRoomSize());
        planeNode.attachObject(plane);
        return planeNode;
        
    }
    
    private void createLightNode(SceneNode rootNode) throws IOException {
		light = createLight(rootNode.getName()+"torch");
		
		Entity torch = sm.createEntity(rootNode.getName()+"torchE", "torch.obj");
	    torch.setPrimitive(Primitive.TRIANGLES);
	       
	    TextureManager tm=sm.getTextureManager();
	    Texture texture=tm.getAssetByPath("light.png");
	    RenderSystem rs = sm.getRenderSystem();
	    TextureState state=(TextureState) rs.createRenderState(RenderState.Type.TEXTURE);
	    state.setTexture(texture);
	    torch.setRenderState(state);
	        
	    	
		SceneNode lightNode = rootNode.createChildSceneNode(light.getName()+"Node");
	    lightNode.attachObject(light);
	    //lightNode.rotate(Degreef.createFrom(90f), Vector3f.createFrom(1f, 0f, 0f));
	    lightNode.attachObject(torch);
	    lightNode.moveUp(.17f);
	    lightNode.moveForward(0.75f);
	    lightNode.scale(0.1f,0.1f, 0.1f);
	    lightNode.rotate(Degreef.createFrom(90f), Vector3f.createFrom(1f, 0f, 0f));
	        
    }
    
    private Light createLight(String name) {
    	Light light=sm.createLight(name, Light.Type.POINT);
    	
    	light.setAmbient(new Color(.11f, .11f, .10f));
    	light.setDiffuse(new Color(.85f, .65f, .5f));
    	light.setSpecular(new Color(0.5f, 0.4f, 0.3f));
    	light.setRange(1f);
    	/*
    	light.setAmbient(new Color(.1f, .1f, .03f));
    	light.setDiffuse(new Color(.85f, .65f, .5f));
    	light.setSpecular(new Color(0.8f, 0.7f, 0.6f));
    	light.setRange(1f);
    	*/
    	
	    
	    return light;
    }
    
    private void setupRoom() throws IOException {
    	planeNum=0;
    	roomNode = sm.getRootSceneNode().createChildSceneNode("room"+roomNum);
    	
    	floor=returnPlane();
    	rightWall=returnWall();
    	rightWall.translate(Vector3f.createFrom(GameUtil.getRoomSize(), 0f, 0f));
    	rightWall.rotate(Degreef.createFrom(90f), Vector3f.createFrom(0f, 0f, 1f));
    	rightWall.rotate(Degreef.createFrom(90f), Vector3f.createFrom(0f, 1f, 0f));
    	
    	leftWall=returnWall();
    	leftWall.translate(Vector3f.createFrom(-GameUtil.getRoomSize(), 0, 0f));
    	leftWall.rotate(Degreef.createFrom(90f), Vector3f.createFrom(0f, 0f, 1f));
    	leftWall.rotate(Degreef.createFrom(90f), Vector3f.createFrom(0f, 1f, 0f));
    	leftWall.rotate(Degreef.createFrom(180f), Vector3f.createFrom(0f, 0f, 1f));
    	
    	ceiling=returnPlane();
    	ceiling.rotate(Degreef.createFrom(180f), Vector3f.createFrom(0f,0f,1f));
    	ceiling.translate(Vector3f.createFrom(0f,GameUtil.getRoomSize(), 0f));
    	
    
        
        if((roomNum%8)==0) {
        	createLightNode(rightWall);
        }
        else if((roomNum%4)==0) {
        	createLightNode(leftWall);
        }
        
       
    	
    	//if first room, give back wall for hoard
    	if(roomNum==0) {
    		SceneNode back=returnWall();
        	back.translate(Vector3f.createFrom(0f, GameUtil.getRoomSize(), -GameUtil.getRoomSize()));
        	back.rotate(Degreef.createFrom(90f), Vector3f.createFrom(1f, 0f, 0f));
    	}
    	
    	roomNode.translate(Vector3f.createFrom(0f,0f,GameUtil.getRoomSize()*roomNum*2));
    	roomNum++;
    		
    	
    }
    
    public void close() throws IOException {
    	SceneNode back=returnWall();
    	back.translate(Vector3f.createFrom(0f, 0, GameUtil.getRoomSize()));
    	back.rotate(Degreef.createFrom(90f), Vector3f.createFrom(-1f, 0f, 0f));
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
		hasTrap=true;
	}

	public boolean HasTrap() {
		return hasTrap;
	}

	public void toggleLights() {
		if(light==null) {
			System.out.println("no lights here");
		}
		else {
			light.setVisible(!light.isVisible());
			System.out.println("light: " +light.isVisible());
		}
	}

	public void clear() {
		if(hasTrap) {
			sm.destroySceneNode(trap.getTrapNode());
			hasTrap=false;
		}
		
	}

	public void delete() {
		//roomNum--;
		sm.destroySceneNode(floor);
		sm.destroySceneNode(ceiling);
		sm.destroySceneNode(leftWall);
		sm.destroySceneNode(rightWall);
		getRoomNode().detachAllChildren();
		sm.destroySceneNode(getRoomNode());
		
	}
	
	
	
	
	
}

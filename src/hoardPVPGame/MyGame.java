package hoardPVPGame;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.io.*;
import java.net.InetAddress;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.rmi.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;
import java.util.Vector;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.script.Invocable;

import myGameEngine.*;
import net.java.games.input.Controller;
import ray.input.*;
import ray.input.action.AbstractInputAction;
import ray.networking.IGameConnection.ProtocolType;
import ray.rage.*;
import ray.rage.asset.texture.*;
import ray.rage.game.*;
import ray.rage.rendersystem.*;
import ray.rage.rendersystem.Renderable.*;
import ray.rage.scene.*;
import ray.rage.scene.Camera.Frustum.*;
import ray.rage.scene.controllers.*;
import ray.rage.util.BufferUtil;
import ray.rage.util.Configuration;
import ray.rml.*;
import ray.rage.rendersystem.gl4.GL4RenderSystem;
import ray.rage.rendersystem.shader.GpuShaderProgram;
import ray.rage.rendersystem.states.*;
import ray.networking.IGameConnection.ProtocolType;

public class MyGame extends VariableFrameRateGame{

	// to minimize variable allocation in update()
	GL4RenderSystem rs;
	SceneManager sm;
	
	float elapsTime = 0.0f;
	String elapsTimeStr, dispStr;
	
	int width=1000, height=700;
	
	private String serverAddress;
	private int serverPort;
	private ProtocolType serverProtocol;
	private ProtocolClient protClient;
	private boolean isClientConnected;
	private Vector<UUID> gameObjectsToRemove;
    private Dungeon dungeon;
    
    private boolean playerIsDragon=false;

    
    private static final String SKYBOX_NAME = "SkyBox";
	
	
	int elapsTimeSec;
	private InputManager im;
	Player player;
	AbstractController rc;
	boolean isConnected = false;
	
	
	public Player getPlayer() {
		return player;
	}

    public MyGame(String serverAddr, int sPort) {
        super();
        System.out.println("ip: " + serverAddr);
        System.out.println("port: " + sPort);
        sm=this.getEngine().getSceneManager();
        this.serverAddress = serverAddr;
        this.serverPort = sPort;
        this.serverProtocol = ProtocolType.UDP;
        System.out.println("ip: " + this.serverAddress);
        System.out.println("port: " + this.serverPort);
        
        gameObjectsToRemove = new Vector<UUID>();
    }
	public MyGame()
	{
		super();
	}

    public static void main(String[] args) {
    	System.out.println("helo");
    	MyGame game;
    	if(args.length > 0)
    	{
    		game = new MyGame(args[0], Integer.parseInt(args[1]));
    	}
    	else
    	{
    		game = new MyGame("", 0);
    	}
    	
        try {
            game.startup();
            game.run();
        } catch (Exception e) {
            e.printStackTrace(System.err);
        } finally {
            game.shutdown();
            game.exit();
        }
    }

	private void executeScript(ScriptEngine jsEngine, String scriptFileName) {
		try{
			FileReader fileReader = new FileReader(scriptFileName);
			jsEngine.eval(fileReader); //execute the script statements in the file
			fileReader.close();
		}
		catch (FileNotFoundException e1){ 
			System.out.println(scriptFileName + " not found " + e1); 
		}catch (IOException e2){ 
			System.out.println("IO problem with " + scriptFileName + e2); 
		}catch (ScriptException e3){ 
			System.out.println("ScriptException in " + scriptFileName + e3); 
		}catch (NullPointerException e4){ 
			System.out.println ("Null ptr exception in " + scriptFileName + e4); 
		}
	}

	private void setupNetworking() {
    	gameObjectsToRemove = new Vector<UUID>();
    	isClientConnected = false;
    	try {
    		protClient=new ProtocolClient(InetAddress.
    				getByName(serverAddress), serverPort, serverProtocol, this);
    	}catch (UnknownHostException e) {e.printStackTrace();
    	}catch (IOException e) {e.printStackTrace();
    	}
    	
    	if(protClient == null) {
    		System.out.println("Missing protocol host");
    	}else {
    		protClient.sendJoinMessage();
    	}
    	
    }
	
	@Override
	protected void setupWindow(RenderSystem rs, GraphicsEnvironment ge) {
		rs.createRenderWindow(new DisplayMode(width, height, 24, 60), false);
	}
	

    @Override
    protected void setupCameras(SceneManager sm, RenderWindow rw) {
        SceneNode rootNode = sm.getRootSceneNode();
        Camera camera = sm.createCamera("MainCamera", Projection.PERSPECTIVE);
        rw.getViewport(0).setCamera(camera);
        
        SceneNode cameraN=rootNode.createChildSceneNode("MainCameraNode");
        cameraN.attachObject(camera);
        camera.setMode('n');
        camera.getFrustum().setFarClipDistance(1000f);
    }
    
	
    private void setupTerrain()
	{  
		SceneManager sm = this.getEngine().getSceneManager();
		Tessellation tessE = sm.createTessellation("tessE", 6);
		
		//Tessellation tessE = this.getEngine().getSceneManager().createTessellation("tessE", 6);
		// subdivisions per patch: min=0, try up to 32
		tessE.setSubdivisions(8f);
		SceneNode tessN =
		sm.getRootSceneNode().
		createChildSceneNode("tessN");
		tessN.attachObject(tessE);
		// to move it, note that X and Z must BOTH be positive OR negative
		// tessN.translate(Vector3f.createFrom(-6.2f, -2.2f, 2.7f));
		// tessN.yaw(Degreef.createFrom(37.2f));
		tessN.scale(100, 500, 100);
		tessN.translate(-40, 0, 0);
		tessE.setHeightMap(this.getEngine(), "heightmap.jpg");
		tessE.setTexture(this.getEngine(), "terrain.jpg");
		// tessE.setNormalMap(. . .)
	}
	
    
    @Override
    protected void setupScene(Engine eng, SceneManager sm) throws IOException {
    	setupNetworking();
    	setupTerrain();
        dungeon=new Dungeon(this.getEngine().getSceneManager(), getEngine());
        
    	
    	/*
    	 * player
    	 */
        if(playerIsDragon)
            player = new FreeMovePlayer(sm, protClient, dungeon);
        else
        	player = new OrbitalPlayer(sm, protClient);
        
        
        /*
         * dungeon
         */
        dungeon.addRoom();
        
        if(!isClientConnected&&!playerIsDragon) {
        	ScriptEngineManager factory = new ScriptEngineManager();
        	ScriptEngine jsEngine = factory.getEngineByName("js");
        	
        	jsEngine.put("dungeon", dungeon);
        	this.executeScript(jsEngine, "src/randomDungeon.js");
        	
        }
		
        /*
         * skybox
         */
        
        Configuration conf = eng.getConfiguration();
        TextureManager tm = getEngine().getTextureManager();
        tm.setBaseDirectoryPath(conf.valueOf("assets.skyboxes.path"));
        Texture front = tm.getAssetByPath("countryside1.png");
        Texture back = tm.getAssetByPath("countryside3.png");
        Texture left = tm.getAssetByPath("countryside4.png");
        Texture right = tm.getAssetByPath("countryside2.png");
        Texture top = tm.getAssetByPath("countryside_top.png");
        Texture bottom = tm.getAssetByPath("countryside_bottom.png");
         tm.setBaseDirectoryPath(conf.valueOf("assets.textures.path"));
        // cubemap textures are flipped upside-down.
        // All textures must have the same dimensions, so any image’s
        // heights will work since they are all the same height
        AffineTransform xform = new AffineTransform();
        xform.translate(0, front.getImage().getHeight());
        xform.scale(1d, -1d);
        front.transform(xform);
        back.transform(xform);
        left.transform(xform);
        right.transform(xform);
        top.transform(xform);
        bottom.transform(xform);
        SkyBox sb = sm.createSkyBox(SKYBOX_NAME);
        sb.setTexture(front, SkyBox.Face.FRONT);
        sb.setTexture(back, SkyBox.Face.BACK);
        sb.setTexture(left, SkyBox.Face.LEFT);
        sb.setTexture(right, SkyBox.Face.RIGHT);
        sb.setTexture(top, SkyBox.Face.TOP);
        sb.setTexture(bottom, SkyBox.Face.BOTTOM);
        sm.setActiveSkyBox(sb);
        
        
        
        
        /*
         * gem
         */
        
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
		
        SceneNode gemNode = sm.getRootSceneNode().createChildSceneNode("GemNode");
        gemNode.attachObject(gem);
        
        gemNode.moveUp(3f);
        gemNode.scale(.3f,.3f,.3f);
        
		gemNode.setLocalPosition(-3f, 1.0f, 0f);
		
		


        
        /*
         * controllers
         */
    	
        rc = new RotationController(Vector3f.createUnitVectorY(), .05f);
        rc.addNode(gemNode); 
        sm.addController(rc);
        
        /*
         * light
         */
        sm.getAmbientLight().setIntensity(new Color(.3f, .3f, .3f));
        
        
    	setupInputs();

      
    }
    
	@Override
    protected void update(Engine engine) {
		// build and set HUD
		rs = (GL4RenderSystem) engine.getRenderSystem();
		
		elapsTime += engine.getElapsedTimeMillis();
		elapsTimeSec = Math.round(elapsTime/1000.0f);
		elapsTimeStr = Integer.toString(elapsTimeSec);
		

		dispStr="Time = " + elapsTimeStr + " Score: "+player.getScore();
		rs.setHUD(dispStr, 15, 15);
		if(player.isBoostActive()) dispStr+=" Boost Active!";
		im.update(elapsTime);
		processNetworking(elapsTime);
		
		checkForCollisions();
		player.update(elapsTimeSec);
		
		/* check if player jumped*/
		
	}

	private void processNetworking(float elapsTime) {
		if(protClient != null) {
			protClient.processPackets();
		}
		Iterator<UUID> it = gameObjectsToRemove.iterator();
		while(it.hasNext()) {
			sm.destroySceneNode(it.next().toString());
		}
		gameObjectsToRemove.clear();
		
	}
	
	@Override
	public void exit() {
		protClient.sendByeMessage();
		super.exit();
	}

	/*
	 * calls functions in Player to handle action setup
	 */
	protected void setupInputs() {
    	im=new GenericInputManager();
    	ArrayList<Controller> controllers=im.getControllers();
    	
    	for(Controller controller:controllers) {
    		player.setupInputs(im, controller);
    	}
    			
    	
    }

	
	private void checkForCollisions() {
		//TODO stub
			
	}
	

	public void setIsConnected(boolean b) {
		isClientConnected=b;
		
	}

	public Vector3 getPlayerPosition() {
		return player.getNode().getWorldPosition();
	}
	
	public Matrix3 getRotation()
	{
		return player.getNode().getLocalRotation();
	}
	
	public void addGhostAvatarToGameWorld(GhostAvatar avatar) throws Exception{
		if(avatar!=null) {
			try {
				System.out.println("Drawing ghost");
				if(sm==null)
					sm=this.getEngine().getSceneManager();
				//Entity ghostE=sm.createEntity("playerEntity"+avatar.getID(), "dolphinHighPoly.obj");
				//Entity ghostE=sm.createEntity("playerEntity"+avatar.getID(), "dragon1.obj");
				Entity ghostE=sm.createEntity("playerEntity"+avatar.getID(), "boxMan9.obj");
				ghostE.setPrimitive(Primitive.TRIANGLES);
				
				TextureManager tm=sm.getTextureManager();
		        //Texture texture=tm.getAssetByPath("dragon1.png");
		        Texture texture=tm.getAssetByPath("boxMan4.png");
		    	RenderSystem rs = sm.getRenderSystem();
		    	TextureState state=(TextureState) rs.createRenderState(RenderState.Type.TEXTURE);
		    	state.setTexture(texture);
		    	ghostE.setRenderState(state);
				
				SceneNode ghostN = sm.getRootSceneNode().createChildSceneNode("playerNode"+avatar.getID());
				ghostN.attachObject(ghostE);
				avatar.setNode(ghostN);
				avatar.setEntity(ghostE);
				avatar.setPos(avatar.getPos().add(0,1,0));
				ghostN.setLocalPosition(avatar.getPos());
				ghostN.setLocalRotation(avatar.getRot());
				
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public SceneNode getPlayerNode()
	{
		
		return player.getNode();
	}
	
	public void removeGhostAvatarFromGameWorld(GhostAvatar avatar) {
		if(avatar!=null)
			gameObjectsToRemove.add((UUID) avatar.getID());
	}
	
	private class SendCloseConnectionPacketAction extends AbstractInputAction{
		@Override
		public void performAction(float time, net.java.games.input.Event arg1) {
			if(protClient != null && isClientConnected == true){ 
				protClient.sendByeMessage();
				}
			
		} 
	}
	
	
}


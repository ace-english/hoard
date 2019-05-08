package hoardPVPGame;

import java.awt.*;
import java.awt.event.*;
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

import hoardPVPGame.Dungeon.TRAP_TYPE;
import hoardPVPGame.GameUtil.SKIN;

import javax.script.Invocable;

import myGameEngine.*;
import net.java.games.input.Controller;
import ray.input.*;
import ray.input.action.AbstractInputAction;
import ray.networking.IGameConnection.ProtocolType;
import ray.physics.PhysicsEngine;
import ray.physics.PhysicsEngineFactory;
import ray.physics.PhysicsObject;
import ray.rage.*;
import ray.rage.asset.texture.*;
import ray.rage.game.*;
import ray.rage.rendersystem.*;
import ray.rage.rendersystem.Renderable.*;
import ray.rage.scene.*;
import ray.rage.scene.Camera.Frustum.*;
import ray.rage.scene.SkeletalEntity.EndType;
import ray.rage.scene.controllers.*;
import ray.rage.util.BufferUtil;
import ray.rage.util.Configuration;
import ray.rml.*;
import ray.rage.rendersystem.gl4.GL4RenderSystem;
import ray.rage.rendersystem.shader.GpuShaderProgram;
import ray.rage.rendersystem.states.*;

enum GAME_MODE 
{ 
    SPLASH, CHAR_SELECT, BUILD, SEIGE; 
}
enum PLAYER_TYPE{
	DRAGON, KNIGHT;
}
enum ONLINE_TYPE{
	ONLINE, OFFLINE;
}

public class MyGame extends VariableFrameRateGame implements MouseListener{

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
    private GAME_MODE gameMode=GAME_MODE.SPLASH;
    private PLAYER_TYPE playerType;
    private ONLINE_TYPE onlineType;
    private boolean avatarWalking = false;

    private HUD hud;
    private NPCController npcController;
    
    private static final String SKYBOX_NAME = "SkyBox";
	
	
	int elapsTimeSec;
	private InputManager im;
	Player player;
	AbstractController rc;
	boolean isConnected = false;
	
	private SceneNode ball1Node, ball2Node, gndNode;///////////
	private SceneNode cameraPositionNode;/////////////
	private final static String GROUND_E = "Ground";//////////////
	private final static String GROUND_N = "GroundNode";/////////////
	private PhysicsEngine physicsEng;//////////
	private PhysicsObject ball1PhysObj, ball2PhysObj, gndPlaneP, knightPhysObj;//////////
	private boolean running = false;////////////
	private UUID ghostID;
	private GhostAvatar gAvatar;
	private boolean avatarExists = false;
	float dir = 0.10f;
	int dIter = 0;
	
	
	public Player getPlayer() {
		return player;
	}

    public MyGame(String serverAddr, int sPort) {
        super();
        this.serverAddress = serverAddr;
        this.serverPort = sPort;
        this.serverProtocol = ProtocolType.UDP;
        
        gameObjectsToRemove = new Vector<UUID>();
    }
	public MyGame()
	{
		super();
	}

    public static void main(String[] args) {
        //MyGame game = new MyGame(args[0], Integer.parseInt(args[1]));
    	MyGame game = new MyGame("", 0);
        try {
            game.startup();
            game.run();
        } catch (Exception e) {
            e.printStackTrace(System.err);
        } finally {
        	System.out.println("Shutting Down");
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
    	System.out.println("Setting up cameras");
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
		tessN.translate(-40,0,0);
		tessE.setHeightMap(this.getEngine(), "heightmap.jpg");
		tessE.setTexture(this.getEngine(), "terrain.jpg");
		// tessE.setNormalMap(. . .)
	}
	
	private void initPhysicsSystem()
    { 
    	String engine = "ray.physics.JBullet.JBulletPhysicsEngine";
    	float[] gravity = {0, -3f, 0};
    	physicsEng = PhysicsEngineFactory.createPhysicsEngine(engine);
    	physicsEng.initSystem();
    	physicsEng.setGravity(gravity);
    }
	
	public void playWalkAnimation() {
		System.out.println("playing animation");
		
		//walkingMutex.acquire();
		SkeletalEntity manSE =
				(SkeletalEntity) this.getEngine().getSceneManager().getEntity("knightSkeleton");
		/*skeleton.playAnimation("walkAnimation", 0.5f, EndType.STOP, 0);
		
		skeleton.stopAnimation();
		skeleton.playAnimation("walkAnimation", 0.5f, EndType.LOOP, 0);*/
		
		//manSE.playAnimation("walkAnimation", 0.5f, EndType.STOP, 0);
		
		manSE.stopAnimation();
		manSE.playAnimation("walkAnimation", 0.5f, EndType.LOOP, 0);
		//walkingMutex.release();
		
		//animation started = true
		avatarWalking = true;
		
	}
	
	private void createRagePhysicsWorld()
    { 
    	float mass = 1.0f;
    	float up[] = {0,1,0};
    	double[] temptf;
    	temptf = toDoubleArray(ball1Node.getLocalTransform().toFloatArray());
    	ball1PhysObj = physicsEng.addSphereObject(physicsEng.nextUID(),
    	mass, temptf, 2.0f);
    	ball1PhysObj.setBounciness(1.0f);
    	ball1Node.setPhysicsObject(ball1PhysObj);
    	temptf = toDoubleArray(ball2Node.getLocalTransform().toFloatArray());
    	ball2PhysObj = physicsEng.addSphereObject(physicsEng.nextUID(),
    			mass, temptf, 2.0f);
    		ball2PhysObj.setBounciness(1.0f);
    	ball2Node.setPhysicsObject(ball2PhysObj);
    	temptf = toDoubleArray(gndNode.getLocalTransform().toFloatArray());
    	gndPlaneP = physicsEng.addStaticPlaneObject(physicsEng.nextUID(),
    	temptf, up, 0.0f);
    	gndPlaneP.setBounciness(1.0f);
    	gndNode.scale(3f, .05f, 3f);
    	gndNode.setLocalPosition(0, -7, -2);
    	gndNode.setPhysicsObject(gndPlaneP);
    	
    	System.out.println("player.getNode().getLocalTransform(): " + player.getNode().getLocalTransform());
    	temptf = toDoubleArray(player.getNode().getLocalTransform().toFloatArray());
    	knightPhysObj = physicsEng.addSphereObject(physicsEng.nextUID(),
    	    	mass, temptf, 2.0f);
    	    	knightPhysObj.setBounciness(1.0f);
    	    	player.getNode().setPhysicsObject(knightPhysObj);
    	
    	/*public void keyPressed(KeyEvent e)
    	{ switch (e.getKeyCode())
    	 { case KeyEvent.VK_SPACE:
    	System.out.println("Starting Physics!");
    	running = true;
    	break;
    	}
    	super.keyPressed(e);
    	}*/
    	// can also set damping, friction, etc.
    }
	
	public SceneNode getBall()
    {
    	return ball1Node;
    }
	
	
	
	
	private float[] toFloatArray(double[] arr)
    { 
    	if (arr == null) 
    		return null;
    	int n = arr.length;
    	
    	float[] ret = new float[n];
    	
    	for (int i = 0; i < n; i++)
    	{ 
    		ret[i] = (float)arr[i];
    	}
    return ret;
    }
    
    private double[] toDoubleArray(float[] arr)
    { 
    	if (arr == null) return null;
    	int n = arr.length;
    	double[] ret = new double[n];
    	for (int i = 0; i < n; i++)
    	{ 
    		ret[i] = (double)arr[i];
    	}
    	
    	return ret;
    }
    @Override
    protected void setupScene(Engine eng, SceneManager sm) throws IOException {
        this.sm=sm;
    	//setupNetworking();
    	im=new GenericInputManager();
    	//setupTerrain();
        
        hud=new HUD(sm, eng);
        
        
        //////////////////////////////////////////
        SceneNode rootNode = sm.getRootSceneNode();
    	// Ball 1
    	Entity ball1Entity = sm.createEntity("ball1", "sphere.obj");
    	ball1Node = rootNode.createChildSceneNode("Ball1Node");
    	ball1Node.attachObject(ball1Entity);
    	ball1Node.setLocalPosition(0, 2, 10);
    	// Ball 2
    	Entity ball2Entity = sm.createEntity("Ball2", "sphere.obj");
    	ball2Node = rootNode.createChildSceneNode("Ball2Node");
    	ball2Node.attachObject(ball2Entity);
    	ball2Node.setLocalPosition(-1,10, 10);
    	// Ground plane
    	Entity groundEntity = sm.createEntity(GROUND_E, "sphere.obj");
    	gndNode = rootNode.createChildSceneNode(GROUND_N);
    	gndNode.attachObject(groundEntity);
    	gndNode.setLocalPosition(0, 0, 2);
    	
    	
    	System.out.println("Press SPACE to start the physics engine!");
    	//running = true;//!!!!!!!!!!!!!!!!!!!!!!!!
    	
        //dungeon=new Dungeon(this.getEngine().getSceneManager(), getEngine());
        
       
    	/*
    	 * player
    	 */
        //if(playerIsDragon)
            //player = new FreeMovePlayer(sm, protClient, dungeon);
        //else
        	//player = new OrbitalPlayer(sm, protClient);
        //player.playWalkAnimation();
        
        
        //initPhysicsSystem();//!!!!!!!!!!!!!!!!!!!
    	//createRagePhysicsWorld();//!!!!!!!!!!!!!!!!
        
        
        /*
         * dungeon
         */
        //dungeon.addRoom();
        
        /*if(!isClientConnected && !playerIsDragon) {
        	ScriptEngineManager factory = new ScriptEngineManager();
        	ScriptEngine jsEngine = factory.getEngineByName("js");
        	
        	jsEngine.put("dungeon", dungeon);
        	//this.executeScript(jsEngine, "src/randomDungeon.js");
        	
        }*/
        
        ///////////////////////////////////////////////////
        /*
         * dungeon
         */
        dungeon=new Dungeon(this.getEngine().getSceneManager(), getEngine());
        dungeon.addRoom();
		
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
        // All textures must have the same dimensions, so any image�s
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
        
		
		RenderSystem rs = sm.getRenderSystem();
	     ZBufferState zstate = (ZBufferState) rs.createRenderState(RenderState.Type.ZBUFFER);
	     zstate.setTestEnabled(true);
	     gem.setRenderState(zstate);
	  
		
		


        
        /*
         * controllers
         */
    	
        rc = new RotationController(Vector3f.createUnitVectorY(), .05f);
        rc.addNode(gemNode); 
        sm.addController(rc);
        
        /*
         * light
         */
        sm.getAmbientLight().setIntensity(new Color(1f, 1f, 1f));

      
    }
    
	@Override
    protected void update(Engine engine) {
		if(player!=null) {
			
			//SceneNode node = sm.getRootSceneNode().createChildSceneNode("playerNode"+ghostID);
			
			
				
        float time = engine.getElapsedTimeMillis();
		float playerFloat[] = player.getNode().getLocalTransform().toFloatArray();
		//playerFloat[7] = player.getNode().getLocalPosition().y();
		double playerMat[] = toDoubleArray(playerFloat);
		Matrix4 mat3;
		mat3 = Matrix4f.createFrom(toFloatArray(player.getNode().getPhysicsObject().getTransform()));
		//player.getNode().setLocalPosition(playerFloat[3],mat3.value(1,3), playerFloat[11]);
		//double playerMat[] = toDoubleArray(player.getNode().getLocalTransform().toFloatArray());
		playerFloat = player.getNode().getLocalTransform().toFloatArray();
		//playerFloat[7] = player.getNode().getLocalPosition().y();
		playerFloat[7] = mat3.value(1,3);//set y coordinate to physics world
		playerMat = toDoubleArray(playerFloat);
		//System.out.println("player.getNode().getLocalTransform(): " + player.getNode().getLocalTransform());
	    player.getNode().getPhysicsObject().setTransform(playerMat);
	    
	    player.update(elapsTimeSec);
	    //SkeletalEntity manSE =
        		//(SkeletalEntity) engine.getSceneManager().getEntity("knightSkeleton");
        		//manSE.update();
		
	    
		if (running)
		{ 
			Matrix4 mat;
			physicsEng.update(time);
			for (SceneNode s : engine.getSceneManager().getSceneNodes())
			{ 
				if (s.getPhysicsObject() != null && s.getName() != player.getNode().getName())
				//if (s.getPhysicsObject() != null /*&& s.getName() != player.getNode().getName()*/)
				{ 
					mat = Matrix4f.createFrom(toFloatArray(s.getPhysicsObject().getTransform()));
					s.setLocalPosition(mat.value(0,3),mat.value(1,3), mat.value(2,3));
				} 
			} 
			
			
		}
		
		}///end
		
		im.update(elapsTime);
		
		if(running)//or change to: if connected to network
		processNetworking(elapsTime);
		
		if(player!=null) {
			//player.update(elapsTime);
			
			
		}
		
		
		if(avatarExists)
			gAvatar.getSkeletalEntity().update();
		if(avatarExists)
		{
			System.out.println("Last and cur position: " + gAvatar.getPos() 
			+ ", " + gAvatar.getLastPos());
	if (gAvatar.getPos() == gAvatar.getLastPos())
	{
		avatarWalking = false;
		
		
		gAvatar.getSkeletalEntity().stopAnimation();
		//manSE.playAnimation("walkAnimation", 0.5f, EndType.LOOP, 0);
	}
	else if (!avatarWalking)
	{   avatarWalking = true;
	   gAvatar.getSkeletalEntity().stopAnimation();
		gAvatar.getSkeletalEntity().playAnimation("walkAnimation", 0.5f, EndType.LOOP, 0);
	}
		}
		
		if(npcController!=null) {
			npcController.update();
		}
		
		if(avatarExists)
		gAvatar.setLastPos(gAvatar.getPos());
	}
	
	//gAvatar.getPos() == gAvatar.getLastPos()
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
    	ArrayList<Controller> controllers=im.getControllers();
    	
    	for(Controller controller:controllers) {
    		player.setupInputs(im, controller);
    	}
    			
    	
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
	
	public void addGhostAvatarToGameWorld(GhostAvatar avatar, String type, String obj) throws Exception{
		if(avatar!=null) {
			try {
				System.out.println("Drawing ghost");
				
				System.out.println("Type of ghost: " + type + " and " + obj);
				if(sm==null)
					sm=this.getEngine().getSceneManager();
				
				TextureManager tm=sm.getTextureManager();
				
				    avatarExists = true;
					ghostID = (UUID) avatar.getID();
					
				//else
				//{ System.out.println("made It to else");
					//texture=tm.getAssetByPath("white_knight.png");
				//}
					
				/*Entity ghostE=sm.createEntity("playerEntity"+avatar.getID(), "boxMan9.obj");
				ghostE.setPrimitive(Primitive.TRIANGLES);
				
				TextureManager tm=sm.getTextureManager();
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
				ghostN.setLocalRotation(avatar.getRot());*/
				
				
				//TextureManager tm=sm.getTextureManager();String bKnight = new String("bKnight"); 
				String ObjKnight = new String("knight"); 
				String objDragon = new String("dragon");
				
				if(obj.equals(ObjKnight))
				{
					String charType = "knight.png";
					String bKnight = new String("bKnight"); 
					String knight = new String("knight"); 
					String gKnight = new String("gKnight");
					Texture texture = texture=tm.getAssetByPath("knight.png");
					if(type.equals(knight))
						texture=tm.getAssetByPath("knight.png");
					if(type.equals(bKnight))
						texture=tm.getAssetByPath("black_knight.png");
					//if(type.equals("wKnight"))
						//texture=tm.getAssetByPath("white_knight.png");
					if(type.equals(gKnight))
						texture=tm.getAssetByPath("gold_knight.png");
				SkeletalEntity skeleton = sm.createSkeletalEntity("knightSkeletonAvatar", "knight.rkm", "knight.rks");
		        
		        //Texture texture=tm.getAssetByPath("black_knight.png");
		    	RenderSystem rs = sm.getRenderSystem();
		    	TextureState state=(TextureState) rs.createRenderState(RenderState.Type.TEXTURE);
		    	state.setTexture(texture);
		    	skeleton.setRenderState(state);

		        SceneNode node = sm.getRootSceneNode().createChildSceneNode("playerNode"+avatar.getID());
		        node.attachObject(skeleton);
		        node.scale(.5f, .5f, .5f);
		  
				avatar.setNode(node);
				avatar.setSkeletalEntity(skeleton);
				avatar.setPos(avatar.getPos().add(0,1,0));
				node.setLocalPosition(avatar.getPos());
				node.setLocalRotation(avatar.getRot());
		    	
		

				skeleton.loadAnimation("walkAnimation", "knight_walk.rka");
				//skeleton.stopAnimation();
				//skeleton.playAnimation("walkAnimation", 0.5f, EndType.LOOP, 0);
				gAvatar = avatar;
				
				//gAvatar = new GhostAvatar(
						//(UUID) avatar.getID(), avatar.getPos(), avatar.getRot());
				//gAvatar.setSkeletalEntity(skeleton);
				//gAvatar.setLastPos(avatar.getPos());
				System.out.println("Last and cur position: " + gAvatar.getPos() 
				+ ", " + gAvatar.getLastPos() );
				}
				else
				{
					String gDragon = new String("gDragon");
					String rDragon = new String("rDragon");
					Texture texture = texture=tm.getAssetByPath("knight.png");
					if(type.equals(gDragon))
						texture=tm.getAssetByPath("green_dragon.png");
					if(type.equals(rDragon))
						texture=tm.getAssetByPath("red_dragon.png");
					
					SkeletalEntity skeleton = sm.createSkeletalEntity("dragonSkeleton", "dragon.rkm", "dragon.rks");
					RenderSystem rs = sm.getRenderSystem();
			    	TextureState state=(TextureState) rs.createRenderState(RenderState.Type.TEXTURE);
			    	state.setTexture(texture);
			    	skeleton.setRenderState(state);

			        SceneNode node = sm.getRootSceneNode().createChildSceneNode("playerNode"+avatar.getID());
			        node.attachObject(skeleton);
			        node.scale(.5f, .5f, .5f);
			  
					avatar.setNode(node);
					avatar.setSkeletalEntity(skeleton);
					avatar.setPos(avatar.getPos().add(0,1,0));
					avatar.setLastPos(avatar.getPos());
					node.setLocalPosition(avatar.getPos());
					node.setLocalRotation(avatar.getRot());
			    	
			

					skeleton.loadAnimation("idleAnimation", "dragon_idle.rka");
					skeleton.playAnimation("idleAnimation", 0.5f, EndType.LOOP, 0);
					
				}
				
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	

	public void addGhostNPCToGameWorld(NPC npc) {
		if(npc!=null) {
			try {
				System.out.println("Drawing NPC");
				if(sm==null)
					sm=this.getEngine().getSceneManager();

		        SkeletalEntity skeleton = sm.createSkeletalEntity("npcSkeleton"+npcController.getNumNPCs(), "knight.rkm", "knight.rks");

		        String skinName;
		        switch(npc.getSkin()) {
				case KNIGHT:
					skinName="knight.png";
					break;
				case BLACK_KNIGHT:
					skinName="black_knight.png";
					break;
				case GOLD_KNIGHT:
					skinName="gold_knight.png";
					break;
				case WHITE_KNIGHT:
					skinName="white_knight.png";
					break;
				default:
					skinName="default.png";
					break;
		        
		        }
				TextureManager tm=sm.getTextureManager();
		        Texture texture=tm.getAssetByPath(skinName);
		    	RenderSystem rs = sm.getRenderSystem();
		    	TextureState state=(TextureState) rs.createRenderState(RenderState.Type.TEXTURE);
		    	state.setTexture(texture);
		    	skeleton.setRenderState(state);
				
				SceneNode ghostN = sm.getRootSceneNode().createChildSceneNode("npcNode"+npcController.getNumNPCs());
				ghostN.attachObject(skeleton);
				npc.setPos(dungeon.getLastRoom().getRoomNode().getWorldPosition());
				ghostN.setLocalPosition(npc.getPos());
		        ghostN.scale(.5f, .5f, .5f);
				
				
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
		
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
	

	
	@Override
	public void mousePressed(MouseEvent e) {
		int x=e.getX();
		int y=e.getY();
		//
		if(gameMode==GAME_MODE.SPLASH) {
			if(x>245&&x<738) {
				if(y>366&&y<431) {
					System.out.println("Clicked sp");
					onlineType=ONLINE_TYPE.OFFLINE;
					setupNetworking();
					setGameMode(GAME_MODE.CHAR_SELECT);
				}
				else if(y>513&&y<578) {
					System.out.println("Clicked online");
					onlineType=ONLINE_TYPE.ONLINE;
					setupNetworking();
					setGameMode(GAME_MODE.CHAR_SELECT);
				}
			}
		}
		else if (gameMode==GAME_MODE.CHAR_SELECT){
			if(y>515&&y<580) {
				if(x<106&&x>37) {
					hud.incrementDragon();
				}
				else if(x<344&&x>136) {
					player=new FreeMovePlayer(getEngine().getSceneManager(), protClient, dungeon, hud.getDragonSkin());
					playerType=PLAYER_TYPE.DRAGON;
					setGameMode(GAME_MODE.BUILD);
				}
				else if(x>372&&x<439) {
					hud.decrementDragon();
				}
				else if(x<612&&x>541) {
					hud.incrementKnight();
				}
				else if(x>642&&x<847) {
					player=new OrbitalPlayer(this.getEngine().getSceneManager(), protClient, hud.getKnightSkin());
					playerType=PLAYER_TYPE.KNIGHT;
					setGameMode(GAME_MODE.BUILD);
				}
				else if(x>875&&x<944) {
					hud.decrementKnight();
				}
			}
		}
		else if(gameMode==GAME_MODE.BUILD) {
			if(playerType==PLAYER_TYPE.DRAGON) {
					if(x>0&&x<200) {
						y=y/80;
						switch(y) {
						case 0:
							dungeon.addRoom();
							break;
						case 1: 
							dungeon.addTrap(getCurrentRoom(), TRAP_TYPE.Swinging);
							break;
						case 2:
							dungeon.addTrap(getCurrentRoom(), TRAP_TYPE.Spike);
							break;
						case 3:
							dungeon.addTrap(getCurrentRoom(), TRAP_TYPE.Pit);
							break;
						case 4:
							dungeon.getRoom(getCurrentRoom()).toggleLights();
							break;
						case 5:
							dungeon.getRoom(getCurrentRoom()).clear();
							break;
						case 6:
							dungeon.removeLastRoom();
							break;
						case 7:
							try {
								dungeon.finish();
								this.setGameMode(GAME_MODE.SEIGE);
							} catch (IOException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
							break;
						}
					}
				
				
			}
			
		}
		else if(gameMode==GAME_MODE.SEIGE) {
			
		}
	}
	
	private int getCurrentRoom(){
		return dungeon.getCurrentRoom(player.getNode().getLocalPosition());
	}
	
	private void setGameMode(GAME_MODE gm) {
		System.out.println("setting game mode to: "+gm);
		this.gameMode=gm;
		switch(gm) {
		case SPLASH:
			try {
				hud.setToSplash();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			break;
		case CHAR_SELECT:
			try {
				hud.setToCharSelect();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case BUILD:
			setupInputs();
	        sm.getAmbientLight().setIntensity(new Color(.5f, .5f, .5f));
	        if(playerType==PLAYER_TYPE.KNIGHT) {
	        	hud.hide();
	        	if(onlineType==ONLINE_TYPE.ONLINE) {
	        		setupTerrain();
	        		running = true;//!!!!!!!!!!!!!!!!!!!!!!!!
	                initPhysicsSystem();//!!!!!!!!!!!!!!!!!!!
	            	createRagePhysicsWorld();//!!!!!!!!!!!!!!!!
	            	//setupNetworking();//!!!!!!!!!!!!!!!!!!!!
	            	
	        	}
	        	else {
	        		System.out.println("initializing dungeon");
	            	ScriptEngineManager factory = new ScriptEngineManager();
	            	ScriptEngine jsEngine = factory.getEngineByName("js");
	            	
	            	jsEngine.put("dungeon", dungeon);
	            	this.executeScript(jsEngine, "src/randomDungeon.js");
	        		setGameMode(GAME_MODE.SEIGE);
	        	}
	        }
	        else {
	        	hud.setToButtons();
	        }
	        
			break;
		case SEIGE:
			//teleport knight to dungeon
			if(playerType==PLAYER_TYPE.KNIGHT) {
				player.setDungeon(dungeon);
				player.teleport(dungeon.getLastRoom().getRoomNode().getWorldPosition());
			}
			else {
				if(onlineType==ONLINE_TYPE.OFFLINE) {
					setupNPC();
				}
			}
			break;
		default:
			break;
		}
	}
	public SceneNode getPlayerNode()///////////////
	{
		return player.getNode();
	}
	
	public SKIN getPlayerType()///////////////
	{
		return hud.getKnightSkin();
	}
	
	public SKIN getPlayerDragonType()///////////////
	{
		return hud.getDragonSkin();
	}
	public PLAYER_TYPE getPlayerObjType(){
		
		return playerType;
	}
	private void setupNPC() {
		npcController=new NPCController();
		this.addGhostNPCToGameWorld(npcController.getNPC());
	}
	
	
	
	
}


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
import ray.rage.scene.SkeletalEntity.EndType;
import ray.rage.scene.controllers.*;
import ray.rage.util.BufferUtil;
import ray.rage.util.Configuration;
import ray.rml.*;
import ray.rage.rendersystem.gl4.GL4RenderSystem;
import ray.rage.rendersystem.shader.GpuShaderProgram;
import ray.rage.rendersystem.states.*;
import ray.networking.IGameConnection.ProtocolType;
import ray.physics.PhysicsEngine;/////////
import ray.physics.PhysicsObject;///////////
import ray.physics.PhysicsEngineFactory;/////////

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
	
	private SceneNode ball1Node, ball2Node, gndNode;///////////
	private SceneNode cameraPositionNode;/////////////
	private final static String GROUND_E = "Ground";//////////////
	private final static String GROUND_N = "GroundNode";/////////////
	private PhysicsEngine physicsEng;//////////
	private PhysicsObject ball1PhysObj, ball2PhysObj, gndPlaneP, knightPhysObj;//////////
	private boolean running = false;////////////
	float dir = 0.10f;
	int dIter = 0;
	
	
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
	
    
    private void initPhysicsSystem()
    { 
    	String engine = "ray.physics.JBullet.JBulletPhysicsEngine";
    	float[] gravity = {0, -3f, 0};
    	physicsEng = PhysicsEngineFactory.createPhysicsEngine(engine);
    	physicsEng.initSystem();
    	physicsEng.setGravity(gravity);
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
    
    private void setupNPC() throws Exception
    {
    	
    	try {
		
    	//Entity ghostE=sm.createEntity("dragonNPC", "boxMan9.obj");
    	Entity dragonE=sm.createEntity("dragonNPC", "dragon1.obj");
        //Texture texture=tm.getAssetByPath("dragon1.png");
    	dragonE.setPrimitive(Primitive.TRIANGLES);
		
		TextureManager tm=sm.getTextureManager();
        Texture texture=tm.getAssetByPath("dragon1.png");
        //Texture texture=tm.getAssetByPath("boxMan4.png");
    	RenderSystem rs = sm.getRenderSystem();
    	//TextureState state=(TextureState) rs.createRenderState(RenderState.Type.TEXTURE);
    	//state.setTexture(texture);
    	//dragonE.setRenderState(state);
		
		SceneNode dragonN = sm.getRootSceneNode().createChildSceneNode("dragonNPCNode");
		dragonN.attachObject(dragonE);
		dragonN.setLocalPosition(0,10,0);
		//ghostN.setLocalRotation("90");
		
    	}
		catch(Exception e) {
			e.printStackTrace();
		}
    }
    
    private void doFlying()
    { SkeletalEntity manSE =
    (SkeletalEntity) this.getEngine().getSceneManager().getEntity("dragonSkeleton");
    manSE.stopAnimation();
    manSE.playAnimation("waveAnimation", 0.5f, EndType.LOOP, 0);
    }
    
    @Override
    protected void setupScene(Engine eng, SceneManager sm) throws IOException {
    	setupNetworking();
    	setupTerrain();
    	
			/*try {
				setupNPC();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
    	
    	//SkeletalEntity dragonSE =
    			//sm.createSkeletalEntity("dragonSkeleton", "dragon.rkm", "dragon.rks");
    	Entity dragonE=sm.createEntity("dragonNPC", "dragon1.obj");
       
    	dragonE.setPrimitive(Primitive.TRIANGLES);
		
		TextureManager tm=sm.getTextureManager();
        Texture texture=tm.getAssetByPath("dragon1.png");
        
    	RenderSystem rs = sm.getRenderSystem();
    	TextureState state=(TextureState) rs.createRenderState(RenderState.Type.TEXTURE);
    	state.setTexture(texture);
    	dragonE.setRenderState(state);
    	//dragonSE.setRenderState(state);
		
		SceneNode dragonN = sm.getRootSceneNode().createChildSceneNode("dragonNPCNode");
		dragonN.attachObject(dragonE);
		//dragonN.attachObject(dragonSE);
		dragonN.setLocalPosition(-20,10,20);
		//dragonSE.loadAnimation("walkAnimation", "dragon_idle.rka");
		
		//doFlying();
		
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
    	running = true;
    	
        dungeon=new Dungeon(this.getEngine().getSceneManager(), getEngine());
        
       
    	/*
    	 * player
    	 */
        if(playerIsDragon)
            player = new FreeMovePlayer(sm, protClient, dungeon);
        else
        	player = new OrbitalPlayer(sm, protClient);
        //player.playWalkAnimation();
        initPhysicsSystem();
    	createRagePhysicsWorld();
        
        
        /*
         * dungeon
         */
        dungeon.addRoom();
        
        if(!isClientConnected&&!playerIsDragon) {
        	ScriptEngineManager factory = new ScriptEngineManager();
        	ScriptEngine jsEngine = factory.getEngineByName("js");
        	
        	jsEngine.put("dungeon", dungeon);
        	//this.executeScript(jsEngine, "src/randomDungeon.js");
        	
        }
		
        /*
         * skybox
         */
        
        Configuration conf = eng.getConfiguration();
        //TextureManager tm = getEngine().getTextureManager();
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
		
		//player.update(elapsTimeSec);
		SceneNode dragon = engine.getSceneManager().getSceneNode("dragonNPCNode");
		
		dIter++;
		//if(dIter == 200)
		//{
			//dir = dir*-1;
			//dIter = 0;
		//}
		if(dIter <= 200)
		dragon.moveBackward(dir);
		
		if(dIter > 200 && dIter <= 400 )
			dragon.moveLeft(dir);
		
		if(dIter > 400 && dIter <= 600 )
			dragon.moveForward(dir);
		
		if(dIter > 600 && dIter <= 800 )
			dragon.moveRight(dir);
		
		if(dIter == 800 )
			dIter = 0;
		//SkeletalEntity dragonSE =
				//(SkeletalEntity) engine.getSceneManager().getEntity("dragonSkeleton");
		//dragonSE.update();
		
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
		System.out.println("player.getNode().getLocalTransform(): " + player.getNode().getLocalTransform());
	    player.getNode().getPhysicsObject().setTransform(playerMat);
	    
	    player.update(elapsTimeSec);
	    SkeletalEntity manSE =
        		(SkeletalEntity) engine.getSceneManager().getEntity("knightSkeleton");
        		manSE.update();

	    
		if (running)
		{ 
			Matrix4 mat;
			physicsEng.update(time);
			for (SceneNode s : engine.getSceneManager().getSceneNodes())
			{ 
				//if (s.getPhysicsObject() != null && s.getName() != player.getNode().getName())
				//if (s.getPhysicsObject() != null /*&& s.getName() != player.getNode().getName()*/)
				{ 
					mat = Matrix4f.createFrom(toFloatArray(s.getPhysicsObject().getTransform()));
					s.setLocalPosition(mat.value(0,3),mat.value(1,3), mat.value(2,3));
				} 
			} 
		}

		dispStr="Time = " + elapsTimeStr + " Score: "+player.getScore();
		rs.setHUD(dispStr, 15, 15);
		if(player.isBoostActive()) dispStr+=" Boost Active!";
		im.update(elapsTime);
		processNetworking(elapsTime);
		
		checkForCollisions();
		//player.update(elapsTimeSec);
		System.out.println("ball transform: " + ball2Node.getLocalTransform() );
		System.out.println("ball physycs object transform: " + ball2Node.getPhysicsObject().getTransform()[0] + ','  
				+ ball2Node.getPhysicsObject().getTransform()[1] + ','
				+ ball2Node.getPhysicsObject().getTransform()[2] + ','
				+ ball2Node.getPhysicsObject().getTransform()[3] + ','
				+ ball2Node.getPhysicsObject().getTransform()[4] + ','
				+ ball2Node.getPhysicsObject().getTransform()[5] + ','
				+ ball2Node.getPhysicsObject().getTransform()[6] + ','
				+ ball2Node.getPhysicsObject().getTransform()[7] + ','
				+ ball2Node.getPhysicsObject().getTransform()[8] + ','
				+ ball2Node.getPhysicsObject().getTransform()[9] + ','
				+ ball2Node.getPhysicsObject().getTransform()[10] + ','
				+ ball2Node.getPhysicsObject().getTransform()[11] + ','
				+ ball2Node.getPhysicsObject().getTransform()[12] + ','
				+ ball2Node.getPhysicsObject().getTransform()[13] + ','
				+ ball2Node.getPhysicsObject().getTransform()[14] + ','
				+ ball2Node.getPhysicsObject().getTransform()[15] + ',');
		System.out.println("ball linear velocity: " + ball2Node.getPhysicsObject().getLinearVelocity());
		if(distanceTo(player.getNode().getLocalPosition(),ball2Node.getLocalPosition()) <= 4)
		{
			ball2Node.setLocalPosition(0,10,0);
			float velocityArray[] = { 0, 5, 0};
			ball2Node.getPhysicsObject().setLinearVelocity(velocityArray);
			float floTemp[] =  ball2Node.getLocalTransform().toFloatArray();
			double dubTemp[] = {(double)floTemp[0], (double)floTemp[1], (double)floTemp[2], (double)floTemp[3], (double)floTemp[4],
					(double)floTemp[5],(double)floTemp[6],(double)floTemp[7], (double)floTemp[8], (double)floTemp[9], (double)floTemp[10], (double)floTemp[11]
							, (double)floTemp[12], (double)floTemp[13], (double)floTemp[14], (double)floTemp[15] };
			
			double dubTemp2[] = toDoubleArray(floTemp);


			 Matrix4 mat2;
			ball2Node.getPhysicsObject().setTransform(dubTemp2);
			
			//mat2 = Matrix4f.createFrom(toDoubleArray( ball2Node.getLocalTransform().toFloatArray()))
			//ball2Node.getPhysicsObject().setTransform(dubTemp2);
			//ball2Node.setPhysicsObject(ball2PhysObj);
			//ball2Node.getPhysicsObject().getTransform().
			
			
			
			
		}
		
		//player.playWalkAnimation();
		
		
		
		/*ball2Node.getPhysicsObject().setTransform(ball2Node.getLocalTransform());
		//ball2PhysObj.set
		physicsEng.
		temptf = toDoubleArray(ball1Node.getLocalTransform().toFloatArray());
    	ball1PhysObj = physicsEng.addSphereObject(physicsEng.nextUID(),
    	mass, temptf, 2.0f);
    	ball1PhysObj.setBounciness(1.0f);
    	ball1Node.setPhysicsObject(ball1PhysObj);*/
		
		/* check if player jumped*/
		
	}

	
	public float distanceTo(Vector3 a, Vector3 b) {
		float ax = a.x();
		float ay = a.y();
		float az = a.z();
		float bx = b.x();
		float by = b.y();
		float bz = b.z();
		
		return (float)Math.sqrt((ax-bx)*(ax-bx) + (ay-by)*(ay-by) + (az-bz)*(az-bz));
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


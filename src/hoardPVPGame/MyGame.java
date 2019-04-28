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
    
    private HUD hud;

    
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
    	MyGame game = new MyGame("", 10);
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
    @Override
    protected void setupScene(Engine eng, SceneManager sm) throws IOException {
        this.sm=sm;
    	setupNetworking();
    	im=new GenericInputManager();
    	//setupTerrain();
        dungeon=new Dungeon(this.getEngine().getSceneManager(), getEngine());
        
        hud=new HUD(sm, eng);
        
    	
    	/*
    	 * player
    	 
        if(playerIsDragon)
            player = new FreeMovePlayer(sm, protClient, dungeon);
        else
        	player = new OrbitalPlayer(sm, protClient);
        
        
        /*
         * dungeon
         */
        dungeon.addRoom();
        
        if(onlineType==ONLINE_TYPE.OFFLINE&&playerType==PLAYER_TYPE.KNIGHT) {
        	
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
        
        
    	//setupInputs();

      
    }
    
	@Override
    protected void update(Engine engine) {
		im.update(elapsTime);
		processNetworking(elapsTime);
		
		if(player!=null)
			player.update(elapsTimeSec);
		
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
	
	public void addGhostAvatarToGameWorld(GhostAvatar avatar) throws Exception{
		if(avatar!=null) {
			try {
				System.out.println("Drawing ghost");
				if(sm==null)
					sm=this.getEngine().getSceneManager();
				Entity ghostE=sm.createEntity("playerEntity"+avatar.getID(), "dolphinHighPoly.obj");
				ghostE.setPrimitive(Primitive.TRIANGLES);
				SceneNode ghostN = sm.getRootSceneNode().createChildSceneNode("playerNode"+avatar.getID());
				ghostN.attachObject(ghostE);
				avatar.setNode(ghostN);
				avatar.setEntity(ghostE);
				ghostN.setLocalPosition(avatar.getPos());
				
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
		//System.out.println(x+", "+y+" ");
		if(gameMode==GAME_MODE.SPLASH) {
			if(x>245&&x<738) {
				if(y>366&&y<431) {
					System.out.println("Clicked sp");
					onlineType=ONLINE_TYPE.OFFLINE;
					setGameMode(GAME_MODE.CHAR_SELECT);
				}
				else if(y>513&&y<578) {
					System.out.println("Clicked online");
					onlineType=ONLINE_TYPE.ONLINE;
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
					setGameMode(GAME_MODE.BUILD);
				}
				else if(x>875&&x<944) {
					hud.decrementKnight();
				}
			}
		}
		else if(gameMode==GAME_MODE.BUILD) {
			if(playerType==PLAYER_TYPE.DRAGON) {
				
			}
			
		}
		else if(gameMode==GAME_MODE.SEIGE) {
			
		}
	}
	
	private void setGameMode(GAME_MODE gm) {
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
			hud.hide();
			setupInputs();
	        sm.getAmbientLight().setIntensity(new Color(.5f, .5f, .5f));
	        if(playerType==PLAYER_TYPE.KNIGHT) {
	        	if(onlineType==ONLINE_TYPE.ONLINE) {
	        		setupTerrain();
	        	}
	        	else {
	            	ScriptEngineManager factory = new ScriptEngineManager();
	            	ScriptEngine jsEngine = factory.getEngineByName("js");
	            	
	            	jsEngine.put("dungeon", dungeon);
	            	this.executeScript(jsEngine, "src/randomDungeon.js");
	        		setGameMode(GAME_MODE.SEIGE);
	        	}
	        }
	        
			break;
		case SEIGE:
			//teleport knight to dungeon
			if(playerType==PLAYER_TYPE.KNIGHT) {
				player.teleport(dungeon.getLastRoom().getWorldPosition());
			}
			break;
		default:
			break;
		}
	}
	
	
	
	
}


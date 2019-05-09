package hoardPVPGame;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Iterator;

import ray.rage.Engine;
import ray.rage.asset.texture.Texture;
import ray.rage.rendersystem.RenderSystem;
import ray.rage.rendersystem.Renderable.DataSource;
import ray.rage.rendersystem.Renderable.Primitive;
import ray.rage.rendersystem.shader.GpuShaderProgram;
import ray.rage.rendersystem.states.FrontFaceState;
import ray.rage.rendersystem.states.RenderState;
import ray.rage.rendersystem.states.TextureState;
import ray.rage.rendersystem.states.ZBufferState;
import ray.rage.scene.Entity;
import ray.rage.scene.ManualObject;
import ray.rage.scene.ManualObjectSection;
import ray.rage.scene.SceneManager;
import ray.rage.scene.SceneNode;
import ray.rage.util.BufferUtil;
import ray.rml.Degreef;
import ray.rml.Vector3;
import ray.rml.Vector3f;

public class HUD {
	
	private ManualObject hud;
	private ManualObject knightThumb;
	private ManualObject dragonThumb;
	private ManualObject buttons;
	private SceneManager sm;
	private Engine eng;
	private int dragonIndex;
	private int knightIndex;
	

	public HUD(SceneManager sm, Engine eng) {
		this.sm = sm;
		this.eng = eng;
		
		dragonIndex=0;
		knightIndex=0;
		
		try {
			setup();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void setup() throws IOException {	
		/*
		 * background
		 */
		hud=sm.createManualObject("splash");
        ManualObjectSection psec=hud.createManualSection("splashsec");
        hud.setGpuShaderProgram(sm.getRenderSystem().getGpuShaderProgram(GpuShaderProgram.Type.RENDERING));
        
        float[] vertices= new float[] {
        	-.5f,-0.35f, 0,
        	-.5f,0.35f, 0,
        	.5f,0.35f, 0,
            .5f,-0.35f, 0
        	
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
		
		setTexture("splash.png", hud);
		
		
		hud.setPrimitive(Primitive.TRIANGLES);
		
		RenderSystem rs = sm.getRenderSystem();
	    ZBufferState zstate = (ZBufferState) rs.createRenderState(RenderState.Type.ZBUFFER);
	    zstate.setTestEnabled(true);
	    hud.setRenderState(zstate);
		
        SceneNode core = sm.getRootSceneNode().createChildSceneNode("SplashNode");
        core.moveForward(.5f);
        //node.scale(GameUtil.getRoomSize(), GameUtil.getRoomSize(), GameUtil.getRoomSize());
        core.attachObject(hud);
        
        
        
        
        /*
         * dragon thumb
         */

		dragonThumb=sm.createManualObject("dragonThumb");
        psec=dragonThumb.createManualSection("dthumbsec");
        dragonThumb.setGpuShaderProgram(sm.getRenderSystem().getGpuShaderProgram(GpuShaderProgram.Type.RENDERING));
        
        vertices= new float[] {
        	-.096f,-0.119f, 0,
        	-.096f,0.119f, 0,
        	.096f,0.119f, 0,
        	.096f,-0.119f, 0
        	
        };
        
        vertBuf = BufferUtil.directFloatBuffer(vertices);
        texBuf = BufferUtil.directFloatBuffer(texcoords);
		indexBuf = BufferUtil.directIntBuffer(indices);
		psec.setVertexBuffer(vertBuf);
		psec.setTextureCoordsBuffer(texBuf);
		psec.setIndexBuffer(indexBuf);

		
		setTexture("green_dragon_thumb.png", dragonThumb);
		
		
		dragonThumb.setPrimitive(Primitive.TRIANGLES);
		
		rs = sm.getRenderSystem();
	    zstate = (ZBufferState) rs.createRenderState(RenderState.Type.ZBUFFER);
	    zstate.setTestEnabled(true);
	    dragonThumb.setRenderState(zstate);
		
        SceneNode dragonNode = core.createChildSceneNode("DragonThumbNode");
        dragonNode.moveBackward(0.1f);
        dragonNode.moveRight(0.19f);
        //node.scale(GameUtil.getRoomSize(), GameUtil.getRoomSize(), GameUtil.getRoomSize());
        dragonNode.attachObject(dragonThumb);
        dragonThumb.setVisible(false);
        
        
        
        
        /*
         * knight thumb
         */

		knightThumb=sm.createManualObject("knightThumb");
        psec=knightThumb.createManualSection("knightsec");
        knightThumb.setGpuShaderProgram(sm.getRenderSystem().getGpuShaderProgram(GpuShaderProgram.Type.RENDERING));
        
        vertBuf = BufferUtil.directFloatBuffer(vertices);
        texBuf = BufferUtil.directFloatBuffer(texcoords);
		indexBuf = BufferUtil.directIntBuffer(indices);
		psec.setVertexBuffer(vertBuf);
		psec.setTextureCoordsBuffer(texBuf);
		psec.setIndexBuffer(indexBuf);
		
		setTexture("knight_thumb.png", knightThumb);
		
		
		knightThumb.setPrimitive(Primitive.TRIANGLES);
		
		rs = sm.getRenderSystem();
	    zstate = (ZBufferState) rs.createRenderState(RenderState.Type.ZBUFFER);
	    zstate.setTestEnabled(true);
	    knightThumb.setRenderState(zstate);
		
        SceneNode knightNode = core.createChildSceneNode("KnightThumbNode");
        knightNode.moveBackward(0.1f);
        knightNode.moveLeft(0.19f);
        //node.scale(GameUtil.getRoomSize(), GameUtil.getRoomSize(), GameUtil.getRoomSize());
        knightNode.attachObject(knightThumb);
        knightThumb.setVisible(false);
        
        /*
         * left menu buttons
         */


		buttons=sm.createManualObject("buttons");
        psec=buttons.createManualSection("buttonsec");
        buttons.setGpuShaderProgram(sm.getRenderSystem().getGpuShaderProgram(GpuShaderProgram.Type.RENDERING));
        
        vertices= new float[] {
        	-1f,-3f, 0,
        	-1f,3f, 0,
        	1f,3f, 0,
        	1f,-3f, 0
        	
        };
        
        vertBuf = BufferUtil.directFloatBuffer(vertices);
        texBuf = BufferUtil.directFloatBuffer(texcoords);
		indexBuf = BufferUtil.directIntBuffer(indices);
		psec.setVertexBuffer(vertBuf);
		psec.setTextureCoordsBuffer(texBuf);
		psec.setIndexBuffer(indexBuf);
		
		setTexture("buttons.png", buttons);

		buttons.setPrimitive(Primitive.TRIANGLES);
		
		rs = sm.getRenderSystem();
	    zstate = (ZBufferState) rs.createRenderState(RenderState.Type.ZBUFFER);
	    zstate.setTestEnabled(true);
	    buttons.setRenderState(zstate);
		
        SceneNode buttonNode = core.createChildSceneNode("ButtonNode");
        buttonNode.moveBackward(1.18f);
        buttonNode.scale(Vector3f.createFrom(.2f, .2f, .2f));
        buttonNode.moveRight(1f);
        buttonNode.rotate(Degreef.createFrom(90f), Vector3f.createFrom(0f, 1f, 0f));
        buttonNode.attachObject(buttons);
        buttons.setVisible(false);
        
        
		
	}
	
	private void setTexture(String filename, ManualObject mo) throws IOException {
		System.out.println("Setting texture to "+filename);
		Texture tex =
		eng.getTextureManager().getAssetByPath(filename);
		TextureState texState = (TextureState)sm.getRenderSystem().
		createRenderState(RenderState.Type.TEXTURE);
		texState.setTexture(tex);
		
		FrontFaceState faceState = (FrontFaceState) sm.getRenderSystem().
		createRenderState(RenderState.Type.FRONT_FACE);
		mo.setDataSource(DataSource.INDEX_BUFFER);
		mo.setRenderState(texState);
		mo.setRenderState(faceState);
	}
	
	public void setToSplash() throws IOException {
		setTexture("splash.png", hud);
		knightThumb.setVisible(false);
		dragonThumb.setVisible(false);
		buttons.setVisible(false);
	}
	
	public void setToCharSelect() throws IOException {
		setTexture("charselect.png", hud);
		knightThumb.setVisible(true);
		dragonThumb.setVisible(true);
		buttons.setVisible(false);
	}
	
	public void setToButtons() {
		Iterator<SceneNode> it = sm.getSceneNodes().iterator();
		while(it.hasNext()){
			System.out.println(it.next());
		}
		buttons.setVisible(true);
		SceneNode cameraNode=sm.getSceneNode("playerNode");
		SceneNode buttonNode=sm.getSceneNode("ButtonNode");
		System.out.println(cameraNode.getWorldPosition());
		System.out.println(buttonNode.getWorldPosition());
		cameraNode.attachChild(buttonNode);
		//buttonNode.moveBackward(0.5f);
		//buttonNode.moveLeft(2.2f);
		//buttonNode.moveDown(30f);
		buttonNode.setLocalPosition(.48f,0f, .7f);
		buttonNode.scale(.7f, .7f, .7f);
		buttonNode.rotate(Degreef.createFrom(90f), Vector3f.createFrom(0,-1f,0));
		System.out.println(cameraNode.getWorldPosition());
		System.out.println(buttonNode.getWorldPosition());
		hud.setVisible(false);
		dragonThumb.setVisible(false);
		knightThumb.setVisible(false);
	}
	
	public void hide() {
		hud.setVisible(false);
		dragonThumb.setVisible(false);
		knightThumb.setVisible(false);
		buttons.setVisible(false);
		
	}

	public void update() {
		try {
			switch(dragonIndex) {
				case 0:
					setTexture("green_dragon_thumb.png", dragonThumb);
					break;
				case 1:
					setTexture("red_dragon_thumb.png", dragonThumb);
					break;
				case 2:
					setTexture("black_dragon_thumb.png", dragonThumb);
					break;
				case 3:
					setTexture("purple_dragon_thumb.png", dragonThumb);
					break;
			}
			switch(knightIndex) {
			case 0:
				setTexture("knight_thumb.png", knightThumb);
				break;
			case 1:
				setTexture("black_knight_thumb.png", knightThumb);
				break;
			case 2:
				setTexture("white_knight_thumb.png", knightThumb);
				break;
			case 3:
				setTexture("gold_knight_thumb.png", knightThumb);
				break;
		}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public void incrementDragon() {
		dragonIndex++;
		dragonIndex=(dragonIndex+GameUtil.getSkinNumber())%GameUtil.getSkinNumber();
		update();
		
	}

	public GameUtil.SKIN getDragonSkin() {
		switch(dragonIndex) {
		case 0:
			return GameUtil.SKIN.GREEN_DRAGON;
		case 1:
			return GameUtil.SKIN.RED_DRAGON;
		case 2:
			return GameUtil.SKIN.BLACK_DRAGON;
		case 3:
			return GameUtil.SKIN.PURPLE_DRAGON;
		default:
			return null;
		}
	}

	public void decrementDragon() {
		dragonIndex--;
		dragonIndex=(dragonIndex+GameUtil.getSkinNumber())%GameUtil.getSkinNumber();
		update();
		
	}

	public void incrementKnight() {
		knightIndex++;
		knightIndex=(knightIndex+GameUtil.getSkinNumber())%GameUtil.getSkinNumber();
		update();
	}

	public GameUtil.SKIN getKnightSkin() {
		switch(knightIndex) {
		case 0:
			return GameUtil.SKIN.KNIGHT;
		case 1:
			return GameUtil.SKIN.BLACK_KNIGHT;
		case 2:
			return GameUtil.SKIN.WHITE_KNIGHT;
		case 3:
			return GameUtil.SKIN.GOLD_KNIGHT;
		default:
			return null;
		}
	}

	public void decrementKnight() {
		knightIndex--;
		knightIndex=(knightIndex+GameUtil.getSkinNumber())%GameUtil.getSkinNumber();
		update();
		
	}
	
	

}

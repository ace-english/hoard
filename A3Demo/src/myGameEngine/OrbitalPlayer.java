package myGameEngine;

import java.io.IOException;
import java.util.concurrent.Semaphore;

import net.java.games.input.Controller;
import ray.input.InputManager;
import ray.input.action.Action;
import ray.rage.asset.texture.Texture;
import ray.rage.asset.texture.TextureManager;
import ray.rage.rendersystem.*;
import ray.rage.rendersystem.Renderable.*;
import ray.rage.rendersystem.gl4.GL4RenderSystem;
import ray.rage.rendersystem.shader.GpuShaderProgram;
import ray.rage.rendersystem.states.*;
import ray.rage.scene.Camera;
import ray.rage.scene.Entity;
import ray.rage.scene.SceneManager;
import ray.rage.scene.SceneNode;
import ray.rage.scene.SkeletalEntity;
import ray.rage.scene.Tessellation;
import ray.rage.scene.SkeletalEntity.EndType;
import ray.rml.Degreef;
import ray.rml.Vector3;
import ray.rml.Vector3f;

//import static ray.rage.scene.SkeletalEntity.EndType.*;

public class OrbitalPlayer extends Player {
	
	private Camera3PController cameraController;
	SceneNode riderNode, cameraNode;
	private ProtocolClient pc;
	private Semaphore walkingMutex;
	
	

	public OrbitalPlayer(SceneManager sm, ProtocolClient pc) {
		super(sm, pc);
		sm2=sm;
		this.pc = getProtocolClient();
		walkingMutex=new Semaphore(1);
		
	}

	@Override
	protected void setupNodes(SceneManager sm) throws IOException {
        //Entity entity = sm.createEntity("player", "knight.obj");
        //entity.setPrimitive(Primitive.TRIANGLES);
		//TextureManager tm=sm.getTextureManager();
		//Texture texture=tm.getAssetByPath("knight.png");
        
        
        
//
        //skeleton = sm.createSkeletalEntity("knightSkeleton", "knight.rkm", "knight.rks");
        
        //Texture texture=tm.getAssetByPath(skinName);
    	//RenderSystem rs = sm.getRenderSystem();
    	//TextureState state=(TextureState) rs.createRenderState(RenderState.Type.TEXTURE);
    	//state.setTexture(texture);
    	//skeleton.setRenderState(state);

        //SceneNode node = sm.getRootSceneNode().createChildSceneNode("playerNode");
        //node.attachObject(skeleton);
        //node.scale(.5f, .5f, .5f);
		//setNode(node);
    	
        //riderNode = node.createChildSceneNode("RiderNode");
        //riderNode.moveUp(6f);

		//skeleton.loadAnimation("walkAnimation", "knight_walk.rka");
        
        //
		
		TextureManager tm=sm.getTextureManager();
		skeleton = sm.createSkeletalEntity("knightSkeleton", "knight.rkm", "knight.rks");
        //TextureManager tm=sm.getTextureManager();
        Texture texture=tm.getAssetByPath("knight.png");
    	RenderSystem rs = sm.getRenderSystem();
    	TextureState state=(TextureState) rs.createRenderState(RenderState.Type.TEXTURE);
    	state.setTexture(texture);
    	//entity.setRenderState(state);
    	skeleton.setRenderState(state);

        //SceneNode node = sm.getRootSceneNode().createChildSceneNode(entity.getName() + "Node");
    	SceneNode node = sm.getRootSceneNode().createChildSceneNode( "playerNode");
        //node.attachObject(entity);
    	node.attachObject(skeleton);
        node.scale(.5f, .5f, .5f);
		setNode(node);
    	
        riderNode = node.createChildSceneNode("RiderNode");
        riderNode.moveUp(6f);
        
        skeleton.loadAnimation("walkAnimation", "knight_walk.rka");
        
        
        
        cameraNode = sm.getSceneNode("MainCameraNode");
		
	}
	
	@Override
	public void update(float elapsTime) {
		super.update(elapsTime);

		cameraController.updateCameraPosition();
		updateVerticalPosition();
		getNode().getWorldPosition();
		pc.sendDetailsForMessage(this.getID(), this.getNode());
		//if jumped and in the air.
		//updateJumpPosition();
		//System.out.println("Mae it hewre");
	}


	public Camera3PController getCameraController() {
		return cameraController;
	}

	public void setCameraController(Camera3PController cameraController) {
		this.cameraController = cameraController;
		cameraController.rotate(180);
	}
	
	/*@Override
	public void playWalkAnimation() {
		try {
			walkingMutex.acquire();
			skeleton.playAnimation("walkAnimation", 0.5f, EndType.STOP, 0);
			walkingMutex.release();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}*/

	@Override
public void playWalkAnimation() {
		System.out.println("playing animation");
		
		//walkingMutex.acquire();
		SkeletalEntity manSE =
				(SkeletalEntity) sm2.getEntity("knightSkeleton");
		/*skeleton.playAnimation("walkAnimation", 0.5f, EndType.STOP, 0);
		
		skeleton.stopAnimation();
		skeleton.playAnimation("walkAnimation", 0.5f, EndType.LOOP, 0);*/
		
		//manSE.playAnimation("walkAnimation", 0.5f, EndType.STOP, 0);
		
		manSE.stopAnimation();
		manSE.playAnimation("walkAnimation", 0.5f, EndType.LOOP, 0);
		//walkingMutex.release();
		
	}
	public void setupInputs(InputManager im, Controller controller) {
		if(cameraController==null)
			setCameraController(new Camera3PController(getCamera(), cameraNode, riderNode, im));
		Action moveAction = new MoveAction(this);
		Action yawAction = new YawAction(this);
		Action jumpAction = new JumpAction(this);
    	cameraController.addController(im, controller);
		if(controller.getType()==Controller.Type.GAMEPAD) {
        	
	    	im.associateAction(controller, 
	    			net.java.games.input.Component.Identifier.Axis.Y,
	    			moveAction, 
	    			InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
        	
	    	im.associateAction(controller, 
	    			net.java.games.input.Component.Identifier.Axis.X,
	    			moveAction, 
	    			InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
        	
	    	im.associateAction(controller, 
	    			net.java.games.input.Component.Identifier.Axis.RX,
	    			yawAction, 
	    			InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
	    			
    	}
    	else if (controller.getType()==Controller.Type.KEYBOARD) {
    		
    		im.associateAction(controller, 
    			net.java.games.input.Component.Identifier.Key.W, 
    			moveAction,
	    		InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
    		
    		im.associateAction(controller, 
    			net.java.games.input.Component.Identifier.Key.S, 
    			moveAction,
	    		InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
    		
    		im.associateAction(controller, 
    			net.java.games.input.Component.Identifier.Key.D, 
    			moveAction,
	    		InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
    		
    		im.associateAction(controller, 
    			net.java.games.input.Component.Identifier.Key.A, 
    			moveAction,
	    		InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
    		
    		im.associateAction(controller, 
    			net.java.games.input.Component.Identifier.Key.LEFT, 
    			yawAction,
	    		InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
    		
    		im.associateAction(controller, 
    			net.java.games.input.Component.Identifier.Key.RIGHT, 
    			yawAction,
	    		InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
    		
    		im.associateAction(controller, 
        			net.java.games.input.Component.Identifier.Key.SPACE, 
        			jumpAction,
    	    		InputManager.INPUT_ACTION_TYPE.ON_PRESS_ONLY);
    	}
	}

}

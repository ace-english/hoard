package myGameEngine;

import java.io.IOException;
import java.util.concurrent.Semaphore;

import hoardPVPGame.GameUtil.SKIN;
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
import ray.rage.scene.Tessellation;
import ray.rage.scene.SkeletalEntity.EndType;
import ray.rml.Degreef;
import ray.rml.Vector3;
import ray.rml.Vector3f;

public class OrbitalPlayer extends Player {

	private Camera3PController cameraController;
	SceneNode riderNode, cameraNode;
	private Semaphore walkingMutex;
	
	public OrbitalPlayer(SceneManager sm, ProtocolClient pc, SKIN skin) {
		super(sm, pc, skin);
		walkingMutex=new Semaphore(1);
	}


	@Override
	protected void setupNodes(SceneManager sm) throws IOException {
        
        TextureManager tm=sm.getTextureManager();
        String skinName;
        switch(skin) {
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
        skeleton = sm.createSkeletalEntity("knightSkeleton", "knight.rkm", "knight.rks");
        
        Texture texture=tm.getAssetByPath(skinName);
    	RenderSystem rs = sm.getRenderSystem();
    	TextureState state=(TextureState) rs.createRenderState(RenderState.Type.TEXTURE);
    	state.setTexture(texture);
    	skeleton.setRenderState(state);

        SceneNode node = sm.getRootSceneNode().createChildSceneNode("playerNode");
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
	}


	public Camera3PController getCameraController() {
		return cameraController;
	}

	public void setCameraController(Camera3PController cameraController) {
		this.cameraController = cameraController;
		cameraController.rotate(180);
	}
	

	public void setupInputs(InputManager im, Controller controller) {
		if(cameraController==null)
			setCameraController(new Camera3PController(getCamera(), cameraNode, riderNode, im));
		Action moveAction = new MoveAction(this);
		Action yawAction = new YawAction(this);
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
    	}
	}


	public void playWalkAnimation() {
		try {
			walkingMutex.acquire();
			skeleton.playAnimation("walkAnimation", 0.5f, EndType.STOP, 0);
			walkingMutex.release();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	
	protected void updateVerticalPosition(){ 
		SceneNode dolphinN = sm.getSceneNode("playerNode");
		try {
		SceneNode tessN = sm.getSceneNode("tessN");
		Tessellation tessE = ((Tessellation) tessN.getAttachedObject("tessE"));
		// Figure out Avatar's position relative to plane
		Vector3 worldAvatarPosition = dolphinN.getWorldPosition();
		Vector3 localAvatarPosition = dolphinN.getLocalPosition();
		// use avatar World coordinates to get coordinates for height
		Vector3 newAvatarPosition = Vector3f.createFrom(
			 // Keep the X coordinate
			 localAvatarPosition.x(),
			 // The Y coordinate is the varying height
			 tessE.getWorldHeight(
			worldAvatarPosition.x(),
			worldAvatarPosition.z()),
			 //Keep the Z coordinate
			 localAvatarPosition.z()
		);
		// use avatar Local coordinates to set position, including height
		dolphinN.setLocalPosition(newAvatarPosition);
		}
		catch(Exception E) {
			//no terrain found
		}
	}

}

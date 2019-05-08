package myGameEngine;

import java.io.IOException;
import java.util.concurrent.Semaphore;

import hoardPVPGame.GameUtil.SKIN;
import myGameEngine.MoveAction.Direction;
import net.java.games.input.Controller;
import net.java.games.input.Event;
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
	private ProtocolClient pc;/////////////////
	
	public OrbitalPlayer(SceneManager sm, ProtocolClient pc, SKIN skin) {
		super(sm, pc, skin);
		sm2=sm;
		this.pc = getProtocolClient();
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
		updateVerticalPosition();
		getNode().getWorldPosition();
		pc.sendDetailsForMessage(this.getID(), this.getNode());
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
		Action jumpAction = new JumpAction(this);///////////////
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
	
	@Override
	public boolean move(float arg0, Event arg1){
		super.move(arg0, arg1);
		boolean ret;
		if(dungeon!=null)
			ret=dungeon.isInBounds(getNode().getWorldPosition());
		else
			ret=true;
		if(ret==false) {
			//if out of bounds, bounce a little closer to the center
			float x=getNode().getWorldPosition().x();
			float z=getNode().getWorldPosition().z();
			float epsilon=0.2f;
			if(z>0) {
				getNode().translate(0,0,-epsilon);
			}
			else if (z<0) {
				getNode().translate(0,0,epsilon);
			}
			if(x>0) {
				getNode().translate(-epsilon, 0, 0);
			}
			else if(x<0) {
				getNode().translate(epsilon, 0, 0);
			}
		}
			if(ret) {
				
				//if(getProtocolClient()!=null)
					//getProtocolClient().sendMoveMessage(getID(), getNode().getWorldPosition());
				
				//updateVerticalPosition();///////////////////////
				playWalkAnimation();
			}
				

			return ret;
		}
	

	
	/*protected void updateVerticalPosition(){  
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
	}*/

}

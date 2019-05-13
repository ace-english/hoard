package myGameEngine;

import java.io.IOException;
import java.util.concurrent.Semaphore;

import hoardPVPGame.GameUtil.SKIN;
import myGameEngine.MoveAction.Direction;
import net.java.games.input.Controller;
import net.java.games.input.Event;
import ray.input.InputManager;
import ray.input.action.Action;
import ray.physics.PhysicsObject;
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
	//PhysicsObject physObj;
	private ProtocolClient pc;
	//private float acceleration = 0.025f;
	private float acceleration = 0.01f;
	private float velocity = 0.0f;
	private boolean jumped = false;
	private float jumpPosition = 0;
	private float displacement = 0;
	private float jumpHeight = 0;
	private Vector3 previousPosition;
	private boolean isWalking;
	
	public OrbitalPlayer(SceneManager sm, ProtocolClient pc, SKIN skin) {
		super(sm, pc, skin);
		sm2=sm;
		this.pc = getProtocolClient();
		isWalking=false;
		previousPosition=this.getNode().getWorldPosition();
	}


	
	public PhysicsObject getPhysObj() {
		return getNode().getPhysicsObject();
	}


	public void setPhysicsObject(PhysicsObject physObj) {
		getNode().setPhysicsObject(physObj);
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
		Vector3 currentPosition=getNode().getLocalPosition();
		if(currentPosition.compareTo(previousPosition)!=0){	//changing position
			if(isWalking==false) {	//going from still to walking
				System.out.println("Playing walk");
				playWalkAnimation();
				isWalking=true;
			}
		}
		else { //not changing position
			if(isWalking==true) {	//going from walking to still
				stopWalkAnimation();
				isWalking=false;
			}
		}
		previousPosition=currentPosition;
		
		
	}



	public void setVelocity(float v)
	{
		velocity = v;
	}
	
	public void setJumped(boolean jump)
	{
		this.jumped = jump;
	}
	
	public boolean getJumped()
	{
		return jumped;
	}
	

	public Camera3PController getCameraController() {
		return cameraController;
	}

	public void setCameraController(Camera3PController cameraController) {
		this.cameraController = cameraController;
		cameraController.rotate(180);
	}
	

	public void setJumpHeight(){
		// Figure out Avatar's position relative to plane
		Vector3 worldAvatarPosition = getNode().getWorldPosition();
		
		float groundHeight;
		SceneNode tessN=null;
		Tessellation tessE=null;
		try {
			tessN =sm2.getSceneNode("tessN");
		}
		catch(Exception e) {
			//no terrain
		}
		if(tessN!=null) {
			tessE = ((Tessellation) tessN.getAttachedObject("tessE"));
			groundHeight=tessE.getWorldHeight(
					worldAvatarPosition.x(),
					worldAvatarPosition.z());
		}
		else {
			groundHeight=dungeon.getNode().getWorldPosition().z();
		}
		jumpHeight = groundHeight;
	}
	
	public void updateVerticalPosition(){ 
		// Figure out Avatar's position relative to plane
		Vector3 worldAvatarPosition = getNode().getWorldPosition();
		Vector3 localAvatarPosition = getNode().getLocalPosition();
		
		float groundHeight;
		SceneNode tessN=null;
		Tessellation tessE=null;
		try {
			tessN =sm2.getSceneNode("tessN");
		}
		catch(Exception e) {
			//no terrain
		}
		if(tessN!=null) {
			tessE = ((Tessellation) tessN.getAttachedObject("tessE"));
			groundHeight=tessE.getWorldHeight(
					worldAvatarPosition.x(),
					worldAvatarPosition.z());
		}
		else {
			groundHeight=dungeon.getNode().getWorldPosition().z();
		}
	
		if(jumped){
			//System.out.println("Velocity: " + velocity);
			velocity = velocity - acceleration;
			if(tessN!=null) {
			displacement = jumpHeight - groundHeight;
			}
			jumpPosition = jumpPosition + velocity;
			/*
		System.out.println("Jump Position: " + jumpPosition);
		System.out.println("Jump Displacement: " + displacement);
		System.out.println("Jump Height: " + jumpHeight);
		System.out.println("heightmap Height: " + 
		tessE.getWorldHeight(
				worldAvatarPosition.x(),
				worldAvatarPosition.z()) );*/
		}
		// use avatar World coordinates to get coordinates for height
		Vector3 newAvatarPosition = Vector3f.createFrom(
			 // Keep the X coordinate
			 localAvatarPosition.x(),
			 // The Y coordinate is the varying height
			 groundHeight + jumpPosition + displacement,
			 //Keep the Z coordinate
			 localAvatarPosition.z()
			);
		// use avatar Local coordinates to set position, including height
		getNode().setLocalPosition(newAvatarPosition );
		
		
		
		
		if ( getNode().getLocalPosition().y() <  groundHeight)
				{
			
			jumped = false;
			 velocity = 0.0f;
			 jumpPosition = 0;
			 displacement = 0;
			 newAvatarPosition = Vector3f.createFrom(
					 // Keep the X coordinate
					 localAvatarPosition.x(),
					 // The Y coordinate is the varying height
					 groundHeight,
					 //Keep the Z coordinate
					 localAvatarPosition.z()
					);
			 getNode().setLocalPosition(newAvatarPosition );
				}
		 //jumpTest = jumpTest - 0.01f;
		 //if ( jumpTest < 0 )
			// jumpTest = 0;
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


	public void playWalkAnimation() {
		skeleton.playAnimation("walkAnimation", 0.5f, EndType.LOOP, 0);
	}


	private void stopWalkAnimation() {
		skeleton.stopAnimation();
		
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
				

			return ret;
		}
	

}

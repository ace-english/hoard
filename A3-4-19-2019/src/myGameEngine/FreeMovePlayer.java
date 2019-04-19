package myGameEngine;

import java.io.IOException;

import hoardPVPGame.AddRoomAction;
import hoardPVPGame.Dungeon;
import net.java.games.input.Controller;
import ray.input.InputManager;
import ray.input.action.Action;
import ray.rage.asset.texture.Texture;
import ray.rage.asset.texture.TextureManager;
import ray.rage.rendersystem.RenderSystem;
import ray.rage.rendersystem.Renderable.Primitive;
import ray.rage.rendersystem.states.RenderState;
import ray.rage.rendersystem.states.TextureState;
import ray.rage.scene.Camera;
import ray.rage.scene.Entity;
import ray.rage.scene.SceneManager;
import ray.rage.scene.SceneNode;
import ray.rml.Degreef;
import ray.rml.Vector3f;

public class FreeMovePlayer extends Player {
	
	Dungeon dungeon;
	SceneNode cameraNode;

	public FreeMovePlayer(SceneManager sm, ProtocolClient pc, Dungeon dungeon) {
		super(sm, pc);
		speed=0.12f;
		cameraNode=getNode().createChildSceneNode("riderNode");
        cameraNode.attachObject(getCamera());
        cameraNode.moveUp(0.5f);
		getCamera().setMode('n');
		this.dungeon=dungeon;
	}

	@Override
	protected void setupNodes(SceneManager sm) throws IOException {
        Entity entity = sm.createEntity("player", "dolphinHighPoly.obj");
        //Entity entity = sm.createEntity("player", "dragon1.obj");
        entity.setPrimitive(Primitive.TRIANGLES);

        
        
    	
    	
        SceneNode node = sm.getRootSceneNode().createChildSceneNode(entity.getName() + "Node");
        node.moveForward(2.0f);
        node.moveUp(1.0f);
        node.attachObject(entity);
        node.rotate(Degreef.createFrom(180), Vector3f.createFrom(0.0f, 1.0f, 0.0f));
		setNode(node);
    	
        node.createChildSceneNode("RiderNode").moveUp(0.8f);
        
        cameraNode = sm.getSceneNode("MainCameraNode");
	}

	@Override
	public void setupInputs(InputManager im, Controller controller) {
		Action moveAction=new MoveAction(this);
    	Action pitchAction=new PitchAction(this);
    	Action yawAction=new YawAction(this);
    	Action addRoomAction = new AddRoomAction(dungeon);
    	Action rollAction = new RollAction(this);
    	
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
		    			net.java.games.input.Component.Identifier.Axis.RY,
		    			pitchAction, 
		    			InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
	        	
		    	im.associateAction(controller, 
		    			net.java.games.input.Component.Identifier.Axis.RX,
		    			yawAction, 
		    			InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
	    	}
	    	else if (controller.getType()==Controller.Type.KEYBOARD) {
	    		
	    		im.associateAction(controller, 
	    			net.java.games.input.Component.Identifier.Key.R, 
	    			addRoomAction,
		    		InputManager.INPUT_ACTION_TYPE.ON_PRESS_ONLY);
	    		
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
	    			net.java.games.input.Component.Identifier.Key.UP, 
	    			pitchAction,
		    		InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
	    		
	    		im.associateAction(controller, 
	    			net.java.games.input.Component.Identifier.Key.DOWN, 
	    			pitchAction,
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
	    			net.java.games.input.Component.Identifier.Key.Q, 
	    			rollAction,
		    		InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
	    		
	    		im.associateAction(controller, 
	    			net.java.games.input.Component.Identifier.Key.E, 
	    			rollAction,
		    		InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
	    	
    	}
		
	}

}

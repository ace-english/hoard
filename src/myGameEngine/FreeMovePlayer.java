package myGameEngine;

import hoardPVPGame.AddRoomAction;
import hoardPVPGame.Dungeon;
import net.java.games.input.Controller;
import ray.input.InputManager;
import ray.input.action.Action;
import ray.rage.scene.Camera;
import ray.rage.scene.SceneManager;
import ray.rage.scene.SceneNode;

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
	protected void setupNodes(SceneManager sm) {
		setNode(sm.getSceneNode("playerNode"));
		
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

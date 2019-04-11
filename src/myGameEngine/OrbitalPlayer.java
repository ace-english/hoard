package myGameEngine;

import java.io.IOException;

import net.java.games.input.Controller;
import ray.input.InputManager;
import ray.input.action.Action;
import ray.rage.rendersystem.Renderable.Primitive;
import ray.rage.scene.Camera;
import ray.rage.scene.Entity;
import ray.rage.scene.SceneManager;
import ray.rage.scene.SceneNode;
import ray.rml.Degreef;
import ray.rml.Vector3f;

public class OrbitalPlayer extends Player {
	
	private Camera3PController cameraController;
	SceneNode riderNode;

	public OrbitalPlayer(SceneManager sm, ProtocolClient pc) {
		super(sm, pc);
		
	}

	@Override
	protected void setupNodes(SceneManager sm) throws IOException {
        Entity entity = sm.createEntity("player", "dolphinHighPoly.obj");
        entity.setPrimitive(Primitive.TRIANGLES);

        SceneNode node = sm.getRootSceneNode().createChildSceneNode(entity.getName() + "Node");
        node.moveForward(2.0f);
        node.moveUp(1.0f);
        node.attachObject(entity);
        node.rotate(Degreef.createFrom(180), Vector3f.createFrom(0.0f, 1.0f, 0.0f));
		setNode(node);
    	
        riderNode = node.createChildSceneNode("RiderNode");
        riderNode.moveUp(0.8f);
		
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
	}
	

	public void setupInputs(InputManager im, Controller controller) {
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

}

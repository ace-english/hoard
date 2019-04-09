package myGameEngine;

import java.util.ArrayList;

import net.java.games.input.Controller;
import net.java.games.input.Event;
import ray.input.InputManager;
import ray.input.action.AbstractInputAction;
import ray.input.action.Action;
import ray.rage.scene.*;
import ray.rml.*;

public class Camera3PController {
	private Camera camera;
	private SceneNode cameraN;
	private SceneNode target;
	private float cameraAzimuth;
	private float cameraElevation;
	private float radius;
	private Vector3 targetPos;
	private Vector3 worldUpVec;
	
	
	public Camera3PController(Camera camera, SceneNode cameraN, SceneNode target,
			InputManager im) {
		super();
		this.camera = camera;
		this.cameraN = cameraN;
		this.target = target;
		this.cameraAzimuth = 225.0f;
		this.cameraElevation = 20.0f;
		this.radius = 2.0f;
		worldUpVec=Vector3f.createFrom(0f,1f,0f);
		//addController(im,controller);
		updateCameraPosition();
		rotate(135f);
	}
	
	public void updateCameraPosition() {
		double theta=Math.toRadians(cameraAzimuth);
		double phi=Math.toRadians(cameraElevation);
		double x = radius*Math.cos(phi)*Math.sin(theta);
		double y = radius*Math.sin(phi);
		double z = radius * Math.cos(phi)*Math.cos(theta);
		
		Vector3 newPosition = Vector3f.createFrom
				((float)x,(float)y,(float)z).add(target.getWorldPosition());
		if(newPosition.y()>0) {
			cameraN.setLocalPosition(newPosition);
			
			cameraN.lookAt(target, worldUpVec);
		}
	}
	
	
	public void addController(InputManager im, Controller controller) {
		Action orbitAAction = new OrbitAroundAction();
		Action zoomAction = new ZoomAction();
		Action rotateAction = new RotateAction();
		
		if(controller.getType()==Controller.Type.GAMEPAD) {
		im.associateAction(controller, 
				net.java.games.input.Component.Identifier.Axis.RY, orbitAAction, 
				InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		im.associateAction(controller, 
				net.java.games.input.Component.Identifier.Axis.Z, zoomAction, 
				InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		im.associateAction(controller, 
				net.java.games.input.Component.Identifier.Button._5, rotateAction, 
				InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		im.associateAction(controller, 
				net.java.games.input.Component.Identifier.Button._4, rotateAction, 
				InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		}
		
		else if(controller.getType()==Controller.Type.KEYBOARD) {
			ArrayList<Controller> controllers = im.getControllers();
			for(Controller c:controllers) {
				if(c.getType()==Controller.Type.KEYBOARD) {
					im.associateAction(c, 
							net.java.games.input.Component.Identifier.Key.Z, zoomAction, 
							InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
					im.associateAction(c, 
							net.java.games.input.Component.Identifier.Key.C, zoomAction, 
							InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
					im.associateAction(c, 
							net.java.games.input.Component.Identifier.Key.Q, rotateAction, 
							InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
					im.associateAction(c, 
							net.java.games.input.Component.Identifier.Key.E, rotateAction, 
							InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
					im.associateAction(c, 
							net.java.games.input.Component.Identifier.Key.UP, orbitAAction, 
							InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
					im.associateAction(c, 
							net.java.games.input.Component.Identifier.Key.DOWN, orbitAAction, 
							InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
				}
			}
		}
		
		
	}
	
	private class OrbitAroundAction extends AbstractInputAction{

		@Override
		public void performAction(float time, Event evt) {
			float rotAmount;
			if(evt.getValue()<-0.2||evt.getValue()>0.2)
				rotAmount=evt.getValue();
			else
				rotAmount=0f;
			String name=evt.getComponent().getName();
			if(name.equals("RY")||name.equals("Down"))
				rotAmount=-rotAmount;
			rotAmount+=cameraElevation;
			if((rotAmount>-45&&rotAmount<90)) {
				cameraElevation=rotAmount;
				cameraElevation = cameraElevation %360;
				updateCameraPosition();
			}
			
		}
		
	}
	
	private class RotateAction extends AbstractInputAction{

		@Override
		public void performAction(float time, Event evt) {
			float rotAmount;
			if(evt.getValue()<-0.2||evt.getValue()>0.2)
				rotAmount=evt.getValue();
			else
				rotAmount=0f;
			String name=evt.getComponent().getName();
			if(name.equals("Button 4")||name.equals("Q"))
				rotAmount=-rotAmount;
			rotAmount+=cameraAzimuth;
			cameraAzimuth=rotAmount;
			cameraAzimuth = cameraAzimuth %360;
			updateCameraPosition();
			
		}
		
	}
	
	private class ZoomAction extends AbstractInputAction{

		@Override
		public void performAction(float time, Event evt) {
			float zAmount;
			if(evt.getValue()<-0.2||evt.getComponent().getName().equals("Z"))
				zAmount=-0.05f;
			else
				if(evt.getValue()>0.2)
					zAmount=0.05f;
				else
					zAmount=0f;
			if(radius+zAmount>0.1f&&radius+zAmount<10) {
				radius+=zAmount;
				//System.out.println("radius: "+ radius);
				updateCameraPosition();
			}
			
		}
		
	}

	public void rotate(float angle) {
		cameraAzimuth+=angle;
		cameraAzimuth = cameraAzimuth %360;
		updateCameraPosition();
		
	}
	
	
	
}
	
	


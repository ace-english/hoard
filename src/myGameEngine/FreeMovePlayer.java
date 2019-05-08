package myGameEngine;

import java.io.IOException;

import hoardPVPGame.AddRoomAction;
import hoardPVPGame.Dungeon;
import hoardPVPGame.GameUtil;
import hoardPVPGame.GameUtil.SKIN;
import net.java.games.input.Controller;
import ray.input.InputManager;
import ray.input.action.Action;
import ray.rage.Engine;
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
import ray.rage.scene.SkeletalEntity;
import ray.rage.scene.SkeletalEntity.EndType;
import ray.rml.Degreef;
import ray.rml.Vector3;
import ray.rml.Vector3f;

public class FreeMovePlayer extends Player {
	
	SceneNode cameraNode;
	SceneNode rider;

	public FreeMovePlayer(SceneManager sm, ProtocolClient pc, Dungeon dungeon, GameUtil.SKIN skin) {
		super(sm, pc, skin);
		speed=0.12f;
		cameraNode=getNode().createChildSceneNode("riderNode");
        cameraNode.attachObject(getCamera());
        //cameraNode.moveUp(2f);
        //cameraNode.translate(7f, 10f, -30f);
		getCamera().setMode('n');
		this.dungeon=dungeon;
	}

	@Override
	protected void setupNodes(SceneManager sm) throws IOException {
        //Entity entity = sm.createEntity("player", "dragon.obj");
        //entity.setPrimitive(Primitive.TRIANGLES);
        
        TextureManager tm=sm.getTextureManager();
        String skinName;
        switch(skin) {
		case GREEN_DRAGON:
			skinName="green_dragon.png";
			break;
		case RED_DRAGON:
			skinName="red_dragon.png";
			break;
		case BLACK_DRAGON:
			skinName="black_dragon.png";
			break;
		case PURPLE_DRAGON:
			skinName="purple_dragon.png";
			break;
		default:
			skinName="default.png";
			break;
        
        }
        skeleton = sm.createSkeletalEntity("dragonSkeleton", "dragon.rkm", "dragon.rks");
        
        Texture texture=tm.getAssetByPath(skinName);
    	RenderSystem rs = sm.getRenderSystem();
    	TextureState state=(TextureState) rs.createRenderState(RenderState.Type.TEXTURE);
    	state.setTexture(texture);
    	skeleton.setRenderState(state);

        SceneNode node = sm.getRootSceneNode().createChildSceneNode("playerNode");
        node.attachObject(skeleton);
        
        
        //node.rotate(Degreef.createFrom(90f), Vector3f.createFrom(0f, 1f, 0f));
		//node.rotate(Degreef.createFrom(90f), (0f, 1f, 0f));
        //node.moveForward(2.0f);
        node.rotate(Degreef.createFrom(90), Vector3f.createFrom(0.0f, 1.0f, 0.0f));
        //node.moveUp(10.0f);
        //node.moveBackward(20f);
		setNode(node);
		
		skeleton.loadAnimation("idleAnimation", "dragon_idle.rka");
		skeleton.playAnimation("idleAnimation", 0.5f, EndType.LOOP, 0);
        
        cameraNode = sm.getSceneNode("MainCameraNode");
        cameraNode.moveUp(30f);
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

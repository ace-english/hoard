package hoardPVPGame;

import java.io.IOException;

import ray.physics.*;
import ray.rage.asset.texture.Texture;
import ray.rage.asset.texture.TextureManager;
import ray.rage.rendersystem.RenderSystem;
import ray.rage.rendersystem.states.RenderState;
import ray.rage.rendersystem.states.TextureState;
import ray.rage.scene.Entity;
import ray.rage.scene.SceneManager;
import ray.rage.scene.SceneNode;
import ray.rml.Vector3;
import ray.rml.Vector3f;

public class SpikeTrap extends Trap {

	float velocity;
	
	
	public SpikeTrap(Room room, SceneManager sm, PhysicsEngine pe) {
		super();
		TextureManager tm=sm.getTextureManager();
        Entity entity;
		try {
			entity = sm.createEntity("trap"+Trap.getNumTraps(), "spikes.obj");
        
	        Texture texture=tm.getAssetByPath("spikes.png");
	    	RenderSystem rs = sm.getRenderSystem();
	    	TextureState state=(TextureState) rs.createRenderState(RenderState.Type.TEXTURE);
	    	state.setTexture(texture);
	    	entity.setRenderState(state);
	
	        SceneNode node = room.getRoomNode().createChildSceneNode("TrapNode"+Trap.getNumTraps());
	        node.attachObject(entity);
	        
	        
			setTrapNode(node);
			node.moveDown(3f);
			velocity=0.05f;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean isColliding(Vector3 pos) {
		if(Math.abs(pos.z()-getTrapNode().getWorldPosition().z())<2f){//same z, check for collisions
			if(Math.abs(pos.y()-getTrapNode().getWorldPosition().y())<3f){//same y
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean willCollide(Vector3 pos) {
		System.out.println("knight: "+pos+ " spike: "+getTrapNode().getWorldPosition());
		if(getTrapNode().getWorldPosition().y()>-2f) {
			System.out.print("checked Y");
			if(pos.z()-getTrapNode().getWorldPosition().z()<3&&pos.z()-getTrapNode().getWorldPosition().z()>0) {
				System.out.print("checked Z");
				return true;
			}
		}
		return false;
	}
	
	public GameUtil.TRAP_TYPE getType() {
		return GameUtil.TRAP_TYPE.Spike;
	}

	@Override
	public void update(float elapsTime) {
		getTrapNode().moveUp(velocity);
		if(getTrapNode().getWorldPosition().y()>=-0.1) {
			velocity=-velocity;
		}
		if(getTrapNode().getWorldPosition().y()<=-3f) {
			velocity=-velocity;
		}
	}

}

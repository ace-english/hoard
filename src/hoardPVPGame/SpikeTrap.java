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
import ray.rml.Vector3f;

public class SpikeTrap extends Trap {

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
	        
	        float mass = 1.0f;
	    	float dim[] = {6f, 2f, 9f};
	    	float[] temptf = node.getLocalTransform().toFloatArray();
	    	double[] tf= new double[16];
	    	for(int i=0; i<16; i++) {
	    		tf[i]=temptf[i];
	    	}
	        PhysicsObject physObj=pe.addBoxObject(pe.nextUID(), mass, tf, dim);
	        
			setTrapNode(node);
			node.setPhysicsObject(physObj);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean isColliding(Vector3f pos) {
		if(Math.abs(pos.x()-getTrapNode().getWorldPosition().x())<0.1f){//same x, check for collisions
			return true;
		}
		return false;
	}
	
	public String getType() {
		return "spike";
	}

	@Override
	public void update(float elapsTime) {
		if(getTrapNode().getWorldPosition().y()>=0) {
			getTrapNode().getPhysicsObject().setLinearVelocity(new float[]{0f,-0.8f,0f});
		}
		if(getTrapNode().getWorldPosition().y()<-1) 
			getTrapNode().getPhysicsObject().setLinearVelocity(new float[]{0f,0.8f,0f});
		System.out.println("moving"+getTrapNode().getPhysicsObject().getLinearVelocity());
		
	}

}

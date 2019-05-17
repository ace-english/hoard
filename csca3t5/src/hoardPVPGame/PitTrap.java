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
import ray.rml.Degreef;
import ray.rml.Vector3;
import ray.rml.Vector3f;

public class PitTrap extends Trap {

	public PitTrap(Room room, SceneManager sm) {
		super();
		TextureManager tm=sm.getTextureManager();
        Entity entity;
		try {
			entity = sm.createEntity("trap"+Trap.getNumTraps(), "pit.obj");
        
	        Texture texture=tm.getAssetByPath("pit.png");
	    	RenderSystem rs = sm.getRenderSystem();
	    	TextureState state=(TextureState) rs.createRenderState(RenderState.Type.TEXTURE);
	    	state.setTexture(texture);
	    	entity.setRenderState(state);
	
	        SceneNode node = room.getRoomNode().createChildSceneNode("TrapNode"+Trap.getNumTraps());
	        node.rotate(Degreef.createFrom(90f), Vector3f.createFrom(0f, 1f, 0f));
	        node.scale(Vector3f.createFrom(2f,2f,1.5f));
	        node.attachObject(entity);
			setTrapNode(node);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean isColliding(Vector3 pos) {
		if(Math.abs(pos.y()-getTrapNode().getWorldPosition().y())<0.001f){
			if(Math.abs(pos.z()-getTrapNode().getWorldPosition().z())<2f){
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean willCollide(Vector3 knight) {
		return getTrapNode().getWorldPosition().z()-knight.z()<2.1f;
	}
	
	public GameUtil.TRAP_TYPE getType() {
		return GameUtil.TRAP_TYPE.Pit;
	}

	@Override
	public void update(float elapsTime) {
		
	}

}

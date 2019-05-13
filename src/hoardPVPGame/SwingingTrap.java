package hoardPVPGame;

import java.io.IOException;

import myGameEngine.OrbitalPlayer;
import ray.physics.*;
import ray.rage.asset.texture.Texture;
import ray.rage.asset.texture.TextureManager;
import ray.rage.rendersystem.RenderSystem;
import ray.rage.rendersystem.states.RenderState;
import ray.rage.rendersystem.states.TextureState;
import ray.rage.scene.*;
import ray.rml.Degreef;
import ray.rml.Vector3;
import ray.rml.Vector3f;

public class SwingingTrap extends Trap {

	public SwingingTrap(Room room, SceneManager sm, PhysicsEngine pe) {
		super();
		TextureManager tm=sm.getTextureManager();
        Entity entity;
		try {
			entity = sm.createEntity("trap"+Trap.getNumTraps(), "axe.obj");
        
	        Texture texture=tm.getAssetByPath("axe.png");
	    	RenderSystem rs = sm.getRenderSystem();
	    	TextureState state=(TextureState) rs.createRenderState(RenderState.Type.TEXTURE);
	    	state.setTexture(texture);
	    	entity.setRenderState(state);
	
	        SceneNode node = room.getRoomNode().createChildSceneNode("TrapNode"+Trap.getNumTraps());
	        node.attachObject(entity);
	        node.translate(0f, GameUtil.getRoomSize(), 0f);
	        node.rotate(Degreef.createFrom(90f), Vector3f.createFrom(0f, 1f, 0f));
	        
	        //float mass = 1.0f;
	    	//double up[] = {0,1,0};
	    	//float[] temptf = node.getLocalTransform().toFloatArray();
	       // PhysicsObject physObj=pe.addCylinderZObject(pe.nextUID(), mass, up, temptf);
	        
			setTrapNode(node);
		//	node.setPhysicsObject(physObj);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean isColliding(Vector3 knightCenter) {
		Vector3 trapCenter=getTrapNode().getWorldPosition();
		trapCenter=trapCenter.add(0,-4.5f,0);
		
		if(((knightCenter.x()-.75)<(trapCenter.x()+1.1)&&(knightCenter.x()+.75)>(trapCenter.x()-1.1))) {
			if(((knightCenter.y())<(trapCenter.y()+.6)&&(knightCenter.y()+3)>(trapCenter.y()-.6))) {
				if(((knightCenter.z()-.5)<(trapCenter.z()+.25)&&(knightCenter.z()+.5)>(trapCenter.z()-.25))) {
					return true;
				}
			}
		}
		
		return false;
	}

	@Override
	public String getType() {
		return "swinging";
	}

	@Override
	public void update(float elapsTime) {
		// TODO Auto-generated method stub
		
	}

}

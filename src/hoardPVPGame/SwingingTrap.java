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
import ray.rml.Angle;
import ray.rml.Degreef;
import ray.rml.Vector3;
import ray.rml.Vector3f;

public class SwingingTrap extends Trap {
    Vector3 hinge, pendulum;
    float velocity;
    float acceleration=0.1f;
    float length=4.5f;
    Angle angle;

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
	        
	        hinge=node.getWorldPosition();
	        pendulum=hinge.add(0,-length,0);
	        velocity=3f;
	        angle=Degreef.createFrom(90);
			System.out.println("Pendulum: " + pendulum);
	        
			setTrapNode(node);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean isColliding(Vector3 knightCenter) {
		if(((knightCenter.x()-.75)<(pendulum.x()+1.1)&&(knightCenter.x()+.75)>(pendulum.x()-1.1))) {
			if(((knightCenter.y())<(pendulum.y()+.6)&&(knightCenter.y()+3)>(pendulum.y()-.6))) {
				if(((knightCenter.z()-.5)<(pendulum.z()+.25)&&(knightCenter.z()+.5)>(pendulum.z()-.25))) {
					return true;
				}
			}
		}
		
		return false;
	}

	@Override
	public boolean willCollide(Vector3 knightCenter) {
		if(((knightCenter.x()-.75)<(pendulum.x()+1.5)&&(knightCenter.x()+.75)>(pendulum.x()-1.5))) {
			if(((knightCenter.y())<(pendulum.y()+.8)&&(knightCenter.y()+3)>(pendulum.y()-.8))) {
				if(knightCenter.z()-pendulum.z()<1&&knightCenter.z()-pendulum.z()>0) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public GameUtil.TRAP_TYPE getType() {
		return GameUtil.TRAP_TYPE.Swinging;
	}

	@Override
	public void update(float elapsTime) {
		Angle angle=Degreef.createFrom(velocity);
		getTrapNode().rotate(angle, Vector3f.createFrom(1f,0,0));
		this.angle=this.angle.add(angle);
		velocity-=acceleration;
		if(velocity>3)
			velocity=3f;
		else if (velocity<-3)
			velocity=-3f;
		
		float x=(float) (length*Math.cos(this.angle.valueRadians()));
		float y=(float) (length-(length*Math.sin(this.angle.valueRadians())));
		pendulum=Vector3f.createFrom(x, y,pendulum.z());
		//System.out.println("Angle: "+this.angle.valueDegrees()+" Velocity: "+velocity+" Pendulum: " + pendulum);
		
		
		if(Math.abs(pendulum.x())<0.1) {
			acceleration=-acceleration;
			//System.out.println("Switch!");
		}
			
		
	}


}

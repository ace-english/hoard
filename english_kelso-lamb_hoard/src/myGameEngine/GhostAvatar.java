package myGameEngine;

import java.util.UUID;

import ray.rage.asset.texture.Texture;
import ray.rage.rendersystem.RenderSystem;
import ray.rage.rendersystem.states.RenderState;
import ray.rage.rendersystem.states.TextureState;
import ray.rage.scene.*;
import ray.rage.scene.SkeletalEntity.EndType;
import ray.rml.Angle;
import ray.rml.Matrix3;
import ray.rml.Matrix3f;
import ray.rml.Vector3;

public class GhostAvatar{ 
	private UUID id;
	private SceneNode node;
	private Entity entity;
	private Vector3 pos;
	private Vector3 lastPos;
	private Matrix3f rot;
	protected SkeletalEntity skeleton;
	protected SkeletalEntity sEntity;
	
	public GhostAvatar(UUID id, Vector3 position, Matrix3f rotMat){ 
		System.out.println("avatar created");
		this.id = id;
		this.pos=position;
		this.rot = rotMat;
		
	}
	
	public Matrix3f getRot() {
		return rot;
	}

	public void setRot(Matrix3f rot) {
		this.rot = rot;
	}

	public Vector3 getPos() {
		return pos;
	}
	public void setPos(Vector3 pos) {
		node.setLocalPosition(pos);
		this.pos=pos;
	}
	public Object getID() {
		return id;
	}
	public Entity getEntity() {
		return entity;
	}
	
	public Vector3 getLastPos() {
		return lastPos;
	}
	public void setLastPos(Vector3 pos) {
		//node.setLocalPosition(pos);
		this.lastPos=pos;
	}
	
	public SkeletalEntity getSkeletalEntity() {
		return sEntity;
	}
	
	public void setSkeletalEntity(SkeletalEntity sEntity) {
		this.sEntity = sEntity;
	}
	public void setEntity(Entity entity) {
		this.entity = entity;
	}
	public SceneNode getNode() {
		return node;
	}
	public void setNode(SceneNode node) {
		this.node = node;
	}
	public void rotate(char axis, Angle angle) {
		switch (axis) {
		case 'x':
			node.pitch(angle);
			break;
		case 'y':
			node.yaw(angle);
			break;
		case 'z':
			node.roll(angle);
			break;
		default:
			System.err.println("Invalid rotation.");
		}
		

			
		
	}

}

package myGameEngine;

import java.util.UUID;

import ray.rage.scene.*;
import ray.rml.Angle;
import ray.rml.Matrix3;
import ray.rml.Vector3;

public class GhostAvatar{ 
	private UUID id;
	private SceneNode node;
	private Entity entity;
	private Vector3 pos;
	
	public GhostAvatar(UUID id, Vector3 position){ 
		System.out.println("avatar created");
		this.id = id;
		this.pos=position;
		
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

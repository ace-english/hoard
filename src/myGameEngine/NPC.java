package myGameEngine;

import hoardPVPGame.GameUtil;
import ray.rml.Vector3;
import ray.rml.Vector3f;

public class NPC {
	double locX, locY, locZ; // other state info goes here (FSM)
	public double getX() { return locX; }
	public double getY() { return locY; }
	public double getZ() { return locZ; }
	GameUtil.SKIN skin;
	boolean isDead;
	
	public boolean isDead() {
		return isDead;
	}
	public void setDead(boolean isDead) {
		this.isDead = isDead;
	}
	NPC(){
		int r=(int) Math.floor(Math.random()*4);
		switch(r) {
		case 0:
			skin=GameUtil.SKIN.KNIGHT;
			break;
		case 1:
			skin=GameUtil.SKIN.BLACK_KNIGHT;
			break;
		case 2:
			skin=GameUtil.SKIN.WHITE_KNIGHT;
			break;
		case 3:
			skin=GameUtil.SKIN.GOLD_KNIGHT;
			break;
		}
		
	}
	
	public void updateLocation(double x, double y, double z) {
		locX=x;
		locY=y;
		locZ=z;
	}
	
	public GameUtil.SKIN getSkin(){
		return skin;
	}
	public void update() {
		// TODO Auto-generated method stub
		
	}
	public Vector3f getPos() {
		return (Vector3f) Vector3f.createFrom((float)locX, (float)locY, (float)locZ);
	}
	public void setPos(Vector3 add) {
		locX=add.x();
		locY=add.y();
		locZ=add.z();
		
	}
	public void moveForward() {
		// TODO Auto-generated method stub
		
	}
}

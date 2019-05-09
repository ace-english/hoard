package myGameEngine;

import java.io.IOException;

import hoardPVPGame.GameUtil;
import myGameEngine.MoveAction.Direction;
import net.java.games.input.Event;
import ray.rage.asset.texture.Texture;
import ray.rage.asset.texture.TextureManager;
import ray.rage.rendersystem.RenderSystem;
import ray.rage.rendersystem.states.RenderState;
import ray.rage.rendersystem.states.TextureState;
import ray.rage.scene.SceneManager;
import ray.rage.scene.SceneNode;
import ray.rage.scene.SkeletalEntity;
import ray.rage.scene.SkeletalEntity.EndType;
import ray.rml.Degreef;
import ray.rml.Vector3;
import ray.rml.Vector3f;

public class NPC {
	GameUtil.SKIN skin;
	boolean isDead;
	SceneNode node;
	private Vector3 previousPosition;
	private boolean isWalking=false;
	private SkeletalEntity skeleton;
	
	public NPC(SceneManager sm) throws IOException{
		System.out.println("Drawing NPC");

        skeleton = sm.createSkeletalEntity("npcSkeleton", "knight.rkm", "knight.rks");

		int r=(int) Math.floor(Math.random()*4);
        String skinName;
		switch(r) {
		case 0:
			skinName="knight.png";
			break;
		case 1:
			skinName="black_knight.png";
			break;
		case 2:
			skinName="white_knight.png";
			break;
		case 3:
			skinName="gold_knight.png";
			break;
		default:
			skinName="default.png";
			break;
        
        }
        skeleton = sm.createSkeletalEntity("knightSkeleton", "knight.rkm", "knight.rks");
        
		TextureManager tm=sm.getTextureManager();
        Texture texture=tm.getAssetByPath(skinName);
    	RenderSystem rs = sm.getRenderSystem();
    	TextureState state=(TextureState) rs.createRenderState(RenderState.Type.TEXTURE);
    	state.setTexture(texture);
    	skeleton.setRenderState(state);
		
		node = sm.getRootSceneNode().createChildSceneNode("npcNode");
		node.attachObject(skeleton);
		//ghostN.setLocalPosition(npc.getPos());
        node.scale(.5f, .5f, .5f);
        node.rotate(Degreef.createFrom(180f), Vector3f.createFrom(0f,1f,0f));
        

		skeleton.loadAnimation("walkAnimation", "knight_walk.rka");
		previousPosition=node.getWorldPosition();
	}
	
	public SceneNode getNode() {
		return node;
	}
	public void setNode(SceneNode node) {
		this.node = node;
	}
	public boolean isDead() {
		return isDead;
	}
	public void setDead(boolean isDead) {
		this.isDead = isDead;
	}

	
	public GameUtil.SKIN getSkin(){
		return skin;
	}
	public void update() {
		//move();
		Vector3 currentPosition=getNode().getLocalPosition();
		if(currentPosition.compareTo(previousPosition)!=0){	//changing position
			if(isWalking==false) {	//going from still to walking
				System.out.println("Playing walk");
				playWalkAnimation();
				isWalking=true;
			}
		}
		else { //not changing position
			if(isWalking==true) {	//going from walking to still
				stopWalkAnimation();
				isWalking=false;
			}
		}
		previousPosition=currentPosition;
	}



	public void playWalkAnimation() {
		skeleton.playAnimation("walkAnimation", 0.5f, EndType.LOOP, 0);
	}


	private void stopWalkAnimation() {
		skeleton.stopAnimation();
		
	}
	
	public Vector3 getPos() {
		return (node.getWorldPosition());
	}
	
	public void setPos(Vector3 add) {
		node.translate(add);
		
	}
	
	public void move() {
		node.moveForward(0.08f);
	}
	
}

package myGameEngine;

import hoardPVPGame.MyGame;
import ray.ai.behaviortrees.*;
import ray.rage.asset.texture.Texture;
import ray.rage.asset.texture.TextureManager;
import ray.rage.rendersystem.RenderSystem;
import ray.rage.rendersystem.states.RenderState;
import ray.rage.rendersystem.states.TextureState;
import ray.rage.scene.SceneManager;
import ray.rage.scene.SceneNode;
import ray.rage.scene.SkeletalEntity;

public class NPCController {

	private NPC npc;
	BehaviorTree bt = new BehaviorTree(BTCompositeType.SELECTOR);
	private long lastUpdateTime;
	private Object tickStartTime;
	private long thinkStartTime;
	private long tickStateTime;
	private long lastThinkUpdateTime;
	private Object lastTickUpdateTime;
	private MyGame game;
	
	public NPCController(MyGame game) {
		this.game=game;
		thinkStartTime = System.nanoTime();
		tickStateTime = System.nanoTime();
		lastThinkUpdateTime = thinkStartTime;
		lastTickUpdateTime = tickStartTime;
		addNPC();
		setupBehaviorTree();
	}
	
	public NPC getNPC() {
		return npc;
	}

	private void setupBehaviorTree() {
		// TODO Auto-generated method stub
		
	}
	public void update() {
		npc.update();
	}

	
	public void addNPC() {
		npc=new NPC();
		if(npc!=null) {
			try {
				System.out.println("Drawing NPC");
				SceneManager sm=game.getEngine().getSceneManager();

		        SkeletalEntity skeleton = sm.createSkeletalEntity("npcSkeleton", "knight.rkm", "knight.rks");

		        String skinName;
		        switch(npc.getSkin()) {
				case KNIGHT:
					skinName="knight.png";
					break;
				case BLACK_KNIGHT:
					skinName="black_knight.png";
					break;
				case GOLD_KNIGHT:
					skinName="gold_knight.png";
					break;
				case WHITE_KNIGHT:
					skinName="white_knight.png";
					break;
				default:
					skinName="default.png";
					break;
		        
		        }
				TextureManager tm=sm.getTextureManager();
		        Texture texture=tm.getAssetByPath(skinName);
		    	RenderSystem rs = sm.getRenderSystem();
		    	TextureState state=(TextureState) rs.createRenderState(RenderState.Type.TEXTURE);
		    	state.setTexture(texture);
		    	skeleton.setRenderState(state);
				
				SceneNode ghostN = sm.getRootSceneNode().createChildSceneNode("npcNode");
				ghostN.attachObject(skeleton);
				npc.setPos(game.getDungeon().getLastRoom().getRoomNode().getWorldPosition());
				ghostN.setLocalPosition(npc.getPos());
		        ghostN.scale(.5f, .5f, .5f);
				
				
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
		
	}
	
	private class NearTrap extends BTCondition{

		public NearTrap(boolean toNegate) {
			super(toNegate);
			// TODO Auto-generated constructor stub
		}

		@Override
		protected boolean check() {
			// TODO Auto-generated method stub
			return false;
		}
		
	}
	
	private class Move extends BTAction{

		@Override
		protected BTStatus update(float arg0) {
			// TODO Auto-generated method stub
			return null;
		}
		
		
	}
	
	private class Jump extends BTAction{

		@Override
		protected BTStatus update(float arg0) {
			// TODO Auto-generated method stub
			return null;
		}
		
		
	}
	
}

package myGameEngine;

import java.io.IOException;

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
		try {
			addNPC();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		setupBehaviorTree();
	}
	
	public NPC getNPC() {
		return npc;
	}

	private void setupBehaviorTree() {
		bt.insertAtRoot(new BTSequence(10));
		bt.insertAtRoot(new BTSequence(20));
		bt.insert(10, new NearTrap(false));
		bt.insert(10, new Jump());
		bt.insert(20, new IsInBounds(false));
		bt.insert(20, new Move());
		
	}
	public void update(float elapsedTime) {
		npc.update();
		bt.update(elapsedTime);
	}

	
	public void addNPC() throws IOException {
		npc=new NPC(game.getEngine().getSceneManager());
		npc.setPos(game.getDungeon().getLastRoom().getRoomNode().getWorldPosition());
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
	
	private class IsInBounds extends BTCondition{

		public IsInBounds(boolean toNegate) {
			super(toNegate);
			// TODO Auto-generated constructor stub
		}

		@Override
		protected boolean check() {
			return game.getDungeon().isInBounds(npc.getPos());
		}
		
	}
	
	private class Move extends BTAction{

		@Override
		protected BTStatus update(float arg0) {
			npc.move();
			return BTStatus.BH_SUCCESS;
		}
		
		
	}
	
	private class Jump extends BTAction{

		@Override
		protected BTStatus update(float arg0) {
			return BTStatus.BH_FAILURE;
		}
		
		
	}
	
}

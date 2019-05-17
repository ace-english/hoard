package myGameEngine;

import java.io.IOException;

import hoardPVPGame.GameUtil;
import hoardPVPGame.MyGame;
import ray.ai.behaviortrees.*;

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
		/*
		bt.insertAtRoot(new BTSequence(10));
		bt.insertAtRoot(new BTSequence(20));
		bt.insert(10, new NearTrap(false));
		bt.insert(10, new Jump());
		bt.insert(20, new IsInBounds(false));
		bt.insert(20, new Move());
		*/
		bt.insertAtRoot(new BTSequence(10));
		bt.insertAtRoot(new BTSequence(15));
		bt.insert(15, new IsInBounds(false));
		bt.insert(15, new Move());
		bt.insert(10, new NearTrap(false));
		bt.insert(10, new BTSelector(20));
		bt.insert(20, new BTSequence(30));
		bt.insert(30, new IsSpike(false));
		bt.insert(30, new Jump());
		bt.insert(20, new BTSequence(40));
		bt.insert(40, new IsPit(false));
		bt.insert(40, new Jump());
		bt.insert(20, new BTSequence(50));
		bt.insert(50, new IsSwinging(false));
		bt.insert(50, new Wait());
		
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
			int currentRoom=game.getDungeon().getCurrentRoom(npc.getPos());
			if(game.getDungeon().getRoom(currentRoom).HasTrap())
				return game.getDungeon().getRoom(currentRoom).getTrap().willCollide(npc.getPos());
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
	
	private class IsPit extends BTCondition{

		public IsPit(boolean toNegate) {
			super(toNegate);
			// TODO Auto-generated constructor stub
		}

		@Override
		protected boolean check() {
			int currentRoom=game.getDungeon().getCurrentRoom(npc.getPos());
			boolean ret=game.getDungeon().getRoom(currentRoom).getTrap().getType()==GameUtil.TRAP_TYPE.Pit;
			return ret;
		}
		
	}
	
	private class IsSwinging extends BTCondition{

		public IsSwinging(boolean toNegate) {
			super(toNegate);
			// TODO Auto-generated constructor stub
		}

		@Override
		protected boolean check() {
			int currentRoom=game.getDungeon().getCurrentRoom(npc.getPos());
			boolean ret=game.getDungeon().getRoom(currentRoom).getTrap().getType()==GameUtil.TRAP_TYPE.Swinging;
			return ret;
		}
		
	}
	
	private class IsSpike extends BTCondition{

		public IsSpike(boolean toNegate) {
			super(toNegate);
			// TODO Auto-generated constructor stub
		}

		@Override
		protected boolean check() {
			int currentRoom=game.getDungeon().getCurrentRoom(npc.getPos());
			boolean ret=game.getDungeon().getRoom(currentRoom).getTrap().getType()==GameUtil.TRAP_TYPE.Spike;
			return ret;
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
			npc.jump();
			return BTStatus.BH_SUCCESS;
		}
		
		
	}
	
	private class Wait extends BTAction{

		@Override
		protected BTStatus update(float arg0) {
			System.out.println("Waiting");
			; //not a damn thing
			return BTStatus.BH_SUCCESS;
		}
		
		
	}
	
}

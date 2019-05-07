package myGameEngine;

import ray.ai.behaviortrees.*;

public class NPCController {

	private NPC[] NPClist = new NPC[5];
	int numNPCs=0;
	BehaviorTree bt = new BehaviorTree(BTCompositeType.SELECTOR);
	private long lastUpdateTime;
	private Object tickStartTime;
	private long thinkStartTime;
	private long tickStateTime;
	private long lastThinkUpdateTime;
	private Object lastTickUpdateTime;
	
	public NPCController() {
		thinkStartTime = System.nanoTime();
		tickStateTime = System.nanoTime();
		lastThinkUpdateTime = thinkStartTime;
		lastTickUpdateTime = tickStartTime;
		addNPC();
		setupBehaviorTree();
		//npcLoop();
	}
	
	public int getNumNPCs() {
		return numNPCs;
	}
	
	public NPC getNPC() {
		return NPClist[0];
	}

	private void setupBehaviorTree() {
		// TODO Auto-generated method stub
		
	}
	public void update() {
		NPC npc;
		for(int i=0; i<numNPCs; i++) {
			npc=NPClist[i];
			npc.update();
		}
	}

	/*
	public void npcLoop(){ 
		while (true){ 
			long frameStartTime = System.nanoTime();
			float elapMilSecs = (frameStartTime-lastUpdateTime)/(1000000.0f);
			if (elapMilSecs >= 50.0f){ 
				lastUpdateTime = frameStartTime;
				updateNPCs();
			}
			Thread.yield();
			} 
		}
		*/
	
	public void addNPC() {
		NPClist[numNPCs]=new NPC();
		numNPCs++;
	}
	
	public void updateNPCs(){
		System.out.println("Updating npcs");
		for (int i=0; i<numNPCs; i++){ 
			NPClist[i].update();
		}
	}
	
}

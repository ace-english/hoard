package myGameEngine;

import hoardPVPGame.Dungeon;
import net.java.games.input.Event;
import ray.input.action.AbstractInputAction;
import ray.rage.scene.SceneNode;
import ray.rage.scene.SkeletalEntity.EndType;

public class MoveAction extends AbstractInputAction {

	private Player player;
	private ProtocolClient pc;
	
	enum Direction{
		fwd, back, left, right;
	}

	public MoveAction(Player player) {
		super();
		this.player = player;
		this.pc=player.getProtocolClient();
	}

	@Override
	public void performAction(float arg0, Event arg1) {
		if(player.move(arg0, arg1)) {
		}
			
			
	}

}

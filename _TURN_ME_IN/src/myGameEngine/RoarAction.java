package myGameEngine;

import hoardPVPGame.MyGame;
import net.java.games.input.Event;
import ray.input.action.Action;

public class RoarAction implements Action {
	
	MyGame game;
	

	public RoarAction(MyGame game) {
		super();
		this.game = game;
	}

	@Override
	public void performAction(float arg0, Event arg1) {
		game.playRoar();
	}

}

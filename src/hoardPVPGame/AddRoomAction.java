package hoardPVPGame;

import net.java.games.input.Event;
import ray.input.action.AbstractInputAction;


public class AddRoomAction extends AbstractInputAction {


	private Dungeon dungeon;

	public AddRoomAction(Dungeon dungeon) {
		super();
		this.dungeon=dungeon;
	}
	
	public void performAction(float arg0, Event arg1) {
		dungeon.addRoom();
	}
}

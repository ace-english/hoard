package myGameEngine;

import net.java.games.input.Event;
import ray.input.action.AbstractInputAction;

public class JumpAction extends AbstractInputAction  {

	private Player player;
	private ProtocolClient pc;
	public JumpAction(Player player) {
		super();
		this.player = player;
		this.pc=player.getProtocolClient();
	}

	
	@Override
	public void performAction(float arg0, Event arg1)  {
		System.out.println("PlayerJumped");
		if(!player.getJumped())
		{
			player.setVelocity(1.0f);
			player.setJumped(true);
			player.setJumpHeight();
		}
		//player.updateVerticalPosition();
	}
	
}

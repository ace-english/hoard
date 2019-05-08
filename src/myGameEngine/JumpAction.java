<<<<<<< HEAD
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
=======
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
>>>>>>> cf2f5088a2c8407d7f4d1a9c742d96eac595d9a3

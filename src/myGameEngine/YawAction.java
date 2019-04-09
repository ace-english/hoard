package myGameEngine;

import net.java.games.input.Event;
import ray.input.action.AbstractInputAction;
import ray.rml.*;

public class YawAction extends AbstractInputAction {

	private Player player;
	private ProtocolClient pc;

	public YawAction(Player player) {
		super();
		this.player=player;
		this.pc=player.getProtocolClient();
	}

	@Override
	public void performAction(float arg0, Event arg1) {
		float value=arg1.getValue();
		boolean left=(
				arg1.getComponent().getName().equals("Left")
				||arg1.getValue()<0);
		//sensitivity threshold
		if(value<0.1f&&value>-0.1f)
			return;

		//System.out.println(arg1.getComponent()+" "+arg1.getValue());
		value=Math.abs(value);
		
		Angle angle;
		if(left) 
			value=1f*value;
		else 
			value=-1f*value;
		
		angle=Degreef.createFrom(1f*value);
		
			player.getNode().yaw(angle);
			if(player instanceof OrbitalPlayer)
				((OrbitalPlayer) player).getCameraController().rotate(value);
			
		pc.sendRotateMessage(player.getID(), 'y', angle);
	}

}

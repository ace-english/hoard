package myGameEngine;

import net.java.games.input.Event;
import ray.input.action.AbstractInputAction;
import ray.rage.scene.SceneNode;
import ray.rml.*;

public class PitchAction extends AbstractInputAction {

	private Player player;
	private ProtocolClient pc;

	public PitchAction(FreeMovePlayer freeMovePlayer) {
		super();
		player=freeMovePlayer;
		this.pc=player.getProtocolClient();
	}

	@Override
	public void performAction(float arg0, Event arg1) {
		System.out.println("made it to pitch");
		float value=arg1.getValue();
		boolean up=(
				arg1.getComponent().getName().equals("Up")
				||arg1.getValue()<0);
		//sensitivity threshold
		if(value<0.1f&&value>-0.1f)
			return;
		
		//System.out.println(arg1.getComponent()+" "+arg1.getValue() + " " + up);
		
		value=Math.abs(value);
		
		Angle angle;
		
			SceneNode avatar=player.getNode();
			if(up) 
				angle=Degreef.createFrom(-1f*value);
			else 
				angle=Degreef.createFrom(1f*value);
			avatar.pitch(angle);
			
		//pc.sendRotateMessage(player.getID(), 'x', angle);
	}

}

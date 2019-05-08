package myGameEngine;

import net.java.games.input.Event;
import ray.input.action.AbstractInputAction;
import ray.rml.*;

public class YawAction extends AbstractInputAction {

	private Player player;

	public YawAction(Player player) {
		super();
		this.player=player;
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
		//player.getNode().getLocalRotation().value(0, 0).
		//player.getNode().getLocalRotation().row(0).x();
		//player.getNode().
		float ft = 2;
		float flo[] = {0,1,ft,3,4,5,6,7,8};
		Matrix3f mtx = (Matrix3f) Matrix3f.createFrom(flo);
		//mtx.
		Matrix3f.createFrom(flo);
		/*
		//create matrix string
		String matStr = Float.toString(player.getNode().getLocalRotation().row(0).x()) + "," +
				Float.toString(player.getNode().getLocalRotation().row(2).x()) + "," +
				Float.toString(player.getNode().getLocalRotation().row(0).z()) + "," +
				Float.toString(player.getNode().getLocalRotation().row(2).z());
		float aValue = angle.valueDegrees();
		System.out.println(player.getNode().getLocalRotation());
		System.out.println("My matrix: " + mtx);
		System.out.println("Here is the angle: " + angle);	
		*/
		
	}

}

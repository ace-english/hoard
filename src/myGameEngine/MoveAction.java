package myGameEngine;

import net.java.games.input.Event;
import ray.input.action.AbstractInputAction;
import ray.rage.scene.SceneNode;

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
		Direction dir; float value=arg1.getValue();
		if(arg1.getComponent().getName().equals("W")||
				(arg1.getComponent().getName().equals("Y Axis")&&value<-0.1f))
			dir=Direction.fwd;
		else if(arg1.getComponent().getName().equals("S")||
				(arg1.getComponent().getName().equals("Y Axis")&&value>0.1f))
			dir=Direction.back;
		else if(arg1.getComponent().getName().equals("A")||
				(arg1.getComponent().getName().equals("X Axis")&&value<-0.1f))
			dir=Direction.left;
		else if(arg1.getComponent().getName().equals("D")||
				(arg1.getComponent().getName().equals("X Axis")&&value>0.1f))
			dir=Direction.right;
		else return;
		
		value=Math.abs(value);
		
		SceneNode avatar=player.getNode();
		
			switch(dir) {
			case fwd:
				avatar.moveForward(player.getSpeed()*value);
				break;
			case back:
				avatar.moveBackward(player.getSpeed()*value);
				break;
			case right:
				avatar.moveLeft(player.getSpeed()*value);
				break;
			case left:
				avatar.moveRight(player.getSpeed()*value);
				break;
			}
			
			pc.sendMoveMessage(player.getID(), avatar.getWorldPosition());
			
			
	}

}

package myGameEngine;
// An AbstractInputAction that quits the game.
// It assumes availability of a method �shutdown� in the game
// (this is always true for classes that extend BaseGame).
import ray.input.action.AbstractInputAction;
import ray.rage.game.*;
import hoardPVPGame.MyGame;
import net.java.games.input.Event;

public class QuitGameAction extends AbstractInputAction{

	private MyGame game;
	
	public QuitGameAction(MyGame g){ 
		game = g;
	}
	
	public void performAction(float time, Event event){ 
		System.out.println("shutdown requested");
		game.setState(Game.State.STOPPING);
	}
}
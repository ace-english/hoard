package myGameEngine;

import java.util.LinkedList;

import hoardPVPGame.MyGame;
import net.java.games.input.Controller;
import ray.rage.scene.Node;
//import ray.rage.scene.Node.Controller;
import ray.rage.scene.controllers.*;

public class AscensionController extends AbstractController{

	
	private float ascendRate = 0.03f;
	Node center;
	MyGame game;
	
	public AscensionController(MyGame game, Node center) {
		this.center=center;
		this.game=game;
	}
	
	
	@Override
	protected void updateImpl(float arg0) {
		LinkedList <Node> removeMe = new LinkedList<Node>();
		for(Node node: super.controlledNodesList) {
			//if the node is higher than the orbital center, remove it
			if(node.getWorldPosition().y()<
					center.getWorldPosition().y()) {	
				node.moveUp(ascendRate);
			}
			else {
				removeMe.add(node);
				center.attachChild(node);
				node.setLocalPosition(node.getLocalPosition().x(), 0, node.getLocalPosition().z());
				
				
			}
			
		}
		for(Node node:removeMe)
			this.removeNode(node);

	}

}

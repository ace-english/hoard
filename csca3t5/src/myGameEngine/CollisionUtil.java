
package myGameEngine;

import ray.rage.scene.Camera;
import ray.rage.scene.Node;
import ray.rage.scene.SceneNode;
import ray.rml.Vector3;

public class CollisionUtil {

	public static float getDistance(Vector3 a, Vector3 b) {
		
		float dx, dy, dz;
		dx=a.x()-b.x();
		//dy=a.y()-b.y();
		dz=a.z()-b.z();
		
		return (float) Math.sqrt(dx*dx+/*dy*dy+*/dz*dz);
		
		
	}
	
	public static boolean isColliding(Node planet2, Node dest) {
		boolean collision=false;
		float threshold=1.0f+dest.getWorldScale().x()*2.0f;
		if(getDistance(planet2.getWorldPosition(), dest.getWorldPosition())<threshold) {
			System.out.println("Collision detected between "+planet2.getName()+" and "+dest.getName());
			collision=true;
		}
		return collision;
	}
	
}

package simulation.physicalobjects;

import java.awt.Color;

import simulation.Simulator;
import simulation.environment.Environment;
import simulation.physicalobjects.collisionhandling.knotsandbolts.CircularShape;

public class Nest extends PhysicalObject {
	
	private Color color;
	private boolean anyPreyAllowed = true;
	private String PreyType;
	
	public Nest(Simulator simulator, String name, double x, double y, double radius) {
		super(simulator, name, x, y, 0, 0, PhysicalObjectType.NEST);
		//keep circular for representation
		this.shape = new CircularShape(simulator, name + "CollisionObject", this, 0, 0, 0, 2 * radius, radius);
		color = Color.LIGHT_GRAY;
	}

	public Nest(Simulator simulator, String name, double x, double y, double z, double radius) {
		super(simulator, name, x, y, z, 0, 0, 0, 0, PhysicalObjectType.NEST);
		//keep circular for representation
		this.shape = new CircularShape(simulator, name + "CollisionObject", this, 0, 0, 0, 2 * radius, radius);
		color = Color.LIGHT_GRAY;
	}
	
	public String getPreytype() {
		return PreyType;
	}
	
	public boolean getPreyAllowance() {
		return anyPreyAllowed;
	}
	
	public void setPreyAllowed(boolean state, String type) {
		anyPreyAllowed = state;
		PreyType = type;
	}	
	
	public Color getColor() {
		return color;
	}
	
	public void setColor(Color color) {
		this.color = color;
	}
	
}
package simulation.physicalobjects;

import mathutils.Vector3d;
import simulation.Simulator;
import simulation.environment.Environment;
import simulation.physicalobjects.collisionhandling.knotsandbolts.Shape;
import simulation.util.Arguments;

public class MovableObject3D extends PhysicalObject3D {
	private static final double NUMBER_OF_CYCLES_PER_SECOND = 10;
	public static final double  MAXIMUMSPEED      = 1000;
	public static final double  TWICEMAXIMUMSPEEDPERTIMESTEP = 2.0 * MAXIMUMSPEED / NUMBER_OF_CYCLES_PER_SECOND;
	protected Environment env;
	protected Vector3d previousPosition;
	
	public MovableObject3D(Simulator simulator, Arguments args) {
		super(simulator, args);
		this.env = simulator.getEnvironment();
		this.previousPosition = position;
	}
	
	public MovableObject3D(Simulator simulator, String name, double x, double y,double z, double orientation, double mass, PhysicalObjectType type) {
		super(simulator, name, x, y, z, orientation, mass, type);
		this.env = simulator.getEnvironment();
	}
	
	public MovableObject3D(Simulator simulator, String name, double x, double y,double z, double orientation, double mass, PhysicalObjectType type, Shape shape) {
		super(simulator, name, x, y, z, orientation, mass, type, shape);
		this.env = simulator.getEnvironment();
	}

	public void teleportTo(Vector3d position){
		setPosition(position);
		env.addTeleported(this);
	}
	
	public void move(Vector3d relativePosition) {
		position.x += relativePosition.x;
		position.y += relativePosition.y;
		position.z += relativePosition.z;	
	}
		
	public void moveTo(Vector3d position) {
		this.position.set(position);
		this.previousPosition = position;
	}
	
	public Vector3d getPreviousPosition() {
		return previousPosition;
	}
}

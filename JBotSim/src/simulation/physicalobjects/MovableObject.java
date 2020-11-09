package simulation.physicalobjects;

import mathutils.VectorLine;
import simulation.Simulator;
import simulation.environment.Environment;
import simulation.physicalobjects.collisionhandling.knotsandbolts.Shape;
import simulation.util.Arguments;

public class MovableObject extends PhysicalObject {
	private static final double NUMBER_OF_CYCLES_PER_SECOND = 10;
	public static final double  MAXIMUMSPEED      = 1000;
	public static final double  TWICEMAXIMUMSPEEDPERTIMESTEP = 2.0 * MAXIMUMSPEED / NUMBER_OF_CYCLES_PER_SECOND;
	protected Environment env;
	protected VectorLine previousPosition;
	
	public MovableObject(Simulator simulator, Arguments args) {
		super(simulator, args);
		this.env = simulator.getEnvironment();
		this.previousPosition = position;
	}

	public MovableObject(Simulator simulator, String name, double x, double y, double orientationZ, double mass, PhysicalObjectType type) {
		super(simulator, name, x, y, 0, 0, 0, orientationZ, mass, type);
		this.env = simulator.getEnvironment();
	}
	
	public MovableObject(Simulator simulator, String name, double x, double y, double orientationZ, double mass, PhysicalObjectType type, Shape shape) {
		super(simulator, name, x, y, 0, 0, 0, orientationZ, mass, type, shape);
		this.env = simulator.getEnvironment();
	}
	
	public MovableObject(Simulator simulator, String name, double x, double y,double z, double orientationX, double orientationY, double orientationZ, double mass, PhysicalObjectType type) {
		super(simulator, name, x, y, z, orientationX, orientationY, orientationZ, mass, type);
		this.env = simulator.getEnvironment();
	}
	
	public MovableObject(Simulator simulator, String name, double x, double y, double z, double orientationX, double orientationY, double orientationZ, double mass, PhysicalObjectType type, Shape shape) {
		super(simulator, name, x, y, z, orientationX, orientationY, orientationZ, mass, type, shape);
		this.env = simulator.getEnvironment();
	}

	public void teleportTo(VectorLine position){
		setPosition(position);
		env.addTeleported(this);
	}
	
	public void move(VectorLine relativePosition) {
		position.x += relativePosition.x;
		position.y += relativePosition.y;
		position.z += relativePosition.z;
	}
		
	public void moveTo(VectorLine position) {
		this.position.set(position);
		this.previousPosition = position;
	}
	
	public VectorLine getPreviousPosition() {
		return previousPosition;
	}
}

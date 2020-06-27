package simulation.physicalobjects;

import mathutils.Vector2d;
import mathutils.Vector3d;
import simulation.Simulator;
import simulation.physicalobjects.collisionhandling.knotsandbolts.Shape;
import simulation.util.Arguments;

public class PhysicalObject3D extends PhysicalObject {
	
	protected Vector3d position = new Vector3d();
	protected double mass;
	protected double orientation;
	private int id;
	private boolean involvedInCollison = false;
	private boolean involvedInCollisonWall = false;
	private PhysicalObjectType type;
	public Shape shape = null;
	private boolean invisible = false;

	private boolean enabled = true;
	protected Vector3d lightDirection = new Vector3d();
	
	public PhysicalObject3D(Simulator simulator, Arguments args) {	
		super(simulator,args);
		double x = args.getArgumentAsDouble("x");
		double y = args.getArgumentAsDouble("y");
		double z = args.getArgumentAsDouble("z");
		this.position.set(x,y,z);
		
		orientation = Math.toRadians(args.getArgumentAsDouble("orientation"));
		mass = args.getArgumentAsDouble("mass");
		type = PhysicalObjectType.valueOf(args.getArgumentAsStringOrSetDefault("type","ROBOT").toUpperCase());
		
		this.id = simulator.getAndIncrementNumberPhysicalObjects(type);
	}
}

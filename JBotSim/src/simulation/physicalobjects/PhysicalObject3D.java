package simulation.physicalobjects;

import mathutils.Vector2d;
import mathutils.Vector3d;
import simulation.Simulator;
import simulation.physicalobjects.collisionhandling.knotsandbolts.CircularShape;
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
	
	public PhysicalObject3D(Simulator simulator, String name, double x, double y, double z, double orientation, double mass, PhysicalObjectType type) {
		super(simulator,name,x, y,orientation,mass,type);
		position.set(x, y, z);

		this.orientation = orientation;
		this.mass = mass;
		this.type = type;

		this.id = simulator.getAndIncrementNumberPhysicalObjects(type);
	}
	
	public PhysicalObject3D(Simulator simulator, String name, double x, double y,double z, double orientation, double mass, PhysicalObjectType type, Shape shape) {
		this(simulator, name, x, y, z, orientation, mass, type);
		this.shape = shape;
	}
	
	
	public Vector3d get3DPosition() {
		return position;
	}

	public double getOrientation() {
		return orientation;
	}

	public void setPosition(double x, double y, double z) {
		position.set(x, y, z);
	}

	public void setPosition(Vector3d vNewPos) {
		position.set(vNewPos);
	}

	public void setOrientation(double orientation) {
		this.orientation = orientation;
	}

	public boolean hasParent() {
		return false;
	}

	public PhysicalObject getParent() {
		return null;
	}
	
	public void setMass(double f_mass) {
		mass = f_mass;
	}

	public double getMass() {
		return mass;
	}

	public int getId() {
		return id;
	}

	public PhysicalObjectType getType() {
		return type;
	}

	@Override
	public int compareTo(PhysicalObject o) {
		return o.getId() - id;
	}

	@Override
	public String toString() {
		return "id=" + id + ", type=" + type + "]";
	}

	public double getRadius() {
		return ((CircularShape) shape).getRadius();
	}

	public double getDiameter() {
		return 2 * getRadius();
	}

	public boolean isInvolvedInCollison() {
		return involvedInCollison;
	}

	public boolean isInvolvedInCollisonWall() {
		return involvedInCollisonWall;
	}
	
	public void setInvolvedInCollison(boolean involvedInCollison) {
		this.involvedInCollison = involvedInCollison;
	}
	
	public void setInvolvedInCollisonWall(boolean involvedInCollisonWall) {
		this.involvedInCollisonWall = involvedInCollisonWall;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public double getDistanceBetween(Vector3d fromPoint) {
		lightDirection.set(position.getX()-fromPoint.getX(),position.getY()-fromPoint.getY(),position.getZ()-fromPoint.getZ());
		return lightDirection.length();
	}
	
	public void setInvisible(boolean invisible) {
		this.invisible = invisible;
	}
	
	public boolean isInvisible() {
		return invisible;
	}
}

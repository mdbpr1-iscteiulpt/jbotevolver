package simulation.physicalobjects;

import mathutils.VectorLine;
import simulation.Simulator;
import simulation.SimulatorObject;
import simulation.physicalobjects.collisionhandling.knotsandbolts.Shape;
import simulation.physicalobjects.collisionhandling.knotsandbolts.CircularShape;
import simulation.util.Arguments;
import simulation.util.ArgumentsAnnotation;

public class PhysicalObject extends SimulatorObject implements
		Comparable<PhysicalObject> {
	
	@ArgumentsAnnotation(name = "is3dimensional", defaultValue = "false")
	private boolean is3dimensional;
	
	protected VectorLine position = new VectorLine();
	protected double mass;
	protected double orientationX;
	protected double orientationY;
	protected double orientationZ;
	private int id;
	private boolean involvedInCollison = false;
	private boolean involvedInCollisonWall = false;
	private PhysicalObjectType type;
	public Shape shape = null;
	private boolean invisible = false;

	private boolean enabled = true;
	protected VectorLine lightDirection = new VectorLine();

	public PhysicalObject(Simulator simulator, Arguments args) {
		super(args.getArgumentAsStringOrSetDefault("name", "defaultName"));
		double x = args.getArgumentAsDouble("x");
		double y = args.getArgumentAsDouble("y");
		this.position.set(x,y);
		orientationX = Math.toRadians(args.getArgumentAsDouble("orientationX"));
		orientationY = Math.toRadians(args.getArgumentAsDouble("orientationY"));		
		orientationZ = Math.toRadians(args.getArgumentAsDouble("orientationZ"));
		mass = args.getArgumentAsDouble("mass");
		type = PhysicalObjectType.valueOf(args.getArgumentAsStringOrSetDefault("type","ROBOT").toUpperCase());
		
		this.id = simulator.getAndIncrementNumberPhysicalObjects(type);
	}
	
	public PhysicalObject(Simulator simulator, String name, double x, double y, double orientation, double mass, PhysicalObjectType type) {
		super(name);
		position.set(x, y);

		this.orientationZ = orientation;
		this.mass = mass;
		this.type = type;

		this.id = simulator.getAndIncrementNumberPhysicalObjects(type);
	}
	
	public PhysicalObject(Simulator simulator, String name, double x, double y, double z, double orientationX, double orientationY, double orientationZ, double mass, PhysicalObjectType type) {
		super(name);
		position.set(x, y, z);
		this.orientationX = orientationX;
		this.orientationY = orientationY;
		this.orientationZ = orientationZ;
		this.mass = mass;
		this.type = type;

		this.id = simulator.getAndIncrementNumberPhysicalObjects(type);
	}
	
	public PhysicalObject(Simulator simulator, String name, double x, double y, double orientation, double mass, PhysicalObjectType type, Shape shape) {
		this(simulator, name, x, y, orientation, mass, type);
		this.shape = shape;
	}

	public PhysicalObject(Simulator simulator, String name, double x, double y,double z, double orientationX, double orientationY, double orientationZ, double mass, PhysicalObjectType type, Shape shape) {
		this(simulator, name, x, y,z, orientationX,orientationY,orientationZ, mass, type);
		this.shape = shape;
	}
	
	public VectorLine getPosition() {
		return position;
	}

	public double getOrientation() {
		return orientationZ;
	}

	public void setPosition(double x, double y) {
		position.set(x, y);
	}

	public void setPosition(VectorLine vNewPos) {
		position.set(vNewPos);
	}

	//2D orientation
	public void setOrientation(double orientation) {
		this.orientationZ = orientation;
	}

	//3D orientation
	public void setOrientationX(double orientationX) {
		this.orientationX = orientationX;
	}

	public void setOrientationY(double orientationY) {
		this.orientationY = orientationY;
	}
	
	public void setOrientationZ(double orientationZ) {
		this.orientationZ = orientationZ;
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

	// @Override
	public int compareTo(PhysicalObject o) {
		return o.id - id;
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

	public double getDistanceBetween(VectorLine fromPoint) {
		lightDirection.set(position.getX()-fromPoint.getX(),position.getY()-fromPoint.getY());
		return lightDirection.length();
	}
	
	public void setInvisible(boolean invisible) {
		this.invisible = invisible;
	}
	
	public boolean isInvisible() {
		return invisible;
	}
	
}
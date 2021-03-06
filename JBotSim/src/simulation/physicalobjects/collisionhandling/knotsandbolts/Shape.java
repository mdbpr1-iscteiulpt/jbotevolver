package simulation.physicalobjects.collisionhandling.knotsandbolts;

import java.awt.geom.Area;
import java.io.Serializable;
import java.util.ArrayList;

import mathutils.Point;
import mathutils.VectorLine;
import simulation.Simulator;
import simulation.physicalobjects.ClosePhysicalObjects;
import simulation.physicalobjects.PhysicalObject;
import simulation.physicalobjects.PhysicalObjectType;
import simulation.physicalobjects.checkers.AllowAllRobotsChecker;
import simulation.physicalobjects.checkers.AllowOrderedLightChecker;
import simulation.physicalobjects.checkers.AllowOrderedPreyChecker;
import simulation.physicalobjects.checkers.AllowOrderedRobotsChecker;
import simulation.physicalobjects.checkers.AllowWallChecker;


public abstract class Shape implements Serializable {
	public static final int COLLISION_OBJECT_TYPE_COMPOUND   = 0;
	public static final int COLLISION_OBJECT_TYPE_RECTANGLE  = 1;
	public static final int COLLISION_OBJECT_TYPE_CIRCLE     = 2;
	public static final int COLLISION_OBJECT_TYPE_POLYGON  = 3;
	public static final int COLLISION_OBJECT_TYPE_SPHERE  = 4;
	public static final int COLLISION_OBJECT_TYPE_RECTANGLE3D  = 4;
	
	
	protected boolean   			enabled;
	protected PhysicalObject        parent;

	protected VectorLine          	relativePosition;
	protected VectorLine          	position = new VectorLine();
	protected double               orientationX = 0;
	protected double               orientationY = 0;
	protected double               orientationZ = 0;
	protected AxisAlignedBoundingBox 	aabb;

	private ArrayList<Shape> collidedWith;

	//TODO::CHECK the next line... check id (-1)
	protected ClosePhysicalObjects closeRobots;
	protected ClosePhysicalObjects closePrey;
	protected ClosePhysicalObjects closeLightPoles;
	protected ClosePhysicalObjects closeWalls;//, closeHoles;
	
	public Shape(Simulator simulator, String name, PhysicalObject parent, 
			double relativePosX, double relativePosY, double relativePosZ, double range) {  
		this.enabled 	 = true; 
		this.parent  	 = parent;
		this.aabb    	 = new AxisAlignedBoundingBox(parent == null ? relativePosX : parent.getPosition().getX(),  
					parent == null ? relativePosY : parent.getPosition().getY());
		collidedWith 	 = new ArrayList<Shape>(10);
		relativePosition = new VectorLine(relativePosX, relativePosY);
		
		closeRobots 	 = new ClosePhysicalObjects(simulator.getEnvironment(), range,new AllowAllRobotsChecker(parent.getId()));
		closePrey   	 = new ClosePhysicalObjects(simulator.getEnvironment(), range,new AllowOrderedPreyChecker(parent.getId()));
		closeLightPoles  = new ClosePhysicalObjects(simulator.getEnvironment(), range,new AllowOrderedLightChecker(parent.getId()));
		closeWalls = new ClosePhysicalObjects(simulator.getEnvironment(), range, new AllowWallChecker());
		//closeHoles = new ClosePhysicalObjects(simulator, range, new AllowHoleChecker());
		
		if (parent != null) {
			setPosition(relativePosition.getX() + parent.getPosition().getX(), 
					relativePosition.getY() + parent.getPosition().getY(),relativePosition.getZ() + parent.getPosition().getZ());
			setOrientation(parent.getOrientationX(),parent.getOrientationY(),parent.getOrientationZ());
		}
	}

	public VectorLine getPosition() {
		return position;
	}

	public double getOrientation() {
		return orientationZ;
	}
	
	public double getOrientationZ() {
		return orientationZ;
	}

	public double getOrientationY() {
		return orientationY;
	}
	
	public double getOrientationX() {
		return orientationX;
	}
	
	public void setPosition(double x, double y) {
		position.set(x, y);
	}
	
	public void setPosition(double x, double y, double z) {
		position.set(x, y, z);
	}

	public void setPosition(VectorLine vNewPos) {
		position.set(vNewPos);
	}

	public void setOrientation(double orientation) {
		this.orientationZ = orientation;
	}
	
	//3D Use
	public void setOrientation(double orientationX,double orientationY,double orientationZ) {
		this.orientationX = orientationX;
		this.orientationY = orientationY;
		this.orientationZ = orientationZ;
	}
	
	public void enable() {
		enabled = true;
	}

	public void disable() {
		enabled = false;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setRelativePosition(double x, double y) {
		relativePosition.set(x,y);
	}

	public void setRelativePosition(VectorLine position) {
		relativePosition = position;
	}

	public void getRelativePosition(Point position) {
		position.set(relativePosition);
	}

	public VectorLine getRelativePosition() {
		return relativePosition;
	}

	public AxisAlignedBoundingBox getAABB() {
		return aabb;
	}

	public PhysicalObject getFounderParent(){
		PhysicalObject founderParent = parent;
		while(founderParent.hasParent()) {
			founderParent = founderParent.getParent();
		}
		return founderParent; 
	}

	public void setParent(PhysicalObject parent) {
		this.parent = parent;
	}

	public boolean hasParent() {
		return parent != null;
	}

	public PhysicalObject getParent() {
		return parent;
	}

	public abstract int getCollisionObjectType();
	public abstract void computeNewPositionAndOrientationFromParent();                               

	
	public void setCollidedWith(Shape collidedWith) {
		this.collidedWith.add(collidedWith);
	}

	public ArrayList<Shape> getCollidedWith() {
		return collidedWith;
	}

	public void clearCollidedWith(){
		collidedWith.clear();
	}

	public ClosePhysicalObjects getCloseRobot() {
		return closeRobots;
	}

	public ClosePhysicalObjects getClosePrey() {
		return closePrey;
	}
	
	public ClosePhysicalObjects getCloseLightPole(){
		return closeLightPoles;
	}
	
	public ClosePhysicalObjects getCloseWalls(){
		return this.closeWalls;
	}
	
	/*public ClosePhysicalObjects getCloseHoles() {
		return this.closeHoles;
	}*/

}

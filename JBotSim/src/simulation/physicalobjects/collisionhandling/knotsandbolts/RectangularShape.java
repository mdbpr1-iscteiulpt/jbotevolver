package simulation.physicalobjects.collisionhandling.knotsandbolts;

import java.awt.geom.Area;

import mathutils.MathUtils;
import mathutils.VectorLine;
import simulation.Simulator;
import simulation.environment.Environment;
import simulation.physicalobjects.PhysicalObject;



/*******************************************************************************

The corners of the collision object are organized in the following way 
when rotation is 0:
                          	 ^
                          	 |
          X1,Y1,Z1...........|..........X2,Y1,Z1
           .               	 |              .
           .               	 |              .
           .--------------------------------.
           .              	 |              .
           .              	 |              .  
          X1,Y2,Z1...........|..........X2,Y2,Z1
                          	 |


Not the most intuitive way, but it works and you are welcome to change it 
(changes are local).

 *******************************************************************************/

//Can only be done in a Rotation X,Y = 0
public class RectangularShape extends Shape {
	private VectorLine cornerX1Y1Z1 = new VectorLine();
	private VectorLine cornerX2Y1Z1 = new VectorLine();
	private VectorLine cornerX2Y2Z1 = new VectorLine();
	private VectorLine cornerX1Y2Z1 = new VectorLine();
	private VectorLine cornerX1Y1Z2 = new VectorLine();
	private VectorLine cornerX2Y1Z2 = new VectorLine();
	private VectorLine cornerX2Y2Z2 = new VectorLine();
	private VectorLine cornerX1Y2Z2 = new VectorLine();
	
	private boolean is3D;
	private double halfSizeX;
	private double halfSizeY;
	private double halfSizeZ;

	private double relativeRotationX;
	private double relativeRotationY;
	private double relativeRotationZ;


	public RectangularShape(Simulator simulator, String name, PhysicalObject parent,
			double relativePosX, double relativePosY, double range,
			double relativeRotation, double sizeX, double sizeY) {
		super (simulator, name, parent, relativePosX, relativePosY, 0, range);
		this.relativeRotationZ = relativeRotation;
		this.halfSizeX = sizeX / 2;
		this.halfSizeY = sizeY / 2;
		is3D = false;
		/*if (parent != null){
			computeNewPositionAndOrientationFromParent();
		}
		else {*/
			cornerX1Y1Z1.set( relativePosX - halfSizeX, relativePosY + halfSizeY);

			cornerX2Y1Z1.set( relativePosX + halfSizeX, relativePosY + halfSizeY);

			cornerX2Y2Z1.set( relativePosX + halfSizeX, relativePosY - halfSizeY);

			cornerX1Y2Z1.set( relativePosX - halfSizeX, relativePosY - halfSizeY);
			aabb.reset(relativePosX, relativePosY, sizeX, sizeY);
			//System.out.println(super.aabb.toString());
			
			setPosition(relativePosX, relativePosY);
			setOrientation(relativeRotation);
		//}
	}
	
	public RectangularShape(Simulator simulator, String name, PhysicalObject parent,
			double relativePosX, double relativePosY, double relativePosZ, double range,
			double relativeRotationX, double relativeRotationY, double relativeRotationZ, double sizeX, double sizeY, double sizeZ) {
		super (simulator, name, parent, relativePosX, relativePosY, relativePosZ, range);
		this.relativeRotationX = relativeRotationX;
		this.relativeRotationY = relativeRotationY;
		this.relativeRotationZ = relativeRotationZ;
		this.halfSizeX = sizeX / 2;
		this.halfSizeY = sizeY / 2;
		this.halfSizeZ = sizeZ / 2;
		is3D = true;
		
			cornerX1Y1Z1.set( relativePosX - halfSizeX, relativePosY + halfSizeY, relativePosZ + halfSizeZ);

			cornerX2Y1Z1.set( relativePosX + halfSizeX, relativePosY + halfSizeY, relativePosZ + halfSizeZ);

			cornerX2Y2Z1.set( relativePosX + halfSizeX, relativePosY - halfSizeY, relativePosZ + halfSizeZ);

			cornerX1Y2Z1.set( relativePosX - halfSizeX, relativePosY - halfSizeY, relativePosZ + halfSizeZ);
			aabb.reset(relativePosX, relativePosY, sizeX, sizeY);

			cornerX1Y1Z2.set( relativePosX - halfSizeX, relativePosY + halfSizeY, relativePosZ - halfSizeZ);

			cornerX2Y1Z2.set( relativePosX + halfSizeX, relativePosY + halfSizeY, relativePosZ - halfSiwsazeZ);

			cornerX2Y2Z2.set( relativePosX + halfSizeX, relativePosY - halfSizeY, relativePosZ - halfSizeZ);

			cornerX1Y2Z2.set( relativePosX - halfSizeX, relativePosY - halfSizeY, relativePosZ - halfSizeZ);
			aabb.reset(relativePosX, relativePosY, relativePosZ, sizeX, sizeY, sizeZ);
			//System.out.println(super.aabb.toString());
			
			setPosition(relativePosX, relativePosY, relativePosZ);
			setOrientation(relativeRotationX,relativeRotationY,relativeRotationZ);
		//}
	}

	public void computeNewPositionAndOrientationFromParent() {                               
		// First we rotate the rectangle and afterwards we move it:
		double fRotationX = relativeRotationX + parent.getOrientationX();    
		double fRotationY = relativeRotationY + parent.getOrientationY();    
		double fRotationZ = relativeRotationZ + parent.getOrientationZ();    
		fRotationZ = MathUtils.normalizeAngle(fRotationZ);
		setOrientation(fRotationZ);www
		System.out.println("PARENT: " + parent.getPosition());

		// Based on the rotation of two points we can find the other two: 
		cornerX1Y1Z1.set(-halfSizeX, -halfSizeY, -halfSizeZ);

		cornerX2Y1Z1.set(halfSizeX, -halfSizeY, -halfSizeZ);

		cornerX1Y1Z1.rotate(fRotationZ);
		cornerX2Y1Z1.rotate(fRotationZ);

		cornerX2Y2Z1.set( -cornerX1Y1Z1.getX(),-cornerX1Y1Z1.getY(), -halfSizeZ);

		cornerX1Y2Z1.set( -cornerX2Y1Z1.getX(), -cornerX2Y1Z1.getY(), -halfSizeZ);
		
		//Same is done with the other corners
		cornerX1Y1Z2.set(-halfSizeX, -halfSizeY, halfSizeZ);

		cornerX2Y1Z2.set(halfSizeX, -halfSizeY, halfSizeZ);

		cornerX1Y1Z2.rotate(fRotationZ);
		cornerX2Y1Z2.rotate(fRotationZ);

		cornerX2Y2Z2.set( -cornerX1Y1Z1.getX(),-cornerX1Y1Z1.getY(), halfSizeZ);

		cornerX1Y2Z2.set( -cornerX2Y1Z1.getX(), -cornerX2Y1Z1.getY(), halfSizeZ);
		// Calculate the size of the AABB:
		double fAABBSizeX = Math.abs(cornerX2Y2Z1.getX()) > Math.abs(cornerX2Y1Z1.getX()) ? 
				Math.abs(cornerX2Y2Z1.getX()) * 2 : Math.abs(cornerX2Y1Z1.getX()) * 2;
		double fAABBSizeY = Math.abs(cornerX2Y2Z1.getY()) > Math.abs(cornerX2Y1Z1.getY()) ? 
				Math.abs(cornerX2Y2Z1.getY()) * 2 : Math.abs(cornerX2Y1Z1.getY()) * 2;
		//Got to check here!!!!!!
		double fAABBSizeZ = Math.abs(cornerX2Y2Z1.getZ()) > Math.abs(cornerX2Y2Z2.getZ()) ? 
				Math.abs(cornerX2Y2Z1.getZ()) * 2 : Math.abs(cornerX2Y2Z2.getZ()) * 2;	
			
		// Now move the rectangle:
		VectorLine vNewPos = relativePosition;
		vNewPos.rotate(parent.getOrientation());

		vNewPos.add(parent.getPosition());
		setPosition(vNewPos);

		cornerX1Y1Z1.add(vNewPos);
		cornerX2Y1Z1.add(vNewPos);
		cornerX2Y2Z1.add(vNewPos);
		cornerX1Y2Z1.add(vNewPos);
		cornerX1Y1Z2.add(vNewPos);
		cornerX2Y1Z2.add(vNewPos);
		cornerX2Y2Z2.add(vNewPos);
		cornerX1Y2Z2.add(vNewPos);
		
		aabb.reset(vNewPos.getX(), vNewPos.getY(),vNewPos.getZ(), fAABBSizeX, fAABBSizeY, fAABBSizeZ);
	}

	public int getCollisionObjectType()
	{
		return COLLISION_OBJECT_TYPE_RECTANGLE;
	}

	public double getHalfSizeX()
	{
		return halfSizeX;
	}

	public double getHalfSizeY()
	{
		return halfSizeY;
	}
	
	public double getHalfSizeZ()
	{
		return halfSizeZ;
	}
	public boolean checkCollisionWithRectangle(RectangularShape pc_rectangle)
	{
		if (checkHalfCollisionWithRectangle(pc_rectangle))
			return true;
		else
			return pc_rectangle.checkHalfCollisionWithRectangle(this);
	}


	private boolean checkCorner(VectorLine v) {
		return (v.getX() <= halfSizeX && v.getX() >= -halfSizeX && v.getY() <= halfSizeY && v.getY() >= -halfSizeY && v.getZ() <= halfSizeZ && v.getZ() >= -halfSizeZ);    
	}

	// We check if pc_rectangle has a corner within "this" rectangle:
	private boolean checkHalfCollisionWithRectangle(RectangularShape pc_rectangle)
	{
		VectorLine cornerX1Y1Z1 = pc_rectangle.cornerX1Y1Z1;
		VectorLine cornerX2Y1Z1 = pc_rectangle.cornerX2Y1Z1;
		VectorLine cornerX2Y2Z1 = pc_rectangle.cornerX2Y2Z1;
		VectorLine cornerX1Y2Z1 = pc_rectangle.cornerX1Y2Z1;      
		VectorLine cornerX1Y1Z2 = pc_rectangle.cornerX1Y1Z2;
		VectorLine cornerX2Y1Z2 = pc_rectangle.cornerX2Y1Z2;
		VectorLine cornerX2Y2Z2 = pc_rectangle.cornerX2Y2Z2;
		VectorLine cornerX1Y2Z2 = pc_rectangle.cornerX1Y2Z2; 
		
		VectorLine myPosition = getPosition();

		cornerX1Y1Z1.sub(myPosition);
		cornerX1Y2Z1.sub(myPosition);
		cornerX2Y2Z1.sub(myPosition);
		cornerX2Y1Z1.sub(myPosition);
		cornerX1Y1Z2.sub(myPosition);
		cornerX1Y2Z2.sub(myPosition);
		cornerX2Y2Z2.sub(myPosition);
		cornerX2Y1Z2.sub(myPosition);
		
		cornerX1Y1Z1.negate();
		cornerX1Y2Z1.negate();
		cornerX2Y2Z1.negate();
		cornerX2Y1Z1.negate();
		cornerX1Y1Z2.negate();
		cornerX1Y2Z2.negate();
		cornerX2Y2Z2.negate();
		cornerX2Y1Z2.negate();

		double fMyRotX = getOrientationX();
		double fMyRotY = getOrientationY();
		double fMyRotZ = getOrientationZ();
		
		cornerX1Y1Z1.rotate(-fMyRotX,-fMyRotY,-fMyRotZ);
		cornerX1Y2Z1.rotate(-fMyRotX,-fMyRotY,-fMyRotZ);
		cornerX2Y2Z1.rotate(-fMyRotX,-fMyRotY,-fMyRotZ);
		cornerX2Y1Z1.rotate(-fMyRotX,-fMyRotY,-fMyRotZ);
		cornerX1Y1Z2.rotate(-fMyRotX,-fMyRotY,-fMyRotZ);
		cornerX1Y2Z2.rotate(-fMyRotX,-fMyRotY,-fMyRotZ);
		cornerX2Y2Z2.rotate(-fMyRotX,-fMyRotY,-fMyRotZ);
		cornerX2Y1Z2.rotate(-fMyRotX,-fMyRotY,-fMyRotZ);
		
		return checkCorner(cornerX1Y1Z1) ||
		checkCorner(cornerX1Y2Z1) || 
		checkCorner(cornerX2Y2Z1) || 
		checkCorner(cornerX2Y1Z1) ||
		checkCorner(cornerX1Y1Z2) ||
		checkCorner(cornerX1Y2Z2) || 
		checkCorner(cornerX2Y2Z2) || 
		checkCorner(cornerX2Y1Z2);
	}


	public void setSize(double sizeX, double sizeY)
	{
		halfSizeX = sizeX / 2;
		halfSizeY = sizeY / 2;

		computeNewPositionAndOrientationFromParent();
	}
	
	public void setSize(double sizeX, double sizeY, double sizeZ)
	{
		halfSizeX = sizeX / 2;
		halfSizeY = sizeY / 2;
		halfSizeZ = sizeZ / 2;
		computeNewPositionAndOrientationFromParent();
	}
	
}

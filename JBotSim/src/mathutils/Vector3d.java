package mathutils;

import java.io.Serializable;

import net.jafama.FastMath;

public class Vector3d extends Point3d implements Serializable {

	private double ax;
	private double ay;
	private double az;
	private double angleX;
	private double angleY;
	private double angleZ;

	public Vector3d(double x, double y,double z) {
		super(x,y,z);
	}

	public Vector3d() {
		super();
	}

	public Vector3d(mathutils.Vector3d v) {
		super(v.x, v.y,v.z);
	}

	/**  
	 * Returns the length of this vector.
	 * @return the length of this vector
	 */  
	public final double length()
	{
		return (double) FastMath.sqrtQuick(this.x*this.x + this.y*this.y + this.z*this.z);
	}


	/**  
	 * Returns the squared length of this vector.
	 * @return the squared length of this vector
	 */  
	public final double lengthSquared()
	{
		return (this.x*this.x + this.y*this.y + this.z*this.z);
	}

	
	public void rotateX(double angle) {
		angleX += angle;
	}

	public void rotateY(double angle) {
		angleY += angle;
	}
	
	public void rotateZ(double angle) {
		angleZ += angle;
	}
	
	
	
	//public void rotateX(double angle) {
	//	double xTemp = x * FastMath.cosQuick(angleX) -  y * FastMath.sinQuick(angleX);
	//	y = x * FastMath.sinQuick(angleX) + y * FastMath.cosQuick(angleX);
	//	x = xTemp;
	//		if(Double.isNaN(x)||Double.isNaN(y))
	//			System.out.println("ERROR");
	//}


	/*// Make vec perpendicular to itself
	public void dVec2Perpendicular()
	{ 
		double tempx = x;
		x  = -y;  
		y  = tempx; 
//		if(Double.isNaN(x)||Double.isNaN(y))
//			System.out.println("ERROR");
	}*/
	
	public void sub(Vector3d B) {
		x -= B.x;
		y -= B.y;
		y -= B.z;
//		if(Double.isNaN(x)||Double.isNaN(y))
//			System.out.println("ERROR");
	}


	// Returns the vector going _from_ A to B (thus B - A)
	public void subFrom(Vector3d B)   
	{                               
		x = B.x - x;       
		y = B.y - y;  
		z = B.z - z;
//		if(Double.isNaN(x)||Double.isNaN(y))
//			System.out.println("ERROR");
	}

	
	public void add(Vector2d v1) {
		x += v1.x;
		y += v1.y;		
//		if(Double.isNaN(x)||Double.isNaN(y))
//			System.out.println("ERROR");
	}

	/**
	 * Computes the dot product of the this vector and vector v1.
	 * @param v1 the other vector
	 */
	public final double dot(Vector3d v1)
	{
		return (this.x * v1.x + this.y * v1.y + this.z * v1.z);
	}



	public double[] getAngle() {
		double[] angle3D = new double[3];
		angleX = ax - x;
		angleY = ay - y;
		angleZ = az - z;
		angle3D[0] = angleX;
		angle3D[1] = angleY;
		angle3D[2] = angleZ;
		return angle3D;
	}

	/**
	 *   Returns the angle in radians between this vector and the vector
	 *   parameter; the return value is constrained to the range [0,PI].
	 *   @param v1    the other vector
	 *   @return   the angle in radians in the range [0,PI]
	 */
	/*public final double angle(Vector2d v1)
	{
		double vDot = this.dot(v1) / ( this.length()*v1.length() );
		if( vDot < -1.0) vDot = -1.0;
		if( vDot >  1.0) vDot =  1.0;
		return((double) (FastMath.acos( vDot )));

	}
	*/
	
	public void negate() {
		x=-x;
		y=-y;
		z=-z;
//		if(Double.isNaN(x)||Double.isNaN(y))
//			System.out.println("ERROR");
	}

	public final void setLength(double newLength) {
//		if(x==0 && y==0)
//			System.out.println("ERROR");
		double factor = newLength/length();
		x *= factor;
		y *= factor;
		z *= factor;
	}
	
	
	public double distanceTo(Vector3d position) {
		double x = position.x - this.x;
		double y = position.y - this.y;	
		double z = position.z - this.z;
		return FastMath.sqrtQuick(x*x+y*y+z*z);
	}
	
	@Override
	public String toString() {
		return "[angle=" + angleX + "," + angleY + "," + angleZ + ", x=" + x + ", y=" + y + "]" + ", z=" + z + "]";
	}

	public void multiply(double t) {
		x*=t;
		y*=t;
		z*=t;
	}

}

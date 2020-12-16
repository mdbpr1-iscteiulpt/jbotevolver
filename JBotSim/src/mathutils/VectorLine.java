package mathutils;

import java.io.Serializable;

import net.jafama.FastMath;

public class VectorLine extends Point implements Serializable {
	private double ay;
	private double ax;
	private double az;
	private double angleX;
	private double angleY;
	private double angleZ;
	
	public VectorLine(double x, double y) {
		super(x,y);
	}

	public VectorLine(double x, double y,double z) {
		super(x,y,z);
	}

	public VectorLine() {
		super();
	}

	public VectorLine(mathutils.VectorLine v) {
		super(v.x, v.y, v.z);
	}

	/**  
	 * Returns the length of this vector.
	 * @return the length of this vector
	 */  
	public final double lengthIgnoringZ()
	{
		return (double) FastMath.sqrtQuick(this.x*this.x + this.y*this.y);
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


	//2D only
	public void rotate(double angle) {
		double xTemp = x * FastMath.cosQuick(angle) -  y * FastMath.sinQuick(angle);
		y = x * FastMath.sinQuick(angle) + y * FastMath.cosQuick(angle);
		x = xTemp;
//		if(Double.isNaN(x)||Double.isNaN(y))
//			System.out.println("ERROR");
	}

	//3D only	
	//Rotate around the Z angle
	public void rotateZ(double angle) {
		double xTemp = x * FastMath.cosQuick(angle) -  y * FastMath.sinQuick(angle);
		y = x * FastMath.sinQuick(angle) + y * FastMath.cosQuick(angle);
		x = xTemp;
//		if(Double.isNaN(x)||Double.isNaN(y))
//			System.out.println("ERROR");
	}
	//Rotate around the Y angle
	public void rotateY(double angle) {
		double xTemp = x * FastMath.cosQuick(angle) +  z * FastMath.sinQuick(angle);
		z = x * FastMath.sinQuick(angle) + z * FastMath.cosQuick(angle);
		x = xTemp;
//		if(Double.isNaN(x)||Double.isNaN(y))
//			System.out.println("ERROR");
	}
	
	//Rotate around the Y angle
	public void rotateX(double angle) {
		double yTemp = y * FastMath.cosQuick(angle) -  z * FastMath.sinQuick(angle);
		z = y * FastMath.sinQuick(angle) + z * FastMath.cosQuick(angle);
		y = yTemp;
//		if(Double.isNaN(x)||Double.isNaN(y))
//			System.out.println("ERROR");
	}
	
	public void rotate(double angleX,double angleY, double angleZ) {
		double xTemp = x * FastMath.cosQuick(angleZ) -  y * FastMath.sinQuick(angleZ);
		y = x * FastMath.sinQuick(angleZ) + y * FastMath.cosQuick(angleZ);
		x = xTemp;
		xTemp = x * FastMath.cosQuick(angleY) +  z * FastMath.sinQuick(angleY);
		z = x * FastMath.sinQuick(angleY) + z * FastMath.cosQuick(angleY);
		x = xTemp;	
		double yTemp = y * FastMath.cosQuick(angleX) -  z * FastMath.sinQuick(angleX);
		z = y * FastMath.sinQuick(angleX) + z * FastMath.cosQuick(angleX);
		y = yTemp;
	}
	
	// Make vec perpendicular to itself
	public void dVec2Perpendicular()
	{ 
		double tempx = x;
		x  = -y;  
		y  = tempx; 
//		if(Double.isNaN(x)||Double.isNaN(y))
//			System.out.println("ERROR");
	}

	public void sub(VectorLine B) {
		x -= B.x;
		y -= B.y;
		z -= B.z;
//		if(Double.isNaN(x)||Double.isNaN(y))
//			System.out.println("ERROR");
	}


	// Returns the vector going _from_ A to B (thus B - A)
	public void subFrom(VectorLine B)   
	{                               
		x = B.x - x;       
		y = B.y - y;
		z = B.z - z;
//		if(Double.isNaN(x)||Double.isNaN(y))
//			System.out.println("ERROR");
	}

	
	public void add(VectorLine v1) {
		x += v1.x;
		y += v1.y;		
		z += v1.z;
//		if(Double.isNaN(x)||Double.isNaN(y))
//			System.out.println("ERROR");
	}

	/**
	 * Computes the dot product of the this vector and vector v1.
	 * @param v1 the other vector
	 */
	public final double dot(VectorLine v1)
	{
		return (this.x * v1.x + this.y * v1.y + this.z * v1.z);
	}


	//2D only
	public double getAngle() {
		//double a=  Math.atan2(y, x) ;
		if(ax != x || ay != y){
			angleZ	= FastMath.atan2(y, x) ;
			ax		= x;
			ay		= y;
		}
		return angleZ;
//		return Math.atan2(y, x) ;
	}
	
	//3D only
	public double getAngleZ() {
		//double a=  Math.atan2(y, x) ;
		if(ax != x || ay != y){
			angleZ	= FastMath.atan2(y, x) ;
			ax		= x;
			ay		= y;
		}
		return angleZ;
//		return Math.atan2(y, x) ;
	}	
	
	public double getAngleX() {
		return angleX;
	}	

	public double getAngleY() {
		if(az != z){
			angleY = FastMath.asin(z);
		}
		return angleY;
//		return Math.atan2(y, x) ;
	}		
	
	//2D only
	/**
	 *   Returns the angle in radians between this vector and the vector
	 *   parameter; the return value is constrained to the range [0,PI].
	 *   @param v1    the other vector
	 *   @return   the angle in radians in the range [0,PI]
	 */
	public final double angle(VectorLine v1)
	{
		double vDot = this.dot(v1) / ( this.length()*v1.length() );
		if( vDot < -1.0) vDot = -1.0;
		if( vDot >  1.0) vDot =  1.0;
		return((double) (FastMath.acos( vDot )));

	}

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
	
	
	public double distanceTo(VectorLine position) {
		double x = position.x - this.x;
		double y = position.y - this.y;		
		double z = position.z - this.z;
		return FastMath.sqrtQuick(x*x+y*y+z*z);
	}
	
	public double distanceToIgnoring3D(VectorLine position) {
		double x = position.x - this.x;
		double y = position.y - this.y;		
		return FastMath.sqrtQuick(x*x+y*y);
	}
	
	@Override
	public String toString() {
		return "[angle=" + angleZ + ", x=" + x + ", y=" + y + ", z=" + z + "]";
	}

	public void multiply(double t) {
		x*=t;
		y*=t;
		z*=t;
	}

}

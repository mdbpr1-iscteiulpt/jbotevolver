package mathutils;

public class Point3d extends Point2d {

	public double z;
	
	public Point3d(double x, double y,double z) {
		super();
		this.x = x;
		this.y = y;
		this.z = z;
		// if(Double.isNaN(x)||Double.isNaN(y))
		// System.out.println("ERROR");
	}
	
	public Point3d() {
		super();
		this.x = 0;
		this.y = 0;
		this.z = 0;
	}

	public final double getZ() {
		return z;
	}

	public final void setZ(double z) {
		this.z = z;
	}

	public final void set(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;

	}

	/*public void set(Point3d newPos) {
		this.x = newPos.x;
		this.y = newPos.y;
		// if(Double.isNaN(x)||Double.isNaN(y))
		// System.out.println("ERROR");
	}*/

	
}

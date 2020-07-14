package mathutils;

import java.io.Serializable;

public class Point implements Serializable {
	public double x;
	public double y;
	public double z;
	
	public Point(double x, double y) {
		super();
		this.x = x;
		this.y = y;
		// if(Double.isNaN(x)||Double.isNaN(y))
		// System.out.println("ERROR");
	}

	public Point(double x, double y, double z) {
		super();
		this.x = x;
		this.y = y;
		this.z = z;
		// if(Double.isNaN(x)||Double.isNaN(y))
		// System.out.println("ERROR");
	}
	
	public Point() {
		super();
		this.x = 0;
		this.y = 0;
		this.z = 0;
	}

	public final double getX() {
		return x;
	}

	public final void setX(double x) {
		this.x = x;
		// if(Double.isNaN(x)||Double.isNaN(y))
		// System.out.println("ERROR");
	}

	public final double getY() {
		return y;
	}

	public final void setY(double y) {
		this.y = y;
		// if(Double.isNaN(x)||Double.isNaN(y))
		// System.out.println("ERROR");

	}
	
	public final double getZ() {
		return z;
	}

	public final void setZ(double z) {
		this.z = z;
		// if(Double.isNaN(x)||Double.isNaN(y))
		// System.out.println("ERROR");
	}

	public final void set(double x, double y) {
		this.x = x;
		this.y = y;
		// if(Double.isNaN(x)||Double.isNaN(y))
		// System.out.println("ERROR");

	}

	public final void set(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
		// if(Double.isNaN(x)||Double.isNaN(y))
		// System.out.println("ERROR");

	}
	
	public void set(Point newPos) {
		this.x = newPos.x;
		this.y = newPos.y;
		this.z = newPos.z;
		// if(Double.isNaN(x)||Double.isNaN(y))
		// System.out.println("ERROR");
	}

}

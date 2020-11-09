package simulation.physicalobjects;

import java.io.Serializable;

public class GeometricInfo implements Serializable {
	private double angleX;
	private double angleY;
	private double angleZ;
	private double distance;
	public GeometricInfo(double angle, double distance) {
		super();
		this.angleZ = angle;
		this.distance = distance;
	}
	public GeometricInfo(double angleX,double angleY, double angleZ, double distance) {
		super();
		this.angleX = angleX;
		this.angleY = angleY;
		this.angleZ = angleZ;
		this.distance = distance;
	}
	
	public double getAngle() {
		return angleZ;
	}
	public double getAngleX() {
		return angleX;
	}
	public double getAngleY() {
		return angleY;
	}
	public double getAngleZ() {
		return angleZ;
	}
	
	public void setAngle(double angle) {
		this.angleZ = angle;
	}
	public void setAngle(double angleX,double angleY,double angleZ) {
		this.angleX = angleX;
		this.angleY = angleY;
		this.angleZ = angleZ;
	}
	public void setAngleX(double angle) {
		this.angleX = angle;
	}
	public void setAngleY(double angle) {
		this.angleY = angle;
	}
	public void setAngleZ(double angle) {
		this.angleZ = angle;
	}
	public double getDistance() {
		return distance;
	}
	public void setDistance(double distance) {
		this.distance = distance;
	}
	@Override
	public String toString() {
		return "GeometricInfo [angle=" + angleZ + ", distance=" + distance + "]";
	}
	
	
}

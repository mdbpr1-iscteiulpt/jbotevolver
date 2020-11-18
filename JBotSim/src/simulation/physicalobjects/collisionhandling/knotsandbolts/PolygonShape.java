package simulation.physicalobjects.collisionhandling.knotsandbolts;

import java.awt.Polygon;
import java.awt.geom.Area;

import simulation.Simulator;
import simulation.physicalobjects.*;
import tests.Cronometer;

//Duvida
public class PolygonShape extends Shape {
	
	private double[] x;
	private double[] y;
	private double[] z;
	private Polygon polygon;
	public boolean collision = false;
	private Area area;

	public PolygonShape(Simulator simulator, String name, PhysicalObject parent,
			double relativePosX, double relativePosY, double relativePosZ, double range, double[] x, double[] y, double[] z) {
		super (simulator, name, parent, relativePosX, relativePosY, relativePosZ, range);

		this.x = x;
		this.y = y;
		this.z = z;
		
		computeNewPositionAndOrientationFromParent();
	}
	
	@Override
	public void computeNewPositionAndOrientationFromParent() {
		int[] xi = new int[x.length];
		int[] yi = new int[y.length];
		int[] zi = new int[z.length];
		
		for(int i = 0 ; i < x.length ; i++) {
			xi[i] = (int)(x[i]*10000);
			yi[i] = (int)(y[i]*10000);
			zi[i] = (int)(z[i]*10000);
		}
		
		this.polygon = new Polygon(xi, yi, xi.length);
		area = new Area(this.polygon);
	}

	public int getCollisionObjectType(){
		return COLLISION_OBJECT_TYPE_RECTANGLE;
	}
	
	public Polygon getPolygon() {
		return polygon;
	}
	
}

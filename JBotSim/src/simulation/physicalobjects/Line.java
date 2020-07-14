package simulation.physicalobjects;

import java.awt.Color;

import mathutils.MathUtils;
import mathutils.VectorLine;
import simulation.Simulator;

public class Line extends PhysicalObject{

	private VectorLine pointA;
	private VectorLine pointB;
	private Color color;
	
	public Line(Simulator simulator, String name, double x0, double y0, double x1, double y1) {
		super(simulator, name, x0+(x1-x0)/2, y0+(y1-y0)/2, 0, 0, PhysicalObjectType.LINE);
		pointA = new VectorLine(x0, y0);
		pointB = new VectorLine(x1, y1);
		
		color=Color.BLUE;
	}
	
	public Line(Simulator simulator, String name, double x0, double y0, double x1, double y1, Color color) {
		super(simulator, name, x0+(x1-x0)/2, y0+(y1-y0)/2, 0, 0, PhysicalObjectType.LINE);
		pointA = new VectorLine(x0, y0);
		pointB = new VectorLine(x1, y1);
		this.color = color;
	}
	
	public VectorLine getPointA() {
		return pointA;
	}
	
	public VectorLine getPointB() {
		return pointB;
	}
	
	public VectorLine intersectsWithLineSegment(VectorLine p1, VectorLine p2) {
		VectorLine closestPoint      = null;
		VectorLine lineSegmentVector = new VectorLine(p2);
		lineSegmentVector.sub(p1);
		
		closestPoint = MathUtils.intersectLines(p1, p2, pointA, pointB);
		
		return closestPoint;
	}
	
	@Override
	public double getDistanceBetween(VectorLine fromPoint) {
		
		VectorLine light = new VectorLine(position);
		lightDirection.set(light.getX()-fromPoint.getX(),light.getY()-fromPoint.getY());

		VectorLine intersection = intersectsWithLineSegment(lightDirection,fromPoint);
		if(intersection != null) {
			return intersection.length();
		}
		return fromPoint.distanceTo(position);
	}
	
	public VectorLine getIntersection(VectorLine fromPoint) {
		
		VectorLine light = new VectorLine(position);
		lightDirection.set(light.getX()-fromPoint.getX(),light.getY()-fromPoint.getY());

		VectorLine intersection = intersectsWithLineSegment(lightDirection,fromPoint);
		if(intersection != null) {
			return intersection;
		}
		return null;
	}
	
	public Color getColor(){
		return color;
	}
	
	public void setColor(Color color){
		this.color=color;
	}

}
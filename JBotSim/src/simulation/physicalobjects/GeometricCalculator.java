package simulation.physicalobjects;

import java.io.Serializable;

import mathutils.VectorLine;

public class GeometricCalculator implements Serializable {

	//to avoid constant allocation, used as temporary variable for calculations
	private VectorLine lightDirection = new VectorLine();

	public GeometricInfo getGeometricInfoBetween(PhysicalObject fromObject,
			PhysicalObject toObject, double time) {
		VectorLine coord = fromObject.position;
		VectorLine light = toObject.position;
		lightDirection.set(light.getX()-coord.getX(),light.getY()-coord.getY());
		double lightAngle=fromObject.getOrientation()-lightDirection.getAngle();

		if(lightAngle>Math.PI){
			lightAngle-=2*Math.PI;
		} else if(lightAngle<-Math.PI){ 
			lightAngle+=2*Math.PI;
		}
		return new GeometricInfo(lightAngle, lightDirection.length());
	}

	public GeometricInfo getGeometricInfoBetween(VectorLine fromPoint, double orientation,
			PhysicalObject toObject, double time) {
		VectorLine light = toObject.position;
		lightDirection.set(light.getX()-fromPoint.getX(),light.getY()-fromPoint.getY());
		double lightAngle=orientation-lightDirection.getAngle();

		if(lightAngle>Math.PI){
			lightAngle-=2*Math.PI;
		} else if(lightAngle<-Math.PI){ 
			lightAngle+=2*Math.PI;
		}
		return new GeometricInfo(lightAngle, lightDirection.length());
	}
	
	public GeometricInfo getGeometricInfoBetweenPoints(VectorLine fromPoint, double orientation,
			VectorLine toPoint, double time){
		VectorLine light = toPoint;
		lightDirection.set(light.getX()-fromPoint.getX(),light.getY()-fromPoint.getY());
		double lightAngle=orientation-lightDirection.getAngle();
		
		if(lightAngle>Math.PI){
			lightAngle-=2*Math.PI;
		} else if(lightAngle<-Math.PI){ 
			lightAngle+=2*Math.PI;
		}
		
		return new GeometricInfo(lightAngle, lightDirection.length());
	}

	public double getDistanceBetween(VectorLine fromPoint, PhysicalObject toObject, double time) {
		return toObject.getDistanceBetween(fromPoint);
	}
}
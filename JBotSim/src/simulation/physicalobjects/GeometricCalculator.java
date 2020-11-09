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
		lightDirection.set(light.getX()-coord.getX(),light.getY()-coord.getY(),light.getZ()-coord.getZ());
		double lightAngleZ=fromObject.getOrientationZ()-lightDirection.getAngleZ();
		double lightAngleX=fromObject.getOrientationX()-lightDirection.getAngleX();
		double lightAngleY=fromObject.getOrientationY()-lightDirection.getAngleY();

		if(lightAngleZ>Math.PI){
			lightAngleZ-=2*Math.PI;
		} else if(lightAngleZ<-Math.PI){ 
			lightAngleZ+=2*Math.PI;
		}
		if(lightAngleX>Math.PI){
			lightAngleX-=2*Math.PI;
		} else if(lightAngleX<-Math.PI){ 
			lightAngleX+=2*Math.PI;
		}
		if(lightAngleY>Math.PI){
			lightAngleY-=2*Math.PI;
		} else if(lightAngleY<-Math.PI){ 
			lightAngleY+=2*Math.PI;
		}
		return new GeometricInfo(lightAngleX ,lightAngleY, lightAngleZ, lightDirection.length());
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

	//3D Version
	public GeometricInfo getGeometricInfoBetween(VectorLine fromPoint, double orientationX, double orientationY, double orientationZ,
			PhysicalObject toObject, double time) {
		VectorLine light = toObject.position;
		lightDirection.set(light.getX()-fromPoint.getX(),light.getY()-fromPoint.getY(),light.getZ()-fromPoint.getZ());
		double lightAngleX=orientationX-lightDirection.getAngleX();
		double lightAngleY=orientationY-lightDirection.getAngleY();
		double lightAngleZ=orientationZ-lightDirection.getAngleZ();
		
		if(lightAngleX>Math.PI){
			lightAngleX-=2*Math.PI;
		} else if(lightAngleX<-Math.PI){ 
			lightAngleX+=2*Math.PI;
		}
		if(lightAngleY>Math.PI){
			lightAngleY-=2*Math.PI;
		} else if(lightAngleY<-Math.PI){ 
			lightAngleY+=2*Math.PI;
		}
		if(lightAngleZ>Math.PI){
			lightAngleZ-=2*Math.PI;
		} else if(lightAngleZ<-Math.PI){ 
			lightAngleZ+=2*Math.PI;
		}
		return new GeometricInfo(lightAngleX, lightAngleY, lightAngleZ, lightDirection.length());
	}
	
	public GeometricInfo getGeometricInfoBetweenPoints(VectorLine fromPoint,  double orientationX, double orientationY, double orientationZ,
			VectorLine toPoint, double time){
		VectorLine light = toPoint;
		lightDirection.set(light.getX()-fromPoint.getX(),light.getY()-fromPoint.getY(),light.getZ()-fromPoint.getZ());
		double lightAngleX=orientationX-lightDirection.getAngleX();
		double lightAngleY=orientationY-lightDirection.getAngleY();
		double lightAngleZ=orientationZ-lightDirection.getAngleZ();		
		
		if(lightAngleX>Math.PI){
			lightAngleX-=2*Math.PI;
		} else if(lightAngleX<-Math.PI){ 
			lightAngleX+=2*Math.PI;
		}
		if(lightAngleY>Math.PI){
			lightAngleY-=2*Math.PI;
		} else if(lightAngleY<-Math.PI){ 
			lightAngleY+=2*Math.PI;
		}
		if(lightAngleZ>Math.PI){
			lightAngleZ-=2*Math.PI;
		} else if(lightAngleZ<-Math.PI){ 
			lightAngleZ+=2*Math.PI;
		}
		
		return new GeometricInfo(lightAngleX, lightAngleY, lightAngleZ, lightDirection.length());
	}

	public double getDistanceBetween(VectorLine fromPoint, PhysicalObject toObject, double time) {
		return toObject.getDistanceBetween(fromPoint);
	}
}
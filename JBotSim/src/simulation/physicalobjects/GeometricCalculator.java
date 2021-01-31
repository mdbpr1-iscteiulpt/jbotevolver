package simulation.physicalobjects;

import java.io.Serializable;

import mathutils.VectorLine;
import simulation.physicalobjects.Wall.Edge;

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
		VectorLine light;
		if(toObject instanceof Wall){
			Double Width = ((Wall) toObject).getWidth()/2; Double Lenght = ((Wall) toObject).getLenght()/2; Double Height = ((Wall) toObject).getHeight()/2;
			VectorLine evaluatedPoint = new VectorLine(); evaluatedPoint.set(fromPoint);
			evaluatedPoint.set(Math.max(toObject.getPosition().x - Width, Math.min(evaluatedPoint.x, toObject.getPosition().x + Width)),Math.max(toObject.getPosition().y - Lenght, Math.min(evaluatedPoint.y, toObject.getPosition().y + Lenght)),Math.max(toObject.getPosition().z - Height, Math.min(evaluatedPoint.z, toObject.getPosition().z + Height)));
			light = evaluatedPoint;
		}
		else{
			light = toObject.position;
		}
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
		//Wall searches for point in wall instead of center of object
		if(toObject instanceof Wall){
			Double Width = ((Wall) toObject).getWidth()/2; Double Lenght = ((Wall) toObject).getLenght()/2; Double Height = ((Wall) toObject).getHeight()/2;
			VectorLine evaluatedPoint = new VectorLine(Math.max(toObject.getPosition().x - Width, Math.min(fromPoint.x, toObject.getPosition().x + Width)),Math.max(toObject.getPosition().y - Lenght, Math.min(fromPoint.y, toObject.getPosition().y + Lenght)),Math.max(toObject.getPosition().z - Height, Math.min(fromPoint.z, toObject.getPosition().z + Height)));
			//System.out.println("Wallposition: (" +  toObject.getPosition() + ") with point: " + evaluatedPoint + " while robot position is: " + fromPoint + " also Widht, Lenght, Height: (" + Width + "," + Lenght + "," + Height + ")");
			return evaluatedPoint.distanceTo(fromPoint);
		}
		else {
			return toObject.getDistanceBetween(fromPoint);
		}
	}
}
 
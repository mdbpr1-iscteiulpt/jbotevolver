package simulation.robot.sensors;

import mathutils.VectorLine;
import simulation.Simulator;
import simulation.physicalobjects.GeometricInfo;
import simulation.physicalobjects.PhysicalObjectDistance;
import simulation.physicalobjects.checkers.AllowWallChecker;
import simulation.physicalobjects.Wall;
import simulation.robot.Robot;
import simulation.util.Arguments;

public class WallSensor extends LightTypeSensor {

	public WallSensor(Simulator simulator, int id, Robot robot, Arguments args) {
		super(simulator, id, robot, args);
		setAllowedObjectsChecker(new AllowWallChecker());
	}
	
	//Detect Closest Point Instead Of center of Object
	@Override
	protected double calculateContributionToSensor(int sensorNumber, PhysicalObjectDistance source) {
		Double Width = ((Wall)source.getObject()).getWidth()/2; Double Lenght = ((Wall)source.getObject()).getLenght()/2; Double Height = ((Wall) source.getObject()).getHeight()/2;
		VectorLine evaluatedPoint = new VectorLine(Math.max(source.getObject().getPosition().x - Width, Math.min(this.sensorPosition.x, source.getObject().getPosition().x + Width)),Math.max(source.getObject().getPosition().y - Lenght, Math.min(this.sensorPosition.y, source.getObject().getPosition().y + Lenght)),Math.max(source.getObject().getPosition().z - Height, Math.min(this.sensorPosition.z, source.getObject().getPosition().z + Height)));
		GeometricInfo sensorInfo = getSensorGeometricInfo(sensorNumber, evaluatedPoint);
		if(topbotTypeSensor[sensorNumber] == false)
		{
			if(verticalAngle==0) {
				if((sensorInfo.getDistance() < getCutOff()) && 
				   (sensorInfo.getAngleZ() < (openingAngle / 2.0)) && 
				   (sensorInfo.getAngleZ() > (-openingAngle / 2.0)) && 
				   (sensorInfo.getAngleY() < (openingAngle / 2.0)) && 
				   (sensorInfo.getAngleY() > (-openingAngle / 2.0))) {
						return (getRange() - sensorInfo.getDistance()) / getRange();
				}
			}
			else {
				if((sensorInfo.getDistance() < getCutOff()) && 
					(sensorInfo.getAngleZ() < (openingAngle / 2.0)) && 
					(sensorInfo.getAngleZ() > (-openingAngle / 2.0)) && 
					(sensorInfo.getAngleY() < (verticalAngle / 2.0)) && 
					(sensorInfo.getAngleY() > (-verticalAngle / 2.0))) {
						return (getRange() - sensorInfo.getDistance()) / getRange();
				}				
			}
		}
		else if(topbotTypeSensor[sensorNumber] == true)   {
			if(verticalAngle==0) {
				if((sensorInfo.getDistance() < getCutOff()) && 
					(sensorInfo.getAngleY() < (openingAngle / 2.0)) && 
					(sensorInfo.getAngleY() > (-openingAngle / 2.0))) {
					return (getRange() - sensorInfo.getDistance()) / getRange();
				}
			}
			else {
				if((sensorInfo.getDistance() < getCutOff()) && 
					(sensorInfo.getAngleY() < (verticalAngle / 2.0)) && 
					(sensorInfo.getAngleY() > (-verticalAngle / 2.0))) {
						return (getRange() - sensorInfo.getDistance()) / getRange();
				}				
			}
		}
 		return 0;
	}
	
}
package simulation.robot.sensors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import mathutils.VectorLine;
import net.jafama.FastMath;
import simulation.Simulator;
import simulation.physicalobjects.ClosePhysicalObjects.CloseObjectIterator;
import simulation.physicalobjects.GeometricInfo;
import simulation.physicalobjects.PhysicalObject;
import simulation.physicalobjects.PhysicalObjectDistance;
import simulation.physicalobjects.PhysicalObjectType;
import simulation.physicalobjects.Wall;
import simulation.physicalobjects.checkers.AllowWallChecker;
import simulation.robot.Robot;
import simulation.util.Arguments;

@SuppressWarnings("serial")
public class WallRaySensor extends ConeTypeSensor {

	protected int numberOfRays = 7;
	protected double[][] rayReadings;
	
	protected Random random;
	protected VectorLine[][] cones;
	protected VectorLine[] sensorPositions;
	protected double cutoffAngle = 90;
	
	protected double closestDistance;
	
	public VectorLine[][][] rayPositions;
	
	private boolean seeRays;
	
	public WallRaySensor(Simulator simulator, int id, Robot robot, Arguments args) {
		super(simulator,id,robot,args);
		this.random = simulator.getRandom();
		
		numberOfRays = args.getArgumentAsIntOrSetDefault("numberofrays", numberOfRays);
		cutoffAngle = args.getArgumentAsDoubleOrSetDefault("cutoffangle", cutoffAngle);
		seeRays = args.getArgumentAsIntOrSetDefault("seerays", 0) == 1;
		
		if(numberOfRays%2 == 0)
			numberOfRays++;
		
		rayReadings = new double[numberOfSensors][numberOfRays];
		
		setAllowedObjectsChecker(new AllowWallChecker());
		
		sensorPositions = new VectorLine[numberOfSensors];
		cones = new VectorLine[numberOfSensors][numberOfRays];
		for(int i = 0 ; i < numberOfSensors ; i++) {
			sensorPositions[i] = new VectorLine();
			for(int j = 0 ; j < numberOfRays ; j++)
				cones[i][j] = new VectorLine();
		}
	}
	
	private void updateCones() {
		
		try {
			
			if(seeRays)
				rayPositions = new VectorLine[numberOfSensors][numberOfRays][2];
			
			for(int sensorNumber = 0 ; sensorNumber < numberOfSensors ; sensorNumber++) {
				double orientation = anglesZ[sensorNumber] + robot.getOrientation();
				
				sensorPositions[sensorNumber].set(
						FastMath.cosQuick(orientation) * robot.getRadius() + robot.getPosition().getX(),
						FastMath.sinQuick(orientation) * robot.getRadius() + robot.getPosition().getY()
					);
				
				double alpha = (this.openingAngle)/(numberOfRays-1);
				
				double halfOpening = openingAngle/2.0;
				
				for(int i = 0 ; i < numberOfRays ; i++) {
					//the multiplication by 5 is necessary because of the close/far objects estimation
					//the number 5 is arbitrary
					
					double angleZ = orientation - halfOpening + alpha*i;
					
					if(numberOfRays == 1) {
						angleZ = orientation;
					}
					
					cones[sensorNumber][i].set(
							FastMath.cosQuick(angleZ)* range*5 + sensorPositions[sensorNumber].getX(),
							FastMath.sinQuick(angleZ)* range*5 + sensorPositions[sensorNumber].getY()
						 );
				}
			}
		}catch(Throwable e) {
			e.printStackTrace();
		}
	}

	/**
	 * Calculates the distance between the robot and a given PhisicalObject if
	 * in range
	 * 
	 * @return distance or 0 if not in range
	 */
	@Override
	protected double calculateContributionToSensor(int sensorNumber,
			PhysicalObjectDistance source) {
			
		double inputValue = 0;
		
		if(source.getObject().getType() != PhysicalObjectType.ROBOT) {
		
			Wall w = (Wall) source.getObject();
			
			for(int i = 0 ; i < numberOfRays ; i++) {
				VectorLine cone = cones[sensorNumber][i];
				
				if(seeRays){
					rayPositions[sensorNumber][i][0] = sensorPositions[sensorNumber];
					if(rayPositions[sensorNumber][i][1] == null)
						rayPositions[sensorNumber][i][1] = cone;
				}
				
				VectorLine intersection = null;
				intersection = w.intersectsWithLineSegment(sensorPositions[sensorNumber], cone, FastMath.toRadians(cutoffAngle));
				
				if(intersection != null) {
					
					double distance = intersection.distanceTo(sensorPositions[sensorNumber]);
					closestDistance = distance < closestDistance ? distance : closestDistance;
					cone.angle(intersection);
					
					if(distance < range) {
						inputValue = (range-distance)/range;
						
						if(inputValue > rayReadings[sensorNumber][i]) {
							if(seeRays)
								rayPositions[sensorNumber][i][1] = intersection;
							
							rayReadings[sensorNumber][i] = Math.max(inputValue, rayReadings[sensorNumber][i]);
						}
					}
				}
			}
		}
		return inputValue;
	}
	
	@Override
	public void update(double time, ArrayList<PhysicalObject> teleported) {
		
		updateCones();
		
		if(closeObjects != null) {
			closeObjects.update(time, teleported);
		}
	
		try { 
			for(int i = 0; i < numberOfSensors; i++){
				for(int j = 0; j < numberOfRays; j++){
					rayReadings[i][j] = 0.0;
				}
				readings[i] = 0.0;
			}
			CloseObjectIterator iterator = getCloseObjects().iterator();
			while(iterator.hasNext()){
				PhysicalObjectDistance source=iterator.next();
				if (source.getObject().isEnabled()){
					closestDistance = range*5;
					calculateSourceContributions(source);
					iterator.updateCurrentDistance(closestDistance);
				}
			}
			
			for(int i = 0; i < numberOfSensors; i++){
				double avg = 0;
				for(int ray = 0 ; ray < numberOfRays ; ray++) {
					avg+= rayReadings[i][ray]/numberOfRays;
				}
				readings[i]=avg;
			}
		} catch (Exception e) {
			e.printStackTrace(); 
		}
	}
	
	@Override
	protected void calculateSourceContributions(PhysicalObjectDistance source) {
		
		for(int j=0; j<numberOfSensors; j++){
			if(openingAngle > 0.018){ //1degree
				calculateContributionToSensor(j, source);
			}
		}
	}
	
	@Override
	protected GeometricInfo getSensorGeometricInfo(int sensorNumber,
			VectorLine source) {
		double orientationX = anglesX[sensorNumber] + robot.getOrientationX();
		double orientationY = anglesY[sensorNumber] + robot.getOrientationY();
		double orientationZ = anglesZ[sensorNumber] + robot.getOrientationZ();
		//O que se ganha aqui sequer?
		sensorPosition.set(
				FastMath.cosQuick(orientationZ) * FastMath.cosQuick(orientationY) * robot.getRadius()
						+ robot.getPosition().getX(),
				FastMath.sinQuick(orientationZ) * FastMath.cosQuick(orientationY) * robot.getRadius()
						+ robot.getPosition().getY(),
				FastMath.sinQuick(orientationY) * robot.getRadius()
						+ robot.getPosition().getZ()
				);

		GeometricInfo sensorInfo = geoCalc.getGeometricInfoBetweenPoints(sensorPosition, 
				orientationX,orientationY,orientationZ,source, time);

		return sensorInfo;
	}

	@Override
	public String toString() {
		for (int i = 0; i < numberOfSensors; i++)
			getSensorReading(i);
		return "WallRaySensor [readings=" + Arrays.toString(readings) + "]";
	}
}
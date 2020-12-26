package simulation.environment;

import java.util.Random;
import mathutils.VectorLine;
import simulation.Simulator;
import simulation.physicalobjects.ClosePhysicalObjects.CloseObjectIterator;
import simulation.physicalobjects.Nest;
import simulation.physicalobjects.PhysicalObjectDistance;
import simulation.physicalobjects.Prey;
import simulation.physicalobjects.Wall;
import simulation.robot.Robot;
import simulation.robot.actuators.PreyPickerActuator;
import simulation.robot.sensors.NearRobotSensor;
import simulation.robot.sensors.PreyCarriedSensor;
import simulation.robot.sensors.RobotSensor;
import simulation.util.Arguments;
import simulation.util.ArgumentsAnnotation;

public class MaintainDistanceEnvironment extends Environment {

	private static final double PREY_RADIUS = 0.025;
	private static final double PREY_MASS = 1;	
	
	private int numberOfRobotsThatCollided = 0;
	private int numberOfRobotsInDangerArea = 0;
	private int numberOfRobotsInSafeArea = 0;
	
	@ArgumentsAnnotation(name="WallAreaSetup", values={"0","1"})	
	private boolean WallAreaSetup = false;

	@ArgumentsAnnotation(name="forbiddenarea", defaultValue="5.0")
	private	double forbiddenArea;
	
	private double dangerDistance = 0.5f;
	
	private Random random;
	
	private Simulator simulator;
	private Arguments args;

	public MaintainDistanceEnvironment(Simulator simulator, Arguments arguments) {
		super(simulator, arguments);
		this.simulator = simulator;
		this.args = arguments;
		forbiddenArea   = args.getArgumentIsDefined("forbiddenarea") ? args.getArgumentAsDouble("forbiddenarea")       : 5.0;
		WallAreaSetup = args.getArgumentAsIntOrSetDefault("WallAreaSetup", 0) == 1;
	}
	
	@Override
	public void setup(Simulator simulator) {
		super.setup(simulator);
		
		if(simulator.getRobots().size() >= 1) {
			double rotation = (2 * Math.PI) /  simulator.getRobots().size();
			int robotnumb = 1;
			for(Robot robot: robots){
				robot.setPosition(Math.cos(rotation * robotnumb), Math.sin(rotation * robotnumb), 0);
				robot.setOrientation(-rotation * robotnumb);	
				robotnumb++;
			}

		}
		
		addStaticObject(new Wall(simulator, new VectorLine(2,0,0), 0.2,100,100,0));
		//addStaticObject(new Wall(simulator, new VectorLine(-2,0,0), 0.2,100,100,0));
		//addStaticObject(new Wall(simulator, new VectorLine(0,2,0), 100,0.2,100,0));
		//addStaticObject(new Wall(simulator, new VectorLine(0,-2,0), 100,0.2,100,0));
		/*//Create Four Walls Around the "Map"
		if(WallAreaSetup) {
		}*/
		
		this.random = simulator.getRandom();
	}
	
	@Override
	public void update(double time) {
		numberOfRobotsInDangerArea = 0;
		numberOfRobotsInSafeArea = 0;
		for(Robot robot: robots){
			RobotSensor sensor = (RobotSensor)robot.getSensorByType(RobotSensor.class);
			Robot rnear = null;
			for(Robot robottest: robots){
				if(rnear == null&& robottest != null && robottest!=robot) { rnear = robottest; }
				if(rnear != null&& robot!= null) {
					if(robottest!=robot 
							&& rnear.getPosition().distanceTo(robot.getPosition()) > robottest.getPosition().distanceTo(robot.getPosition())) { 
						rnear = robottest; 	
					}
				}
			}
			
			if(rnear != null && robot != null) {
				if (sensor != null && robot.getPosition().distanceTo(rnear.getPosition()) < dangerDistance){
					numberOfRobotsInDangerArea++;
				}
				if (sensor != null && robot.getPosition().distanceTo(rnear.getPosition()) >= dangerDistance){
					numberOfRobotsInSafeArea++;
				}
				if (sensor != null && (robot.isInvolvedInCollison() || robot.isInvolvedInCollisonWall())){
					numberOfRobotsThatCollided++;
				}
			}
		}
	}
	
	public int getNumberOfRobotsThatCollided() {
		return numberOfRobotsThatCollided;
	}

	public double getDangerArea() {
		return dangerDistance;
	}
	
	public int getInDangerArea() {
		return numberOfRobotsInDangerArea;
	}
	
	public int getInSafeArea() {
		return numberOfRobotsInSafeArea;
	}
	
	public double getForbiddenArea() {
		return forbiddenArea;
	}
}

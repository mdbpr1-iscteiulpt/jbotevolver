package simulation.environment;

import java.util.Random;

import javax.swing.text.Position;

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
	@ArgumentsAnnotation(name="CircularCreation", values={"0","1"})	
	private boolean CircularCreation = false;
	@ArgumentsAnnotation(name="WallsSpawnRandomy", values={"0","1"})	
	private boolean WallsSpawnRandomy = false;
	@ArgumentsAnnotation(name="NumbOfWalls", values={"0"})	
	private int NumbOfWalls = 5;	
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
		random = new Random();
		forbiddenArea   = args.getArgumentIsDefined("forbiddenarea") ? args.getArgumentAsDouble("forbiddenarea")       : 5.0;
		CircularCreation = args.getArgumentAsIntOrSetDefault("CircularCreation", 0) == 1;
		WallAreaSetup = args.getArgumentAsIntOrSetDefault("WallAreaSetup", 1) == 1;
		WallsSpawnRandomy = args.getArgumentAsIntOrSetDefault("WallsSpawnRandomy", 1) == 1;
		NumbOfWalls   = args.getArgumentAsIntOrSetDefault("NumbOfWalls", 5);
	}
	
	@Override
	public void setup(Simulator simulator) {
		super.setup(simulator);
		
		if(simulator.getRobots().size() >= 1) {
			double rotation = (2 * Math.PI) /  simulator.getRobots().size();
			int robotnumb = 1;
			for(Robot robot: robots){
				if(CircularCreation){ robot.setPosition(Math.cos(rotation * robotnumb), Math.sin(rotation * robotnumb), 0); }
				else{ robot.setPosition(0, 0, 0); }
				robot.setOrientation(-rotation * robotnumb);	
				robotnumb++;
			}

		}
		if(WallAreaSetup) {
			addStaticObject(new Wall(simulator, new VectorLine(2,0,0), 0.2,100,100,0));
			addStaticObject(new Wall(simulator, new VectorLine(-2,0,0), 0.2,100,100,0));
			addStaticObject(new Wall(simulator, new VectorLine(0,2,0), 100,0.2,100,0));
			addStaticObject(new Wall(simulator, new VectorLine(0,-2,0), 100,0.2,100,0));
		}
		if(WallsSpawnRandomy) {
			for(int i = 0; i < NumbOfWalls ; i++) {
				double[] a = randomPosition();
				double size1 = 0.2 + (0.9-0.2) * random.nextDouble();
				double size2 = 0.2 + (0.9-0.2) * random.nextDouble();
				addStaticObject(new Wall(simulator, new VectorLine(a[0],a[1],0), size1,size2,100,0));
			}
		}
		
		this.random = simulator.getRandom();
	}
	
	@Override
	public void update(double time) {
		numberOfRobotsInDangerArea = 0;
		numberOfRobotsInSafeArea = 0;
		numberOfRobotsThatCollided = 0;
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
			}
			if (sensor != null && robot != null && robot.isInvolvedInCollisonWall()){
				numberOfRobotsThatCollided++;
			}
		}
	}
	
	public double[] randomPosition() {
		double[] a = new double[3];
		for(int i = 0;i<2;i++) {
			double value =  4 * random.nextDouble() - 2;
			if(value < 0.4&& value >= 0) { value = 0.4; } 	if(value > -0.4&& value < 0) { value = -0.4; }
			a[i]= value;			
		}
		return a;
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

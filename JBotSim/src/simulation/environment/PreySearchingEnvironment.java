package simulation.environment;

import java.awt.List;
import java.util.ArrayList;
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

public class PreySearchingEnvironment extends Environment {

	private static final double PREY_RADIUS = 0.025;
	private static final double PREY_MASS = 1;	
	
	private int numberOfRobotsThatCollided = 0;
	private int numberOfRobotsInDangerArea = 0;
	private int numberOfRobotsInSafeArea = 0;
	
	@ArgumentsAnnotation(name="WallAreaSetup", values={"0","1"})	
	private boolean WallAreaSetup = false;
	@ArgumentsAnnotation(name="BigStageTwoSide", values={"0","1"})	
	private boolean BigStageTwoSide = false;
	@ArgumentsAnnotation(name="NumbOfWalls", values={"0"})	
	private int NumbOfWalls = 5;	
	@ArgumentsAnnotation(name="nestArea", defaultValue="1")
	private	double nestArea;
	@ArgumentsAnnotation(name="numberofpreys", defaultValue="2")
	private int numberOfPreys;
		
	private double dangerDistance = 0.5f;

	private ArrayList<Wall> walls;
	
	private int numberOfFoodSuccessfullyForaged = 0;
	private int spawnPos = 0;
	private Random random;
	
	private Simulator simulator;
	private Arguments args;

	public PreySearchingEnvironment(Simulator simulator, Arguments arguments) {
		super(simulator, arguments);
		this.simulator = simulator;
		this.args = arguments;
		random = new Random();
		nestArea   = args.getArgumentIsDefined("nestArea") ? args.getArgumentAsDouble("nestArea")       : 0.4;
		WallAreaSetup = args.getArgumentAsIntOrSetDefault("WallAreaSetup", 1) == 1;
		BigStageTwoSide = args.getArgumentAsIntOrSetDefault("BigStageTwoSide", 1) == 1;
		NumbOfWalls   = args.getArgumentAsIntOrSetDefault("NumbOfWalls", 5);
	}
	
	@Override
	public void setup(Simulator simulator) {
		super.setup(simulator);
		double numberOfPreys;
		Nest nest1 = new Nest(simulator, "Nest", 0, -1.5, nestArea);
		addObject(nest1);
		Nest nest2 = new Nest(simulator, "Nest", 0, 1.5, nestArea);
		addObject(nest2);	
		if(BigStageTwoSide) {
			addWall(new Wall(simulator, new VectorLine(0.8,0,0), 1,0.2,100,0));
			addWall(new Wall(simulator, new VectorLine(-0.8,0,0), 1,0.2,100,0));
			addWall(new Wall(simulator, new VectorLine(0.6,1.5,0), 0.2,1,100,0));
			addWall(new Wall(simulator, new VectorLine(-0.6,1.5,0), 0.2,1,100,0));
			addWall(new Wall(simulator, new VectorLine(0.6,-1.5,0), 0.2,1,100,0));
			addWall(new Wall(simulator, new VectorLine(-0.6,-1.5,0), 0.2,1,100,0));
			addWall(new Wall(simulator, new VectorLine(1.5,1.2,0), 1,0.2,100,0));
			addWall(new Wall(simulator, new VectorLine(-1.5,1.2,0), 1,0.2,100,0));
			addWall(new Wall(simulator, new VectorLine(1.5,-1.2,0), 1,0.2,100,0));
			addWall(new Wall(simulator, new VectorLine(-1.5,-1.2,0), 1,0.2,100,0));			
			
			if(args.getArgumentIsDefined("densityofpreys")){
				double densityoffood = args.getArgumentAsDouble("densityofpreys");
				numberOfPreys = (int)(densityoffood);
			} else {
				numberOfPreys = args.getArgumentIsDefined("numberofpreys") ? args.getArgumentAsInt("numberofpreys") : 10;
			}
			
			for(int i = 0; i < numberOfPreys; i++ ){
				addPrey(new Prey(simulator, "Prey "+i, cycleTroughtSpawnPositions(), 0, PREY_MASS, PREY_RADIUS));
			}
		}		
		if(simulator.getRobots().size() >= 1) {
			double rotation = (2 * Math.PI) /  simulator.getRobots().size();
			int robotnumb = 1;
			for(Robot robot: robots){
				if(robotnumb%2==0) {
					robot.setPosition(nest1.getPosition());
				}
				else {
					robot.setPosition(nest2.getPosition());					
				}
				robotnumb++;
			}

		}
		if(WallAreaSetup) {
			addWall(new Wall(simulator, new VectorLine(2,0,0), 0.2,2,100,0));
			addWall(new Wall(simulator, new VectorLine(-2,0,0), 0.2,2,100,0));
			addWall(new Wall(simulator, new VectorLine(0,2,0), 2,0.2,100,0));
			addWall(new Wall(simulator, new VectorLine(0,-2,0), 2,0.2,100,0));
		}

		
		this.random = simulator.getRandom();
	}
	
	//Chooses one of four spots to spawn (each of the four corners of the map)
	public VectorLine cycleTroughtSpawnPositions() {
		Random m = new Random();
		double[] offset = new double[2];
		if(spawnPos==0) { offset[0] = 1.5; offset[1]= 1.7;  spawnPos=1;}
		else if(spawnPos==1) { offset[0] = -1.5; offset[1]= 1.7; spawnPos=2;}
		else if(spawnPos==2) { offset[0] = 1.5; offset[1]= -1.7; spawnPos=3;}
		else if(spawnPos==3) { offset[0] = -1.5; offset[1]= -1.7; spawnPos=0;}
		double posX = m.nextDouble()*0.5 + offset[0];
		double posY = m.nextDouble()*0.2 + offset[1];
		double posZ = 0;
		if(is3D==true) {
			posZ = (m.nextDouble()*4);	
		}
		return new VectorLine(posX,posY,posZ);
	}
	
	@Override
	public void update(double time) {
		numberOfRobotsInDangerArea = 0;
		numberOfRobotsInSafeArea = 0;
		for(Robot robot: robots){
			VectorLine l = robot.getPosition();
			robot.shape.getClosePrey().update(time, teleported);
			//Check if robot has any prey being carried
			PreyCarriedSensor preysensor = (PreyCarriedSensor)robot.getSensorByType(PreyCarriedSensor.class);
			if (preysensor != null && preysensor.preyCarried()){
				PreyPickerActuator actuator = (PreyPickerActuator)robot.getActuatorByType(PreyPickerActuator.class);
				if(actuator != null) {
					numberOfFoodSuccessfullyForaged++;
					Prey preyToDrop = actuator.dropPrey();
					preyToDrop.teleportTo(cycleTroughtSpawnPositions());
				}
			}	
		}
	}
	
	public int getNumberOfPreyCollected() {
		return numberOfFoodSuccessfullyForaged;
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
	
	public double getNestArea() {
		return nestArea;
	}
	
	public int getInDangerArea() {
		return numberOfRobotsInDangerArea;
	}
	
	public int getInSafeArea() {
		return numberOfRobotsInSafeArea;
	}
	
	public double getForbiddenArea() {
		return nestArea;
	}
}

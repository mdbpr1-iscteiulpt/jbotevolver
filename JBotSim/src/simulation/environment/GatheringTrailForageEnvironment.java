package simulation.environment;

import java.awt.Color;
import java.util.Random;
import mathutils.VectorLine;
import simulation.Simulator;
import simulation.physicalobjects.ClosePhysicalObjects.CloseObjectIterator;
import simulation.physicalobjects.Nest;
import simulation.physicalobjects.PhysicalObjectDistance;
import simulation.physicalobjects.Prey;
import simulation.robot.Robot;
import simulation.robot.actuators.PreyPickerActuator;
import simulation.robot.sensors.PreyCarriedSensor;
import simulation.util.Arguments;
import simulation.util.ArgumentsAnnotation;
//Doesnt Work Yet!
public class GatheringTrailForageEnvironment extends Environment {

	private static final double PREY_RADIUS = 0.025;
	private static final double PREY_MASS = 1;
	
	@ArgumentsAnnotation(name="nestlimit", defaultValue="0.5")
	private double nestLimit;
	
	@ArgumentsAnnotation(name="foragelimit", defaultValue="2.0")
	private double forageLimit;
	
	@ArgumentsAnnotation(name="forbiddenarea", defaultValue="5.0")
	private	double forbiddenArea;
	
	@ArgumentsAnnotation(name="numberofpreys", defaultValue="20")
	private int numberOfPreys;
	
	@ArgumentsAnnotation(name="preyPositionX", defaultValue="1")	
	double preyPositionX;
	@ArgumentsAnnotation(name="preyPositionY", defaultValue="0")	
	double preyPositionY;
	@ArgumentsAnnotation(name="preyPositionZ", defaultValue="0")	
	double preyPositionZ;
	
	@ArgumentsAnnotation(name="nestPositionX", defaultValue="-1")	
	double nestPositionX;
	@ArgumentsAnnotation(name="nestPositionY", defaultValue="0")	
	double nestPositionY;
	@ArgumentsAnnotation(name="nestPositionZ", defaultValue="0")	
	double nestPositionZ;
	
	@ArgumentsAnnotation(name="densityofpreys", defaultValue="")
	private Nest nest;
	private int numberOfFoodSuccessfullyForaged = 0;
	private Random random;
	
	private Simulator simulator;
	private Arguments args;

	public GatheringTrailForageEnvironment(Simulator simulator, Arguments arguments) {
		super(simulator, arguments);
		this.simulator = simulator;
		this.args = arguments;
		nestLimit       = arguments.getArgumentIsDefined("nestlimit") ? arguments.getArgumentAsDouble("nestlimit")       : .5;
		forageLimit     = arguments.getArgumentIsDefined("foragelimit") ? arguments.getArgumentAsDouble("foragelimit")       : 2.0;
		forbiddenArea   = arguments.getArgumentIsDefined("forbiddenarea") ? arguments.getArgumentAsDouble("forbiddenarea") :1;
		preyPositionX   = arguments.getArgumentIsDefined("preyPositionX") ? arguments.getArgumentAsDouble("preyPositionX") :0;
		preyPositionY   = arguments.getArgumentIsDefined("preyPositionY") ? arguments.getArgumentAsDouble("preyPositionY") :0;
		preyPositionZ   = arguments.getArgumentIsDefined("preyPositionZ") ? arguments.getArgumentAsDouble("preyPositionZ") :0;
		nestPositionX   = arguments.getArgumentIsDefined("nestPositionX") ? arguments.getArgumentAsDouble("nestPositionX") :-1;
		nestPositionY   = arguments.getArgumentIsDefined("nestPositionY") ? arguments.getArgumentAsDouble("nestPositionY") :0;
		nestPositionZ   = arguments.getArgumentIsDefined("nestPositionZ") ? arguments.getArgumentAsDouble("nestPositionZ") :0;

	}
	
	@Override
	public void setup(Simulator simulator) {
		super.setup(simulator);
		
		if(simulator.getRobots().size() == 1) {
			Robot r = simulator.getRobots().get(0);
			r.setPosition(0, 0, 0);
			r.setOrientation(0);
		}
		
		this.random = simulator.getRandom();
		
		if(args.getArgumentIsDefined("densityofpreys")){
			double densityoffood = args.getArgumentAsDouble("densityofpreys");
			numberOfPreys = (int)(densityoffood*Math.PI*forageLimit*forageLimit+.5);
		} else {
			numberOfPreys = args.getArgumentIsDefined("numberofpreys") ? args.getArgumentAsInt("numberofpreys") : 20;
		}
		//Creating a Spot for the Prey to spawn in
	    VectorLine preyPosition = new VectorLine(0,0,0);
		
		for(int i = 0; i < numberOfPreys; i++ ){
			addPrey(new Prey(simulator, "Prey "+i, new3DRandomPositionFromSpawnLocation(preyPosition), 0, PREY_MASS, PREY_RADIUS));
		}
		//nest is only 3D for effectiveness measure
		nest = new Nest(simulator, "Nest", nestPositionX, nestPositionY,nestPositionZ, nestLimit);
		nest.setColor(Color.GREEN);
		addObject(nest);
	}
	//if 2D
	private VectorLine newRandomPosition() {
		double radius = random.nextDouble()*(forageLimit-nestLimit)+nestLimit*1.1;
		double angle = random.nextDouble()*2*Math.PI;
		return new VectorLine(radius*Math.cos(angle),radius*Math.sin(angle));
	}
	
	//if 3D
	private VectorLine new3DRandomPositionFromSpawnLocation(VectorLine v) {
		double radius = random.nextDouble()*0.5;
		double angle = random.nextDouble()*2*Math.PI;
		double depthplacement = 0;
		if(is3D==true) {
			depthplacement = (random.nextDouble());	
		}
		return new VectorLine(Math.max(v.x+radius*Math.cos(angle),forbiddenArea),Math.max(v.y+radius*Math.sin(angle),forbiddenArea),Math.max(v.z+depthplacement,forbiddenArea));
	}
	
	@Override
	public void update(double time) {
//		nest.shape.getClosePrey().update(time, teleported);
//		CloseObjectIterator i = nest.shape.getClosePrey().iterator();
		for (Prey nextPrey : simulator.getEnvironment().getPrey()) {
			double distance;
			if(!getCylinderNest()) { distance = nextPrey.getPosition().distanceTo(nest.getPosition()); }
			else { distance = nextPrey.getPosition().distanceToIgnoring3D(nest.getPosition()); }
			if(nextPrey.isEnabled() && distance < nestLimit){
				if(distance == 0){
					 System.out.println("ERRO--- zero");
				}
				VectorLine preyPosition = new VectorLine(preyPositionX,preyPositionY,preyPositionZ);
				nextPrey.teleportTo(new3DRandomPositionFromSpawnLocation(preyPosition));
				numberOfFoodSuccessfullyForaged++;
			}
		}
		
		for(Robot robot: robots){
			PreyCarriedSensor sensor = (PreyCarriedSensor)robot.getSensorByType(PreyCarriedSensor.class);
			if (sensor != null && sensor.preyCarried() && robot.isInvolvedInCollison()){
				PreyPickerActuator actuator = (PreyPickerActuator)robot.getActuatorByType(PreyPickerActuator.class);
				if(actuator != null) {
					Prey preyToDrop = actuator.dropPrey();
					VectorLine preyPosition = new VectorLine(preyPositionX,preyPositionY,preyPositionZ);
					preyToDrop.teleportTo(new3DRandomPositionFromSpawnLocation(preyPosition));
				}
			}
		}
	}
	
	public int getNumberOfFoodSuccessfullyForaged() {
		return numberOfFoodSuccessfullyForaged;
	}

	public double getNestRadius() {
		return nestLimit;
	}

	public double getForageRadius() {
		return forageLimit;
	}

	public double getForbiddenArea() {
		return forbiddenArea;
	}
}

package simulation.environment;

import java.awt.Color;
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
import simulation.robot.sensors.PreyCarriedSensor;
import simulation.util.Arguments;
import simulation.util.ArgumentsAnnotation;

public class TwoNestForageEnvironment extends Environment {

	protected static final double PREY_RADIUS = 0.025;
	protected static final double PREY_MASS = 1;
	
	@ArgumentsAnnotation(name="nestlimit", defaultValue="0.5")
	private double nestLimit;
	
	@ArgumentsAnnotation(name="foragelimit", defaultValue="2.0")
	protected double forageLimit;
	
	@ArgumentsAnnotation(name="forbiddenarea", defaultValue="7.0")
	private	double forbiddenArea;
	
	@ArgumentsAnnotation(name="nestdistance", defaultValue="2.0")
	protected double nestDistance;
	
	@ArgumentsAnnotation(name="wallsExistance", defaultValue="false")
	protected boolean wallsExistance;
	
	protected int amountOfFood;
	protected Nest nestA;
	protected Nest nestB;
	protected int numberOfFoodSuccessfullyForagedNestA = 0;
	protected int numberOfFoodSuccessfullyForagedNestB = 0;
	protected VectorLine center = new VectorLine(0, 0);
	protected Random random;

	public TwoNestForageEnvironment(Simulator simulator, Arguments arguments) {
		super(simulator, arguments);

		nestDistance = arguments.getArgumentIsDefined("nestdistance") ? arguments.getArgumentAsDouble("nestdistance") : 2;
		nestLimit = arguments.getArgumentIsDefined("nestlimit") ? arguments.getArgumentAsDouble("nestlimit") : .5;
		forageLimit = arguments.getArgumentIsDefined("foragelimit") ? arguments.getArgumentAsDouble("foragelimit") : 2.0;
		forbiddenArea = arguments.getArgumentIsDefined("forbiddenarea") ? arguments.getArgumentAsDouble("forbiddenarea") : 5.0;

		this.random = simulator.getRandom();
		
		if (arguments.getArgumentIsDefined("densityoffood")) {
			double densityoffood = arguments
					.getArgumentAsDouble("densityoffood");
			amountOfFood = (int) (densityoffood * Math.PI * forageLimit
					* forageLimit + .5);
		} else {
			amountOfFood = arguments.getArgumentIsDefined("amountfood") ? arguments
					.getArgumentAsInt("amountfood") : 20;
		}
	}
	
	@Override
	public void setup(Simulator simulator) {
		super.setup(simulator);
		nestA = new Nest(simulator, "NestA", -nestDistance / 2, 0, nestLimit);
		nestA.setParameter("TEAM", 1);
		nestA.setPreyAllowed(false,"A");
		nestA.setColor(Color.BLUE);
		nestB = new Nest(simulator, "NestB", nestDistance / 2, 0, nestLimit);
		nestB.setParameter("TEAM", 2);
		nestB.setPreyAllowed(false,"B");
		nestB.setColor(Color.RED);
		addObject(nestA);
		addObject(nestB);

		deployPreys(simulator);	
		//if(wallsExistance) {
			deployWalls(simulator);
		//}
	}

	protected void deployWalls(Simulator simulator) {
		for (int i = 0; i < 1; i++) {
			addStaticObject(new Wall(simulator, newRandomPosition(),0.2,0.5));
		}
	}
	
	protected void deployPreys(Simulator simulator) {
		for (int i = 0; i < getAmoutOfFood(); i++) {
			int temp = (Math.random() <= 0.5) ? 1 : 2;
			Prey p;
			if(temp == 1) {
				addPrey(p = new Prey(simulator, "Prey " + i, newRandomPosition(), 0,
					PREY_MASS, PREY_RADIUS,"A"));
				p.setColor(Color.red);
			}
			else {
				addPrey(p = new Prey(simulator, "Prey " + i, newRandomPosition(), 0,
						PREY_MASS, PREY_RADIUS,"B"));
				p.setColor(Color.blue);				
			}
		}
	}

	protected int getAmoutOfFood() {
		return amountOfFood;
	}

	protected VectorLine newRandomPosition() {
		VectorLine position;
		do {
			double radius = random.nextDouble() * (forageLimit);
			double angle = random.nextDouble() * 2 * Math.PI;
			position = new VectorLine(radius * Math.cos(angle), radius
					* Math.sin(angle));
		} while (position.distanceTo(nestA.getPosition()) < nestA.getRadius()
				|| position.distanceTo(nestB.getPosition()) < nestB.getRadius());
		return position;
	}

	@Override
	public void update(double time) {
		nestA.shape.getClosePrey().update(time, teleported);
		nestB.shape.getClosePrey().update(time, teleported);
		dropPreysDueToCollison();
		numberOfFoodSuccessfullyForagedNestA += calculateNewForagePrey(nestA);
		numberOfFoodSuccessfullyForagedNestB += calculateNewForagePrey(nestB);

		if (numberOfFoodSuccessfullyForagedNestB
				+ numberOfFoodSuccessfullyForagedNestA >= amountOfFood) {
//			simulator.getExperiment().endExperiment();
		}
	}

	protected void dropPreysDueToCollison() {
		for(Robot robot: robots){
			PreyCarriedSensor sensor = (PreyCarriedSensor)robot.getSensorByType(PreyCarriedSensor.class);
			if (sensor.preyCarried() && robot.isInvolvedInCollison()){
				PreyPickerActuator actuator = (PreyPickerActuator)robot.getActuatorByType(PreyPickerActuator.class);
				Prey preyToDrop = actuator.dropPrey();
				preyToDrop.teleportTo(newRandomPosition());
			}
		}
	}

	protected int calculateNewForagePrey(Nest nest) {
		CloseObjectIterator i = nest.shape.getClosePrey().iterator();
		int numberOfFoodSuccessfullyForaged = 0;
		VectorLine nestPosition = nest.getPosition();
		while (i.hasNext()) {
			PhysicalObjectDistance preyDistance = i.next();
			Prey nextPrey = (Prey) (preyDistance.getObject());
			double distance = nextPrey.getPosition().distanceTo(nestPosition);
			if (nextPrey.isEnabled() && distance < nestLimit) {
				if (distance == 0) {
					System.out.println("ERRO--- zero");
				}
				// Fixed number of prey
				// nextPrey.teleportTo(newRandomPosition());
				if(nest.getPreyAllowance()||nest.getPreytype() == nextPrey.getPreyType()) {
					numberOfFoodSuccessfullyForaged++;					
				}
				nextPrey.setEnabled(false);
			}
			i.updateCurrentDistance(distance);
		}
		return numberOfFoodSuccessfullyForaged;
	}

	public int getNumberOfFoodSucessfullyForagedNest(int nestNumber) {
		if(nestNumber == 0) {
			return numberOfFoodSuccessfullyForagedNestA;
		}
		else {
			return numberOfFoodSuccessfullyForagedNestB;
		}
	}
	
	public int getNumberOfFoodSuccessfullyForagedNestA() {
		return numberOfFoodSuccessfullyForagedNestA;
	}

	public int getNumberOfFoodSuccessfullyForagedNestB() {
		return numberOfFoodSuccessfullyForagedNestB;
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

	public VectorLine getNestAPosition() {
		return nestA.getPosition();
	}

	public VectorLine getNestBPosition() {
		return nestB.getPosition();
	}
}
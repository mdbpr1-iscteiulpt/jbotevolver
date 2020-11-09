package evolutionaryrobotics.evaluationfunctions;

import mathutils.VectorLine;
import simulation.Simulator;
import simulation.environment.TwoNestForageEnvironment;
import simulation.robot.Robot;
import simulation.robot.sensors.PreyCarriedSensor;
import simulation.util.Arguments;

public class TwoNestForagingEvaluationFunction extends EvaluationFunction{
	protected VectorLine   nestOnePosition = new VectorLine(0, 0);
	protected VectorLine   nestTwoPosition = new VectorLine(0, 0);	
	protected int numberOfFoodAForaged = 0;
	protected int numberOfFoodBForaged = 0;

	public TwoNestForagingEvaluationFunction(Arguments args) {
		super(args);	
	}

	//@Override
	public double getFitness() {
		return fitness + numberOfFoodAForaged + numberOfFoodBForaged;
	}

	//@Override
	public void update(Simulator simulator) {			
		int numberOfRobotsWithPrey       = 0;
		int numberOfRobotsBeyondForbidenLimit       = 0;
		int numberOfRobotsBeyondForagingLimit       = 0;
		nestOnePosition = ((TwoNestForageEnvironment)(simulator.getEnvironment())).getNestAPosition();
		nestTwoPosition = ((TwoNestForageEnvironment)(simulator.getEnvironment())).getNestBPosition();
		double forbidenArea =  ((TwoNestForageEnvironment)(simulator.getEnvironment())).getForbiddenArea();
		double foragingArea =  ((TwoNestForageEnvironment)(simulator.getEnvironment())).getForageRadius();	

		for(Robot r : simulator.getEnvironment().getRobots()){
			double distanceToANest = r.getPosition().distanceTo(nestOnePosition);
			double distanceToBNest = r.getPosition().distanceTo(nestTwoPosition);
			
			if(distanceToANest > forbidenArea||distanceToBNest > forbidenArea){
				numberOfRobotsBeyondForbidenLimit++;
			} else 	if(distanceToANest > foragingArea||distanceToBNest > foragingArea){
				numberOfRobotsBeyondForagingLimit++;
			}
			
			if (((PreyCarriedSensor)r.getSensorByType(PreyCarriedSensor.class)).preyCarried()) {
				numberOfRobotsWithPrey++;
			}
		}
		fitness += (double) numberOfRobotsWithPrey * 0.001 + numberOfRobotsBeyondForbidenLimit * -0.1 + numberOfRobotsBeyondForagingLimit * -0.0001;
		numberOfFoodAForaged = ((TwoNestForageEnvironment)(simulator.getEnvironment())).getNumberOfFoodSucessfullyForagedNest(0);
		numberOfFoodBForaged = ((TwoNestForageEnvironment)(simulator.getEnvironment())).getNumberOfFoodSucessfullyForagedNest(1);
	}
}
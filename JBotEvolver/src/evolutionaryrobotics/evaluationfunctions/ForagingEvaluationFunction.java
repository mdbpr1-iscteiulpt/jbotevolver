package evolutionaryrobotics.evaluationfunctions;

import mathutils.VectorLine;
import simulation.Simulator;
import simulation.environment.GatheringTrailForageEnvironment;
import simulation.environment.RoundForageEnvironment;
import simulation.robot.Robot;
import simulation.robot.sensors.PreyCarriedSensor;
import simulation.util.Arguments;

public class ForagingEvaluationFunction extends EvaluationFunction{
	protected VectorLine   nestPosition = new VectorLine(0, 0,0);
	protected int numberOfFoodForaged = 0;

	public ForagingEvaluationFunction(Arguments args) {
		super(args);	
	}

	//@Override
	public double getFitness() {
		return fitness + numberOfFoodForaged;
	}
 
	//@Override
	public void update(Simulator simulator) {			
		int numberOfRobotsWithPrey  	      = 0;
		int numberOfRobotsBeyondForbidenLimit       = 0;
		int numberOfRobotsBeyondForagingLimit       = 0;
		
		double forbidenArea = 0;
		double foragingArea = 0;
		
		if(simulator.getEnvironment() instanceof RoundForageEnvironment) {
			forbidenArea =  ((RoundForageEnvironment)(simulator.getEnvironment())).getForbiddenArea();
			foragingArea =  ((RoundForageEnvironment)(simulator.getEnvironment())).getForageRadius();	
		}
		if(simulator.getEnvironment() instanceof GatheringTrailForageEnvironment) {
			forbidenArea =  ((GatheringTrailForageEnvironment)(simulator.getEnvironment())).getForbiddenArea();
			foragingArea =  ((GatheringTrailForageEnvironment)(simulator.getEnvironment())).getForageRadius();	
		}
		
		for(Robot r : simulator.getEnvironment().getRobots()){
			double distanceToNest = r.getPosition().distanceTo(nestPosition);
			VectorLine l = r.getPosition();
			if(distanceToNest > forbidenArea){
				numberOfRobotsBeyondForbidenLimit++;
			} else 	if(distanceToNest > foragingArea){
				numberOfRobotsBeyondForagingLimit++;
			}
			
			if (((PreyCarriedSensor)r.getSensorByType(PreyCarriedSensor.class)).preyCarried()) {
				numberOfRobotsWithPrey++;
			}
		}
		fitness += (double) numberOfRobotsWithPrey * 0.001 + numberOfRobotsBeyondForbidenLimit * -0.1 + numberOfRobotsBeyondForagingLimit * -0.0001;
		if(simulator.getEnvironment() instanceof RoundForageEnvironment) {
			numberOfFoodForaged = ((RoundForageEnvironment)(simulator.getEnvironment())).getNumberOfFoodSuccessfullyForaged();
		}
		if(simulator.getEnvironment() instanceof GatheringTrailForageEnvironment) {
			numberOfFoodForaged = ((GatheringTrailForageEnvironment)(simulator.getEnvironment())).getNumberOfFoodSuccessfullyForaged();
		}		
	}
}
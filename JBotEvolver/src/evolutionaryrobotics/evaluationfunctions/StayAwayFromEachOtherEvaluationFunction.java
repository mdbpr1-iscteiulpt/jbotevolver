package evolutionaryrobotics.evaluationfunctions;

import mathutils.VectorLine;
import simulation.Simulator;
import simulation.environment.GatheringTrailForageEnvironment;
import simulation.environment.RoundForageEnvironment;
import simulation.robot.Robot;
import simulation.robot.sensors.PreyCarriedSensor;
import simulation.util.Arguments;

public class StayAwayFromEachOtherEvaluationFunction extends EvaluationFunction{
	protected VectorLine   nestPosition = new VectorLine(0, 0,0);
	protected int numberOfRobotsThatCollided = 0;
	protected int numberOfRobotsDistanced = 0;
	
	public StayAwayFromEachOtherEvaluationFunction(Arguments args) {
		super(args);	
	}

	//@Override
	public double getFitness() {
		return fitness - numberOfRobotsThatCollided + numberOfRobotsDistanced;
	}
 
	//@Override
	public void update(Simulator simulator) {			
		int numberOfRobotsThatCollided       = 0;
		int numberOfRobotsThatAlmostCollided       = 0;
		
		//numberOfRobotsThatCollided =  ((GatheringTrailForageEnvironment)(simulator.getEnvironment()));
		
		for(Robot r : simulator.getEnvironment().getRobots()){
			double distanceToNest = r.getPosition().distanceTo(nestPosition);
			VectorLine l = r.getPosition();
			
			if (((PreyCarriedSensor)r.getSensorByType(PreyCarriedSensor.class)).preyCarried()) {
			}
		}
		fitness += (double) 0 * 0.001 + 0 * -0.1 + 0 * -0.0001;
		if(simulator.getEnvironment() instanceof RoundForageEnvironment) {
			numberOfRobotsThatCollided = ((RoundForageEnvironment)(simulator.getEnvironment())).getNumberOfFoodSuccessfullyForaged();
		}
	}
}
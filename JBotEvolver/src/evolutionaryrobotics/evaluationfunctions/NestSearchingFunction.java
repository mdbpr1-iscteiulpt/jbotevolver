package evolutionaryrobotics.evaluationfunctions;

import mathutils.VectorLine;
import simulation.Simulator;
import simulation.environment.GatheringTrailForageEnvironment;
import simulation.environment.MaintainDistanceEnvironment;
import simulation.environment.RoundForageEnvironment;
import simulation.environment.SearchNestMazeEnvironment;
import simulation.robot.Robot;
import simulation.robot.sensors.PreyCarriedSensor;
import simulation.util.Arguments;

public class NestSearchingFunction extends EvaluationFunction{
	protected VectorLine   nestPosition = new VectorLine(0, 2.5,0);
	protected int numberOfFoodForaged = 0;

	public NestSearchingFunction(Arguments args) {
		super(args);	
	}

	//@Override
	public double getFitness() {
		return fitness;
	}
 
	//@Override
	public void update(Simulator simulator) {			
		int numberOfRobotsInsideNest       = 0;
		int numberOfRobotsThatCollided       = 0;
		double shortestdistanceToNest = 100;
		
		double forbidenArea = 0;
		double foragingArea = 0;
		
		SearchNestMazeEnvironment environment =  ((SearchNestMazeEnvironment)(simulator.getEnvironment()));
		
		if(simulator.getEnvironment() instanceof SearchNestMazeEnvironment) {
			numberOfRobotsThatCollided = environment.getNumberOfRobotsThatCollided();
		}
		
		
		for(Robot r : simulator.getEnvironment().getRobots()){
			double distanceToNest = r.getPosition().distanceTo(nestPosition);
			if(shortestdistanceToNest <= 100) { shortestdistanceToNest = distanceToNest; }
		}
		
		
		fitness += (double) -0.001 * numberOfRobotsThatCollided + Math.min(-0.001, -0.0005 * shortestdistanceToNest);
	}
}
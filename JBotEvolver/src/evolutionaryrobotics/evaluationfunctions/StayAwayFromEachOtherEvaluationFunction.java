package evolutionaryrobotics.evaluationfunctions;

import mathutils.VectorLine;
import simulation.Simulator;
import simulation.environment.GatheringTrailForageEnvironment;
import simulation.environment.MaintainDistanceEnvironment;
import simulation.environment.RoundForageEnvironment;
import simulation.robot.FlightDroneRobot;
import simulation.robot.Robot;
import simulation.robot.sensors.PreyCarriedSensor;
import simulation.util.Arguments;

public class StayAwayFromEachOtherEvaluationFunction extends EvaluationFunction{
	protected VectorLine   nestPosition = new VectorLine(0, 0,0);
	protected int numberOfRobotsThatCollided = 0;
	protected int numberOfRobotsDistanced = 0;
	protected int numberOfRobotsDanger = 0;
	protected int numberOfRobotsBeyondForbidenLimit = 0;
	protected int numberOfRobots = 1;
	protected int numbersOfRobotStopped = 0;
	
	public StayAwayFromEachOtherEvaluationFunction(Arguments args) {
		super(args);	
	}

	//@Override
	public double getFitness() {
		return (fitness - numberOfRobotsThatCollided + numberOfRobotsDistanced)/numberOfRobots;
	}
 
	//@Override
	public void update(Simulator simulator) {			
		int numberOfRobotsThatCollided       = 0;
		int numberOfRobotsDanger       = 0;
		int numberOfRobotsDistanced       = 0;
		int numbersOfRobotStopped       = 0;
		
		double forbidenArea = 0;
		
		MaintainDistanceEnvironment environment =  ((MaintainDistanceEnvironment)(simulator.getEnvironment()));
		forbidenArea =  ((MaintainDistanceEnvironment)(simulator.getEnvironment())).getForbiddenArea();	
		numberOfRobotsDanger =  environment.getInDangerArea();
		numberOfRobotsDistanced =  environment.getInSafeArea();
		numberOfRobots = simulator.getEnvironment().getRobots().size();
		for(Robot r : simulator.getEnvironment().getRobots()){
			double distanceToCenter = r.getPosition().distanceTo(new VectorLine(0,0,0));
			if(distanceToCenter > forbidenArea){
				numberOfRobotsBeyondForbidenLimit++;
			}
		}
		fitness += (double) numberOfRobotsDistanced * 0.001 + numberOfRobotsBeyondForbidenLimit * -0.1 + numberOfRobotsDanger * -0.0001;
		if(simulator.getEnvironment() instanceof MaintainDistanceEnvironment) {
			numberOfRobotsThatCollided = ((MaintainDistanceEnvironment)(simulator.getEnvironment())).getNumberOfRobotsThatCollided();
			numberOfRobotsDistanced = ((MaintainDistanceEnvironment)(simulator.getEnvironment())).getInSafeArea();

		}
	}
}
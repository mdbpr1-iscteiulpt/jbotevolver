package evolutionaryrobotics.evaluationfunctions;

import mathutils.VectorLine;
import simulation.Simulator;
import simulation.environment.GatheringTrailForageEnvironment;
import simulation.environment.MaintainDistanceEnvironment;
import simulation.environment.RoundForageEnvironment;
import simulation.robot.DifferentialDriveRobot;
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
	protected int robotsBellowSpeed = 0;
	protected int numberOfRobots = 1;
	protected int numbersOfRobotStopped = 0;
	
	public StayAwayFromEachOtherEvaluationFunction(Arguments args) {
		super(args);	
	}

	//@Override
	public double getFitness() {
		return (fitness);
	}
 
	//@Override
	public void update(Simulator simulator) {			
		numberOfRobotsThatCollided       = 0;
		numberOfRobotsDanger       = 0;
		numberOfRobotsDistanced       = 0;
		numbersOfRobotStopped       = 0;
		robotsBellowSpeed = 0;
		
		double forbidenArea = 0;
		
		MaintainDistanceEnvironment environment =  ((MaintainDistanceEnvironment)(simulator.getEnvironment()));
		forbidenArea =  ((MaintainDistanceEnvironment)(simulator.getEnvironment())).getForbiddenArea();	
		numberOfRobotsDanger =  environment.getInDangerArea();
		numberOfRobotsDistanced =  environment.getInSafeArea();
		numberOfRobots = simulator.getEnvironment().getRobots().size();
		for(Robot r : simulator.getEnvironment().getRobots()){
			double distanceToCenter = r.getPosition().distanceTo(new VectorLine(0,0,0));
			if(r instanceof FlightDroneRobot) {
				if(((FlightDroneRobot) r).getForwardSpeed() < 0.2)
					robotsBellowSpeed++;
			}
			if(r instanceof DifferentialDriveRobot) {
				if(((DifferentialDriveRobot) r).getLeftWheelSpeed() + ((DifferentialDriveRobot) r).getLeftWheelSpeed()<0.2)
					robotsBellowSpeed++;
			}
			/*if(r.getStopped()) {
				numberOfRobotsThatCollided++;
			}*/
		}
		if(simulator.getEnvironment() instanceof MaintainDistanceEnvironment) {
			numberOfRobotsThatCollided = environment.getNumberOfRobotsThatCollided();
			numberOfRobotsDistanced = environment.getInSafeArea();
			numberOfRobotsDanger =  environment.getInDangerArea();
		}
		fitness += (double) numberOfRobotsDistanced * 0.001 + robotsBellowSpeed * -0.0005 + numberOfRobotsDanger * -0.0008 + numberOfRobotsThatCollided * -0.001;
	}
}
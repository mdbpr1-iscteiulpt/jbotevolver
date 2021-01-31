package evolutionaryrobotics.evaluationfunctions;

import java.util.ArrayList;
import java.util.Iterator;

import mathutils.VectorLine;
import simulation.Simulator;
import simulation.environment.GatheringTrailForageEnvironment;
import simulation.environment.MaintainDistanceEnvironment;
import simulation.environment.PreySearchingEnvironment;
import simulation.environment.RoundForageEnvironment;
import simulation.physicalobjects.PhysicalObject;
import simulation.physicalobjects.Prey;
import simulation.physicalobjects.Wall;
import simulation.robot.DifferentialDriveRobot;
import simulation.robot.FlightDroneRobot;
import simulation.robot.Robot;
import simulation.robot.sensors.PreyCarriedSensor;
import simulation.robot.sensors.PreySensor;
import simulation.robot.sensors.Sensor;
import simulation.util.Arguments;

public class PreySearchingWithWallsFunction extends EvaluationFunction{
	protected VectorLine   nestPosition1 = new VectorLine(0, -1.5,0);
	protected VectorLine   nestPosition2 = new VectorLine(0, 1.5,0);
	protected double forbiddenLimit = 4;
	protected int numberOfPreyCollected = 0;
	protected int numberOfRobotsCollided = 0;
	protected int numberOfRobotsOutofBounds = 0;
	protected int numberOfRobotsBeyondForbidenLimit = 0;
	protected int robotsBellowSpeed = 0;
	protected int numberOfRobots = 1;
	protected int numbersOfRobotStopped = 0;
	
	public PreySearchingWithWallsFunction(Arguments args) {
		super(args);	
	}

	//@Override
	public double getFitness() {
		return (fitness + numberOfPreyCollected);
	}
 
	//@Override
	public void update(Simulator simulator) {			
		numberOfPreyCollected       = 0;
		numberOfRobotsOutofBounds       = 0;
		numberOfRobotsCollided       = 0;
		numbersOfRobotStopped       = 0;
		robotsBellowSpeed = 0;
		numberOfRobotsBeyondForbidenLimit = 0;
		
		PreySearchingEnvironment environment =  ((PreySearchingEnvironment)(simulator.getEnvironment()));
		numberOfRobotsCollided =  environment.getInSafeArea();
		numberOfRobots = environment.getRobots().size();
		double nestArea = environment.getNestArea();
		double minimumDistanceFromPrey = 100000;
		double minimumDistanceFromWall = 100000;
		for(Robot r : simulator.getEnvironment().getRobots()){
			VectorLine l = r.getPosition();
			if(r instanceof FlightDroneRobot) {
				if(((FlightDroneRobot) r).getForwardSpeed() < 0.15)
					robotsBellowSpeed++;
			}
			if(r instanceof DifferentialDriveRobot) {
				if(((DifferentialDriveRobot) r).getLeftWheelSpeed() + ((DifferentialDriveRobot) r).getLeftWheelSpeed()<0.15)
					robotsBellowSpeed++;
			}
			double distanceToNest1 = l.distanceTo(nestPosition1);
			double distanceToNest2 = l.distanceTo(nestPosition2);
			if(distanceToNest1<nestArea||distanceToNest2<nestArea) {
				numberOfRobotsBeyondForbidenLimit++;
			}
			if(l.distanceTo(new VectorLine(0,0,0))>forbiddenLimit){
				numberOfRobotsOutofBounds++;	
			}
			ArrayList<Prey> listPrey = environment.getPrey();
			for(Prey p: listPrey){
				if(p.getPosition().distanceTo(l)<minimumDistanceFromPrey) { minimumDistanceFromPrey = p.getPosition().distanceTo(l); }
			}
			//System.out.println(minimumDistanceFromPrey);
			
			ArrayList<Wall> listWall = environment.getWalls();
			for(Wall w: listWall){
				Double Width = w.getWidth(); Double Lenght = w.getLenght(); Double Height = w.getHeight();
				VectorLine evaluatedPoint = new VectorLine(); evaluatedPoint.set(l);
				evaluatedPoint.set(Math.max(w.getPosition().x - Width/2, Math.min(evaluatedPoint.x, w.getPosition().x + Width/2)),Math.max(w.getPosition().y - Lenght/2, Math.min(evaluatedPoint.y, w.getPosition().y + Lenght/2)),Math.max(w.getPosition().z - Height/2, Math.min(evaluatedPoint.z, w.getPosition().z + Height/2)));
				if(evaluatedPoint.distanceTo(l)<r.getRadius()/2) { 
					numberOfRobotsCollided++; 
				}
			}
		}
		if(simulator.getEnvironment() instanceof MaintainDistanceEnvironment) {
			numberOfPreyCollected = environment.getNumberOfPreyCollected();
			numberOfRobotsCollided = environment.getInSafeArea();
		}
		fitness += (double) robotsBellowSpeed * -0.005 +  numberOfRobotsCollided * -0.05 + numberOfRobotsBeyondForbidenLimit * -0.01;
	}
}
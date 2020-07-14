package simulation.robot.actuators;

import java.util.Random;

import simulation.Simulator;
import simulation.robot.FlightDroneRobot;
import simulation.robot.Robot;
import simulation.util.Arguments;
import simulation.util.ArgumentsAnnotation;

public class FlightDroneActuator extends Actuator {

	public static final float NOISESTDEV = 0.05f;

	protected double fowardSpeed = 0;
	//protected double rightSpeed = 0;
	protected Random random;
	@ArgumentsAnnotation(name="maxspeed", defaultValue = "0.1")
	protected double maxSpeed;
	
	public FlightDroneActuator(Simulator simulator, int id, Arguments arguments) {
		super(simulator, id, arguments);
		this.random = simulator.getRandom();
		this.maxSpeed = arguments.getArgumentAsDoubleOrSetDefault("maxspeed", 0.1);
	}

	public void setForwardSpeed(double value) {
		fowardSpeed = (value - 0.5) * maxSpeed * 2.0;
	}


	@Override
	public void apply(Robot robot, double timeDelta) {
		fowardSpeed*= (1 + random.nextGaussian() * NOISESTDEV);

		if (fowardSpeed < -maxSpeed)
			fowardSpeed = -maxSpeed;
		else if (fowardSpeed > maxSpeed)
			fowardSpeed = maxSpeed;
		
		((FlightDroneRobot) robot).setForwardSpeed(fowardSpeed);
	}

	@Override
	public String toString() {
		return "TwoWheelActuator [fowardSpeed=" + fowardSpeed;
	}
	
	public double getMaxSpeed() {
		return maxSpeed;
	}
	
	public double getSpeed(){
		return fowardSpeed;
	}
	
	public double getSpeedPrecentage(){
		double fowardSpeedper = fowardSpeed/maxSpeed;
		
		return fowardSpeedper;
	}
	
}
package simulation.robot.actuators;

import java.util.Random;
import net.jafama.FastMath;
import simulation.Simulator;
import simulation.robot.FlightDroneRobot;
import simulation.robot.Robot;
import simulation.util.Arguments;
import simulation.util.ArgumentsAnnotation;

public class FlightDroneActuator extends Actuator {

	public static final float NOISESTDEV = 0.05f;

	protected double fowardSpeed = 0;
	protected double rotatingSpeed = 0;
	protected double flightSpeed = 0;
	protected Random random;
	@ArgumentsAnnotation(name="maxspeed", defaultValue = "0.10")
	protected double maxSpeed;
	@ArgumentsAnnotation(name="maxFlightSpeed", defaultValue = "0.10")
	protected double maxFlightSpeed;
	@ArgumentsAnnotation(name="maxRotationSpeed", defaultValue = "PI/4")
	protected double maxRotationSpeed;
	
	public FlightDroneActuator(Simulator simulator, int id, Arguments arguments) {
		super(simulator, id, arguments);
		this.random = simulator.getRandom();
		this.maxSpeed = arguments.getArgumentAsDoubleOrSetDefault("maxspeed", 0.1);
		this.maxFlightSpeed = arguments.getArgumentAsDoubleOrSetDefault("maxFlightSpeed", 0.1);
		this.maxRotationSpeed = arguments.getArgumentAsDoubleOrSetDefault("maxRotationSpeed", Math.PI/4);
	}

	public void setForwardSpeed(double value) {
		fowardSpeed = (value-0.5) * maxSpeed * 2;
	}
	
	public void setFlightSpeed(double value) {
		flightSpeed = (value - 0.5) * maxFlightSpeed * 2;
	}
	
	//Gotta set limits
	public void setRotationSpeed(double value) {
		rotatingSpeed = (value-0.5) * maxRotationSpeed;
	}


	@Override
	public void apply(Robot robot, double timeDelta) {
		fowardSpeed*= (1 + random.nextGaussian() * NOISESTDEV);
		rotatingSpeed*= (1 + random.nextGaussian() * NOISESTDEV);
		flightSpeed*= (1 + random.nextGaussian() * NOISESTDEV);
		if (fowardSpeed < -maxSpeed)
			fowardSpeed = -maxSpeed;
		else if (fowardSpeed > maxSpeed)
			fowardSpeed = maxSpeed;
		((FlightDroneRobot) robot).setForwardSpeed(fowardSpeed);	
		
		if (rotatingSpeed > maxRotationSpeed)
			rotatingSpeed = maxRotationSpeed;
		else if (rotatingSpeed < -maxRotationSpeed)
			rotatingSpeed = -maxRotationSpeed;		
		((FlightDroneRobot) robot).setRotationSpeed(rotatingSpeed);
		
		if (flightSpeed < -maxFlightSpeed)
			flightSpeed = -maxFlightSpeed;
		else if (flightSpeed > maxFlightSpeed)
			flightSpeed = maxFlightSpeed;	
		((FlightDroneRobot) robot).setFlightSpeed(flightSpeed);
	}

	@Override
	public String toString() {
		return "FlightDroneActuator [fowardSpeed=" + fowardSpeed;
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
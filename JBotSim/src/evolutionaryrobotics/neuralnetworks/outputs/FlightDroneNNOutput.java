package evolutionaryrobotics.neuralnetworks.outputs;

import simulation.robot.actuators.Actuator;
import simulation.robot.actuators.FlightDroneActuator;
import simulation.util.Arguments;

public class FlightDroneNNOutput extends NNOutput {
	private FlightDroneActuator flightDroneActuator;
	private double forwardSpeed = 0;
	private double rotatingSpeed = 0;
	private double flightSpeed = 0;
	
	public FlightDroneNNOutput(Actuator flightDroneActuator, Arguments args) {
		super(flightDroneActuator,args);
		this.flightDroneActuator = (FlightDroneActuator)flightDroneActuator;
	}
	
	@Override
	public int getNumberOfOutputValues() {
		return 3;
	}

	@Override
	public void setValue(int output, double value) {
		if (output == 0)
			forwardSpeed = value;
		if (output == 1)
			rotatingSpeed = value;
		if (output == 2)
			flightSpeed = value;
	}

	@Override
	public void apply() {
		flightDroneActuator.setForwardSpeed(forwardSpeed);
		flightDroneActuator.setRotationSpeed(rotatingSpeed);
		flightDroneActuator.setFlightSpeed(flightSpeed);
	}
}
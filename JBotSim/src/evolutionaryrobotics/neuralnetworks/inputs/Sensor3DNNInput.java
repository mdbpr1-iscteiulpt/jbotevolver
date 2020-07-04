package evolutionaryrobotics.neuralnetworks.inputs;

import simulation.robot.sensors.ConeTypeSensor;
import simulation.robot.sensors.Sensor;

public class Sensor3DNNInput extends NNInput {

	private SphereTypeSensor sphereTypeSensor;
	
	public Sensor3DNNInput(Sensor sensor) {
		super(sensor);
		
		if(sensor instanceof ConeTypeSensor)
			this.sphereTypeSensor = (SphereTypeSensor)sensor;
		else
			this.sensor = sensor;
	}
	
	@Override
	public int getNumberOfInputValues() {
		return sphereTypeSensor == null ? 1 : sphereTypeSensor.getNumberOfSensors();
	}

	@Override
	public double getValue(int index) {
		if(sphereTypeSensor == null){
			if(sensor.isEnabled())
				return sensor.getSensorReading(index);
			return 0;
		}else{
			if(sphereTypeSensor.isEnabled())
				return sphereTypeSensor.getSensorReading(index);
			return 0;
		}
	}
}
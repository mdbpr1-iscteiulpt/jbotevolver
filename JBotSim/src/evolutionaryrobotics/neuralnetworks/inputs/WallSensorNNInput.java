package evolutionaryrobotics.neuralnetworks.inputs;

import simulation.robot.sensors.WallLightTypeSensor;
import simulation.robot.sensors.Sensor;

public class WallSensorNNInput extends NNInput {

	private WallLightTypeSensor wallTypeSensor;
	
	public WallSensorNNInput(Sensor sensor) {
		super(sensor);
		
		if(sensor instanceof WallLightTypeSensor)
			this.wallTypeSensor = (WallLightTypeSensor)sensor;
		else
			this.sensor = sensor;
	}
	
	@Override
	public int getNumberOfInputValues() {
		return wallTypeSensor == null ? 1 : wallTypeSensor.getNumberOfSensors();
	}

	@Override
	public double getValue(int index) {
		if(wallTypeSensor == null){
			if(sensor.isEnabled()) {
				return sensor.getSensorReading(index);
			}
			return 0;
		}else{
			if(wallTypeSensor.isEnabled())
				return wallTypeSensor.getSensorReading(index);
			return 0;
		}
	}
}
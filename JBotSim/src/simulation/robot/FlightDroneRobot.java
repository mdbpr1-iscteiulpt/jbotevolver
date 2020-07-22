package simulation.robot;

import net.jafama.FastMath;
import mathutils.MathUtils;
import mathutils.VectorLine;
import simulation.Simulator;
import simulation.physicalobjects.collisionhandling.knotsandbolts.CircularShape;
import simulation.robot.actuators.Actuator;
import simulation.util.Arguments;
import simulation.util.ArgumentsAnnotation;

public class FlightDroneRobot extends Robot {
	/**
	 * Current speed of the flight.
	 */	
	protected double     flightspeed      = 0;

	/**
	 * Current speed and direction of the rotation.
	 */	
	protected double     rotatingspeed      = 0;
	
	/**
	 * Current fowardSpeed.
	 */	
	protected double     fowardspeed     = 0;
	
	protected int stopTimestep = 0;
	
	public FlightDroneRobot(Simulator simulator, Arguments args) {
		super(simulator, args);
	}
	
	public void updateActuators(Double time, double timeDelta) {	
		this.previousPosition = new VectorLine(position);
		
		if(stopTimestep <= 0) {
			double fowardX = FastMath.cos(orientationZ);
			double fowardY = FastMath.sin(orientationZ);
			position.set(
					position.getX() + timeDelta * (fowardX * fowardspeed),
					position.getY() + timeDelta * (fowardY * fowardspeed),
					position.getZ() + timeDelta * (flightspeed - 0.05)
					);
			
			orientationZ += timeDelta * rotatingspeed; 
	
			orientationZ = MathUtils.modPI2(orientationZ);
			
			
		}
		
		stopTimestep--;
		
		for(Actuator actuator: actuators){
			actuator.apply(this,timeDelta);
		}
	}

	/**
	 * Set the rotational speed of rotation
	 * 
	 * @param speed of the rotation (in radians)
	 */	
	public void setRotationSpeed(double rotation) {
		rotatingspeed = rotatingspeed + rotation * Math.PI;
	}

	
	/**
	 * Set the speed of the drone (in meters/second).
	 * 
	 * @param left the speed of the left wheel (in meters/second)
	 */
	public void setForwardSpeed(double speed) {
		fowardspeed  = speed;	
	}
	
	/**
	 * Set the flight speed of drone
	 * 
	 * @param speed of flight (in meters/second)
	 */	
	public void setFlightSpeed(double flight) {
		flightspeed = flight;
	}

	public double getRotatingSpeed() {
		return rotatingspeed;
	}
	
	public double getForwardSpeed() {
		return stopTimestep > 0 ? 0 : fowardspeed;
	}
	
	public double getFlightSpeed(){
		return stopTimestep > 0 ? 0 : flightspeed;
	}
	
	@Override
	public void stop() {
		super.stop();
		setForwardSpeed(0);
	}
	
	public void stopTimestep(int time) {
		this.stopTimestep = time;
		if(time > 0) {
			fowardspeed = 0;
			rotatingspeed = 0;
		}
	}
	
	public int getStopTimestep() {
		return stopTimestep;
	}
}
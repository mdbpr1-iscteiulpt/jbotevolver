package simulation.robot;

import net.jafama.FastMath;

import java.awt.Color;

import gui.util.AltimetryColor;
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
	protected double      flightspeed      = 0;

	/**
	 * Current speed and direction of the rotation.
	 */	
	protected double     rotatingspeed      = 0;
	
	/**
	 * Current fowardSpeed.
	 */	
	protected double     fowardspeed     = 0;
	
	/**
	 * Simulator limits for color representation
	 */
	protected double[] zLimits = new double[2];
	
	protected int stopTimestep = 0;
	
	public FlightDroneRobot(Simulator simulator, Arguments args) {
		super(simulator, args);
		is3D = true;
		zLimits[0] = simulator.getEnvironment().getHeight(); zLimits[1] = -simulator.getEnvironment().getHeight();
	}
	
	public void updateActuators(Double time, double timeDelta) {
		color = getColorAccordingToZ();
		this.previousPosition = new VectorLine(position);
		if(stopTimestep <= 0) {
			position.set(
					position.getX() + timeDelta * (FastMath.cos(orientationZ) * fowardspeed),
					position.getY() + timeDelta * (FastMath.sin(orientationZ) * fowardspeed),
					position.getZ() + timeDelta * (flightspeed)
					);
			
			orientationZ += timeDelta * rotatingspeed; 
	
			orientationZ = MathUtils.modPI2(orientationZ);
		}
		
		//Update Color According to new Z position!
		updateColorAccordingToZ();
		
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
		rotatingspeed = rotation;
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
	
	/**
	 * Set the color according to the z value
	 * Uses a rainbow color scheme to define the value
	 * 2+ -> Red; 1+ -> Yellow; 0+ or 0 -> Green; 0- -> Light Blue; 1- -> Dark Blue; 2- -> Purple
	 * @return
	 */
	private void updateColorAccordingToZ() {
		AltimetryColor colorRamp = new AltimetryColor();
		double ramplimits = (zLimits[0]-zLimits[1])/21;
		int position = Math.min(Math.max(0, (int)((this.position.z-zLimits[1])/ramplimits)), 20);
		Color colorchoice = colorRamp.getColorinLayer(position);
		double[] colorrgb = new double[3];
		colorrgb[0]=colorchoice.getRed();
		colorrgb[1]=colorchoice.getBlue();
		colorrgb[2]=colorchoice.getGreen();
		setBodyColor(colorchoice);
	}
	
	private Color getColorAccordingToZ() {
		AltimetryColor colorRamp = new AltimetryColor();
		double ramplimits = (zLimits[0]-zLimits[1])/21;
		int position = Math.min(Math.max(0, (int)((this.position.z-zLimits[1])/ramplimits)), 20);
		Color colorchoice = colorRamp.getColorinLayer(position);	
		return colorchoice;
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
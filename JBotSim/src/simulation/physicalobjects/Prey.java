package simulation.physicalobjects;

import java.awt.Color;

import gui.util.AltimetryColor;
import mathutils.VectorLine;
import simulation.Simulator;
import simulation.physicalobjects.collisionhandling.knotsandbolts.CircularShape;
import simulation.robot.Robot;

public class Prey extends MovableObject {

	private Robot holder;
	private String PreyType;
	private Color color;

	protected double[] zLimits = new double[2];
	
	public Prey(Simulator simulator,  String name, double x, double y, double angle, double mass, double radius) {
		super(simulator,name, x, y,0, angle,0, mass,0, PhysicalObjectType.PREY, null);
		zLimits[0] = simulator.getEnvironment().getHeight(); zLimits[1] = -simulator.getEnvironment().getHeight();
		updateColorAccordingToZ();
		this.shape = new CircularShape(simulator, name + "CollisionObject", this, 0, 0, 0, 2 * radius, radius);
	}

	public Prey(Simulator simulator,  String name, double x, double y,double z, double angle, double mass, double radius) {
		super(simulator,name, x, y,z ,0, angle,0, mass, PhysicalObjectType.PREY, null);
		zLimits[0] = simulator.getEnvironment().getHeight(); zLimits[1] = -simulator.getEnvironment().getHeight();
		updateColorAccordingToZ();
		this.shape = new CircularShape(simulator, name + "CollisionObject", this, 0, 0, 0, 2 * radius, radius);
	}
	
	public Prey(Simulator simulator,  String name, double x, double y, double angle, double mass, double radius, String preyType) {
		super(simulator,name, x, y,0 ,0, angle,0, mass, PhysicalObjectType.PREY, null);
		zLimits[0] = simulator.getEnvironment().getHeight(); zLimits[1] = -simulator.getEnvironment().getHeight();
		updateColorAccordingToZ();
		this.PreyType = preyType;
		this.shape = new CircularShape(simulator, name + "CollisionObject", this, 0, 0, 0, 2 * radius, radius);
	}
	
	public Prey(Simulator simulator, String name, VectorLine position, int angle,
			double mass, double radius) {
		this(simulator, name, position.x, position.y,position.z, angle, mass, radius);
	}
	
	public Prey(Simulator simulator, String name, VectorLine position, int angle,
			double mass, double radius, String preyType) {
		this(simulator, name, position.x, position.y, position.z, angle, mass, radius);
		this.PreyType = preyType;
	}

	
	//Color setting based on Z value
	private void updateColorAccordingToZ() {
		AltimetryColor colorRamp = new AltimetryColor();
		double ramplimits = (zLimits[0]-zLimits[1])/21;
		int position = (int)((this.position.z-zLimits[1])/ramplimits);
		color = colorRamp.getColorinLayer(position);
	}
	
	public void setCarrier(Robot robot) {
		holder=robot;
		if(robot==null){
			setEnabled(true);
		} else {
			setEnabled(false);
		}
	}
	
	public Robot getHolder(){
		return holder;
	}
	
	@Override
	public void teleportTo(VectorLine position) {
		super.teleportTo(position);
	}

	public String getPreyType() {
		return PreyType;
	}
	
	@Override
	public VectorLine getPosition() {
		if (holder==null){
			return super.getPosition();
		} else {
			return holder.getPosition();
		}
	}
	
	public Color getColor() {
		return color;
	}
	
	public void setColor(Color color) {
		this.color = color;
	}

	@Override
	public String toString() {		
		return "Prey [holder=" + holder + ", position=" + position + "]";
	}

//	public boolean isEnabled() {
//		return holder==null;
//	}


}

package simulation.util;

import java.util.ArrayList;

import mathutils.VectorLine;

import simulation.robot.Robot;

public class ClusterUtil {
	
	private ArrayList<Robot> robots;
	private Robot robot;
	private VectorLine coord;
	public ClusterUtil(Robot robot){
		this.robot=robot;
		robots=new ArrayList<Robot>();
		robots.add(robot);
		this.coord=getCenterMass();
	}
	
	public VectorLine centerMass(){
		return coord;
	}
	
	private VectorLine getCenterMass(){
		VectorLine coord = new VectorLine();
		double x=0;
		double y=0;
		for(Robot r: robots){
			x+=r.getPosition().x;
			y+=r.getPosition().y;
		}
		x=x/robots.size();
		y=y/robots.size();
		coord.set(x,y);
		
		return coord;
		
	}
	
	public double getDistance(ClusterUtil cluster){
		return this.coord.distanceTo(cluster.coord);
	}
	public ArrayList<Robot> getClusterElements(){
		return robots;
	}
	
	public void addRobots(ArrayList<Robot> otherRobots){
		robots.addAll(otherRobots);
		this.coord=getCenterMass();
	}
	
	public void removeAllRobots(){
		robots.clear();
	}
	

}

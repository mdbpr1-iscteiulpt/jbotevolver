package simulation.physicalobjects.collisionhandling;

import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;

import mathutils.VectorLine;
import simulation.Simulator;
import simulation.environment.Environment;
import simulation.physicalobjects.ClosePhysicalObjects;
import simulation.physicalobjects.ClosePhysicalObjects.CloseObjectIterator;
import simulation.physicalobjects.MovableObject;
import simulation.physicalobjects.PhysicalObject;
import simulation.physicalobjects.PhysicalObjectDistance;
import simulation.physicalobjects.Prey;
import simulation.physicalobjects.Wall;
import simulation.physicalobjects.collisionhandling.knotsandbolts.CircularShape;
import simulation.physicalobjects.collisionhandling.knotsandbolts.CollisionManager;
import simulation.physicalobjects.collisionhandling.knotsandbolts.PolygonShape;
import simulation.robot.Robot;
import simulation.robot.sensors.RobotSensor;
import simulation.util.Arguments;

public class SimpleCollisionManager extends CollisionManager {
	
	public boolean drag = false;
	public double dragValue = 0.5;

	public SimpleCollisionManager(Simulator simulator) {
		super(simulator);
		Arguments args = simulator.getArguments().get("--robots");
		drag = args.getFlagIsTrue("drag");
		dragValue = args.getArgumentAsDoubleOrSetDefault("dragvalue", dragValue);
	}

	@Override
	public void handleCollisions(Environment environment, double time) {
		
		for(Robot r : environment.getRobots()) {
			r.getCollidingObjects().clear();
		}

		for (MovableObject mo : environment.getMovableObjects()) {
			
			mo.setInvolvedInCollison(false);
			mo.setInvolvedInCollisonWall(false);
			mo.shape.computeNewPositionAndOrientationFromParent();
			mo.shape.getCloseRobot().update(time, environment.getTeleported());
			mo.shape.getClosePrey().update(time, environment.getTeleported());
			mo.shape.getCloseWalls().update(time, environment.getTeleported());
		}

		VectorLine temp = new VectorLine();

		// robot - robot collisions
		for (Robot robot : environment.getRobots()) {
			ClosePhysicalObjects closeRobots = robot.shape.getCloseRobot();
			CloseObjectIterator iterator = closeRobots.iterator();
			while (iterator.hasNext()) {
				PhysicalObjectDistance closeRobot = iterator.next();
				temp.set(closeRobot.getObject().getPosition());
				temp.sub(robot.getPosition());
				double length = temp.length() - robot.getRadius()
						- closeRobot.getObject().getRadius();

				if (length < 0) {
					setLength(temp, length/2);
					robot.move(temp);
					temp.negate();
					((MovableObject) closeRobot.getObject()).move(temp);
					if(!robot.ignoreRobotToRobotCollisions()) {
						robot.setInvolvedInCollison(true);
						robot.getCollidingObjects().add(closeRobot.getObject());
						((Robot)closeRobot.getObject()).getCollidingObjects().add(robot);
						closeRobot.getObject().setInvolvedInCollison(true);
					}

				} else {

					iterator.updateCurrentDistance(length);
				}
			}
		}

		if (!environment.is3D()) {
			//robot - wall
			for (Robot robot : environment.getRobots()) {
				if (robot.ignoreWallCollisions()) {
					continue;
				}
				ClosePhysicalObjects closeWalls = robot.shape.getCloseWalls();
				CloseObjectIterator iterator = closeWalls.iterator();

				while (iterator.hasNext()) {

					Wall closeWall = (Wall) (iterator.next().getObject());

					if (closeWall.getMinDist(new VectorLine(robot.getPosition())) < robot.getRadius()) {

						PolygonShape ps = (PolygonShape) closeWall.shape;
						ps.collision = true;

						robot.setInvolvedInCollison(true);
						robot.setInvolvedInCollisonWall(true);
						robot.getCollidingObjects().add(closeWall);

						if (drag) {

							boolean leftFirst = simulator.getRandom().nextBoolean();

							double speed = robot.getPreviousPosition().distanceTo(robot.getPosition()) * dragValue;

							double orientation = robot.getOrientation();

							VectorLine prev = robot.getPreviousPosition();

							VectorLine left = new VectorLine(robot.getPreviousPosition());
							left.add(new VectorLine(speed * Math.cos(orientation - Math.PI / 2),
									speed * Math.sin(orientation - Math.PI / 2)));

							VectorLine right = new VectorLine(robot.getPreviousPosition());
							right.add(new VectorLine(speed * Math.cos(orientation + Math.PI / 2),
									speed * Math.sin(orientation + Math.PI / 2)));

							VectorLine[] pos = new VectorLine[] { left, right };

							if (!leftFirst) {
								pos[0] = right;
								pos[1] = left;
							}

							if (validPosition(pos[0], robot.getRadius(), closeWalls))
								robot.moveTo(pos[0]);
							else if (validPosition(pos[1], robot.getRadius(), closeWalls))
								robot.moveTo(pos[1]);
							else
								robot.moveTo(prev);

						} else {
							robot.moveTo(robot.getPreviousPosition());
							break;
						}
					}
				}
			} 
		}
		else {	//3D Wall - Robot Variant
			for (Robot robot : environment.getRobots()) {
				if (robot.ignoreWallCollisions()||robot.getStopped()) {
					continue;
				}
				ClosePhysicalObjects closeWalls = robot.shape.getCloseWalls();
				CloseObjectIterator iterator = closeWalls.iterator();
				
				while (iterator.hasNext()) {
					int involvedColl = 0;
					
					Wall closeWall = (Wall) (iterator.next().getObject());
					double sphereXDistance = Math.abs(robot.getPosition().x - closeWall.getPosition().x);
					double sphereYDistance = Math.abs(robot.getPosition().y - closeWall.getPosition().y);
					double sphereZDistance = Math.abs(robot.getPosition().z - closeWall.getPosition().z);
					
					if(sphereXDistance >= (closeWall.getWidth()/2 + robot.getRadius())) { continue; }
					if(sphereYDistance >= (closeWall.getLenght()/2 + robot.getRadius())) { 
; }
					if(sphereZDistance >= (closeWall.getHeight()/2 + robot.getRadius())) { continue; }

				    if ((sphereXDistance < (closeWall.getWidth()/2 + robot.getRadius()))
				    		&&  (sphereYDistance < (closeWall.getLenght()/2 + robot.getRadius()))
			    			&&  (sphereZDistance < (closeWall.getHeight()/2 + robot.getRadius()))){
						PolygonShape ps = (PolygonShape) closeWall.shape;
						ps.collision = true; involvedColl = 3;
						robot.setInvolvedInCollison(true);
						robot.setInvolvedInCollisonWall(true);
						robot.getCollidingObjects().add(closeWall);
						robot.setStopped(true);
				    } 										
				}
				
			}
		}
		//prey - wall
		for (Prey prey : environment.getPrey()) {

			ClosePhysicalObjects closeWalls = prey.shape.getCloseWalls();
			CloseObjectIterator iterator = closeWalls.iterator();
			while (iterator.hasNext()) {
				
				Wall closeWall = (Wall) (iterator.next().getObject());
				
				if(closeWall.getMinDist(new VectorLine(prey.getPosition())) < prey.getRadius()) {
					
					PolygonShape ps = (PolygonShape) closeWall.shape;
					ps.collision = true;

					prey.setInvolvedInCollison(true);
					prey.setInvolvedInCollisonWall(true);
					if(prey.getPreviousPosition()!=null) {
						prey.moveTo(prey.getPreviousPosition());						
					}
					break;
				} 
			}
		}
		
		// robot - prey collisions
		for(Prey p : environment.getPrey()) {
			if (p.isEnabled()) {
				ClosePhysicalObjects closeRobots = p.shape.getCloseRobot();
				CloseObjectIterator iterator = closeRobots.iterator();
				
				//if the number of robots touching the prey is less than the mass, make it stay in the same place
				//first, count the robots that are touching
				//then, move the prey
				boolean heavy = false;
				if(p.getMass() > 1) {
					int number = 0;
					while(iterator.hasNext()) {
						Robot r = (Robot) (iterator.next().getObject());
						temp.set(p.getPosition());
						temp.sub(r.getPosition());
						
						double length = temp.length() - r.getRadius()
								- p.getRadius();
						if (length < 0) {
							number++;
						}
					}
					if (number < p.getMass()) {
						heavy = true;
					}
				}
				
				iterator = closeRobots.iterator();
				while (iterator.hasNext()) {
					Robot r = (Robot) (iterator.next().getObject());
					
					temp.set(p.getPosition());
					temp.sub(r.getPosition());
					
					double length = temp.length() - r.getRadius()
							- p.getRadius();
					if (length < 0) {
						
						setLength(temp, length/2);
						
						r.move(temp);
						temp.negate();
						if(!heavy)
							p.move(temp);
						//r.setInvolvedInCollison(true);
						//p.setInvolvedInCollison(true);
					} else {
						iterator.updateCurrentDistance(length);
					}
				}
			}
		}
	/*
		// robot - prey collisions
		for (Robot robot : environment.getRobots()) {
			ClosePhysicalObjects closePreys = robot.shape.getClosePrey();
			CloseObjectIterator iterator = closePreys.iterator();
			// closePreys.debugInfo();
			while (iterator.hasNext()) {
				Prey closePrey = (Prey) (iterator.next().getObject());
				if (closePrey.isEnabled()) {
					temp.set(closePrey.getPosition());
					temp.sub(robot.getPosition());
					Vector2d temp2 = new Vector2d(temp);
					
					double length = temp.length() - robot.getRadius()
							- closePrey.getRadius();
					if (length < 0) {
						
						double div = 2;
						double mult = 1;
						
						setLength(temp, length/2);
						setLength(temp2, length/2);
						robot.move(temp);
						temp2.negate();
//						if(closePrey.getMass() != Double.MAX_VALUE)
							closePrey.move(temp2);
						// robot.setInvolvedInCollison(true);
						// closePrey.setInvolvedInCollison(true);
					} else {
						iterator.updateCurrentDistance(length);
					}
				}
			}
		}*/

		// prey - prey collisions
		for (Prey prey : environment.getPrey()) {
			if (prey.isEnabled()) {
				ClosePhysicalObjects closePreys = prey.shape.getClosePrey();
				CloseObjectIterator iterator = closePreys.iterator();
				while (iterator.hasNext()) {
					PhysicalObjectDistance closePrey = iterator.next();
					if (closePrey.getObject().isEnabled()) {
						temp.set(closePrey.getObject().getPosition());
						temp.sub(prey.getPosition());
						double tempLength = temp.length();
						double length = tempLength - prey.getDiameter();

						if (length < 0) {
							if (tempLength > 0) {
								setLength(temp, length/2);
							} else {
								temp.set(0, length / 2);
								temp.rotate(simulator.getRandom()
										.nextGaussian() * Math.PI);
							}
							prey.move(temp);
							temp.negate();
							((MovableObject) closePrey.getObject()).move(temp);
							// prey.setInvolvedInCollison(true);
							// closePrey.getObject().setInvolvedInCollison(true);
						} else {
							iterator.updateCurrentDistance(length);
						}
					}
				}
			}
		}
	}
	
	private boolean validPosition(VectorLine pos, double radius, ClosePhysicalObjects closeWalls) {
		CloseObjectIterator iterator = closeWalls.iterator();

		while(iterator.hasNext()) {
			Wall w = (Wall)iterator.next().getObject();
			if(w.getMinDist(pos) < radius)
				return false;
		}
		return true;
	}
	
	protected void setLength(VectorLine vector, double length) {
		if (vector.x == 0 && vector.y == 0) {
			vector.x = simulator.getRandom().nextGaussian();
			vector.y = simulator.getRandom().nextGaussian();
		}
		vector.setLength(length);
	}
	
	protected VectorLine handleCollision(PhysicalObject obj, Wall wall, int collisionStatus) {
		
		double valueX = obj.getPosition().getX(), valueY = obj.getPosition().getY();
		switch (collisionStatus) {
		// robot comes from the right
		case 0:
			valueX = wall.getTopLeftX() + wall.getWidth() + obj.getRadius();
			break;
		// from the left
		case 1:
			valueX = wall.getTopLeftX() - obj.getRadius();
			break;
		// from above
		case 2:
			valueY = wall.getTopLeftY() + obj.getRadius();
			break;
		// from below.
		case 3:
			valueY = wall.getTopLeftY() - wall.getLenght() - obj.getRadius();
			break;
		}

		return new VectorLine(valueX, valueY);
	}

	protected int checkIfCollided(Wall closeWall, PhysicalObject obj) {

		VectorLine topLeft = new VectorLine(closeWall.getTopLeftX(),
				closeWall.getTopLeftY()), topRight = new VectorLine(
				closeWall.getTopLeftX() + closeWall.getWidth(),
				closeWall.getTopLeftY()), bottomLeft = new VectorLine(
				closeWall.getTopLeftX(), closeWall.getTopLeftY()
						- closeWall.getLenght()), bottomRight = new VectorLine(
				closeWall.getTopLeftX() + closeWall.getWidth(),
				closeWall.getTopLeftY() - closeWall.getLenght());

		if (mathutils.MathUtils.distanceBetween(topRight, bottomRight,
				obj.getPosition()) <= obj.getRadius()) {
			// System.out.println("robot from right");
			return 0;
		}
		if (mathutils.MathUtils.distanceBetween(topLeft, bottomLeft,
				obj.getPosition()) <= obj.getRadius()) {
			// System.out.println("robot from left");
			return 1;
		}
		if (mathutils.MathUtils.distanceBetween(topRight, topLeft,
				obj.getPosition()) <= obj.getRadius()) {
			// System.out.println("robot from above");
			return 2;
		}
		if (mathutils.MathUtils.distanceBetween(bottomRight, bottomLeft,
				obj.getPosition()) <= obj.getRadius()) {
			// System.out.println("robot from below");
			return 3;
		}

		if (obj.getPosition().x > topLeft.x
				&& obj.getPosition().x < topRight.x) {
			if (obj.getPosition().y < topLeft.y
					&& obj.getPosition().y > bottomLeft.y) {
				return 4;
			}
		}

		return -1;
	}
}

package simulation.robot.sensors;

import simulation.Simulator;
import simulation.physicalobjects.checkers.AllowWallRobotChecker;
import simulation.robot.Robot;
import simulation.util.Arguments;

public class WallSensor extends LightTypeSensor {

	public WallSensor(Simulator simulator, int id, Robot robot, Arguments args) {
		super(simulator, id, robot, args);
		setAllowedObjectsChecker(new AllowWallRobotChecker(robot.getId()));
	}
}
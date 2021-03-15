package simulation.environment;

import java.util.Random;

import mathutils.VectorLine;
import simulation.Simulator;
import simulation.physicalobjects.Nest;
import simulation.physicalobjects.Wall;
import simulation.robot.Robot;
import simulation.util.ArgumentsAnnotation;
import simulation.util.Arguments;

public class SearchNestMazeEnvironment extends Environment{
	
	private static final double PREY_RADIUS = 0.025;
	private static final double PREY_MASS = 1;
	
	private int numberOfRobotsInNest = 0;
	private int numberOfRobotsThatCollided = 0;
	
	private Nest nest;
	
	@ArgumentsAnnotation(name="DisableWalls", values={"0","1"})	
	private boolean DisableWalls = false;

	private Random random;
	
	private Simulator simulator;
	private Arguments args;
	
	public SearchNestMazeEnvironment(Simulator simulator, Arguments arguments) {
		super(simulator, arguments);
		this.simulator = simulator;
		this.args = arguments;
		random = new Random();
		DisableWalls = args.getArgumentAsIntOrSetDefault("CircularCreation", 0) == 1;
	}

	@Override
	public void setup(Simulator simulator) {
		super.setup(simulator);
		
		if(simulator.getRobots().size() >= 1) {
			for(Robot robot: robots){
				robot.setPosition(0,-2.5,0);
			}
		}
		
		this.random = simulator.getRandom();
		
		//SetupWall
		if(!DisableWalls) {
			addWall(new Wall(simulator, new VectorLine(2,0,0), 0.2,100,100,0));
			addWall(new Wall(simulator, new VectorLine(-2,0,0), 0.2,100,100,0));
			addWall(new Wall(simulator, new VectorLine(0,3,0), 100,0.2,100,0));
			addWall(new Wall(simulator, new VectorLine(0,-3,0), 100,0.2,100,0));
		}
		
		//RandomWallSpawning
		if(!DisableWalls) {
			double[] xpos = {1.5,1.25,1,0.75,0.5};
			for(double i = 1.5; i>=-1.5; i--) {
				int randomPos = random.nextInt(5);
				System.out.println(randomPos);
				if(randomPos == 0){
					addWall(new Wall(simulator, new VectorLine(1.625,i,0), 0.75,0.25,100,0)); addWall(new Wall(simulator, new VectorLine(-0.5,i,0), 2.5,0.25,100,0));			
				}
				if(randomPos == 1) {
					addWall(new Wall(simulator, new VectorLine(-1.3,i,0), 1.4,0.25,100,0)); addWall(new Wall(simulator, new VectorLine(0.9,i,0), 2,0.25,100,0));			
				}
				if(randomPos == 2) {
					addWall(new Wall(simulator, new VectorLine(0,i+0.5,0), 0.2,1,100,0));
					addWall(new Wall(simulator, new VectorLine(0,i,0), 2.5,0.25,100,0));	
				}
				if(randomPos == 3) {
					addWall(new Wall(simulator, new VectorLine(1,i+0.5,0), 0.2,1,100,0));
					addWall(new Wall(simulator, new VectorLine(-1,i,0), 2,0.25,100,0));	
				}
				if(randomPos == 4) {
					addWall(new Wall(simulator, new VectorLine(-1,i+0.5,0), 0.2,1,100,0));
					addWall(new Wall(simulator, new VectorLine(1,i,0), 2,0.25,100,0));	
				}
				/*if(randomPos == 5) {
					addWall(new Wall(simulator, new VectorLine(2,i,0), x,1,100,0)); addWall(new Wall(simulator, new VectorLine(2,i,0), x,1,100,0));				
				}
				if(randomPos == 6) {
					addWall(new Wall(simulator, new VectorLine(2,i,0), x,1,100,0)); addWall(new Wall(simulator, new VectorLine(2,i,0), x,1,100,0));
				}	*/			
			}
		}
		
		//Add Nest
		nest = new Nest(simulator, "Nest", 0, 0, 0.5);
		addObject(nest);
	}
	
	@Override
	public void update(double time) {
		// TODO Auto-generated method stub
		
	}
	
}

package environments_ForagingIntensityPreys;

import java.awt.Color;

import physicalobjects.IntensityPrey;
import mathutils.Vector2d;
import simulation.Simulator;
import simulation.environment.Environment;
import simulation.physicalobjects.Prey;
import simulation.util.Arguments;

public class EasiestEnv_JustPreys extends ForagingIntensityPreysEnvironment {
	protected static final double MAX_X_LIMIT_FOR_PREY_PREYENV = 1.8;
	protected static final double MIN_X_LIMIT_FOR_PREY_PREYENV = -1.8;
	protected static final double MAX_Y_LIMIT_FOR_PREY_PREYENV = 1.8;
	protected static final double MIN_Y_LIMIT_FOR_PREY_PREYENV = -1.8;

	public EasiestEnv_JustPreys(Simulator simulator, Arguments arguments) {
		super(simulator, arguments);
	}

	public void setup(Environment env, Simulator simulator) { // when we want to use this env in other EnvMain -> (forLoopsOfEnvironmnt)
		super.setup(simulator);
		init(env);
	}

	@Override
	public void setup(Simulator simulator) {
		super.setup(simulator);
		init(this);
	}

	public void init(Environment env) {
		for (int i = 0; i < numberOfPreys; i++)
			env.addPrey(new IntensityPrey(simulator, "Prey " + i,
					newRandomPosition(), 0, PREY_MASS, PREY_RADIUS,
					randomIntensity()));
	}

	@Override
	protected Vector2d newRandomPosition() {

		return new Vector2d(random.nextDouble()
				* (MAX_X_LIMIT_FOR_PREY_PREYENV - MIN_X_LIMIT_FOR_PREY_PREYENV)
				+ MIN_X_LIMIT_FOR_PREY_PREYENV, random.nextDouble()
				* (MAX_Y_LIMIT_FOR_PREY_PREYENV - MIN_Y_LIMIT_FOR_PREY_PREYENV)
				+ MIN_Y_LIMIT_FOR_PREY_PREYENV);

	}

}

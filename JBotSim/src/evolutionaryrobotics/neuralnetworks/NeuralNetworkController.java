package evolutionaryrobotics.neuralnetworks;

import java.util.LinkedList;

import simulation.Simulator;
import simulation.robot.Robot;
import simulation.robot.actuators.Actuator;
import simulation.robot.sensors.Sensor;
import simulation.util.Arguments;
import simulation.util.ArgumentsAnnotation;
import controllers.Controller;
import controllers.FixedLenghtGenomeEvolvableController;

public class NeuralNetworkController extends Controller implements FixedLenghtGenomeEvolvableController {
	protected NeuralNetwork neuralNetwork;
	
	@ArgumentsAnnotation(name="printweights", values={"0","1"})
	protected boolean printWeights = false;

	public NeuralNetworkController(Simulator simulator, Robot robot, Arguments args) {
		super(simulator, robot, args);
		
		neuralNetwork = NeuralNetwork.getNeuralNetwork(simulator, robot, new Arguments(args.getArgumentAsString("network")));
		
		boolean printValues = args.getArgumentAsIntOrSetDefault("printvalues",0) == 1;
		if(printValues)
			neuralNetwork.printValues = true;
		
		for(Sensor s : robot.getSensors()) {
			if(s.getNumberExtraParameters() > 0)
				neuralNetwork.setRequiredNumberOfWeights(neuralNetwork.getGenomeLength() + s.getNumberExtraParameters());
		}
		
		for(Actuator a : robot.getActuators()) {
			if(a.getNumberExtraParameters() > 0)
				neuralNetwork.setRequiredNumberOfWeights(neuralNetwork.getGenomeLength() + a.getNumberExtraParameters());
		}
		
		if(args.getArgumentIsDefined("weights")) {
			String[] rawArray = args.getArgumentAsString("weights").split(",");
			double[] weights = new double[rawArray.length];
			for(int i = 0 ; i < weights.length ; i++)
				weights[i] = Double.parseDouble(rawArray[i]);
			setNNWeights(weights);
		}
		
		printWeights = args.getArgumentAsIntOrSetDefault("printweights", 0) == 1;
	}

	public boolean isAlive() {
		return true;
	}

	@Override
	public void begin() {
	}

	@Override
	public void controlStep(double time) {
		neuralNetwork.controlStep(time);
	}

	@Override
	public void end() {
	}

	@Override
	public int getGenomeLength() {
		return neuralNetwork.getGenomeLength();
	}
	
	public void setNeuralNetwork(NeuralNetwork neuralNetwork) {
		this.neuralNetwork = neuralNetwork;
	}

	public NeuralNetwork getNeuralNetwork() {
		return neuralNetwork;
	}

	@Override
	public void reset() {
		super.reset();
		neuralNetwork.reset();
	}

	@Override
	public void setNNWeights(double[] weights) {
		
		if(printWeights) {
			String w = "#weights (total of "+weights.length+")\n";
			for(int i = 0 ; i < weights.length ; i++) {
				w+=weights[i];
				if(i != weights.length-1)
					w+=",";
			}
			System.out.println(w);
		}
		
		neuralNetwork.setWeights(weights);
		
		int currentIndex = weights.length - 1;
		
		for(Sensor s : robot.getSensors()) {
			if(s.getNumberExtraParameters() > 0) {
				double[] params = new double[s.getNumberExtraParameters()];
				for(int i = 0 ; i < params.length ; i++,currentIndex--) {
					params[i] = weights[currentIndex];
				}
				s.setExtraParameters(params);
			}
		}
		
		for(Actuator a : robot.getActuators()) {
			if(a.getNumberExtraParameters() > 0) {
				double[] params = new double[a.getNumberExtraParameters()];
				for(int i = 0 ; i < params.length ; i++,currentIndex--) {
					params[i] = weights[currentIndex];
				}
				a.setExtraParameters(params);
			}
		}
	}
	
	@Override
	public double[] getNNWeights() {
		return neuralNetwork.getWeights();
	}

	public static void setNNWeights(LinkedList<Robot> robots, double[] weights) {
		
		for (Robot r : robots) {
			if (r.getController() instanceof FixedLenghtGenomeEvolvableController){
				FixedLenghtGenomeEvolvableController nnController = (FixedLenghtGenomeEvolvableController) r.getController();
				if (nnController != null)
					nnController.setNNWeights(weights);
			}
		}
	}
}
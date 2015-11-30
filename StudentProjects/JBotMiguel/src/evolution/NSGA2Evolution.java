package evolution;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import controllers.Controller;
import controllers.FixedLenghtGenomeEvolvableController;
import evolutionaryrobotics.JBotEvolver;
import evolutionaryrobotics.evolution.Evolution;
import evolutionaryrobotics.evolution.GenerationalEvolution;
import evolutionaryrobotics.neuralnetworks.Chromosome;
import evolutionaryrobotics.populations.Population;
import simulation.Simulator;
import simulation.robot.Robot;
import simulation.util.Arguments;
import taskexecutor.TaskExecutor;
import taskexecutor.results.SimpleFitnessResult;
import taskexecutor.tasks.GenerationalTask;

public class NSGA2Evolution extends GenerationalEvolution{

    protected ArrayList<Individual> allInds;
    protected boolean ordinalRanking;
    protected String[] objectives;
    
    public NSGA2Evolution(JBotEvolver jBotEvolver, TaskExecutor taskExecutor, Arguments arg) {
    	super(jBotEvolver, taskExecutor, arg);
    	ordinalRanking = arg.getArgumentIsDefined("ordinalranking") ? arg.getFlagIsTrue("ordinalranking") : true;
    }
    
    @Override
    protected void setupPopulation() {
    	
    	Arguments eval = jBotEvolver.getArguments().get("--evaluation");
    	Arguments multiObj = new Arguments(eval.getArgumentAsString("objectives"));
    	objectives = new String[multiObj.getNumberOfArguments()];
    	
    	for(int i = 0 ; i < objectives.length ; i++) {
    		objectives[i] = multiObj.getArgumentAsString(multiObj.getArgumentAt(i));
    	}
    	
    	jBotEvolver.getArguments().get("--population").setArgument("mainobjective",objectives[0]);
    	super.setupPopulation();
    }
    
    @Override
	public void executeEvolution() {
    	
		if(population.getNumberOfCurrentGeneration() == 0)
			population.createRandomPopulation();
		
		if(!population.evolutionDone()) {
			taskExecutor.setTotalNumberOfTasks((population.getNumberOfGenerations()-population.getNumberOfCurrentGeneration())*population.getPopulationSize()*objectives.length);
		}
		
		double highestFitness = 0;
		
		while(!population.evolutionDone() && executeEvolution) {
			
			double d = Double.valueOf(df.format(highestFitness));
			taskExecutor.setDescription(output+" "+population.getNumberOfCurrentGeneration()+"/"+population.getNumberOfGenerations() + " " + d);
			
			int totalEvaluations = 0;
			
			for(int o = 0 ; o < objectives.length ; o++) {
				
				HashMap<String,Arguments> args = jBotEvolver.getArgumentsCopy();
				String currentObj = objectives[o];
				args.put("--evaluation", new Arguments(currentObj));
				
				for(Chromosome c : ((NSGA2Population)population).getChromosomes()) {

					if(!executeEvolution)
						break;
			
					int samples = population.getNumberOfSamplesPerChromosome();
				
					taskExecutor.addTask(new MOTask(
							new JBotEvolver(args, jBotEvolver.getRandomSeed()),
							samples,c,population.getGenerationRandomSeed(),currentObj)
					);
					totalEvaluations++;
					print(".");
				}
				
			}
			
			print("\n");
			
			while(totalEvaluations-- > 0 && executeEvolution) {
				MOFitnessResult result = (MOFitnessResult)taskExecutor.getResult();
				((NSGA2Population)population).setEvaluationResultForId(result.getChromosomeId(), result.getFitness(),result.getObjective());
				print("!");
			}
			
			//TODO post-evaluators
			
			if(executeEvolution) {
				
				print("\nGeneration "+population.getNumberOfCurrentGeneration()+
						"\tHighest: "+population.getHighestFitness()+
						"\tAverage: "+population.getAverageFitness()+
						"\tLowest: "+population.getLowestFitness()+"\n");
				
				try {
					diskStorage.savePopulation(population);
				} catch(Exception e) {e.printStackTrace();}
				
				highestFitness = population.getHighestFitness();
				population.createNextGeneration();
			}
		}
		
		evolutionFinished = true;
		diskStorage.close();
	}
    
    public void processPopulation() {
        allInds = new ArrayList<Individual>(); // for stats only
        
        float[] maxVals = new float[objectives.length];
        Arrays.fill(maxVals, Float.NEGATIVE_INFINITY);
        float[] minVals = new float[objectives.length];
        Arrays.fill(minVals, Float.POSITIVE_INFINITY);
        for (int k = 0 ; k < ((NSGA2Population)population).getChromosomes().length ; k++) {
            double[] vals = new double[objectives.length];
            for(int j = 0 ; j < objectives.length ; j++) {
            	MOChromosome c = (MOChromosome)((NSGA2Population)population).getChromosomes()[k];
                vals[j] = c.getFitness(objectives[j]);
                maxVals[j] = Math.max((float)vals[j], maxVals[j]);
                minVals[j] = Math.min((float)vals[j], minVals[j]);
            }
            allInds.add(new Individual(k, vals));
        }
        double[] ranges = new double[objectives.length];
        for(int j = 0 ; j < ranges.length ; j++) {
            ranges[j] = maxVals[j] - minVals[j];
            if(ranges[j] == 0) {
                ranges[j] = 0.001f;
            }
        }

        // Sort by rank
        List<List<Individual>> ranked = fastNondominatedSort(allInds);
        // Calculate crowding distance
        for (List<Individual> rank : ranked) {
            //System.out.println(rank.size());
            crowdingDistanceAssignement(rank, ranges);
        }

        assignFitnessScores(ranked);

    }

    protected void assignFitnessScores(List<List<Individual>> rankedInds) {
        // Calculate score
        if (ordinalRanking) {
            List<Individual> all = new ArrayList<Individual>();
            for (List<Individual> rank : rankedInds) {
                all.addAll(rank);
            }
            Collections.sort(all);
            int index = 0;
            for (int i = 0; i < all.size(); i++) {
                Individual ind = all.get(i);
                if (i > 0 && ind.compareTo(all.get(i - 1)) == 0) {
                    // Assign the same score to individuals that are tied
                    ind.score = index;
                } else {
                    index = i;
                    ind.score = index;
                }
                
                population.setEvaluationResultForId(ind.individualId, ind.score);
            }
        } else {
            for (List<Individual> rank : rankedInds) {
                for (Individual ind : rank) {
                    double rankScore = rankedInds.size() - ind.rank;
                    double distScore = Double.isInfinite(ind.crowdingDistance) ? 1 : ind.crowdingDistance / 2;
                    ind.score = rankScore + distScore;
                    population.setEvaluationResultForId(ind.individualId, ind.score);
                }
            }
        }
    }

    /*
     * Returns the fronts and assign the nondomination rank to each individual
     */
    protected List<List<Individual>> fastNondominatedSort(List<Individual> inds) {
        List<List<Individual>> F = new ArrayList<List<Individual>>();
        List<Individual> F1 = new ArrayList<Individual>();
        for (Individual p : inds) {
            for (Individual q : inds) {
                if (p != q) {
                    if (p.paretoDominates(q)) {
                        p.S.add(q);
                    } else if (q.paretoDominates(p)) {
                        p.n = p.n + 1;
                    }
                }
            }
            //System.out.println("dom by: " + p.dominatedBy.size() + " | doms: " + p.S.size());
            if (p.n == 0) {
                F1.add(p);
            }
        }
        F.add(F1);
        int i = 0;
        while (!F.get(i).isEmpty()) {
            List<Individual> H = new ArrayList<Individual>();
            for (Individual p : F.get(i)) {
                for (Individual q : p.S) {
                    q.n = q.n - 1;
                    if (q.n == 0) {
                        H.add(q);
                    }
                }
            }
            i = i + 1;
            F.add(H);
        }

        // Last one may be empty
        if (F.get(F.size() - 1).isEmpty()) {
            F.remove(F.size() - 1);
        }

        // assign ranks
        for (i = 0; i < F.size(); i++) {
            for (Individual ind : F.get(i)) {
                ind.rank = i + 1;
            }
        }
        return F;
    }

    protected void crowdingDistanceAssignement(List<Individual> I, double[] ranges) {
        int mTotal = I.get(0).objectives.length;
        int l = I.size();
        for (Individual i : I) {
            i.crowdingDistance = 0;
        }
        for (int m = 0; m < mTotal; m++) {
            final int mm = m;
            // Sort according to objective m
            Collections.sort(I, new Comparator<Individual>() {
                @Override
                public int compare(Individual ind1, Individual ind2) {
                    return Double.compare(ind1.objectives[mm], ind2.objectives[mm]);
                }
            });
            I.get(0).crowdingDistance = Double.POSITIVE_INFINITY;
            I.get(l - 1).crowdingDistance = Double.POSITIVE_INFINITY;
            for (int i = 1; i < l - 1; i++) {
                I.get(i).crowdingDistance = I.get(i).crowdingDistance
                        + (I.get(i + 1).objectives[m] - I.get(i - 1).objectives[m]) / ranges[m];
            }
        }
    }
    
    protected class Individual implements Comparable<Individual> {

        double[] objectives;
        int rank;
        double crowdingDistance;
        double score;
        int n;
        List<Individual> S;
        int individualId;

        Individual(int individualId, double[] objectives) {
            this.individualId = individualId;
            this.objectives = objectives;
            this.S = new ArrayList<Individual>();
            this.n = 0;
        }

        boolean paretoDominates(Individual other) {
            boolean oneBetter = false;
            for (int i = 0; i < this.objectives.length; i++) {
                if (this.objectives[i] < other.objectives[i]) {
                    return false;
                } else if (this.objectives[i] > other.objectives[i]) {
                    oneBetter = true;
                }
            }
            return oneBetter;
        }

        @Override
        public int compareTo(Individual o) {
            if (this.rank < o.rank) {
                return 1;
            } else if (this.rank > o.rank) {
                return -1;
            } else {
                if (this.crowdingDistance > o.crowdingDistance) {
                    return 1;
                } else if (this.crowdingDistance < o.crowdingDistance) {
                    return -1;
                } else {
                    return 0;
                }
            }
        }
    }
}
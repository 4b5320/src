package cs190;

import java.util.LinkedList;
import java.util.Random;
public class GA {
	private final int maxPop = 500;
	private final int maxGen = 5000;
	private int gen = 1;
	private chromosome[] population;
	private int row, col, N;
	private double crossRate = 0.7;
	private int m = 10; //tournament size
	private Random rand = new Random();

	public GA(int row, int col, int N) {
		this.row = row;
		this.col = col;
		this.N = N;
	}
	

	protected void startGA() {
		population = new chromosome[maxPop];
		
		//Initialize chromosomes
		for(int i=0;i<population.length;i++) {
			population[i] = new chromosome(row, col, N, 12);
		}
		
		while(gen < maxGen) {
			//Compute fitness
			for(int i=0;i<population.length;i++) {
				population[i].computeFitness();
			}
			
			double totFit = 0;
			for(chromosome c : population) {
				totFit += c.getFitness();
			}
			System.out.println("Gen " + gen + ": " + (totFit/population.length) + "\t" + totFit + "\t" + population.length);
			
			
			//tournament selection
			chromosome[] newPop = new chromosome[maxPop];
			chromosome[] parents = new chromosome[(int) Math.floor(newPop.length*(1-crossRate))];
			for(int i=0;i<parents.length;i++) {
				chromosome[] competitors = new chromosome[m];
				
				//choose competitors
				for(int j=0;j<competitors.length;j++) {
					competitors[j] = population[rand.nextInt(population.length)];
				}
				
				chromosome winner = competitors[0];
				for(chromosome c : competitors) {
					if(c.getFitness() < winner.getFitness()) {
						winner = c;
					}
				}
				parents[i] = winner;
				//newPop[i] = parents[i];
			}
			
			LinkedList<chromosome> list = new LinkedList<chromosome>();
			for(int i=0;i<population.length;i++) {
				list.add(population[i]);
			}
			for(int i=0;i<parents.length;i++) {
				int best = 0;
				for(int j=1;j<list.size();j++) {
					if(population[j].getFitness() < population[0].getFitness()) {
						best = j;
					}
				}
				newPop[i] = list.remove(best);
			}
			
			
			//crossover
			for(int i=parents.length;i<newPop.length;i++) {
				chromosome[] offsprings = parents[rand.nextInt(parents.length)].crossWith(parents[rand.nextInt(parents.length)]);
				newPop[i] = offsprings[0];
				newPop[++i] = offsprings[1];
			}
			
			//mutation
			for(int i=0;i<newPop.length;i++) {
				newPop[i].mutate(0.01);
				newPop[i].repair();
			}
			
			//move to the next generation
			population = new chromosome[newPop.length];
			for(int i=0;i<newPop.length;i++) {
				population[i] = newPop[i];
			}
			
			gen++;
		}
	}

}











package cs190;

import java.util.LinkedList;
import java.util.Random;
import java.util.Scanner;
public class GA {
	private final int maxPop = 500;
	private final int maxGen = 50000;
	private int gen = 1;
	private chromosome[] population;
	
	private int row, col, N;
	private double crossRate = 0.7;
	private int m = 5; //tournament size
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
			double totFit = 0;
			for(int i=0;i<population.length;i++) {
				population[i].computeFitness();
				totFit += population[i].getFitness();
			}
			System.out.printf("Gen %d %.5f\n", gen, ((float) totFit/population.length));
			
			
			//tournament selection
			chromosome[] newPop = new chromosome[maxPop];
			chromosome[] parents = new chromosome[(int) Math.floor(newPop.length*(1-crossRate))];
			
			//convert the population to a list of contestants for the tournament
			LinkedList<chromosome> cont = new LinkedList<chromosome>();
			for(chromosome c : population) {
				cont.add(c);
			}
			
			//choose the parents
			for(int i=0;i<parents.length;i++) {
				chromosome[] competitors = new chromosome[m];
				
				//choose competitors of the tournament
				for(int j=0;j<competitors.length;j++) {
					competitors[j] = cont.remove(rand.nextInt(cont.size()));
				}
				
				//show the competitors
				
				
				//tournament!
				chromosome winner = competitors[0];
				for(int j=1;j<competitors.length;j++) {
					if(Double.compare(competitors[j].getFitness(), winner.getFitness()) < 0) {
						cont.add(winner);
						winner = competitors[j];
					} else {
						cont.add(competitors[j]);
					}
				}
				
				//choose the winner as a parent and add to next gen
				parents[i] = winner;
				newPop[i] = parents[i];
			}
			
			
			//crossover
			for(int i=parents.length;i<newPop.length;i++) {
				chromosome[] offsprings = parents[rand.nextInt(parents.length)].crossWith(parents[rand.nextInt(parents.length)]);
				newPop[i] = offsprings[0];
				try {
					newPop[++i] = offsprings[1];
				} catch (ArrayIndexOutOfBoundsException e) { }
			}
			
			//mutation
			for(int i=0;i<newPop.length;i++) {
				newPop[i].mutate(0.02);
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











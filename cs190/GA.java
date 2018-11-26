package cs190;

import java.util.LinkedList;
import java.util.Random;
import java.util.Scanner;
public class GA {
	private final int maxPop = 100;
	private final int maxGen = 50000;
	private int gen = 1;
	private chromosome[] population;
	
	private int row, col, N;
	private double crossRate = 0.7;
	private int m = 3; //tournament size
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
			
			
			//tournament selection
			chromosome[] newPop = new chromosome[maxPop];
			chromosome[] parents = new chromosome[(int) Math.floor(newPop.length*(1-crossRate))];
			chromosome[] drafted = new chromosome[parents.length];
			for(int i=0;i<parents.length;i++) {
				chromosome[] competitors = new chromosome[m];
				
				//choose competitors
				for(int j=0;j<competitors.length;j++) {
					
					competitors[j] = population[rand.nextInt(population.length)];
					//cant compete with itself
					for(int k=0;k<=j;k++){
						if(competitors[k]==competitors[j]){
							competitors[j]=population[rand.nextInt(population.length)];
							k=0;
						}
						//cant compete again
						for(int l=0;l<=j;l++){
							if(drafted[l]==competitors[j]){
								competitors[j]=population[rand.nextInt(population.length)];
								l=j+1;
								k=0;
							}
						}
					}
					drafted[j]=competitors[j];
					//dito nag iinfinite loop
				}
				
				chromosome winner = competitors[0];
				for(chromosome c : competitors) {
					if(Double.compare(c.getFitness(), winner.getFitness()) < 0) {
						winner = c;
					}
				}
				parents[i] = winner;
				//newPop[i] = parents[i];
			}
			
			chromosome[] fittest = findFittest(population);
			for(int i=0;i<parents.length;i++) {
				newPop[i] = fittest[i];
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
				newPop[i].mutate(0.01);
				newPop[i].repair();
			}
			
			//move to the next generation
			population = new chromosome[newPop.length];
			for(int i=0;i<newPop.length;i++) {
				population[i] = newPop[i];
			}
			
			gen++;
			new Scanner(System.in).nextLine();
		}
	}
	
	
	private chromosome[] findFittest(chromosome[] pop) {
		LinkedList<chromosome> list = new LinkedList<chromosome>();
		for(chromosome c : pop) {
			list.add(c);
		}
		
		chromosome[] fittest = new chromosome[(int) Math.floor(maxPop*0.3)];
		for(int i=0;i<fittest.length;i++) {
			int best = 0;
			for(int j=1;j<list.size();j++) {
				
				if(Double.compare(list.get(j).getFitness(), list.get(best).getFitness()) < 0) {
					best = j;
				}
			}
			fittest[i] = list.get(best);
			list.remove(best);
		}
		
		return fittest;
	}
}











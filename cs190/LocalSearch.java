package cs190;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.Scanner;

public class LocalSearch {
	LinkedList<chromosome> mostSuperiorNeighbors = new LinkedList<chromosome>();
	boolean hasSuperiorNeighboringSolution = true;
	
	public LocalSearch() {
		//hahahahahaha
	}
	
	protected void startLocalSearchOn(LinkedList<chromosome> initialSol, FileWriter writer) throws IOException {
		int iter = 1;
		chromosome currSol = null, superiorSol = initialSol.get(0);
		do {
			System.out.println("Iteration " + (iter++));
			writer.write("Iteration " + (iter) + "\n");
			
			System.out.println(superiorSol);
			System.out.println(superiorSol.getFitness() + "\n");
			writer.write(superiorSol.toString() + "\n");
			writer.write(superiorSol.getFitness() + "\n\n");
			
			currSol = superiorSol;
			superiorSol = getSuperiorNeighbor(currSol, writer);
		} while(superiorSol != null);
	}
	
	private chromosome getSuperiorNeighbor(chromosome c, FileWriter writer) throws IOException {
		System.out.println("Neighbors:");
		
		for(int i=0;i<c.genes.length;i++) {
			for(int j=0;j<c.genes[i].length;j++) {
				if(c.isTurbinePresentA(i, j)) {
					int[] pos = new int[] {-1,0,1};
					for(int a=0;a<pos.length;a++) {
						for(int b=0;b<pos.length;b++) {
							if(!(a==1 && b==1)) {
								try {
									chromosome neighbor = c.editChromosome(i, j, i+pos[a], j+pos[b]);
									neighbor.computeFitness();
									
									System.out.println(neighbor);
									System.out.println(neighbor.getFitness() + "\n");
									writer.write(neighbor.toString() + "\n");
									writer.write(neighbor.getFitness() + "\n\n");
									
									if(Double.compare(neighbor.getFitness(), c.getFitness()) < 0) {
										return neighbor;
									}
								} catch (ArrayIndexOutOfBoundsException e) { }
							}
						}
					}
				}
			}
		}
		
		return null;
	}
}

package cs190;

import java.util.LinkedList;

public class LocalSearch {
	
	
	public LocalSearch() {
		
	}
	
	protected void startLocalSearchOn(LinkedList<chromosome> initialSol) {
		LinkedList<Thread> searchThreads = new LinkedList<Thread>();
		for(chromosome c : initialSol) {
			Thread t = new Thread(new Runnable() {
				public void run() {
					findLocalMaxima(c);
				}
			});
			t.start();
			searchThreads.add(t);
		}
		
		// Wait for threads to finish
		for(int i=0;i<searchThreads.size();i++) {
			try {
				searchThreads.get(i).join();
			} catch (InterruptedException e) { }
		}
	}
	
	private void findLocalMaxima(chromosome c) {
		LinkedList<chromosome> neighbors = getSuperiorNeighborsOf(c);
		
		if(!neighbors.isEmpty()) {
			
		}
	}
	
	private LinkedList<chromosome> getSuperiorNeighborsOf(chromosome c) {
		LinkedList<chromosome> superiorNeighbors = new LinkedList<chromosome>();
		LinkedList<chromosome> neighbors = new LinkedList<chromosome>(); //Not necessarily superior
		
		// Find all neighbors
		for(int i=0;i<c.getDimension()[0];i++) {
			for(int j=0;j<c.getDimension()[1];j++) {
				if(c.isTurbinePresentA(i, j)) {
					int[] para = new int[] {-1, 0, 1};
					
					for(int x : para) {
						for(int y : para) {
							if(true /*x!=0 && y != 0*/) {
								try {
									if(!c.isTurbinePresentA(i+x, j+y)) {
										chromosome neigh = c.editChromosome(i, j, i+x, j+y);
										neighbors.add(neigh);
									}
								} catch (ArrayIndexOutOfBoundsException e) { }
							}
						}
					}
				}
			}
		}

		System.out.println("Neighbors: " + neighbors.size());
		for(chromosome chr : neighbors) {
			for(int i=0;i<chr.getDimension()[0];i++) {
				for(int j=0;j<chr.getDimension()[1];j++) {
					System.out.print(chr.isTurbinePresentA(i, j) + "\t");
				}
				System.out.println();
			}
			System.out.println();
			System.out.println();
		}
		
		// Find superior neighbors
		c.computeFitness();
		double baseFitness = c.getFitness();
		
		for(chromosome chr : neighbors) {
			chr.computeFitness();
			if(Double.compare(chr.getFitness(), baseFitness) <= 0) {
				superiorNeighbors.add(chr);
			}
		}
		
		System.out.println("Superior neighbors: " + superiorNeighbors.size());
		for(chromosome chr : superiorNeighbors) {
			for(int i=0;i<chr.getDimension()[0];i++) {
				for(int j=0;j<chr.getDimension()[1];j++) {
					System.out.print(chr.isTurbinePresentA(i, j) + "\t");
				}
				System.out.println();
			}
			System.out.println();
			System.out.println();
		}
		
		return superiorNeighbors;
	}
}

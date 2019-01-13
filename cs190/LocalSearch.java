package cs190;

import java.util.LinkedList;
import java.util.Scanner;

public class LocalSearch {
	LinkedList<chromosome> mostSuperiorNeighbors = new LinkedList<chromosome>();
	/*LinkedList<chromosome> checkedNeighbors = new LinkedList<chromosome>();
	Thread checkingThread = new Thread();
	int checked = 0;
	boolean chromosomeChecked = false;*/
	
	public LocalSearch() {
		
	}
	
	protected void startLocalSearchOn(LinkedList<chromosome> initialSol) {
		LinkedList<Thread> searchThreads = new LinkedList<Thread>();
		for(chromosome c : initialSol) {
			Thread t = new Thread(new Runnable() {
				public void run() {
					//findLocalMaxima(c);
					mostSuperiorNeighbors.add(getMostSuperiorNeighborOf(c));
				}
			});
			t.start();
			searchThreads.add(t);
			
			//findLocalMaxima(c);
		}
		
		// Wait for threads to finish
		for(int i=0;i<searchThreads.size();i++) {
			try {
				searchThreads.get(i).join();
			} catch (InterruptedException e) { }
		}
		
		
	}
	
	private chromosome getMostSuperiorNeighborOf(chromosome c) {
		chromosome superior = getSuperiorNeighborOf(c);
		
		if(superior == null) {
			return c;
		} else {
			return getMostSuperiorNeighborOf(superior);
		}
	}
	
	private chromosome getSuperiorNeighborOf(chromosome c) {
		LinkedList<chromosome> neighbors = new LinkedList<chromosome>();
		
		// Find all neighbors
		for(int i=0;i<c.getDimension()[0];i++) {
			for(int j=0;j<c.getDimension()[1];j++) {
				if(c.isTurbinePresentA(i, j)) {
					/*int[] para = new int[] {-1, 0, 1};
					
					for(int x : para) {
						for(int y : para) {
							if(!(x==0 && y==0)) {
								try {
									if(!c.isTurbinePresentA(i+x, j+y)) {
										chromosome neigh = c.editChromosome(i, j, i+x, j+y);
										
										if(Double.compare(neigh.getFitness(), c.getFitness()) < 0) {
											return neigh;
										}
									}
								} catch (ArrayIndexOutOfBoundsException e) { }
							}
						}
					}*/
					
					
					for(int x=0;x<c.genes.length;x++) {
						for(int y=0;y<c.genes[i].length;y++) {
							if(!c.isTurbinePresentA(x, y)) {
								chromosome neigh = c.editChromosome(i, j, x, y);
								
								if(Double.compare(neigh.getFitness(), c.getFitness()) < 0) {
									neighbors.add(neigh);
								}
							}
						}
					}
					
					//get the most fit superior neighbor
					int best = 0;
					for(int z=1;z<neighbors.size();z++) {
						if(Double.compare(neighbors.get(z).getFitness(), neighbors.get(best).getFitness()) < 0) {
							best = z;
						}
					}
					
					if(neighbors.isEmpty()) {
						return null;
					} else {
						return neighbors.get(best);
					}
				}
			}
		}
		
		return null;
	}

	
	/*private void findLocalMaxima(chromosome c) {
		LinkedList<Thread> searchThreads = new LinkedList<Thread>();
		LinkedList<chromosome> SuperiorNeighbors = getSuperiorNeighborsOf(c);
		
		if(SuperiorNeighbors.isEmpty()) {
			mostSuperiorNeighbors.add(c);
		} else {
			for(chromosome chr : SuperiorNeighbors) {
				Thread t = new Thread(new Runnable() {
					public void run() {
						findLocalMaxima(chr);
					}
				});
				
				t.start();
				searchThreads.add(t);
				//findLocalMaxima(chr);
			}
			
			// Wait for threads to finish
			for(int i=0;i<searchThreads.size();i++) {
				try {
					searchThreads.get(i).join();
				} catch (InterruptedException e) { }
			}
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
							if(!(x==0 && y==0)) {
								try {
									if(!c.isTurbinePresentA(i+x, j+y)) {
										chromosome neigh = c.editChromosome(i, j, i+x, j+y);
										
										try {
											checkingThread.join();
										} catch (InterruptedException e) { }
										if (!isChromosomeChecked(neigh)) {
											//Check if chromosome is already checked
											neighbors.add(neigh);
											checked++;
											checkedNeighbors.add(neigh);
										}
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
			if(Double.compare(chr.getFitness(), baseFitness) < 0) {
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

	private boolean isChromosomeChecked(chromosome neigh) {
		chromosomeChecked = false;
		
		checkingThread = new Thread(new Runnable() {
			public void run() {
				for(chromosome c : checkedNeighbors) {
					boolean sameBits = true;
					for(int i=0;i<c.getDimension()[0];i++) {
						for(int j=0;j<c.getDimension()[1];j++) {
							if(c.isTurbinePresentA(i, j) != neigh.isTurbinePresentA(i, j)) {
								sameBits = false;
								break;
							}
						}
						if(!sameBits) {
							break;
						}
					}
					if(sameBits) {
						chromosomeChecked = true;
						break;
					}
				}
			}
		});
			
		
		return chromosomeChecked;
	}*/
}

package cs190;

import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Random;
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
public class GA {
	private final int maxPop = 100, maxGen = 4000;
	//0.001984415661195087
	private int gen = 1;
	private chromosome[] population;
	private int row, col, N;
	private double crossRate = 0.7, mutationRate = 0.02;
	private Random rand = new Random();
	private boolean isConverged = false;
	private LinkedList<chromosome> distinct;
	private boolean multipleWindSpeed, isIrregular;
	
	/*JFrame frame = new JFrame();
	JLabel[][] matrix;*/

	public GA(int row, int col, int N, boolean multipleWindSpeed, boolean isIrregular) {
		this.row = row;
		this.col = col;
		this.N = N;
		this.multipleWindSpeed = multipleWindSpeed;
		this.isIrregular = isIrregular;
		
		/*frame = new JFrame();
		frame.getContentPane().setBackground(Color.WHITE);
		frame.setBounds(100, 100, 525, 545);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		frame.setVisible(true);
		
		matrix = new JLabel[row][col];
		
		for(int i=0;i<matrix.length;i++) {
			for(int j=0;j<matrix[i].length;j++) {
				matrix[i][j] = new JLabel("");
				matrix[i][j].setOpaque(true);
				matrix[i][j].setHorizontalAlignment(SwingConstants.CENTER);
				matrix[i][j].setBounds(51*i, 51*j, 50, 50);
				frame.getContentPane().add(matrix[i][j]);
			}
		}
		
		frame.repaint();
		frame.revalidate();*/
	}
	
	protected LinkedList<chromosome> getDistinctIndividiuals() {
		return distinct;
	}

	protected void startGA() throws IOException {
		File file;
		if(this.isIrregular) {
			file = new File("./Results/C" + N + ".csv");
		} else if(this.multipleWindSpeed) {
			file = new File("./Results/B" + N + ".csv");
		} else {
			file = new File("./Results/A" + N + ".csv");
		}
		FileWriter writer = new FileWriter(file);
		
		population = new chromosome[maxPop];
		
		//Initialize chromosomes
		LinkedList<Thread> threadsInit = new LinkedList<Thread>();
		for(int i=0;i<population.length;i++) {
			final int index = i;
			Thread t = new Thread(new Runnable() {
				public void run() {
					if(multipleWindSpeed) {
						population[index] = new chromosome(row, col, N, new double[] {8.0, 12.0, 17.0}, multipleWindSpeed, isIrregular);
					} else {
						population[index] = new chromosome(row, col, N, new double[] {12.0}, multipleWindSpeed, isIrregular);
					}
				}
			});
			t.start();
			threadsInit.add(t);
			//population[i] = new chromosome(row, col, N, new double[] {8.0, 12.0, 17.0});
		}
		//Wait for the threads to finish
		for(int i=0;i<threadsInit.size();i++) {
			try {
				threadsInit.get(i).join();
			} catch (InterruptedException e) { }
		}
		
		double startTime = 0, endTime = 0;
		
		while(gen <= maxGen && !isConverged) {
			
			// Get fitness
			int best = 0;
			double totalFit = 0;
			for(int i=0;i<population.length;i++) {
				//population[i].computeFitness();
				totalFit += population[i].getFitness();
				if(Double.compare(population[i].getFitness(), population[best].getFitness()) < 0) {
					best = i;
				}
			}
			
			//Save distinct chromosomes
			chromosome[] fittest = population;
			distinct = new LinkedList<chromosome>();
			
			for(chromosome c1 : fittest) {
				boolean isDistinct = true;
				for(chromosome c2 : distinct) {
					boolean sameGenes = true;
					for(int i=0;i<c1.genes.length;i++) {
						for(int j=0;j<c1.genes[i].length;j++) {
							if(c1.isTurbinePresentA(i, j) != c2.isTurbinePresentA(i, j)) {
								sameGenes = false;
								break;
							}
						}
						if(!sameGenes) {
							break;
						}
					}
					
					if(sameGenes) {
						isDistinct = false;
						break;
					}
				}
				
				if(isDistinct) {
					distinct.add(c1);
				}
			}
			
			//Get the end time
			endTime = System.currentTimeMillis();
			
			System.out.print(gen + "," + population[best].getFitness() + "," + ((double) totalFit/population.length) + "," + distinct.size());
			if(startTime != 0) {
				double hrs = ((0.001*(endTime - startTime)*(maxGen-gen))/60)/60;
				System.out.printf("\t%.3fs/gen %.3f hours left\n", (0.001*(endTime - startTime)), hrs);
			} else {
				System.out.println();
			}
			
			writer.write(gen + "," + population[best].getFitness() + "," + ((double) totalFit/population.length) + "," + distinct.size() + "\n");

			//Get the start time
			startTime = System.currentTimeMillis();
			
			//Check convergence
			isConverged = (distinct.size() == 1);
			
			//color the gui
			/*final int x = best;
			new Thread(new Runnable() {
				public void run() {
					for(int i=0;i<population[x].genes.length;i++) {
						for(int j=0;j<population[x].genes[i].length;j++) {
							if(Integer.parseInt(population[x].getPowerAt(i, j)) == 0) {
								matrix[j][i].setBackground(Color.LIGHT_GRAY);
							} else {
								matrix[j][i].setBackground(Color.BLACK);
							}
						}
					}
					frame.repaint();
					frame.revalidate();
				}
			}).start();*/
			
			
			
			//tournament selection
			chromosome[] newPop = new chromosome[population.length];
			chromosome[] parents = new chromosome[(int) Math.floor(newPop.length*(1-crossRate))];
			
			//convert the population to a list of contestants for the tournament
			LinkedList<chromosome> cont = new LinkedList<chromosome>();
			for(chromosome c : population) {
				cont.add(c);
			}
			
			//choose the parents
			for(int i=0;i<parents.length;i++) {
				chromosome[] competitors = new chromosome[(int) Math.floorDiv(cont.size(), parents.length)];
				
				
				
				//choose competitors of the tournament
				for(int j=0;j<competitors.length;j++) {
					try {
						competitors[j] = cont.remove(rand.nextInt(cont.size()));
					} catch (Exception e) {
						System.out.println(cont.isEmpty() + " " + population.length);
					}
				}
				
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
			int crossNum = 0;
			for(int i=parents.length;i<newPop.length;i++) {
				System.out.println("Crossover " + (crossNum++) + " of generation " + gen);
				chromosome[] offsprings = parents[rand.nextInt(parents.length)].crossWith(parents[rand.nextInt(parents.length)]);
				newPop[i] = offsprings[0];
				try {
					newPop[++i] = offsprings[1];
				} catch (ArrayIndexOutOfBoundsException e) { }
			}
			
			//mutation
			for(int i=0;i<newPop.length;i++) {
				newPop[i].mutate(mutationRate, i);
				newPop[i].repair();
			}
			
			//move to the next generation
			population = new chromosome[newPop.length];
			LinkedList<Thread> threadsFit = new LinkedList<Thread>();
			for(int i=0;i<newPop.length;i++) {
				population[i] = newPop[i];
				final int index = i;
				Thread t = new Thread(new Runnable() {
					public void run() {
						population[index].computeFitness();
					}
				});
				t.start();
				threadsFit.add(t);
			}
			//Wait for the threads to finish
			for(int i=0;i<threadsInit.size();i++) {
				try {
					threadsFit.get(i).join();
				} catch (InterruptedException e) { }
			}
			
			gen++;
			
		}
		
		writer.close();
	}
}











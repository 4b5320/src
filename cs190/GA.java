package cs190;

import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedList;
import java.util.Random;
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
public class GA {
	private final int maxPop = 100, maxGen = 5000;
	private int gen = 1;
	private chromosome[] population;
	private int row, col, N;
	private double crossRate = 0.7, mutationRate = 0.02;
	private int m = 5; //tournament size
	private Random rand = new Random();
	
	JFrame frame = new JFrame();
	private JLabel[][] matrix;

	public GA(int row, int col, int N) {
		this.row = row;
		this.col = col;
		this.N = N;
		
		frame = new JFrame();
		frame.getContentPane().setBackground(Color.WHITE);
		frame.setBounds(100, 100, 525, 545);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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
		frame.revalidate();
	}

	protected void startGA() {
		population = new chromosome[maxPop];
		
		//Initialize chromosomes
		for(int i=0;i<population.length;i++) {
			population[i] = new chromosome(row, col, N, 12);
		}
		
		while(gen < maxGen) {
			//Compute fitness
			int best = 0;
			double totalFit = 0;
			for(int i=0;i<population.length;i++) {
				population[i].computeFitness();
				totalFit += population[i].getFitness();
				if(Double.compare(population[i].getFitness(), population[best].getFitness()) < 0) {
					best = i;
				}
			}
			System.out.println("Gen " + gen + " " + population[best].getFitness());
			
			//color the gui
			final int x = best;
			new Thread(new Runnable() {
				public void run() {
					for(int i=0;i<matrix.length;i++) {
						for(int j=0;j<matrix[i].length;j++) {
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
			}).start();
			
			
			
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
				chromosome[] competitors = new chromosome[m];
				
				
				
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
			for(int i=parents.length;i<newPop.length;i++) {
				chromosome[] offsprings = parents[rand.nextInt(parents.length)].crossWith(parents[rand.nextInt(parents.length)]);
				newPop[i] = offsprings[0];
				try {
					newPop[++i] = offsprings[1];
				} catch (ArrayIndexOutOfBoundsException e) { }
			}
			
			//mutation
			for(int i=0;i<newPop.length;i++) {
				newPop[i].mutate(mutationRate);
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











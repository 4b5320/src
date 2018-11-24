package cs190;

import java.util.LinkedList;
import java.util.Random;
import java.util.Scanner;
public class main {
	private final int maxPop = 500;
	private final int maxGen = 5000;
	private int gen = 1;
	private chromosome[] population;
	private int row = 10, col = 10, N = 10;
	private double crossRate = 0.7;
	private int m = 10; //tournament size
	private Random rand = new Random();

	public static void main(String[] args) {
		new main();
	}
	
	public main() {
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
	
	public class chromosome{
		private gene[][] genes;
		private int N;
		private int row, col, theta = 0; //angle of wind direction
		private Random rand = new Random();
		private double u, fitness;
		
		public chromosome(int row, int col, int N, double u) {
			genes = new gene[row][col];
			this.u = u;
			this.N = N;
			this.row = row;
			this.col = col;
			
			for(int i=0;i<row;i++) {
				for(int j=0;j<col;j++) {
					genes[i][j] = new gene(row, col, i, j);
				}
			}
			
			//Initialize chromosome
			int i = 0;
			while(i < N) {
				int randomRow = rand.nextInt(row);
				int randomCol = rand.nextInt(col);
				if(!genes[randomRow][randomCol].isTurbinePresent()) {
					genes[randomRow][randomCol].setTurbinePresence(true);
					i++;
				}
			}
			
			computeFitness();
		}
		
		private void computeFitness() {
			
			//determine affected turbines by the wake of each turbine
			for(int i=0;i<genes.length;i++) {
				for(int j=0;j<genes[i].length;j++) {
					if(genes[i][j].isTurbinePresent())
						genes[i][j].computeWake(i, j, theta);
				}
			}
			
			//compute wind speed at each turbine
			double totalPower = 0;
			for(int i=0;i<genes.length;i++) {
				for(int j=0;j<genes[i].length;j++) {
					genes[i][j].setWindSpeed(getWindSpeedAt(i, j));
					totalPower += genes[i][j].getPower();
				}
			}
			
			//Compute the cost
			double cost = N*((2d/3d) + (1d/3d)*Math.pow(Math.E, 0.00147*Math.pow(N, 2)));
			
			fitness = cost/totalPower;
		}
		
		protected double getFitness() {
			return fitness;
		}
		
		protected chromosome[] crossWith(chromosome c) {
			//generate the random mask
			boolean[][] mask = new boolean[c.row][c.col];
			for(int i=0;i<mask.length;i++) {
				for(int j=0;j<mask.length;j++) {
					mask[i][j] = rand.nextBoolean();
				}
			}
			
			chromosome[] offsprings = {new chromosome(row, col, N, u), new chromosome(row, col, N, u)};
			for(int i=0;i<mask.length;i++) {
				for(int j=0;j<mask.length;j++) {
					if(mask[i][j]) {
						offsprings[0].setGeneAt(i, j, genes[i][j].isTurbinePresent());
						offsprings[1].setGeneAt(i, j, c.isTurbinePresentA(i, j));
					} else {
						offsprings[0].setGeneAt(i, j, c.isTurbinePresentA(i, j));
						offsprings[1].setGeneAt(i, j, genes[i][j].isTurbinePresent());
					}
				}
			}
			
			return offsprings;
		}
		
		protected void setGeneAt(int i, int j, boolean val) {
			genes[i][j].setTurbinePresence(val);
		}
		
		protected boolean isTurbinePresentA(int i, int j) {
			return genes[i][j].isTurbinePresent();
		}
		
		protected void mutate(double rate) {
			if(rand.nextDouble() < rate) {
				//Find a random 1
				int i0, j0;
				do {
					i0 = rand.nextInt(row);
					j0 = rand.nextInt(col);
				} while(!genes[i0][j0].isTurbinePresent());
				genes[i0][j0].setTurbinePresence(false);
				
				//Find a random 0
				do {
					i0 = rand.nextInt(row);
					j0 = rand.nextInt(col);
				} while(genes[i0][j0].isTurbinePresent());
				genes[i0][j0].setTurbinePresence(true);
			}
		}
		
		protected void repair() {
			//Count the number of turbines
			int turbineCount = 0;
			for(gene[] g1 : genes) {
				for(gene g2 : g1) {
					if(g2.isTurbinePresent()) {
						turbineCount++;
					}
				}
			}
			
			if(turbineCount > N) {
				while(turbineCount > N) {
					int i0, j0;
					do {
						i0 = rand.nextInt(row);
						j0 = rand.nextInt(col);
					} while(!genes[i0][j0].isTurbinePresent());
					genes[i0][j0].setTurbinePresence(false);
					turbineCount--;
				}
			} else if(turbineCount < N) {
				while(turbineCount < N) {
					int i0, j0;
					do {
						i0 = rand.nextInt(row);
						j0 = rand.nextInt(col);
					} while(genes[i0][j0].isTurbinePresent());
					genes[i0][j0].setTurbinePresence(true);
					turbineCount++;
				}
			}
		}
		
		private double getWindSpeedAt(int i, int j) {
			
			if (genes[i][j].isTurbinePresent()) {
				if (Double.isNaN(genes[i][j].getWindSpeed())) {
					//Find upstream turbines
					LinkedList<gene> upstream = new LinkedList<gene>();
					for (gene[] k : genes) {
						for (gene g : k) {
							if (g.isTurbineUnderWake(i, j)) {
								upstream.add(g);
							}
						}
					}

					if (upstream.isEmpty()) {
						genes[i][j].setWindSpeed(u);
						return genes[i][j].getWindSpeed();
					} else {
						double sum = 0;
						for (gene g : upstream) {
							sum = sum + Math
									.pow(1 - (getWindSpeedDueTo(g.ipos, g.jpos, i, j) / getWindSpeedAt(g.ipos, g.jpos)), 2);
						}

						genes[i][j].setWindSpeed((1 - Math.sqrt(sum)) * u);
						return genes[i][j].getWindSpeed();
					}
				} else {
					return genes[i][j].getWindSpeed();
				} 
			} else {
				genes[i][j].setWindSpeed(0);
				return genes[i][j].getWindSpeed();
			}
		}
		
		private double getWindSpeedDueTo(int i0, int j0, int i, int j) {//Jensen
			double x = 0;
			if(theta == 0) {
				x = 2000*(i - i0);
			} else {
				System.out.println("Angle not yet available");
				System.exit(1);
			}
			
			double CT = 0.88; //thrust coefficient
			double a =  0.5 - 0.5*(Math.sqrt(1-CT)); //axial induction
			double z = 60;// hub height
			double z0 = 0.3; //ground roughness
			double beta = 0.5/Math.log(z/z0); //entrainment constant
			double r0 = 20; // rotor radius
			
			return getWindSpeedAt(i0, j0)*(1-(2*a*(r0/(r0+beta*x))));
		}
	}
	
	public class gene{
		private boolean turbine;
		private double power;
		private boolean[][] wake;
		private double u;
		protected int ipos, jpos;
		
		public gene(int row, int col, int ipos, int jpos) {
			wake = new boolean[row][col];
			turbine = false;
			power = 0;
			u = Double.NaN;
			this.ipos = ipos;
			this.jpos = jpos;
		}
		
		protected boolean isTurbineUnderWake(int i, int j) {
			return wake[i][j];
		}
		
		protected boolean isTurbinePresent() {
			return turbine;
		}
		
		protected void setTurbinePresence(boolean b) {
			turbine = b;
		}
		
		protected double getWindSpeed() {
			return u;
		}
		
		protected void setWindSpeed(double u) {
			this.u = u;
			power = 0.3*Math.pow(u, 3);
		}
		
		protected double getPower() {
			return power;
		}
		
		protected void computeWake(int i0, int j0, int theta) {
			wake = new boolean[row][col];
			int iMin = 0, iMax = wake.length, jMin = 0, jMax = wake[0].length;
			double z = 20/Math.tan(Math.toRadians(10)) / 200;
			double dx, dy;
			
			switch(theta){
				case 90:
					dx = 0;
					dy = -z;
					break;
				case 180:
					dx = -z;
					dy = 0;
					break;
				case 270:
					dx = 0;
					dy = z;
					break;
				default:
					dx = z*Math.cos(Math.toRadians((-1)*theta));
					dy = z*Math.sin(Math.toRadians((-1)*theta));
			}
			
			//Sets the area of coverage of the wake
			if(theta == 0) {
				iMin = i0;
			}else if(theta > 0 && theta < 90){
				iMin = i0;
				jMax = j0;
			}else if(theta == 90) {
				jMax = j0;
			}else if(theta > 90 && theta < 180) {
				iMax = i0;
				jMax = j0;
			}else if(theta == 180) {
				iMax = i0;
			}else if(theta > 180 && theta < 270) {
				iMax = i0;
				jMin = j0;
			}else if(theta == 270) {
				jMin = j0;
			}else{
				iMin = i0;
				jMin = j0;
			}
			
			for(int i=0;i<10;i++) {
				for(int j=0;j<10;j++) {
					//Compute the angle
					if(i != i0 || j != j0) {
						if(i >= iMin && i <= iMax && j >= jMin && j <= jMax) {
							try {
								
								double angle = 0;
								double diffangle = 0;
								if((theta>=0 && theta<90) || (theta>=180 && theta<270)) {
									angle = Math.toDegrees(Math.atan( ((float) j-j0+dy)/((float) i-i0+dx) ));
									diffangle = Math.abs(angle+theta-(90*Math.floor(theta/90.0)));
								}else {
									angle = Math.toDegrees(Math.atan( ((float) i-i0+dx)/((float) j-j0+dy) ));
									diffangle = Math.abs(angle-(theta-(90*Math.floor(theta/90.0))));
								}
								
								wake[i][j] = (diffangle <= 10);
							} catch (Exception e) {
								System.out.print("null\t");
							}
						}else{
							wake[i][j] = false;
						}
					}
				}
			}
		}
	}
}











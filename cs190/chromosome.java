package cs190;

import java.util.LinkedList;
import java.util.Random;

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
	
	protected void computeFitness() {
		
		/*//determine affected turbines by the wake of each turbine
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
		}*/
		
		double totalPower = 0;
		for(int i=0;i<genes.length;i++) {
			for(int j=0;j<genes[i].length;j++) {
				if((i==0 || i==genes.length-1) && genes[i][j].isTurbinePresent()) {
					totalPower++;
				}
			}
		}
		
		//Compute the cost
		double cost = N*((2d/3d) + (1d/3d)*Math.pow(Math.E, 0.00147*Math.pow(N, 2)));
		
		fitness = N/totalPower - 1;
		//System.out.println("Fitness: " + fitness);
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
	
	public String toString() {
		String s = "";
		
		for(gene[] a : genes) {
			for(gene b : a) {
				if(b.isTurbinePresent()) {
					s = s + (1 + " ");
				} else {
					s = s + (0 + " ");
				}
			}
			s = s + "\n";
		}
		
		return s;
	}
}

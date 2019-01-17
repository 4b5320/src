package cs190;

import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Random;

public class chromosome{
	protected gene[][] genes;
	private int N;
	private int row, col, theta = 0; //angle of wind direction
	private Random rand = new Random();
	private double fitness;
	private double[] u;
	protected boolean isMultipleWindSpeed, isIrregular;
	private int[] columns = {4,5,5,6,7,7,8,9,9,10};
	
	/* IRREGULAR LAND SPACE 10X10
	 * 	O	O	O	O	X	X	X	X	X	X
	 * 	O	O	O	O	O	X	X	X	X	X
	 * 	O	O	O	O	O	X	X	X	X	X
	 * 	O	O	O	O	O	O	X	X	X	X
	 * 	O	O	O	O	O	O	O	X	X	X
	 * 	O	O	O	O	O	O	O	X	X	X
	 * 	O	O	O	O	O	O	O	O	X	X
	 * 	O	O	O	O	O	O	O	O	O	X
	 * 	O	O	O	O	O	O	O	O	O	X
	 * 	O	O	O	O	O	O	O	O	O	O
	 * */
	
	public chromosome(int row, int col, int N, double[] u, boolean multSpeed, boolean isIrregular) {
		if(isIrregular) {
			genes = new gene[row][];
			for(int i=0;i<genes.length;i++) {
				genes[i] = new gene[columns[i]];
			}
		} else {
			genes = new gene[row][col];
		}
		
		this.isIrregular = isIrregular;
		this.u = u;
		this.N = N;
		this.row = row;
		this.col = col;
		this.isMultipleWindSpeed = multSpeed;
		
		for(int i=0;i<genes.length;i++) {
			for(int j=0;j<genes[i].length;j++) {
				genes[i][j] = new gene(row, col, i, j, multSpeed);
			}
		}
		
		//Initialize chromosome
		int i = 0;
		while(i < N) {
			int randomRow = rand.nextInt(row);
			int randomCol;
			if (isIrregular) {
				randomCol = rand.nextInt(columns[randomRow]);
			} else {
				randomCol = rand.nextInt(col);
			}
			if(!genes[randomRow][randomCol].isTurbinePresent()) {
				genes[randomRow][randomCol].setTurbinePresence(true);
				i++;
			}
		}
		
		computeFitness();
	}
	
	public chromosome(chromosome c) {
		this.u = c.getWindInitialWindSpeed();
		this.N = c.getNumberOfTurbines();
		this.row = c.getDimension()[0];
		this.col = c.getDimension()[1];
		this.isMultipleWindSpeed = c.isMultipleWindSpeed;
		this.isIrregular = c.isIrregular;
		if(c.isIrregular) {
			genes = new gene[row][];
			for(int i=0;i<genes.length;i++) {
				genes[i] = new gene[columns[i]];
			}
		} else {
			genes = new gene[row][col];
		}
		
		for(int i=0;i<genes.length;i++) {
			for(int j=0;j<genes[i].length;j++) {
				genes[i][j] = new gene(row, col, i, j, isMultipleWindSpeed);
				genes[i][j].setTurbinePresence(c.isTurbinePresentA(i, j));
			}
		}
		
		computeFitness();
	}
	
	public chromosome(gene[][] genes, double[] u, boolean multSpeed, boolean isIrregular) {
		this.u = u;
		this.row = genes.length;
		this.col = genes[genes.length-1].length;
		this.isMultipleWindSpeed = multSpeed;
		this.isIrregular = isIrregular;
		
		int N = 0;
		for(int i=0;i<genes.length;i++) {
			for(int j=0;j<genes[i].length;j++) {
				if(genes[i][j].isTurbinePresent()) {
					N++;
				}
			}
		}
		this.N= N;
		
		if(isIrregular) {
			this.genes = new gene[row][];
			for(int i=0;i<genes.length;i++) {
				this.genes[i] = new gene[columns[i]];
			}
		} else {
			this.genes = new gene[row][col];
		}
		for(int i=0;i<genes.length;i++) {
			for(int j=0;j<genes[i].length;j++) {
				this.genes[i][j] = new gene(row, col, i, j, isMultipleWindSpeed);
				this.genes[i][j].setTurbinePresence(genes[i][j].isTurbinePresent());
			}
		}
		
		computeFitness();
	}
	
	protected double[] getWindInitialWindSpeed() {
		return u;
	}
	
	protected int getNumberOfTurbines() {
		return N;
	}
	
	protected void computeFitness() {
		double totalPower = 0;
		theta = 0; //reset the theta to 0
		
		//compute wind speed at each turbine given wakes
		while(theta < 360) {
			
			//clear wind speed
			for (int i = 0; i < genes.length; i++) {
				for (int j = 0; j < genes[i].length; j++) {
					boolean temp = genes[i][j].isTurbinePresent();
					genes[i][j] = new gene(row, col, i, j, isMultipleWindSpeed);
					genes[i][j].setTurbinePresence(temp);
				}
			}
			
			//determine affected turbines by the wake of each turbine
			for (int i = 0; i < genes.length; i++) {
				for (int j = 0; j < genes[i].length; j++) {
					if (genes[i][j].isTurbinePresent())
						genes[i][j].computeWake(i, j, theta);
				}
			}
			
			for (int i = 0; i < genes.length; i++) {
				for (int j = 0; j < genes[i].length; j++) {
					genes[i][j].setWindSpeed(getWindSpeedAt(i, j), theta);
					totalPower += genes[i][j].getPower();
				}
			} 
			
			theta += 10;
		}
		
		
		//Compute the cost
		double cost = N*((2d/3d) + (1d/3d)*Math.pow(Math.E, -0.00174*Math.pow(N, 2)));
		
		fitness = cost/totalPower;
		//System.out.println("Fitness: " + fitness);
	}
	
	protected double getFitness() {
		return fitness;
	}
	
	protected String getPowerAt(int i, int j) {
		double totalPow = 0;
		for(int k=0;k<u.length;k++) {
			totalPow += 100*genes[i][j].getWindSpeed()[k]/12;
		}
		
		return String.valueOf((int) Math.floor(totalPow));
	}
	
	protected chromosome[] crossWith(chromosome c, FileWriter writer) throws IOException { //Exception thrown by writer
		//generate the random mask
		boolean[][] mask;
		if(isIrregular) {
			mask = new boolean[c.row][];
			for(int i=0;i<mask.length;i++) {
				mask[i] = new boolean[columns[i]];
			}
		} else {
			mask = new boolean[c.row][c.col];
		}
		for(int i=0;i<mask.length;i++) {
			for(int j=0;j<mask[i].length;j++) {
				mask[i][j] = rand.nextBoolean();
			}
		}
		
		//print the parents
		writer.write("Parent 1\n" + c.toString() + "\n");
		writer.write("Parent 2\n" + this.toString() + "\n");
		//System.out.println("Parent 1\n" + c.toString());
		//System.out.println("Parent 2\n" + this.toString());
		
		
		chromosome[] offsprings = {new chromosome(row, col, N, u, isMultipleWindSpeed, c.isIrregular),
				new chromosome(row, col, N, u, isMultipleWindSpeed, c.isIrregular)};

		//System.out.println("Mask");
		writer.write("Mask\n");
		for(int i=0;i<mask.length;i++) {
			for(int j=0;j<mask[i].length;j++) {
				if(mask[i][j]) {
					//System.out.print("1 ");
					writer.write("1 ");
					offsprings[0].setGeneAt(i, j, genes[i][j].isTurbinePresent());
					offsprings[1].setGeneAt(i, j, c.isTurbinePresentA(i, j));
				} else {
					//System.out.print("0 ");
					writer.write("0 ");
					offsprings[0].setGeneAt(i, j, c.isTurbinePresentA(i, j));
					offsprings[1].setGeneAt(i, j, genes[i][j].isTurbinePresent());
				}
			}
			writer.write("\n");
			//System.out.println();
		}
		writer.write("\n");
		//System.out.println();
		

		//print the offspring
		writer.write("Offspring 1\n" + offsprings[0].toString() + "\n");
		writer.write("Offspring 2\n" + offsprings[1].toString() + "\n");
		//System.out.println("Offspring 1\n" + offsprings[0].toString());
		//System.out.println("Offspring 2\n" + offsprings[1].toString());
		
		return offsprings;
	}
	
	protected void setGeneAt(int i, int j, boolean val) {
		genes[i][j].setTurbinePresence(val);
	}
	
	protected int[] getDimension() {
		return new int[] {row, col};
	}
	
	protected chromosome editChromosome(int i0, int j0, int i, int j) throws ArrayIndexOutOfBoundsException{
		chromosome c = new chromosome(this);
		c.setGeneAt(i0, j0, false);
		c.setGeneAt(i, j, true);
		
		return c;
	}
	
	protected boolean isTurbinePresentA(int i, int j) throws ArrayIndexOutOfBoundsException {
		return genes[i][j].isTurbinePresent();
	}
	
	protected void mutate(double rate, int lbl, FileWriter writer) throws IOException { //Exception thrown by writer
		if(rand.nextDouble() < rate) {
			//System.out.println(lbl + "th chromosome mutated from\n" + this.toString() + "\nto");
			writer.write(lbl + "th chromosome mutated from\n" + this.toString() + "\nto\n");
			
			//Find a random 1
			int i0, j0;
			do {
				i0 = rand.nextInt(row);
				if (isIrregular) {
					j0 = rand.nextInt(columns[i0]);
				} else {
					j0 = rand.nextInt(col);
				}
			} while(!genes[i0][j0].isTurbinePresent());
			genes[i0][j0].setTurbinePresence(false);
			
			//Find a random 0
			do {
				i0 = rand.nextInt(row);
				if (isIrregular) {
					j0 = rand.nextInt(columns[i0]);
				} else {
					j0 = rand.nextInt(col);
				}
			} while(genes[i0][j0].isTurbinePresent());
			genes[i0][j0].setTurbinePresence(true);

			//System.out.println(this.toString());
			writer.write(this.toString() + "\n");
		}
	}
	
	protected void repair(int lbl, FileWriter writer) throws IOException { //Exception thrown by writer
		
		//Count the number of turbines
		int turbineCount = 0;
		for(gene[] g1 : genes) {
			for(gene g2 : g1) {
				if(g2.isTurbinePresent()) {
					turbineCount++;
				}
			}
		}
		
		if(turbineCount != N) {
			//System.out.println(lbl + "th chromosome repaired from\n" + this.toString());
			writer.write(lbl + "th chromosome repaired from\n" + this.toString() + "\n");
			if(turbineCount > N) {
				while(turbineCount > N) {
					int i0, j0;
					do {
						i0 = rand.nextInt(row);
						if (isIrregular) {
							j0 = rand.nextInt(columns[i0]);
						} else {
							j0 = rand.nextInt(col);
						}
					} while(!genes[i0][j0].isTurbinePresent());
					genes[i0][j0].setTurbinePresence(false);
					turbineCount--;
				}
			} else if(turbineCount < N) {
				while(turbineCount < N) {
					int i0, j0;
					do {
						i0 = rand.nextInt(row);
						if (isIrregular) {
							j0 = rand.nextInt(columns[i0]);
						} else {
							j0 = rand.nextInt(col);
						}
					} while(genes[i0][j0].isTurbinePresent());
					genes[i0][j0].setTurbinePresence(true);
					turbineCount++;
				}
			}
			//System.out.println("to\n" + this.toString());
			writer.write("to\n" + this.toString());
		}
		
	}
	
	private double[] getWindSpeedAt(int i, int j) {
		
		if (genes[i][j].isTurbinePresent()) {
			if (Double.isNaN(genes[i][j].getWindSpeed()[0])) {
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
					genes[i][j].setWindSpeed(u, theta);
					return genes[i][j].getWindSpeed();
				} else {
					double sum;
					double[] windSpeed = new double[u.length];
					for (int k = 0; k < u.length; k++) {
						sum = 0;
						for (gene g : upstream) {
							sum = sum + Math.pow(
									1 - (getWindSpeedDueTo(g.ipos, g.jpos, i, j)[k] / getWindSpeedAt(g.ipos, g.jpos)[k]), 2);
						} 
						windSpeed[k] = (1 - Math.sqrt(sum)) * u[k];
					}
					
					genes[i][j].setWindSpeed(windSpeed, theta);
					return genes[i][j].getWindSpeed();
				}
			} else {
				return genes[i][j].getWindSpeed();
			} 
		} else {
			genes[i][j].setWindSpeed(new double[] {0,0,0}, theta);
			return genes[i][j].getWindSpeed();
		}
	}
	
	private double[] getWindSpeedDueTo(int i0, int j0, int i, int j) {//Modified Jensen
		double x = 200*Math.sqrt(Math.pow(Math.abs(i - i0), 2) + Math.pow(Math.abs(j - j0), 2));
		
		double CT = 0.88; //thrust coefficient
		double a =  0.5 - 0.5*(Math.sqrt(1-CT)); //axial induction
		double z = 60;// hub height
		double z0 = 0.3; //ground roughness
		double beta = 0.5/Math.log(z/z0); //entrainment constant
		double r0 = 20; // rotor radius
		
		double[] windSpeed;
		if(this.isMultipleWindSpeed) {
			windSpeed = new double[3];
			windSpeed[0] = getWindSpeedAt(i0, j0)[0]*(1-(2*a*Math.pow((r0/(r0+beta*x)), 2)));
			windSpeed[1] = getWindSpeedAt(i0, j0)[1]*(1-(2*a*Math.pow((r0/(r0+beta*x)), 2)));
			windSpeed[2] = getWindSpeedAt(i0, j0)[2]*(1-(2*a*Math.pow((r0/(r0+beta*x)), 2)));
		} else {
			windSpeed = new double[3];
			windSpeed[0] = getWindSpeedAt(i0, j0)[0]*(1-(2*a*Math.pow((r0/(r0+beta*x)), 2)));
		}
		
		
		return windSpeed;
	}
	
	public String toString() {
		String s = "";
		
		for(gene[] a : genes) {
			for(gene b : a) {
				if(b.isTurbinePresent()) {
					s = s + ("TRUE, ");
				} else {
					s = s + ("FALSE, ");
				}
			}
			s = s + "\n";
		}
		
		return s;
	}
}

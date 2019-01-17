package cs190;

import java.awt.Color;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Random;
import java.util.Scanner;
import java.util.Stack;

public class main{
	int dim = 5;

	public static void main(String[] arguments) {
		
		arguments = new String[] {/*"-fitness",*/ "A", "5"};
		
		if (!arguments[0].equals("-fitness")) {
			LinkedList<String> done = new LinkedList<String>();
			boolean multSpeed, isIrregular;
			new File("Results").mkdir();
			if (arguments[0].equals("A")) {
				multSpeed = false;
				isIrregular = false;
			} else if (arguments[0].equals("B")) {
				multSpeed = true;
				isIrregular = false;
			} else if (arguments[0].equals("C")) {
				multSpeed = true;
				isIrregular = true;
			} else {
				System.out.println("Invalid argument!");
				return;
			}
			for (int i = 1; i < arguments.length; i++) {
				try {
					new main(Integer.parseInt(arguments[i]), multSpeed, isIrregular);
					done.add(arguments[i]);
				} catch (IOException e) {
					e.printStackTrace();
					new Scanner(System.in).nextLine();
				}
			}
			for (String i : done) {
				System.out.println("Case " + arguments[0] + i + " finished.");
			} 
		} else {
			boolean[][] geneVal = {{true, false, true, false, true},
					{false, false, false, false, false},
					{true, false, true, false, true},
					{false, false, false, false, false},
					{true, false, true, false, true}};
			boolean multSpeed, isIrregular;
			double[] u;
			if (arguments[1].equals("A")) {
				multSpeed = false;
				isIrregular = false;
				u = new double[] {12.0};
			} else if (arguments[1].equals("B")) {
				multSpeed = true;
				isIrregular = false;
				u = new double[] {8.0, 12.0, 17.0};
			} else if (arguments[1].equals("C")) {
				multSpeed = true;
				isIrregular = true;
				u = new double[] {8.0, 12.0, 17.0};
			} else {
				System.out.println("Invalid argument!");
				return;
			}
			
			//initialize genes
			gene[][] genes = new gene[geneVal.length][];
			for(int i=0;i<genes.length;i++) {
				genes[i] = new gene[geneVal[i].length];
			}
			for(int i=0;i<genes.length;i++) {
				for(int j=0;j<genes[i].length;j++) {
					genes[i][j] = new gene(geneVal.length, geneVal.length, i, j, multSpeed);
					genes[i][j].setTurbinePresence(geneVal[i][j]);
				}
			}
			
			//init chromosome
			chromosome c = new chromosome(genes, u, multSpeed, isIrregular);
			System.out.println(c.getFitness());
		}
		
	}
	
	public main(int N, boolean multipleWindSpeed, boolean isIrregular) throws IOException {
		
		GA obj = new GA(dim, dim, N, multipleWindSpeed, isIrregular);
		obj.startGA();
		
		LinkedList<chromosome> initialSol = obj.getDistinctIndividiuals();

		File file;
		if(isIrregular) {
			file = new File("./Results/C" + N + ".csv");
		} else if(multipleWindSpeed) {
			file = new File("./Results/B" + N + ".csv");
		} else {
			file = new File("./Results/A" + N + ".csv");
		}
		FileWriter writer = new FileWriter(file, true);

		writer.write("\n");
		writer.write("\n");
		
		LocalSearch ls = new LocalSearch();
		ls.startLocalSearchOn(initialSol);
		
		//localsearch
		System.out.println("Local Search");
		int[][] wtposition = new int[N][2];
		chromosome local = null;
		chromosome newlocal = null;
		Stack<chromosome> STACK = new Stack<chromosome>();
		
		for(chromosome chr : ls.mostSuperiorNeighbors) {
			System.out.println(N + "," + chr.getFitness());
			
			for(int i=0;i<chr.genes.length;i++) {
				for(int j=0;j<chr.genes[i].length;j++) {
					if(Integer.parseInt(chr.getPowerAt(i, j)) == 0) {
						writer.write("0 ");

					} else {
						writer.write("1 ");

					}
				}
				writer.write("\n");
			}
			local = chr;
			writer.write("\n");
			writer.write("\n");
		}
		
		int turbs = 0;
		
		for(int i=0;i<dim;i++) {
			for(int j=0;j<dim;j++) {
				if(local.isTurbinePresentA(i,j)) {
					System.out.print("1 ");
					wtposition[turbs][1] = i;
					wtposition[turbs][2] = j;
					turbs++;
				}else {
					System.out.print("0 ");
				}
			}
			System.out.print("\n");
		}
		
		chromosome[] listlocal = new chromosome[N*8];
		boolean localsearchIsRunning = true;
		int nums = 1;
		
		while(localsearchIsRunning) {
			
			System.out.println("iteration "+nums);
			
			for(int i=0;i<N;i++) {
				for(int j=0;j<8;j++) {
					
				}
			}
			localsearchIsRunning = false;
			nums++;
		}
		
		writer.close();
	}
}
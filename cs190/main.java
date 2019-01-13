package cs190;

import java.awt.Color;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Random;
import java.util.Scanner;

public class main{

	public static void main(String[] arguments) {
		arguments = new String[] {"A", "12", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14"};
		
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
			boolean[][] geneVal = {{true, true, true, true},
					{true, false, false, false, true},
					{true, false,  true, false, false},
					{true, false, false, false, false, true},
					{true, false, false,  true, false, false, true},
					{true, false,  true, false,  true, false, false},
					{true, false, false,  true, false,  true, false, true},
					{true, false, false, false, false, false, false, false, true},
					{true, false, false, false, false, false, false, false, true},
					{true, true, true, true, true, true, true, true, true, true}};
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
		
		GA obj = new GA(5, 5, N, multipleWindSpeed, isIrregular);
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
		
		for(chromosome chr : ls.mostSuperiorNeighbors) {
			System.out.println(N + "," + chr.getFitness());
			
			for(int i=0;i<chr.genes.length;i++) {
				for(int j=0;j<chr.genes[i].length;j++) {
					if(Integer.parseInt(chr.getPowerAt(i, j)) == 0) {
						writer.write(",");
					} else {
						writer.write("XXXX,");
					}
				}
				writer.write("\n");
			}

			writer.write("\n");
			writer.write("\n");
		}
		
		writer.close();
	}
}
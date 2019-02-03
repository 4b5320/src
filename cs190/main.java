package cs190;

import java.awt.Color;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Scanner;

public class main{
	int dim = 5;

	public static void main(String[] arguments) {
		
		arguments = new String[] {"A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25"};
		
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
			boolean[][] geneVal = new boolean[5][5];
			
			for(int i=0;i<geneVal.length;i++) {
				for(int j=0;j<geneVal[i].length;j++) {
					geneVal[i][j] = false;
				}
			}

			geneVal[2][2] = true;
			
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
			System.out.println(c.toString() + c.getFitness());
		}
		
	}
	
	public main(int N, boolean multipleWindSpeed, boolean isIrregular) throws IOException {
		
		GA obj = new GA(dim, dim, N, multipleWindSpeed, isIrregular);
		obj.startGA();
		
		LinkedList<chromosome> initialSol = obj.getDistinctIndividiuals();
		
		System.out.println(initialSol.getFirst().getFitness() + "\n" + initialSol.getFirst().toString());

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
		writer.close();
		
		/*if(isIrregular) {
			file = new File("./Results/C" + N + "-LS.csv");
		} else if(multipleWindSpeed) {
			file = new File("./Results/B" + N + "-LS.csv");
		} else {
			file = new File("./Results/A" + N + "-LS.csv");
		}
		writer = new FileWriter(file, true);
		
		LocalSearch ls = new LocalSearch();
		ls.startLocalSearchOn(initialSol, writer);
		
		writer.close();*/
	}
}
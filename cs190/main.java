package cs190;

import java.util.LinkedList;
import java.util.Random;

public class main{
	public static void main(String[] arguments) {
		int n = 4;
		GA obj = new GA(n, n, 12);
		obj.startGA();
		
		LinkedList<chromosome> initialSol = obj.getDistinctIndividiuals();
		/*LinkedList<chromosome> initialSol = new LinkedList<chromosome>();
		
		gene[][] genes = new gene[3][3];
		for(int i=0;i<genes.length;i++) {
			for(int j=0;j<genes[i].length;j++){
				genes[i][j] = new gene(genes.length, genes[i].length, i, j);
			}
		}
		
		genes[0][0].setTurbinePresence(true);
		genes[0][1].setTurbinePresence(false);
		genes[0][2].setTurbinePresence(true);
		genes[1][0].setTurbinePresence(true);
		genes[1][1].setTurbinePresence(false);
		genes[1][2].setTurbinePresence(false);
		genes[2][0].setTurbinePresence(true);
		genes[2][1].setTurbinePresence(false);
		genes[2][2].setTurbinePresence(true);
		
		true	false	true
		true	false	false
		true	false	true
		
		chromosome c = new chromosome(genes, 12);
		initialSol.add(c);*/
		
		LocalSearch ls = new LocalSearch();
		ls.startLocalSearchOn(initialSol);
		
		System.exit(0);
	}
}
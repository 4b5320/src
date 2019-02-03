package cs190;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class brute {
	FileWriter writer;

	public static void main(String[] args) {
		new File("Exhaustive Search").mkdir();
		
		new brute(24);
		System.out.println("Thread 1 - 24 - exhausted");
		
		//Manage the search here
		new searchManager(new File("Exhaustive Search/Exhaustic-24.csv"));
		System.out.println("Thread 1 - 24 - managed");
	}
	
	public brute(int r) {
		int[] arr = {0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24};
		int n = arr.length;
		printCombination(arr, n, r);
	}
	
	private void combinationUtil(int arr[], int data[], int start, int end, int index, int r) throws IOException {
		if(index == r) {
			//build the matrix here
			boolean[][] mat = new boolean[5][5];
			for(int i=0;i<mat.length;i++) {
				for(int k=0;k<mat[i].length;k++) {
					mat[i][k] = false;
				}
			}
			for(int j=0;j<r;j++) {
				//Assign genes here
				mat[data[j]/mat.length][data[j]%mat.length] = true;
			}
			
			//Compute fitness here
			//initialize genes
			gene[][] genes = new gene[mat.length][];
			for(int i=0;i<genes.length;i++) {
				genes[i] = new gene[mat[i].length];
			}
			for(int i=0;i<genes.length;i++) {
				for(int j1=0;j1<genes[i].length;j1++) {
					genes[i][j1] = new gene(mat.length, mat.length, i, j1,false);
					genes[i][j1].setTurbinePresence(mat[i][j1]);
				}
			}
			
			//init chromosome
			chromosome c = new chromosome(genes, new double[] {12.0}, false, false);
			writer.write((double) c.getFitness() + "\n" + c.toString() + "\n");
			return;
		}
		
		for(int i=start;i<=end && end-1>=r-index;i++) {
			data[index] = arr[i];
			combinationUtil(arr, data, i+1, end, index+1, r);
		}
	}
	
	private void printCombination(int arr[], int n, int r) {
		int[] data = new int[r];
		File file = new File("Exhaustive Search/Exhaustic-" + r + ".csv");
		try {
			writer = new FileWriter(file);
			combinationUtil(arr, data, 0, n-1, 0, r);
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}

package cs190;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class searchManager {
	
	public static void main(String[] args) {
		new searchManager();
	}
	
	public searchManager() {
		for(int i=12;i<25;i++) {
			File dir= new File("Compiled Search/Exhaustic-" + i + ".csv");
			double best = 1.0;
			for(File file : dir.listFiles()) {
				if(Double.compare(Double.parseDouble(file.getName()), best) < 0) {
					best = Double.parseDouble(file.getName());
				}
			}
			System.out.println("N=" + i + "\tobj=" + best);
		}
	}
	
	public searchManager(File file) {
		new File("Compiled Search/").mkdir();
		
		new File("Compiled Search/" + file.getName()).mkdir();
		try {
			Scanner scan = new Scanner(file);
			
			while(scan.hasNextLine()) {
				String[] row = scan.nextLine().split(",");
				if(row[0].length() > 0) {
					try {
						Double.parseDouble(row[0]);
						try {
							File currFitFile = new File("Compiled Search/" + file.getName() + "/" + row[0]);
							FileWriter fitWriter = new FileWriter(currFitFile, true);
							fitWriter.write("\n"
									+ scan.nextLine() + "\n"
									+ scan.nextLine() + "\n"
									+ scan.nextLine() + "\n"
									+ scan.nextLine() + "\n"
									+ scan.nextLine() + "\n");
							fitWriter.close();
						} catch (IOException e) {
							System.out.println("Failed to write to a file.");
							System.exit(0);
						}
					} catch(NumberFormatException e) {
						
					}
				}
			}
			
			scan.close();
		} catch (FileNotFoundException e) {
			System.out.println("No such file or directory.");
		}
		
	}
}

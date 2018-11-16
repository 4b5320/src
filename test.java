
import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;
import java.util.Random;

import javax.swing.JOptionPane;

public class test {

	public static void main(String[] args) {
		int n = 2;
		
		for(int i=1;i<=n;i++) {
			final int port = i;
			new Thread(new Runnable() {
				public void run() {
					new host(port);
				}
			}).start();
		}
		
	}
/*
 * Story - display on the chat box at the start of the game
 * 
 * Evidences - display on as list, (evidence name, evidence description)
 * 
 * witnesses - an evidence that only the prosecutor knows(witness name, witness' testimony)
 * Examples:
 * 	Story:
 * 		
 * 	Evidences:
 *		
 * 	
 * 	Witness:
 * 		
 * 
 * 
 * 
 * 
 * 
 * 
 * */
}


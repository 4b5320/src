
import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;

public class test {

	public static void main(String[] args) {
		new Thread(new Runnable() {
			public void run() {
				new host(1);
			}
		}).start();
		new Thread(new Runnable() {
			public void run() {
				new host(2);
			}
		}).start();
		new Thread(new Runnable() {
			public void run() {
				new host(3);
			}
		}).start();
		new Thread(new Runnable() {
			public void run() {
				new host(4);
			}
		}).start();
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


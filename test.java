
import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;

public class test {

	public static void main(String[] args) {
		new Thread(new Runnable() {
			public void run() {
				new host(5678);
			}
		}).start();
	}
/*
 * Story - display on the chat box at the start of the game
 * 
 * Evidences - display on as list, (evidence name, evidence description)
 * 
 * witnesses - an evidence that only the prosecutor knows(witness name, witness' testimony)
 * Example case 1:
 * 	Story: 
 * 		
 * 	Evidences:
 *		
 * 	
 * 	Witness:
 * 		
 *
 * 
 * */
}


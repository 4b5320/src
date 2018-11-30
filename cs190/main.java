package cs190;

import java.util.Random;

public class main{
	public static void main(String[] arguments) {
		new Thread(new Runnable() {
			public void run() {
				new GA(10, 10, 39).startGA();
			}
		}).start();
	}
}
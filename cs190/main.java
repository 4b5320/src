package cs190;

public class main{
	public static void main(String[] arguments) {
		GA obj = new GA(10, 10, 39);
		new Thread(new Runnable() {
			public void run() {
				obj.startGA();
			}
		}).start();
	}
}
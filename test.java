import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;
import java.util.Random;
import java.util.Scanner;

public class test {

	public static void main(String[] args) {
		/*new Thread(new Runnable() {
			public void run() {
				new host(5678);
			}
		}).start();*/
		
		int x = 10;
		int y=0;
		while (true) {
			for (int i = 0; i < x; i++) {
				if (new Random().nextFloat() < 0.7) {
					y++;
				}
			}
			y +=3;
			System.out.println(y);
			x = y;
			y = 0;
			//new Scanner(System.in).nextLine();
		}
	}

}


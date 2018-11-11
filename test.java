
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

}


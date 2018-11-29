package cs190;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;
import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.SwingConstants;

public class gui {

	private JFrame frame;
	private JLabel[][] matrix = new JLabel[10][10];

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		
		Thread t1 = new Thread(new Runnable() {
			public void run() {
				for(int i=0;i<10;i++) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				System.out.println("t1 loop has finished");
			}
		});
		
		Thread t2 = new Thread(new Runnable() {
			public void run() {
				for(int i=0;i<500;i++) {
					
				}
			}
		});
		
		t1.start();
		t2.start();
		try {
			t1.join();
			System.out.println("t1 has finished");
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		/*EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					gui window = new gui();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});*/
	}

	/**
	 * Create the application.
	 */
	public gui() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.getContentPane().setBackground(Color.WHITE);
		frame.setBounds(100, 100, 525, 545);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JLabel lblNewLabel = new JLabel("New label");
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setBounds(133, 152, 46, 14);
		frame.getContentPane().add(lblNewLabel);
		
		for(int i=0;i<matrix.length;i++) {
			for(int j=0;j<matrix[i].length;j++) {
				matrix[i][j] = new JLabel("");
				matrix[i][j].setOpaque(true);
				matrix[i][j].setBackground(Color.BLACK);
				matrix[i][j].setBounds(51*i, 51*j, 50, 50);
				final JLabel temp = matrix[i][j];
				temp.addMouseListener(new MouseAdapter() {
					public void mouseEntered(MouseEvent e) {
						temp.setBackground(Color.WHITE);
					}
					public void mouseExited(MouseEvent e) {
						temp.setBackground(Color.BLACK);
					}
				});
				frame.getContentPane().add(matrix[i][j]);
			}
		}
	}
}

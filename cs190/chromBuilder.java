package cs190;

import java.awt.Color;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.awt.event.ActionEvent;
import java.awt.Font;
import java.awt.Image;
import java.awt.Rectangle;

import javax.swing.Icon;
import javax.swing.JTextField;
import javax.swing.border.CompoundBorder;
import javax.swing.border.LineBorder;
import javax.swing.JLabel;

public class chromBuilder {

	private JFrame frame;
	private JTextField textField;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					chromBuilder window = new chromBuilder();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public chromBuilder() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 562, 339);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		ImageIcon icon = new ImageIcon(getClass().getClassLoader()
                .getResource("images/dot.png"));
		new File("Chromosomes").mkdir();
		
		JPanel panel = new JPanel();
		panel.setBorder(new CompoundBorder(new LineBorder(new Color(0, 0, 0)), null));
		panel.setBounds(20, 20, 260, 260);
		frame.getContentPane().add(panel);
		panel.setLayout(null);
		
		JButton btnNewButton = new JButton("CAPTURE!");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Rectangle rect = panel.getBounds();
				 
			    try {
			        BufferedImage captureImage =
			                new BufferedImage(rect.width, rect.height,
			                                    BufferedImage.TYPE_INT_ARGB);
			        panel.paint(captureImage.getGraphics());
			 
			        ImageIO.write(captureImage, "png", new File("Chromosomes/" + textField.getText() + "." + "png"));
			 
			        System.out.printf("The screenshot of %s was saved!", textField.getText() );
			    } catch (IOException ex) {
			        System.err.println(ex);
			    }
			}
		});
		btnNewButton.setFont(new Font("Times New Roman", Font.BOLD | Font.ITALIC, 20));
		btnNewButton.setBounds(290, 103, 245, 45);
		frame.getContentPane().add(btnNewButton);
		
		textField = new JTextField();
		textField.setFont(new Font("Times New Roman", Font.BOLD | Font.ITALIC, 20));
		textField.setBounds(290, 49, 245, 45);
		frame.getContentPane().add(textField);
		textField.setColumns(10);
		
		JLabel lblWriteFilenameHere = new JLabel("Write filename here");
		lblWriteFilenameHere.setFont(new Font("Times New Roman", Font.BOLD | Font.ITALIC, 15));
		lblWriteFilenameHere.setBounds(290, 20, 240, 18);
		frame.getContentPane().add(lblWriteFilenameHere);
		
		
		JButton[][] mat = new JButton[5][5];
		
		for(int i=0;i<mat.length;i++) {
			for(int j=0;j<mat[i].length;j++) {
				mat[i][j] = new JButton();
				mat[i][j].setIcon(null);
				mat[i][j].setBounds(i*50+5, j*50+5, 50, 50);
				mat[i][j].setBackground(Color.WHITE);
				panel.add(mat[i][j]);
				
				final int x = i, y = j;
				mat[i][j].addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						if(mat[x][y].getIcon() == null) {
							mat[x][y].setIcon(icon);
						} else {
							mat[x][y].setIcon(null);
						}
					}
				});
			}
		}
	}
}

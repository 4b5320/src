package test;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import javax.swing.JButton;
import java.awt.Color;
import java.awt.Font;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class chatUI{

	private JFrame frame;
	private JTextField textField;

	public chatUI() {
		initialize();
	}

	private void initialize() {
		JFrame frame = new JFrame();
		frame.setBounds(100, 100, 450, 357);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JPanel panel = new JPanel();
		panel.setLayout(null);
		frame.getContentPane().add(panel, BorderLayout.CENTER);
		
		JTextArea textArea = new JTextArea();
		textArea.setBounds(0, 0, 432, 265);
		panel.add(textArea);
		
		textField = new JTextField();
		textField.setBounds(10, 277, 343, 22);
		panel.add(textField);
		textField.setColumns(10);
		
		JButton btnNewButton = new JButton("New button");
		btnNewButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
			}
		});
		btnNewButton.setBounds(347, 278, 97, 25);
		panel.add(btnNewButton);
	}
}

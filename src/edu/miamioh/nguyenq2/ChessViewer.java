package edu.miamioh.nguyenq2;

import java.awt.FontFormatException;
import java.io.IOException;

import javax.swing.JFrame;

public class ChessViewer {
	public static void main(String[] args) throws FontFormatException, IOException {
		JFrame frame = new Chess();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		frame.setTitle("Chess");
	}
}

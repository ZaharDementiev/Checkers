package com.production;

import com.production.visual.WindowLayout;

import javax.swing.UIManager;

public class Main {

	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(
					UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		WindowLayout window = new WindowLayout();
		window.setDefaultCloseOperation(WindowLayout.EXIT_ON_CLOSE);
		window.setVisible(true);
	}
}

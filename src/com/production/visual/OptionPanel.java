package com.production.visual;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.production.player.ComputerPlayer;
import com.production.player.HumanPlayer;
import com.production.player.Player;

public class OptionPanel extends JPanel {
	private WindowLayout window;
	private final JButton restartBtn;
	private final JComboBox<String> player1Opts;
	private final JComboBox<String> player2Opts;
	public OptionPanel(WindowLayout window) {
		super(new GridLayout(0, 1));
		
		this.window = window;
		
		// Initialize the components
		OptionListener ol = new OptionListener();
		final String[] playerTypeOpts = {"Человек", "Компъютер"};
		this.restartBtn = new JButton("Рестарт");
		this.player1Opts = new JComboBox<>(playerTypeOpts);
		this.player2Opts = new JComboBox<>(playerTypeOpts);
		this.restartBtn.addActionListener(ol);
		this.player1Opts.addActionListener(ol);
		this.player2Opts.addActionListener(ol);
		JPanel top = new JPanel(new FlowLayout(FlowLayout.CENTER));
		JPanel middle = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		
		// Add components to the layout
		top.add(restartBtn);
		JLabel jLabel1 = new JLabel();
		jLabel1.setFont(new Font("GOST Common", Font.BOLD, 15));
		jLabel1.setText("(Синие) Игрок 1: ");
		JLabel jLabel2 = new JLabel();
		jLabel2.setFont(new Font("GOST Common", Font.BOLD, 15));
		jLabel2.setText("(Синие) Игрок 1: ");
		middle.add(jLabel1);
		jLabel2.setText("(Красные) Игрок 2: ");
		middle.add(player1Opts);
		middle.add(jLabel2);
		middle.add(player2Opts);
		this.add(top);
		this.add(middle);
		this.add(bottom);
	}

	public WindowLayout getWindow() {
		return window;
	}

	public void setWindow(WindowLayout window) {
		this.window = window;
	}

	private static Player getPlayer(JComboBox<String> playerOpts) {
		
		Player player = new HumanPlayer();
		if (playerOpts == null) {
			return player;
		}
		
		// Determine the type
		String type = "" + playerOpts.getSelectedItem();
		if (type.equals("Компъютер")) {
			player = new ComputerPlayer();
		}
		
		return player;
	}

	private class OptionListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			
			// No window to update
			if (window == null) {
				return;
			}
			
			Object src = e.getSource();

			// Handle the user action
			if (src == restartBtn) {
				window.restart();
			} else if (src == player1Opts) {
				Player player = getPlayer(player1Opts);
				window.setPlayer1(player);
			} else if (src == player2Opts) {
				Player player = getPlayer(player2Opts);
				window.setPlayer2(player);
			}
		}
	}
}

package com.production.visual;

import java.awt.*;

import javax.swing.JFrame;
import javax.swing.JPanel;

import com.production.player.Player;

public class WindowLayout extends JFrame {
	public static final int DEFAULT_WIDTH = 500;
	public static final int DEFAULT_HEIGHT = 600;
	public static final String DEFAULT_TITLE = "Шашки";
	private final BoardLayout board;

	public WindowLayout() {
		this(DEFAULT_WIDTH, DEFAULT_HEIGHT, DEFAULT_TITLE);
	}
	
	public WindowLayout(Player player1, Player player2) {
		this();
		setPlayer1(player1);
		setPlayer2(player2);
	}
	
	public WindowLayout(int width, int height, String title) {
		
		// Установка окна
		super(title);
		super.setSize(width, height);
		super.setLocationByPlatform(true);
		
		// Установка компонентов
		JPanel layout = new JPanel(new BorderLayout());
		this.board = new BoardLayout(this);
		OptionPanel opts = new OptionPanel(this);
		layout.add(board, BorderLayout.CENTER);
		layout.add(opts, BorderLayout.SOUTH);
		this.add(layout);
	}
	
	public BoardLayout getBoard() {
		return board;
	}

	public void setPlayer1(Player player1) {
		this.board.setPlayer1(player1);
		this.board.update();
	}

	public void setPlayer2(Player player2) {
		this.board.setPlayer2(player2);
		this.board.update();
	}

	public void restart() {
		this.board.getGame().restart();
		this.board.update();
	}
	
	public void setGameState(String state) {
		this.board.getGame().setGameState(state);
	}
}

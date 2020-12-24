package com.production.models;

import com.production.behaviour.MoveGenerator;
import com.production.behaviour.MoveLogic;

import java.awt.Point;
import java.util.List;

public class Game {
	private Board board;
	private boolean isFirstPlayer;
	private int skipPosition;
	
	public Game() {
		restart();
	}

	public void restart() {
		this.board = new Board();
		this.isFirstPlayer = true;
		this.skipPosition = -1;
	}

	private int makeMove(int startIndex, int endIndex) {
		Point middle = Board.middle(startIndex, endIndex);
		int midIndex = Board.toIndex(middle);
		this.board.set(endIndex, board.get(startIndex));
		this.board.set(midIndex, Board.EMPTY);
		this.board.set(startIndex, Board.EMPTY);
		return midIndex;
	}

	public boolean isGameOver() {
		List<Point> black = pointsInit(Board.BLACK_CHECKER, Board.BLACK_KING);
		List<Point> white = pointsInit(Board.WHITE_CHECKER, Board.WHITE_KING);
		if (black.isEmpty() || white.isEmpty())
			return true;
		List<Point> test = isFirstPlayer ? black : white;
		for (Point p : test) {
			int i = Board.toIndex(p);
			if (!MoveGenerator.getMoves(board, i).isEmpty() ||
					!MoveGenerator.getSkips(board, i).isEmpty()) {
				return false;
			}
		}
		return true;
	}

	public Game copy() {
		Game g = new Game();
		g.board = board.copy();
		g.isFirstPlayer = isFirstPlayer;
		g.skipPosition = skipPosition;
		return g;
	}

	public boolean move(Point start, Point end) {
		if (start == null || end == null) {
			return false;
		}
		return move(Board.toIndex(start), Board.toIndex(end));
	}

	public Board getBoard() {
		return board.copy();
	}

	private List<Point> pointsInit(int side, int king) {
		List<Point> checkers = board.find(side);
		checkers.addAll(board.find(king));
		return checkers;
	}
	
	public boolean isFirstPlayer() {
		return isFirstPlayer;
	}
	
	public int getSkipPosition() {
		return skipPosition;
	}

	public String getGameState() {
		StringBuilder state = new StringBuilder();
		for (int i = 0; i < Board.SQUARES; i ++) {
			state.append("").append(board.get(i));
		}
		state.append(isFirstPlayer ? "1" : "0");
		state.append(skipPosition);
		return state.toString();
	}

	public void setGameState(String state) {
		restart();
		if (state == null || state.isEmpty()) {
			return;
		}
		int n = state.length();
		for (int i = 0; i < Board.SQUARES && i < n; i ++) {
			try {
				int id = Integer.parseInt("" + state.charAt(i));
				this.board.set(i, id);
			} catch (NumberFormatException e) {}
		}
		if (n > Board.SQUARES) {
			this.isFirstPlayer = (state.charAt(Board.SQUARES) == '1');
		}
		if (n > Board.SQUARES + 1) {
			try {
				this.skipPosition = Integer.parseInt(state.substring(33));
			} catch (NumberFormatException e) {
				this.skipPosition = -1;
			}
		}
	}

	public boolean move(int startIndex, int endIndex) {
		if (!MoveLogic.isValidMove(this, startIndex, endIndex)) {
			return false;
		}
		int midIndex = makeMove(startIndex, endIndex);
		Point end = Board.toPoint(endIndex);
		int id = board.get(endIndex);
		boolean switchTurn = false;
		if (end.y == Board.MIN_BORD_SIZE && id == Board.WHITE_CHECKER) {
			this.board.set(endIndex, Board.WHITE_KING);
			switchTurn = true;
		} else if (end.y == Board.MAX_BORD_SIZE && id == Board.BLACK_CHECKER) {
			this.board.set(endIndex, Board.BLACK_KING);
			switchTurn = true;
		}
		boolean midValid = Board.isValidIndex(midIndex);
		if (midValid) {
			this.skipPosition = endIndex;
		}
		if (!midValid || MoveGenerator.getSkips(
				board.copy(), endIndex).isEmpty()) {
			switchTurn = true;
		}
		if (switchTurn) {
			this.isFirstPlayer = !isFirstPlayer;
			this.skipPosition = -1;
		}
		return true;
	}
}

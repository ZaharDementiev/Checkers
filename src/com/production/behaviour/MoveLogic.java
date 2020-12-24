package com.production.behaviour;

import com.production.models.Board;
import com.production.models.Game;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;


public class MoveLogic {

	public static boolean isSafe(Board board, Point checker) {
		if (board == null || checker == null || Board.toIndex(checker) < 0 || board.get(Board.toIndex(checker)) == Board.EMPTY) {
			return true;
		}
		int index = Board.toIndex(checker);
		int id = board.get(index);
		boolean isBlack = (id == Board.BLACK_CHECKER || id == Board.BLACK_KING);
		List<Point> check = new ArrayList<>();
		MoveGenerator.addPoints(check, checker, Board.BLACK_KING, 1);
		for (Point p : check) {
			int start = Board.toIndex(p);
			int tid = board.get(start);
			if (tid == Board.EMPTY || tid == Board.INVALID) {
				continue;
			}
			boolean isWhite = (tid == Board.WHITE_CHECKER || tid == Board.WHITE_KING);
			if (isBlack && !isWhite) {
				continue;
			}
			boolean isKing = (tid == Board.BLACK_KING);
			int dx = (checker.x - p.x) * 2;
			int dy = (checker.y - p.y) * 2;
			if (!isKing && (isWhite ^ (dy < 0))) {
				continue;
			}
			int endIndex = Board.toIndex(new Point(p.x + dx, p.y + dy));
			if (MoveGenerator.isSkipAvailable(board, start, endIndex)) {
				return false;
			}
		}
		return true;
	}

	public static boolean isValidMove(Game game, int startIndex, int endIndex) {
		return game != null && isValidMove(game.getBoard(),	game.isFirstPlayer(), startIndex, endIndex, game.getSkipPosition());
	}

	public static boolean isValidMove(Board board, boolean isP1Turn, int startIndex, int endIndex, int skipIndex) {
		if (board == null || !Board.isValidIndex(startIndex) ||	!Board.isValidIndex(endIndex)) {
			return false;
		} else if (startIndex == endIndex) {
			return false;
		} else if (Board.isValidIndex(skipIndex) && skipIndex != startIndex) {
			return false;
		}
		if (!validateIDs(board, isP1Turn, startIndex, endIndex)) {
			return false;
		} else return validateDistance(board, isP1Turn, startIndex, endIndex);
	}

	private static boolean validateDistance(Board board, boolean isP1Turn, int startIndex, int endIndex) {
		Point start = Board.toPoint(startIndex);
		Point end = Board.toPoint(endIndex);
		int dx = end.x - start.x;
		int dy = end.y - start.y;
		if (Math.abs(dx) != Math.abs(dy) || Math.abs(dx) > 2 || dx == 0) {
			return false;
		}
		int id = board.get(startIndex);
		if ((id == Board.WHITE_CHECKER && dy > 0) ||
				(id == Board.BLACK_CHECKER && dy < 0)) {
			return false;
		}
		Point middle = Board.middle(startIndex, endIndex);
		int midID = board.get(Board.toIndex(middle));
		if (midID < 0) {
			List<Point> checkers;
			if (isP1Turn) {
				checkers = board.find(Board.BLACK_CHECKER);
				checkers.addAll(board.find(Board.BLACK_KING));
			} else {
				checkers = board.find(Board.WHITE_CHECKER);
				checkers.addAll(board.find(Board.WHITE_KING));
			}
			for (Point p : checkers) {
				int index = Board.toIndex(p);
				if (!MoveGenerator.getSkips(board, index).isEmpty()) {
					return false;
				}
			}
		}
		return true;
	}

	private static boolean validateIDs(Board board, boolean isP1Turn, int startIndex, int endIndex) {
		if (board.get(endIndex) != Board.EMPTY) {
			return false;
		}
		int id = board.get(startIndex);
		if ((isP1Turn && id != Board.BLACK_CHECKER && id != Board.BLACK_KING)
				|| (!isP1Turn && id != Board.WHITE_CHECKER
				&& id != Board.WHITE_KING)) {
			return false;
		}
		Point middle = Board.middle(startIndex, endIndex);
		int midID = board.get(Board.toIndex(middle));
		return midID == Board.INVALID || ((isP1Turn ||
				midID == Board.BLACK_CHECKER || midID == Board.BLACK_KING) &&
				(!isP1Turn || midID == Board.WHITE_CHECKER ||
						midID == Board.WHITE_KING));
	}
}
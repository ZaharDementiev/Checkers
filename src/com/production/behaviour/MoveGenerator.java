package com.production.behaviour;

import com.production.models.Board;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;


public class MoveGenerator {

	public static void addPoints(List<Point> points, Point p, int id, int delta) {
		boolean isKing = (id == Board.BLACK_KING || id == Board.WHITE_KING);
		if (isKing || id == Board.BLACK_CHECKER) {
			points.add(new Point(p.x + delta, p.y + delta));
			points.add(new Point(p.x - delta, p.y + delta));
		}
		if (isKing || id == Board.WHITE_CHECKER) {
			points.add(new Point(p.x + delta, p.y - delta));
			points.add(new Point(p.x - delta, p.y - delta));
		}
	}

	private static List<Point> fillPoints(Board board, int startIndex, int delta) {
		List<Point> endPoints = new ArrayList<>();
		if (board == null || !Board.isValidIndex(startIndex)) {
			return endPoints;
		}
		int id = board.get(startIndex);
		Point p = Board.toPoint(startIndex);
		addPoints(endPoints, p, id, delta);
		return endPoints;
	}

	public static List<Point> getMoves(Board board, int startIndex) {
		List<Point> endPoints = fillPoints(board, startIndex, 1);
		for (int i = 0; i < endPoints.size(); i++) {
			Point end = endPoints.get(i);
			if (board.get(end.x, end.y) != Board.EMPTY) {
				endPoints.remove(i--);
			}
		}
		return endPoints;
	}

	public static List<Point> getSkips(Board board, int startIndex) {
		List<Point> endPoints = fillPoints(board, startIndex, 2);
		for (int i = 0; i < endPoints.size(); i ++) {
			Point end = endPoints.get(i);
			if (!isSkipAvailable(board, startIndex, Board.toIndex(end))) {
				endPoints.remove(i --);
			}
		}
		return endPoints;
	}

	public static boolean isSkipAvailable(Board board, int startIndex, int endIndex) {
		if (board == null || board.get(endIndex) != Board.EMPTY) {
			return false;
		}
		int id = board.get(startIndex);
		int midID = board.get(Board.toIndex(Board.middle(startIndex, endIndex)));
		if (id == Board.INVALID || id == Board.EMPTY) {
			return false;
		} else if (midID == Board.INVALID || midID == Board.EMPTY) {
			return false;
		} else {
			return (midID == Board.BLACK_CHECKER || midID == Board.BLACK_KING) == (id == Board.WHITE_CHECKER || id == Board.WHITE_KING);
		}
	}
}

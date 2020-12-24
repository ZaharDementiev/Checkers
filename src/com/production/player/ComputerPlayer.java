package com.production.player;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import com.production.behaviour.MoveGenerator;
import com.production.behaviour.MoveLogic;
import com.production.models.Board;
import com.production.models.Game;
import com.production.models.Move;

public class ComputerPlayer extends Player {

	private static final double SAFE = 3;
	private static final double UNSAFE = -5;
	private static final double KING_FACTOR = 2;
	private static final double SAFE_SAFE = 5;
	private static final double SAFE_UNSAFE = -40;
	private static final double UNSAFE_SAFE = 40;
	private static final double UNSAFE_UNSAFE = -40;
	private static final double WEIGHT_SKIP = 25;
	private static final double SKIP_ON_NEXT = 20;

	private List<Move> getMoves(Game game) {
		if (game.getSkipPosition() >= 0) {
			List<Move> moves = new ArrayList<>();
			List<Point> skips = MoveGenerator.getSkips(game.getBoard(),
					game.getSkipPosition());
			for (Point end : skips) {
				moves.add(new Move(game.getSkipPosition(), Board.toIndex(end)));
			}
			return moves;
		}
		List<Point> checkers = new ArrayList<>();
		Board b = game.getBoard();
		if (game.isFirstPlayer()) {
			checkers.addAll(b.find(Board.BLACK_CHECKER));
			checkers.addAll(b.find(Board.BLACK_KING));
		} else {
			checkers.addAll(b.find(Board.WHITE_CHECKER));
			checkers.addAll(b.find(Board.WHITE_KING));
		}
		List<Move> moves = new ArrayList<>();
		for (Point checker : checkers) {
			int index = Board.toIndex(checker);
			List<Point> skips = MoveGenerator.getSkips(b, index);
			for (Point end : skips) {
				Move m = new Move(index, Board.toIndex(end));
				m.changeWeight(WEIGHT_SKIP);
				moves.add(m);
			}
		}
		if (moves.isEmpty()) {
			for (Point checker : checkers) {
				int index = Board.toIndex(checker);
				List<Point> movesEnds = MoveGenerator.getMoves(b, index);
				for (Point end : movesEnds) {
					moves.add(new Move(index, Board.toIndex(end)));
				}
			}
		}
		return moves;
	}

	private void getMoveWeight(Game game, Move m) {
		Point start = m.getStart(), end = m.getEnd();
		int startIndex = Board.toIndex(start), endIndex = Board.toIndex(end);
		Board b = game.getBoard();
		boolean changed = game.isFirstPlayer();
		boolean safeBefore = MoveLogic.isSafe(b, start);
		int id = b.get(startIndex);
		boolean isKing = (id == Board.BLACK_KING || id == Board.WHITE_KING);
		m.changeWeight(getSafetyWeight(b, game.isFirstPlayer()));
		if (!game.move(m.getStartIndex(), m.getEndIndex())) {
			m.setWeight(Move.WEIGHT_INVALID);
			return;
		}
		b = game.getBoard();
		changed = (changed != game.isFirstPlayer());
		id = b.get(endIndex);
		isKing = (id == Board.BLACK_KING || id == Board.WHITE_KING);
		boolean safeAfter = true;
		if (changed) {
			safeAfter = MoveLogic.isSafe(b, end);
			int depth = getSkipDepth(game, endIndex, !game.isFirstPlayer());
			if (safeAfter) {
				m.changeWeight(SKIP_ON_NEXT * depth * depth);
			} else {
				m.changeWeight(SKIP_ON_NEXT);
			}
		}
		else {
			int depth = getSkipDepth(game, startIndex, game.isFirstPlayer());
			m.changeWeight(WEIGHT_SKIP * depth * depth);
		}
		if (safeBefore && safeAfter) {
			m.changeWeight(SAFE_SAFE);
		} else if (!safeBefore && safeAfter) {
			m.changeWeight(UNSAFE_SAFE);
		} else if (safeBefore && !safeAfter) {
			m.changeWeight(SAFE_UNSAFE * (isKing? KING_FACTOR : 1));
		} else {
			m.changeWeight(UNSAFE_UNSAFE);
		}
		m.changeWeight(getSafetyWeight(b,changed != game.isFirstPlayer()));
	}

	private int getSkipDepth(Game game, int startIndex, boolean isP1Turn) {
		if (isP1Turn != game.isFirstPlayer()) {
			return 0;
		}
		List<Point> skips = MoveGenerator.getSkips(game.getBoard(), startIndex);
		int depth = 0;
		for (Point end : skips) {
			int endIndex = Board.toIndex(end);
			game.move(startIndex, endIndex);
			int testDepth = getSkipDepth(game, endIndex, isP1Turn);
			if (testDepth > depth) {
				depth = testDepth;
			}
		}
		return depth + (skips.isEmpty()? 0 : 1);
	}

	private double getSafetyWeight(Board b, boolean isBlack) {
		double weight = 0;
		List<Point> checkers = new ArrayList<>();
		if (isBlack) {
			checkers.addAll(b.find(Board.BLACK_CHECKER));
			checkers.addAll(b.find(Board.BLACK_KING));
		} else {
			checkers.addAll(b.find(Board.WHITE_CHECKER));
			checkers.addAll(b.find(Board.WHITE_KING));
		}
		for (Point checker : checkers) {
			int index = Board.toIndex(checker);
			int id = b.get(index);
			boolean isKing = (id == Board.BLACK_KING || id == Board.WHITE_KING);
			if (MoveLogic.isSafe(b, checker)) {
				weight += SAFE;
			} else {
				weight += UNSAFE * (isKing? KING_FACTOR : 1);
			}
		}
		return weight;
	}

	@Override
	public boolean isHuman() {
		return false;
	}

	@Override
	public void updateGame(Game game) {
		if (game == null || game.isGameOver()) {
			return;
		}
		Game copy = game.copy();
		List<Move> moves = getMoves(copy);
		int n = moves.size(), count = 1;
		double bestWeight = Move.WEIGHT_INVALID;
		for (Move m : moves) {
			getMoveWeight(copy.copy(), m);
			if (m.getWeight() > bestWeight) {
				count = 1;
				bestWeight = m.getWeight();
			} else if (m.getWeight() == bestWeight) {
				count++;
			}
		}
		int move = ((int) (Math.random() * count)) % count;
		for (Move m : moves) {
			if (bestWeight == m.getWeight()) {
				if (move == 0) {
					game.move(m.getStartIndex(), m.getEndIndex());
				} else {
					move--;
				}
			}
		}
	}
}

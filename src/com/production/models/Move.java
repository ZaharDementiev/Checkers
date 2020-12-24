package com.production.models;

import java.awt.Point;

public class Move {
	public static final double WEIGHT_INVALID = Double.NEGATIVE_INFINITY;
	private byte startIndex;
	private byte endIndex;
	private double weight;
	
	public Move(int startIndex, int endIndex) {
		setStartIndex(startIndex);
		setEndIndex(endIndex);
	}
	
	public int getStartIndex() {
		return startIndex;
	}
	
	public void setStartIndex(int startIndex) {
		this.startIndex = (byte) startIndex;
	}
	
	public int getEndIndex() {
		return endIndex;
	}
	
	public void setEndIndex(int endIndex) {
		this.endIndex = (byte) endIndex;
	}
	
	public Point getStart() {
		return Board.toPoint(startIndex);
	}
	
	public Point getEnd() {
		return Board.toPoint(endIndex);
	}

	public double getWeight() {
		return weight;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}
	
	public void changeWeight(double weight) {
		this.weight += weight;
	}
}

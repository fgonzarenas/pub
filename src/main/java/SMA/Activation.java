package SMA;

import java.util.ArrayList;

import generator.Position;

public class Activation {
	
	/*
	 * The historic of positions crossed since the first cycle of SMA
	 */
	private ArrayList<Position> precedentPositions;
	/*
	 * The current position
	 */
	private Position myPosition;
	/*
	 * The positions where the path can be extended
	 */
	private ArrayList<Position> nextPositions;
	
	public Activation(ArrayList<Position> precPos, Position myPos) {
		precedentPositions = precPos;
		myPosition = myPos;
	}
	
	public void setNextPosition(Position nextPos) {
		if (nextPositions == null) {
			nextPositions = new ArrayList<Position>();
			nextPositions.add(nextPos);
		}
		else {
			nextPositions.add(nextPos);
		}
	}
	
	public ArrayList<Position> getPrecedentPositions() {
		return new ArrayList<Position>(precedentPositions);
	}
	
	public Position getMyPosition() {
		return myPosition;
	}
	
	public ArrayList<Position> getNextPositions() {
		return nextPositions;
	}
	
	public boolean hasNextPosition() {
		return nextPositions != null;
	}
}

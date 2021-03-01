package SMA;

import java.util.ArrayList;

import generator.Position;

public class Activation {
	private Position precedentPosition;
	private Position myPosition;
	private ArrayList<Position> nextPositions;
	
	public Activation() {
		
	}
	
	public void setPrecedentPosition(Position precPos) {
		precedentPosition = precPos;
	}
	
	public void setmyPosition(Position myPos) {
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
	
	public Position getPrecedentPosition() {
		return precedentPosition;
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

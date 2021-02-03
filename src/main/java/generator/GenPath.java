package generator;

import java.util.ArrayList;

public class GenPath {
	private ArrayList<Position> path;
	
	public GenPath() {
		path = new ArrayList<Position>();
	}
	
	public void addPosition(Position p) {
		path.add(p);
	}
	
	public ArrayList<Position> getPath() {
		return path;
	}
}

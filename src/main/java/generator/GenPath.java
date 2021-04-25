package generator;

import java.sql.Timestamp;
import java.util.ArrayList;

public class GenPath implements Comparable<GenPath> {
	
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
	
	public ArrayList<Timestamp> getTimestampList() {
		ArrayList<Timestamp> list = new ArrayList<Timestamp>();
		for (Position p : path)
			list.add(p.getTimestamp());
		return list;
	}
	
	@Override
	public int compareTo(GenPath p) {
		GenPath compare = (GenPath) p;
		if (path.size() == 0) 
			return -1;
		if (p.path.size() == 0) 
			return 1;
		return path.get(0).getTimestamp().compareTo(compare.path.get(0).getTimestamp());
	}
	
	public String toString() {
		String str = "[";
		for (Position p : path) 
			str += p.toString() + ";";
		return str.substring(0, str.length()-1) + "]";
	}
}

package generator;

public class PosEdge implements Comparable<PosEdge> {
	
	private Position firstPos;
	private Position nextPos;
	
	public PosEdge(Position from, Position to) {
		firstPos = from;
		nextPos = to;
	}
	
	public Position getFirstPos() {
		return firstPos;
	}
	
	public Position getNextPos() {
		return nextPos;
	}

	@Override
	public int compareTo(PosEdge pe) {
		PosEdge compare = (PosEdge) pe;
		return firstPos.getTimestamp().compareTo(compare.getFirstPos().getTimestamp());
	}
	
	public String toString() {
		return "{" + firstPos.toString() + "->" + nextPos.toString() + "}";
	}

}

package SMA;

import java.sql.Timestamp;
import java.util.ArrayList;

import fr.irit.smac.amak.Agent;

public class NodeAgent extends Agent<GraphAmas, GraphEnvironment> {
	
	private String nodeId;
	private ArrayList<Timestamp> timetable;
	
	public NodeAgent(GraphAmas amas, String node) {
		super(amas);
		nodeId = node;
		timetable = new ArrayList<Timestamp>();
	}
	
	@Override
	protected void onPerceive() {
		// TODO Auto-generated method stub
		super.onPerceive();
	}
	
	@Override
	protected void onDecide() {
		// TODO Auto-generated method stub
		super.onDecide();
	}
	
	@Override
	protected void onAct() {
		// TODO Auto-generated method stub
		super.onAct();
		if (timetable != null)
			System.out.println("[" + nodeId + "] nombre de passage : " + timetable.size());
	}
	
	public void setTimetable(ArrayList<Timestamp> timestampList) {
		timetable = timestampList;
	}
	
	public String getNodeId() {
		return nodeId;
	}
	
	public ArrayList<Timestamp> getTimetable() {
		return timetable;
	}
}

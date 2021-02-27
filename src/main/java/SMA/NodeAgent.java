package SMA;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import fr.irit.smac.amak.Agent;
import generator.Position;

public class NodeAgent extends Agent<GraphAmas, GraphEnvironment> {
	
	private String nodeId;
	private ArrayList<Timestamp> timetable;
		
	// True if this agent correspond to the final node of the path search
	private boolean isFinalNode;
	// True if the agent is active on this cycle (he has to search a next node on the path)
	private boolean isActive;
	
	private Timestamp activeTimestamp;
	// Regroup the information of the nodeAgent who has activate this nodeAgent and the date of passage on the precedent node
	private Position precedentPosition;
	// Next position, null if the agent hasn't found a next position
	private Position nextPosition; // MODIFIER EN ARRAYLIST
	// Regroup the activation history on each cycle
	private Map<Integer, ArrayList<Position>> activationHistory;
	
	
	public NodeAgent(GraphAmas amas, String node) {
		super(amas);
		nodeId = node;
		timetable = new ArrayList<Timestamp>();
		isActive = false;
		activationHistory = new HashMap<Integer, ArrayList<Position>>();
	}
	
	@Override
	protected void onPerceive() {
		super.onPerceive();
		nextPosition = null;
		if (isActive) {
			
			if (precedentPosition != null) 
				System.out.println("[" + nodeId + "] activate : " + activeTimestamp + " | precedentPosition : " + precedentPosition.getNode());
			else
				System.out.println("[" + nodeId + "] activate : " + activeTimestamp);
			
			for (Agent<GraphAmas, GraphEnvironment> neighboor : neighborhood) {
				NodeAgent neighboorAgent = (NodeAgent) neighboor;
				if (neighboorAgent.getTimetable() != null) {
					int i = 0;
					boolean found = false;
					while (!found && i<neighboorAgent.getTimetable().size()) {
						if (neighboorAgent.getTimetable().get(i).compareTo(activeTimestamp) > 0) {
							found = true;
							nextPosition = new Position(neighboorAgent.getNodeId(), neighboorAgent.getTimetable().get(i));
						}
						else {
							i++;
						}
					}
				}
			}
		}
		else {
			System.out.println("[" + nodeId + "] deactivate");
		}
	}
	
	@Override
	protected void onDecide() {
		// TODO Auto-generated method stub
		super.onDecide();
	}
	
	@Override
	protected void onAct() {
		super.onAct();
		/*
		if (timetable != null)
			System.out.println("[" + nodeId + "] nombre de passage : " + timetable.size() + " timetable : " + timetable);
		*/
		if (isActive) {
			NodeAgent nextAgent = getAmas().getAgentMap().get(nextPosition.getNode());
			nextAgent.activate(new Position(nodeId, activeTimestamp), nextPosition.getTimestamp());
		}
	}
	
	
	
	public void setTimetable(ArrayList<Timestamp> timestampList) {
		timetable = timestampList;
	}
	
	public void setFinalNode() {
		isFinalNode = true;
	}
	
	// Method to activate the agent for the next cycle, he will search a next position for the path
	public void activate(Position precedentPos, Timestamp myTimestamp) {
		isActive = true;
		activeTimestamp = myTimestamp;
		precedentPosition = precedentPos;
		if (activationHistory.containsKey(getEnvironment().getCycleNumber())) {
			activationHistory.get(getEnvironment().getCycleNumber()).add(precedentPosition);
		}
		else {
			ArrayList<Position> list = new ArrayList<Position>();
			list.add(precedentPosition);
			activationHistory.put(getEnvironment().getCycleNumber(), list);
		}
	}
	
	// method to deactivate the agent for the next cycle
	public void deactivate() {
		isActive = false;
	}
	
	public String getNodeId() {
		return nodeId;
	}
	
	public ArrayList<Timestamp> getTimetable() {
		return timetable;
	}
	
	public Map<Integer, ArrayList<Position>> getActivationHistory() {
		return activationHistory;
	}
}

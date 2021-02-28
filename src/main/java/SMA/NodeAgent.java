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
			//private ArrayList<Position> precedentPosition;
	// Next position, null if the agent hasn't found a next position
	private ArrayList<Position> nextPosition;
	// Regroup the activation history on each cycle. The Position contains the node and the activeTimestamp of the agent who has activate this agent
	private Map<Integer, ArrayList<Position>> activationHistory;
	
	
	public NodeAgent(GraphAmas amas, String node) {
		super(amas);
		nodeId = node;
		timetable = new ArrayList<Timestamp>();
		isActive = false;
		nextPosition = new ArrayList<Position>();
		activationHistory = new HashMap<Integer, ArrayList<Position>>();
	}
	
	@Override
	protected void onPerceive() {
		super.onPerceive();
		nextPosition.clear();
		if (isActive) {
			
			/*
			 *  TRACE D'AFFICHAGE
			 */
			if (activationHistory.containsKey(getEnvironment().getCycleNumber()-1)) {
				int cycleNumberPrec = getEnvironment().getCycleNumber()-1;
				System.out.print("[" + nodeId + "] activate : " + activeTimestamp + " | cycleNumberPrec : " + cycleNumberPrec + " | precedentPosition : ");
				for (Position p : activationHistory.get(getEnvironment().getCycleNumber()-1)) {
					System.out.print(p.getNode() + " ");
				}
				System.out.println();
			}
			else
				System.out.println("[" + nodeId + "] activate : " + activeTimestamp);
			/*
			 *  FIN TRACE D'AFFICHAGE
			 */
			
			for (Agent<GraphAmas, GraphEnvironment> neighboor : neighborhood) {
				NodeAgent neighboorAgent = (NodeAgent) neighboor;
				if (neighboorAgent.getTimetable() != null && neighboorAgent.getNodeId() != nodeId) {
					int i = 0;
					boolean found = false;
					while (!found && i<neighboorAgent.getTimetable().size()) {
						if (neighboorAgent.getTimetable().get(i).compareTo(activeTimestamp) > 0) {
							found = true;
							nextPosition.add(new Position(neighboorAgent.getNodeId(), neighboorAgent.getTimetable().get(i)));
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
		super.onDecide();
	}
	
	@Override
	protected void onAct() {
		super.onAct();
		if (isActive) {
			for (Position nextP : nextPosition) {
				NodeAgent nextAgent = getAmas().getAgentMap().get(nextP.getNode());
				nextAgent.activate(new Position(nodeId, activeTimestamp), nextP.getTimestamp());
			}
		}
		if (activationHistory.containsKey(getEnvironment().getCycleNumber())) {
			deactivate();
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
		if (getEnvironment().getCycleNumber() != -1)
			System.out.println("   ACTIVATE [" + nodeId + "] on cycle : " + getEnvironment().getCycleNumber() + " | by : " + precedentPos.getNode());
		if (activationHistory.containsKey(getEnvironment().getCycleNumber())) {
			activationHistory.get(getEnvironment().getCycleNumber()).add(precedentPos);
		}
		else {
			ArrayList<Position> list = new ArrayList<Position>();
			list.add(precedentPos);
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

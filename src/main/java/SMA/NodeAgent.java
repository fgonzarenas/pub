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
	// Regroup the activation history on each cycle. The Position contains the node and the activeTimestamp of the agent who has activate this agent
	private Map<Integer, ArrayList<Activation>> activationHistory;
	
	
	public NodeAgent(GraphAmas amas, String node) {
		super(amas);
		nodeId = node;
		timetable = new ArrayList<Timestamp>();
		isActive = false;
		activationHistory = new HashMap<Integer, ArrayList<Activation>>();
	}
	
	@Override
	protected void onPerceive() {
		super.onPerceive();
		if (isActive && !isFinalNode ) {
			
			/*
			 *  TRACE D'AFFICHAGE
			 */
			if (activationHistory.containsKey(getEnvironment().getCycleNumber()-1)) {
				int cycleNumberPrec = getEnvironment().getCycleNumber()-1;
				System.out.print("[" + nodeId + "] activate | cycleNumberPrec : " + cycleNumberPrec + " | precedentPosition : ");
				for (Activation a : activationHistory.get(getEnvironment().getCycleNumber()-1)) {
					System.out.print(a.getPrecedentPosition().getNode() + " " + a.getPrecedentPosition().getTimestamp());
				}
				System.out.println();
			}
			else
				System.out.println("[" + nodeId + "] activate");
			/*
			 *  FIN TRACE D'AFFICHAGE
			 */
			
			// Search if there is candidates to next nodes
			for (Activation a : activationHistory.get(getEnvironment().getCycleNumber()-1)) {
				Timestamp activeTimestamp = a.getMyPosition().getTimestamp();
				
				for (Agent<GraphAmas, GraphEnvironment> neighboor : neighborhood) {
					NodeAgent neighboorAgent = (NodeAgent) neighboor;
					
					if (neighboorAgent.getTimetable() != null && neighboorAgent.getNodeId() != nodeId && neighboorAgent.getNodeId() != a.getPrecedentPosition().getNode()) {
						int i = 0;
						boolean found = false;
						while (!found && i<neighboorAgent.getTimetable().size()) {
							if (neighboorAgent.getTimetable().get(i).compareTo(activeTimestamp) > 0) {
								found = true;
								a.setNextPosition(new Position(neighboorAgent.getNodeId(), neighboorAgent.getTimetable().get(i)));
								System.out.println("   [" + nodeId + "] setNextPosition : " + neighboorAgent.getNodeId());
							}
							else {
								i++;
							}
						}
					}
				}
				
			}
		}
		// Recomposition of the path
		else if (isActive && isFinalNode ) {
			/*
			for (Activation a : activationHistory.get(getEnvironment().getCycleNumber()-1)) {
				int cycleNumber = getEnvironment().getCycleNumber()-1;
				String currentNode = nodeId;
				String previousNode = a.getPrecedentPosition().getNode();
				ArrayList<String> path = new ArrayList<String>();
				path.add(currentNode);
				
				while (previousNode != null) {
					cycleNumber--;
					// list of activations of the precedent node
					ArrayList<Activation> activationList = getAmas().getAgentMap().get(previousNode).getActivationHistory().get(cycleNumber);
					while 
					
					
					currentNode = previousNode;
					path.add(currentNode);
					// previousNode = ... ;;;
				}
				
			}
			*/
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
		/*
		 * Activation des noeuds suivants
		 */
		if (isActive && activationHistory.containsKey(getEnvironment().getCycleNumber()-1)) {

			System.out.println("   [" + nodeId + "] act | taille activationHistory : " + activationHistory.get(getEnvironment().getCycleNumber()-1).size());
			
			for (Activation a : activationHistory.get(getEnvironment().getCycleNumber()-1)) {
				if (a.hasNextPosition()) {
					System.out.println("      [" + nodeId + "] act | taille nextPosition : " + a.getNextPositions().size());
					for (Position nextPos : a.getNextPositions()) {
						NodeAgent nextAgent = getAmas().getAgentMap().get(nextPos.getNode());
						nextAgent.activate(a.getMyPosition(), nextPos);
					}
				}
			}
		}
		if (!activationHistory.containsKey(getEnvironment().getCycleNumber())) {
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
	public void activate(Position precedentPos, Position myPosition) {
		isActive = true;
		
		if (getEnvironment().getCycleNumber() == -1)
			System.out.println("   ACTIVATE [" + nodeId + "] on cycle : " + getEnvironment().getCycleNumber() + " | precedentPosition : " + precedentPos.getNode() + " | myPosition : " + myPosition.getNode() + " " + myPosition.getTimestamp());
		else 
			System.out.println("   ACTIVATE [" + nodeId + "] on cycle : " + getEnvironment().getCycleNumber() + " | by : " + precedentPos.getNode());
		
		Activation a = new Activation();
		a.setPrecedentPosition(precedentPos);
		a.setmyPosition(myPosition);
		
		if (activationHistory.containsKey(getEnvironment().getCycleNumber())) {
			activationHistory.get(getEnvironment().getCycleNumber()).add(a);
		}
		else {
			ArrayList<Activation> list = new ArrayList<Activation>();
			list.add(a);
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
	
	public Map<Integer, ArrayList<Activation>> getActivationHistory() {
		return activationHistory;
	}
}

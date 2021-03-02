package SMA;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import fr.irit.smac.amak.Agent;
import generator.Position;

public class NodeAgent extends Agent<GraphAmas, GraphEnvironment> {
	
	/*
	 * On each cycle, a NodeAgent can be activated or disabled. If the agent is disabled 
	 * it will do nothing. 
	 * A NodeAgent "b" can be activated by another NodeAgents "a" if the NodeAgent "a" discovered 
	 * that it could continue his path on node "b". A NodeAgent could be activated by several 
	 * NodeAgents on the same cycle. If the agent is activated, it will look for a next node where 
	 * it could extend the path and it will activate the associated NodeAgents
	 */
	
	private String nodeId;
	private ArrayList<Timestamp> timetable;
		
	// True if this agent correspond to the final node of the path search
	private boolean isFinalNode;
	// True if the agent is active on this cycle (he has to search a next node on the path)
	private boolean isActive;
	/* 
	 * Regroup the activation history on each cycle. One activation contains the path traveled, the current 
	 * node (here nodeId) and the neighboring nodes on which it is possible to continue this path
	 */ 
	private Map<Integer, ArrayList<Activation>> activationHistory;
	
	
	public NodeAgent(GraphAmas amas, String node) {
		super(amas);
		nodeId = node;
		timetable = new ArrayList<Timestamp>();
		isActive = false;
		activationHistory = new HashMap<Integer, ArrayList<Activation>>();
	}
	
	// Check if node is already in the path
	private boolean notCrossedNode(ArrayList<Position> path, String node) {
		for (Position p : path) {
			if (p.getNode() == node)
				return false;
		}
		return true;
	}
	
	@Override
	protected void onPerceive() {
		super.onPerceive();
		if (isActive && !isFinalNode ) {
			
			/*
			 *  LOG
			 */
			if (getEnvironment().hasLog()) {
				if (activationHistory.containsKey(getEnvironment().getCycleNumber()-1)) {
					int cycleNumberPrec = getEnvironment().getCycleNumber()-1;
					System.out.print("[" + nodeId + "] activate | cycleNumberPrec : " + cycleNumberPrec + " | precedentPosition : ");
					for (Activation a : activationHistory.get(getEnvironment().getCycleNumber()-1)) {
						if (getEnvironment().getCycleNumber() != 0)
							System.out.print(a.getPrecedentPositions().get(a.getPrecedentPositions().size()-1).getNode() + " " 
									+ a.getPrecedentPositions().get(a.getPrecedentPositions().size()-1).getTimestamp() + " ");
					}
					System.out.println();
				}
				else
					System.out.println("[" + nodeId + "] activate");
			}
			
			
			// Search if there is candidates to next nodes for each activation
			for (Activation a : activationHistory.get(getEnvironment().getCycleNumber()-1)) {
				Timestamp activeTimestamp = a.getMyPosition().getTimestamp();
				
				// Explore all neighboring NodeAgents
				for (Agent<GraphAmas, GraphEnvironment> neighboor : neighborhood) {
					NodeAgent neighboorAgent = (NodeAgent) neighboor;
					
					if (neighboorAgent.getTimetable() != null 
							// the next node is different of this node
							&& neighboorAgent.getNodeId() != nodeId 
							// the next node isn't already in the path
							&& notCrossedNode(a.getPrecedentPositions(), neighboorAgent.getNodeId())) {
						
						int i = 0;
						boolean found = false;
						// Explore the timetable of the NodeAgent
						while (!found && i<neighboorAgent.getTimetable().size()) {
							Timestamp ts = neighboorAgent.getTimetable().get(i);
							/*
							 * If a timestamp greater than the active timestamp of this agent is found and with a waiting 
							 * period less than the waiting maximum, the following position is validated
							 */
							 
							if (ts.compareTo(activeTimestamp) > 0 && (ts.getTime() - activeTimestamp.getTime()) <= getAmas().getMaxWaitingTime()) {
								found = true;
								a.setNextPosition(new Position(neighboorAgent.getNodeId(), neighboorAgent.getTimetable().get(i)));
								
								if (getEnvironment().hasLog())
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
		// final node of the path research
		else if (isActive && isFinalNode ) {
			if (getEnvironment().hasLog())
				System.out.println("[" + nodeId + "] PATH FOUND !");
			
			for (Activation a : activationHistory.get(getEnvironment().getCycleNumber()-1)) {
				System.out.print("   PATH : ");
				for (Position p : a.getPrecedentPositions()) {
					System.out.print(p.getNode() + " ");
				}
				System.out.println(getNodeId());				
			}
		}
		else {
			if (getEnvironment().hasLog())
				System.out.println("[" + nodeId + "] disable");
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
			if (getEnvironment().hasLog())
				System.out.println("   [" + nodeId + "] act | taille activationHistory : " 
						+ activationHistory.get(getEnvironment().getCycleNumber()-1).size());
			
			for (Activation a : activationHistory.get(getEnvironment().getCycleNumber()-1)) {
				// Si un noeud suivant a été trouvé
				if (a.hasNextPosition()) {
					if (getEnvironment().hasLog())
						System.out.println("      [" + nodeId + "] act | taille nextPosition : " + a.getNextPositions().size());
					
					for (Position nextPos : a.getNextPositions()) {
						// Recherche de l'agent suivant
						NodeAgent nextAgent = getAmas().getAgentMap().get(nextPos.getNode());
						// Ajout de ce noeud au chemin
						ArrayList<Position> path = a.getPrecedentPositions();
						path.add(a.getMyPosition());
						// Activation de l'agent suivant pour le prochain cycle
						nextAgent.activate(path, nextPos);
					}
				}
			}
		}
		// On desactive l'agent s'il n'a pas ete active sur ce cycle
		if (!activationHistory.containsKey(getEnvironment().getCycleNumber())) {
			disable();
		}
	}
	
	
	
	public void setTimetable(ArrayList<Timestamp> timestampList) {
		timetable = timestampList;
	}
	
	public void setFinalNode() {
		isFinalNode = true;
	}
	
	// Method to activate the agent for the next cycle, he will search a next position for the path
	public void activate(ArrayList<Position> precedentPos, Position myPosition) {
		isActive = true;
		
		/*
		 *  LOG
		 */
		if (getEnvironment().hasLog()) {
			if (getEnvironment().getCycleNumber() == -1)
				System.out.println("   ACTIVATE [" + nodeId + "] on cycle : " + getEnvironment().getCycleNumber());
			else 
				System.out.println("   ACTIVATE [" + nodeId + "] on cycle : " + getEnvironment().getCycleNumber() 
						+ " | by : " + precedentPos.get(precedentPos.size()-1).getNode());
		}
		
		
		Activation a = new Activation(precedentPos, myPosition);
		
		if (activationHistory.containsKey(getEnvironment().getCycleNumber())) {
			activationHistory.get(getEnvironment().getCycleNumber()).add(a);
		}
		else {
			ArrayList<Activation> list = new ArrayList<Activation>();
			list.add(a);
			activationHistory.put(getEnvironment().getCycleNumber(), list);
		}
	}
	
	// method to disable the agent for the next cycle
	public void disable() {
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

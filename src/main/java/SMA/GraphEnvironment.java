package SMA;


import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Map;

import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.MultiGraph;

import fr.irit.smac.amak.Environment;
import fr.irit.smac.amak.Scheduling;
import generator.Parser;


public class GraphEnvironment extends Environment {
	
	private Graph graph;
	private String graphFilename;
	private String agentMoveFilename;
	private Map<String, ArrayList<Timestamp>> timestampMap;
	
	// attribute use to count the number of cycle
	private int cycleNumber;
	
	// true if we want to display log
	private boolean log;
	
	
	public GraphEnvironment(String dgsFileName, String moveFilename, boolean log) {
		super(Scheduling.DEFAULT);
		graphFilename = "road-networks/" + dgsFileName;
		graph = new MultiGraph(graphFilename);
		agentMoveFilename = moveFilename;
		cycleNumber = -1;
		initGraph();
		
		this.log = log;
	}
	
	private void initGraph() {
		try {
			System.out.println("filename : " + graphFilename);
			graph.read(graphFilename);
		} 
		catch(Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		Parser p = new Parser(agentMoveFilename);
		p.parse();
		timestampMap = p.translate();
	}
	
	
	@Override
	public void onInitialization() {
		
	}
	
	@Override
	public void onCycle() {
		super.onCycle();
		cycleNumber++;
		System.out.println("---------------------------------");
		System.out.println("Cycle : " + cycleNumber);
	}
	
	
	public int getCycleNumber() {
		return cycleNumber;
	}
	
	public Graph getGraph() {
		return graph;
	}
	
	public Map<String, ArrayList<Timestamp>> getTimestampMap() {
		return timestampMap;
	}
	
	public boolean hasLog() {
		return log;
	}
	
	
	
	public static void main(String[] args) {
		GraphEnvironment env = new GraphEnvironment("GraphTest.dgs", "test_serial.json", false);
		GraphAmas amas = new GraphAmas(env);
		
		String start = "M";
		String end = "B";
		//long maxWaitingTime = 15*60000; // 15min in milliseconds
		long maxWaitingTime = 120*60000;
		amas.initPathSearch(start, end, maxWaitingTime);
		
		while (env.getCycleNumber() < env.getGraph().nodes().count())
			amas.getScheduler().step();
		
		ArrayList<ArrayList<String>> pathList = amas.getPathList();
		System.out.println("\nAcceptable path from " + start + " to " + end + " : " + pathList);
	}
}	

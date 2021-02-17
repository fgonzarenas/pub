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
	
	public GraphEnvironment(String dgsFileName, String moveFilename) {
		super(Scheduling.DEFAULT);
		graphFilename = "road-networks/" + dgsFileName;
		graph = new MultiGraph(graphFilename);
		agentMoveFilename = moveFilename;
		initGraph();
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
		// TODO Auto-generated method stub
		super.onCycle();
		System.out.println("---------------------------------");
	}
	
	public Graph getGraph() {
		return graph;
	}
	
	public Map<String, ArrayList<Timestamp>> getTimestampMap() {
		return timestampMap;
	}
	
	public static void main(String[] args) {
		GraphEnvironment env = new GraphEnvironment("GraphTest.dgs", "test_serial.json");
		new GraphAmas(env);
	}
}	

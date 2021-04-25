package generator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Random;
import java.io.FileWriter;
import java.sql.Timestamp;

import org.graphstream.graph.*;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.algorithm.*;
import org.graphstream.algorithm.Dijkstra.Element;

import translator.GUI;
import translator.TrafficTranslator;

public class Generator {
	private Graph graph;
	
	private ArrayList<String> startNodes;
	private ArrayList<String> endNodes;
	private int idCounter;
	
	private int nbAgent;
	private int nbPath;
	
	private int startYear;
	private int startMonth;
	private int startDay;
	private int startHour;
	private int endHour;

	public static int NB_AGENT = 0;
	public static int NB_PATH = 1;
	public static int START_YEAR = 2;
	public static int START_MONTH = 3;
	public static int START_DAY = 4;
	public static int START_HOUR = 5;
	public static int END_HOUR = 6;
	
	public Generator(String graph_filename, int nbA, int nbP, int startY, int startM, int startD, int startH, int endH) {
		graph = new MultiGraph(graph_filename);
		
		startNodes = new ArrayList<String>(Arrays.asList("I","J","K","L","M","O","P","Q","R"));
		endNodes = new ArrayList<String>(Arrays.asList("A","B","C","D","E","F","G","H","N"));
		idCounter = 1;
		
		nbAgent = nbA;
		nbPath = nbP;
		startYear = startY;
		startMonth = startM;
		startDay = startD;
		startHour = startH;
		endHour = endH;
	}
	
	// Shortest path between two nodes using Dijkstra
	public Path shortestPath(String n1, String n2) {
		Dijkstra dijkstra = new Dijkstra(Element.EDGE, null, "length");
		dijkstra.setSource(graph.getNode(n1));
		dijkstra.init(graph);
 		dijkstra.compute();
 		
 		Path path = dijkstra.getPath(graph.getNode(n2));
 		
 		return path;
	}
	
	public Path pathWithVariation(String n1, String n2) {
		// TODO
		return null;
	}
	
	public Agent createAgent() {
		Agent a = new Agent(idCounter);
		idCounter++;
		
		// random choice of starting and ending node
		Random random = new Random(System.currentTimeMillis());
		String start = startNodes.get(random.nextInt(startNodes.size()));
		String end = endNodes.get(random.nextInt(endNodes.size()));
		
		// generation of starting date
		int hour = random.nextInt(endHour-startHour) + startHour;
		int min = random.nextInt(60);
		int sec = random.nextInt(60);
		Calendar startCal = Calendar.getInstance();
		startCal.setTimeInMillis(0);
		startCal.set(startYear, startMonth, startDay, hour, min, sec);			
		
		for (int i=0; i<nbPath; i++) {
			// creation of the path
			Path path = null;
			
			// creation of the Calendar for this path
			Calendar travelCalendar = (Calendar) startCal.clone();
			
			path = shortestPath(start,end);
			travelCalendar.add(Calendar.MINUTE, random.nextInt(10));
			// agent takes the same path often but sometimes goes to another destination
			/*
			if(random.nextInt(7) < 5) {
				path = shortestPath(start,end);
				travelCalendar.add(Calendar.MINUTE, random.nextInt(5));
			} else {
				
				// agent does not travel sometimes
				if(random.nextInt(3) < 1) {
					continue;
				}
				
				// go to random destination at random time
				path = shortestPath(start, endNodes.get(random.nextInt(endNodes.size())));
				travelCalendar.add(Calendar.HOUR_OF_DAY, random.nextInt(12));
				travelCalendar.add(Calendar.MINUTE, random.nextInt(60));
			}
			*/
			
			travelCalendar.add(Calendar.DATE, i);			
			travelCalendar.add(Calendar.SECOND, random.nextInt(60));
			
			GenPath p = new GenPath();
			List<Node> nodeList = path.getNodePath();
			List<Edge> edgeList = path.getEdgePath();
			
			p.addPosition(new Position(nodeList.get(0).getId(), travelCalendar));
			
			for (int j=0; j<edgeList.size(); j++) {
				Integer travelTime = (Integer) edgeList.get(j).getAttribute("length");
				travelCalendar.add(Calendar.MINUTE, travelTime);
				travelCalendar.add(Calendar.SECOND, random.nextInt(60));
				p.addPosition(new Position(nodeList.get(j+1).getId(), travelCalendar));
			}
			
			a.addPath(p);
		}
		
		return a;
	}
	
	// generate a list of nbAgent Agent
	public ArrayList<Agent> generate() {
		ArrayList<Agent> result = new ArrayList<Agent>();
		
		for (int i=0; i<nbAgent; i++)
			result.add(createAgent());
		
		return result;
	}
    
    public Graph getGraph()
    {
    	return graph;
    }
    
    public int[] getConfig()
    {
    	return new int[]
    	{
    		nbAgent,
    		nbPath,
    		startYear,
    		startMonth,
    		startDay,
    		startHour,
    		endHour
    	};
    }
    
    public static void main(final String[] args) {
    	int startYear = 2021;
		int startMonth = 0;
		int startDay = 1;
		int startHour = 0;
		int endHour = 10;
		String filename = "road-networks/GraphTest_oriented.dgs";
		int step = 15;
		int nbAgents = 100;
		int nbPaths = 1;
    	
    	Generator g = new Generator(filename, nbAgents, nbPaths, startYear, startMonth, startDay, startHour, endHour);
    	Graph graph = g.getGraph();
    	int[] config = g.getConfig();
    	
    	GUI view = new GUI(filename, graph);
    	TrafficTranslator tt = new TrafficTranslator(graph, config);
    	
    	view.init();
    	
        ArrayList<Agent> listAgent = g.generate();
        int[][] traffic = tt.pathsAsTraffic(listAgent, step);
        // g.writeCSV(traffic, step, "traffic_data_oriented.csv");
        
        view.displayAllTraffic(traffic, nbPaths * nbAgents);
        // Display paths
        /*
        for(int i = 0; i < traffic.length; i++)
        {
        	view.displayTraffic(traffic, i, (float) Math.log(nbAgents));
        	view.sleep(1000);
        }
        */

    }
}

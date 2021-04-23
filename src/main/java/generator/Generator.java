package generator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.io.FileWriter;
import java.sql.Timestamp;

import org.graphstream.graph.*;
import org.graphstream.algorithm.*;
import org.graphstream.algorithm.Dijkstra.Element;

import translator.GUI;

public class Generator {
	private GUI view;
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
	
	
	public Generator(String graph_filename, int nbA, int nbP, int startY, int startM, int startD, int startH, int endH) {
		//view = new GUI("GraphTest.dgs");
		view = new GUI(graph_filename);
		view.init();
		graph = view.getGraph();
		
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
	
	// function to slow down the display
    protected void sleep(int millis) {
        try { 
        	Thread.sleep(millis); 
        } catch (Exception e) {
        	e.printStackTrace();
        }
    }
    
    // Traffic across entire network every 'step' minutes
    public int[][] asTraffic(ArrayList<Agent> listAgent, int step)
    {
    	int total_mins = nbPath * 24 * 60;
        int data_size = total_mins / step;
        int traffic[][] = new int[data_size][graph.getEdgeCount()]; 
        int per_path = 24 * 60 / step;
        int per_hour = 60 / step;
        
    	for(Agent agent : listAgent)
        {	
        	for(int ipath = 0; ipath < agent.getPath().size(); ipath++)
        	{	
        		GenPath gp = agent.getPath().get(ipath);
        		ArrayList<Position> path = gp.getPath();
        		
        		for(int i = 0; i < path.size() - 1; i++)
        		{
        			// Edge id is source node id concatenated to target node id
        			String source = path.get(i).getNode();
        			String target = path.get(i+1).getNode();
        				
        			Edge edge = graph.getEdge(source + target);
        				
        			// If edge not found, invert source and target id
        			if(edge == null)
        			{
        				edge = graph.getEdge(target + source);
        			}
        			
        			int iedge = edge.getIndex();
        			
        			// Get closest time value at 'step' interval
        			Calendar time_source = Calendar.getInstance();
        			time_source.setTimeInMillis(path.get(i).getTimestamp().getTime());
        			
        			int interval_s = time_source.get(Calendar.MINUTE) / step;
        			
        			// Add count
        			int offset = ipath * per_path + time_source.get(Calendar.HOUR_OF_DAY) * per_hour;
        			traffic[offset + interval_s][iedge] += 1;
        			
        			if(i+1 == path.size() - 1)
        			{
        				Calendar time_target = Calendar.getInstance();
            			time_target.setTimeInMillis(path.get(i+1).getTimestamp().getTime());
            			int interval_t = time_target.get(Calendar.MINUTE) / step;
	        			offset = ipath * per_path + time_target.get(Calendar.HOUR_OF_DAY) * per_hour;
	        			traffic[offset + interval_t][iedge] += 1;
        			}
        		}
        	}
        }
    	/*
    	for(int i = 0; i < traffic.length; i++)
    	{
    		System.out.print(i + " ");
    		
    		for(int j = 0; j < traffic[i].length; j++)
    		{
    			System.out.print(traffic[i][j] + " ");
    		}
    		System.out.println();
    	}
    	*/
    	return traffic;
    }
    
    
    // Get traffic across entire network ever 'step' minutes as a csv string 
    public String trafficAsCSV(int[][] traffic, int step)
    {
    	String csv = "";
    	Calendar timestamp = Calendar.getInstance();
    	timestamp.setTimeInMillis(0);
    	timestamp.set(startYear, startMonth, startDay, 0, 0, 0);
    	int features = traffic[0].length;
    	
    	for(int i = 0; i < traffic.length; i++)
    	{  		
    		csv += new Timestamp(timestamp.getTimeInMillis()) + ",";
    				
    		for(int j = 0; j < features-1; j++)
    		{
    			csv += traffic[i][j] + ",";
    		}
    		
    		csv += traffic[i][features-1] + "\n";
    		
    		timestamp.add(Calendar.MINUTE, step); 
    	}
    	
    	return csv;
    }
    
    // Write traffic across entire network ever 'step' minutes to a csv file 
    public void writeCSV(int[][] traffic, int step, String filename)
    {
        String csv = trafficAsCSV(traffic, step);
        
    	try {
    	      FileWriter writer = new FileWriter(filename);
    	      writer.write(csv);
    	      writer.close();
    	      System.out.println("Successfully wrote csv file...");
    	    } catch (Exception e) {
    	      e.printStackTrace();
    	    }
    }
    
    // Display traffic across all recorded timestamps 
	public void displayAllTraffic(int[][] traffic)
	{
        // Accumulate passages
        for(int i = 0; i < traffic.length; i++)
        {
        	for(int i_edge = 0; i_edge < traffic[i].length; i_edge++)
        	{
        		Edge edge = graph.getEdge(i_edge);
        		
        		float passages = ((float) edge.getNumber("ui.color")) + traffic[i][i_edge];
    			
    			edge.setAttribute("ui.color", passages);
        	}
        }
        
        // Normalize
        graph.edges().forEach(edge -> {
        	edge.setAttribute("ui.color", ((float) edge.getNumber("ui.color")) / (nbAgent * nbPath));
    	});
	}
	
	// Display traffic on a specific timestamp 
		public void displayTraffic(int[][] traffic, int i)
		{
			// Normalize according to this (somewhat arbitrary)
			float div = (float) Math.log(nbAgent);

	        // Set number of passages and normalize
			for(int i_edge = 0; i_edge < traffic[i].length; i_edge++)
	        {
	        	Edge edge = graph.getEdge(i_edge);
	        	
	        	float passages = traffic[i][i_edge] / div;
	    			
	    		edge.setAttribute("ui.color", passages);
	        }
		}
	
    
    
    public static void main(final String[] args) {
    	int startYear = 2021;
		int startMonth = 0;
		int startDay = 1;
		int startHour = 0;
		int endHour = 23;
		String filename = "road-networks/GraphTest_oriented.dgs";
		int step = 15;
    	
    	Generator g = new Generator(filename, 100, 1, startYear, startMonth, startDay, startHour, endHour);
    	
    	// Set initial edge color
    	g.graph.edges().forEach(edge -> {
    		edge.setAttribute("ui.color", 0.f);
    	});
    	
        ArrayList<Agent> listAgent = g.generate();
        int[][] traffic = g.asTraffic(listAgent, step);
        
        // Display paths
        for(int i = 0; i < traffic.length; i++)
        {
        	g.displayTraffic(traffic, i);
        	g.sleep(1000);
        }

    }
}

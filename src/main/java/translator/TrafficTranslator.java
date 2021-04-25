package translator;

import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;

import com.opencsv.CSVReader;

import generator.Agent;
import generator.GenPath;
import generator.Generator;
import generator.Position;

public class TrafficTranslator 
{
	private Graph graph;
	private int[] config;
	
	public TrafficTranslator(Graph graph, int[] config)
	{
		this.graph = graph;
		this.config = config;
	}
	
	// Traffic across entire network every 'step' minutes
    public int[][] pathsAsTraffic(ArrayList<Agent> listAgent, int step)
    {
    	int nbPath = config[Generator.NB_PATH];
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
    
    
    // Get traffic across entire network every 'step' minutes as a csv string 
    public String[] trafficAsCSV(int[][] traffic, int step)
    {
    	int startYear = config[Generator.START_YEAR];
    	int startMonth = config[Generator.START_MONTH];
    	int startDay = config[Generator.START_DAY];
    	
    	String[] csv = new String[traffic.length];
    	Calendar timestamp = Calendar.getInstance();
    	timestamp.setTimeInMillis(0);
    	timestamp.set(startYear, startMonth, startDay, 0, 0, 0);
    	int features = traffic[0].length;

    	
    	for(int i = 0; i < traffic.length; i++)
    	{  		
    		csv[i] = new Timestamp(timestamp.getTimeInMillis()) + ",";
    				
    		for(int j = 0; j < features-1; j++)
    		{
    			csv[i] += traffic[i][j] + ",";
    		}
    		
    		csv[i] += traffic[i][features-1] + "\n";
    		
    		timestamp.add(Calendar.MINUTE, step); 
    	}

    	return csv;
    }
    
    // Write traffic across entire network ever 'step' minutes to a csv file 
    public void writeCSV(int[][] traffic, String filename, int step)
    {
        String[] csv = trafficAsCSV(traffic, step);
        int features = traffic[0].length;
  
		String header = "timestamp,";
		
    	for(int i = 0; i < features-1; i++)
    	{
    		header += Integer.toString(i) + ",";
    	}
    	
    	header += Integer.toString(features-1) + "\n";
        
    	try 
    	{
    		FileWriter fileWriter = new FileWriter(filename);
    		
    		fileWriter.write(header);
			fileWriter.flush();
			
    		for(int i = 0; i < csv.length; i++)
    		{
    			fileWriter.write(csv[i]);
    			fileWriter.flush();
    		}

			fileWriter.close();
			System.out.println("Successfully wrote csv file...");
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    }
    
    // Converts csv file to array
    public static int[][] csvToArray(String filename)
    {    	
    	try 
    	{
    		CSVReader reader = new CSVReader(new FileReader(filename));
    		
    		// Skip header
    		String[] nextLine = reader.readNext();
    		
    		int features = nextLine.length - 1;
    		int timesteps = (int) Files.lines(Paths.get(filename)).count() - 1;
    		int[][] traffic = new int[timesteps][features];
    		int timestep = 0;
    		
    		while((nextLine = reader.readNext()) != null)
    		{
    			for(int i = 1; i < nextLine.length; i++)
    			{
    				traffic[timestep][i-1] = Integer.parseInt(nextLine[i]);		
    			}
    			
    			timestep++; 
    		}

			reader.close();
			return traffic;
			
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    	
    	return null;
    }
    
    // Get all timestamps
    public static String[] getTimestamps(String filename)
    {    	
    	try 
    	{
    		CSVReader reader = new CSVReader(new FileReader(filename));
    		
    		// Skip header
    		String[] nextLine = reader.readNext();
    		
    		int timesteps = (int) Files.lines(Paths.get(filename)).count() - 1;
    		String[] timestamps = new String[timesteps];
    		int timestep = 0;
    		
    		while((nextLine = reader.readNext()) != null)
    		{
    			timestamps[timestep] = nextLine[0];		
    			timestep++; 
    		}

			reader.close();
			return timestamps;
			
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    	
    	return null;
    }
}

package learning;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;

import generator.Agent;
import generator.GenPath;
import generator.Parser;
import generator.Position;

public class Preprocessing {
	
	private String pathFilename;
	private ArrayList<String> graphPath;
	private ArrayList<ArrayList<Timestamp>> dataTimestamp;
	
	public Preprocessing(String filename) {
		pathFilename = filename;
		parsing();
	}
	
	private void parsing() {
		Parser p = new Parser(pathFilename);
		p.parse();
		ArrayList<Agent> al = p.getAgentList();
		
		graphPath = new ArrayList<String>();
		dataTimestamp = new ArrayList<ArrayList<Timestamp>>();
		
		ArrayList<GenPath> pathList = new ArrayList<GenPath>();
		
		// Initialization of dataTimestamp
		for (Agent a : al) {
			for (GenPath path : a.getPath())
				pathList.add(path);
		}
		
		// Sorting path by start timestamp
		Collections.sort(pathList);
		
		for (GenPath path : pathList) 
			dataTimestamp.add(path.getTimestampList());

		ArrayList<Position> onePath = al.get(0).getPath().get(0).getPath();
		for (Position pos : onePath)
			graphPath.add(pos.getNode());
		
		System.out.println("graphPath : " + graphPath);
		System.out.println("dataTimestamp : " + dataTimestamp.size());
	}
	
	public void writeToCSV(String filename, String separator) {		
		try {
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename+".csv"), "UTF-8"));
			
			// head
			StringBuffer line = new StringBuffer();
			for (int i=0; i<graphPath.size(); i++) {
				line.append(graphPath.get(i));
				if (i != graphPath.size()-1)
					line.append(separator);
			}
			bw.write(line.toString());
			bw.newLine();
			
            for (ArrayList<Timestamp> path : dataTimestamp) {
                line = new StringBuffer();
                for (int i=0; i<path.size(); i++) {
                	//line.append(path.get(i).getTime());
                	line.append(path.get(i).toString());
                	if (i != path.size()-1)
    					line.append(separator);
                }
                bw.write(line.toString());
                bw.newLine();
            }
            bw.flush();
            bw.close();
        }
        catch (Exception e) {
        	
        }
		System.out.println("Successfully write to CSV File...");
	}
	
	public static void main(final String[] args) {
		String filename = "one_path.json";
		Preprocessing p = new Preprocessing(filename);
		p.writeToCSV("test", ";");
	}
}

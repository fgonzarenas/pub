package generator;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
//import com.google.gson.stream.JsonWriter;

public class Serializer {
	
	public static void main(final String[] args) { 
		GsonBuilder builder = new GsonBuilder();
	    Gson gson = builder.setPrettyPrinting().create();
	    
	    ArrayList<Agent> listAgent = new ArrayList<Agent>();
	    Agent a1 = new Agent(1);
	    a1.addPosition(new Position("A", new Timestamp(System.currentTimeMillis())));
	    a1.addPosition(new Position("B", new Timestamp(System.currentTimeMillis())));
	    listAgent.add(a1);
	    Agent a2 = new Agent(2);
	    a2.addPosition(new Position("C", new Timestamp(System.currentTimeMillis())));
	    a2.addPosition(new Position("D", new Timestamp(System.currentTimeMillis())));
	    listAgent.add(a2);
	    		
		try {
			FileWriter file = new FileWriter("test_serial.json");
		    //JsonWriter jsonWriter = new JsonWriter(file);
			gson.toJson(listAgent, ArrayList.class, file);
			//gson.toJson(a1, Agent.class, file);
			//gson.toJson(a2, Agent.class, file);
			file.close();
			System.out.println("Successfully Copied JSON Object to File...");
	    } catch (final IOException e) {
	    	e.printStackTrace();
	    }
	}
}

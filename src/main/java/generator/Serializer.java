package generator;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Serializer {
	private ArrayList<Agent> listAgent;

	private GsonBuilder builder;
	private Gson gson;
	private String filename;
	private Generator generator;
	
	public Serializer(String file) {
		filename = file;
		listAgent = new ArrayList<Agent>();
		initGson();
	}
	
	protected void initGson() {
		builder = new GsonBuilder();
	    gson = builder.setPrettyPrinting().create();
	}
	
	public void addGenerator(Generator g) {
		generator = g;
	}
	
	public void run() {
		// generation of agent
		listAgent = generator.generate();
	}
	
	public void setListAgent(ArrayList<Agent> list) {
		listAgent = list;
	}
	
	public void serialize() {
		// serialization
		try {
			FileWriter file = new FileWriter(filename);
			gson.toJson(listAgent, ArrayList.class, file);
			file.close();
			System.out.println("Successfully Copied listAgent to JSON File...");
	    } catch (final IOException e) {
	    	e.printStackTrace();
	    }	
	}
}

package generator;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Serializer {
	private ArrayList<Agent> listAgent;
	private int nbAgent;
	private GsonBuilder builder;
	private Gson gson;
	private String filename;
	
	public Serializer(int nbA, String file) {
		nbAgent = nbA;
		filename = file;
		listAgent = new ArrayList<Agent>();
		initGson();
	}
	
	protected void initGson() {
		builder = new GsonBuilder();
	    gson = builder.setPrettyPrinting().create();
	}
	
	public void serialize() {
		// generation of agent
		Generator g = new Generator();
		listAgent = g.generate(nbAgent);
		
		// serialization
		try {
			FileWriter file = new FileWriter(filename);
			gson.toJson(listAgent, ArrayList.class, file);
			file.close();
			System.out.println("Successfully Copied JSON listAgent to File...");
	    } catch (final IOException e) {
	    	e.printStackTrace();
	    }	
	}
	
	public static void main(final String[] args) {
		Serializer s = new Serializer(10, "test_serial.json");
		s.serialize();
	}
}

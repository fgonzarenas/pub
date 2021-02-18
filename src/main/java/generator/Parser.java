package generator;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;

public class Parser {
	private String filename;
	private ArrayList<Agent> listAgent;
	private Gson gson;
	
	public Parser(String file) {
		filename = file;
		gson = new Gson();
	}
	
	public void parse() {
		try (Reader reader = new FileReader(filename)) {
            // Convert JSON File to list of agents
			Agent[] tabAgent = gson.fromJson(reader, Agent[].class);
			listAgent = new ArrayList<Agent>(Arrays.asList(tabAgent));
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
	
	// Parcours tous les chemins des agents pour retourner une Map associant une liste de Timestamp à chaque noeud
	public Map<String, ArrayList<Timestamp>> translate() {
		Map<String, ArrayList<Timestamp>> resultMap = new HashMap<String, ArrayList<Timestamp>>();
		
		for (Agent a : listAgent) {
			for (GenPath path : a.getPath()) {
				for (Position pos : path.getPath()) {
					String node = pos.getNode();
					// Ajout de la position à la Map
					if (resultMap.containsKey(node)) {
						resultMap.get(node).add(pos.getTimestamp());
					}
					else {
						ArrayList<Timestamp> timestampList = new ArrayList<Timestamp>();
						timestampList.add(pos.getTimestamp());
						resultMap.put(node, timestampList); 
					}
				}
			}
		}
		
		return resultMap;
	}
	
	public ArrayList<Agent> getAgentList() {
		return listAgent;
	}
	
	public static void main(final String[] args) {
		Parser p = new Parser("test_serial.json");
		p.parse();
		System.out.println(p.translate());
	}
}

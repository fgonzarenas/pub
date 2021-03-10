package generator;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
	public Map<String, ArrayList<Timestamp>> translate2() {
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
		
		// Sorting lists of Timestamps
		for (String node : resultMap.keySet()) {
			Collections.sort(resultMap.get(node));
		}
		
		return resultMap;
	}
	
	public Map<String, Map<String, ArrayList<PosEdge>>> translate() {
		Map<String, Map<String, ArrayList<PosEdge>>> resultMap = new HashMap<String, Map<String, ArrayList<PosEdge>>>();
		
		for (Agent a : listAgent) {
			for (GenPath path : a.getPath()) {
				int pathLength = path.getPath().size();
				for (int i=0; i<pathLength-1; i++) {
					Position originPos = path.getPath().get(i);
					Position nextPos = path.getPath().get(i+1);
					PosEdge e = new PosEdge(originPos, nextPos);
					
					// On récupère la Map associé au noeud d'origine de l'arête
					Map<String, ArrayList<PosEdge>> originMap;
					if (resultMap.containsKey(originPos.getNode()))
						originMap = resultMap.get(originPos.getNode());
					else {
						originMap = new HashMap<String, ArrayList<PosEdge>>();
						resultMap.put(originPos.getNode(), originMap);
					}
					
					// On ajoute l'arête à la liste des arêtes pour ce noeud
					if (originMap.containsKey(nextPos.getNode())) {
						originMap.get(nextPos.getNode()).add(e);
					}
					else {
						ArrayList<PosEdge> edgeList = new ArrayList<PosEdge>();
						edgeList.add(e);
						originMap.put(nextPos.getNode(), edgeList);
					}
				}
			}
		}
		
		// Sorting lists of edges
		for (String originNode : resultMap.keySet()) {
			Map<String, ArrayList<PosEdge>> originMap = resultMap.get(originNode);
			for (String nextNode : originMap.keySet())
				Collections.sort(originMap.get(nextNode));
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

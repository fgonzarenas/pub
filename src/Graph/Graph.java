package Graph;

import java.util.ArrayList;
import java.util.Random;

public abstract class Graph
{
	protected int nb_nodes;
	protected int nb_edges;

	// Edges of a node can be accessed directly with it's id
	protected final ArrayList<Node> nodes;
	protected final ArrayList<ArrayList<Edge>> edges;
	
	protected NodeType n_type;
	protected EdgeType e_type;
	
	public final static int MAX_NODES = 100;
	public final static int MIN_NODES = 5;
	public final static int MAX_CONNECT = 4;
	public final static int MIN_EDGE_LEN = 1;
	public final static int MAX_EDGE_LEN = 100;
	
	public final static int MAX_X = 500;
	public final static int MAX_Y = 500;
	
	public final Random rand;
	
	public Graph(int nb_nodes, NodeType n_type, EdgeType e_type)
	{		
		this.nb_nodes = nb_nodes;
		this.nb_edges = 0;
		
		this.n_type = n_type;
		this.e_type = e_type;
		
		nodes = new ArrayList<Node>();
		edges = new ArrayList<ArrayList<Edge>>(nb_nodes);
		
		nodes.ensureCapacity(nb_nodes);
		edges.trimToSize();
		
		rand = new Random(System.currentTimeMillis());
	}
	
	public Graph(NodeType n_type, EdgeType e_type)
	{
		rand = new Random(System.currentTimeMillis());
		nb_nodes = rand.nextInt(MAX_NODES - MIN_NODES) + MIN_NODES;
		
		this.n_type = n_type;
		this.e_type = e_type;
		
		nodes = new ArrayList<Node>();
		edges = new ArrayList<ArrayList<Edge>>(nb_nodes);
		
		nodes.ensureCapacity(nb_nodes);
		edges.trimToSize();
	}
	
	public abstract void init();
	
	public abstract void addRoads(int cur_node);
	
	public Tuple<Point, Edge> intersects(Point origin, Point dest)
	{
		Point tmp, o2, d2;
		Point inter_pt = null;
		Edge inter_edge = null;
		
		// Check if road from 'origin' to 'dest' intersects any existing roads
		for(var list : edges)
			for(var edge : list)
			{
				o2 = nodes.get(edge.getId1()).getPosition();
				d2 = nodes.get(edge.getId2()).getPosition();
				
				tmp = Point.intersects(origin, dest, o2, d2);
				
				// Update intersection point if it's closer than last intersection found
				if(tmp != null && (inter_pt == null || distance(tmp, origin) < distance(inter_pt, origin)))
				{
					inter_pt = tmp;
					inter_edge = edge;
				}	
			}
		
		// Return intersection info if any was found
		if(inter_pt != null)
		{
			return new Tuple<Point, Edge>(inter_pt, inter_edge);
		}
		
		return null;
	}
	
	public void cut(Edge edge, int id, Point position)
	{
		int id2 = edge.getId2();
		Point pos_id2 = nodes.get(id2).getPosition();
		
		// Cut first half of original edge at intersection point
		edge.setId2(id);
		Point pos_id1 = nodes.get(edge.getId1()).getPosition();
		edge.setLength(distance(pos_id1, position));
		
		// Add second half of the edge starting from the intersection point
		Edge new_edge = Edge.newInstance(e_type, 0, id, id2);
		edge.setLength(distance(position, pos_id2));
		edges.get(id).add(new_edge);
	}
	
	public double distance(Point a, Point b)
	{
		return Math.sqrt(Math.pow(b.getPosX() - a.getPosX(), 2) + Math.pow(b.getPosY() - a.getPosY(), 2));
	}
	
	public ArrayList<Node> getNodes()
	{
		return nodes;
	}
	
	public ArrayList<ArrayList<Edge>> getEdges()
	{
		return edges;
	}
}

class RandomGraph extends Graph
{
	public RandomGraph(int nb_nodes, NodeType n_type, EdgeType e_type)
	{
		super(nb_nodes, n_type, e_type);
	}
	
	public RandomGraph(NodeType n_type, EdgeType e_type)
	{
		super(n_type, e_type);
	}
	
	@Override
	public void init()
	{
		Random rand = new Random();
		
		// Create initial node
		double x = rand.nextDouble() * MAX_X;
		double y = rand.nextDouble() * MAX_Y;
		nodes.add(Node.newInstance(n_type, 0, x, y));
		
		// Add entry in edges for current node
		for(int i = 0; i < nb_nodes; i++)
		{
			edges.add(new ArrayList<Edge>());
		}
		
		// Build nodes as long as the max number of nodes hasn't been reached
		for(int i = 0; nodes.size() < nb_nodes; i++)
		{ 
			// Try to create rand roads for each new node
			addRoads(i);
		}
	}
	
	@Override
	public void addRoads(int cur_node)
	{
		int n_roads = rand.nextInt(MAX_CONNECT) + 1;
		Point cur_pos = nodes.get(cur_node).getPosition();
		Tuple<Point, Edge> inter;
		
		for(int i = 1; i <= n_roads && nodes.size() < nb_nodes; i++)
		{	
			// Create new road randomly
			double len = rand.nextDouble() * MAX_EDGE_LEN + MIN_EDGE_LEN;
			double angle = rand.nextDouble() * Math.PI * 2;
			double x = Math.max(0, Math.min((Math.cos(angle) * len) + cur_pos.getPosX(), MAX_X));
			double y = Math.max(0, Math.min((Math.sin(angle) * len) + cur_pos.getPosY(), MAX_Y));
			Point dest_pos = new Point(x, y);
			int id = nodes.size();
			
			// Check if road intersects
			inter = intersects(cur_pos, dest_pos);
			
			// If it does:
			// - make the new road only reach the intersection point
			// - cut intersected edge in half at the intersection point
			if(inter != null)
			{
				dest_pos = inter.getFirst();
				len = distance(cur_pos, dest_pos);
				cut(inter.getSecond(), id, dest_pos);
			}
			
			// Add new node and new road
			nodes.add(Node.newInstance(n_type, id, x, y));
			edges.get(cur_node).add(Edge.newInstance(e_type, len, cur_node, id));
		}
	}
}
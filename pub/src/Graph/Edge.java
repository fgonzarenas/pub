package Graph;

enum EdgeType
{
	DEFAULT;
}

public abstract class Edge
{
	private double length;
	private int id1;
	private int id2;
	
	public Edge(double len, int i1, int i2)
	{
		this.length = len;
		id1 = i1;
		id2 = i2;
	}
	
	public static Edge newInstance(EdgeType type, double len, int i1, int i2)
	{
		switch(type)
		{
			case DEFAULT: return new DefaultEdge(len, i1, i2);
		}
		
		throw new RuntimeException("Instance of this type cannot be initialized.");
	}

	public int getId1()
	{
		return id1;
	}

	public int getId2() 
	{
		return id2;
	}
	
	public void setId2(int new_dest)
	{
		id2 = new_dest;
	}
	
	public void setLength(double new_len)
	{
		length = new_len;
	}
}

class DefaultEdge extends Edge
{
	public DefaultEdge(double len, int i1, int i2)
	{
		super(len, i1, i2);
	}
}

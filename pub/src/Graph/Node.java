package Graph;

enum NodeType
{
	DEFAULT;
}

public abstract class Node
{
	private final int id;
	private final Point position;
	
	public Node(int id, double x, double y)
	{
		this.id = id;
		position = new Point(x, y);
	}
	
	public static Node newInstance(NodeType type, int id, double x, double y)
	{
		switch(type)
		{
			case DEFAULT: return new DefaultNode(id, x, y);
		}
		
		throw new RuntimeException("Instance of this type cannot be initialized.");
	}
	
	public double getPosX() 
	{
		return position.getPosX();
	}

	public double getPosY() 
	{
		return position.getPosY();
	}
	
	public Point getPosition()
	{
		return position;
	}
	
}

class DefaultNode extends Node
{
	public DefaultNode(int id, double x, double y)
	{
		super(id, x, y);
	}
}


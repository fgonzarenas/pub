package Graph;

import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.util.LinkedList;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JPanel;
import javax.swing.JScrollBar;

public class View
{
	private Graph graph;
	
	private JFrame frame;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) 
	{
		Graph model = new Graph(NodeType.DEFAULT, EdgeType.DEFAULT);
		model.init();
		
		EventQueue.invokeLater( new Runnable() 
		{
			public void run() 
			{
				try 
				{
					View window = new View(model);
					window.frame.setVisible(true);
				} 
				catch (Exception e) 
				{
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public View(Graph graph) 
	{
		this.graph = graph;
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() 
	{
		frame = new JFrame();
		frame.setBounds(100, 100, Graph.MAX_X, Graph.MAX_Y);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		DisplayGraph panel = new DisplayGraph();
		frame.getContentPane().add(panel, BorderLayout.CENTER);
	}
	
	class DisplayGraph extends JPanel
	{		
		private LinkedList<Shape> shapes;
		
		public DisplayGraph()
		{
			shapes = new LinkedList<Shape>();
			
			// Add each shape to the shapes list
			for(Node node : graph.getNodes())
			{
				double x = node.getPosX();
				double y = node.getPosY();
				shapes.add(new Circle(x, y));
				
				for(Edge edge : graph.getEdges().get(node.getId()))
				{
					double x2 = graph.getNodes().get(edge.getId2()).getPosX();
					double y2 = graph.getNodes().get(edge.getId2()).getPosY();
					shapes.add(new Line(x, y, x2, y2));
				}
			}
		}
		
		@Override
		public void paintComponent(Graphics g)
		{		
			for(Shape shape : shapes)
			{
				shape.draw(g);
			}	
		}
	}
	
	abstract class Shape
	{
		protected double x, y;
		
		public Shape(double x, double y)
		{
			this.x = x;
			this.y = y;
		}
		
		public abstract void draw(Graphics g);
	}
	
	class Circle extends Shape
	{	
		public Circle(double x, double y)
		{
			super(x, y);
		}
		
		@Override
		public void draw(Graphics g)
		{
			Graphics2D g2d = (Graphics2D) g;
			Ellipse2D.Double circle = new Ellipse2D.Double(x - 2.5, y - 2.5, 5, 5); 
			g2d.setColor(Color.RED);
			g2d.fill(circle);
		}
	}

	class Line extends Shape
	{
		private double x2, y2;
		
		public Line(double x1, double y1, double x2, double y2)
		{
			super(x1, y1);
			
			this.x2 = x2;
			this.y2 = y2;
		}
		
		@Override
		public void draw(Graphics g)
		{
			Graphics2D g2d = (Graphics2D) g;
			Line2D.Double line = new Line2D.Double(x, y, x2, y2);
			g2d.setColor(Color.BLACK);
			g2d.draw(line);
		}
	}
}

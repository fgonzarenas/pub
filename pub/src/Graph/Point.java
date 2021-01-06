package Graph;

public class Point
{
	private double x;
	private double y;
	public static final double ACCURACY = 1e-10;
	
	public Point(double x, double y)
	{
		this.x = x;
		this.y = y;
	}
	
	public double getPosX() {
		return x;
	}

	public double getPosY() {
		return y;
	}
	
	public static Point minus(Point a, Point b)
    {
        return new Point(a.x - b.x, a.y - b.y);
    }

    public static Point plus(Point a, Point b)
    {
        return new Point(a.x + b.x, a.y + b.y);
    }

    public static double times(Point a, Point b)
    {
        return a.x * b.y + a.x * b.y;
    }

    public static Point times(Point a, double mult)
    {
        return new Point(a.x * mult, a.y * mult);
    }

    public static Point times(double mult, Point a)
    {
        return new Point(a.x * mult, a.y * mult);
    }

    public double cross(Point b)
    {
        return x * b.y - y * b.x;
    }

    public boolean equals(Point b)
    {
        return isZero(x - b.x) && isZero(y - b.y);
    }

    public static boolean isZero(double d)
    {
        return Math.abs(d) < ACCURACY;
    }
    
    // Returns intersection point of two segments if there is one, else returns null
    public static Point intersects(Point p, Point p2, Point q, Point q2)
    	{
    	    Point r = minus(p2, p);
    	    Point s = minus(q2, q);
    	    double rxs = r.cross(s);
    	    double qpxr = minus(q, p).cross(r);

    	    // If r x s = 0 and (q - p) x r = 0, then the two lines are collinear.
    	    if (isZero(rxs) && isZero(qpxr))
    	       return null;

    	    // If r x s = 0 and (q - p) x r != 0, then the two lines are parallel and non-intersecting.
    	    if (isZero(rxs) && !isZero(qpxr))
    	        return null;

    	    // t = (q - p) x s / (r x s)
    	    double t = minus(q, p).cross(s) / rxs;

    	    // u = (q - p) x r / (r x s)
    	    double u = minus(q, p).cross(r) / rxs;

    	    // If r x s != 0 and 0 <= t <= 1 and 0 <= u <= 1
    	    // the two line segments meet at the point p + t r = q + u s.
    	    if (!isZero(rxs) && (0 <= t && t <= 1) && (0 <= u && u <= 1))
    	    {
    	        // We can calculate the intersection point using either t or u.
    	        Point intersection = plus(p, times(t, r));

    	        // An intersection was found.
    	        return intersection;
    	    }

    	    // Otherwise, the two line segments are not parallel but do not intersect.
    	    return null;
    	}
}

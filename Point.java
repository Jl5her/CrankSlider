
public class Point {

	double x, y, v = 0;

	public Point(double x, double y) {
		this.x = x;
		this.y = y;
	}

	double distance(Point p) {
		return Math.sqrt(Math.pow(p.y - y, 2) + Math.pow(p.x - x, 2));
	}

}

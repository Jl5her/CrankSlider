import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;

public class Crank {

	double id, angle = 0;
	double angularSpeed = Math.PI / 90;
	Point p;
	int radius = 50;

	boolean isMouseOver, isSelected = false;
	Crank connected;

	List<Point> art = new ArrayList<>();

	Color color;

	public Crank(double x, double y) {
		this.p = new Point(x, y);

		int red = (int) (Math.random() * 255);
		int green = (int) (Math.random() * 255);
		int blue = (int) (Math.random() * 255);
		this.color = Color.rgb(red, green, blue);
	}

	void update() {
		this.angle += angularSpeed;

		this.art = this.art.stream().filter(a -> a.v < 100).collect(Collectors.toList());
	}

	void drawBody(GraphicsContext ctx) {
		double opacity = isSelected ? 1 : isMouseOver ? 0.9 : 0.8;
		ctx.setFill(Color.color(color.getRed(), color.getBlue(), color.getGreen(), opacity));
		ctx.setStroke(Color.BLACK);
		ctx.setLineWidth(1);
		ctx.beginPath();
		ctx.arc(p.x, p.y, radius, radius, 0, 360);
		ctx.fill();
		ctx.stroke();
		ctx.closePath();
	}

	Point armPos() {
		return new Point((p.x + (Math.cos(angle) * radius)), (p.y + (Math.sin(angle) * radius)));
	}

	void drawArm(GraphicsContext ctx) {
		ctx.setFill(Color.WHITE);
		ctx.setStroke(Color.BLACK);
		ctx.setLineWidth(1);

		ctx.beginPath();
		ctx.moveTo(p.x, p.y);
		ctx.lineTo(armPos().x, armPos().y);
		ctx.stroke();
		ctx.closePath();

		ctx.strokeArc(p.x - 1, p.y - 1, 2, 2, 0, 360, ArcType.ROUND);
		ctx.strokeArc(armPos().x - 1, armPos().y - 1, 2, 2, 0, 360, ArcType.ROUND);
		ctx.fillArc(p.x - 1, p.y - 1, 2, 2, 0, 360, ArcType.ROUND);
		ctx.fillArc(armPos().x - 1, armPos().y - 1, 2, 2, 0, 360, ArcType.ROUND);
	}

	void drawBar(GraphicsContext ctx) {
		if (connected == null || isSelected || connected.isSelected)
			return;

		if (p.y < connected.p.y)
			return;

		ctx.setFill(Color.WHITE);
		ctx.setStroke(Color.BLACK);

		ctx.setLineWidth(1);

		double thisX = armPos().x;
		double thisY = armPos().y;

		double connectedX = connected.armPos().x;
		double connectedY = connected.armPos().y;

		double angle = Math.atan2(connectedY - thisY, connectedX - thisX);
		double d = p.distance(connected.p) + 300;

		double toX = thisX + (Math.cos(angle) * d);
		double toY = thisY + (Math.sin(angle) * d);

		ctx.beginPath();
		ctx.moveTo(thisX, thisY);
		ctx.lineTo(toX, toY);
		ctx.stroke();
		ctx.closePath();

		ctx.beginPath();
		ctx.arc(toX, toY, 2, 2, 0, 2 * Math.PI);
		ctx.stroke();
		ctx.fill();
		ctx.closePath();

		art.add(new Point(toX, toY));
	}

	void drawArt(GraphicsContext ctx) {
		if (art.size() <= 0)
			return;

		ctx.setStroke(Color.BLACK);

		ctx.setLineWidth(2);
		ctx.beginPath();
		ctx.moveTo(art.get(0).x, art.get(0).y);
		for (Point p : art) {
			ctx.lineTo(p.x, p.y);
			p.v++;
		}
		ctx.stroke();
		ctx.closePath();
	}

	void connect(Crank crank) {
		connected = crank;
		crank.connected = this;
	}

}

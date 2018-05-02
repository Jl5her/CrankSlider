
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.Duration;

public class CrankSlider extends Application {

	private final static int WIDTH = 1000;
	private final static int HEIGHT = 1000;

	private List<Crank> cranks = new ArrayList<>();

	double mouseX, mouseY = 0;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage theStage) {
		theStage.setTitle("Crank Slider");

		Group root = new Group();
		Scene theScene = new Scene(root);
		theStage.setScene(theScene);

		setHandlers(theScene);

		Canvas canvas = new Canvas(WIDTH, HEIGHT);
		root.getChildren().add(canvas);

		GraphicsContext ctx = canvas.getGraphicsContext2D();

		KeyFrame kf = new KeyFrame(Duration.millis(20), e -> {
			render(ctx);
		});

		Timeline mainLoop = new Timeline(kf);
		mainLoop.setCycleCount(Animation.INDEFINITE);
		mainLoop.play();

		theStage.show();
	}

	private void setHandlers(Scene theScene) {
		theScene.setOnKeyPressed(e -> {
			keyDown(e);
		});
		theScene.setOnMouseDragged(e -> {
			mouseDrag(e);
		});
		theScene.setOnMousePressed(e -> {
			mouseDown(e);
		});
		theScene.setOnMouseReleased(e -> {
			mouseUp(e);
		});
		theScene.setOnMouseMoved(e -> {
			mouseMove(e);
		});

	}

	void render(GraphicsContext ctx) {
		ctx.clearRect(0, 0, WIDTH, HEIGHT);
		for (Crank crank : cranks) {
			crank.update();
			crank.drawBody(ctx);
			crank.drawArm(ctx);
		}
		for (Crank crank : cranks) {
			crank.drawBar(ctx);
			crank.drawArt(ctx);
		}
	}

	void mouseDrag(MouseEvent e) {
		mouseX = e.getX();
		mouseY = e.getY();
		for (Crank crank : cranks.stream().filter(c -> {
			return c.isSelected;
		}).collect(Collectors.toList())) {
			crank.p = new Point(e.getX(), e.getY());
		}
	}

	void mouseDown(MouseEvent e) {
		switch (e.getButton()) {
			case PRIMARY:
				getCranksAt(e.getX(), e.getY()).get(0).isSelected = true;
				break;
			case SECONDARY:
				cranks.add(new Crank(e.getX(), e.getY()));
				if (cranks.size() > 0 && cranks.size() % 2 == 0)
					cranks.get(cranks.size() - 2).connect(cranks.get(cranks.size() - 1));
				break;
			default:
				break;
		}
	}

	void mouseUp(MouseEvent e) {
		for (Crank c : cranks)
			c.isSelected = false;

	}

	void mouseMove(MouseEvent e) {
		mouseX = e.getX();
		mouseY = e.getY();

		for (Crank c : cranks) {
			c.isMouseOver = new Point(e.getX(), e.getY()).distance(c.p) <= c.radius;
		}
	}

	void keyDown(KeyEvent e) {
		switch (e.getCode()) {
			case BACK_SPACE:
				cranks = cranks.stream().filter(c -> {
					return getCranksAt(mouseX, mouseY).indexOf(c) == -1;
				}).collect(Collectors.toList());
				break;
			case DOWN:
				for (Crank c : getCranksAt(mouseX, mouseY)) {
					c.angularSpeed -= Math.PI / 180.0;
				}
				break;
			case UP:
				for (Crank c : getCranksAt(mouseX, mouseY)) {
					c.angularSpeed += Math.PI / 180.0;
				}
				break;
			default:
				break;
		}
	}

	List<Crank> getCranksAt(double x, double y) {
		return cranks.stream().filter((c) -> {
			return new Point(x, y).distance(c.p) <= c.radius;
		}).collect(Collectors.toList());
	}

}

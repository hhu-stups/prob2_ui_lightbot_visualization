package de.heinzen.visualization.lightbot.ui;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;

/**
 * Description of class
 *
 * @author Christoph Heinzen
 * @version 0.1.0
 * @since 14.11.17
 */
public class Robot extends Group {

	public Robot(int size) {
		int radius = size / 4;
		int arrowLength = size / 3;
		int padding = (size - (3 * radius) - 3)/2;
		int arrowX = size - padding - radius;

		Rectangle background = new Rectangle(size, size, Color.TRANSPARENT);
		Circle body = new Circle(radius, Color.BLACK);
		body.setCenterX(padding + radius);
		body.setCenterY(size/2);
		SVGPath arrow = new SVGPath();
		arrow.setContent("M" +  arrowX + " " + ((size/2) - (arrowLength/2)) +
				" l0 " + arrowLength +
				" L" + (arrowX + radius) + " " + (size/2) + " Z");
		arrow.setFill(Color.BLACK);

		getChildren().addAll(background, body, arrow);

	}

}

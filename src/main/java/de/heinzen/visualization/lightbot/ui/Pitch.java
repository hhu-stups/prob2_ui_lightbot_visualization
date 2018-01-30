package de.heinzen.visualization.lightbot.ui;

import de.heinzen.visualization.lightbot.Position;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description of class
 *
 * @author Christoph Heinzen
 * @version 0.1.0
 * @since 14.11.17
 */
public class Pitch extends Group {

	private static final int INITIAL_WIDTH = 600;
	private static final int INITIAL_HEIGHT = 500;
	private static final Color TILE_GRAY = Color.valueOf("#c3ceda");
	private static final Color TILE_BLUE = Color.valueOf("#4f9cd4");
	private static final Color TILE_YELLOW = Color.valueOf("#f1ef09");
	private static final Color BORDER_GRAY = Color.valueOf("#596d7c");
	private static final int PADDING = 20;
	private static final Map<String, Integer> DIRECTION_MAP;
	static {
		Map<String, Integer> aMap = new HashMap<>(4);
		aMap.put("right_direction", 0);
		aMap.put("down_direction", 90);
		aMap.put("left_direction", 180);
		aMap.put("up_direction", 270);
		DIRECTION_MAP = Collections.unmodifiableMap(aMap);
	}


	private final Map<Position, Rectangle> lightMap;
	private final Robot robot;
	private final int tileSize;
	private final int paddingX;
	private final int paddingY;
	private int minPosX;
	private int minPosY;

	public Pitch(Map<Position, Integer> positions, List<Position> lights, int robotX, int robotY, String robotDirection) {
		super();

		int maxPosX = Integer.MIN_VALUE;
		minPosX = Integer.MAX_VALUE;
		int maxPosY = Integer.MIN_VALUE;
		minPosY = Integer.MAX_VALUE;

		for (Position pos : positions.keySet()) {
			if (pos.getX() > maxPosX) maxPosX = pos.getX();
			if (pos.getX() < minPosX) minPosX = pos.getX();
			if (pos.getY() > maxPosY) maxPosY = pos.getY();
			if (pos.getY() < minPosY) minPosY = pos.getY();
		}

		int width = INITIAL_WIDTH - PADDING, height = INITIAL_HEIGHT - PADDING;
		int deltaX = ((maxPosX - minPosX) + 1), deltaY = ((maxPosY - minPosY) + 1);
		int possibleWidth = width / deltaX;
		int possibleHeight = height / deltaY;
		tileSize = Integer.min(possibleWidth, possibleHeight);
		paddingX = (INITIAL_WIDTH - (tileSize * deltaX))/2;
		paddingY = (INITIAL_HEIGHT - (tileSize * deltaY))/2;

		Rectangle background = new Rectangle(INITIAL_WIDTH, INITIAL_HEIGHT, Color.WHITE);
		getChildren().add(background);

		lightMap = new HashMap<>(lights.size());
		for (Position pos : positions.keySet()) {
			boolean light = lights.contains(pos);
			int tileX = ((pos.getX() - minPosX) * tileSize) + paddingX;
			int tileY = ((pos.getY() - minPosY) * tileSize) + paddingY;

			Rectangle tile = new Rectangle(tileX, tileY, tileSize, tileSize);
			tile.setFill(light ? TILE_BLUE : TILE_GRAY);
			tile.setStrokeWidth(2);
			tile.setStroke(BORDER_GRAY);
			getChildren().add(tile);

			Label heightLabel = new Label(positions.get(pos).toString());
			heightLabel.setLayoutX(tileX + 2);
			heightLabel.setLayoutY(tileY + 2);
			heightLabel.setFont(Font.font(17));
			getChildren().add(heightLabel);

			if (light) lightMap.put(pos, tile);
		}

		robot = new Robot(tileSize);
		setRobotX(robotX);
		setRobotY(robotY);
		setRobotDirection(robotDirection);

		getChildren().add(robot);
	}

	public void setRobotX(int x) {
		robot.setLayoutX(((x - minPosX) * tileSize) + paddingX);
	}

	public void setRobotY(int y) {
		robot.setLayoutY(((y - minPosY) * tileSize) + paddingY);
	}

	public void setRobotDirection(String directionString) {
		robot.setRotate(DIRECTION_MAP.get(directionString));
	}

	public void setLightState(Map<Position, Boolean> lightState) {
		for (Position pos : lightState.keySet()) {
			lightMap.get(pos).setFill(lightState.get(pos) ? TILE_YELLOW : TILE_BLUE);
		}
	}
}

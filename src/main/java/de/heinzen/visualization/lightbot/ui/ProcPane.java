package de.heinzen.visualization.lightbot.ui;

import de.heinzen.visualization.lightbot.Command;
import de.heinzen.visualization.lightbot.LightBotVisualization;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.List;

/**
 * Description of class
 *
 * @author Christoph Heinzen
 * @version 0.1.0
 * @since 12.11.17
 */
public class ProcPane extends VBox {

	private final String proc;
	private final int size;
	private final Label procLabel;
	private final TilePane cmdPane;
	private boolean selected;

	public ProcPane(String proc, int maxSize, int size, EventHandler<MouseEvent> clickHandler) {
		super();
		this.proc = proc;
		this.size = size;

		setSpacing(0);

		procLabel = new Label(proc.toUpperCase());
		procLabel.setPadding(new Insets(5, 10, 5, 10));
		procLabel.setFont(Font.font("Monospace", FontWeight.BOLD, 18));
		procLabel.setStyle("-fx-background-color: " + LightBotVisualization.PROC_GRAY + ";");

		Color procGray = Color.valueOf(LightBotVisualization.PROC_GRAY);
		Color procDarkGray = Color.valueOf(LightBotVisualization.PROC_DARK_GRAY);

		cmdPane = new TilePane();
		cmdPane.setPrefColumns(4);
		cmdPane.setPrefRows(3);
		cmdPane.setBorder(new Border(new BorderStroke(procDarkGray,
				BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
		for (int i = 0; i < size; i++) {
			cmdPane.getChildren().add(createStackPane(procGray));
		}

		for (int i = size; i < maxSize; i++) {
			cmdPane.getChildren().add(createStackPane(procDarkGray));
		}

		getChildren().addAll(procLabel, cmdPane);
		setOnMouseClicked(clickHandler);

	}

	public String getProc() {
		return proc;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
		String color = selected ? LightBotVisualization.PROC_YELLOW : LightBotVisualization.PROC_GRAY;
		for (int i = 0; i < size; i++) {
			StackPane pane = (StackPane) cmdPane.getChildren().get(i);
			for (Node child : pane.getChildren()) {
				if (child instanceof Rectangle) {
					((Rectangle) child).setFill(Color.valueOf(color));
					break;
				}
			}
		}
		procLabel.setStyle("-fx-background-color: " + color + ";");
	}

	public void setCommands(List<Command> commands) {
		String color = selected ? LightBotVisualization.PROC_YELLOW : LightBotVisualization.PROC_GRAY;
		for (int i = 0; i < commands.size(); i++) {
			StackPane pane = createStackPane(Color.valueOf(color));
			CommandButton cmdButton = new CommandButton(commands.get(i), 3);
			pane.getChildren().add(cmdButton);
			cmdPane.getChildren().set(i, pane);

		}
		for (int i = commands.size(); i < size; i++) {
			cmdPane.getChildren().set(i, createStackPane(Color.valueOf(color)));
		}
	}

	private StackPane createStackPane(Color color) {
		StackPane pane = new StackPane(new Rectangle(78, 77, color));
		pane.setBorder(new Border(new BorderStroke(Color.valueOf(LightBotVisualization.PROC_DARK_GRAY),
				BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
		return pane;
	}

}

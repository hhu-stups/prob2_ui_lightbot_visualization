package de.heinzen.visualization.lightbot.ui;

import de.heinzen.visualization.lightbot.Command;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.net.URL;

/**
 * @author Christoph Heinzen
 * @version 0.1.0
 * @since 09.11.17
 */
public class CommandButton extends Button {

	public CommandButton (Command cmd, int padding) {
		super();
		Image cmdImage = new Image(getClass().getClassLoader().getResource(cmd.getImage()).toExternalForm());
		setGraphic(new ImageView(cmdImage));
		setPadding(new Insets(padding));
		setUserData(cmd);
	}

}

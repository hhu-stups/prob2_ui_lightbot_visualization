package de.heinzen.visualization.lightbot;

/**
 * Description of class
 *
 * @author Christoph Heinzen
 * @version 0.1.0
 * @since 09.11.17
 */
public enum Command {

	MOVE("bt_move.png", "move"),
	LIGHT("bt_toggle_light.png", "toggle_light"),
	JUMP("bt_jump.png", "jump"),
	TURN_RIGHT("bt_turn_right.png", "turn_right"),
	TURN_LEFT("bt_turn_left.png", "turn_left"),
	PROC1("bt_proc1.png", "proc1"),
	PROC2("bt_proc2.png", "proc2");

	public static Command getByEvent(String event) {
		for (Command cmd : Command.values()) {
			if (cmd.getEvent().equals(event)) {
				return cmd;
			}
		}
		return null;
	}

	private final String image, event;

	Command(String image, String event) {
		this.image = image;
		this.event = event;
	}

	public String getImage() {
		return image;
	}

	public String getEvent() {
		return event;
	}
}

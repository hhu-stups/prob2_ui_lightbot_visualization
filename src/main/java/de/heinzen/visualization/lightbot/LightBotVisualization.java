package de.heinzen.visualization.lightbot;

import de.heinzen.visualization.lightbot.ui.CommandButton;
import de.heinzen.visualization.lightbot.ui.Pitch;
import de.heinzen.visualization.lightbot.ui.ProcPane;
import de.prob.translator.types.BigInteger;
import de.prob.translator.types.Tuple;
import de.prob2.ui.visualisation.fx.Visualisation;
import de.prob2.ui.visualisation.fx.listener.FormulaListener;
import javafx.beans.property.SimpleObjectProperty;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;

import java.util.*;

/**
 * @author Christoph Heinzen
 * @version 0.1.0
 * @since 09.11.17
 */
public class LightBotVisualization extends Visualisation{

	private static final String MAIN_PROC = "main";
	private static final String PROC_1 = "proc1";
	private static final String PROC_2 = "proc2";

	public static final String PROC_GRAY = "#dfe3e7";
	public static final String PROC_DARK_GRAY = "#ACADAE";
	public static final String PROC_YELLOW = "#f6e5a5";

	private SimpleObjectProperty<String> focusedProc = new SimpleObjectProperty<>(null);
	private Map<String, ProcPane> procPanes;
	private Pitch pitch;
	private ImageView stopImage;
	private ImageView playImage;
	private boolean randomExecution = false;

	public LightBotVisualization() {
		super();
		focusedProc.addListener((observable, oldValue, newValue) -> {
            for (ProcPane procPane : procPanes.values()) {
                procPane.setSelected(newValue.equals(procPane.getProc()));
            }
        });
	}

	@Override
	protected String getName() {
		return "LightBot";
	}

	@Override
	protected String[] getMachines() {
		return new String[]{"Mch3_Commands.bum"};
	}

	@Override
	protected Node initialize() {

		procPanes = new LinkedHashMap<>(3);

		//get initial values of the animation
		int startX = translateToInt(model.getValue("bot_x"));
		int startY = translateToInt(model.getValue("bot_y"));
		String startDirection = model.getValue("bot_direction").toString();
		Map<Position, Integer> positions = Position.to3DPositionMap(model.getValue("level"));
		List<Position> lights = Position.toPositionList(model.getValue("lights"));
		int[] procSizes = getProcSizes(model.getValue("program_size"));

		HBox mainLayout = new HBox();
		mainLayout.setFillHeight(false);

		VBox pitchBox = new VBox();

		playImage = new ImageView(new Image(getClass().getClassLoader().getResource("play.png").toExternalForm()));
		stopImage = new ImageView(new Image(getClass().getClassLoader().getResource("stop.png").toExternalForm()));
		HBox playButtonBox = new HBox();
		playButtonBox.setAlignment(Pos.CENTER_RIGHT);
		Button playButton = new Button();
		playButton.setGraphic(playImage);
		playButton.setOnMouseClicked(event -> {
			if (MouseButton.PRIMARY == event.getButton()) {
				if (randomExecution) {
					model.stopRandomExecution();
				} else {
					boolean executed = model.executeEvent("start_program");
					if (translateToBool(model.getValue("start")) || executed) {
						randomExecution = true;
						playButton.setGraphic(stopImage);
						model.executeRandomEvents(Integer.MAX_VALUE, 1000, true,
								event1 -> {
									randomExecution = false;
									playButton.setGraphic(playImage);
								});
					}
				}
			}
		});
		playButtonBox.getChildren().add(playButton);

		pitch = new Pitch(positions, lights, startX, startY, startDirection);

		HBox commandButtons = new HBox(3);
		commandButtons.setAlignment(Pos.CENTER);
		commandButtons.setPadding(new Insets(3));

		EventHandler<MouseEvent> cmdClickHandler = event -> {
			if (event.getButton() == MouseButton.PRIMARY && focusedProc.get() != null) {
				String eventClicked = ((Command) ((CommandButton) event.getSource()).getUserData()).getEvent();
				model.executeEvent("add_command", "c = " + eventClicked, "p = " + focusedProc.get());
			}
		};

		for (Command cmd : Command.values()) {
			CommandButton cmdButton = new CommandButton(cmd, 0);
			cmdButton.setOnMouseClicked(cmdClickHandler);
			commandButtons.getChildren().add(cmdButton);
		}
		pitchBox.getChildren().addAll(playButtonBox, pitch, commandButtons);

		VBox procs = new VBox(5);
		procs.setPadding(new Insets(5));

		EventHandler<MouseEvent> procHandler = event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                focusedProc.set(((ProcPane) event.getSource()).getProc());
            }
        };

		//create panes for procs
		procPanes.put(MAIN_PROC, new ProcPane(MAIN_PROC, 12,  procSizes[0], procHandler));
		if (procSizes[1] != 0) {
			procPanes.put(PROC_1, new ProcPane(PROC_1, 8, procSizes[1], procHandler));
		}
		if (procSizes[2] != 0) {
			procPanes.put(PROC_2, new ProcPane(PROC_2, 8, procSizes[2], procHandler));
		}
		procs.getChildren().addAll(procPanes.values());

		mainLayout.getChildren().addAll(pitchBox, procs);

		return new ScrollPane(mainLayout);
	}

	@Override
	protected void stop() {}

	@Override
	protected void registerFormulaListener() {
		registerFormulaListener(new FormulaListener("programs(" + MAIN_PROC + ")") {
			@Override
			public void variablesChanged(Object[] newValues) throws Exception {
				List<Command> cmdsPerProc = translateProgram(newValues[0]);
				procPanes.get(MAIN_PROC).setCommands(cmdsPerProc);
			}
		});
		registerFormulaListener(new FormulaListener("programs(" + PROC_1 + ")") {
			@Override
			public void variablesChanged(Object[] newValues) throws Exception {
				List<Command> cmdsPerProc = translateProgram(newValues[0]);
				if (procPanes.containsKey(PROC_1)) {
					procPanes.get(PROC_1).setCommands(cmdsPerProc);
				}
			}
		});
		registerFormulaListener(new FormulaListener("programs(" + PROC_2 + ")") {
			@Override
			public void variablesChanged(Object[] newValues) throws Exception {
				List<Command> cmdsPerProc = translateProgram(newValues[0]);
				if (procPanes.containsKey(PROC_2)) {
					procPanes.get(PROC_2).setCommands(cmdsPerProc);
				}
			}
		});
		registerFormulaListener(new FormulaListener("bot_x") {
			@Override
			public void variablesChanged(Object[] newValues) throws Exception {
				int x = translateToInt(newValues[0]);
				pitch.setRobotX(x);
			}
		});
		registerFormulaListener(new FormulaListener("bot_y") {
			@Override
			public void variablesChanged(Object[] newValues) throws Exception {
				int y = translateToInt(newValues[0]);
				pitch.setRobotY(y);
			}
		});
		registerFormulaListener(new FormulaListener("bot_direction") {
			@Override
			public void variablesChanged(Object[] newValues) throws Exception {
				pitch.setRobotDirection(newValues[0].toString());
			}
		});
		registerFormulaListener(new FormulaListener("light_state") {
			@Override
			public void variablesChanged(Object[] newValues) throws Exception {
				pitch.setLightState(translateLightState(newValues[0]));
			}
		});
	}

	@Override
	protected void registerEventListener() {}

	private List<Command> translateProgram(Object program) {
		List<Command> ret = new ArrayList<>();
		if ( program instanceof Set) {
			Set cmdSet = (Set) program;
			Command[] cmds = new Command[cmdSet.size()];
			for (Object cmdObject : cmdSet) {
				if (cmdObject instanceof Tuple) {
					Tuple cmdTuple = (Tuple) cmdObject;
					if (cmdTuple.getFirst() instanceof BigInteger) {
						int pos = ((BigInteger) cmdTuple.getFirst()).intValue();
						cmds[pos - 1] = Command.getByEvent(cmdTuple.getSecond().toString());
					}
				}
			}
			ret =  Arrays.asList(cmds);
		}
		return ret;
	}

	private Map<Position, Boolean> translateLightState(Object lightStateObject) {
		if (lightStateObject instanceof Set) {
			Set lightStateSet = (Set) lightStateObject;
			Map<Position, Boolean> ret = new HashMap<>(lightStateSet.size());
			for (Object lightObject : lightStateSet) {
				if (lightObject instanceof Tuple) {
					Tuple lightTuple = (Tuple) lightObject;
					if (lightTuple.getFirst() instanceof Tuple && lightTuple.getSecond() instanceof de.prob.translator.types.Boolean) {
						Tuple lightTuple2 = (Tuple) lightTuple.getFirst();
						boolean lightState = translateToBool(lightTuple.getSecond());
						if (lightTuple2.getFirst() instanceof BigInteger &&
								lightTuple2.getSecond() instanceof BigInteger) {
							int x = ((BigInteger) lightTuple2.getFirst()).intValue();
							int y = ((BigInteger) lightTuple2.getSecond()).intValue();
							ret.put(new Position(x, y), lightState);
						}
					}
				}
			}
			return ret;
		}
		return new HashMap<>();
	}

	private int[] getProcSizes(Object procSizesObject) {
		int[] procSizes = new int[]{0, 0, 0};
		if (procSizesObject instanceof Set) {
			for (Object sizeObject : (Set) procSizesObject) {
				if (sizeObject instanceof Tuple) {
					Tuple sizeTuple = (Tuple) sizeObject;
					String proc = sizeTuple.getFirst().toString();
					if (proc.equals(MAIN_PROC)) {
						procSizes[0] = ((BigInteger) sizeTuple.getSecond()).intValue();
					} else if (proc.equals(PROC_1)) {
						procSizes[1] = ((BigInteger) sizeTuple.getSecond()).intValue();
					} else if (proc.equals(PROC_2)) {
						procSizes[2] = ((BigInteger) sizeTuple.getSecond()).intValue();
					}
				}
			}
		}
		return procSizes;
	}
}

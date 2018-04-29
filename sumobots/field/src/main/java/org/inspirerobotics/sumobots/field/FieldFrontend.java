package org.inspirerobotics.sumobots.field;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import org.inspirerobotics.sumobots.field.gui.RootGroup;
import org.inspirerobotics.sumobots.field.util.InternalLog;
import org.inspirerobotics.sumobots.library.TimePeriod;
import org.inspirerobotics.sumobots.library.concurrent.InterThreadMessage;
import org.inspirerobotics.sumobots.library.concurrent.ThreadChannel;
import org.inspirerobotics.sumobots.library.networking.connection.Connection;
import org.inspirerobotics.sumobots.library.networking.tables.NetworkTable;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class FieldFrontend extends Application {

	private FieldBackend fieldBackend;

	private ThreadChannel threadChannel;

	private Stage stage;

	private RootGroup root;

	private Logger log = InternalLog.getLogger();

	private TimePeriod timePeriod;

	@Override
	public void start(Stage s) throws Exception {
		Thread.currentThread().setName("Frontend Thread");

		threadChannel = new ThreadChannel();

		log.fine("Starting backend thread");
		fieldBackend = new FieldBackend(threadChannel.createPair());
		fieldBackend.start();
		log.fine("Finished starting backend thread");

		log.fine("Creating the GUI");
		this.stage = s;
		initStage(stage);
		initGUI();
		log.fine("Finished Creating the GUI");

		stage.show();

		log.info("Frontend initialization complete. Starting main loop");

		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				update();
				Platform.runLater(this);
			}

		});
	}

	protected void update() {
		InterThreadMessage m = null;
		while ((m = threadChannel.poll()) != null) {
			onBackendMessageReceived(m);
		}
	}

	private void onBackendMessageReceived(InterThreadMessage m) {
		String name = m.getName();
		log.finer("Recieved Message from Backend: " + name);

		switch (name) {
		case "conn_update":
			Object data = m.getData();
			if (data instanceof List) {
				@SuppressWarnings("unchecked")
				ArrayList<Connection> conn = (ArrayList<Connection>) data;
				root.getGameTab().setConnections(conn);
			}
			break;
		case "time_period_update":
			timePeriod = (TimePeriod) m.getData();
			log.fine("New Time Period on Frontend: " + timePeriod);
			
			if(timePeriod == TimePeriod.GAME) {
				AudioEffect.play("start.wav");
			}else if(timePeriod == TimePeriod.ESTOPPED) {
				AudioEffect.play("estop.wav");
			}
			
			break;
		case "update_internal_table":
			root.getGameTab().setInternalNetwTable((NetworkTable) m.getData());
			break;
		default:
			log.warning("Unknown Message Recieved on Frontend: " + name);
			break;
		}
	}

	private void initGUI() {
		root = new RootGroup(this);
		stage.setScene(root.toScene());
		
		stage.addEventFilter(KeyEvent.KEY_PRESSED, k -> {
			log.finer("Key Pressed: " + k.getCode());
			if(this.timePeriod == TimePeriod.GAME) {
				if(k.getCode() == KeyCode.SPACE) {
					eStop();
		            k.consume();
				}
				
				if(k.getCode() == KeyCode.ENTER) {
					endMatch();
					k.consume();
				}
			}
	    });
	}

	private void initStage(Stage stage) {
		stage.setTitle("SumoBots FMS");
		stage.setAlwaysOnTop(false);
		stage.setMinWidth(700);
		stage.setMinHeight(400);

		stage.setOnCloseRequest(event -> {
            log.info("Application Window has been closed");
            threadChannel.add(new InterThreadMessage("exit_app"));
            log.info("Closing down Frontend Thread...");
            stage.hide();
            Platform.exit();
        });
	}

	@Override
	public void stop() throws Exception {
		super.stop();

		fieldBackend.join();
	}

	public static void main(String[] args) {
		launch(args);
	}

	public void startMatch() {
		threadChannel.add(new InterThreadMessage("start_match"));
	}

	public void initMatch() {
		threadChannel.add(new InterThreadMessage("init_match"));
	}

	public void endMatch() {
		threadChannel.add(new InterThreadMessage("end_match"));
	}

	public void eStop() {
		threadChannel.add(new InterThreadMessage("e-stop"));
	}
	
	public void disable(String name) {
		threadChannel.add(new InterThreadMessage("disable_ds", name));
	}

	public TimePeriod getTimePeriod() {
		return timePeriod;
	}

}

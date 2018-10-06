package Control;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class MainMenuController extends ParentController {
	@FXML public Button practice;
	@FXML public Button challenge;
	@FXML public Button create;
	@FXML public Button listen;
	@FXML public Button stats;
	@FXML public Button help;
	@FXML public Button micTest;
	@FXML public AnchorPane mainPane;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		_mediator.setParent(this);
	}

	@FXML
	public void practice(ActionEvent actionEvent) {
		loadNextPane("Practice1");
	}

	@FXML
	public void challenge(ActionEvent actionEvent) {
		loadNextPane("Challenge1");
	}

	@FXML
	public void create(ActionEvent actionEvent) {
		createPopUp();
	}

	@FXML
	public void listen(ActionEvent actionEvent) {
		loadNextPane("Listen");
	}

	@FXML
	public void stats(ActionEvent actionEvent) {
		// if (notAttemptedTable.size() > 0)
		loadNextPane("Stats");
		// else loadPane("StatsAllAttempted") // todo NOT SURE IF I WANT TO DO 2 DIFFERENT STATS PAGES
	}

	@FXML
	public void help(ActionEvent actionEvent) { //todo HELP POPUP
	}

	@FXML
	public void micTest(ActionEvent actionEvent) { //todo MIC TEST POPUP
	}

	/**
	 * Load a scene into the {@code mainPane} which
	 * is an {@link AnchorPane} which displays
	 * the entire program.
	 *
	 * @param page scene to load into {@code mainPane}
	 */
	@Override
	public void loadPane(String page) {
		super.loadPane(page, mainPane);
	}

	/**
	 * Loads the next scene under a Header depending
	 * on which button was pressed.
	 *
	 * @param page scene to load under a Header.
	 */
	private void loadNextPane(String page) {
		super.loadPane("Header", mainPane);
		_mediator.loadPane(Type.HEADER, page);
	}
}

package Control;

import Ratings.ChallengeRatings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
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
	@FXML public Text micTestLabel;
	@FXML public Text helpLabel;
	@FXML public Label averageSuccessLabel, progressLabel;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		_mediator.setParent(this);
		String score = Integer.toString(ChallengeRatings.getInstance().getOverallScore());
		averageSuccessLabel.setText("Average success: " + score + "%");
		String progress = Integer.toString(ChallengeRatings.getInstance().getProgress());
		progressLabel.setText("Progress is: "+progress+"%");
	}

	@FXML
	public void practice(ActionEvent actionEvent) {
		loadNextPane("Practice1", PageType.PRACTICE);
	}

	@FXML
	public void challenge(ActionEvent actionEvent) {
		loadNextPane("Challenge1", PageType.CHALLENGE);
	}

	@FXML
	public void create(ActionEvent actionEvent) {
		createPopUp("ConcatenateNames", "Create Name", 450, 259);
	}

	@FXML
	public void listen(ActionEvent actionEvent) {
		loadNextPane("Listen", PageType.LISTEN);
	}

	@FXML
	public void stats(ActionEvent actionEvent) {
		loadNextPane("Stats", PageType.STATS);
	}

	@FXML
	public void help(ActionEvent actionEvent) { //todo HELP POPUP
		createPopUp("HelperPopup", "Help", 550, 350);
	}

	@FXML
	public void micTest(ActionEvent actionEvent) { //todo MIC TEST POPUP
		createPopUp("MicTest", "Microphone Test", 450, 259);
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
	 * @param type indicates what type of page it is switching to.
	 */
	private void loadNextPane(String page, PageType type) {
		super.loadPane("Header", mainPane);
		_mediator.setPageType(type);
		_mediator.loadPane(Type.HEADER, page);
	}

	public void micTestHovered(MouseEvent mouseEvent) {
		micTestLabel.setVisible(true);
	}

	public void micTestExited(MouseEvent mouseEvent) {
		micTestLabel.setVisible(false);
	}

	public void helpHovered(MouseEvent mouseEvent) {
		helpLabel.setVisible(true);
	}

	public void helpExited(MouseEvent mouseEvent) {
		helpLabel.setVisible(false);
	}
}

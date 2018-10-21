package Control;

import Model.ChallengeSession;
import Model.Mediator;
import Ratings.ChallengeRatings;
import Ratings.DifficultyRatings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ResourceBundle;

public class MainMenuController extends ParentController implements MicTesterController {
	@FXML public Button practice, challenge, listen, stats, help;
	@FXML public AnchorPane mainPane;
	@FXML public Text micTestLabel, helpLabel, settingsLabel, micLabel;
	@FXML public Label averageSuccessLabel, progressLabel;
	@FXML public MenuButton settings;
	@FXML public MenuItem save, reset, quit;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		MainMenuController instance = this;
		_mediator.setParent(this);
		String score = Integer.toString(ChallengeRatings.getInstance().getOverallScore());
		averageSuccessLabel.setText("Average success: " + score + "%");
		String progress = Integer.toString(ChallengeRatings.getInstance().getProgress());
		progressLabel.setText("Progress is: " + progress + "%");

		Thread thread = new Thread(() -> micTester(instance));
		Mediator.getInstance().setChallengeSession(null); //reset if main menu
		thread.setDaemon(true);
		thread.start();
	}

	@FXML
	public void practice(ActionEvent actionEvent) {
		loadNextPane("SelectPractices", PageType.PRACTICE);
	}

	@FXML
	public void challenge(ActionEvent actionEvent) {
		loadNextPane("Challenge1", PageType.CHALLENGE);
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

	public void helpHovered(MouseEvent mouseEvent) {
		helpLabel.setVisible(true);
	}

	public void helpExited(MouseEvent mouseEvent) {
		helpLabel.setVisible(false);
	}

	public void settingsHovered(MouseEvent mouseEvent) {
		settingsLabel.setVisible(true);
	}

	public void settingsExited(MouseEvent mouseEvent) {
		settingsLabel.setVisible(false);
	}

	public void quit(ActionEvent actionEvent) {
	}

	@Override
	public void setMicLevel(float rms) {
		if ((10*rms) > 0) {
			micTestLabel.setText("Your microphone is good to go!");
			micTestLabel.setFill(Color.DARKGREEN);
			micLabel.setText("✔️");
			micLabel.setFill(Color.DARKGREEN);
		}
	}

}

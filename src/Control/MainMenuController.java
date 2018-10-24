package Control;
import Model.Originals;
import Ratings.ChallengeRatings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controls MainMenu.fxml which allows the user to navigate
 * the application
 *
 * @author Eric Pedrido
 * @author Lucy Chen
 */
public class MainMenuController extends ParentController implements MicTesterController {
	@FXML public Button practice, challenge, listen, stats, help;
	@FXML public AnchorPane mainPane;
	@FXML public Text micTestLabel, helpLabel, settingsLabel, micLabel;
	@FXML public Label averageSuccessLabel, progressLabel;
	@FXML public MenuButton settings;
	@FXML public MenuItem save, reset;
	@FXML public ImageView settingsImage, micImage;
	public Button micTest;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		MainMenuController instance = this;
		_mediator.setParent(this);
		_mediator.setChallengeSession(null); //reset if main menu

		// Print the average success
		String score = Integer.toString(ChallengeRatings.getInstance().getOverallScore());
		averageSuccessLabel.setText("Average success: " + score + "%");
		String progress = Integer.toString(ChallengeRatings.getInstance().getProgress());
		progressLabel.setText("Progress is: " + progress + "%");


		// Tell the user if their microphone is working
		Thread thread = new Thread(() -> micTester(instance));
		thread.setDaemon(true);
		thread.start();
	}

	@FXML
	public void practice(ActionEvent actionEvent) {
		loadNextPane("SelectPractices", PageType.PRACTICE);
	}

	/**
	 * If the Recordings folder is empty, notify the user.
	 */
	@FXML
	public void challenge(ActionEvent actionEvent) {
		if (_originals.listNames().size() > 0) {
			loadNextPane("Challenge1", PageType.CHALLENGE);
		} else {
			Label l = new Label("Your recordings folder is empty");
			l.setWrapText(true);
			Alert alert = new Alert(Alert.AlertType.WARNING);
			alert.setTitle("Empty recordings");
			alert.setHeaderText("Empty recordings folder");
			alert.getDialogPane().setContent(l);
			alert.showAndWait();
		}
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
		createPopUp("HelperPopup", "Helper Menu", 740, 490);
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

	@FXML
	public void helpHovered(MouseEvent mouseEvent) {
		help.setTextFill(Color.WHITE);  // Set to white and show a label
		helpLabel.setVisible(true);
	}

	@FXML
	public void helpExited(MouseEvent mouseEvent) {
		help.setTextFill(Paint.valueOf("#ff9900")); // Return to orange and hide label
		helpLabel.setVisible(false);
	}

	@FXML
	public void settingsHovered(MouseEvent mouseEvent) {
		colorAdjust(1.0, settingsImage);
		settingsLabel.setVisible(true);
	}

	@FXML
	public void settingsExited(MouseEvent mouseEvent) {
		colorAdjust(0, settingsImage);
		settingsLabel.setVisible(false);
	}

	@FXML
	public void micHovered(MouseEvent mouseEvent) {
		colorAdjust(1.0, micImage);
	}

	@FXML
	public void micExited(MouseEvent mouseEvent) {
		colorAdjust(0, micImage);
	}

	/**
	 * If a small amount of sound is detected (perhaps background noise),
	 * notifies the user that their microphone is working.
	 *
	 * @param rms The detected level (from 0-1)
	 */
	@Override
	public void setMicLevel(float rms) {
		if (rms > 0.01) {
			micTestLabel.setText("Your microphone is good to go!");
			micTestLabel.setFill(Color.DARKGREEN);
			micLabel.setText("✔️");
			micLabel.setFill(Color.DARKGREEN);
		}
	}
}

package Control;

import Model.Challenges;
import Ratings.ChallengeRatings;
import Ratings.DifficultyRatings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.Pane;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.Optional;

/**
 * Represents all classes which can contain Sub-scenes.
 * There are three types of {@code ParentController}s which
 * are enumerated in {@link Type}.
 *
 * @author Eric Pedrido
 * @author Lucy Chen
 */
public abstract class ParentController extends Controller {

	/**
	 * Types of different {@code ParentController}'s.
	 */
	public enum Type {HEADER, MAIN, SUB_MAIN, PRACTICE}

	/**
	 * Different types of pages. Enumerated so that the system knows which page
	 * the GUI is currently at.
	 */
	public enum PageType{PRACTICE, CHALLENGE, LISTEN, STATS, CHALLENGECOMPARE}

	/**
	 * Changes scene to the desired fxml file.
	 *
	 * @param page the desired scene to switch to.
	 * @param pane the JavaFX pane to load the scene onto.
	 */
	void loadPane(String page, Pane pane) {
		Pane newPane = null;
		try {
			newPane = FXMLLoader.load(getClass().getResource("/" + page + ".fxml"));
			pane.getChildren().setAll(newPane);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public abstract void loadPane(String page);

	@FXML
	public void micTest(ActionEvent actionEvent) {
		createPopUp("MicTest", "Microphone Test", 675, 389);
	}

	/**
	 * A generic {@code Alert} pop-up template which displays a message to the user.
	 * This pop-up contains only one button that is "Ok".
	 *
	 * @param headerText text to display to user.
	 */
	protected void donePopUp(String headerText) {
		ButtonType ok = new ButtonType("Ok", ButtonBar.ButtonData.APPLY);
		Alert alert = createAlert(Alert.AlertType.INFORMATION, "Action complete", headerText, "",
				new ButtonType[]{ok});
		Optional<ButtonType> result = alert.showAndWait();

		if (result.get() == ok) {
			alert.close();
		}
	}

	/**
	 * Creates an informative pop-up, telling the user that their progress
	 * has been saved.
	 */
	@FXML
	protected void save(ActionEvent actionEvent) {
		donePopUp("Your progress has been saved");
		ChallengeRatings.getInstance().saveSession();
		DifficultyRatings.getInstance().saveSession();
	}

	/**
	 * Resets any progress and deletes any challenges.
	 */
	@FXML
	protected void reset() {
		Challenges.getInstance().reset();
		ChallengeRatings.getInstance().reset();
		DifficultyRatings.getInstance().reset();
		_mediator.loadPane(Type.MAIN, "MainMenu");
	}
}

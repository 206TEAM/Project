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
 */
public abstract class ParentController extends Controller {

	public enum Type {HEADER, MAIN, SUB_MAIN, PRACTICE}

	public enum PageType{PRACTICE, CHALLENGE, LISTEN, STATS}

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

	protected void donePopUp(String headerText) {
		ButtonType ok = new ButtonType("Ok", ButtonBar.ButtonData.APPLY);
		Alert alert = createAlert(Alert.AlertType.INFORMATION, "Action complete", headerText, "",
				new ButtonType[]{ok});
		Optional<ButtonType> result = alert.showAndWait();

		if (result.get() == ok) {
			alert.close();
		}
	}

	@FXML
	protected void save(ActionEvent actionEvent) {
		donePopUp("Your progress has been saved");
		ChallengeRatings.getInstance().saveSession();
		DifficultyRatings.getInstance().saveSession();
	}

	/**
	 * this method resets any progress
	 * deletes challenges
	 */
	@FXML
	protected void reset(){
		Challenges.getInstance().reset();
		ChallengeRatings.getInstance().reset();
		DifficultyRatings.getInstance().reset();
		_mediator.loadPane(Type.MAIN, "MainMenu");
		try {
			Files.deleteIfExists(Paths.get("Ratings.txt"));
			Files.deleteIfExists(Paths.get("NameRatings.txt"));
		} catch (Exception e){
			//todo
		}



	}
}

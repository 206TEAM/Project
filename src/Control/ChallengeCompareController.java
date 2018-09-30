package Control;

import Model.Mediator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.text.Text;

public class ChallengeCompareController {

	@FXML public ProgressBar originalProgressBar;
	@FXML public Button playOriginal;
	@FXML public ProgressBar practiceProgressBar;
	@FXML public Button playPractice;
	@FXML public Text originalProgressText;
	@FXML public Text practiceProgressText;
	@FXML public Button correct;
	@FXML public Button wrong;

	@FXML
	public void playOriginal(ActionEvent actionEvent) {
		//todo play original audio
		//todo progress bar ting
		originalProgressText.setText("Playing...");
	}

	@FXML
	public void playPractice(ActionEvent actionEvent) {
		practiceProgressText.setText("Playing...");
	}

	@FXML
	public void correct(ActionEvent actionEvent) {
		//todo mark this name (and this challenge recording) to be a success (Add it to stats)

		// if (practiceListView.size() == 0) {
		Mediator.getInstance().loadPane(ParentController.Type.MAIN, "MainMenu");
	}

	@FXML
	public void wrong(ActionEvent actionEvent) {
		//todo mark this name (and this challenge Recording) to be a failure

		// if (practiceListView.size() == 0) {
		Mediator.getInstance().loadPane(ParentController.Type.MAIN, "MainMenu");
	}
}

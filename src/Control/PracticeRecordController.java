package Control;

import Model.Mediator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;

import java.net.URL;
import java.util.ResourceBundle;

public class PracticeRecordController implements Initializable {

	@FXML public ProgressBar progressBar;
	@FXML public Button play;
	@FXML public Button record;
	@FXML public Button next;
	@FXML public Label progressText; // if there's multiple originals, say "Select a version"

	private boolean _recordState;


	@Override
	public void initialize(URL location, ResourceBundle resources) {

	}

	@FXML
	public void play(ActionEvent actionEvent) {
		//todo play original
		//todo progress the progressbar (copy/paste)

		progressText.setText("Playing...");
	}

	@FXML
	public void record(ActionEvent actionEvent) {
		record.setText("Stop");

		// todo record audio on another thread

		progressBar.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);
		progressText.setText("Recording...");

		if (_recordState) {
			// todo stop recording

			progressBar.setProgress(0.0); // reset progress bar
			progressText.setText("Play Original");
			_recordState = false; // resets it to give opportunity to re-record.
		} else {
			_recordState = true; //sets true after one press so that on second press, it stops recording.
		}
	}

	@FXML
	public void next(ActionEvent actionEvent) {
		Mediator.getInstance().loadPane(ParentController.Type.SUB_MAIN, "PracticeCompare");
	}

}

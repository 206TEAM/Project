package Control;

import Model.Media;
import Model.Practice;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;

public class PracticeRecordController extends PracticeMainController implements Observer, MediaController {

	@FXML public ProgressBar mainProgressBar, practiceProgressBar;
	@FXML public Button playMain, playPractice, record, next, compare;
	@FXML public Text progressText, practiceProgressText;

	private boolean _recordState;
	private boolean _practicePlayState;
	private boolean _mainPlayState;
	private boolean _recordClicked;
	private boolean _autoClicked;
	private String _currentName;
	private String _currentFileName;
	private int _numVersions;
	private Practice _currentPractice;
	private Media _currentMedia;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		_recordState = false;
		_mainPlayState = false;
		_practicePlayState = false;
		_recordClicked = false;
		_autoClicked = false;
		_mediator.addObserver(this);
		_mediator.requestUpdate();
	}

	@FXML
	public void playMain(ActionEvent actionEvent) {
		playOriginal();
	}

	@FXML
	public void playPractice(ActionEvent actionEvent) {
		playAttempt();
	}

	@FXML
	public void record(ActionEvent actionEvent) {
		_mediator.fireDisableTable(TableType.PRACTICE, TableType.VERSION, true);
		_recordClicked = true;
		if (_recordState) {
			_currentPractice.stopRecording();
			this.stopRecording();
		} else {
			record.setText("Stop");

			practiceProgressBar.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);
			practiceProgressText.setText("Recording...");

			Task<Void> task = new Task<Void>() {
				@Override
				protected Void call() throws Exception {
					_currentPractice.record();
					return null;
				}
			};
			task.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED, event -> stopRecording());
			Thread thread = new Thread(task);

			thread.setDaemon(true);
			thread.start();
			_recordState = true; //sets true after one press so that on second press, it stops recording.
		}
	}


	@FXML
	public void autoCompare(ActionEvent actionEvent) {
		_autoClicked = true;
		playOriginal();
	}

	@FXML
	public void next(ActionEvent actionEvent) {
		List<String> practices = _mediator.getPracticeMainList();
		practices.remove(_currentName);
		if (practices.size() == 0) {
			ButtonType challenge = new ButtonType("I'm ready", ButtonBar.ButtonData.YES);
			ButtonType home = new ButtonType("Take me home", ButtonBar.ButtonData.NO);
			Alert alert = createAlert(Alert.AlertType.INFORMATION,
					"List Complete",
					"You have completed your practice list!",
					"Are you ready for a challenge?", new ButtonType[]{challenge, home});
			Optional result = alert.showAndWait();
			if (result.get() == challenge) {
				_mediator.loadPane(Type.HEADER, "Challenge1");
			} else {
				_mediator.loadPane(Type.MAIN, "MainMenu");
			}
		} else {
			_mediator.setPracticeMainList(practices);
			_mediator.fireTableValues(practices);
			_mediator.fireDisableTable(TableType.PRACTICE, TableType.VERSION, false);
		}
	}

	private void playOriginal() {
		_mediator.fireDisableTable(PracticeMainController.TableType.PRACTICE, PracticeMainController.TableType.VERSION, false);
		if (_mainPlayState) {
			stopPlaying(mainProgressBar, playMain);
		} else {
			playFile(this, progressText, playMain, mainProgressBar, _currentFileName, _currentName, _numVersions);
			_mainPlayState = true;
		}
	}

	private void playAttempt() {
		if (_practicePlayState) {
			stopPlaying(practiceProgressBar, playPractice);
			_practicePlayState = false;
		} else {
			String dir = "Temp/" + _currentPractice.getFileName() + ".wav";
			super.playFile(this, practiceProgressText, playPractice, practiceProgressBar, dir, _currentMedia);
			_practicePlayState = true;
		}
	}

	public void finish() {
		if (_autoClicked) {
			_autoClicked = false;
			playAttempt();
		}
	}

	private void stopRecording() {
		practiceProgressBar.setProgress(0.0); // reset progress bar
		practiceProgressText.setText("Play Your Attempt");
		record.setText("Re-record");
		next.setDisable(false);
		playPractice.setDisable(false);
		compare.setDisable(false);
		next.setDisable(false);
		_recordState = false; // resets it to give opportunity to re-record.
	}

	public void update(String name, String fileName, int numberOfVersions, Practice practice) {
		_currentName = name;
		_currentPractice = practice;
		_currentFileName = fileName;
		_numVersions = numberOfVersions;
		playMain.setDisable(false);
		playPractice.setDisable(true);
		record.setDisable(false);
		record.setText("Record");
		_recordClicked = false;
		_currentMedia = new Media(practice);
	}

	public void stopPlaying(ProgressBar progressBar, Button playButton) {
		if (!_recordClicked) {
			_mediator.fireDisableTable(TableType.PRACTICE, TableType.VERSION, false);
		}
		stopProgress();
		progressBar.setProgress(0.0);
		playButton.setText("▶️️");
		playButton.setTextFill(Color.LIME);
		if (_mainPlayState) {
			_mainPlayState = false;
		}
		if (_practicePlayState) {
			_practicePlayState = false;
		}
	}
}

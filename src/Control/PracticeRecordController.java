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

/**
 * Controls PracticeRecordController.fxml which records and compares the users
 * attempts to the original.
 *
 * <p> This class observes actions taken on {@link PracticeMainController},
 * to allow for selected files from {@link PracticeMainController#versionListView}
 * to be played through this class. </p>
 *
 * @author Eric Pedrido
 */
public class PracticeRecordController extends PracticeMainController implements Observer, MediaController {

	@FXML public ProgressBar mainProgressBar, practiceProgressBar;
	@FXML public Button playMain, playPractice, record, next, compare;
	@FXML public Text progressText, practiceProgressText;

	private boolean _recordState, _practicePlayState, _mainPlayState, _recordClicked, _autoClicked;

	/**
	 * Details about the currently selected name
	 */
	private String _currentName, _currentFileName;
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

		if (_mediator.getPracticeMainList().size() == 1) {
			next.setText("Done");
		}
	}

	/**
	 * Plays the {@code Original} if {@link #playMain} is pressed.
	 */
	@FXML
	public void playMain(ActionEvent actionEvent) {
		playOriginal();
	}


	/**
	 * Plays the {@code Practice} if the {@link #playPractice} is pressed.
	 */
	@FXML
	public void playPractice(ActionEvent actionEvent) {
		playAttempt();
	}

	/**
	 * Records the user and updates the GUI.
	 *
	 * <p> If the user is currently recording, then pressing this button
	 * stops the user from recording. </p>
	 */
	@FXML
	public void record(ActionEvent actionEvent) {
		_mediator.fireDisableTable(TableType.PRACTICE, TableType.VERSION, true);
		_recordClicked = true;


		if (_recordState) { // If the user is currently recording
			_currentPractice.stopRecording();
			this.stopRecording();
		} else {
			record.setText("Stop");

			// Update GUI
			practiceProgressBar.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);
			practiceProgressText.setText("Recording...");

			// Record the user in another thread
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

			// Sets true after one press so that on second press, it stops recording.
			_recordState = true;
		}
	}

	/**
	 * Plays the selected {@code Original} and then the user's
	 * attempt straight afterward.
	 */
	@FXML
	public void autoCompare(ActionEvent actionEvent) {
		_autoClicked = true;
		compare.setDisable(true);
		playOriginal();
	}

	/**
	 * Moves onto the next name on the practice list, removing the current one.
	 *
	 * <p> If there are no more names on the list, then opens a popup to
	 * allow the user to navigate the application. </p>
	 */
	@FXML
	public void next(ActionEvent actionEvent) {
		// Remove the current name from the practice list
		List<String> practices = _mediator.getPracticeMainList();
		practices.remove(_currentName);

		// If the list is empty, open a popup to allow the user to navigate app
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
			if (practices.size() == 1) {
				next.setText("Done");
			}

			// Update GUI then move onto the next name
			next.setDisable(true);
			compare.setDisable(true);
			_mediator.setPracticeMainList(practices);
			_mediator.fireTableValues(practices);
			_mediator.fireDisableTable(TableType.PRACTICE, TableType.VERSION, false);
		}
	}

	/**
	 * Updates the GUI, and plays the {@code Original} file.
	 *
	 * <p> If {@link #playMain} is clicked again, then stops the current audio. </p>
	 */
	private void playOriginal() {
		_mediator.fireDisableTable(PracticeMainController.TableType.PRACTICE, PracticeMainController.TableType.VERSION, false);
		if (_mainPlayState) { // If the button is pressed to stop
			stopPlaying(mainProgressBar, playMain);
			progressText.setText("Done");
			_mainPlayState = false;
		} else {
			disableButtons(true);
			_mainPlayState = true;
			playFile(this, progressText, playMain, mainProgressBar, _currentFileName, _currentName, _numVersions);
		}
	}

	/**
	 * Updates the GUI, and plays the {@code Practice} file.
	 *
	 * <p> If {@link #playPractice} is clicked again, then stops the current audio. </p>
	 */
	private void playAttempt() {
		if (_practicePlayState) { // If the button is pressed to stop
			stopPlaying(practiceProgressBar, playPractice);
			practiceProgressText.setText("Done");
			_practicePlayState = false;
		} else {
			disableButtons(true);
			String dir = "Temp/" + _currentPractice.getFileName() + ".wav";
			_practicePlayState = true;
			playFile(this, practiceProgressText, playPractice, practiceProgressBar, dir, _currentMedia);
		}
	}

	/**
	 * If the user clicked {@link #compare}, then upon finishing playing the
	 * {@code Original}, the user's attempt will play straight afterwards.
	 */
	public void finish() {
		if (_autoClicked) {
			_autoClicked = false;
			playAttempt();
		}
	}

	/**
	 * Updates the GUI and stops recording.
	 */
	private void stopRecording() {
		practiceProgressBar.setProgress(0.0); // reset progress bar
		practiceProgressText.setText("Play Your Attempt");
		record.setText("Re-record");
		disableButtons(false);
		playPractice.setDisable(false);
		_recordState = false; // resets it to give opportunity to re-record.
	}

	/**
	 * Sets the fields to know what is currently selected to allow for playing of the files.
	 *
	 * @param name             Selected name from the table
	 * @param fileName         corresponding file name
	 * @param numberOfVersions the number of versions of the name
	 * @param practice         a {@code Practice} object corresponding to the selected name
	 */
	public void update(String name, String fileName, int numberOfVersions, Practice practice) {
		// Update system about current selection
		_currentName = name;
		_currentPractice = practice;
		_currentFileName = fileName;
		_numVersions = numberOfVersions;
		_currentMedia = new Media(practice);

		// Update the GUI that a new file has been selected
		playMain.setDisable(false);
		playPractice.setDisable(true);
		record.setDisable(false);
		record.setText("Record");
		_recordClicked = false;

	}

	/**
	 * Updates the GUI and stops playing the current audio file.
	 *
	 * @param progressBar progress bar to reset the progress of.
	 * @param playButton  the button to set back to a play triangle after completion or cancellation.
	 */
	public void stopPlaying(ProgressBar progressBar, Button playButton) {
		if (!_recordClicked) { // re-enables the tables if the user has not yet recorded anything
			_mediator.fireDisableTable(TableType.PRACTICE, TableType.VERSION, false);
		}
		// Update GUI
		stopProgress();
		progressBar.setProgress(0.0);
		playButton.setText("▶️️");
		playButton.setTextFill(Color.LIME);

		if (_mainPlayState) { // If the user was playing the original file
			record.setDisable(false);
			_mainPlayState = false;
			if (record.getText().equals("Re-record")) { // If the user has already recorded
				next.setDisable(false);
			}
		}
		if (_practicePlayState) { // If the user was playing their attempt
			disableButtons(false);
			_practicePlayState = false;
		}
	}

	/**
	 * Disables or enables all the buttons.
	 *
	 * @param disable if {@code true}, then disables all the buttons,
	 *                otherwise, enables them.
	 */
	private void disableButtons(boolean disable) {
		record.setDisable(disable);
		compare.setDisable(disable);
		next.setDisable(disable);
	}
}

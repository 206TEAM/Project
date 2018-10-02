package Control;

import Model.*;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class PracticeRecordController extends PracticeMainController implements Observer {

	@FXML public ProgressBar progressBar;
	@FXML public Button play;
	@FXML public Button record;
	@FXML public Button next;
	@FXML public Text progressText;

	private boolean _recordState;
	private String _currentName;
	private String _currentFileName;
	private int _numVersions;
	private List<Practice> _practices = new ArrayList<>();
	private Practice _currentPractice;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		_recordState = false;
		Mediator.getInstance().addObserver(this);
	}

	@FXML
	public void play(ActionEvent actionEvent) {
		EventHandler<ActionEvent> doAfter = event -> {
			progressText.setText("Done");
			play.setDisable(false);
		};
		Mediator.getInstance().showProgress(progressBar, "Original", _currentFileName, doAfter);

		progressText.setText("Playing...");
		play.setDisable(true);

		Thread thread = new Thread(() -> {
			Original original;
			if (_numVersions > 1) {
				original = Originals.getInstance().getOriginalWithVersions(_currentFileName, _currentName);
			} else {
				original = Originals.getInstance().getOriginal(_currentFileName);
			}

			Media media = new Media(original);
			media.play();
		});
		thread.setDaemon(true);
		thread.start();
	}

	@FXML
	public void record(ActionEvent actionEvent) {
		Mediator.getInstance().fireDisableTables();
		if (_recordState) {
			_currentPractice.stopRecording();
			this.stopRecording();
		} else {
			record.setText("Stop");

			progressBar.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);
			progressText.setText("Recording...");
			_practices.add(_currentPractice);

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
	public void next(ActionEvent actionEvent) {
		Mediator.getInstance().loadPane(ParentController.Type.SUB_MAIN, "PracticeCompare");
	}

	private void stopRecording(){
		progressBar.setProgress(0.0); // reset progress bar
		progressText.setText("Play Original");
		record.setText("Re-record");
		next.setDisable(false);
		_recordState = false; // resets it to give opportunity to re-record.
	}

	public void update(String name) {
		_currentName = name;
		_currentPractice = new Practice(name);
		play.setDisable(false);
		record.setDisable(false);
	}

	public void update(String name, String fileName, int numberOfVersions) {
		update(name);
		_currentFileName = fileName;
		_numVersions = numberOfVersions;
	}
}

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

	private Mediator _mediator;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		_mediator = Mediator.getInstance();
		_recordState = false;
		_mediator.addObserver(this);
	}

	@FXML
	public void play(ActionEvent actionEvent) {
		super.playFile(progressText, play, progressBar, _currentFileName, _currentName, _numVersions);
	}

	@FXML
	public void record(ActionEvent actionEvent) {
		_mediator.fireDisableTable(TableType.PRACTICE, TableType.VERSION, true);
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
		_mediator.loadPane(ParentController.Type.SUB_MAIN, "PracticeCompare");
	}

	private void stopRecording(){
		progressBar.setProgress(0.0); // reset progress bar
		progressText.setText("Play Original");
		record.setText("Re-record");
		next.setDisable(false);
		_recordState = false; // resets it to give opportunity to re-record.
	}

	public void update(String name, String fileName, int numberOfVersions) {
		_currentName = name;
		_currentPractice = new Practice(name);
		_currentFileName = fileName;
		_numVersions = numberOfVersions;
		play.setDisable(false);
		record.setDisable(false);
	}
}

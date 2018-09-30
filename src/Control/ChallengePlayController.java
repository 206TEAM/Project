package Control;

import Model.Mediator;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.net.URL;
import java.util.*;

public class ChallengePlayController implements Initializable {
	@FXML public Text nameLabel;
	@FXML public ProgressIndicator timer;
	@FXML public Button next;
	@FXML public Text countdown;

	private int _seconds;
	private int _iteration;
	private List<String> _challengeList;
	private Timeline _timeLine;
	private Mediator _mediator;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		_mediator = Mediator.getInstance();
		_challengeList = _mediator.getChallengeList();
		_iteration = 0;

		_timeLine = new Timeline(
				new KeyFrame(Duration.ZERO, new KeyValue(timer.progressProperty(), 0)),
				new KeyFrame(Duration.seconds(5), event -> {
					nextName();
				}, new KeyValue(timer.progressProperty(), 1)));
		_timeLine.setCycleCount(_challengeList.size());

		Timer countdownTimer = new Timer(true);
		_seconds = 3;
		countdownTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				if (_seconds == 0) {
					countdownTimer.cancel();
					countdown.setVisible(false);
					next.setDisable(false);
					timer();
					record();
					loadName();
				} else {
					countdown.setText(String.valueOf(_seconds));
					_seconds--;
				}
			}
		}, 0, 1000);
	}

	@FXML
	public void next(ActionEvent actionEvent) {
		//todo stop and save recording
		if (next.getText().equals("Done")) {
			_mediator.setPracticeMainList(_challengeList);
			_mediator.loadPane(ParentController.Type.HEADER, "PracticeMain");
			_mediator.loadPane(ParentController.Type.SUB_MAIN, "Challenge3");
		} else {
			nextName();
		}
	}

	private void timer() {
		timer.setProgress(0.0);
		if (_timeLine.getStatus().equals(Animation.Status.RUNNING)) {
			_timeLine.stop();
		}
		_timeLine.play();
	}

	private void record() {
		//todo record audio
	}

	private void loadName() {
		String currentName = _challengeList.get(_iteration);
		nameLabel.setText(currentName);
		_iteration++;
	}

	private void nextName() {
		if (_iteration == _challengeList.size()) {
			next.setText("Done");
			_timeLine.stop();
			timer.setProgress(1.0);
		} else {
			timer();
			record();
			loadName();
		}
	}
}

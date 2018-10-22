package Control;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;
import java.net.URL;
import java.util.ResourceBundle;

public class MicTestController extends Controller implements MicTesterController {
	@FXML public ProgressBar micLevel;
	@FXML public Button done;
	@FXML public AnchorPane pane;
	@FXML public Text notWorkingLabel;

	private boolean _working;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		MicTestController instance = this;
		_working = false;

		Thread thread = new Thread(() -> micTester(instance));

		thread.setDaemon(true);
		thread.start();
	}

	@FXML
	public void done(ActionEvent actionEvent) {
		exit(pane);
	}

	public void setMicLevel(float rms) {
		double level = 2*rms;
		if (rms > 0.01 && !_working) {
			_working = true;
		}
		if (level >= 1) {
			micLevel.setProgress(1);
		} else {
			micLevel.setProgress(level);
		}
	}
}

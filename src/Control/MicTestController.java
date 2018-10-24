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

/**
 * Controls MicTest.fxml which is a real time level meter.
 *
 * @author Eric Pedrido
 */
public class MicTestController extends Controller implements MicTesterController {
	@FXML public ProgressBar micLevel;
	@FXML public Button done;
	@FXML public AnchorPane pane;
	@FXML public Text notWorkingLabel;

	private boolean _working; // true if the microphone is working

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		MicTestController instance = this;
		_working = false;

		// Start getting input from the microphone
		Thread thread = new Thread(() -> micTester(instance));
		thread.setDaemon(true);
		thread.start();
	}

	/**
	 * Closes the popup
	 */
	@FXML
	public void done(ActionEvent actionEvent) {
		exit(pane);
	}

	/**
	 * Updates the progress bar to act as a real time level meter.
	 *
	 * @param rms The detected level (from 0-1)
	 */
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

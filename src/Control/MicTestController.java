package Control;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.AnchorPane;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;
import java.net.URL;
import java.util.ResourceBundle;

public class MicTestController extends Controller {
	@FXML public ProgressBar micLevel;
	@FXML public Button done;
	@FXML public AnchorPane pane;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				AudioFormat af = new AudioFormat(44100f, 16, 1, true, false);
				TargetDataLine dataLine = null;
				try {
					dataLine = AudioSystem.getTargetDataLine(af);
					dataLine.open(af, 2048);
				} catch (LineUnavailableException e) {
					e.printStackTrace();
				}

				byte[] buffer = new byte[2048];
				float[] samples = new float[1024];

				dataLine.start();
				for (int i; (i = dataLine.read(buffer, 0, buffer.length)) > -1; ) {
					for (int j = 0, k = 0; j < i; ) {
						int sample = 0;

						sample |= buffer[j++] & 0xFF;
						sample |= buffer[j++] << 8;

						samples[k++] = sample / 32768f;
					}

					float rms = 0f;
					for (float sample : samples) {
						rms += sample * sample;
					}
					rms = (float) Math.sqrt(rms / samples.length);
					rms = Math.abs(rms);
					setMicLevel(rms);
				}
			}
		});

		thread.setDaemon(true);
		thread.start();
	}

	@FXML
	public void done(ActionEvent actionEvent) {
		exit(pane);
	}

	private void setMicLevel(float val) {
		double level = 2*val;
		if (level >= 1) {
			micLevel.setProgress(1);
		} else {
			micLevel.setProgress(level);
		}
	}
}

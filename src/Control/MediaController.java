package Control;

import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;

public interface MediaController {
	void stopPlaying(ProgressBar progressBar, Button playButton);
	void autoPlay();
}

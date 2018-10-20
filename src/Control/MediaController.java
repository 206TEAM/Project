package Control;

import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;

/**
 * Represents a {@link Controller} that can play media files.
 *
 * @author Eric Pedrido
 */
public interface MediaController {
	/**
	 * Stops playing the current {@link Model.Media} and updates
	 * the corresponding JavaFX elements.
	 *
	 * @param progressBar progress bar to reset the progress of.
	 * @param playButton the button to set back to a play triangle after completion or cancellation.
	 */
	void stopPlaying(ProgressBar progressBar, Button playButton);

	/**
	 * Any activity that needs to be done after the current {@link Model.Media}
	 * is finished.
	 */
	void finish();
}

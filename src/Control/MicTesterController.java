package Control;

/**
 * Represents Controllers that detect any microphone activity.
 *
 * @author Eric Pedrido
 */
public interface MicTesterController {
	/**
	 * Do something with the detected microphone level from {@link Controller#micTester(MicTesterController)}.
	 *
	 * @param rms The detected level (from 0-1)
	 */
	void setMicLevel(float rms);
}

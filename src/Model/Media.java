package Model;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;

/**
 * Responsible for handling various interactions with the media
 * files referenced as {@link Original} and {@link Practice}.
 * Also contains methods allowing for a microphone test.
 */
public class Media {

	private String _fileName;
	private String _originalName;
	private File _directory;
	private static final File CURRENT_DIRECTORY = FileSystems.getDefault().getPath(".").toFile();

	public Media(Practice practice) {
		_fileName =  practice.getFileName() + ".wav";
		_originalName = null;
		_directory = practice.getDirectory();
	}

	public Media(Original original) {
		String fileName = original.getFileName();
		_fileName = fileName.substring(0, fileName.lastIndexOf('.')) + original.getVersion() + ".wav";
		_originalName = original.getName();
		_directory = original.getDirectory();
	}

	/**
	 * Plays the {@code Practice} or {@code Original}
	 * depending on which constructor is called.
	 */
	public void play() {
		String command = "ffplay -autoexit -nodisp -i " + _fileName;
		process(command, _directory);
	}

	/**
	 * Records for 3 seconds, giving the user time
	 * to input a microphone test, saving the test to
	 * <dir>temp.wav</dir>. When the 3 seconds
	 * is finished, the audio is played back to the
	 * user.
	 *
	 * @see #playbackMicTest()
	 */
	public static void recordMicTest() {
		String record = "ffmpeg -f alsa -i default -t 3 temp.wav";
		process(record, CURRENT_DIRECTORY);
		playbackMicTest();
	}

	/**
	 * Plays the short microphone test back to the user,
	 * confirming whether or not the user has a functioning
	 * microphone. Once the user closes the mic test window,
	 * <dir>temp.wav</dir> is deleted.
	 *
	 * @see #finishMicTest()
	 */
	private static void playbackMicTest() {
			String record = "ffplay -autoexit -nodisp -i temp.wav";
			process(record, CURRENT_DIRECTORY);
			finishMicTest();
	}

	/**
	 * Deletes the temporary audio file created by the user
	 * when recording a test at {@link #recordMicTest()}.
	 */
	public static void finishMicTest() {
		try {
			Files.delete(new File("temp.wav").toPath());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Processes a bash command.
	 *
	 * @param command bash command to be processed.
	 * @param directory working directory for bash command.
	 */
	public static void process(String command, File directory) {
		ProcessBuilder pb = new ProcessBuilder("/bin/bash", "-c", command);
		pb.directory(directory);

		try {
			Process process = pb.start();
			process.waitFor();
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}
}

package Model;

import Control.SelectPracticeController;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.FileSystems;

/**
 * Responsible for handling various interactions with the media
 * files referenced as {@link Original} and {@link Practice}.
 * Also contains methods allowing for a microphone test.
 *
 * @author Eric Pedrido
 */
public class Media {

	private String _fileName;
	private String _originalName;
	private File _directory;
	private static final File CURRENT_DIRECTORY = FileSystems.getDefault().getPath(".").toFile();
	private static final int TARGET_VOLUME = 20; // Normalise concatenated names to reach 30dB
	private static Process PROCESS;

	public Media(Challenge challenge) {
		_fileName =  challenge.getFileName() + ".wav";
		_originalName = null;
		_directory = challenge.getDirectory();
	}

	public Media(Practice practice) {
		_fileName =  practice.getFileName() + ".wav";
		_originalName = null;
		_directory = practice.getDirectory();
	}

	public Media(Original original) {
		String fileName = original.getFileName();
		_fileName = fileName.substring(0, fileName.lastIndexOf('.')) + original.getVersion() + ".wav";
		_originalName = original.getName();
		if (_originalName.contains(" ")) {
			_directory = new File("Temp");
		} else {
			_directory = original.getDirectory();
		}
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
	 * Concatenates names specified in <dir>list.txt</dir>
	 *
	 * @param outputName file name of the final output in format <q>(name 1)_(name 2)_ ... _(name i)_.wav</q>
	 *                   for i names.
	 */
	public static void concatNames(String outputName) {
		String command = "ffmpeg -f concat -safe 0 -i list.txt -c copy Temp/_output.wav";
		process(command, CURRENT_DIRECTORY);
		removeSilence(outputName);
	}

	/**
	 * Normalizes the volume of the file. This is done by
	 * first detecting the mean volume using the <q>volumedetect</q> filter from ffmpeg.
	 * Then, reading the output and extracting just the {@code double} value from the piped output.
	 * Then, calculating how far from the {@link #TARGET_VOLUME} the detected mean volume of the file was.
	 * Then, adjusting the volume accordingly.
	 *
	 * @param file name of file to normalize.
	 * @param num iteration number to save as. See {@link SelectPracticeController#concatNames()} for implementation.
	 */
	public static void normalizeVolume(String file, int num) {
		try {
			// Get Mean volume
			String command = "ffmpeg -i " + file + " -filter:a volumedetect -f null /dev/null 2>&1 | grep mean_volume";
			ProcessBuilder pb = new ProcessBuilder("/bin/bash", "-c", command);
			Process process = pb.start();
			process.waitFor();

			// Read output from ffmpeg and extract just the volume.
			BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String volumeDetectOutput = br.readLine();
			volumeDetectOutput = volumeDetectOutput.replaceAll(".*:", ""); // remove all unnecessary characters
			double meanVolume = Double.valueOf(volumeDetectOutput.substring(1, volumeDetectOutput.lastIndexOf('d') - 1)); // Only get digits

			double difference = TARGET_VOLUME - Math.abs(meanVolume);

			// Adjust the volume based on how far it is from the target volume.
			String applyAdjusmentCommand = "ffmpeg -i " +file + " -filter:a \"volume=" + Math.abs(difference) +
					"dB\" Temp/_normalized" + num + ".wav";

			process(applyAdjusmentCommand, CURRENT_DIRECTORY);
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Uses the silenceremove filter from ffmpeg to trim any quiet patches in the already concatenated file.
	 *
	 * @param file final output filename. Naming nomenclature specified in {@link #concatNames(String)}.
	 */
	private static void removeSilence(String file) {
		String command = "ffmpeg -y -i Temp/_output.wav -af silenceremove=0:0:0:-1:1:-50dB:1 " + "Temp/" + file + ".wav";
		process(command, CURRENT_DIRECTORY);
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
			PROCESS = pb.start();
			PROCESS.waitFor();
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static void cancel() {
		PROCESS.destroy();
	}
}

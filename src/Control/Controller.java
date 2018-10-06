package Control;

import Model.Media;
import Model.Mediator;
import Model.Original;
import Model.Originals;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Methods that all controllers can use as utility.
 *
 * @author Eric Pedrido
 */
abstract class Controller implements Initializable {
	public Mediator _mediator = Mediator.getInstance();
	public Originals _originals = Originals.getInstance();

	/**
	 * Same as {@link List#contains(Object)} method, except
	 * it finds out if the {@code List<String>} contains a
	 * {@code String} that is case insensitive.
	 *
	 * @param string case insensitive {@code String} to find.
	 * @param list the {@code List<String>} to search through.
	 * @return {@code true} if the list does indeed contain the
	 *         {@code String} regardless of the case. Otherwise,
	 *         returns {@code false}.
	 */
	protected boolean containsName(String string, List<String> list){
		return list.stream().anyMatch(x -> x.equalsIgnoreCase(string));
	}

	/**
	 * Finds the {@code Original} with the given parameters even
	 * with multiple versions.
	 *
	 * @param fileName unique filename of the {@code Original}.
	 * @param name the name the {@code Original} corresponds to.
	 * @param numVersions number of versions of the desired {@code Original}.
	 * @return the exact version of the {@code Original}.
	 */
	protected Original getOriginal(String fileName, String name, int numVersions) {
		Original original;
		if (numVersions > 1) {
			original = _originals.getOriginalWithVersions(fileName, name);
		} else {
			original = _originals.getOriginal(fileName);
		}
		return original;
	}


	public void playFile(Text progressText, Button playButton, ProgressBar progressBar, String dir, Media media) {
		EventHandler<ActionEvent> doAfter = event -> {
			progressText.setText("Done");
			playButton.setDisable(false);
		};
		progressText.setText("Playing...");
		playButton.setDisable(true);
		if (_mediator.praticeNotNull()) {
			_mediator.fireDisableTable(PracticeMainController.TableType.PRACTICE, PracticeMainController.TableType.VERSION, true);
		}

		Thread thread1 = new Thread(media::play);
		Thread thread2 = new Thread(() -> showProgress(progressBar, dir, doAfter));

		thread1.setDaemon(true);
		thread2.setDaemon(true);
		thread1.start();
		thread2.start();
	}

	public void playFile(Text progressText, Button playButton, ProgressBar progressBar, String fileName, String name,
	                     int numVersions) {
		Original original;
		String dir;
		if (name.contains(" ")) {
			original = _originals.getConcatOriginal(name);
			dir = "Temp/" + fileName;
		} else {
			original = getOriginal(fileName, name, numVersions);
			dir = "Names/" + name + "/Original/" + fileName;
		}
		playFile(progressText, playButton, progressBar, dir, new Media(original));
	}

	/**
	 * Given a name, finds the version of an {@code Original} that has
	 * a <q>good</q> rating. If there are no <q>good</q> rated
	 * versions, picks a random <q>bad</q> version.
	 *
	 * @param name the name of the {@code Original}.
	 * @return the filename of the best version of the {@code Original}.
	 */
	public String pickBestOriginal(String name) {
		List<Original> allVersions = _originals.getAllVersions(name);
		List<String> goodFiles = new ArrayList<>();
		String fileName;

		if (allVersions.isEmpty()) {
			goodFiles.add(_originals.getFileName(name).get(0));
		} else {
			for (Original original : allVersions) {
				if (Originals.getInstance().getRating(original).equals("&good&")) {
					goodFiles.add(original.getFileNameWithVersion());
				}
			}
		}

		if (goodFiles.size() == 1) {
			fileName = goodFiles.get(0);
		} else if (goodFiles.size() > 1) { // Pick a random file if there are multiple "good" files.
			Collections.shuffle(goodFiles);
			fileName = goodFiles.get(0);
		} else {
			Collections.shuffle(allVersions); // Pick any file if they are all "bad" files.
			fileName = allVersions.get(0).getFileNameWithVersion();
		}
		return fileName;
	}

	protected void createPopUp(String scene, String title, int width, int height) {
		Parent root;
		try {
			root = FXMLLoader.load(getClass().getResource("/" + scene + ".fxml"));
			Stage stage = new Stage();
			stage.setTitle(title);
			stage.setScene(new Scene(root, width, height));
			stage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	protected void searchListener(FilteredList<String> filteredList, String newValue, ListView<String> listView) {
		filteredList.setPredicate(string -> {
			if (newValue==null || newValue.isEmpty()) {
				return true;
			}
			return string.toUpperCase().contains(newValue.toUpperCase());
		});
		listView.setItems(filteredList);
	}

	/**
	 * Sets the duration of which the progress bar goes from
	 * 0 to 100 to be the length that the audio file it is
	 * playing plays for.
	 *
	 * @param progress the {@code ProgressIndicator} being displayed
	 * @param dir whether it is an {@code Original} or a {@code Practice}
	 */
	public void showProgress(ProgressIndicator progress, String dir, EventHandler<ActionEvent> event) {
		double duration = 0;
		try {
			File file = new File(dir);
			AudioInputStream ais = AudioSystem.getAudioInputStream(file);
			AudioFormat format = ais.getFormat();

			long frames = ais.getFrameLength();
			duration = (frames+0.0) / format.getFrameRate();
		} catch (UnsupportedAudioFileException | IOException e) {
			e.printStackTrace();
		}

		Timeline timeLine = new Timeline(
				new KeyFrame(Duration.ZERO, new KeyValue(progress.progressProperty(), 0)),
				new KeyFrame(Duration.seconds(duration), event,
						new KeyValue(progress.progressProperty(), 1)));
		timeLine.setCycleCount(1);
		timeLine.play();
	}
}

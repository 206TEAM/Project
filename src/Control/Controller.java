package Control;

import Model.Media;
import Model.Mediator;
import Model.Original;
import Model.Originals;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.text.Text;

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
		_mediator.showProgress(progressBar, dir, doAfter);
		progressText.setText("Playing...");
		playButton.setDisable(true);
		_mediator.fireDisableTable(PracticeMainController.TableType.PRACTICE, PracticeMainController.TableType.VERSION, true);

		Thread thread = new Thread(media::play);
		thread.setDaemon(true);
		thread.start();
	}

	public void playFile(Text progressText, Button playButton, ProgressBar progressBar, String fileName, String name,
	                     int numVersions) {
		Original original = getOriginal(fileName, name, numVersions);
		playFile(progressText, playButton, progressBar, "Names/" + name + "/Original/" + fileName, new Media(original));
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

}

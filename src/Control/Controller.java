package Control;

import Model.Media;
import Model.Mediator;
import Model.Original;
import Model.Originals;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.text.Text;
import javafx.stage.Stage;

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

}

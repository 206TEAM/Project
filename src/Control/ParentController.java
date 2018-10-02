package Control;

import Model.Media;
import Model.Mediator;
import Model.Original;
import Model.Originals;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

import java.io.IOException;

/**
 * Represents all classes which can contain Sub-scenes.
 * There are three types of {@code ParentController}s which
 * are enumerated in {@link Type}.
 *
 * @author Eric Pedrido
 */
public abstract class ParentController implements Initializable {

	public enum Type { HEADER, MAIN, SUB_MAIN }

	void loadPane(String page, Pane pane) {
		Pane newPane = null;
		try {
			newPane = FXMLLoader.load(getClass().getResource("/" + page + ".fxml"));
			pane.getChildren().setAll(newPane);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public abstract void loadPane(String page);

	public void playFile(Text progressText, Button playButton, ProgressBar progressBar,
	                     String fileName, String name, int numVersions) {
		Mediator mediator = Mediator.getInstance();
		EventHandler<ActionEvent> doAfter = event -> {
			progressText.setText("Done");
			playButton.setDisable(false);
			mediator.fireDisableTable(PracticeMainController.TableType.PRACTICE, PracticeMainController.TableType.VERSION, false);
		};
		mediator.showProgress(progressBar, "Original", fileName, doAfter);
		progressText.setText("Playing...");
		playButton.setDisable(true);
		mediator.fireDisableTable(PracticeMainController.TableType.PRACTICE, PracticeMainController.TableType.VERSION, true);

		Thread thread = new Thread(() -> {
			Original original;
			if (numVersions > 1) {
				original = Originals.getInstance().getOriginalWithVersions(fileName, name);
			} else {
				original = Originals.getInstance().getOriginal(fileName);
			}

			Media media = new Media(original);
			media.play();
		});
		thread.setDaemon(true);
		thread.start();
	}
}

package Control;

import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.Pane;

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
}

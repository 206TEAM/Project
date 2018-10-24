package Control;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controls NoNameWarning.fxml which is a warning popup
 * that appears if the user uploads a text file that contains
 * names that are not in the database.
 *
 * @author Eric Pedrido
 */
public class NoNameController extends Controller {
	@FXML public AnchorPane pane;
	@FXML public Label listName;
	@FXML public ListView<String> missingNames;
	@FXML public Button okay;

	private List<String> _missingNamesList; // Names that are not in the database

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// get the missing names
		_missingNamesList = _mediator.getMissingNames();
		ObservableList<String> listToDisplay = FXCollections.observableArrayList(_missingNamesList);

		// Display them on a table
		missingNames.setItems(listToDisplay);
		listName.setText(_mediator.getMissngListFile());

	}

	/**
	 * Close the popup and reset the missing names list.
	 */
	public void done(ActionEvent actionEvent) {
		_missingNamesList.clear();
		_mediator.setMissingNames(_missingNamesList, "");
		exit(pane);
	}
}

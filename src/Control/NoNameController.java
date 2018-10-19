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

public class NoNameController extends Controller {
	@FXML public AnchorPane pane;
	@FXML public Label listName;
	@FXML public ListView<String> missingNames;
	@FXML public Button okay;

	private List<String> _missingNamesList;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		_missingNamesList = _mediator.getMissingNames();
		ObservableList<String> listToDisplay = FXCollections.observableArrayList(_missingNamesList);

		missingNames.setItems(listToDisplay);
		listName.setText(_mediator.getMissngListFile());

	}

	public void done(ActionEvent actionEvent) {
		_missingNamesList.clear();
		_mediator.setMissingNames(_missingNamesList, "");
		exit(pane);
	}
}

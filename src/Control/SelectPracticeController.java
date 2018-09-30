package Control;

import Model.Mediator;
import Model.Originals;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class SelectPracticeController implements Initializable {

	@FXML public ListView<String> selectListView;
	@FXML public Button go;
	@FXML public CheckBox shuffle;
	@FXML public TextField search;
	@FXML public SplitMenuButton filter;
	@FXML public Button create;

	private Mediator _mediator;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		_mediator = Mediator.getInstance();
		List<String> names = Originals.getInstance().listNames();

		java.util.Collections.sort(names);
		if (names.size() == 0) {
			selectListView.setVisible(false);
		} else {
			selectListView.setVisible(true);

			ObservableList<String> challengeNames = FXCollections.observableArrayList(names);
			selectListView.setItems(challengeNames);
			selectListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		}
	}

	@FXML
	public void go(ActionEvent actionEvent) {
		_mediator.loadPane(ParentController.Type.HEADER,"PracticeMain");
		_mediator.loadPane(ParentController.Type.SUB_MAIN, "PracticeRecord");
	}

	@FXML
	public void shuffle(ActionEvent actionEvent) {
	}

	@FXML
	public void filter(ActionEvent actionEvent) {
	}

	@FXML
	public void search(ActionEvent actionEvent) {
	}

	@FXML
	public void create(ActionEvent actionEvent) { //todo COMBINE TWO NAMES
	}
}

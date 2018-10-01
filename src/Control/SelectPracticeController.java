package Control;

import Model.Mediator;
import Model.Originals;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
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

		if (names.size() == 0) {
			selectListView.setVisible(false);
		} else {
			selectListView.setVisible(true);

			Collections.sort(names);
			ObservableList<String> practiceNames = FXCollections.observableArrayList(names);
			selectListView.setItems(practiceNames);
			selectListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		}
	}

	@FXML
	public void go(ActionEvent actionEvent) {
		List<String> practiceList = _mediator.getPracticeMainList();
		List<String> selectedListTemp = selectListView.getSelectionModel().getSelectedItems();
		List<String> selectedList = new ArrayList<>(selectedListTemp);

		if (practiceList == null) {
			practiceList = selectedList;
		} else {
			for (String name : selectedList) {
				if (!practiceList.contains(name)) {
					practiceList.add(name);
				}
			}
		}

		_mediator.setPracticeMainList(practiceList);
		_mediator.loadPane(ParentController.Type.HEADER,"PracticeMain");
		_mediator.loadPane(ParentController.Type.SUB_MAIN, "PracticeRecord");
	}

	@FXML
	public void shuffle(ActionEvent actionEvent) {
		if (shuffle.isSelected()) {
			FXCollections.shuffle(selectListView.getItems());
		} else {
			FXCollections.sort(selectListView.getItems());
		}
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

	@FXML
	public void listViewSelected(MouseEvent mouseEvent) {
		if (selectListView.getSelectionModel().getSelectedItems().size() > 0) {
			shuffle.setDisable(false);
			go.setDisable(false);
		}
	}
}

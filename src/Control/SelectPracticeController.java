package Control;

import Model.Mediator;
import Model.Originals;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Stream;

public class SelectPracticeController extends Controller {

	@FXML public ListView<String> selectListView;
	@FXML public Button go;
	@FXML public CheckBox shuffle;
	@FXML public TextField search;
	@FXML public MenuButton add;
	@FXML public ListView<String> previewList;
	@FXML public MenuItem combine;
	@FXML public MenuItem upload;
	@FXML public Button reset;

	private static SelectPracticeController _INSTANCE;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		_INSTANCE = this;
		List<String> names = Originals.getInstance().listNames();

		combine.setOnAction(event -> combine());
		upload.setOnAction(event -> upload());

		if (names.size() == 0) {
			selectListView.setVisible(false);
		} else {
			selectListView.setVisible(true);

			List<String> previouslySelected = Mediator.getInstance().getPracticeMainList();
			if (previouslySelected != null) {
				names.removeAll(previouslySelected);
				ObservableList<String> previewNames = FXCollections.observableArrayList(previouslySelected);
				previewList.setItems(previewNames);
				disableButtons(false);
			}

			Collections.sort(names);
			ObservableList<String> practiceNames = FXCollections.observableArrayList(names);
			FilteredList<String> filteredList = new FilteredList<>(practiceNames, s -> true);
			selectListView.setItems(filteredList);
			selectListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

			search.textProperty().addListener(((observable, oldValue, newValue) -> {
				filteredList.setPredicate(string -> {
					if (newValue==null || newValue.isEmpty()) {
						return true;
					}
					return string.toUpperCase().contains(newValue.toUpperCase());
				});
				selectListView.setItems(filteredList);
			}));
		}
	}

	@FXML
	public void go(ActionEvent actionEvent) {
		List<String> practiceList = _mediator.getPracticeMainList();
		List<String> selectedListTemp = previewList.getItems();
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
		_mediator.loadPane(ParentController.Type.PRACTICE, "PracticeRecord");
	}

	@FXML
	public void reset(ActionEvent actionEvent) {
		List<String> allNames = Originals.getInstance().listNames();
		ObservableList<String> list = FXCollections.observableList(allNames);
		selectListView.setItems(list);
		previewList.getItems().clear();
		disableButtons(true);
	}

	@FXML
	public void shuffle(ActionEvent actionEvent) {
		if (shuffle.isSelected()) {
			FXCollections.shuffle(previewList.getItems());
		} else {
			FXCollections.sort(previewList.getItems());
		}
	}


	@FXML
	public void listViewSelected(MouseEvent mouseEvent) {
		String selectedItem = selectListView.getSelectionModel().getSelectedItem();
		if (selectedItem != null) {
			if (!previewList.getItems().contains(selectedItem)) {
				previewList.getItems().add(selectedItem);
			}
			disableButtons(false);
		}

	}

	@FXML
	public void previewListSelected(MouseEvent mouseEvent) {
		String selectedItem = previewList.getSelectionModel().getSelectedItem();
		if (selectedItem != null) {
			previewList.getItems().remove(selectedItem);
			if (previewList.getItems().size() == 0) {
				disableButtons(true);
			}
		}
	}

	//TODO can make it so it removes from select practice. Requires a bit of work... not sure if necessary.
//	private void transferListItem(ListView<String> removeFrom, ListView<String> addTo) {
//		String toAdd = removeFrom.getSelectionModel().getSelectedItem();
//		if (toAdd != null) {
//			addTo.getItems().add(toAdd);
//			removeFrom.getItems().remove(toAdd);
//		}
//	}

	private void combine() {
		Parent root;
		try {
			root = FXMLLoader.load(getClass().getResource("/ConcatenateNames.fxml"));
			Stage stage = new Stage();
			stage.setTitle("Combine Names");
			stage.setScene(new Scene(root, 450, 259));
			stage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void upload() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Upload a List");
		fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("text files", "*.txt"));

		File file = fileChooser.showOpenDialog(go.getScene().getWindow());

		List<String> uploadedNames = new ArrayList<>();

		if (file != null) {
			try (Stream<String> stream = Files.lines(file.toPath())) {
				stream.forEach(e -> {
					List<String> names = new ArrayList<>(selectListView.getItems());
					if (containsName(e, names)) {
						e = e.substring(0, 1).toUpperCase() + e.substring(1).toLowerCase();
						uploadedNames.add(e);
					}
				});
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (uploadedNames.size() > 0) {
				disableButtons(false);
				previewList.getItems().addAll(uploadedNames);
			}
		}
	}

	private void disableButtons(boolean disable) {
		go.setDisable(disable);
		shuffle.setDisable(disable);
		reset.setDisable(disable);
	}

	protected void addValue(String name) {
		previewList.getItems().add(name);
	}

	protected SelectPracticeController getInstance() {
		return _INSTANCE;
	}
}

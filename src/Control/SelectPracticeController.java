package Control;

import Model.Originals;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;

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

	@FXML public ListView<String> selectListView, previewList;
	@FXML public Button go, reset;
	@FXML public CheckBox shuffle;
	@FXML public TextField search;
	@FXML public MenuButton add, sortBy;
	@FXML public MenuItem combine, upload, selected, alphabetical;

	private static SelectPracticeController _INSTANCE;
	private List<String> selectedOrder;
	private List<String> currentPreviewList;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		_INSTANCE = this;
		disableButtons(true);
		selectedOrder = new ArrayList<>();
		currentPreviewList = new ArrayList<>();

		List<String> names = Originals.getInstance().listNames();

		combine.setOnAction(event -> combine());
		upload.setOnAction(event -> upload());

		if (names.size() == 0) {
			selectListView.setVisible(false);
		} else {
			selectListView.setVisible(true);

			List<String> previouslySelected = _mediator.getPracticeMainList();
			if (previouslySelected != null) {
				if (previouslySelected.size() > 0) {
					names.removeAll(previouslySelected);
					ObservableList<String> previewNames = FXCollections.observableArrayList(previouslySelected);
					previewList.setItems(previewNames);
					disableButtons(false);
				}
			}

			Collections.sort(names);
			ObservableList<String> practiceNames = FXCollections.observableArrayList(names);
			FilteredList<String> filteredList = new FilteredList<>(practiceNames, s -> true);
			selectListView.setItems(filteredList);
			selectListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

			search.textProperty().addListener(((observable, oldValue, newValue) -> {
				searchListener(filteredList, newValue, selectListView);
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
		searchOpacity(true);
	}

	@FXML
	public void reset(ActionEvent actionEvent) {
		_mediator.setPracticeMainList(new ArrayList<>());
		previewList.getItems().clear();
		selectedOrder.clear();

		disableButtons(true);
		searchOpacity(true);
	}

	@FXML
	public void shuffle(ActionEvent actionEvent) {
		if (shuffle.isSelected()) {
			sortBy.setDisable(true);
			FXCollections.shuffle(previewList.getItems());
		} else {
			sortBy.setDisable(false);
			if (currentPreviewList.size() == 0) {
				currentPreviewList = selectedOrder;
			}
			previewList.getItems().setAll(currentPreviewList);
		}
		searchOpacity(true);
	}


	@FXML
	public void listViewSelected(MouseEvent mouseEvent) {
		String selectedItem = selectListView.getSelectionModel().getSelectedItem();
		if (selectedItem != null) {
			if (!previewList.getItems().contains(selectedItem)) {
				previewList.getItems().add(selectedItem);
				selectedOrder.add(selectedItem);
			}
			disableButtons(false);
		}
		searchOpacity(true);
	}

	@FXML
	public void previewListSelected(MouseEvent mouseEvent) {
		String selectedItem = previewList.getSelectionModel().getSelectedItem();
		if (selectedItem != null) {
			if (mouseEvent.getClickCount() == 2) {
				previewList.getItems().remove(selectedItem);
				selectedOrder.remove(selectedItem);
				_mediator.setPracticeMainList(previewList.getItems());
				if (previewList.getItems().size() == 0) {
					disableButtons(true);
				}
			}
		}
		searchOpacity(true);
	}

	public void searchClicked(MouseEvent mouseEvent) {
		searchOpacity(false);
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
		createPopUp("ConcatenateNames", "Create Name", 675, 384);
	}

	private void searchOpacity(boolean on) {
		if (on) {
			search.setOpacity(1.0);
		} else {
			search.setOpacity(0.5);
		}
	}

	public void selectedSort(ActionEvent actionEvent) {
		updatePreview(selectedOrder);
	}

	public void alphabeticalSort(ActionEvent actionEvent) {
		List<String> listDuplicate = new ArrayList<>(selectedOrder);
		Collections.sort(listDuplicate);
		updatePreview(listDuplicate);
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
		if (!previewList.getItems().contains(name)) {
			previewList.getItems().add(name);
			disableButtons(false);
		}
	}

	public void sortClicked(MouseEvent mouseEvent) {
		searchOpacity(true);
	}

	private void updatePreview(List<String> list) {
		currentPreviewList = list;
		previewList.getItems().setAll(currentPreviewList);
	}

	protected static SelectPracticeController getInstance() {
		return _INSTANCE;
	}


}

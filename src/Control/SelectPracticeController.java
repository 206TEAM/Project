package Control;

import Model.Media;
import Model.Original;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.stream.Stream;

public class SelectPracticeController extends Controller {

	@FXML public ListView<String> selectListView, previewList;
	@FXML public Button go, uploadList, reset, add, concatAdd, clear;
	@FXML public CheckBox shuffle;
	@FXML public TextField search, concatNameText;
	@FXML public MenuButton sortBy;
	@FXML public MenuItem selected, alphabetical;
	@FXML public Label nameText;

	private static SelectPracticeController _INSTANCE;
	private List<String> _selectedOrder;
	private List<String> _currentPreviewList;
	private List<String> _names;
	private String _newName;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		_INSTANCE = this;
		disableButtons(true);
		_selectedOrder = new ArrayList<>();
		_currentPreviewList = new ArrayList<>();

		if (_allNames.size() == 0) {
			selectListView.setVisible(false);
		} else {
			selectListView.setVisible(true);

			List<String> previouslySelected = _mediator.getPracticeMainList();
			if (previouslySelected != null) {
				if (previouslySelected.size() > 0) {
					_allNames.removeAll(previouslySelected);
					ObservableList<String> previewNames = FXCollections.observableArrayList(previouslySelected);
					previewList.setItems(previewNames);
					disableButtons(false);
				}
			}

			Collections.sort(_allNames);
			ObservableList<String> practiceNames = FXCollections.observableArrayList(_allNames);
			FilteredList<String> filteredList = new FilteredList<>(practiceNames, s -> true);
			selectListView.setItems(filteredList);
			selectListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

			search.textProperty().addListener(((observable, oldValue, newValue) -> {
				if (!newValue.equals("")) {
					add.setDisable(false);
					searchListener(filteredList, newValue, selectListView);
					selectListView.getSelectionModel().selectFirst();
				} else {
					add.setDisable(true);
					selectListView.setItems(practiceNames);
				}
			}));

			concatNameText.textProperty().addListener((observable, oldValue, newValue) -> setLabelText(newValue));
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
		_mediator.loadPane(ParentController.Type.HEADER, "PracticeMain");
		_mediator.loadPane(ParentController.Type.PRACTICE, "PracticeRecord");
		searchOpacity(true);
	}

	@FXML
	public void reset(ActionEvent actionEvent) {
		_mediator.setPracticeMainList(new ArrayList<>());
		previewList.getItems().clear();
		_selectedOrder.clear();

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
			if (_currentPreviewList.size() == 0) {
				_currentPreviewList = _selectedOrder;
			}
			previewList.getItems().setAll(_currentPreviewList);
		}
		searchOpacity(true);
	}


	@FXML
	public void listViewSelected(MouseEvent mouseEvent) {
		String selectedItem = selectListView.getSelectionModel().getSelectedItem();
		if (selectedItem != null) {
			int clickCount = mouseEvent.getClickCount();
			if (clickCount == 1) {
				String currString = concatNameText.getText();

				if (currString.contains(" ") || currString.isEmpty()) {
					currString = currString + selectedItem;
				} else {
					if (containsName(currString, _allNames)) {
						currString = currString + " " + selectedItem;
					} else {
						currString = selectedItem;
					}
				}
				concatNameText.setText(currString + " ");
				setLabelText(currString);

			} else if (clickCount > 1) {
				if (!previewList.getItems().contains(selectedItem)) {
					previewList.getItems().add(selectedItem);
					_selectedOrder.add(selectedItem);
					String newString = concatNameText.getText().substring(0, concatNameText.getText().length() - selectedItem.length() - 1);
					concatNameText.setText(newString);
					if (newString.contains(" ")) {
						setLabelText(newString.substring(0, newString.lastIndexOf(' ')));
					} else {
						setLabelText("");
					}
				}
				disableButtons(false);
			}
		}
		searchOpacity(true);
	}

	@FXML
	public void previewListSelected(MouseEvent mouseEvent) {
		String selectedItem = previewList.getSelectionModel().getSelectedItem();
		if (selectedItem != null) {
			if (mouseEvent.getClickCount() == 2) {
				previewList.getItems().remove(selectedItem);
				_selectedOrder.remove(selectedItem);
				_mediator.setPracticeMainList(previewList.getItems());
				if (previewList.getItems().size() == 0) {
					disableButtons(true);
				}
			}
		}
		searchOpacity(true);
	}

	public void add(ActionEvent actionEvent) {
		addName();
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
	private void searchOpacity(boolean on) {
		if (on) {
			search.setOpacity(1.0);
		} else {
			search.setOpacity(0.5);
		}
	}

	private void setLabelText(String curName) {
		String[] names = curName.split("[ -]");
		List<Character> splits = extractSplits(curName);// Get all spaces and hyphens only
		StringBuilder confirmed = new StringBuilder();
		_names = new ArrayList<>();

		if (names.length == 1) {
			setNameText("Enter or click two or more names to combine them", Color.BLACK, true);
		} else {
			for (int i = 0; i < names.length; i++) {
				String name = names[i];
				if (!containsName(name, _allNames)) {
					setNameText("\"" + name + "\" is not in the database", Color.RED, true);
				} else {
					name = name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
					confirmed.append(name);
					_names.add(name);
					if (i >= splits.size()) {
						confirmed.append(" ");
					} else {
						confirmed.append(splits.get(i));
					}
				}
			}

			String entered = curName.toUpperCase() + " ";
			if (entered.equals(confirmed.toString().toUpperCase())) {
				_newName = confirmed.toString();
				setNameText(_newName, Color.GREEN, false);
			}
		}
	}

	private List<Character> extractSplits(String s) {
		char[] temp = s.toCharArray();
		List<Character> output = new ArrayList<>();
		for (char c : temp) {
			if (c == ' ' || c == '-') {
				output.add(c);
			}
		}
		return output;
	}

	private void setNameText(String text, Paint paint, boolean disable) {
		nameText.setText(text);
		nameText.setTextFill(paint);
		concatAdd.setDisable(disable);
		clear.setDisable(disable);
	}

	public void selectedSort(ActionEvent actionEvent) {
		updatePreview(_selectedOrder);
	}

	public void alphabeticalSort(ActionEvent actionEvent) {
		List<String> listDuplicate = new ArrayList<>(previewList.getItems());
		Collections.sort(listDuplicate);
		updatePreview(listDuplicate);
	}

	public void upload(ActionEvent actionEvent) {
		concatNameText.setText("");

		List<String> missingNames = new ArrayList<>();
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Upload a List");
		fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("text files", "*.txt"));

		File file = fileChooser.showOpenDialog(go.getScene().getWindow());

		List<String> uploadedNames = new ArrayList<>();

		if (file != null) {
			try (Stream<String> stream = Files.lines(file.toPath())) {
				stream.forEach(name -> {
					List<String> names = new ArrayList<>(_allNames);
					if (containsName(name, names) && !containsName(name, previewList.getItems())) {
						name = name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
						uploadedNames.add(name);
					} else if (name.contains(" ") || name.contains("-")) {
						String[] diffNames = name.split("[ -]");
						List<Character> splits = extractSplits(name);
						StringBuilder confirmed = new StringBuilder();
						_names = new ArrayList<>();

						for (int i = 0; i < diffNames.length; i++) {
							String singleName = diffNames[i];
							if (containsName(singleName, _allNames)) {
								singleName = singleName.substring(0, 1).toUpperCase() + singleName.substring(1).toLowerCase();
								confirmed.append(singleName);
								_names.add(singleName);

								if (i >= splits.size()) {
									confirmed.append(" ");
								} else {
									confirmed.append(splits.get(i));
								}
							} else {
								missingNames.add("\"" + singleName + "\" from: \"" + name + "\"");
							}
						}

						String entered = name.toUpperCase() + " ";
						if (entered.equals(confirmed.toString().toUpperCase())) {
							_newName = confirmed.toString();
							concatNames();
						}
					} else {
						if (!name.equals("")) {
							missingNames.add(name);
						}
					}
				});
			} catch (IOException e) {
				e.printStackTrace();
			}

			if (uploadedNames.size() > 0) {
				disableButtons(false);
				previewList.getItems().addAll(uploadedNames);
				_selectedOrder.addAll(uploadedNames);
			}
			if (missingNames.size() > 0) {
				String missingListFile = file.getName();
				_mediator.setMissingNames(missingNames, missingListFile);
				createPopUp("NoNameWarning", "WARNING: Names not found", 505, 462);
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
			_selectedOrder.add(name);
			disableButtons(false);
		}
	}

	public void sortClicked(MouseEvent mouseEvent) {
		searchOpacity(true);
	}

	private void updatePreview(List<String> list) {
		_currentPreviewList = list;
		previewList.getItems().setAll(_currentPreviewList);
	}

	protected static SelectPracticeController getInstance() {
		return _INSTANCE;
	}

	public void addNameFromSearch(KeyEvent keyEvent) {
		if (keyEvent.getCode().equals(KeyCode.ENTER)) {
			addName();
		}
	}

	private void addName() {
		String name = selectListView.getSelectionModel().getSelectedItem();
		if (name != null) {
			if (!containsName(name, previewList.getItems())) {
				previewList.getItems().add(name);
				_selectedOrder.add(name);
			}
		}
	}

	private void concatNames() {
		String newFileName = _newName.replace(' ', '_');
		if (Files.notExists(Paths.get("Temp/" + newFileName + ".wav"))) {
			try {
				Files.deleteIfExists(Paths.get("Temp/output.wav"));
				Files.deleteIfExists(Paths.get("list.txt"));
				Files.deleteIfExists(Paths.get("Temp/normalized.wav"));
				Files.deleteIfExists(Paths.get("Temp/finalNormalized.wav"));
				Files.createFile(Paths.get("list.txt"));
				for (int i = 0; i < _names.size(); i++) {
					String name = _names.get(i);
					String fileName = pickBestOriginal(name);

					String dir = "Names/" + name + "/Original/" + fileName;
					String line = "file 'Temp/finalNormalized" + i + ".wav'\n";
					Files.write(Paths.get("list.txt"), line.getBytes(), StandardOpenOption.APPEND);

					Media.normalizeVolume(dir, i);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

			Media.concatNames(newFileName);
			_originals.addConcat(new Original(_newName, newFileName + ".wav"));
		}

		addValue(_newName);
		concatNameText.clear();
	}

	public void concatAdd(ActionEvent actionEvent) {
		if (!concatNameText.getText().isEmpty()) {
			concatNames();
		}
	}

	public void concatClear(ActionEvent actionEvent) {
		if (!concatAdd.getText().isEmpty()) {
			concatNameText.clear();
		}
	}
}

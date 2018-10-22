package Control;

import Model.Media;
import Model.Original;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Stream;

public class SelectPracticeController extends Controller {

	@FXML
	public ListView<String> selectListView, previewList;
	@FXML
	public Button go, uploadList, reset, add, concatAdd, clear;
	@FXML
	public CheckBox shuffle;
	@FXML
	public TextField search, concatNameText;
	@FXML
	public MenuButton sortBy;
	@FXML
	public MenuItem selected, alphabetical;
	@FXML
	public Label nameText;
	@FXML
	public AnchorPane pane;
	@FXML
	public ProgressIndicator loadingCircle;
	@FXML
	public Text loadingText;
	@FXML
	public ToggleButton toggle;

	private static SelectPracticeController _INSTANCE;

	private List<String> _selectedOrder;
	private List<String> _currentPreviewList;
	private List<String> _names, _uploadConcat;
	private String _newName;
	private boolean _alphabetical;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		_INSTANCE = this;
		disableButtons(true);
		_alphabetical = false;
		_selectedOrder = new ArrayList<>();
		_currentPreviewList = new ArrayList<>();

		if (_allNames.size() == 0) {
			selectListView.setVisible(false);
		} else {
			selectListView.setVisible(true);

			List<String> previouslySelected = _mediator.getPracticeMainList();
			if (previouslySelected != null) {
				if (previouslySelected.size() > 0) {
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
				searchListener(filteredList, newValue, selectListView);
				if (!newValue.equals("")) {
					add.setDisable(false);
					selectListView.getSelectionModel().selectFirst();
				} else {
					add.setDisable(true);
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
	public void toggle(ActionEvent actionEvent) {
		if (toggle.isSelected()) {
			toggle.setTextFill(Paint.valueOf("#1e1e1e"));
			toggle.setStyle("-fx-background-color: LIME");
			concatNameText.setPromptText("Select names from list...");
		} else {
			toggle.setTextFill(Paint.valueOf("#ff9900"));
			toggle.setStyle("-fx-background-color: #1e1e1e");
			concatNameText.setPromptText("Enter names here...");
		}
	}

	@FXML
	public void listViewSelected(MouseEvent mouseEvent) {
		String selectedItem = selectListView.getSelectionModel().getSelectedItem();
		if (selectedItem != null) {
			if (toggle.isSelected()) {
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
			} else {
				if (!previewList.getItems().contains(selectedItem)) {
					addValue(selectedItem);
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
		_alphabetical = false;
		updatePreview(_selectedOrder);
	}

	public void alphabeticalSort(ActionEvent actionEvent) {
		List<String> listDuplicate = new ArrayList<>(previewList.getItems());
		Collections.sort(listDuplicate);
		_alphabetical = true;
		updatePreview(listDuplicate);
	}

	public void upload(ActionEvent actionEvent) {
		concatNameText.setText("");
		_uploadConcat = new ArrayList<>();
		List<String> missingNames = new ArrayList<>();
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Upload a List");
		fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("text files", "*.txt"));

		File file = fileChooser.showOpenDialog(go.getScene().getWindow());

		List<String> uploadedNames = new ArrayList<>();

		if (file != null) {
			setLoading(true);
			List<String> currentItems = previewList.getItems();
			try (Stream<String> stream = Files.lines(file.toPath())) {
				stream.forEach(name -> {
					if (containsName(name, _allNames) && !containsName(name, currentItems)) {
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
							_uploadConcat.add(_newName);
						}
					} else {
						if (!name.equals("")) {
							if (!containsName(name, _allNames)) {
								missingNames.add(name);
							}
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

			Task task = new Task() {
				@Override
				protected Object call() throws Exception {
					for (String name : _uploadConcat) {
						_newName = name;
						concatNames();
					}
					return null;
				}
			};
			task.setOnSucceeded(event -> {
				for (String name : _uploadConcat) {
					addValue(name);
				}
				setLoading(false);
			});
			Thread thread = new Thread(task);
			thread.setDaemon(true);
			thread.start();
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
			if (_alphabetical) {
				List<String> listDuplicate = new ArrayList<>(previewList.getItems());
				Collections.sort(listDuplicate);
				updatePreview(listDuplicate);
			}
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
			final File tempFolder = new File("Temp");
			// Only delete temp files (Not concatenated name files)
			final File[] tempFiles = tempFolder.listFiles((dir, name) -> name.matches("^_.*"));
			if (tempFiles != null) {
				for (final File file : tempFiles) {
					if (!file.delete()) {
						System.err.println("File " + file.getName() + " could not be deleted");
					}
				}
			}
			try {
				Files.deleteIfExists(Paths.get("list.txt"));
				Files.createFile(Paths.get("list.txt"));
				for (int i = 0; i < _names.size(); i++) {
					String name = _names.get(i);
					String fileName = pickBestOriginal(name);

					String dir = "Names/" + name + "/Original/" + fileName;
					String line = "file 'Temp/_normalized" + i + ".wav'\n";

					Files.write(Paths.get("list.txt"), line.getBytes(), StandardOpenOption.APPEND);
					Media.normalizeVolume(dir, i);
				}
			}
			catch (IOException e) {
				e.printStackTrace();
			}
			Media.concatNames(newFileName);
			_originals.addConcat(new Original(_newName, newFileName + ".wav"));
		}
	}

	public void concatAdd(ActionEvent actionEvent) {
		if (!concatNameText.getText().isEmpty()) {
			setLoading(true);
			Task task = new Task() {
				@Override
				protected Object call() throws Exception {
					concatNames();
					return null;
				}
			};
			task.setOnSucceeded(event -> {
				concatNameText.setText("");
				addValue(_newName);
				setLoading(false);
			});
			Thread thread = new Thread(task);
			thread.setDaemon(true);
			thread.start();
		}
	}

	public void concatClear(ActionEvent actionEvent) {
		if (!concatAdd.getText().isEmpty()) {
			concatNameText.clear();
		}
	}

	private void setLoading(boolean load) {
		pane.setDisable(load);
		loadingCircle.setVisible(load);
		loadingText.setVisible(load);
	}
}

package Control;

import Model.Mediator;
import Model.Originals;
import Model.Practice;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class PracticeMainController extends ParentController {

	public enum TableType { PRACTICE, VERSION }

	@FXML public ListView<String> practiceListView;
	@FXML public ListView<String> versionListView;
	@FXML public Button addPractice;
	@FXML public Text nameLabel;
	@FXML public ComboBox rating;
	@FXML public ImageView difficultyStar; // set to visible if the name has been flagged difficult
	@FXML public Pane subPane;

	private Mediator _mediator;
	private Observer _observer;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		_mediator = Mediator.getInstance();
		_mediator.setParent(this);
		setTableValues(_mediator.getPracticeMainList());
	}

	@FXML
	public void add(ActionEvent actionEvent) {
		_mediator.loadPane(Type.HEADER, "Practice1");
	}

	@FXML
	public void nameSelected(MouseEvent mouseEvent) {
		String name = practiceListView.getSelectionModel().getSelectedItem();
		_mediator.setCurrentName(name);
		if (name != null) {
			List<String> versions = Originals.getInstance().getFileName(name);
			ObservableList<String> versionsToDisplay = FXCollections.observableArrayList(versions);
			versionListView.setItems(versionsToDisplay);
			nameLabel.setText(name);

			if (versionsToDisplay.size() == 1) {
				versionListView.getSelectionModel().select(0);
				notifyObserver(name, versionListView.getSelectionModel().getSelectedItem(), 1);
			} else {
				// todo select the original with a good rating over bad one
			}
		}
	}

	@FXML
	public void selectNameOriginal(MouseEvent event) {
		String fileName = versionListView.getSelectionModel().getSelectedItem();
		String name = practiceListView.getSelectionModel().getSelectedItem();
		if (fileName != null) {
			notifyObserver(name, fileName, versionListView.getItems().size());
		}
	}
	/**
	 * Load a scene into the {@code subPane}.
	 *
	 * @param page scene to load
	 */
	@Override
	public void loadPane(String page) {
		super.loadPane(page, subPane);
	}

	public void setDisableTables(TableType type, boolean disable) {
		if (type.equals(TableType.PRACTICE)) {
			practiceListView.setDisable(disable);
		} else if (type.equals(TableType.VERSION)) {
			versionListView.setDisable(disable);
		}
	}

	public void setTableValues(List<String> practiceList) {
		ObservableList<String> practices = FXCollections.observableArrayList(practiceList);
		practiceListView.setItems(practices);
	}

	/**
	 * Adds observers so that Sub-scenes can get notified
	 * when an item is selected in {@code practiceListView} or
	 * {@code versionListView}.
	 *
	 * @param o {@link Observer} to add
	 */
	public void addObserver(Observer o) {
		_observer = o;
	}

	/**
	 * Notifies the sub-scene that an item was selected
	 * and informs the {@code Observer} which item was selected.
	 *
	 * @see #addObserver(Observer)
	 */
	private void notifyObserver(String name, String fileName, int numberOfVersions) {
		Practice practice = new Practice(name);
		_mediator.setCurrent(name, fileName, numberOfVersions, practice);
		_observer.update(name, fileName, numberOfVersions, practice);
	}
}

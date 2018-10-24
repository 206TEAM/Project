package Control;

import Model.Mediator;
import Model.Original;
import Model.Practice;
import Ratings.DifficultyRatings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controls PracticeMain.fxml which is a component of the practice module
 * that consists of just the tables and labels i.e not the media elements or
 * buttons.
 *
 * <p> This class is observed by {@link PracticeRecordController} and was observed by
 * other classes in previous versions. The interface, although only represents one class,
 * was kept to increase re-usability. </p>
 *
 * @author Eric Pedrido
 */
public class PracticeMainController extends ParentController {

	/**
	 * Different types of table featured on this scene
	 */
	public enum TableType { PRACTICE, VERSION }

	@FXML public ListView<String> practiceListView;
	@FXML public ListView<String> versionListView;
	@FXML public Button addPractice;
	@FXML public Text nameLabel;
	@FXML public ImageView difficultyStar; // set to visible if the name has been flagged difficult
	@FXML public Pane subPane;
	@FXML public Button goodButton;
	@FXML public Button badButton;
	@FXML public Text fileLabel;
	@FXML public Text rateLabel;

	private Observer _observer; // Can only have one observer at a time
	private boolean _good; // Refers to the rating of an Original

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		_mediator.setParent(this);
		setTableValues(_mediator.getPracticeMainList());
	}

	/**
	 * Goes back to SelectPractices
	 */
	@FXML
	public void add(ActionEvent actionEvent) {
		_mediator.loadPane(Type.HEADER, "SelectPractices");
	}

	/**
	 * Sets a name to be selected when the user selects a name
	 * from the {@link #practiceListView}.
	 */
	@FXML
	public void nameSelected(MouseEvent mouseEvent) {
		String name = practiceListView.getSelectionModel().getSelectedItem();
		_mediator.setCurrentName(name);
		updateStar(name);
		setSelected(name);
	}

	/**
	 * Sets the file to be whichever version the user selected from
	 * the {@link #versionListView}.
	 */
	@FXML
	public void versionSelected(MouseEvent event) {
		String fileName = versionListView.getSelectionModel().getSelectedItem();
		String name = practiceListView.getSelectionModel().getSelectedItem();
		if (fileName != null) {
			ratingHandler(fileName, name);
			notifyObserver(name, fileName, versionListView.getItems().size());
		}
	}

	/**
	 * Rates a version as good.
	 */
	@FXML
	public void goodAction(ActionEvent actionEvent) {
		_good = true;
		setRating();
	}

	/**
	 * Rates a version as bad.
	 */
	@FXML
	public void badAction(ActionEvent actionEvent) {
		_good = false;
		setRating();
	}

	/**
	 * gets the difficulty rating from {@link DifficultyRatings}, then changes the star colour appropriately.
	 * @param name the name to get the difficulty from.
	 */
	private void updateStar(String name){
		if ((name != null) && !name.contains(" ")) {
			Boolean difficulty = DifficultyRatings.getInstance().getRating(name);
			starOn(difficultyStar, difficulty);
		}
	}

	/**
	 * when user clicks on star, set it to the opposite of what it was.
	 */
	@FXML
	public void difficult(MouseEvent event) {
		String name = Mediator.getInstance().getCurrentName();
		if (name != null) {
			Boolean difficulty = DifficultyRatings.getInstance().getRating(name);
			starOn(difficultyStar, !difficulty);
			DifficultyRatings.getInstance().setRating(name, !difficulty);
		}
	}

	/**
	 * Sets the current name to be selected, while also automatically selecting
	 * the best version of that name.
	 *
	 * <p> Updates the GUI accordingly, displaying the name, file name, difficulty rating,
	 * and quality rating of the automatically selected version. </p>
	 *
	 * <p> Notifies the observers that the user selected a name. </p>
	 *
	 * @param name the name that was selected
	 */
	private void setSelected(String name) {
		if (name != null) {
			// Get the versions of the selected name
			List<String> versions = _originals.getFileName(name);
			ObservableList<String> versionsToDisplay;

			// If there are no versions, then it is a concatenated name
			if (versions.size() == 0) {
				versions = new ArrayList<>();
				versions.add(_originals.getConcatFileName(name));
			}

			// Display the versions and update the GUI elements
			versionsToDisplay = FXCollections.observableArrayList(versions);
			versionListView.setItems(versionsToDisplay);
			nameLabel.setText(name);
			textSizeHandler(nameLabel, name);
			String fileName;


			if (versionsToDisplay.size() == 1) {
				// Select the only version if there is only one
				versionListView.getSelectionModel().selectFirst();
				fileName = versionListView.getSelectionModel().getSelectedItem();
			} else {
				// Select the best version if there are multiple versions
				fileName = pickBestOriginal(name);
				for (int i = 0; i < versions.size(); i++) {
					if (versions.get(i).equals(fileName)) {
						versionListView.getSelectionModel().select(i);
						break;
					}
				}
			}
			notifyObserver(name, fileName, versionListView.getItems().size());
			ratingHandler(fileName, name); // Set the rating to match the auto-selected version
		}
	}

	/**
	 * Update the rating of the {@code Original}
	 */
	private void setRating() {
		String fileName = versionListView.getSelectionModel().getSelectedItem();
		String name = practiceListView.getSelectionModel().getSelectedItem();
		Original original = super.getOriginal(fileName,name,versionListView.getItems().size());

		if (!name.contains(" ")) {
			if (_good) {
				_originals.setRating(original, "&good&");
			} else {
				_originals.setRating(original, "&bad&");
			}
			loadRating(original);
		}
	}

	/**
	 * Display the rating of the corresponding {@code Original}
	 *
	 * @param fileName file name of the {@code Original}
	 * @param name     name of the {@code Original}
	 */
	private void ratingHandler(String fileName, String name) {
		if (!name.contains(" ")) {
			// enable the buttons
			goodButton.setDisable(false);
			badButton.setDisable(false);
			rateLabel.setOpacity(1.0);

			Original original = super.getOriginal(fileName, name, versionListView.getItems().size());
			loadRating(original);
		}
		fileLabel.setText(fileName);
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

	/**
	 * Sets the table values to be that of the entered list and automatically
	 * selects the first item.
	 *
	 * @param practiceList list to set the table values to.
	 */
	public void setTableValues(List<String> practiceList) {
		ObservableList<String> practices = FXCollections.observableArrayList(practiceList);
		practiceListView.setItems(practices);
		practiceListView.getSelectionModel().selectFirst();
		setSelected(practiceListView.getSelectionModel().getSelectedItem());
	}

	/**
	 * Sets the rating buttons to match what the rating of the
	 * entered {@code Original} is.
	 */
	private void loadRating(Original original) {
		String rating = _originals.getRating(original);
		if (rating.equals("&bad&")){
			badButton.setOpacity(1);
			goodButton.setOpacity(0.5); // fade out good
		} else {
			goodButton.setOpacity(1);
			badButton.setOpacity(0.5); // fade out bad
		}
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
		if (_observer != null) {
			Practice practice = new Practice(name);
			_mediator.setCurrent(name, fileName, numberOfVersions, practice);
			_observer.update(name, fileName, numberOfVersions, practice);
		}
	}

	/**
	 * Allows observers to update the table.
	 */
	public void updateOnRequest() {
		setSelected(practiceListView.getSelectionModel().getSelectedItem());
	}
}

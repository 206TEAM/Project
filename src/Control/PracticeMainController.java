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

public class PracticeMainController extends ParentController {

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

	private Observer _observer;
	private boolean _good;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
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
		updateStar(name);
		if (name != null) {
			List<String> versions = _originals.getFileName(name);
			ObservableList<String> versionsToDisplay;

			if (versions.size() == 0) {
				versions = new ArrayList<>();
				versions.add(_originals.getConcatFileName(name));
			}

			versionsToDisplay = FXCollections.observableArrayList(versions);
			versionListView.setItems(versionsToDisplay);
			nameLabel.setText(name);
			textSizeHandler(nameLabel, name);
			String fileName;

			if (versionsToDisplay.size() == 1) {
				versionListView.getSelectionModel().selectFirst();
				 fileName = versionListView.getSelectionModel().getSelectedItem();
			} else {
				fileName = pickBestOriginal(name);
				for (int i = 0; i < versions.size(); i++) {
					if (versions.get(i).equals(fileName)) {
						versionListView.getSelectionModel().select(i);
						break;
					}
				}
			}
			notifyObserver(name, fileName, versionListView.getItems().size());
			ratingHandler(fileName, name);
		}
	}

	@FXML
	public void versionSelected(MouseEvent event) {
		String fileName = versionListView.getSelectionModel().getSelectedItem();
		String name = practiceListView.getSelectionModel().getSelectedItem();
		if (fileName != null) {
			ratingHandler(fileName, name);
			notifyObserver(name, fileName, versionListView.getItems().size());
		}
	}

	@FXML
	public void goodAction(ActionEvent actionEvent) {
		_good = true;
		setRating();
	}

	@FXML
	public void badAction(ActionEvent actionEvent) {
		_good = false;
		setRating();
	}

	/**
	 * gets the difficulty rating from difficultyratings class, then changes the star colour appropriately.
	 * @param name
	 */
	private void updateStar(String name){
		if (name != null) {
			Boolean difficulty = DifficultyRatings.getInstance().getRating(name);
			starOn(difficultyStar, difficulty);
		}
	}

	/**
	 * when user clicks on star
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

	private void ratingHandler(String fileName, String name) {
		if (!name.contains(" ")) {
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

	public void setTableValues(List<String> practiceList) {
		ObservableList<String> practices = FXCollections.observableArrayList(practiceList);
		practiceListView.setItems(practices);
	}

	private void loadRating(Original original) {
		String rating = _originals.getRating(original);
		if (rating.equals("&bad&")){
			badButton.setStyle("-fx-background-color: #FF0000; ");
			goodButton.setStyle("-fx-background-color: #8FBC8F; ");

		} else {
			goodButton.setStyle("-fx-background-color: #228B22; ");
			badButton.setStyle("-fx-background-color: #FF9999; ");
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
		Practice practice = new Practice(name);
		_mediator.setCurrent(name, fileName, numberOfVersions, practice);
		_observer.update(name, fileName, numberOfVersions, practice);
	}
}

package Control;

import Model.*;
import Ratings.DifficultyRatings;
import com.sun.org.apache.xpath.internal.operations.Or;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ListenController extends Controller implements MediaController {
	@FXML
	public TextField search;
	@FXML
	public ListView<String> listView, challengeListView, originalListView;
	@FXML
	public Text nameLabel, fileLabel, progressText;
	@FXML
	public ImageView difficultyStar;
	@FXML
	public ProgressBar playProgressBar;
	@FXML
	public Button play, delete, goodButton, badButton;


	/*****fields******/
	private String _selected;
	private String _type;
	private boolean _good;
	private boolean _playState;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //todo add listeners to each table and adjust progressText text accordingly
        difficultyStar.setDisable(true);
		_playState = false;
        List<String> names = _originals.listNames();

		java.util.Collections.sort(names);
		ObservableList<String> challengeNames = FXCollections.observableArrayList(names);

		FilteredList<String> filteredList = new FilteredList<>(challengeNames, s -> true);
		listView.setItems(filteredList);
		listView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

		search.textProperty().addListener(((observable, oldValue, newValue) -> {
			searchListener(filteredList, newValue, listView);
		}));
	}

    @FXML
    public void selectName(MouseEvent event) {
        difficultyStar.setDisable(false);
        //todo rating show
        String name = listView.getSelectionModel().getSelectedItem();
        nameLabel.setText(name);
        textSizeHandler(nameLabel, name);
        _mediator.setCurrentName(name);
        disableButtons();
        updateStar(name);

        //todo populate sublists
        populateSubLists();
    }

	/**
	 * this method populates the original list and challenge list with files from the model
	 */
	public void populateSubLists() {

		String name = _mediator.getCurrentName();

		ObservableList<String> originals = FXCollections.observableArrayList(_originals.getFileName(name));
		originalListView.setItems(originals); //todo
		originalListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

		if (Challenges.getInstance().listChallenges(name) != null) {
			ObservableList<String> challenges = FXCollections.observableArrayList(Challenges.getInstance().listChallenges(name));
			challengeListView.setItems(challenges);
		} else {
			challengeListView.getItems().clear();
		}
	}

	/**
	 * Method for when user selects a challenge name.
	 *
	 * @param event
	 */
	@FXML
	public void selectNameChallenge(MouseEvent event) { //todo selecting original and challenge is very similar - refactor
		String fileName = challengeListView.getSelectionModel().getSelectedItem();
		String name = listView.getSelectionModel().getSelectedItem();

		if (fileName != null) {
			_selected = fileName;
			_type = "challenge";
			fileLabel.setText(fileName + ".wav");

			//Challenge challenge = Challenges.getInstance().getChallenge(name, fileName);
			//todo do we want to get the rating?
			play.setDisable(false);
		} else {
			play.setDisable(true);
		}
	}

    /**
     * when user selects an original file name, they can play it
     *
     * @param event
     */
    @FXML
    public void selectNameOriginal(MouseEvent event) {
        String fileName = originalListView.getSelectionModel().getSelectedItem();
        String name = listView.getSelectionModel().getSelectedItem();

        if (fileName != null) {
            fileLabel.setText(fileName);
            //showRatings(true);
            goodButton.setDisable(false);
            badButton.setDisable(false);
            play.setDisable(false);
        } else {
            //playButton_3.setDisable(true);
        }

        _selected = fileName;
        _type = "original";

		//clearRatings();

        Original original = getOriginal(fileName, name, _originals.getFileName(name).size());
        loadRating(original);
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

	@FXML
	public void play(ActionEvent event) {
		disableTables(true);
		String name = _mediator.getCurrentName(); //getting the name
		if (_playState) {
			stopPlaying(playProgressBar, play);
			_playState = false;
		} else {
			String dir;
			Media media;
			if (_type.equals("original")) {
				Original original = getOriginal(_selected, name, _originals.getFileName(name).size());
				dir = "Names/" + name + "/Original/" + original.getFileName();
				media = new Media(original);
			} else {
				Challenge challenge = Challenges.getInstance().getChallenge(name, _selected);
				dir = "Names/" + name + "/Challenge/" + challenge.getFileName() + ".wav";
				media = new Media(challenge);
			}
			playFile(this, progressText, play, playProgressBar, dir, media);
			_playState = true;
		}
	}

	@FXML
	public void delete(ActionEvent actionEvent) {
		//todo popup "Are you sure?", then delete challenge recording
		if (_type.equals("challenge")) {
			String name = _mediator.getCurrentName();
			if (name != null) {
				Challenges.getInstance().deleteChallenge(name, _selected);
				challengeListView.getItems().remove(_selected);
				if (challengeListView.getItems().size() < 1) {
					//deleteButton_3.setDisable(true);
					//playButton_3.setDisable(true);
				}
			}
		}
	}

	private void ratingHandler() {
		String fileName = originalListView.getSelectionModel().getSelectedItem();
		String name = listView.getSelectionModel().getSelectedItem();

        if (fileName != null) {
	        Original original = getOriginal(fileName, name, _originals.getFileName(name).size());
            if (!_good) {
                _originals.setRating(original, "&bad&");
            } else {
                _originals.setRating(original, "&good&");
            }
            loadRating(original);
        }
    }

    @FXML
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

    @FXML
    public void goodAction(ActionEvent event) {
        //todo edit stats of name based on challenge recording success rating
        _good = true;
        ratingHandler();

    }

    @FXML
    public void badAction(ActionEvent actionEvent) {
        //todo same as correct()
        _good = false;
        ratingHandler();
    }

    public void disableButtons() {
        goodButton.setDisable(true);
        badButton.setDisable(true);
    }

	private void disableTables(boolean disable) {
		listView.setDisable(disable);
		challengeListView.setDisable(disable);
		originalListView.setDisable(disable);
	}

	@Override
	public void stopPlaying(ProgressBar progressBar, Button playButton) {
		stopProgress();
		disableTables(false);
		progressBar.setProgress(0.0);
		playButton.setText("▶️️");
		playButton.setTextFill(Color.LIME);
		progressText.setText("Play");
		if (_playState) {
			_playState = false;
		}
	}

	@Override
	public void finish() {
		disableTables(false);
		progressText.setText("Play");
	}
}

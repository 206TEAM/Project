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

/**
 * this class controls List.fxml file
 * It handles when the user wants to refer back to a previous challenge attempt
 * or listen to a particular name from the database. they can rate the quality of an original file
 *
 * @author Lucy Chen
 * @author Eric Pedrido
 */
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
	public Button play, goodButton, badButton;

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

	/**
	 * when a name is selected from the list of names, the difficulty star mirrors its
	 * current difficulty
	 * the original and challenge list views are populated with the corresponding files
	 * @param event
	 */
    @FXML
    public void selectName(MouseEvent event) {
        difficultyStar.setDisable(false);
        String name = listView.getSelectionModel().getSelectedItem();
        nameLabel.setText(name);
        _mediator.setCurrentName(name);
        disableButtons();
        updateStar(name);
        populateSubLists();
    }

	/**
	 * this method populates the original list and challenge list with files from the model
	 */
	public void populateSubLists() {

		String name = _mediator.getCurrentName();

		ObservableList<String> originals = FXCollections.observableArrayList(_originals.getFileName(name));
		originalListView.setItems(originals);
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
	public void selectNameChallenge(MouseEvent event) {
		String fileName = challengeListView.getSelectionModel().getSelectedItem();

		if (fileName != null) {
			_selected = fileName;
			_type = "challenge";
			fileLabel.setText(fileName + ".wav");
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
            goodButton.setDisable(false);
            badButton.setDisable(false);
            play.setDisable(false);
        }

        _selected = fileName;
        _type = "original";

        Original original = getOriginal(fileName, name, _originals.getFileName(name).size());
        loadRating(original);
    }

    /**
     * gets the difficulty rating from {@link DifficultyRatings}, then changes the star colour appropriately.
     * @param name
     */
    private void updateStar(String name){
        if (name != null) {
            Boolean difficulty = DifficultyRatings.getInstance().getRating(name);
            starOn(difficultyStar, difficulty);
        }
    }

    /**
     * when user clicks on star, sets the difficulty to the opposite to what it current is
     */
    @FXML
    public void difficult(MouseEvent event) {
        String name = _mediator.getCurrentName();
        if (name != null) {
            Boolean difficulty = DifficultyRatings.getInstance().getRating(name);
            starOn(difficultyStar, !difficulty);
            DifficultyRatings.getInstance().setRating(name, !difficulty);
        }
    }

	/**
	 * when the user clicks the play button.
	 * depending on which file was last selected (original or challenge), that file is played
	 * through the Media class
	 * @param event
	 */
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
				List<String> fileNames = _originals.getFileName(name);
				Original original = getOriginal(_selected, name, fileNames.size());
				media = new Media(original);
				if (fileNames.size() == 1) {
					dir = "Names/" + name + "/Original/" + original.getFileName();
				} else {
					dir = "Names/" + name + "/Original/" + original.getFileNameWithVersion();
				}
			} else {
				Challenge challenge = Challenges.getInstance().getChallenge(name, _selected);
				dir = "Names/" + name + "/Challenge/" + challenge.getFileName() + ".wav";
				media = new Media(challenge);
			}

			playFile(this, progressText, play, playProgressBar, dir, media); //sets the progress bar
			_playState = true;
		}
	}

	/**
	 * this method handles the ratings for the original file.
	 * sets it to good or bad based on what button the user clicks
	 */
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

	/**
	 * loads the current rating information of the original file and displays the buttons
	 * that correspond to the rating
	 * @param original
	 */
	@FXML
    private void loadRating(Original original) {
        String rating = _originals.getRating(original);
        if (rating.equals("&bad&")){
            badButton.setOpacity(1.0);
            goodButton.setOpacity(0.5);
        } else {
            goodButton.setOpacity(1.0);
            badButton.setOpacity(0.5);
        }
    }

	/**
	 * when clicks the good button for an orignal rating, update
	 * @param event
	 */
	@FXML
    public void goodAction(ActionEvent event) {
        _good = true;
        ratingHandler();

    }

	/**
	 * when user clicks on the bad button for an original rating, update
	 * @param actionEvent
	 */
    @FXML
    public void badAction(ActionEvent actionEvent) {
        _good = false;
        ratingHandler();
    }

	/**
	 * disables all the buttons
	 */
	public void disableButtons() {
        goodButton.setDisable(true);
        badButton.setDisable(true);
        play.setDisable(true);

    }

	/**
	 * disables the tables
	 * @param disable
	 */
	private void disableTables(boolean disable) {
		listView.setDisable(disable);
		challengeListView.setDisable(disable);
		originalListView.setDisable(disable);
	}

	/**
	 * @param progressBar progress bar to reset the progress of.
	 * @param playButton the button to set back to a play triangle after completion or cancellation.
	 */
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

	/**
	 * when file stops playing
	 */
	@Override
	public void finish() {
		disableTables(false);
		progressText.setText("Play");
	}
}

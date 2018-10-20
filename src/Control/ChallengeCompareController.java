package Control;

import Main.Main;
import Model.*;
import Ratings.ChallengeRatings;
import Ratings.DifficultyRatings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ChallengeCompareController extends ParentController implements MediaController {

    @FXML
    public ProgressBar originalProgressBar, practiceProgressBar;
    @FXML
    public Text originalProgressText, nameLabel, challengeProgressText;
    @FXML
    public Button correct, wrong, compare, playOriginal, playChallenge;
    @FXML
    public ListView<String> challengeListView, versionListView;
    @FXML
    public ImageView difficultyStar; // set to visible if the name has been flagged difficult
    @FXML
    public Pane subPane;

	private Main main;

    /***********fields****************/
    private ChallengeSession _session;
	private boolean _mainPlayState, _challengePlayState, _autoClicked;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        _mediator.setParent(this);
        _mainPlayState = false;
        _challengePlayState = false;
        _autoClicked = false;

        _session = _mediator.getChallengeSession();
        List<String> list = _mediator.getPracticeMainList();
        ObservableList<String> practiceList = FXCollections.observableArrayList(list);
        challengeListView.setItems(practiceList);

        challengeListView.getSelectionModel().selectFirst();
        nameSelected(challengeListView.getSelectionModel().getSelectedItem());
	    String fileName = versionListView.getSelectionModel().getSelectedItem();
	    _mediator.setOriginalFilename(fileName);
    }

    @FXML
    public void playOriginal(ActionEvent actionEvent) {
	    playOriginal();
    }

    @FXML
    public void playChallenge(ActionEvent actionEvent) {
    	playChallenge();
    }

    @FXML
    public void correct(ActionEvent actionEvent) {
        processRating(true);
    }

    @FXML
    public void wrong(ActionEvent actionEvent) {
        processRating(false);
    }

    private void processRating(Boolean rating){
        String name = _session.getCurrentName();
        String fileName = _session.getChallengeFile(name);
        ChallengeRatings.getInstance().setRating(name, fileName, rating);

        challengeListView.getItems().remove(name);
        if (challengeListView.getItems().size()==0){
            _mediator.removeInChallengeSession();
            popup();
        } else {
            wrong.setDisable(true);
            correct.setDisable(true);
            originalProgressBar.setProgress(0);
            practiceProgressBar.setProgress(0);
            challengeProgressText.setText("Play Your Attempt");
            challengeListView.getSelectionModel().selectFirst();

            String originalFileName = nameSelected(challengeListView.getSelectionModel().getSelectedItem());
            _mediator.setOriginalFilename(originalFileName);
        }
    }

    @FXML
    public void nameSelected(MouseEvent mouseEvent) {
        String name = challengeListView.getSelectionModel().getSelectedItem();
        updateStar(name);
        if (name != null) {
	        String fileName = nameSelected(name);
	        _mediator.setOriginalFilename(fileName);
        }
    }

    @FXML
    public void selectNameOriginal(MouseEvent event) {
        String fileName = versionListView.getSelectionModel().getSelectedItem();

        if (fileName != null) {
           _mediator.setOriginalFilename(fileName);
            playOriginal.setDisable(false);
        } else {
            playOriginal.setDisable(true);
        }
    }

    @FXML
    public void autoCompare(ActionEvent actionEvent) {
    	_autoClicked = true;
    	playOriginal();
    }

    private void playOriginal() {
    	disableTables(true);
    	if (_mainPlayState) {
    		stopPlaying(originalProgressBar, playOriginal);
    		_mainPlayState = false;
	    } else {
		    String name = _session.getCurrentName();
		    String fileName = _mediator.getOriginalFilename();

		    playFile(this, originalProgressText, playOriginal, originalProgressBar, fileName, name, _originals.getFileName(name).size());
		    _mainPlayState = true;
	    }
    }

    private void playChallenge() {
    	if (_challengePlayState) {
    		stopPlaying(practiceProgressBar, playChallenge);
    		_challengePlayState = false;
	    } else {
		    String name = _session.getCurrentName();
		    String fileName = _session.getChallengeFile(name);
		    String dir = "Names/" + name + "/Challenge/" + _session.getChallengeFile(name) + ".wav";
		    Media media = new Media(Challenges.getInstance().getChallenge(name, fileName));

		    playFile(this, challengeProgressText, playChallenge, practiceProgressBar, dir, media);

		    correct.setDisable(false);
		    wrong.setDisable(false);
		    _challengePlayState = true;
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

    public void popup() {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/ChallengePopup.fxml"));
        PopupController popupController = new PopupController();
        loader.setController(popupController);
        Parent layout;
        try {
            layout = loader.load();
            Scene scene = new Scene(layout);
            Stage popupStage = new Stage();
            popupController.setStage(popupStage);

            if (this.main != null) {
                popupStage.initOwner(main.getPrimaryStage());
            }
            popupStage.initModality(Modality.WINDOW_MODAL);
            popupStage.setScene(scene);
            popupStage.showAndWait();
        } catch (IOException error) {
            error.printStackTrace();
        }
    }

    private String nameSelected(String name) {
	    _session.setCurrentName(name);
	    originalProgressText.setText("Play Original");
	    playOriginal.setDisable(false);
	    playChallenge.setDisable(false);

	    List<String> versions = _originals.getFileName(name);
	    ObservableList<String> versionsToDisplay;

	    versionsToDisplay = FXCollections.observableArrayList(versions);
	    versionListView.setItems(versionsToDisplay);
	    nameLabel.setText(name);

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
	    return fileName;
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
        String name = _session.getCurrentName();
        if (name != null) {
            Boolean difficulty = DifficultyRatings.getInstance().getRating(name);
            starOn(difficultyStar, !difficulty);
            DifficultyRatings.getInstance().setRating(name, !difficulty);
        }
    }

    private void disableTables(boolean disable) {
    	challengeListView.setDisable(disable);
    	versionListView.setDisable(disable);
    }

	@Override
	public void stopPlaying(ProgressBar progressBar, Button playButton) {
		stopProgress();
		progressBar.setProgress(0.0);
		playButton.setText("▶️️");
		playButton.setTextFill(Color.LIME);
		if (_mainPlayState) {
			_mainPlayState = false;
		}
		if (_challengePlayState) {
			_challengePlayState = false;
		}
	}

	@Override
	public void finish() {
		if (_autoClicked) {
			_autoClicked = false;
			playChallenge();
		}
	}
}

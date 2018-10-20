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
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * this controller class controls ChallengeMain.fxml
 * it allows the user to compare the challenge attempts to the original recordings
 * it allows the user to rate the attempt (good / bad)
 * at the end of the challenge session, a pop up showing the score is displayed
 *
 * @author Lucy Chen
 */
public class ChallengeCompareController extends ParentController {

    @FXML
    public ProgressBar originalProgressBar;
    @FXML
    public Button playOriginal;
    @FXML
    public ProgressBar practiceProgressBar;
    @FXML
    public Button playChallenge;
    @FXML
    public Text originalProgressText;
    @FXML
    public Button correct;
    @FXML
    public Button wrong;
    @FXML
    public ListView<String> challengeListView;
    @FXML
    public ListView<String> versionListView;
    @FXML
    public Button addPractice;
    @FXML
    public Text nameLabel;
    @FXML
    public ImageView difficultyStar; // set to visible if the name has been flagged difficult
    @FXML
    public Pane subPane;
    @FXML
    public Text challengeProgressText;

    private Main main;

    /***********fields****************/
    private ChallengeSession _session;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        _mediator.setParent(this);
        _session = _mediator.getChallengeSession();
        List<String> list = _mediator.getPracticeMainList();
        ObservableList<String> practiceList = FXCollections.observableArrayList(list);
        challengeListView.setItems(practiceList);
        playChallenge.setDisable(true);
        playOriginal.setDisable(true);
        difficultyStar.setDisable(true);
    }

    /**
     * when event detected, plays the original recording using Media class and does the progress bar
     *
     * @param actionEvent
     */
    @FXML
    public void playOriginal(ActionEvent actionEvent) {
        String name = _session.getCurrentName();
        String fileName = _mediator.getOriginalFilename();

        super.playFile(originalProgressText, playOriginal, originalProgressBar, fileName, name, _originals.getFileName(name).size());
    }

    /**
     * when event detected, plays the users attempt recording using Media class
     * after playing, the rating buttons are enabled
     *
     * @param actionEvent
     */
    @FXML
    public void playChallenge(ActionEvent actionEvent) {

        String name = _session.getCurrentName();
        String fileName = _session.getChallengeFile(name);
        String dir = "Names/" + name + "/Challenge/" + _session.getChallengeFile(name) + ".wav";
        Media media = new Media(Challenges.getInstance().getChallenge(name, fileName));
        super.playFile(challengeProgressText, playChallenge, practiceProgressBar, dir, media);
        correct.setDisable(false);
        wrong.setDisable(false);
    }

    /**
     * when user gives attempt a good rating - system processes rating
     *
     * @param actionEvent
     */
    @FXML
    public void correct(ActionEvent actionEvent) {
        processRating(true);
    }

    /**
     * when user gives attempt a bad rating - system processes rating
     *
     * @param actionEvent
     */
    @FXML
    public void wrong(ActionEvent actionEvent) {
        processRating(false);
    }

    /**
     * If last challenge name is rated, show score pop up and remove in challenge session
     * otherwise it selects the next name in the list and disables the play buttons.
     *
     * @param rating
     */
    private void processRating(Boolean rating) {
        String name = _session.getCurrentName();
        String fileName = _session.getChallengeFile(name);
        ChallengeRatings.getInstance().setRating(name, fileName, rating);

        challengeListView.getItems().remove(name);
        if (challengeListView.getItems().size() == 0) {
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

    /**
     * @param actionEvent
     */
    @FXML
    public void add(ActionEvent actionEvent) {
        _mediator.loadPane(Type.HEADER, "Practice1");
    }

    /**
     * when a name is selected on the bar, the star rating (difficulty) is updated
     * updates the list views of original recordings associated with name
     *
     * @param mouseEvent
     */
    @FXML
    public void nameSelected(MouseEvent mouseEvent) {
        difficultyStar.setDisable(false);
        String name = challengeListView.getSelectionModel().getSelectedItem();
        updateStar(name);
        if (name != null) {
            String fileName = nameSelected(name);
            _mediator.setOriginalFilename(fileName);
        }
    }

    /**
     * original version is selected - updates the file name in Mediator class
     * enables the playoriginal button
     *
     * @param event
     */
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

    /**
     * Load a scene into the {@code subPane}.
     *
     * @param page scene to load
     */
    @Override
    public void loadPane(String page) {
        super.loadPane(page, subPane);
    }


    /**
     * pop up for when all the challenges have been rated
     * pop up containing score is loaded.
     */
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

    /**
     * this method returns the original recording fileName based on the parameter, name
     * if there is only one version of the name, then that one is returned
     * otherwise, the best quality is selected
     * it also updates the difficult star as well as the nameLabel
     */
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
        updateStar(name);
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
     *
     * @param name
     */
    private void updateStar(String name) {
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
}

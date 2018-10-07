package Control;

import Model.*;
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
import javafx.scene.text.Text;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ListenController extends Controller {
    @FXML
    public TextField search;
    @FXML
    public ListView<String> listView, challengeListView, originalListView;
    @FXML
    public Text nameLabel, fileLabel;
    @FXML
    public ImageView difficultyStar;
    @FXML
    public ProgressBar playProgressBar;
    @FXML
    public Button play, delete, goodButton, badButton;
    @FXML
    public Label progressText;

    /*****fields******/
    private String _selected;
    private String _type;
    private boolean _good;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //todo add listeners to each table and adjust progressText text accordingly

        List<String> names = _originals.listNames();

        java.util.Collections.sort(names);
        ObservableList<String> challengeNames = FXCollections.observableArrayList(names);

        FilteredList<String> filteredList = new FilteredList<>(challengeNames, s -> true);
        listView.setItems(filteredList);listView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        search.textProperty().addListener(((observable, oldValue, newValue) -> {
        	searchListener(filteredList, newValue, listView);
        }));
    }

    @FXML
    public void selectName(MouseEvent event) {
        //todo rating show
        String name = listView.getSelectionModel().getSelectedItem();
        nameLabel.setText(name);
        _mediator.setCurrentName(name);
        disableButtons();

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
    public void selectNameChallenge(MouseEvent event) {
        String name = challengeListView.getSelectionModel().getSelectedItem();
        if (name != null) {
            //showRatings(false);
            // playButton_3.setDisable(false);
            //deleteButton_3.setDisable(false);
        } else {
            // playButton_3.setDisable(true);
            //deleteButton_3.setDisable(true);
        }

        _selected = name;
        _type = "challenge";
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

    @FXML
    public void play(ActionEvent event) {
        String name = _mediator.getCurrentName(); //getting the name

        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                //generates file which adds to creation list
                Media media;
                if (_type.equals("original")) { //if type is original

                    Original original = getOriginal(_selected, name, _originals.getFileName(name).size());

                    media = new Media(original);
                } else { //type is challenge
                    media = new Media(Challenges.getInstance().getChallenge(name, _selected));
                }
                media.play();
                return null;
            }
        };

        //after creating the creation, user reviews the audio
        task.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED, e -> {
            progressText.setText("Play.");
        });

        progressText.setText("Playing...");
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();

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
            if (_good == false) { ;
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


}

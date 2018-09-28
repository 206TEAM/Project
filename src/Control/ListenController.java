package Control;

import Model.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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

public class ListenController implements Initializable {
    @FXML
    public TextField search;
    @FXML
    public ListView<String> listView, challengeListView, originalListView;
    @FXML
    public Text nameLabel;
    @FXML
    public ComboBox rating;
    @FXML
    public ImageView difficultyStar;
    @FXML
    public ProgressBar playProgressBar;
    @FXML
    public Button play, delete, correct, wrong;
    @FXML
    public Label progressText;

    /*****fields******/
    private String _selected;
    private String _type;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //todo add listeners to each table and adjust progressText text accordingly

        List<String> names = Originals.getInstance().listNames();

        if (names.size() == 0) {
            //todo when no names
        } else {
            java.util.Collections.sort(names);
            ObservableList<String> challengeNames = FXCollections.observableArrayList(names);
            listView.setItems(challengeNames);
            listView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        }
    }
    @FXML
    public void selectName(MouseEvent event) {
        //todo rating show
        String name = listView.getSelectionModel().getSelectedItem();
        nameLabel.setText(name);
        Mediator.getInstance().setCurrentName(name);

        //todo populate sublists
        populateSubLists();
    }

    /**
     * this method populates the original list and challenge list with files from the model
     */
    public void populateSubLists() {

        String name = Mediator.getInstance().getCurrentName();

        ObservableList<String> originals = FXCollections.observableArrayList(Originals.getInstance().getFileName(name));
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
     * @param event
     */
    @FXML
    public void selectNameOriginal(MouseEvent event) {
        String fileName = originalListView.getSelectionModel().getSelectedItem();
        String name = listView.getSelectionModel().getSelectedItem();

        if (fileName != null) {
            //showRatings(true);
            //playButton_3.setDisable(false);
            //deleteButton_3.setDisable(true);
        } else {
            //playButton_3.setDisable(true);
        }

        _selected = fileName;
        _type = "original";

        //clearRatings();

        Original original;
        if (Originals.getInstance().getFileName(name).size() > 1) {
            original = Originals.getInstance().getOriginalWithVersions(fileName, name);
        } else {
            original = Originals.getInstance().getOriginal(fileName);
        }
        //loadRating(original);
    }

    @FXML
    public void play(ActionEvent actionEvent) {
        String name = Mediator.getInstance().getCurrentName(); //getting the name
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Media media;
                if (_type.equals("original")) { //if type is original

                    Original original;

                    if (Originals.getInstance().getFileName(name).size() > 1) {
                        original = Originals.getInstance().getOriginalWithVersions(_selected, name);
                    } else {
                        original = Originals.getInstance().getOriginal(_selected);
                    }

                    media = new Media(original);
                } else { //type is challenge
                    media = new Media(Challenges.getInstance().getChallenge(name, _selected));
                }
                media.play();
            }
        });
        thread.setDaemon(true);
        thread.start();
        if (_type.equals("original")) {
            //Mediator.getInstance().showProgress(progressBar, "Original", _selected);
        } else {
            //Mediator.getInstance().showProgress(progressBar, "Challenges", _selected + ".wav");
        }

        progressText.setText("Playing...");
    }

    @FXML
    public void delete(ActionEvent actionEvent) {
        //todo popup "Are you sure?", then delete challenge recording
        if (_type.equals("challenge")){
            String name = Mediator.getInstance().getCurrentName();
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

    @FXML
    public void correct(ActionEvent actionEvent) {
        //todo edit stats of name based on challenge recording success ratio
    }

    @FXML
    public void wrong(ActionEvent actionEvent) {
        //todo same as correct()
    }


}

package Control;

import Model.Challenge;
import Model.Challenges;
import Ratings.ChallengeRatings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * this class controls the Stats.fxml file
 * it adds the good, bad and not attemped names into the list views of the scene
 * it controls how the stats of each name is displayed.
 *
 * @author: Lucy Chen
 */
public class StatsController implements Initializable {

    @FXML
    private ListView<String> poorListView, naListView, goodListView;
    @FXML
    private Label nameLabel, attemptLabel, scoreLabel;
    public static final String NASCORE = "NA";

    /******fields******/
    ChallengeRatings _challengeRatings = ChallengeRatings.getInstance();

    /**
     * initialises controller by getting the good, bad, and na lists from the challenge ratings class instance
     * sets these names in the list view
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        List<String> bad = _challengeRatings.get_badNames();
        List<String> good = _challengeRatings.get_goodNames();
        List<String> na = _challengeRatings.get_notAttempedNames();
        ObservableList<String> badList = FXCollections.observableArrayList(bad);
        ObservableList<String> goodList = FXCollections.observableArrayList(good);
        ObservableList<String> naList = FXCollections.observableArrayList(na);
        poorListView.setItems(badList);
        naListView.setItems(naList);
        goodListView.setItems(goodList);
    }

    /**
     * when user selects a name from the good list view, display stats
     *
     * @param event
     */
    @FXML
    public void selectNameGood(MouseEvent event) {
        if (goodListView.getItems().size() > 0) {
            String name = goodListView.getSelectionModel().getSelectedItem();
            updateText(name);
        }
    }

    /**
     * when user selects name from the bad list view display stats
     * @param event
     */
    @FXML
    public void selectNamePoor(MouseEvent event) {
        if (poorListView.getItems().size() > 0) {
            String name = poorListView.getSelectionModel().getSelectedItem();
            updateText(name);
        }
    }

    /**
     * when user selects name from the not attempted list view, display stats
     * @param event
     */
    @FXML
    public void selectNameNa(MouseEvent event) {
        if (naListView.getItems().size() > 0) {
            String name = naListView.getSelectionModel().getSelectedItem();
            updateText(name);
        }
    }

    /**
     * this method updates the stats such as number of attempts, score for name
     * it gets this information from challenge ratings class
     * @param name name of the stats to retrieve
     */
    private void updateText(String name) {
        nameLabel.setText(name);
        try {
            String attempts = Integer.toString(Challenges.getInstance().getChallengeSize(name));
            attemptLabel.setText(attempts);
            String score = Integer.toString(_challengeRatings.getScore(name));
            if (score.equals("-1")) {
                score = NASCORE; //displays NA names as NASCORE constant
            }
            scoreLabel.setText(score);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

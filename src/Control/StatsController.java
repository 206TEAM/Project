package Control;

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
 * this class controls the Stats.fxml page
 * it adds the good, bad and not attemped names into the list views of the scene
 * it controls how the stats of each name is displayed.
 *
 */
public class StatsController implements Initializable {

    public ListView<String> poorListView, naListView, goodListView;
    public Label nameLabel, attemptLabel, scoreLabel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        List<String> bad = ChallengeRatings.getInstance().get_badNames();
        List<String> good = ChallengeRatings.getInstance().get_goodNames();
        List<String> na = ChallengeRatings.getInstance().get_notAttempedNames();
        ObservableList<String> badList = FXCollections.observableArrayList(bad);
        ObservableList<String> goodList = FXCollections.observableArrayList(good);
        ObservableList<String> naList = FXCollections.observableArrayList(na);
        poorListView.setItems(badList);
        naListView.setItems(naList);
        goodListView.setItems(goodList);
    }

    /**
     * when user selects a name from the good list view, display stats
     * @param event
     */
    @FXML
    public void selectNameGood(MouseEvent event) {
        if (goodListView.getItems().size()>0) {
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
        if (poorListView.getItems().size()>0) {
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
        if (naListView.getItems().size()>0) {
            String name = naListView.getSelectionModel().getSelectedItem();
            updateText(name);
        }
    }

    /**
     * this method updates the stats such as number of attempts, score for name
     * @param name
     */
    private void updateText(String name) {
        nameLabel.setText(name);
        try {
            String attempts = Integer.toString(Challenges.getInstance().getChallengeSize(name));
            attemptLabel.setText(attempts);
            String score = Integer.toString(ChallengeRatings.getInstance().getScore(name));
            scoreLabel.setText(score);
        } catch (NumberFormatException e){
            //todo
        }
    }

}

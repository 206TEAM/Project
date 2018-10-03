package Control;

import Model.Challenges;
import Model.Mediator;
import Ratings.ChallengeRatings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class StatsController implements Initializable {

    public ListView<String> poorListView, naListView, goodListView;
    public Label nameLabel, attemptLabel, scoreLabel;

    private void statsText() {
        // todo set statsText to display stats of selected poor/good/na name.
    }

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

    @FXML
    public void selectNameGood(MouseEvent event) { //todo get name for any item from each list selected
        String name = goodListView.getSelectionModel().getSelectedItem();
        updateText(name);
    }

    @FXML
    public void selectNamePoor(MouseEvent event) { //todo get name for any item from each list selected
        String name = poorListView.getSelectionModel().getSelectedItem();
        updateText(name);
    }
    @FXML
    public void selectNameNa(MouseEvent event) { //todo get name for any item from each list selected
        String name = naListView.getSelectionModel().getSelectedItem();
        updateText(name);
    }

    private void updateText(String name) {
        nameLabel.setText(name);
        String attempts = Integer.toString(Challenges.getInstance().getChallengeSize(name));
        System.out.println("attempt is " + attempts);
        attemptLabel.setText(attempts);
        String score = Integer.toString(ChallengeRatings.getInstance().getScore(name));
        System.out.println(score);
        scoreLabel.setText(score);
    }

}

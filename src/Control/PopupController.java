package Control;

import Model.ChallengeSession;
import Model.Mediator;
import Ratings.ChallengeRatings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * this controller controls the popup HelperPopup.fxml file
 * it has a challengesession instance where it reviews what names were rated bad or good
 * and uses this information to display the names in their corresponding lists of good or bad names
 *
 * @author Lucy Chen
 */
public class PopupController extends Controller {
    private Stage stage = null;
    private ChallengeSession _session;
    private Mediator _mediator;

    @FXML
    public ListView<String> correctListView, wrongListView;

    @FXML
    public Text scoreLabel, conditionsText;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    /**
     * Closes the stage of this view
     */
    private void closeStage() {
        if (stage != null) {
            _mediator.loadPane(ParentController.Type.MAIN, "MainMenu");
            _mediator.setPracticeMainList(new ArrayList<>());
            stage.close();
        }
    }

    /**
     * initialises the pop up
     *
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        _session = Mediator.getInstance().getChallengeSession();
        _mediator = Mediator.getInstance();
        _session.getLists();
        conditionsText.setText(_session.getScoreMessage());
        List<String> goodList = _session.getGoodList();
        List<String> badList = _session.getBadList();
        ObservableList<String> correctList = FXCollections.observableArrayList(goodList);
        ObservableList<String> wrongList = FXCollections.observableArrayList(badList);
        String score;
        try {
            score = Integer.toString(_session.getSessionScore());
        } catch (NumberFormatException e) {
            score = "0";
        }
        scoreLabel.setText(score);
        ChallengeRatings.getInstance().newSession(_session.getSessionScore()); //updates the overall stats based on the score from the current challenge session

        if (correctList.size() > 0) { //updates good and bad list of the pop up
            correctListView.setItems(correctList);
        }

        if (wrongList.size() > 0) {
            wrongListView.setItems(wrongList);
        }
    }

    /**
     * when user clicks redo, take the current challenge list from the current challenge session
     * and create a new challenge session with those names
     * change scene
     *
     * @param event
     */
    @FXML
    private void add(ActionEvent event) {
        ChallengeSession newSession = new ChallengeSession(_session.getChallengeList()); //creates a new challengeSession instance with the old names
        _mediator.setChallengeSession(newSession); //sets session in the mediator class
        Mediator.getInstance().setInChallengeSession();
        _mediator.loadPane(ParentController.Type.MAIN, "Header");
        _mediator.loadPane(ParentController.Type.HEADER, "Challenge2");
        stage.close();
    }

    /**
     * when user clicks home, close stage and go to main menu
     *
     * @param event
     */
    @FXML
    private void cancel(ActionEvent event) {
        closeStage();
    }

    /**
     * when user clicks challenge, goes back to the challenge settings scene
     *
     * @param event
     */
    @FXML
    private void challenge(ActionEvent event) {
        _mediator.loadPane(ParentController.Type.MAIN, "Header");
        _mediator.loadPane(ParentController.Type.HEADER, "Challenge1");
        stage.close();
    }
}

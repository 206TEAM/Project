package Control;

import Model.ChallengeSession;
import Model.Media;
import Model.Mediator;
import Ratings.ChallengeRatings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class PopupController implements Initializable {
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
            Mediator.getInstance().loadPane(ParentController.Type.MAIN, "MainMenu");
            stage.close();
        }
    }

    @FXML
    private void add(ActionEvent event) { //todo make it so that it redos the stuff the user has selected
        System.out.println("challenge list is "+_session.getChallengeList());
        ChallengeSession newSession = new ChallengeSession(_session.getChallengeList());
        _mediator.setChallengeSession(newSession);
        Mediator.getInstance().setInChallengeSession();
        _mediator.loadPane(ParentController.Type.MAIN, "Header");
        _mediator.loadPane(ParentController.Type.HEADER, "Challenge2");
        stage.close();
    }

    @FXML
    private void cancel(ActionEvent event) {
        closeStage();
    }

    @FXML
    private void challenge(ActionEvent event){
        _mediator.loadPane(ParentController.Type.MAIN, "Header");
        _mediator.loadPane(ParentController.Type.HEADER, "Challenge1");
        stage.close();
    }

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
        String score = Integer.toString(_session.getSessionScore());
        scoreLabel.setText(score); //todo refactor
        ChallengeRatings.getInstance().newSession(_session.getSessionScore());

        if (correctList.size() > 0) {
            correctListView.setItems(correctList);
        }

        if (wrongList.size() > 0) {
            wrongListView.setItems(wrongList);
        }


    }
}

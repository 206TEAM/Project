package Control;

import Model.ChallengeSession;
import Model.Mediator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class PopupController implements Initializable {
    private Stage stage = null;
    private ChallengeSession _session;

    @FXML
    public ListView<String> correctListView, wrongListView;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    /**
     * Closes the stage of this view
     */
    private void closeStage() {
        if (stage != null) {
            Mediator.getInstance().loadPane(ParentController.Type.MAIN, "MainMenu");
        }
    }

    @FXML
    private void selectNameOriginal(ActionEvent event){

    }
    @FXML
    private void selectNameChallenge(ActionEvent event){

    }
    @FXML
    private void add(ActionEvent event){

    }
    @FXML
    private void cancel(ActionEvent event){

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        _session = Mediator.getInstance().getChallengeSession();
        _session.getLists();
        List<String> goodList = _session.getGoodList();
        List<String> badList = _session.getBadList();
        System.out.println(goodList);
        System.out.println("wrong list is " + badList);
        ObservableList<String> correctList = FXCollections.observableArrayList(goodList);
        ObservableList<String> wrongList = FXCollections.observableArrayList(badList);

        if (correctList.size()>0){
            correctListView.setItems(correctList);
        }

        if (wrongList.size()>0){
            System.out.println("wrong");
            wrongListView.setItems(wrongList);
        }
    }
}

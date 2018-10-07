package Control;

import Model.ChallengeSession;
import Model.Mediator;
import Ratings.ChallengeRatings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Scanner;

public class HelperController implements Initializable {
    private Stage stage = null;
    @FXML
    TextArea helperContent;
    @FXML
    Button closeButton;

    /**
     * Closes the stage of this view
     */
    @FXML
    private void closeStage() {
        if (stage != null) {
            stage.close();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            Scanner s = new Scanner(new File("instructions.txt"));
            while (s.hasNextLine()) {
                String string = s.nextLine();
                helperContent.appendText(string + "\n");
            }
        } catch (FileNotFoundException ex) {
            System.err.println(ex);
        }
    }
}
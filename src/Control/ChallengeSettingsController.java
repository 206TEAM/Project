package Control;

import Model.ChallengeSession;
import Model.Mediator;
import Model.Originals;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

public class ChallengeSettingsController implements Initializable {
    @FXML
    public Slider numberSlider;
    @FXML
    public Slider difficultySlider;
    @FXML
    public Button start;
    @FXML
    public Text howMany;
    @FXML
    public Text howDifficult;

    private Mediator _mediator;

    /***fields***/
    private int _difficulty;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        _mediator = Mediator.getInstance();

        numberSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            howMany.setText(String.valueOf(newValue.intValue()));
        });

        difficultySlider.valueProperty().addListener((observable, oldValue, newValue) -> {
         int num = newValue.intValue();
         _difficulty = num;
         switch (num) {
         case 1:
         howDifficult.setText("Beginner");
         break;
         case 2:
         howDifficult.setText("Easy");
         break;
         case 3:
         howDifficult.setText("Medium");
         break;
         case 4:
         howDifficult.setText("Hard");
         break;
         case 5:
         howDifficult.setText("A true challenge");
         break;
         }
         });
    }

    @FXML
    public void start(ActionEvent actionEvent) {
        Mediator.getInstance().setInChallengeSession();

        int numberOfChallenges;
        if (howMany.getText().equals("How Many")) {
            numberOfChallenges = 1;
        } else {
            numberOfChallenges = Integer.parseInt(howMany.getText());
        }

        if (howDifficult.getText().equals("How Difficult")) {
            _difficulty = 1;
        }

         ChallengeSession session = new ChallengeSession(numberOfChallenges, _difficulty);
        System.out.println("from settingsController" + numberOfChallenges + " " + _difficulty);
         Mediator.getInstance().setChallengeSession(session);
        // TODO implement DIFFICULTY with math and stuff (THIS WILL CHANGE HOW THE RANDOM NAMES ARE SELECTED)
        _mediator.loadPane(ParentController.Type.HEADER, "Challenge2");
    }


}

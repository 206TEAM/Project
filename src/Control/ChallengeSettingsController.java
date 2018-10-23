package Control;

import Model.ChallengeSession;
import Model.Mediator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.text.Text;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * this class controls the Challenge1.fxml file
 * It prompts the user to select how many challenges the user wishes to have
 * as well as how difficult the challenges are.
 * a challenge session instance is created from pressing start which is stored in the Mediator class
 *
 * @author Lucy Chen
 * @author Eric Pedrido
 */
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

        /**
         * initialises the slider values and sets values to beginner, easy, and medium, hard, and true challenge
         */
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
         }});
    }

    /**
     * when user clicks the start button
     * if they didn't touch the slider value for how many, initialises the challenge size to 1
     * if they didn't touch the difficulty slider, initalises the difficulty to 1
     * @param actionEvent
     */
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

         ChallengeSession session = new ChallengeSession(numberOfChallenges, _difficulty); //create a challenge session
         Mediator.getInstance().setChallengeSession(session); //setting the challenge session in Mediator class
        _mediator.loadPane(ParentController.Type.HEADER, "Challenge2"); //switch scenes
    }


}

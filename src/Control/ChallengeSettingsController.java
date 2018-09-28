package Control;

import Model.Mediator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;

public class ChallengeSettingsController {
	@FXML public Slider numberSlider;
	@FXML public Slider difficultySlider;
	@FXML public Button start;

	@FXML
	public void start(ActionEvent actionEvent) {
		// todo slider math stuff (Apply settings based on slider... maybe have their own event listeners)
		Mediator.getInstance().loadPane(ParentController.Type.HEADER, "Challenge2");
	}
}

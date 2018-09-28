package Control;

import Model.Mediator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class PracticeCompareController {
	@FXML public Button playOriginal;
	@FXML public Button playPractice;
	@FXML public Button next;
	@FXML public Label originalProgressText;
	@FXML public Label practiceProgressText;

	@FXML
	public void playOriginal(ActionEvent actionEvent) {
		//todo play original audio
		//todo progressbar ting
		originalProgressText.setText("Playing...");
	}

	@FXML
	public void playPractice(ActionEvent actionEvent) {
		practiceProgressText.setText("Playing...");
	}

	@FXML
	public void next(ActionEvent actionEvent) {
		// if (practiceListView.size() > 0)
		Mediator.getInstance().loadPane(ParentController.Type.SUB_MAIN, "PracticeCompare");
		// else { Mediator.getInstance().loadPane(ParentController.Type.MAIN, "MainMenu"); }
	}
}

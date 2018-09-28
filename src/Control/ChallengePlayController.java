package Control;

import Model.Mediator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ResourceBundle;

public class ChallengePlayController implements Initializable {
	@FXML public Text nameLabel;
	@FXML public ProgressIndicator timer;
	@FXML public Button next;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		timer();
		record();
	}

	@FXML
	public void next(ActionEvent actionEvent) {
		//todo stop and save recording

		// if (namesList.size() > 0) {
		//      timer();
		//      record();
		// else {
				next.setText("Done");
				Mediator mediator = Mediator.getInstance();
				mediator.loadPane(ParentController.Type.HEADER, "PracticeMain");
				mediator.loadPane(ParentController.Type.SUB_MAIN, "Challenge3");
		// }

	}

	private void timer() {
		//todo start from zero, go to 100% when 5 seconds is up
	}

	private void record() {
		//todo record audio
	}

}

package Control;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ResourceBundle;

public class ListenController implements Initializable {
	@FXML public TextField search;
	@FXML public ListView listView;
	@FXML public Text nameLabel;
	@FXML public ComboBox rating;
	@FXML public ImageView difficultyStar;
	@FXML public ListView challengeListView;
	@FXML public ListView originalListView;
	@FXML public ProgressBar playProgressBar;
	@FXML public Button play;
	@FXML public Button delete;
	@FXML public Button correct;
	@FXML public Button wrong;
	@FXML public Label progressText;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		//todo add listeners to each table and adjust progressText text accordingly
	}

	@FXML
	public void play(ActionEvent actionEvent) {
		// todo play audio

		progressText.setText("Playing...");
	}

	@FXML
	public void delete(ActionEvent actionEvent) {
		//todo popup "Are you sure?", then delete challenge recording

	}

	@FXML
	public void correct(ActionEvent actionEvent) {
		//todo edit stats of name based on challenge recording success ratio
	}

	@FXML
	public void wrong(ActionEvent actionEvent) {
		//todo same as correct()
	}


}

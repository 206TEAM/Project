package Control;

import Model.Mediator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ResourceBundle;

public class PracticeCompareController extends PracticeMainController implements Observer {
	@FXML public Button playOriginal;
	@FXML public Button playPractice;
	@FXML public Button next;
	@FXML public Text originalProgressText;
	@FXML public Text practiceProgressText;
	@FXML public ProgressBar originalProgressBar;
	@FXML public ProgressBar practiceProgressBar;

	private Mediator _mediator;
	private String _currentFileName;
	private String _currentName;
	private int _numVersions;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		_mediator = Mediator.getInstance();
		//_mediator.fireDisableTable(TableType.VERSION, false);
		_mediator.addObserver(this);
		//todo SET THE CURRENT FILENAME, NAME, NUMBER OF VERSION.
		//TODO play PRACTICE
		//todo USER RESTRICTIONS.
	}

	@FXML
	public void playOriginal(ActionEvent actionEvent) {
		super.playFile(originalProgressText, playOriginal, originalProgressBar,
				_mediator.getOriginalFilename(), _mediator.getCurrentName(), _numVersions);
	}

	@FXML
	public void playPractice(ActionEvent actionEvent) {
		practiceProgressText.setText("Playing...");
	}

	@FXML
	public void next(ActionEvent actionEvent) {
		// if (practiceListView.size() > 0)
		Mediator.getInstance().loadPane(Type.PRACTICE, "PracticeCompare");
		// else { Mediator.getInstance().loadPane(ParentController.Type.MAIN, "MainMenu"); }
	}

	@Override
	public void update(String name, String fileName, int numberOfVersions) {
	}
}

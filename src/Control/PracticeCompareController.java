package Control;

import Model.Media;
import Model.Mediator;
import Model.Practice;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.List;
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
	private Practice _currentPractice;

	private boolean _finalPractice;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		_mediator = Mediator.getInstance();
		//_mediator.fireDisableTable(TableType.VERSION, false);
		_mediator.addObserver(this);

		_currentFileName = _mediator.getOriginalFilename();
		_currentName = _mediator.getCurrentName();
		_numVersions = _mediator.getNumVersions();
		_currentPractice = _mediator.getCurrentPractice();

		List<String> practices = _mediator.getPracticeMainList();
		practices.remove(_currentName);
		if (practices.size() == 0) {
			_finalPractice = true;
			next.setText("Main Menu");
			next.setFont(new Font(14.0));
		} else {
			_mediator.setPracticeMainList(practices);
			_mediator.fireTableValues(practices);
			_finalPractice = false;
		}
	}

	@FXML
	public void playOriginal(ActionEvent actionEvent) {
//	todo	super.playFile(originalProgressText, playOriginal, originalProgressBar,
//				_mediator.getOriginalFilename(), _mediator.getCurrentName(), _numVersions);
		_mediator.fireDisableTable(TableType.VERSION, false);
	}

	@FXML
	public void playPractice(ActionEvent actionEvent) {
		String dir = "Temp/" + _currentPractice.getFileName() + ".wav";
//		todo super.playFile(practiceProgressText, playPractice, practiceProgressBar, dir, new Media(_currentPractice));
		_mediator.fireDisableTable(TableType.VERSION, false);
	}

	@FXML
	public void next(ActionEvent actionEvent) {
		if (_finalPractice) {
			_mediator.loadPane(Type.MAIN, "MainMenu");
		} else {
			_mediator.fireDisableTable(TableType.PRACTICE, false);
			_mediator.loadPane(Type.PRACTICE, "PracticeRecord");
		}
	}

	public void update(String name, String fileName, int numberOfVersions, Practice practice) {
		_currentName = fileName;
		_numVersions = numberOfVersions;
	}
}

package Control;

import Model.Mediator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ResourceBundle;

public class PracticeMainController extends ParentController{
	@FXML public ListView practiceListView;
	@FXML public ListView versionListView;
	@FXML public Button addPractice;
	@FXML public Text nameLabel;
	@FXML public ComboBox rating;
	@FXML public ImageView difficultyStar; // set to visible if the name has been flagged difficult
	@FXML public Pane subPane;

	private Mediator _mediator;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		_mediator = Mediator.getInstance();
		_mediator.setParent(this);
	}

	@FXML
	public void add(ActionEvent actionEvent) {
		_mediator.loadPane(Type.HEADER, "Practice1");
	}

	/**
	 * Load a scene into the {@code subPane}.
	 *
	 * @param page scene to load
	 */
	@Override
	public void loadPane(String page) {
		super.loadPane(page, subPane);
	}
}

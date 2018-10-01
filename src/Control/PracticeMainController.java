package Control;

import Model.Mediator;
import Model.Original;
import Model.Originals;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class PracticeMainController extends ParentController {
	@FXML public ListView<String> practiceListView;
	@FXML public ListView<String> versionListView;
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
		List<String> list = _mediator.getPracticeMainList();
		ObservableList<String> practiceList = FXCollections.observableArrayList(list);
		practiceListView.setItems(practiceList);
	}

	@FXML
	public void add(ActionEvent actionEvent) {
		_mediator.loadPane(Type.HEADER, "Practice1");
	}

	@FXML
	public void nameSelected(MouseEvent mouseEvent) {
		String name = practiceListView.getSelectionModel().getSelectedItem();
		Mediator.getInstance().setCurrentName(name);
		if (name != null) {
			List<String> versions = Originals.getInstance().getFileName(name);
			ObservableList<String> versionsToDisplay = FXCollections.observableArrayList(versions);
			versionListView.setItems(versionsToDisplay);
			nameLabel.setText(name);
		}
	}

	@FXML
	public void selectNameOriginal(MouseEvent event) {
		String fileName = versionListView.getSelectionModel().getSelectedItem();
		String name = practiceListView.getSelectionModel().getSelectedItem();
		System.out.println(fileName);



		if (fileName != null) {
			Mediator.getInstance().setOriginalFilename(name);
		} else {
			//playButton_3.setDisable(true);
		}
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

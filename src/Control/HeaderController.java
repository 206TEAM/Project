package Control;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;

public class HeaderController extends ParentController {

	@FXML public Text home;
	@FXML public Button micTest;
	@FXML public Pane headerPane;
	@FXML public Text menuLabel;

	private PageType _page;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		_mediator.setParent(this);
	}

	@FXML
	public void home(MouseEvent mouseEvent) {
		if (_page == PageType.CHALLENGE || _page == PageType.PRACTICE) {
			Alert warning = new Alert(Alert.AlertType.CONFIRMATION);
			warning.setTitle("Warning message");
			warning.setHeaderText("Are you sure you wish to exit?");

			String text;
			if (_page == PageType.PRACTICE) {
				text = "Your playlist will be lost";
			} else {
				text = "The Challenge list will be lost";
			}
			warning.setContentText(text);

			ButtonType yes = new ButtonType("Yes", ButtonBar.ButtonData.YES);
			ButtonType cancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

			warning.getButtonTypes().setAll(yes, cancel);

			Optional<ButtonType> result = warning.showAndWait();
			if (result.get() == yes) {
				_mediator.setPracticeMainList(new ArrayList<>());
				_mediator.loadPane(Type.MAIN, "MainMenu");
			} else {
				warning.close();
			}
		} else {
			_mediator.loadPane(Type.MAIN, "MainMenu");
		}
	}

	@FXML
	public void micTest(ActionEvent actionEvent) {
		createPopUp("MicTest", "Microphone Test", 450, 259);
	}

	public void setPage(PageType pageType) {
		String text;
		if (pageType == PageType.PRACTICE) {
			text = "Practice";
		} else if (pageType == PageType.CHALLENGE) {
			text = "Challenge";
		} else if (pageType == PageType.LISTEN) {
			text = "Listen";
		} else {
			text = "Stats";
		}
		_page = pageType;
		menuLabel.setText(text);
	}

	/**
	 * Load a scene into the pane with a header
	 *
	 * @param page scene to load in {@code headerPane}
	 */
	@Override
	public void loadPane(String page) {
		super.loadPane(page, headerPane);
	}

	public void homeHovered(MouseEvent mouseEvent) {
		home.setFill(Paint.valueOf("#ff9900"));
	}

	public void homeExited(MouseEvent mouseEvent) {
		home.setFill(Color.WHITE);
	}
}

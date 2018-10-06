package Control;

import Model.ChallengeSession;
import Model.Mediator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class HeaderController extends ParentController {

	@FXML public Text home;
	@FXML public Button micTest;
	@FXML public Pane headerPane;
	@FXML public Text menuLabel;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		_mediator.setParent(this);
	}

	@FXML
	public void home(MouseEvent mouseEvent) {
		Boolean inSession = _mediator.getChallengeStatus();

		if (inSession) {
			boolean confirmAction = false;
			confirmAction = confirmAction();

			// If the user confirms, delete it
			if (confirmAction) {
				ChallengeSession _session =_mediator.getChallengeSession();
				_session.abortSession(); //gets rid of challenges
				_mediator.removeInChallengeSession();
				_mediator.loadPane(Type.MAIN, "MainMenu");
			}
		} else {
			_mediator.loadPane(Type.MAIN, "MainMenu");
			Mediator.getInstance().removeInChallengeSession();
		}
	}

	/**
	 * A confirmation popup that asks user if they want to delete their creation
	 */
	public boolean confirmAction() {
		Label l = new Label("Are you sure you want to abandon your challenge session?");
		l.setWrapText(true);

		Alert alert = new Alert(Alert.AlertType.WARNING);
		alert.setTitle("Abandon");
		alert.getDialogPane().setContent(l);

		//Creates the buttons
		ButtonType noButton = new ButtonType("No", ButtonBar.ButtonData.NO);
		ButtonType yesButton = new ButtonType("Yes", ButtonBar.ButtonData.YES);
		alert.getButtonTypes().setAll(yesButton, noButton);

		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == yesButton) {
			return true;
		} else {
			return false;
		}
	}

	@FXML
	public void micTest(ActionEvent actionEvent) { //todo MIC TEST POPUP
		createPopUp("MicTest", "Microphone Test", 450, 259);
	}

	@FXML
	public void help(ActionEvent actionEvent) {//todo HELP POPUP
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

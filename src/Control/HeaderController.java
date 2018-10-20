package Control;

import Model.ChallengeSession;
import Model.Challenges;
import Model.Mediator;
import Ratings.ChallengeRatings;
import Ratings.DifficultyRatings;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.Effect;
import javafx.scene.image.ImageView;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import javax.swing.*;
import java.net.URL;
import java.util.Optional;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;

public class HeaderController extends ParentController {

	@FXML public Text home;
	@FXML public Button micTest;
	@FXML public Pane headerPane;
	@FXML public Text menuLabel;
	@FXML public ImageView micImage;
	@FXML public MenuButton settings;
	@FXML public MenuItem save, reset, help, quit;

	private PageType _page;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		_mediator.setParent(this);
	}

	@FXML
	public void home(MouseEvent mouseEvent) {

        //Boolean inSession = _mediator.getChallengeStatus();

		if (_page == PageType.CHALLENGE || _page == PageType.PRACTICE) {
			String text;
			if (_page == PageType.PRACTICE) {
				text = "Your playlist will be lost";
			} else {
				text = "The Challenge list will be lost";
			}
            boolean confirmAction = false;
            confirmAction = confirmAction(text);

            // If the user confirms, delete it
            if (confirmAction) {
                ChallengeSession _session =_mediator.getChallengeSession();
                if (_session!=null) {
					_session.abortSession(); //gets rid of challenges
				}
                _mediator.removeInChallengeSession();
                _mediator.loadPane(Type.MAIN, "MainMenu");
				_mediator.setPracticeMainList(new ArrayList<>());
            }
        } else {
            _mediator.loadPane(Type.MAIN, "MainMenu");
            //Mediator.getInstance().removeInChallengeSession();
        }
	}

	/**
	 * A confirmation popup that asks user if they want to delete their creation
	 */
	public boolean confirmAction(String text) {
		Label l = new Label(text);
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

	@FXML
	public void homeHovered(MouseEvent mouseEvent) {
		home.setFill(Paint.valueOf("#ff9900"));
	}

	@FXML
	public void homeExited(MouseEvent mouseEvent) {
		home.setFill(Color.WHITE);
	}

	@FXML
	public void micHovered(MouseEvent mouseEvent) {
		colorAdjust(1.0); // Make it white
	}

	@FXML
	public void micExited(MouseEvent mouseEvent) {
		colorAdjust(0.0); // Return it to orange
	}

	@FXML
	public void quit(ActionEvent actionEvent) {
		ButtonType saveQuit = new ButtonType("Save and quit", ButtonBar.ButtonData.YES);
		ButtonType quit = new ButtonType("Quit", ButtonBar.ButtonData.APPLY);
		ButtonType cancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

		Alert confirm = createAlert(Alert.AlertType.WARNING, "Confirm exit",
				"Are you sure you wish to exit?", "You have unsaved progress!",
				new ButtonType[]{saveQuit, quit, cancel});
		DialogPane dp = confirm.getDialogPane();
		dp.getButtonTypes().stream().map(dp::lookupButton)
				.forEach(node -> ButtonBar.setButtonUniformSize(node, false));

		Optional<ButtonType> result = confirm.showAndWait();
		if (result.get() == saveQuit) {
			//todo "save" the work
			exit(headerPane);
		} else if (result.get() == quit) {
			exit(headerPane);
		} else {
			confirm.close();
		}
	}

	private void colorAdjust(double brightness) {
		ColorAdjust colorAdjust = new ColorAdjust();
		colorAdjust.setBrightness(brightness);
		colorAdjust.setHue(0.2);
		colorAdjust.setSaturation(1.0);
		micImage.setEffect(colorAdjust);
	}

	@FXML
	private void help(ActionEvent event){
		createPopUp("HelperPopup", "Help", 550, 350);
	}

	@FXML
	public void reset(ActionEvent actionEvent) { //todo move to header
		String title = "Reset Progress";
		String header = "Are you sure you want to delete all your progress?";
		String content = "This cannot be undone.";
		Alert alert = createAlert(Alert.AlertType.CONFIRMATION, title, header, content);

		Optional<ButtonType> result = alert.showAndWait();
		Challenges.getInstance().reset();

		if (result.get() == _yes) {
			donePopUp("Your progress has been reset");
			_mediator.loadPane(Type.MAIN, "MainMenu");
		}
	}


}

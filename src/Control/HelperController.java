package Control;

import Model.ChallengeSession;
import Model.ImagesHelper;
import Model.Mediator;
import Ratings.ChallengeRatings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Scanner;

/**
 * this controller controls the helperpopup.fxml file
 * a pop up is displayed containing information about how to use the app
 */
public class HelperController extends Controller {
    @FXML
    public AnchorPane pane;
    @FXML
    Button closeButton, nextButton, backButton;
    @FXML
    ImageView imgViewer;

    /****Fields*****/
    ImagesHelper _imagesHelper = ImagesHelper.getInstance();

    /**
     * Closes the stage of this view
     */
    @FXML
    private void closeStage() {
        exit(pane);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        _imagesHelper.setImage(Mediator.getInstance().getPageType());
        Image image = _imagesHelper.getCurrentImage();
        imgViewer.setImage(image);
        backButton.setDisable(true);
    }

    @FXML
    private void nextImage(ActionEvent event) {
        backButton.setDisable(false);
        Image image = _imagesHelper.nextImage();
        if (image != null) {
            imgViewer.setImage(image);
        }
        if (_imagesHelper.lastImage()) {

            nextButton.setDisable(true);
        }

    }

    @FXML
    private void previousImage(ActionEvent event) {
        nextButton.setDisable(false);
        Image image = _imagesHelper.previousImage();
        if (image != null) {
            imgViewer.setImage(image);
        }
        if (_imagesHelper.firstImage()) {
            backButton.setDisable(true); //when first image, set disable back button
        }
    }
}

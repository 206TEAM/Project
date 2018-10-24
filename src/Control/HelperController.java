package Control;

import Model.ImagesHelper;
import Model.Mediator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import java.net.URL;
import java.util.ResourceBundle;
/**
 * this controller controls the Helperpopup.fxml file
 * a pop up is displayed containing information about how to use the app
 *
 * @author Lucy Chen
 */
public class HelperController extends Controller {
    @FXML
    public AnchorPane pane;
    @FXML
    Button closeButton, nextButton, backButton;
    @FXML
    ImageView imgViewer;

    /*******fields******/
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
        _imagesHelper.setImage(Mediator.getInstance().getPageType()); //sets the image of the helper based on the page the user is currently on
        Image image = _imagesHelper.getCurrentImage();
        imgViewer.setImage(image);
        backButton.setDisable(true);
    }

    /**
     * when user clicks on the next button, displays next image
     * disablse next button if on the last image
     * @param event
     */
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

    /**
     * when user clicks on back button, displays previous image
     * disables back button when on first image.
     * @param event
     */
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

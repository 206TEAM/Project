package Control;

import Model.*;
import Ratings.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import Main.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ChallengeCompareController extends ParentController {

    @FXML
    public ProgressBar originalProgressBar;
    @FXML
    public Button playOriginal;
    @FXML
    public ProgressBar practiceProgressBar;
    @FXML
    public Button playChallenge;
    @FXML
    public Text originalProgressText;
    @FXML
    public Text practiceProgressText;
    @FXML
    public Button correct;
    @FXML
    public Button wrong;

    @FXML
    public ListView<String> challengeListView;
    @FXML
    public ListView<String> versionListView;
    @FXML
    public Button addPractice;
    @FXML
    public Text nameLabel;
    @FXML
    public ComboBox rating;
    @FXML
    public ImageView difficultyStar; // set to visible if the name has been flagged difficult
    @FXML
    public Pane subPane;

    private Mediator _mediator;
    private Main main;

    /***********fields****************/
    private ChallengeSession _session;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        _mediator = Mediator.getInstance();
        _mediator.setParent(this);
        _session = Mediator.getInstance().getChallengeSession();
        List<String> list = _mediator.getPracticeMainList();
        ObservableList<String> practiceList = FXCollections.observableArrayList(list);
        challengeListView.setItems(practiceList);
    }

    @FXML
    public void playOriginal(ActionEvent actionEvent) {
        //todo play original audio
        //todo progress bar ting
        originalProgressText.setText("Playing...");

        //String name = Mediator.getInstance().getCurrentName();
        //String fileName = Mediator.getInstance().getOriginalFilename();
       // System.out.println("name of original is " + name);
       // System.out.println("fileName is " + fileName);


        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
	            Media media;

	            String name = _session.getCurrentName();
	            String fileName = Mediator.getInstance().getOriginalFilename();

	            Original original;
	            if (Originals.getInstance().getFileName(name).size() > 1) {
		            original = Originals.getInstance().getOriginalWithVersions(fileName, name);
		            // System.out.println(original.getFileName());
	            } else {
		            original = Originals.getInstance().getOriginal(fileName);
		            //System.out.println(original.getFileName());
	            }

	            media = new Media(original);
	            media.play();
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    @FXML
    public void playChallenge(ActionEvent actionEvent) {
        correct.setDisable(false);
        wrong.setDisable(false);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Media media;

                String name = _session.getCurrentName();
                String fileName = _session.getChallengeFile(name);

                media = new Media(Challenges.getInstance().getChallenge(name, fileName));

                media.play();
            }
        });
        thread.setDaemon(true);
        thread.start();

    }


    @FXML
    public void correct(ActionEvent actionEvent) {
        //todo mark this name (and this challenge recording) to be a success (Add it to stats)
        processRating(true);
    }

    @FXML
    public void wrong(ActionEvent actionEvent) {
        //todo mark this name (and this challenge Recording) to be a failure
        processRating(false);
    }

    private void processRating(Boolean rating){
        String name = _session.getCurrentName();
        String fileName = _session.getChallengeFile(name);
        ChallengeRatings.getInstance().setRating(name, fileName, rating);

        challengeListView.getItems().remove(name);
        if (challengeListView.getItems().size()==0){
            popup();
            //Mediator.getInstance().loadPane(ParentController.Type.MAIN, "MainMenu");
        } else {
            wrong.setDisable(true);
            correct.setDisable(true);
            nameLabel.setText("--");
        }
    }


    @FXML
    public void add(ActionEvent actionEvent) {
        _mediator.loadPane(Type.HEADER, "Practice1");
    }

    @FXML
    public void nameSelected(MouseEvent mouseEvent) {
        String name = challengeListView.getSelectionModel().getSelectedItem();
        _session.setCurrentName(name);
        if (name != null) {
            List<String> versions = Originals.getInstance().getFileName(name);
            ObservableList<String> versionsToDisplay = FXCollections.observableArrayList(versions);
            versionListView.setItems(versionsToDisplay);
            nameLabel.setText(name);
            playChallenge.setDisable(false);

        }
    }

    @FXML
    public void selectNameOriginal(MouseEvent event) {
        String fileName = versionListView.getSelectionModel().getSelectedItem();
        System.out.println(fileName);


        if (fileName != null) {
           Mediator.getInstance().setOriginalFilename(fileName);
            playOriginal.setDisable(false);
        } else {
            playOriginal.setDisable(true);
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


    public void popup() {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/ChallengePopup.fxml"));
        PopupController popupController = new PopupController();
        loader.setController(popupController);
        Parent layout;
        try {
            layout = loader.load();
            Scene scene = new Scene(layout);
            Stage popupStage = new Stage();
            popupController.setStage(popupStage);

            if (this.main != null) {
                popupStage.initOwner(main.getPrimaryStage());
            }
            popupStage.initModality(Modality.WINDOW_MODAL);
            popupStage.setScene(scene);
            popupStage.showAndWait();
        } catch (IOException error) {
            error.printStackTrace();
        }
    }
}

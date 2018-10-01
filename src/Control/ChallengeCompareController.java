package Control;

import Model.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

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


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        _mediator = Mediator.getInstance();
        _mediator.setParent(this);
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

	            String name = Mediator.getInstance().getCurrentName();
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
        System.out.println("playing practice");
//        practiceProgressText.setText("Playing...");
        String name = Mediator.getInstance().getCurrentName();
        String fileName = Mediator.getInstance().getChallengeFile(name);

        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                //generates file which adds to creation list
                Media media;

                media = new Media(Challenges.getInstance().getChallenge(name, fileName));

                media.play();
                return null;
            }
        };

        //after creating the creation, user reviews the audio
        task.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED, e -> {
            practiceProgressText.setText("Play.");
        });


    }

    @FXML
    public void correct(ActionEvent actionEvent) {
        //todo mark this name (and this challenge recording) to be a success (Add it to stats)

        // if (practiceListView.size() == 0) {
        Mediator.getInstance().loadPane(ParentController.Type.MAIN, "MainMenu");
    }

    @FXML
    public void wrong(ActionEvent actionEvent) {
        //todo mark this name (and this challenge Recording) to be a failure

        // if (practiceListView.size() == 0) {
        Mediator.getInstance().loadPane(ParentController.Type.MAIN, "MainMenu");
    }


    @FXML
    public void add(ActionEvent actionEvent) {
        _mediator.loadPane(Type.HEADER, "Practice1");
    }

    @FXML
    public void nameSelected(MouseEvent mouseEvent) {
        String name = challengeListView.getSelectionModel().getSelectedItem();
        Mediator.getInstance().setCurrentName(name);
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
        String name = challengeListView.getSelectionModel().getSelectedItem();
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
}

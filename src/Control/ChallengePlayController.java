package Control;

import Model.ChallengeSession;
import Model.Challenges;
import Model.Media;
import Model.Mediator;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.net.URL;
import java.util.*;

public class ChallengePlayController implements Initializable{
    @FXML
    public Text nameLabel;
    @FXML
    public ProgressIndicator timer;
    @FXML
    public Button next;
    @FXML
    public Text countdown;

    private int _seconds;
    private int _iteration;
    private List<String> _challengeList;
    private Timeline _timeLine;
    private ChallengeSession _session;
    private Mediator _mediator;
    private Boolean sessionEnded;

    public void abort(){
        _timeLine.stop();
        _challengeList.clear();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        _session = Mediator.getInstance().getChallengeSession();
        _mediator = Mediator.getInstance();
        _challengeList = _session.getChallengeList();
        _iteration = 0;

        _timeLine = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(timer.progressProperty(), 0)),
                new KeyFrame(Duration.seconds(5), new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        ChallengePlayController.this.nextName();
                    }
                }, new KeyValue(timer.progressProperty(), 1)));
        _timeLine.setCycleCount(_challengeList.size());

        Timer countdownTimer = new Timer(true);
        _seconds = 3;
        countdownTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (_seconds == 0) {
                    countdownTimer.cancel();
                    countdown.setVisible(false);
                    next.setDisable(false);
                    timer();
                    record();
                    loadName();
                } else {
                    countdown.setText(String.valueOf(_seconds));
                    _seconds--;
                }
            }
        }, 0, 1000);
    }

    @FXML
    public void next(ActionEvent actionEvent) {
        if (next.getText().equals("Done")) {

            Media.cancel();
            _mediator.setPracticeMainList(_challengeList);
            _mediator.loadPane(ParentController.Type.MAIN, "Header");
            _mediator.loadPane(ParentController.Type.HEADER, "ChallengeMain");
        } else {
            nextName();
        }
    }

    private void timer() {
        if(Mediator.getInstance().getChallengeStatus()) {
            timer.setProgress(0.0);
            if (_timeLine.getStatus().equals(Animation.Status.RUNNING)) {
                _timeLine.stop();
            }
            _timeLine.play();
        } else {
            abort();
        }
    }

    private void record() {
        Thread thread = new Thread(new Task<Void>() {
            @Override
            protected Void call() throws Exception {

                String name = _challengeList.get(_iteration - 1);
                String fileName = Challenges.getInstance().addNewChallenge(name);
                _session.addChallengeFile(fileName);
                return null;
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    private void loadName() {
        if (_challengeList.size()>0) {
            String currentName = _challengeList.get(_iteration);
            nameLabel.setText(currentName);
            _iteration++;
        }
    }

    private void nextName() {
        if (_iteration == _challengeList.size()) {
            Media.cancel();
            next.setText("Done");
            _timeLine.stop();
            timer.setProgress(1.0);
        } else {
            Media.cancel();
            timer();
            record();
            loadName();
        }
    }

}

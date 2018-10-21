package Control;

import Model.Media;
import Model.Mediator;
import Model.Original;
import Model.Originals;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Methods that all controllers can use as utility.
 *
 * @author Eric Pedrido
 */
abstract class Controller implements Initializable {

    public Mediator _mediator = Mediator.getInstance();
    protected Originals _originals = Originals.getInstance();
    protected List<String> _allNames = _originals.listNames();

    private Timeline _progressTimeline;
    protected ButtonType _yes;

    /**
     * Same as {@link List#contains(Object)} method, except
     * it finds out if the {@code List<String>} contains a
     * {@code String} that is case insensitive.
     *
     * @param string case insensitive {@code String} to find.
     * @param list   the {@code List<String>} to search through.
     * @return {@code true} if the list does indeed contain the
     * {@code String} regardless of the case. Otherwise,
     * returns {@code false}.
     */
    protected boolean containsName(String string, List<String> list) {
        return list.stream().anyMatch(x -> x.equalsIgnoreCase(string));
    }

    /**
     * Finds the {@code Original} with the given parameters even
     * with multiple versions.
     *
     * @param fileName    unique filename of the {@code Original}.
     * @param name        the name the {@code Original} corresponds to.
     * @param numVersions number of versions of the desired {@code Original}.
     * @return the exact version of the {@code Original}.
     */
    protected Original getOriginal(String fileName, String name, int numVersions) {
        Original original;
        if (numVersions > 1) {
            original = _originals.getOriginalWithVersions(fileName, name);
        } else {
            original = _originals.getOriginal(fileName);
        }
        return original;
    }

    /**
     * Plays the {@code Media} provided.
     *
     * <p> Also changes the {@code progressText} to become <q>Playing...</q> when playing,
     * and <q>Done</q> when finished while displaying the current duration of a file in
     * real-time. </p>
     *
     * @param progressText the JavaFX component to change text of.
     * @param playButton   the JavaFX button to disable and enable when playing.
     * @param progressBar  the progress bar being displayed.
     * @param dir          the directory of file (e.g Names/Ahn/Challenge/AhnChallenge1.wav).
     * @param media        the {@code Media} to play.
     */
    public void playFile(MediaController controller, Text progressText, Button playButton, ProgressBar progressBar, String dir, Media media) {
        EventHandler<ActionEvent> doAfter = event -> {
            progressText.setText("Done");
            playButton.setText("▶️");
            playButton.setTextFill(Color.LIME);
            controller.stopPlaying(progressBar, playButton);
            controller.finish();
        };

        progressText.setText("Playing...");
        playButton.setText("◼️");
        playButton.setTextFill(Color.RED);

        if (_mediator.praticeNotNull()) {
            _mediator.fireDisableTable(PracticeMainController.TableType.PRACTICE, PracticeMainController.TableType.VERSION, true);
        }

        Thread thread1 = new Thread(media::play);
        Thread thread2 = new Thread(() -> showProgress(progressBar, dir, doAfter));

        thread1.setDaemon(true);
        thread2.setDaemon(true);
        thread1.start();
        thread2.start();
    }

    /**
     * Plays the {@code Original} corresponding to the parameters entered.
     *
     * @param fileName    the fileName of the {@code Original}.
     * @param name        the name of the {@code Original}.
     * @param numVersions the number of versions the {@code Original} has.
     */
    public void playFile(MediaController controller, Text progressText, Button playButton, ProgressBar progressBar, String fileName, String name,
                         int numVersions) {
        Original original;
        String dir;

        // Find the corresponding Original given the parameters
        if (name.contains(" ")) {
            original = _originals.getConcatOriginal(name);
            dir = "Temp/" + fileName;
        } else {
            original = getOriginal(fileName, name, numVersions);
            dir = "Names/" + name + "/Original/" + fileName;
        }

        playFile(controller, progressText, playButton, progressBar, dir, new Media(original));
    }

    /**
     * Given a name, finds the version of an {@code Original} that has
     * a <q>good</q> rating. If there are no <q>good</q> rated
     * versions, picks a random <q>bad</q> version.
     *
     * @param name the name of the {@code Original}.
     * @return the filename of the best version of the {@code Original}.
     */
    public String pickBestOriginal(String name) {
        List<Original> allVersions = _originals.getAllVersions(name);
        List<String> goodFiles = new ArrayList<>();
        String fileName;

        if (allVersions.isEmpty()) {
        	// If there are only singular versions of a name, set that one as the best version
            goodFiles.add(_originals.getFileName(name).get(0));
        } else {
            for (Original original : allVersions) {
                if (_originals.getRating(original).equals("&good&")) {
                    goodFiles.add(original.getFileNameWithVersion());
                }
            }
        }

        if (goodFiles.size() == 1) {
            fileName = goodFiles.get(0);
        } else if (goodFiles.size() > 1) { // Pick a random file if there are multiple "good" files.
            Collections.shuffle(goodFiles);
            fileName = goodFiles.get(0);
        } else {
            Collections.shuffle(allVersions); // Pick any file if they are all "bad" files.
            fileName = allVersions.get(0).getFileNameWithVersion();
        }
        return fileName;
    }

    /**
     * Loads a <q>.fxml</q> file into a new window.
     *
     * @param scene  the name of the <q>.fxml</q> (e.g "MicTest").
     * @param title  the title of the window.
     * @param width  width of the window.
     * @param height height of the window.
     */
    protected void createPopUp(String scene, String title, int width, int height) {
        Parent root;
        try {
            root = FXMLLoader.load(getClass().getResource("/" + scene + ".fxml"));
            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(new Scene(root, width, height));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates an {@code Alert} pop-up with specified variables.
     * Important to note that this method does not handle the interactions
     * of the buttons and that those should be handled separately.
     *
     * @param type        type of the {@code Alert}.
     * @param title       title of the window.
     * @param headerText  main text shown in pop-up window.
     * @param contentText smaller text shown in pop-up window.
     * @param buttons     buttons that are shown.
     * @return a custom {@code Alert} pop-up.
     */
    protected Alert createAlert(Alert.AlertType type, String title, String headerText, String contentText, ButtonType[] buttons) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);

        alert.getButtonTypes().setAll(buttons);

        return alert;
    }

	/**
	 * Creates a default {@code Alert} pop-up, with the buttons being "Yes" and "Cancel".
	 *
	 * @see #createAlert(Alert.AlertType, String, String, String, ButtonType[])
	 */
    protected Alert createAlert(Alert.AlertType type, String title, String headerText, String contentText) {
        _yes = new ButtonType("Yes", ButtonBar.ButtonData.YES);
        ButtonType cancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        return createAlert(type, title, headerText, contentText, new ButtonType[]{_yes, cancel});
    }

    /**
     * Sets the items of a {@code ListView} to be only those that
     * are matching {@code String}'s or sub-strings of the entered
     * text.
     *
     * @param filteredList list containing data to filter from.
     * @param newValue     text to find in filtered list.
     * @param listView     JavaFX component to set the items to.
     *
     */
    protected void searchListener(FilteredList<String> filteredList, String newValue, ListView<String> listView) {
        filteredList.setPredicate(string -> {
            if (newValue == null || newValue.isEmpty()) {
                return true;
            }
            return string.toUpperCase().contains(newValue.toUpperCase());
        });
        listView.setItems(filteredList);
    }

    /**
     * Sets the duration of which the progress bar goes from
     * 0 to 100 to be the length that the audio file it is
     * playing plays for.
     *
     * The {@code Timeline} for the {@code ProgressIndicator} was retrieved from:
     * <a href="https://stackoverflow.com/questions/38773124/how-to-get-javafx-timeline-to-increase-by-second-and-to-bind-to-a-progressbar">
     * https://stackoverflow.com/questions/38773124/how-to-get-javafx-timeline-to-increase-by-second-and-to-bind-to-a-progressbar</a>
     *
     * @param progress the {@code ProgressIndicator} being displayed
     * @param dir      whether it is an {@code Original} or a {@code Challenge}
     *                 (e.g "Original").
     */
    public void showProgress(ProgressIndicator progress, String dir, EventHandler<ActionEvent> event) {
        double duration = 0;
        try {
            File file = new File(dir);
            AudioInputStream ais = AudioSystem.getAudioInputStream(file);
            AudioFormat format = ais.getFormat();

            long frames = ais.getFrameLength();
            duration = (frames + 0.0) / format.getFrameRate();
        } catch (UnsupportedAudioFileException | IOException e) {
            e.printStackTrace();
        }

        // This segment has been referenced in the JavaDocs
        _progressTimeline = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(progress.progressProperty(), 0)),
                new KeyFrame(Duration.seconds(duration), event,
                        new KeyValue(progress.progressProperty(), 1)));
        _progressTimeline.setCycleCount(1);
        _progressTimeline.play();
    }

    protected void stopProgress() {
        _progressTimeline.stop();
        Media.cancel();
    }

    /**
     * Closes the window of the {@code Stage} which the given {@code Pane}
     * is in.
     */
    protected void exit(Pane pane) {
        Stage stage = (Stage) pane.getScene().getWindow();
        stage.close();
    }

	/**
	 * Adjusts the size of the text displaying the selected Name to play.
	 * It uses an algorithm depending on the length of the Name, making it
	 * smaller so that it can fit longer names.
	 *
	 * @param nameLabel the JavaFX element to adjust the size of.
	 * @param name      the selected Name.
	 */
	protected void textSizeHandler(Text nameLabel, String name) {
    	nameLabel.setLayoutY(43);
        int numChars = name.length();
        int fontSize = 26;
        int minSize = 15;
        if (numChars > 15) {
            fontSize = 26 - (numChars - 15);
            if (numChars > 30) {
                minSize = 12;
            }
            if (fontSize <= minSize) {
                fontSize = minSize;
                nameLabel.setLayoutY(35);
            }
        }
        nameLabel.setFont(new Font("DejaVu Sans Bold", fontSize));
    }

	/**
	 * Adjusts the color of the difficulty star to be either yellow,
	 * which is the color of the image, or grey, to indicate that it
	 * has not been selected.
	 *
	 * @param star the image of the star to adjust colors of.
	 * @param on whether or not the star has been selected to be on or not.
	 */
	protected void starOn(ImageView star, boolean on) {
        ColorAdjust color = new ColorAdjust(0, 0, 0, 0);
        if (!on) {
            color.setSaturation(-1);
            color.setContrast(-1);
        }
        star.setEffect(color);
    }

	/**
	 * Captures input from the microphone of the user, and invokes
	 * {@link MicTestController#setMicLevel(float)} after getting the
	 * calculated root mean squared value.
	 *
	 * This code segment was retrieved from:
	 * <a href="https://stackoverflow.com/questions/26574326/how-to-calculate-the-level-amplitude-db-of-audio-signal-in-java">
	 *     https://stackoverflow.com/questions/26574326/how-to-calculate-the-level-amplitude-db-of-audio-signal-in-java</a>
	 *
	 * @param controller
	 */
	protected void micTester(MicTesterController controller) {
        AudioFormat af = new AudioFormat(44100f, 16, 1, true, false);
        TargetDataLine dataLine = null;
        try {
            dataLine = AudioSystem.getTargetDataLine(af);
            dataLine.open(af, 2048);
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }

        byte[] buffer = new byte[2048];
        float[] samples = new float[1024];

        dataLine.start();
        for (int i; (i = dataLine.read(buffer, 0, buffer.length)) > -1; ) {
            for (int j = 0, k = 0; j < i; ) {
                int sample = 0;

                sample |= buffer[j++] & 0xFF;
                sample |= buffer[j++] << 8;

                samples[k++] = sample / 32768f;
            }

            float rms = 0f;
            for (float sample : samples) {
                rms += sample * sample;
            }
            rms = (float) Math.sqrt(rms / samples.length);
            rms = Math.abs(rms);
            controller.setMicLevel(rms); // Do something with the rms in the controller
        }
    }
}

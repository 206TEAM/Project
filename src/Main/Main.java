package Main;

import Model.Originals;
import Ratings.ChallengeRatings;
import Ratings.DifficultyRatings;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.Optional;

public class Main extends Application {
    private Stage primaryStage;

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    @Override
    public void start(Stage primaryStage) {

        try {
            if (Files.exists(Paths.get("Temp"))) {
                Files.walk(Paths.get("Temp")).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
            }
            Files.createDirectory(Paths.get("Temp"));

            if (Files.notExists(Paths.get("Recordings"))) {
            	Files.createDirectory(Paths.get("Recordings"));
            }
            if (Files.notExists(Paths.get("Names"))) {
            	Files.createDirectory(Paths.get("Names"));
            }

            Originals.getInstance().populateFolders();

            Parent root = FXMLLoader.load(getClass().getResource("/MainMenu.fxml"));
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.setTitle("NameSayer");
            primaryStage.setResizable(false);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

        primaryStage.setOnCloseRequest((WindowEvent event) -> {
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
                ChallengeRatings.getInstance().saveSession();
                DifficultyRatings.getInstance().saveSession();
            } else if (result.get() == cancel) {
                event.consume();
                primaryStage.show();
            }
        });
    }


    private Alert createAlert(Alert.AlertType type, String title, String headerText, String contentText, ButtonType[] buttons) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        alert.getButtonTypes().setAll(buttons);
        return alert;
    }

    public static void main(String[] args) {
        launch(args);
    }
}

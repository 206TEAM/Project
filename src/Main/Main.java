package Main;

import Control.ParentController;
import Model.Mediator;
import Model.Originals;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;

public class Main extends Application {
	private Stage primaryStage;

	public Stage getPrimaryStage() {
		return primaryStage;
	}

	@Override
	public void start(Stage primaryStage) {
		Originals.getInstance().populateFolders();
		try {
			if (Files.exists(Paths.get("Temp"))) {
				// Delete All contents of Temp folder
				Files.walk(Paths.get("Temp")).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
			}
			Files.createDirectory(Paths.get("Temp"));
			if (Files.notExists(Paths.get("Recordings"))) {
				Files.createDirectory(Paths.get("Recordings"));
			}
			if (Files.notExists(Paths.get("Names"))) {
				Files.createDirectory(Paths.get("Names"));
			}

			Parent root = FXMLLoader.load(getClass().getResource("/MainMenu.fxml"));
			Scene scene = new Scene(root);
			primaryStage.setScene(scene);
			primaryStage.setResizable(false);
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}

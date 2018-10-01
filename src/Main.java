import Model.Originals;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.nio.file.Files;
import java.nio.file.Paths;

public class Main extends Application {

	@Override
	public void start(Stage primaryStage) {
		Originals.getInstance().populateFolders();
		try {
			if (Files.notExists(Paths.get("Temp"))) {
				Files.createDirectory(Paths.get("Temp"));
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

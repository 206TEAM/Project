package Control;

import Model.Media;
import Model.Originals;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ConcatenateNamesController extends SelectPracticeController {
	@FXML
	public Text nameText;
	@FXML
	public Button addButton;
	@FXML
	public Text conditionsText;
	@FXML
	public TextField newName;
	@FXML
	public Button cancel;

	private List<String> _names;
	private String _newName;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		List<String> allNames = Originals.getInstance().listNames();

		newName.textProperty().addListener((observable, oldValue, newValue) -> {
			String[] names = newValue.split("[ -]");
			List<Character> splits = extractSplits(newValue);// Get all spaces and hyphens only
			StringBuilder confirmed = new StringBuilder();
			_names = new ArrayList<>();

			if (names.length == 1) {
				setNameText("Enter more than 1 name", Color.RED, true);
			} else {
				for (int i = 0; i < names.length; i++) {
					String name = names[i];
					if (!containsName(name, allNames)) {
						setNameText(name + " is not in the database", Color.RED, true);
					} else {
						name = name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
						confirmed.append(name);
						_names.add(name);
						if (i >= splits.size()) {
							confirmed.append(" ");
						} else {
							confirmed.append(splits.get(i));
						}
					}
				}

				String entered = newValue.toUpperCase() + " ";
				if (entered.equals(confirmed.toString().toUpperCase())) {
					_newName = confirmed.toString();
					setNameText(_newName, Color.GREEN, false);
				}
			}
		});
	}

	@FXML
	public void add(ActionEvent actionEvent) {
		try {
			Files.deleteIfExists(Paths.get("Temp/output.wav"));
			Files.deleteIfExists(Paths.get("list.txt"));
			Files.createFile(Paths.get("list.txt"));
			for (String name : _names) {
				String fileName = pickBestOriginal(name);

				String dir = "file 'Names/" + name + "/Original/" + fileName + "'\n";
				Files.write(Paths.get("list.txt"), dir.getBytes(), StandardOpenOption.APPEND);

			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		String newName = _newName.replace(' ', '_');
		Media.concatNames(newName);

		super.getInstance().addValue(_newName);
		exit();
	}

	@FXML
	public void cancel(ActionEvent actionEvent) {
		exit();
	}

	private void exit() {
		Stage stage = (Stage) cancel.getScene().getWindow();
		stage.close();
	}

	private void setNameText(String text, Paint paint, boolean disableAdd) {
		nameText.setText(text);
		nameText.setFill(paint);
		addButton.setDisable(disableAdd);
	}

	private List<Character> extractSplits(String s) {
		char[] temp = s.toCharArray();
		List<Character> output = new ArrayList<>();
		for (char c : temp) {
			if (c == ' ' || c == '-') {
				output.add(c);
			}
		}
		return output;
	}
}

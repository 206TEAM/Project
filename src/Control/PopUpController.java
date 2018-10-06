package Control;

import javafx.stage.Stage;

public abstract class PopUpController extends Controller {
	Stage _stage;

	protected void exit() {
		_stage.close();
	}
}

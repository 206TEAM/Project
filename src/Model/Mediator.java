package Model;

import Control.HeaderController;
import Control.MainMenuController;
import Control.ParentController;
import Control.PracticeMainController;

public class Mediator {

	private final static Mediator SINGLETON = new Mediator();

	private HeaderController _header;
	private MainMenuController _main;
	private PracticeMainController _subMain;

	public void setParent(ParentController parent) {
		if (parent instanceof HeaderController) {
			_header = (HeaderController) parent;
		} else if (parent instanceof MainMenuController) {
			_main = (MainMenuController) parent;
		} else {
			_subMain = (PracticeMainController) parent;
		}
	}

	public void loadPane(ParentController.Type parent, String page) {
		 if (parent == ParentController.Type.HEADER) {
		 	_header.loadPane(page);
		 } else if (parent == ParentController.Type.MAIN) {
		 	_main.loadPane(page);
		 } else if (parent == ParentController.Type.SUB_MAIN) {
		 	_subMain.loadPane(page);
		 }
	}

	public static Mediator getInstance() {
		return SINGLETON;
	}

}

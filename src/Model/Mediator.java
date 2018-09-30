package Model;

import Control.HeaderController;
import Control.MainMenuController;
import Control.ParentController;
import Control.PracticeMainController;

import java.util.ArrayList;
import java.util.List;

public class Mediator {

	private final static Mediator SINGLETON = new Mediator();

	private HeaderController _header;
	private MainMenuController _main;
	private PracticeMainController _subMain;

	private List<String> _practiceMainList;

	/********fields are for challenges********/
	private ArrayList<String> _challengeNames;
	private String _currentName;
	private String _currentFileName;
	private List<String> _challengeList;
	private double _difficulty;

	/********methods for getting/setting challenge related things********/
	public void setChallengeList(List<String> challengeList) {
		_challengeList = challengeList;
	}

	public List<String> getChallengeList() {
		return _challengeList;
	}

	public void removePracticeName(String name){
		_challengeNames.remove(name);
	}

	public void clearCurrentNames() {
		_challengeNames = null;
	}

	public String getCurrentName() {
		return _currentName;
	}

	public void setFileName(String name) {
		_currentFileName = name;
	}

	public String getFileName() {
		return _currentFileName;
	}

	public void setCurrentName(String name) {
		_currentName = name;
	}

	public void addNames(List<String> names) {
		if (_challengeNames == null) {
			_challengeNames = new ArrayList();
			_challengeNames.addAll(names);
		} else {
			for (String name : names) {
				if (!_challengeNames.contains(name)) {
					_challengeNames.add(name);
				}
			}
		}
	}

	public List<String> getChallengeNames() {
		return _challengeNames;
	}

	/*********Methods for setting scenes**********/

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

	public void setPracticeMainList(List<String> list) {
		_practiceMainList = list;
	}

	public List<String> getPracticeMainList() {
		return _practiceMainList;
	}

	public static Mediator getInstance() {
		return SINGLETON;
	}

}

package Model;

import Control.*;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.ProgressIndicator;
import javafx.util.Duration;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Mediator {

    private final static Mediator SINGLETON = new Mediator();

    private HeaderController _header;
    private MainMenuController _main;
    private ChallengeCompareController _subMain;
    private PracticeMainController _practiceMain;

    private List<String> _practiceMainList;

    /********fields are for challenges********/
    private ChallengeSession _session;

	private ParentController.PageType _currentPage;
    //private ArrayList<String> _challengeNames;
    private String _currentName;
    private String _currentFileName;
    private String _originalFileName;
    private Practice _currentPractice;
    private int _numVersions;
    private List<String> _challengeList;
    private List<String> _challengeFileList;

	public void addObserver(Observer o) {
		_practiceMain.addObserver(o);
	}

	/********method for getting session object********/
    public void setChallengeSession(ChallengeSession session){
        _session = session;
    }

    public ChallengeSession getChallengeSession(){
        return _session;
    }

    /********methods for getting/setting challenge related things********/
    public void setChallengeList(List<String> challengeList) {
        _challengeList = challengeList;
    }

    public void addChallengeFile(String fileName) {
        if (_challengeFileList == null) {
            _challengeFileList = new ArrayList();
            _challengeFileList.add(fileName);
        } else {
            _challengeFileList.add(fileName);
        }
    }

    public List<String> getChallengeFiles() {
        return _challengeFileList;
    }

    public List<String> getChallengeList() {
        return _challengeList;
    }

    public void removePracticeName(String name) {
        // _challengeNames.remove(name);
    }

    public void clearCurrentNames() {
        //  _challengeNames = null;
    }

    public String getCurrentName() {
        return _currentName;
    }

    public void setCurrentName(String name) {
        _currentName = name;
    }

    public void setCurrentPractice(Practice practice) {
    	_currentPractice = practice;
    }

    public Practice getCurrentPractice() {
    	return _currentPractice;
    }

    public String getOriginalFilename(){
        return _originalFileName;
    }

    public void setOriginalFilename(String fileName){
        _originalFileName = fileName;
    }

    public void setNumVersions(int numVersions) {
    	_numVersions = numVersions;
    }

    public int getNumVersions() {
    	return _numVersions;
    }

    public void setCurrent(String name, String fileName, int numVersions, Practice practice) {
    	setCurrentName(name);
    	setOriginalFilename(fileName);
    	setNumVersions(numVersions);
    	setCurrentPractice(practice);
    }

    public String getChallengeFile(String name){
        int index = 0;
        for (int i=0;i<_challengeList.size();i++){
            if (_challengeList.get(i).equals(name)){
                index = i;
            }
        }
        return _challengeFileList.get(index);
    }

    /*********Methods for setting scenes**********/
    public void setParent(HeaderController parent) {
    	_header = parent;
    }

    public void setParent(MainMenuController parent) {
    	_main = parent;
    }

    public void setParent(ChallengeCompareController parent) {
    	_subMain = parent;
    }

    public void setParent(PracticeMainController parent) {
    	_practiceMain = parent;
    }

    public void setPageType(ParentController.PageType type) {
    	_currentPage = type;
    }

    public void loadPane(ParentController.Type parent, String page) {
        if (parent == ParentController.Type.HEADER) {
            _header.loadPane(page);
            _header.setPage(_currentPage);
        } else if (parent == ParentController.Type.MAIN) {
            _main.loadPane(page);
        } else if (parent == ParentController.Type.SUB_MAIN) {
            _subMain.loadPane(page);
        } else if (parent == ParentController.Type.PRACTICE) {
        	_practiceMain.loadPane(page);
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

	public void fireDisableTable(PracticeMainController.TableType type1, PracticeMainController.TableType type2, boolean disable) {
		fireDisableTable(type1, disable);
		fireDisableTable(type2, disable);
	}

	/**
	 * Disables a table in {@link PracticeMainController} so that
	 * the user cannot change their selection.
	 *
	 * @param type which table to disable/enable
	 * @param disable if {@code true}, then disables the table;
	 *                otherwise, enables the table.
	 */
	public void fireDisableTable(PracticeMainController.TableType type, boolean disable) {
		_practiceMain.setDisableTables(type, disable);
	}

	public void fireTableValues(List<String> newTable) {
		_practiceMain.setTableValues(newTable);
	}

	public boolean praticeNotNull() {
		return _practiceMain != null;
	}

}

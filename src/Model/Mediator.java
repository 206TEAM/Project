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
import java.util.HashMap;
import java.util.List;

public class Mediator {

    private final static Mediator SINGLETON = new Mediator();

    private HeaderController _header;
    private MainMenuController _main;
    private PracticeMainController _subMain;

    private List<String> _practiceMainList;

    /********fields are for challenges********/
    //private ArrayList<String> _challengeNames;
    private String _currentName;
    private String _currentFileName;
    private String _originalFileName;
    private int _numVersions;
    private List<String> _challengeList;
    private List<String> _challengeFileList;
    private double _difficulty;

    public void addObserver(Observer o) {
    	_subMain.addObserver(o);
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

    public String getOriginalFilename(){
        return _originalFileName;
    }

    public void setOriginalFilename(String fileName){
        _originalFileName = fileName;
    }

    public void setNumVersions(int numVersions) {
    	_numVersions = numVersions;
    }

    public void setCurrent(String name, String fileName, int numVersions) {
    	setCurrentName(name);
    	setOriginalFilename(fileName);
    	setNumVersions(numVersions);
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

	/**
	 * Sets the duration of which the progress bar goes from
	 * 0 to 100 to be the length that the audio file it is
	 * playing plays for.
	 *
	 * @param progress the {@code ProgressIndicator} being displayed
	 * @param dir whether it is an {@code Original} or a {@code Practice}
	 * @param fileName name of the file being played
	 */
	public void showProgress(ProgressIndicator progress, String dir, String fileName, EventHandler<ActionEvent> event) {
		double duration = 0;
		try {
			File file = new File("Names/" + _currentName + "/" + dir +  "/" + fileName);
			AudioInputStream ais = AudioSystem.getAudioInputStream(file);
			AudioFormat format = ais.getFormat();

			long frames = ais.getFrameLength();
			duration = (frames+0.0) / format.getFrameRate();
		} catch (UnsupportedAudioFileException | IOException e) {
			e.printStackTrace();
		}

		Timeline timeLine = new Timeline(
				new KeyFrame(Duration.ZERO, new KeyValue(progress.progressProperty(), 0)),
				new KeyFrame(Duration.seconds(duration), event,
						new KeyValue(progress.progressProperty(), 1)));
		timeLine.setCycleCount(1);
		timeLine.play();
	}

	/**
	 * Shows progress without triggering an event occurring after it is finished.
	 *
	 * @see #showProgress(ProgressIndicator, String, String, EventHandler)
	 */
	public void showProgress(ProgressIndicator progress, String dir, String fileName) {
		EventHandler<ActionEvent> event = Event::consume; //do nothing
		showProgress(progress,dir,fileName,event);
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
		_subMain.setDisableTables(type, disable);
	}

}

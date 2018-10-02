package Model;

import Control.HeaderController;
import Control.MainMenuController;
import Control.ParentController;
import Control.PracticeMainController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Mediator {

    private final static Mediator SINGLETON = new Mediator();

    private HeaderController _header;
    private MainMenuController _main;
    private ParentController _subMain;

    private List<String> _practiceMainList;

    /********fields are for challenges********/
    private ChallengeSession _session;


    //private ArrayList<String> _challengeNames;
    private String _currentName;
    private String _currentFileName;
    private String _originalFileName;
    private List<String> _challengeList;
    private List<String> _challengeFileList;

    /********method for getting session object********/
    public void setChallengeSession(ChallengeSession session){
        _session = session;
    }

    public ChallengeSession getChallengeSession(){
        return _session;
    }

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

    public void setOriginalFilename(String name){
        _originalFileName = name;
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
            _subMain = parent;
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

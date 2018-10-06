package Model;

import Ratings.ChallengeRatings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ChallengeSession {
    private double _difficulty;
    private int _numberOfNames;

    //private ArrayList<String> _challengeNames;
    private String _currentName;
    private String _currentFileName;
    private String _originalFileName;
    private List<String> _challengeList;
    private List<String> _challengeFileList;
    private List<String> _goodList;
    private List<String> _badList;


    public ChallengeSession(int number, double difficulty) {
        _numberOfNames = number;
        _difficulty = difficulty;
        generateNames();
    }

    private void generateNames() {
        List<String> challengeList = Originals.getInstance().listNames();
        Collections.shuffle(challengeList);
        setChallengeList(challengeList.subList(0, _numberOfNames));
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

    public List<String> getGoodList() {
        return _goodList;
    }

    public List<String> getBadList() {
        return _badList;
    }

    public String getCurrentName() {
        return _currentName;
    }

    public void setCurrentName(String name) {
        _currentName = name;
    }

    public String getChallengeFile(String name) {
        int index = 0;
        for (int i = 0; i < _challengeList.size(); i++) {
            if (_challengeList.get(i).equals(name)) {
                index = i;
            }
        }
        return _challengeFileList.get(index);
    }

    /**
     * this gets rid of all the challenge files
     */
    public void abortSession() {
       //todo: kill processes
        if (_challengeFileList != null && !_challengeFileList.isEmpty()) {
            for (int i = 0; i < _challengeFileList.size(); i++) {
                String name = _challengeList.get(i);
                if (name != null) {

                    System.out.println("to be deleted: " + _challengeFileList.get(i));
                    Challenges.getInstance().deleteChallenge(name, _challengeFileList.get(i));

                }
            }
        }
    }

    public void getLists() {
        _goodList = new ArrayList<String>();
        _badList = new ArrayList<String>();
        for (int i = 0; i < _challengeList.size(); i++) {
            String name = _challengeList.get(i);
            String fileName = _challengeFileList.get(i);

            if (ChallengeRatings.getInstance().getRating(name, fileName)) {
                _goodList.add(name);
            } else {
                _badList.add(name);
            }
        }
    }
}

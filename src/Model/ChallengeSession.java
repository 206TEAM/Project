package Model;

import Ratings.ChallengeRatings;
import Ratings.DifficultyRatings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ChallengeSession {
    private int _difficulty;
    private int _numberOfNames;
    private String _currentName;
    private List<String> _challengeList;
    private List<String> _challengeFileList;
    private List<String> _goodList;
    private List<String> _badList;
    private Control.ChallengePlayController _controller;

    //todo temp messages
    public static final String GOODMESSAGE = "Good job!";
    public static final String BADMESSAGE = "Keep trying. You're almost there.";

    public ChallengeSession(int number, int difficulty) {
        _numberOfNames = number;
        _difficulty = difficulty;
        generateNames();
    }

    public ChallengeSession(List<String> oldList) { //constructor for redoing
        _numberOfNames = oldList.size();
        _difficulty = 0; //todo hmm
        setChallengeList(oldList);
    }

    private void generateNames() {

        List<String> names = DifficultyRatings.getInstance().generateList(_difficulty, _numberOfNames);
        System.out.println("\n the generated names are \n");
        System.out.println(names);

        setChallengeList(names);
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

    /**
     * this session returns the score
     */
    public int getSessionScore() {
        double ratio = (_goodList.size() / (double) _numberOfNames) * 100;
        return (int) ratio;

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
        Mediator.getInstance().removeInChallengeSession();
        if (_challengeFileList != null || !_challengeFileList.isEmpty()) {
            for (int i = 0; i < _challengeFileList.size(); i++) {
                String name = _challengeList.get(i);
                if (name != null) {
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

    public String getScoreMessage() {
        int score = getSessionScore();
        if (score >= ChallengeRatings.SCORELIMIT) { //currently 60% is good
            return GOODMESSAGE;
        } else {
            return BADMESSAGE;
        }
    }
}

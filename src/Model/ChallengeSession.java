package Model;

import Ratings.ChallengeRatings;
import Ratings.DifficultyRatings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * this class represents a challenge session.
 * it includes methods such as getting/setting challenge lists, difficulty, number of names etc
 *
 * @author Lucy Chen
 */
public class ChallengeSession {
    private int _difficulty;
    private int _numberOfNames;
    private String _currentName;
    private List<String> _challengeList;
    private List<String> _challengeFileList;
    private List<String> _goodList;
    private List<String> _badList;
    private Control.ChallengePlayController _controller;

    public static final String GOODMESSAGE = "Good job!";
    public static final String BADMESSAGE = "Keep trying. You're almost there.";

    public ChallengeSession(int number, int difficulty) {
        _numberOfNames = number;
        _difficulty = difficulty;
        generateNames();
    }

    /**
     * constructor for creating a challengesession from an existing list of names
     *
     * @param oldList
     */
    public ChallengeSession(List<String> oldList) {
        _numberOfNames = oldList.size();
        _difficulty = 1;
        setChallengeList(oldList);
    }

    /**
     * this method generates names for challenge session based on difficulty and number of names
     */
    private void generateNames() {
        List<String> names = DifficultyRatings.getInstance().generateList(_difficulty, _numberOfNames);
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
     * this method returns the session score based on what the user rated each attempt
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

    /**
     * this method gets the file of the challenge attempt based on the name
     */
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
     * this method sorts the names of the session into good and bad lists based on the
     * rating the user gives the challenge.
     */
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

    /**
     * this method returns the score message based on the score of the session.
     *
     * @return
     */
    public String getScoreMessage() {
        int score = getSessionScore();
        if (score >= ChallengeRatings.SCORELIMIT) { //currently >60% is good
            return GOODMESSAGE;
        } else {
            return BADMESSAGE;
        }
    }

    /**
     * this method gets rid of all the challenge files and removes from challenge list
     */
    public void abortSession() {
        //todo: kill processes
        Mediator.getInstance().removeInChallengeSession();
        try {
            if (_challengeFileList!=null || _challengeFileList!=null) {
                for (int i = 0; i < _challengeFileList.size(); i++) {
                    String name = _challengeList.get(i);
                    if (name != null) {
                        Challenges.getInstance().deleteChallenge(name, _challengeFileList.get(i)); //removes challenge file and from the model
                    }
                }

            }
        } catch (Exception e) {
            System.out.println("error");
        }
    }
}


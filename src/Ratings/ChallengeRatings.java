package Ratings;

import Model.Challenge;
import Model.Challenges;
import Model.Original;
import Model.Originals;
import Save.Saving;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class represents a list of challenges for each Name
 * _challengesRating is a hashmap that contains the Name as the key value, and challenge list for each name as the value.
 */

/**
 * -1 (notAttempted)
 * 0 to 60 (badNames)
 * 60 to 100 (goodNames)
 */
public class ChallengeRatings extends Saving {

    private HashMap<String, Integer> _challengesRating; //rating for each name.
    private List<String> _goodNames;
    private List<String> _badNames;
    private List<String> _notAttemptedNames;
    private int _noOfSessions;
    private int _overallScore;
    private int _progress;
    public static final int SCORELIMIT = 60;
    public static final String SESSIONFILE = "NameRatings.txt";

    private final static ChallengeRatings instance = new ChallengeRatings();

    private ChallengeRatings() {
        createTextFile(SESSIONFILE);
        _challengesRating = new HashMap<String, Integer>();
        _goodNames = new ArrayList<String>();
        _notAttemptedNames = new ArrayList<String>();
        _badNames = new ArrayList<String>();
        _noOfSessions = 0;
        _overallScore = 0;
        _progress = 0;


        List<String> names = Originals.getInstance().listNames();

        for (String name : names) {
            _challengesRating.put(name, -1); //adding names
            _notAttemptedNames.add(name);
        }
        updateModel();

    }

    public static ChallengeRatings getInstance() {
        return instance;
    }

    public int getOverallScore() {
        return _overallScore;
    }

    public List<String> get_badNames() {
        return _badNames;
    }

    public List<String> get_goodNames() {
        return _goodNames;
    }

    public List<String> get_notAttempedNames() {
        return _notAttemptedNames;
    }

    /**
     * sets the rating for specific challenge AS WELL AS updating the average of the name.
     */
    public void setRating(String nameKey, String fileName, Boolean rating) {
        Challenge challenge = Challenges.getInstance().getChallenge(nameKey, fileName);
        challenge.setRating(rating);
        updateAverage(nameKey, rating);
    }

    public Boolean getRating(String nameKey, String fileName) {
        Challenge challenge = Challenges.getInstance().getChallenge(nameKey, fileName);
        return challenge.getRating();
    }

    /**
     * This method takes the new rating
     * calculates the score for the particular namekey, adds value to hashmap
     * and puts the name into the correct list through helper methods.
     */
    private void updateAverage(String nameKey, Boolean rating) {
        //todo make rating more distinguashable e.g 0 and 100?
        int number;
        if (rating) {
            number = 100;
        } else {
            number = 0;
        }

        int currentAvg, newAvg;

        if (_challengesRating.get(nameKey) == -1) { //todo find etter way to distinguish between NA and bad
            newAvg = number;
            currentAvg = -1;
        } else {
            currentAvg = _challengesRating.get(nameKey);
            int challengeSize = Challenges.getInstance().getChallengeSize(nameKey);
            newAvg = currentAvg + (number - currentAvg) / challengeSize;
        }
        _challengesRating.put(nameKey, newAvg); //puts new average into hashmap

        int challengeSize = Challenges.getInstance().getChallengeSize(nameKey);
        if (newAvg<50 && challengeSize>2){ //todo change threshold
            DifficultyRatings.getInstance().setDifficult(nameKey);
        } else if (newAvg>90 && challengeSize > 3){
            DifficultyRatings.getInstance().setEasy(nameKey);
        }
        handleList(currentAvg, newAvg, nameKey); //updates the list
    }

    private void handleList(int oldScore, int newScore, String nameKey) {
        if (getList(newScore) != getList(oldScore)) {
            if (getList(oldScore) != null) {
                getList(oldScore).remove(nameKey);
            } else {
                _notAttemptedNames.remove(nameKey);
            }
            getList(newScore).add(nameKey);
        }
    }

    private List<String> getList(int average) {
        if (average == -1) {
            return null;
        } else if (average < SCORELIMIT) {
            return _badNames;
        } else {
            return _goodNames;
        }
    }

    /**
     * updates progress and average score each time after a session has finished
     */
    public void newSession(int score) {
        _noOfSessions++;
        int newAvg = _overallScore + (score - _overallScore) / _noOfSessions;
        double newProgress = 100 - ((_notAttemptedNames.size() / (double) Originals.getInstance().listNames().size()) * 100); //todo make better
        _progress = (int) newProgress;
        _overallScore = newAvg;
    }

    public int getProgress() {
        return _progress;
    }

    /**
     * score of a particular name.
     */
    public int getScore(String nameKey) {
        return _challengesRating.get(nameKey);
    }

    /**
     * This method reads from saved session text file and updates the hashmap
     * AND the lists
     */
    private void updateModel() {
        try {
            _noOfSessions = Integer.parseInt(getSaved(SESSIONFILE, "_noOfSessions"));
            _overallScore = Integer.parseInt(getSaved(SESSIONFILE, "_overallScore"));
            _progress = Integer.parseInt(getSaved(SESSIONFILE, "_progress"));

            _goodNames = getList(SESSIONFILE, "_goodNames");
            _badNames = getList(SESSIONFILE, "_badNames");

            System.out.println(_goodNames.size());

            for (String name : _goodNames) {
                updateAverage(name, true);
            }

            for (String name : _badNames) {
                updateAverage(name, false);
            }
        } catch (Exception e) {
            //todo
        }



    }

    public void saveSession() {

        List<String> params = new ArrayList<String>();
        params.add("_noOfSessions &" + _noOfSessions + "&");
        params.add("_overallScore &" + _overallScore + "&");
        params.add("_progress &" + _progress + "&");

        int i = 0;
        for (String name : _goodNames) {
            params.add("_goodNames &" + _goodNames.get(i) + "&");
            i++;
        }
        i = 0;
        for (String name : _badNames) {
            params.add("_badNames &" + _badNames.get(i) + "&");
            i++;
        }
        saveSession(SESSIONFILE, params);
    }


}




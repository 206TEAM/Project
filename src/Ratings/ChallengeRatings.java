package Ratings;

import Model.*;
import Save.Saving;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * This class deals with the ratings of challenges and implements some algorithms that dictate the score of the
 * users progress based on the challenge results
 *
 * @author Lucy Chen
 */

/**
 * These values dictates the scores:
 * -1 (notAttempted)
 * 0 to 59 (badNames)
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
    public static final int SCORELIMIT = 60; //note that -1 is notattempted, 0 to 60 is badNames and 60 to 100 is goodNames
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

        /**
         * adds all of the names into the not attempted list when initialising
         */
        for (String name : names) {
            _challengesRating.put(name, -1);
            _notAttemptedNames.add(name);
        }
        updateModel();
    }

    public static ChallengeRatings getInstance() {
        return instance;
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
     * This method reads from saved session text file and updates the fields when this class is initialised.
     */
    private void updateModel() {
        try {
            _noOfSessions = Integer.parseInt(getSaved(SESSIONFILE, "_noOfSessions"));
            _overallScore = Integer.parseInt(getSaved(SESSIONFILE, "_overallScore"));
            _progress = Integer.parseInt(getSaved(SESSIONFILE, "_progress"));
            _goodNames = getList(SESSIONFILE, "_goodNames");
            _badNames = getList(SESSIONFILE, "_badNames");

            for (String name : _goodNames) {
                updateAverage(name, true);
            }
            for (String name : _badNames) {
                updateAverage(name, false);
            }

        } catch (Exception e) { //purposefully tries to catch the number format exception, doesn't do anything
        }
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

    /**
     * returns the rating for challenge given name and fileName
     * @param nameKey the name of the rating to retrieve
     * @param fileName filename of the particular challenge
     * @return
     */
    public Boolean getRating(String nameKey, String fileName) {
        Challenge challenge = Challenges.getInstance().getChallenge(nameKey, fileName);
        return challenge.getRating();
    }

    /**
     * This method takes the new rating and
     * calculates the score for the particular namekey, adds value to hashmap
     * and puts the name into the correct list through helper methods.
     */
    private void updateAverage(String nameKey, Boolean rating) {
        int number;
        if (rating) {
            number = 100;
        } else {
            number = 0;
        }

        int currentAvg, newAvg;

        if (_challengesRating.get(nameKey) == -1) { //if the current rating is "not attempted"
            newAvg = number;
            currentAvg = -1; //take the current average
        } else {
            currentAvg = _challengesRating.get(nameKey);
            int challengeSize = Challenges.getInstance().getChallengeSize(nameKey); //gets the number of challenges for the nameKey
            newAvg = currentAvg + (number - currentAvg) / challengeSize;
        }
        _challengesRating.put(nameKey, newAvg); //puts new average into hashmap
        handleList(currentAvg, newAvg, nameKey); //updates the list
    }

    /**
     * this is a helper method that puts the name into the right list based on their old and new score
     * @param oldScore of score of the name
     * @param newScore new updated score of the name
     * @param nameKey name where the score is updated
     */
    private void handleList(int oldScore, int newScore, String nameKey) {
        if (getList(newScore) != getList(oldScore)) {
            if (getList(oldScore) != null) {
                getList(oldScore).remove(nameKey);
            } else {
                _notAttemptedNames.remove(nameKey);
            }
            if (getList(newScore)!=null){
                getList(newScore).add(nameKey); //adds the new name to the corresponding list with the correct score
            }
        }
    }

    /**
     * helper method that returns the list based on the average score of the name
     * can be _badNames or _goodNames (based on scorelimit).
     * @param average
     * @return
     */
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
        //calculates the new progress by findind the average (incrementally from the last)
        double newProgress = 100 - ((_notAttemptedNames.size() / (double) Originals.getInstance().listNames().size()) * 100);
        _progress = (int) newProgress;
        _overallScore = newAvg;
    }

    /**
     * public method that writes all the fields and values to a text file
     */
    public void saveSession() {
        List<String> params = new ArrayList<String>();
        params.add("_noOfSessions &" + _noOfSessions + "&");
        params.add("_overallScore &" + _overallScore + "&");
        params.add("_progress &" + _progress + "&");

        for (int i = 0;i<_goodNames.size();i++){
            params.add("_goodNames &" + _goodNames.get(i) + "&");
        }

        for (int i = 0;i<_badNames.size();i++){
            params.add("_badNames &" + _badNames.get(i) + "&");
        }

        saveSession(SESSIONFILE, params); //writes the params to the text file.
    }

    /**
     * this method resets all the fields and variables to the default settings
     */
    public void reset() {
        _noOfSessions = 0;
        _overallScore = 0;
        _progress = 0;

        _goodNames.clear();
        _badNames.clear();
        _notAttemptedNames.clear();

        List<String> names = Originals.getInstance().listNames();
        for (String name : names) {
            _challengesRating.put(name, -1); //adding names
            _notAttemptedNames.add(name);
        }
        deleteTextFile(SESSIONFILE);
    }
}
package Ratings;

import Model.Challenge;
import Model.Challenges;
import Model.Originals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * This class represents a list of challenges for each Name
 * _challengesRating is a hashmap that contains the Name as the key value, and challenge list for each name as the value.
 *
 */
public class ChallengeRatings {

    private HashMap<String, Integer> _challengesRating; //rating for each name.
    private List<String> _goodNames;
    private List<String> _badNames;
    private List<String> _notAttempedNames;

    private final static ChallengeRatings instance = new ChallengeRatings();

    private ChallengeRatings() {
        _challengesRating = new HashMap<String, Integer>();
        _goodNames = new ArrayList<String>();
        _notAttempedNames = new ArrayList<String>();
        _badNames = new ArrayList<String>();
        List<String> names = Originals.getInstance().listNames();

        for (String name : names){
            _challengesRating.put(name,null); //adding names
            _notAttempedNames.add(name);

        }
        updateModel();
    }

    public static ChallengeRatings getInstance() {
        return instance;
    }

    /**
     * sets the rating for specific challenge AS WELL AS updating the average of the name.
     */
    public void setRating(String nameKey, String fileName, Boolean rating){
        Challenge challenge = Challenges.getInstance().getChallenge(nameKey, fileName);
        challenge.setRating(rating);
        updateAverage(nameKey, rating);
    }

    public Boolean getRating(String nameKey, String fileName){
        Challenge challenge = Challenges.getInstance().getChallenge(nameKey, fileName);
        return challenge.getRating();
    }

    private void updateAverage(String nameKey, Boolean rating){
        //todo make rating more distinguashable e.g 0 and 100?
        int number;
        if (rating){
            number = 100;
        } else {
            number = 0;
        }
        int newAvg;
        if (_challengesRating.get(nameKey)==null){
            newAvg = number;
        } else {
            int currentAvg = _challengesRating.get(nameKey); //is there a better way than getting / setting lol
           int challengeSize = Challenges.getInstance().getChallengeSize(nameKey);
           newAvg = currentAvg + (number - currentAvg)/challengeSize;
        }
        _challengesRating.put(nameKey,newAvg); //puts new average
    }

    private void updateLists(String nameKey, int newAvg){ //todo find more efficient way of updating list
    }

    /**
     * This method reads from saved session text file and updates the hashmap
     * AND the lists
     */
    private void updateModel(){
    }

}




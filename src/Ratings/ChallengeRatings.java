package Model;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * This class represents a list of challenges for each Name
 * _challengesRating is a hashmap that contains the Name as the key value, and challenge list for each name as the value.
 *
 */
public class ChallengeRatings {

    private HashMap<String, Boolean> _challengesRating;

    private final static ChallengeRatings instance = new ChallengeRatings();

    private ChallengeRatings() {
        _challengesRating = new HashMap<String, Boolean>();
        //updateModel();
    }

    public static ChallengeRatings getInstance() {
        return instance;
    }

    public void setRating(String nameKey, String fileName, Boolean rating){
        Challenge challenge = Challenges.getInstance().getChallenge(nameKey, fileName);
        challenge.setRating(rating);
    }

    public Boolean getRating(String nameKey, String fileName){
        Challenge challenge = Challenges.getInstance().getChallenge(nameKey, fileName);
        return challenge.getRating();
    }



}





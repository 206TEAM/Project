package Ratings;

import Model.Challenge;
import Model.Challenges;
import Model.Originals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * This class represents difficulty in names
 * _nameRatings is a hashmap that contains the Name as the key value, and challenge list for each name as the value.
 */

/**
 * -1 (notAttempted)
 * 0 to 60 (badNames)
 * 60 to 100 (goodNames)
 */
public class DifficultyRatings {

    private HashMap<String, Boolean> _nameRatings; //difficulty rating for each name
    private List<String> _hardNames;
    private List<String> _defaultNames;

    private final static DifficultyRatings instance = new DifficultyRatings();

    /**
     * creates a model for the difficulty ratings of the names
     */
    private DifficultyRatings() {
        _nameRatings = new HashMap<String, Boolean>();
        _hardNames = new ArrayList<String>();
        _defaultNames = new ArrayList<String>();
        List<String> names = Originals.getInstance().listNames();

        for (String name : names) {
            _nameRatings.put(name, false); //adding names
            _defaultNames.add(name);
        }
        updateModel();
    }

    public static DifficultyRatings getInstance() {
        return instance;
    }

    public List<String> gethardNames() {
        return _hardNames;
    }

    public List<String> getDefaultNames() {
        return _defaultNames;
    }

    /**
     * This method updates the difficulty rating of a name.
     * it also updates the lists
     */
    public void setRating(String nameKey, Boolean difficulty) {
        _nameRatings.put(nameKey, difficulty);
        if (!difficulty) {
            if (_hardNames.contains(nameKey)) {
                _hardNames.remove(nameKey);
            }
            _defaultNames.add(nameKey);
        } else {
            if (_defaultNames.contains(nameKey)) {
                _defaultNames.remove(nameKey);
            }
            _hardNames.add(nameKey);
        }
    }

    /**
     * gets rating for a particular name
     */
    public Boolean getRating(String nameKey) {
        return _nameRatings.get(nameKey);
    }

    /**
     * This method reads from saved session text file and updates the hashmap
     * AND the lists
     */
    private void updateModel() { //todo
    }

    /**
     * this method returns a list of names based on the difficulty selected by user.
     * @return
     */
    public List<String> getNames(Boolean difficult){ //todo

        return null;
    }

}




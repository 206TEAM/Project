package Ratings;
import Model.Originals;
import Save.Saving;

import java.util.*;

/**
 * This class represents difficulty in names
 * _nameRatings is a hashmap that contains the Name as the key value, and challenge list for each name as the value.
 */
public class DifficultyRatings extends Saving {

    private HashMap<String, Boolean> _nameRatings; //legit difficulty rating for each name

    private List<String> _easyNames;
    private List<String> _mediumNames;
    private List<String> _hardNames;
    private List<String> _ascending;

    /**
     * creates a model for the difficulty ratings of the names
     */
    private DifficultyRatings() {
        _nameRatings = new HashMap<String, Boolean>();
        _easyNames = new ArrayList<String>();
        _mediumNames = new ArrayList<String>();
        _hardNames = new ArrayList<String>();

        List<String> names = Originals.getInstance().listNames();
        _ascending = sortNameOrder(names, true);
        sortNames();

        for (String name : names) {
            _nameRatings.put(name, false); //adding names
        }
        updateModel();
    }

    public static DifficultyRatings getInstance() {
        return instance;
    }

    /**
     * this method sorts the names into their respective lists (easy, medium and hard)
     * It divides up the name list into 3 sections based on the length of the names.
     * (longer names are harder, and shorter names are easier).
     */
    private void sortNames() {
        int listSize = _ascending.size();
        if (listSize == 0) {

        } else if (listSize == 1 || listSize == 2) {
            for (String name : _ascending) {
                _mediumNames.add(name);
            }
        } else {
            int slice = (int) Math.floor(listSize / 3);
            for (int i = 0; i < slice; i++) {
                _easyNames.add((_ascending.get(i)));
            }
            for (int i = slice; i < slice * 2; i++) {
                _mediumNames.add((_ascending.get(i)));
            }
            for (int i = slice * 2; i < listSize; i++) {
                _hardNames.add((_ascending.get(i)));
            }
        }
    }

    /**
     * This method sorts a list of strings by their string length. can be in ascending or descending order.
     * returns sorted list.
     */
    private List<String> sortNameOrder(List<String> names, Boolean ascending) {
        Comparator c = new Comparator<String>() {
            public int compare(String s1, String s2) {
                if (!ascending) {
                    return Integer.compare(s2.length(), s1.length());
                } else {
                    return Integer.compare(s1.length(), s2.length());
                }
            }
        };
        Collections.sort(names, c);
        return names;
    }

    private final static DifficultyRatings instance = new DifficultyRatings();

    /**
     * This method updates the difficulty rating of a name.
     * it also updates the lists (easy, medium, hard).
     */
    public void setRating(String nameKey, Boolean difficulty) {
        _nameRatings.put(nameKey, difficulty);
        updateDifficultyList(nameKey, difficulty);
    }

    private void updateDifficultyList(String nameKey, Boolean difficulty){
        if (!difficulty) { //if user set rating to be easy
            if (_hardNames.contains(nameKey)) {
                _hardNames.remove(nameKey);
            }
            _easyNames.add(nameKey); //todo should it be easy?
        } else { //user set rating of name to be hard
            if (_easyNames.contains(nameKey)) {
                _easyNames.remove(nameKey);
                _hardNames.add(nameKey);
            } else if (_mediumNames.contains(nameKey)) {
                _mediumNames.remove(nameKey);
                _hardNames.add(nameKey);
            }
        }
    }

    public void setDifficult(String nameKey){
        updateDifficultyList(nameKey, true);
    }

    public void setEasy(String nameKey){
        updateDifficultyList(nameKey, false);
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
        try {

            List<String> list = getList(ChallengeRatings.SESSIONFILE, "difficultNames");
            for (String name : list){
                _nameRatings.put(name,true);
                setRating(name, true);
            }

        } catch (Exception e) {
            //todo
        }
    }

    /**
     * This method generates a challenge list based on the difficulty of the session and the
     * number of names required for that session.
     */
    public List<String> generateList(int difficultySession, int number) {
        if (number > _ascending.size()) { //if number is too big
            return _ascending;
        }

        switch (difficultySession) {
            case 1:
                return getDiffList(_easyNames, _mediumNames, _hardNames, number, true, false);
            case 2:
                return getDiffList(_easyNames, _mediumNames, _hardNames, number, false, true);
            case 3:
                return getDiffList(_mediumNames, _hardNames, _easyNames, number, false, false);
            case 4:
                return getDiffList(_hardNames, _mediumNames, _easyNames, number, false, true);
            case 5:
                return getDiffList(_hardNames, _mediumNames, _easyNames, number, true, false);
        }
        return null;
    }

    /**
     * this method returns the list of names based on several conditions.
     * if difficulty is 1 or 5 (extreme cases), then it takes as much of the easy/hard lists for the challenge
     * it then takes as much medium names to fill up, and then finally easy names, on the condition that the number
     * of names do not exceed the required number.
     * if difficulty is 2 or 4 (middle cases), it uses 40% for easy/hard, a further 40% for medium and the remainder 20%
     * for hard/easy. if names run out, it then takes a random portion of the remaining names left to get to the required
     * number of names.
     * if difficulty is 3, it uses 50% for medium names, and 25% for easy and hard to ensure for a balance of names.
     * @param first: dominant list of names (if difficulty is 1,2), then it would be easy
     * @param second middle for difficulty levels 1,2,4,5
     * @param third recessive list of names
     * @param number number of challenge names required
     * @param extreme true for difficulty 1,5
     * @param middle true for difficulty 2,4
     */
    private List<String> getDiffList(List<String> first, List<String> second, List<String> third, int number, Boolean extreme, Boolean middle) {
        List<String> challengeList = new ArrayList<String>();
        List<String> remaining = _ascending;
        Collections.shuffle(first);
        Collections.shuffle(second);
        Collections.shuffle(third);
        Collections.shuffle(remaining);

        int firstIndex;
        int secondIndex;

        if (middle) {
            firstIndex = (int) Math.ceil(number * 0.4);
            secondIndex = (int) Math.floor(number * 0.2);
        } else if (extreme) {
            firstIndex = number;
            secondIndex = second.size();
        } else {
            firstIndex = (int) Math.ceil(number * 0.5);
            secondIndex = (int) Math.floor(number * 0.25);
        }

        int i = 0;
        while (i < firstIndex && i < first.size()) {
            challengeList.add(first.get(i)); //gets the dominant
            remaining.remove(first.get(i));
            i++;
        }

        i = 0;
        while (i < firstIndex && i < second.size() && challengeList.size() < number) {
            challengeList.add(second.get(i)); //gets the medium
            remaining.remove(second.get(i));
            i++;
        }

        i = 0;
        while (i < secondIndex && i < third.size() && challengeList.size() < number) {
            challengeList.add(third.get(i)); //gets the hard names
            remaining.remove(third.get(i));
            i++;
        }

        /**
         * if still more names need to be filled up, this method adds the remaining names.
         */
        i = 0;
        while (challengeList.size() < number) {
            challengeList.add(remaining.get(i)); //gets the dominant
            remaining.remove(first.get(i));
            i++;
        }
        return challengeList;
    }

    public void saveSession() {

        List<String> params = new ArrayList<String>();

        for (String name : _nameRatings.keySet()){
            if (_nameRatings.get(name)==true){
                params.add("difficultNames &" + name + "&");
            }
        }
        saveSession(ChallengeRatings.SESSIONFILE, params);
    }


}
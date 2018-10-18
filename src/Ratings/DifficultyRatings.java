package Ratings;

import Model.Challenge;
import Model.Challenges;
import Model.Originals;

import java.util.*;

/**
 * This class represents difficulty in names
 * _nameRatings is a hashmap that contains the Name as the key value, and challenge list for each name as the value.
 */
public class DifficultyRatings {

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

    private void sortNames() {
        int listSize = Originals.getInstance().listNames().size();
        if (listSize == 0) {

        } else if (listSize == 1 || listSize == 2) {
            for (String name : _ascending) {
                _mediumNames.add(name);
                //_defaultNames.put(name, Difficulty.MEDIUM);
            }
        } else {
            int slice = (int) Math.floor(listSize / 3);
            for (int i = 0; i < slice; i++) {
                _easyNames.add((_ascending.get(i)));
                //_defaultNames.put(_ascending.get(i), Difficulty.EASY);
            }
            for (int i = slice; i < slice * 2; i++) {
                _mediumNames.add((_ascending.get(i)));
                // _defaultNames.put(_ascending.get(i), Difficulty.MEDIUM);
            }
            for (int i = slice * 2; i < listSize; i++) {
                _hardNames.add((_ascending.get(i)));
                //_defaultNames.put(_ascending.get(i), Difficulty.HARD);
            }
        }
    }

    /**
     * This method sorts the names by their name lengths
     *
     * @return
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
     * it also updates the lists
     */
    public void setRating(String nameKey, Boolean difficulty) {
        _nameRatings.put(nameKey, difficulty);
        if (!difficulty) { //if user set rating to be easy

            if (_hardNames.contains(nameKey)) {
                _hardNames.remove(nameKey);
            }
            _easyNames.add(nameKey); //todo should it be easy?

        } else { //user set rating to be hard
            if (_easyNames.contains(nameKey)) {
                _easyNames.remove(nameKey);

                _hardNames.add(nameKey);
            } else if (_mediumNames.contains(nameKey)) {
                _mediumNames.remove(nameKey);

                _hardNames.add(nameKey);
            }
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
     * This method generates a list of the names
     */
    public List<String> generateList(int difficultySession, int number) {
        System.out.println("before hard names are \n" + _hardNames);
        if (number > _ascending.size()) { //if number is too big
            return _ascending;
        }

        switch (difficultySession) {
            case 1:
                return getDiffList(_easyNames, _mediumNames, _hardNames, number, true, false);
            case 2:
                //
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
            firstIndex = (int) Math.floor(number * 0.4);
            secondIndex = (int) Math.floor(number * 0.2);
        } else if (extreme) {
            firstIndex = number;
            secondIndex = second.size();
        } else {
            firstIndex = (int) Math.floor(number * 0.5);
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

        i = 0;
        while (challengeList.size() < number) {
            challengeList.add(remaining.get(i)); //gets the dominant
            remaining.remove(first.get(i));
            i++;
        }

        printstuff();
        return challengeList;
    }

    public void printstuff(){
        System.out.println("easynames are \n" + _easyNames);
        System.out.println("mednames are \n" + _mediumNames);
        System.out.println("hardnames are \n" + _hardNames);

    }
}




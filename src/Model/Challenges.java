package Model;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * This class represents a list of challenges for each Name
 * _challenges is a hashmap that contains the Name as the key value, and challenge list for each name as the value.
 */
public class Challenges {

    private HashMap<String, ArrayList<Challenge>> _challenges;
    private List<Challenge> _existingChallenges;

    private final static Challenges instance = new Challenges();

    private Challenges() {
        _challenges = new HashMap<String, ArrayList<Challenge>>();
        _existingChallenges = new ArrayList<Challenge>();
        updateModel();
    }

    public static Challenges getInstance() {
        return instance;
    }

    /**
     * This method adds a challenge recording into the challenge list
     *
     * @param nameKey
     * @param challenge returns filename
     */
    public String addChallenge(String nameKey, Challenge challenge) {
        ArrayList<Challenge> challengeList = new ArrayList<Challenge>();

        if (_challenges.containsKey(nameKey)) { //if key already exists
            challengeList = _challenges.get(nameKey);

            // if list does not exist create it
            if (challengeList == null) {
                _existingChallenges.add(challenge);
                challengeList = new ArrayList<Challenge>();
                challengeList.add(challenge);
                _challenges.put(nameKey, challengeList);
            } else {
                challengeList.add(challenge);
            }
        } else {
            challengeList.add(challenge);
            _challenges.put(nameKey, challengeList);
        }

        return challenge.getFileName();
    }

    /**
     * Populates the _challenges with existing recording files in each folder.
     * iterates through the names list from Original.java
     * goes into each folder and adds to the list
     */
    public void updateModel() {
        List<String> names = Originals.getInstance().listNames();

        for (String name : names) {
            File file = new File("Names/" + name + "/Challenge");
            File[] fileList = file.listFiles();

            for (File f : fileList) {
                String fullName = f.getName();
                String fileName = fullName.substring(0, fullName.lastIndexOf('.'));
                Challenge challenge = new Challenge(name, fileName);
                addChallenge(name, challenge);
            }
        }
    }

    public int getChallengeSize(String nameKey) {
        if (_challenges.get(nameKey) != null) {
            return _challenges.get(nameKey).size();
        } else {
            return 0;
        }
    }

    /**
     * returns a list of the fileNames of the challenges given the nameKey
     */
    public List<String> listChallenges(String nameKey) {
        if (_challenges.get(nameKey) != null) {
            ArrayList<Challenge> challengeList = _challenges.get(nameKey);
            List<String> challengeNames = new ArrayList<String>();

            for (Challenge challenge : challengeList) {
                challengeNames.add(challenge.getFileName());
            }
            return challengeNames;

        } else {
            return null;
        }
    }

    /**
     * deletes the challenge from directory AND the challenges list
     *
     * @param nameKey
     * @param fileName
     */
    public void deleteChallenge(String nameKey, String fileName) {
        Boolean hi = challengeExists(nameKey, fileName);
        if (hi) {
            Challenge challengeDelete = getChallenge(nameKey, fileName);
            challengeDelete.delete(); // delete the challenge
            _challenges.get(nameKey).remove(challengeDelete); //not sure if this works yet (needs testing)
        }
    }

    /**
     * deletes the challenge from directory AND the challenges list
     */
    public void deleteChallenge(Challenge challenge) {
        challenge.getNameKey();
        _challenges.get(challenge.getNameKey()).remove(challenge); //not sure if this works yet (needs testing)
        challenge.delete();
    }

    /**
     * gets the challenge from the filename of challenge and the nameKey
     *
     * @param fileName
     * @return
     */
    public Challenge getChallenge(String nameKey, String fileName) {
        ArrayList<Challenge> challengeList = _challenges.get(nameKey);

        int index = 0;
        for (int i = 0; i < challengeList.size(); i++) {
            if (challengeList.get(i).getFileName().equals(fileName)) {
                index = i;
                break;
            }
        }
        return challengeList.get(index);
    }

    /**
     * this method generates a new recording based on the nameKey
     * it then adds it to the _challenges list
     *
     * @param nameKey
     * @return challenge
     */
    public String addNewChallenge(String nameKey) {
        // Create a new challenge of the given name
        Challenge challenge = new Challenge(nameKey);
        challenge.create();
        addChallenge(nameKey, challenge);
        _existingChallenges.add(challenge);
        return challenge.getFileName();
    }

    private Boolean challengeExists(String nameKey, String fileName) {

        File file = new File("Names/" + nameKey + "/Challenge/" + fileName + ".wav");
        return file.exists();
    }

    /**
     * deletes all existing challenges.
     */
    public void reset() {
        for (Challenge challenge : _existingChallenges){
            deleteChallenge(challenge);
        }

        _existingChallenges.clear();
    }

}





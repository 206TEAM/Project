package Model;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * This class represents a list of practices for each Name
 * _practices is a hashmap that contains the Name as the key value, and practice list for each name as the value.
 * _practiceNames is a list of the names that the user is currently practicing (originally selected)
 * _currentName is the name that the user is currently working on
 */
public class Challenges {

    private HashMap<String, ArrayList<Practice>> _practices;
    private ArrayList<String> _practiceNames;
    protected String _currentName;
    private final static Challenges instance = new Challenges();
    private String _currentFileName;

    private Challenges() {
        _practices = new HashMap<String, ArrayList<Practice>>();
        updateModel();
    }

    public void removePracticeName(String name){
        _practiceNames.remove(name);
    }

    public void clearCurrentNames() {
        _practiceNames = null;
    }

    /**
     * gets the current name that is being worked on or selected
     *
     * @ the current name
     */
    public String getCurrentName() {
        return _currentName;
    }

    /**
     * sets the current name being worked on
     *
     * @param name the current name
     */
    public void setFileName(String name) {
        _currentFileName = name;
    }

    /**
     * gets the filename of the name that is being worked on or selected
     *
     * @return the file name
     */
    public String getFileName() {
        return _currentFileName;
    }

    /**
     * sets the current name being worked on
     *
     * @param name the current name
     */
    public void setCurrentName(String name) {
        _currentName = name;
    }

    public static Challenges getInstance() {
        return instance;
    }

    /**
     * creates a currently practicing names list
     */
    public void addNames(List<String> names) {
        if (_practiceNames == null) {
            _practiceNames = new ArrayList();
            _practiceNames.addAll(names);
        } else {
            for (String name : names) {
                if (!_practiceNames.contains(name)) {
                    _practiceNames.add(name);
                }
            }
        }
    }

    public List<String> getPracticeNames() {
        return _practiceNames;
    }

    /**
     * This method adds a practice recording into the practice list
     *
     * @param nameKey
     * @param practice returns filename
     */
    public String addPractice(String nameKey, Practice practice) {
        ArrayList<Practice> practiceList = new ArrayList<Practice>();

        if (_practices.containsKey(nameKey)) { //if key already exists
            practiceList = _practices.get(nameKey);

            // if list does not exist create it
            if (practiceList == null) {
                practiceList = new ArrayList<Practice>();
                practiceList.add(practice);
                _practices.put(nameKey, practiceList);
            } else {
                practiceList.add(practice);
            }
        } else {
            practiceList.add(practice);
            _practices.put(nameKey, practiceList);
        }

        return practice.getFileName();
    }

    /**
     * Populates the _practices with existing recording files in each folder.
     * iterates through the names list from Original.java
     * goes into each folder and adds to the list
     */
    public void updateModel() {
        List<String> names = Originals.getInstance().listNames();

        for (String name : names) {
            File file = new File("Names/" + name + "/Practices");
            File[] fileList = file.listFiles();

            for (File f : fileList) {
                String fullName = f.getName();
                String fileName = fullName.substring(0, fullName.lastIndexOf('.'));
                Practice practice = new Practice(name, fileName);
                addPractice(name, practice);
            }
        }
    }

    /**
     * returns a list of the fileNames of the practices given the nameKey
     */
    public List<String> listPractices(String nameKey) {
        if (_practices.get(nameKey) != null) {
            ArrayList<Practice> practiceList = _practices.get(nameKey);
            List<String> practiceNames = new ArrayList<String>();

            for (Practice practice : practiceList) {
                practiceNames.add(practice.getFileName());
            }
            return practiceNames;

        } else {
            return null;
        }
    }

    /**
     * deletes the practice from directory AND the practices list
     *
     * @param nameKey
     * @param fileName
     */
    public void deletePractice(String nameKey, String fileName) {
        Practice practiceDelete = getPractice(nameKey, fileName);
        practiceDelete.delete(); // delete the practice
        _practices.get(nameKey).remove(practiceDelete); //not sure if this works yet (needs testing)
    }

    /**
     * gets the practice from the filename of practice and the nameKey
     *
     * @param fileName
     * @return
     */
    public Practice getPractice(String nameKey, String fileName) {
        ArrayList<Practice> practiceList = _practices.get(nameKey);

        int index = 0;
        for (int i = 0; i < practiceList.size(); i++) {
            if (practiceList.get(i).getFileName().equals(fileName)) {
                index = i;
                break;
            }
        }
        return practiceList.get(index);
    }

    /**
     * this method generates a new recording based on the nameKey
     * it then adds it to the _practices list
     *
     * @param nameKey
     * @return practice
     */
    public String addNewPractice(String nameKey) {
        // Create a new practice of the given name
        Practice practice = new Practice(nameKey);
        practice.create();
        addPractice(nameKey, practice);
        return practice.getFileName();
    }

}





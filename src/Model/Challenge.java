package Model;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a single recording for a challenge for a particular name.
 * This file is saved as a .wav in the directory:
 * Names/(_nameKey)/Practices/(fileName).wav
 *
 * @author Lucy Chen
 */

public class Challenge {

    private String _fileName;
    private String _nameKey;
    private Boolean _good;

    public Challenge(String nameKey) {
        _nameKey = nameKey;
        _fileName = generateFileName(nameKey);
        _good = null;
    }

    /**
     * creates a challenge instance based on a name as well as filename
     * @param nameKey the name of the challenge
     * @param fileName the filename of the challenge
     */
    public Challenge(String nameKey, String fileName) {
        _nameKey = nameKey;
        _fileName = fileName;
        _good = null;
    }

    /**
     * creates a challenge
     */
    public void create() {
        justAudio();
    }

    /**
     * this deletes a challenge
     */
    public void delete() {
        deleteFile(getDirectory() + System.getProperty("file.separator")+ _fileName + ".wav");
    }

    /**
     * this creates the audio component of the challenge
     * records for 5 seconds
     */
    public void justAudio() {
        String command = "ffmpeg -f alsa -i default -t 5 " + _fileName + ".wav";
        File directory = getDirectory();
        Media.process(command, directory);
    }

      /**
     *deletes a file specified by the fileName
     * @param filePath must of the format filename.wav etc
     */
    public void deleteFile(String filePath) {

        Path path = Paths.get(filePath);
        try {
            Files.delete(path);
        } catch (NoSuchFileException x) {
            System.err.format("%s: no such" + " file", path);
        } catch (DirectoryNotEmptyException x) {
            System.err.format("%s not empty%n", path);
        } catch (IOException x) {
            System.err.println(x);
        }
    }

    /**
     * generates file name based on other recordings.
     *
     * @param nameKey name that the {@code Practice} is linked to
     * @return a fileName in the format <q>(nameKey)Challenges(version)</q>
     */
    private String generateFileName(String nameKey){
        List<String> names = new ArrayList<String>();
        String name;
        File directory = getDirectory();
            for (File f : directory.listFiles()) {
                names.add(f.getName()); //adds the file names from directory into the list
            }

            if (names.size()==0) {
                name = nameKey + "Challenge1";
            } else {
                name = nameKey + "Challenge" + Integer.toString(names.size() + 1);

            }
        return name;
    }

    /**
     * @return string of the name of the challenge
     */
    public String getNameKey(){
        return _nameKey;
    }

    /**
     * @return fileName without extension
     */
    public String getFileName(){
        return _fileName;
    }

    /**
     * @return the directory of the challenge in a file format
     */
    public File getDirectory(){
        return new File("Names" + System.getProperty("file.separator") + _nameKey + System.getProperty("file.separator") + "Challenge");
    }

    /**
     * sets the rating of the challenge when the user sets it
     * @param rating boolean of the challenge of the rating
     */
    public void setRating(Boolean rating){
        _good = rating;
    }

    /**
     * @return rating of the particular challenge
     */
    public Boolean getRating(){
        return _good;
    }

}
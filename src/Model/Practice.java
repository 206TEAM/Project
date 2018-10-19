package Model;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a single recording for a particular name.
 * This file is saved as a .wav in the directory:
 * Names/(_nameKey)/Practices/(fileName).wav
 */


public class Practice {

    private String _fileName;
    private String _nameKey;

    public Practice(String nameKey) {
    	if (nameKey.contains(" ")) {
    		_nameKey = nameKey.replace(' ', '_');
	    } else {
		    _nameKey = nameKey;
	    }
        _fileName = generateFileName();
    }
    public Practice(String nameKey, String fileName) {
        _nameKey = nameKey;
        _fileName = fileName;
    }


    /**
     * this deletes a practice
     */
    public void delete() {
        deleteFile(getDirectory() + System.getProperty("file.separator")+ _fileName + ".wav");
    }

    /**
     * this creates the audio component of the practice
     */
    public void record() {
	    try {
		    Files.deleteIfExists(Paths.get("Temp/" + _fileName + ".wav"));
	    } catch (IOException e) {
		    e.printStackTrace();
	    }

	    String command = "ffmpeg -f alsa -i default -t 5 " + _fileName + ".wav";
        File directory = getDirectory();
        Media.process(command, directory);
    }

    public void stopRecording() {
    	Media.cancel();
    }

      /**
     *deletes a file specified by the fileName
     * @param filePath must of the format filename.mp3 filename.mp4 etc
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
     * @return a fileName in the format <q>(nameKey)Practice(version)</q>
     */
    private String generateFileName(){
        int version = 1;
        String name = _nameKey + "Practice" + version;
        File directory = getDirectory();
            for (File f : directory.listFiles()) {
                if (f.getName().equals(name)) {
                    name = _nameKey + "Practice" + version;
                }
            }
        return name;
    }

    /**
     * @return fileName without extension
     */
    public String getFileName(){
        return _fileName;
    }

    public File getDirectory(){
        return new File("Temp");
    }

}
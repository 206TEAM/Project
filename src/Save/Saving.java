package Save;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class is used to save information about the users progress
 * Classes can extend this abstract class to use its methods.
 *
 * @author Lucy Chen
 */
public abstract class Saving {

    /**
     * This method saves the session by taking in a fileName desired to be written to
     * as well as all of the information to be written to the text file.
     * Param must contain a single line of information in the form of "key &value_to_be_extracted&"
     * which the & symbols surrounding the value.
     */
    protected void saveSession(String fileName, List<String> params) {
        try {
            List<String> fileContents = new ArrayList<>(Files.readAllLines(Paths.get(fileName)));
            for (String param : params) {
                param = param + "\n";
                Files.write(Paths.get(fileName), param.getBytes(), StandardOpenOption.APPEND);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * this Method returns a list of all the values encoded &value& corresponding to the key.
     * filename is the name of the file you are reading from.
     */
    protected List<String> getList(String fileName, String key) {
        List<String> list = new ArrayList<>();
        try {
            List<String> fileContents = new ArrayList<String>(Files.readAllLines(Paths.get(fileName)));
            for (int i = 0; i < fileContents.size(); i++) {
                String output;
                if (fileContents.get(i).startsWith(key)) {

                    Pattern pattern = Pattern.compile("&+[a-zA-Z_0-9]+&"); //todo
                    Matcher matcher = pattern.matcher(fileContents.get(i));
                    if (matcher.find()) {
                        output = matcher.group(0);
                        list.add(getFieldsString(output, fileName));
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * this method gets the saved value encoded &value& from the corresponding key and fileName
     *
     * @param fileName the name of file to be read from
     * @param key      the key of the value to be retrieved
     * @return value
     */
    protected String getSaved(String fileName, String key) {
        String output;
        try {
            List<String> fileContents = new ArrayList<String>(Files.readAllLines(Paths.get(fileName)));


            for (int i = 0; i < fileContents.size(); i++) {
                if (fileContents.get(i).startsWith(key)) {
                    Pattern pattern = Pattern.compile("&+[a-zA-Z_0-9]+&"); //todo
                    Matcher matcher = pattern.matcher(fileContents.get(i));
                    if (matcher.find()) {
                        output = matcher.group(0);
                        return getFieldsString(output, fileName);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Helper method that gets the value from the decoded value.
     *
     * @param param
     * @param fileName
     * @return
     */
    private String getFieldsString(String param, String fileName) {
        int paramLength = param.length();

        return param.substring(1, paramLength - 1);
    }

    /**
     * method that creates the text file for saving
     *
     * @param fileName name of file to be used
     */
    protected void createTextFile(String fileName) {
        try {
            File ratings = new File(fileName);
            if (Files.notExists(ratings.toPath())) {
                Files.createFile(ratings.toPath());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void deleteTextFile(String fileName) {
        File f = new File(fileName);
        if (f.exists()) {
            f.delete();
            try {
                f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}

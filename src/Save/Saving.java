package Save;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class Saving {

    /**
     * saves session.
     * @param fileName
     * @param params
     */
    protected void saveSession(String fileName, List<String> params) {
        try {
            List<String> fileContents = new ArrayList<>(Files.readAllLines(Paths.get(fileName)));
            for (String param : params) {
                int lineNumber = getLineNumber(param, fileName);
                if (lineNumber == -1) {
                    // Adds a new line if the name is rated for the first time
                    param = "\n" + param;
                    Files.write(Paths.get(fileName), param.getBytes(), StandardOpenOption.APPEND);
                } else {
                    // Rewrites entire file, replacing existing line.
                    fileContents.set(lineNumber, param);
                    Files.write(Paths.get(fileName), fileContents, StandardCharsets.UTF_8);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * gets line number
     */
    private int getLineNumber(String key, String fileName) {
        try {
            List<String> fileContents = new ArrayList<String>(Files.readAllLines(Paths.get(fileName)));

            for (int i = 0; i < fileContents.size(); i++) {
                if (fileContents.get(i).startsWith(key)) {
                    return i;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1;
    }

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

    private String getFieldsString(String param, String fileName) {
        int paramLength = param.length();

        return param.substring(1, paramLength-1);
    }

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

}

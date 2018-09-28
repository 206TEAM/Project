package Model;

import javax.naming.InvalidNameException;
import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class handles the individual sound recordings from <dir>Recordings</dir>.
 * A sound recording in that directory is referred to as an {@code Original}.
 *
 * <p> {@code Original} objects cannot be altered, deleted, or created from within the program.
 * Any interaction other than playing the sound recording must be done externally
 * by altering files in <dir>Recordings</dir>.</p>
 *
 * <p> An {@code Original} is identified by its file name exactly as it is written.
 * However, a user friendly name can be retrieved from {@link #extractName()}
 * and is stored in {@link #_name}.</p>
 *
 * <p> Behaviour of all the {@code Original} objects in a group is different.
 * This behaviour is implemented in {@link Originals}</p>
 *
 * @author Eric Pedrido
 */
public class Original {

	private String _name;
	private String _fileName;
	private String _version = "";

	/**
	 * The {@code Original} files are provided by <dir>Recordings</dir>
	 * and so this constructor does not construct an {@code Original} directly.
	 * Instead, it creates a reference to the corresponding file.
	 *
	 * @param fileName Name of the {@code Original} to reference.
	 * @throws FileNotFoundException if the requested file doesn't exist.
	 * @throws InvalidNameException  if the fileName has a user-friendly name that contains
	 *                               characters that are not alphabet letters or spaces.
	 * @see #extractName()
	 */
	public Original(String fileName) throws FileNotFoundException, InvalidNameException {
		// Find the file with the entered filename
		if (Files.exists(Paths.get("Recordings/" + fileName))) {
			_fileName = fileName;
			_name = extractName();
		} else {
			throw new FileNotFoundException();
		}
	}

	/**
	 * Extracts a name that is user-friendly from the file name
	 * of an {@code Original}.
	 *
	 * <p> The naming convention for an {@code Original} is
	 * <q>(user code)_(date)_(hh-mm-ss)_(name).wav</q> where
	 * h, m, and s are hours, minutes, and seconds representing
	 * the time the recording was taken. This method extracts just the name
	 * without any file extensions.</p>
	 *
	 * @return just the name with no file extensions.
	 * It is impossible for a match to be not found
	 * because the {@code Original} file must
	 * exist in order for the instantiation to be
	 * successful.
	 * @throws InvalidNameException if the name contains characters that are not
	 *                              alphabet letters or spaces.
	 */
	public String extractName() throws InvalidNameException {
		StringBuilder name = new StringBuilder();

		Pattern pattern = Pattern.compile("[ a-zA-Z]+.wav");
		Matcher matcher = pattern.matcher(_fileName);

		if (matcher.find()) {
			name.append(matcher.group(0));
			name.setLength(name.length() - 4); // Removes .wav file extension
			return name.toString();
		} else {
			throw new InvalidNameException("Name must contain only letters and spaces");
		}

	}

	public void setVersion(String version) {
		_version = version;
	}

	public String getVersion() {
		return _version;
	}

	public File getDirectory() {
		return new File("Names/" + _name + "/Original");
	}

	public String getName() {
		return _name;
	}

	public String getNameWithVersion() {
		return _name + _version;
	}

	public String getFileName() {
		return _fileName;
	}
}

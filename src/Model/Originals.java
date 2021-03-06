package Model;

import javax.naming.InvalidNameException;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class represents a collection of {@link Original} objects.
 * It is responsible for handling their behaviour as a group, not
 * as individual files.
 *
 * <p> This class can only be instantiated once because it denies
 * the possibility for multiple lists of {@code Originals} to be used.
 * The one list generated from scanning the contents of <dir>Recordings</dir>
 * is in essence, the only true list of {@code Originals}.</p>
 *
 * @author Eric Pedrido
 */
public class Originals {

	private List<Original> _originals = new ArrayList<>();
	private List<Original> _concats = new ArrayList<>();

	private static final Originals _SINGLETON = new Originals();

	/**
	 * Upon construction, <dir>Recordings</dir> will be scanned
	 * to populate {@link #_originals} and creates <dir>Ratings.txt</dir>,
	 * which stores information on the quality of files that the user
	 * has rated as either good or bad.
	 */
	private Originals() {
		updateModel();
		try {
			File ratings = new File ("Ratings.txt");
			if (Files.notExists(ratings.toPath())) {
				Files.createFile(ratings.toPath());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Reads all existing {@code Original} objects provided in <dir>Recordings</dir>
	 * and creates <dir>Names/(name)</dir> if such folders do not already exist.
	 *
	 * <p> Each {@code Original} of unique name gets its own sub-folders where
	 * the {@code Original} and {@code Challenge} are stored. A Names folder is
	 * created to better organise the database and make changes without tampering
	 * with the database files.</p>
	 *
	 * <p> Should there be multiple {@code Original} files of the same name (not file name),
	 * then multiple recordings will be stored in the same directory,
	 * but will have a number at the end indicating which
	 * version it is. For example
	 * <dir>Smith1.wav</dir>
	 * <dir>Smith2.wav</dir>.</p>
	 */
	public void populateFolders() {
		try {
			List<String> names = listAllNames();
			List<String> fileNames = listFileNames("Recordings");
			// Make a folder for each creation containing sub-folders
			for (String name : names) {
				if (Files.notExists(Paths.get("Names/" + name))) {
					Files.createDirectories(Paths.get("Names/" + name + "/Original"));
					Files.createDirectories(Paths.get("Names/" + name + "/Challenge"));
				}
			}

			HashMap<String, Integer> duplicateNames = new HashMap<>();

			// Put the respective creations in their folders
			for (int i = 0; i < fileNames.size(); i++) {
				String creation = fileNames.get(i);
				String name = names.get(i);
				String finalName = creation;

				// Insert an int n for the nth version of that Original.
				if (names.lastIndexOf(name) != names.indexOf(name)) {
					Integer version = 0;
					if (duplicateNames.containsKey(name)) {
						version = duplicateNames.get(name);
					}
					duplicateNames.put(name, version + 1);

					_SINGLETON.getOriginal(creation).setVersion("" + (version + 1));

					StringBuilder tempName = new StringBuilder(creation);
					tempName.insert(tempName.length() - 4, duplicateNames.get(name));

					finalName = tempName.toString();
				}
				// Move the file from the database into the Names directory
				if (Files.notExists(Paths.get("Names/" + name + "/Original/" + finalName)))
					Files.copy(Paths.get("Recordings/" + creation),
							Paths.get("Names/" + name + "/Original/" + finalName),
							StandardCopyOption.COPY_ATTRIBUTES);
			}
		} catch (IOException | DirectoryIteratorException e) {
			System.err.println(e);
		}
	}

	/**
	 * Checks <dir>Recordings</dir> to build a {@code List<String>}
	 * containing the file name of every {@code Original}.
	 *
	 * <p> Note that a valid {@code Original} is any file that has
	 * the file extension <q>.wav</q>.</p>
	 *
	 * @return a list of the file names of all existing {@code Original} files.
	 * @throws IOException                if an I/O error occurs.
	 * @throws DirectoryIteratorException if an error occurs while iterating through
	 *                                    the directory.
	 */
	private List<String> listFileNames(String dir) throws IOException, DirectoryIteratorException {
		List<String> fileNames = new ArrayList<>();
		DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(dir), "*.wav");

		for (Path creation : stream) {
			fileNames.add(creation.getFileName().toString());
		}
		return fileNames;
	}

	/**
	 * Takes {@link #_originals} and extracts just
	 * the name of the {@code Original}, ignoring any other details in
	 * the file name.
	 *
	 * @return a list containing the names of all existing {@code Original} files.
	 */
	private List<String> listAllNames() {
		List<String> names = new ArrayList<>();
		try {
			for (Original creation : _originals) {
				names.add(creation.extractName());
			}
		} catch (InvalidNameException e) {
			System.err.println(e);
		}
		return names;
	}

	/**
	 * Finds all unique names from the database by scanning through the more organised
	 * Names directory.
	 *
	 * @return the list of unique names.
	 */
	public List<String> listNames() {
		List<String> names = new ArrayList<>();
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get("Names"))) {
			for (Path name : stream) {
				names.add(name.getFileName().toString());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return names;
	}

	/**
	 * Sets the values of {@link #_originals} to be correct given the current
	 * contents of <dir>Recordings</dir>.
	 */
	public void updateModel() {
		try {
			List<String> fileNames = listFileNames("Recordings");

			for (String fileName : fileNames) {
				_originals.add(new Original(fileName));
			}
		} catch (IOException | DirectoryIteratorException | InvalidNameException e) {
			System.err.println(e);
		}
	}

	/**
	 * Given the name of an existing {@code Original}, finds the
	 * corresponding {@code Original}, specifically,
	 * its {@link Original#_fileName}.
	 *
	 * @param name user-friendly name of the {@code Original}
	 * @return the corresponding file name if there exists an
	 *         {@code Original} with that name. Otherwise, return
	 *         {@code null}.
	 */
	public List<String> getFileName(String name) {
		List<String> fileNames = new ArrayList<>();
		for (Original original : _originals) {
			if (original.getName().equals(name)) {
				try {
					fileNames = listFileNames("Names/" + name + "/Original");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return fileNames;
	}

	/**
	 * Finds the corresponding {@code Original} with the entered fileName.
	 *
	 * @return {@code Original} with the entered file name. If no {@code Original}
	 *         exists with that file name, returns {@code null}.
	 */
	public Original getOriginal(String fileName) {
		for (Original original : _originals) {
			if (original.getFileName().equals(fileName)) {
				return original;
			}
		}
		return null;
	}

	/**
	 * Finds the {@code Original} given the name and version number.
	 *
	 * @return the corresponding {@code Original} with the entered name and version.
	 */
	private Original getOriginal(String name, String version) {
		for (Original original : _originals) {
			if (original.getName().equals(name) && original.getVersion().equals(version)) {
				return original;
			}
		}
		return null;
	}

	/**
	 * Find the {@code Original} with the matching fileName and name that
	 * has more than one version.
	 *
	 * @return the corresponding {@code Original} with the entered file name and name.
	 */
	public Original getOriginalWithVersions(String fileName, String name) {
		String version = extractVersion(fileName);
		return getOriginal(name, version);
	}

	/**
	 * Takes a file name and extracts the version of the {@code Original}
	 * that the fileName represents.
	 *
	 * @param fileName name of file to extract version from.
	 * @return the version that was extract. the String is a number
	 *         in string form ex: returns {@code "1"}.
	 */
	private String extractVersion(String fileName) {
		StringBuilder version = new StringBuilder();
		String output = null;

		Pattern pattern = Pattern.compile("[0-9]+.wav");
		Matcher matcher = pattern.matcher(fileName);

		if (matcher.find()) {
			version.append(matcher.group(0));
			version.setLength(version.length() - 4);
			output = version.toString();
		}
		return output;
	}

	/**
	 * Gets all versions of {@code Original}'s corresponding
	 * to the same name.
	 *
	 * @param name the name of {@code Original} to get versions from.
	 * @return a List of all {@code Original}'s that correspond to the name.
	 */
	public List<Original> getAllVersions(String name) {
		List<Original> output = new ArrayList<>();
		int i = 1;
		while (true) {
			String version = String.valueOf(i);
			Original original = getOriginal(name, version);
			if (original == null) {
				break;
			} else {
				if (!output.contains(original))
				output.add(original);
			}
			i++;
		}
		return output;
	}

	/**
	 * Sets the rating of an {@code Original} by writing
	 * it into <dir>Rating.txt</dir> in the format
	 * <q>Name: &good&</q> or <q>Name: &bad&</q> depending
	 * on the input from the user.
	 *
	 * @param original name of the {@code Original}.
	 * @param rating rating to set.
	 */
	public void setRating(Original original, String rating) {
		String name = original.getName() + original.getVersion();
		String text = name + ": " + rating;
		try {
			int lineNumber = getLineNumber(name);
			List<String> fileContents = new ArrayList<>(Files.readAllLines(Paths.get("Ratings.txt")));
			if (lineNumber == -1) {
				// Adds a new line if the name is rated for the first time
				text = "\n" + text;
				Files.write(Paths.get("Ratings.txt"), text.getBytes(), StandardOpenOption.APPEND);
			} else {
				// Rewrites entire file, replacing existing line.
				fileContents.set(lineNumber, text);
				Files.write(Paths.get("Ratings.txt"), fileContents, StandardCharsets.UTF_8);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Reads the contents of <dir>Ratings.txt</dir> and
	 * extracts just the rating for the given name should
	 * there be a rating.
	 *
	 * <p> The default rating for any {@code Original} is 0
	 * which indicates that no rating has been set. </p>
	 *
	 * @param original the name of the {@code Original} to find.
	 * @return the corresponding rating as set in <dir>Ratings.txt</dir>.
	 *         If no rating is specified for the given name, then
	 *         returns 0.
	 */
	public String getRating(Original original) {
		String output = "&good&";
		if (original != null) {
			try {
				int counter = -1;
				boolean found = false;
				BufferedReader br = new BufferedReader(new FileReader("Ratings.txt"));
				String line;

				while ((line = br.readLine()) != null) {
					counter++;
					if (counter == getLineNumber(original.getNameWithVersion())) {
						found = true;
						break;
					}
				}
				if (found) {
					Pattern pattern = Pattern.compile("&[a-z]+&");
					Matcher matcher = pattern.matcher(line);
					if (matcher.find()) {
						output = matcher.group(0);
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return output;
	}

	/**
	 * Finds the line in <dir>Ratings.txt</dir> that the entered
	 * name is on.
	 *
	 * @param name the name to match in <dir>Ratings.txt</dir>
	 * @return the line number
	 */
	private int getLineNumber(String name) {
		try {
			List<String> fileContents = new ArrayList<>(Files.readAllLines(Paths.get("Ratings.txt")));

			for (int i = 0; i < fileContents.size(); i++) {
				if(fileContents.get(i).startsWith(name)) {
					return i;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return -1;
	}

	/**
	 * Adds a name that contains spaces to a separate list from
	 * {@link #_originals} since concatenated names are only stored
	 * temporarily.
	 *
	 * @param concat the concatenated {@code Original} to add.
	 */
	public void addConcat(Original concat) {
		_concats.add(concat);
	}

	/**
	 * Gets the file name of the concatenated {@code Original} given the name.
	 */
	public String getConcatFileName(String name) {
		for (Original original : _concats) {
			if (original.getName().equals(name)) {
				return original.getFileName();
			}
		}
		return null;
	}

	/**
	 * Finds the concatenated {@code Original} given the name.
	 */
	public Original getConcatOriginal(String name) {
		for (Original original : _concats) {
			if (original.getName().equals(name)) {
				return original;
			}
		}
		return null;
	}

	/**
	 * Ensures that only one object of {@code Originals}
	 * can be instantiated.
	 *
	 * @return the singleton {@code Originals} object.
	 */
	public static Originals getInstance() {
		return _SINGLETON;
	}

}
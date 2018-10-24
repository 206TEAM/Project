package Control;

import Model.Practice;

/**
 * Observer design pattern used for {@link PracticeMainController} to communicate
 * directly to {@link PracticeRecordController}.
 *
 * <p> In this situation {@code PracticeMainController} is the observable class, as it
 * notifies its observers of any selections the user has made. </p>
 *
 * <p> Initially developed because of previous versions of NameSayer having
 * multiple {@code SUB_PANE}'s that observed {@code PracticeMainController},
 * however, that was removed in a recent patch. This framework was kept here
 * in case of future development. </p>
 *
 * @author Eric Pedrido
 */
public interface Observer {
	/**
	 * Updates the GUI with the values given by the Observable class.
	 *
	 * @param name             Selected name from the table
	 * @param fileName         corresponding file name
	 * @param numberOfVersions the number of versions of the name
	 * @param practice         a {@code Practice} object corresponding to the selected name
	 */
	void update(String name, String fileName, int numberOfVersions, Practice practice);
}

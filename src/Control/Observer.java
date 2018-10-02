package Control;

import Model.Practice;

public interface Observer {
	void update(String name, String fileName, int numberOfVersions, Practice practice);
}

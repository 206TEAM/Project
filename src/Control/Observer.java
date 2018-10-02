package Control;

public interface Observer {
	void update(String name);
	void update(String name, String fileName, int numberOfVersions);
}

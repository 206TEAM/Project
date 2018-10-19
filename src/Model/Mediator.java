package Model;

import Control.*;

import java.util.List;

public class Mediator {

    private final static Mediator SINGLETON = new Mediator();

    private HeaderController _header;
    private MainMenuController _main;
    private ChallengeCompareController _subMain;
    private PracticeMainController _practiceMain;

    private List<String> _practiceMainList;

    /********fields are for challenges********/
    private ChallengeSession _session;

	private ParentController.PageType _currentPage;

    private String _currentName;
    private String _originalFileName;
    private Practice _currentPractice;
    private int _numVersions;
    private List<String> _challengeList;
    private List<String> _challengeFileList;
    private Boolean _inChallengeSession;
    private List<String> _missingNames;
    private String _missingListFile;

	public void addObserver(Observer o) {
		_practiceMain.addObserver(o);
	}

	/********method for getting session object********/
    public void setChallengeSession(ChallengeSession session){
        _session = session;
    }

    //todo add enums
    public void  setInChallengeSession(){ //for aborting (when click home button).
        _inChallengeSession = true;
    }

    public void  removeInChallengeSession(){
        _inChallengeSession = false;
    }

    public Boolean getChallengeStatus(){
        return _inChallengeSession;
    }

    public ChallengeSession getChallengeSession(){
        return _session;
    }

    /********methods for getting/setting challenge related things********/

    public String getCurrentName() {
        return _currentName;
    }

    public void setCurrentName(String name) {
        _currentName = name;
    }

    public void setCurrentPractice(Practice practice) {
    	_currentPractice = practice;
    }

    public Practice getCurrentPractice() {
    	return _currentPractice;
    }

    public String getOriginalFilename(){
        return _originalFileName;
    }

    public void setOriginalFilename(String fileName){
        _originalFileName = fileName;
    }

    public void setNumVersions(int numVersions) {
    	_numVersions = numVersions;
    }

    public int getNumVersions() {
    	return _numVersions;
    }

    public void setCurrent(String name, String fileName, int numVersions, Practice practice) {
    	setCurrentName(name);
    	setOriginalFilename(fileName);
    	setNumVersions(numVersions);
    	setCurrentPractice(practice);
    }

    /*********Methods for setting scenes**********/
    public void setParent(HeaderController parent) {
    	_header = parent;
    }

    public void setParent(MainMenuController parent) {
    	_main = parent;
    }

    public void setParent(ChallengeCompareController parent) {
    	_subMain = parent;
    }

    public void setParent(PracticeMainController parent) {
    	_practiceMain = parent;
    }

    public void setPageType(ParentController.PageType type) {
    	_currentPage = type;
    }

    public void loadPane(ParentController.Type parent, String page) {
        if (parent == ParentController.Type.HEADER) {
            _header.loadPane(page);
            _header.setPage(_currentPage);
        } else if (parent == ParentController.Type.MAIN) {
            _main.loadPane(page);
        } else if (parent == ParentController.Type.SUB_MAIN) {
            _subMain.loadPane(page);
        } else if (parent == ParentController.Type.PRACTICE) {
        	_practiceMain.loadPane(page);
        }
    }

	/*********Methods for setting up practices**********/
    public void setPracticeMainList(List<String> list) {
        _practiceMainList = list;
    }

    public List<String> getPracticeMainList() {
        return _practiceMainList;
    }

    public static Mediator getInstance() {
        return SINGLETON;
    }

	public void fireDisableTable(PracticeMainController.TableType type1, PracticeMainController.TableType type2, boolean disable) {
		fireDisableTable(type1, disable);
		fireDisableTable(type2, disable);
	}

	/**
	 * Disables a table in {@link PracticeMainController} so that
	 * the user cannot change their selection.
	 *
	 * @param type which table to disable/enable
	 * @param disable if {@code true}, then disables the table;
	 *                otherwise, enables the table.
	 */
	public void fireDisableTable(PracticeMainController.TableType type, boolean disable) {
		_practiceMain.setDisableTables(type, disable);
	}

	public void fireTableValues(List<String> newTable) {
		_practiceMain.setTableValues(newTable);
	}

	public boolean praticeNotNull() {
		return _practiceMain != null;
	}

	public void setMissingNames(List<String> missingNames, String missingListFile) {
		_missingNames = missingNames;
		_missingListFile = missingListFile;
	}

	public List<String> getMissingNames() {
		return _missingNames;
	}

	public String getMissngListFile() {
		return _missingListFile;
	}

}

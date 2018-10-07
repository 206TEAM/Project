# **Name Sayer**

*Name Sayer* is an application that allows you to listen to, and record different names in a database. You can *Practice* names or test your ability and *Challenge* yourself. This application is intended for anyone who wishes to master pronouncing the names of their peers to avoid potential embarrassment or just for fun. 

### **How to Run**
1) Open the terminal my pressing 'Ctrl + Alt + T'
2) Set the current directory to the location of 'NameSayer.jar'
3) Type the code below and press 'Enter'
    > java -jar NameSayer.jar

### **Usage**
****
#### Main Menu
****
- Access all features from here.
- Select on which feature you want to use by pressing on the appropriate button.
****
#### Practice Mode
****
- **Select Practices:**
Select Names from the database to practice

| Element | Description |
| ------ | ------ |
|Name Table|*Select a name from the database to practice*|
|Preview Table|*Approved names to practice will be displayed here. Clicking on a name will remove it from the list*|
|Add Button|*Prompts a drowpdown menu where the user can: either **upload** a file or create a new name from existing names. See: **Create** for details on creating a new name*|
|Shuffle Checkbox|*Checking this box shuffles the **Preview Table**. Unchecking makes the table display in alphabetical order*|
|Go Button|*Proceeds to the **Play and Record** page*|
|Reset Button|*Clears the **Preview Table***|

**Uploading a file:** Must be a '.txt' file. Names are separated by new lines.
****
- **Play and Record:**
Play the original recording and record your own attempt. Your recorded attempts are not saved permanently and are deleted when you close the application.

| Element | Description |
| ------ | ------ |
|Name Table|*Select which name to listen to and attempt*|
|Version Table|*Displays the versions of the selected name. Select one to play that version. A **good** version is selected by default*|
|Play Button|*Plays the selected version*|
|Record Button|*Records your attempt of the selected name. Becomes **Stop***|
|Stop Button|*Stops recording your attempt. If you do not stop the recording, it will automatically stop after 5 seconds. Becomes **Re-record***|
|Re-record Button|*Deletes your previous attempt and records a new one.*|
|'Tick' Button|*Notifies the system that this **Version** is of **good** quality. *|
|'Cross' Button|*Notifies the system that this **Versions** is of **bad** quality.*|

**Good Quality Version:** All versions are rated 'good' by default. If a version is good, then it gets selected to be played by default over 'bad' ones.
****
- **Compare Your Attempt:**
Compare the attempt you just recorded to the original recordings. At this stage, you cannot select a new name, since the name you recorded for 

| Element | Description |
| ------ | ------ |
|Play Original Button|*Plays the version selected in the **Version Table***|
|Play Practice Button|*Plays the attempt previously recorded*|
|Next Button|*Moves onto the next name on the list. Goes back to **Play and Record**. If there are no more names on the list, returns to **Main Menu***|
*****
#### Challenge Mode
****
- **Challenge Setup:**
Configure the settings desired for the challenge mode.

| Element | Description |
| ------ | ------ |
|Amount Slider|*Select how many names you wish to go through. Names in challenge mode are selected randomly based off the selected **difficulty***|
|Difficulty Slider|*Select how difficult you wish the challenge to be. The more difficult, the more names that you often fail or have marked to find difficult will appear.*|
|Start Button|*Confirms settings and starts the challenge. Changes to the **Record Attempts** page*|
****
- **Record Your Attempts:**
Flashes names at you to attempt in a game-like fashion. This is initiated with a 3 second countdown to get you ready. You have 5 seconds to attempt each name before it automatically moves on to the next name.

| Element | Description |
| ------ | ------ |
|Next Button|*Stops your current attempt early and moves onto the next name in the list. If there are no more names on the list, becomes **Done Button***|
|Done Button|*Indicates the completion of the list. Moves you to the **Compare and Score** page*|
****
- **Compare and Score:**
Compare your attempts with the original versions and rate yourself on how you did. This self-scoring system determines your progress, so be honest! Shares much of the functionality of **Practice mode: Compare your attempt**. Only the different elements will be described below.

| Element | Description |
| ------ | ------ |
|Correct Button|*Scores your attempt as **correct** and moves onto the next name on the list. If there are no more names on the list, displays the **Score Review Pop-up***|
|Wrong Button|*Same as **Correct Button** except it scores your attempt as **wrong***|

**Correct and Wrong:** These are permanently saved in your progress and determine how well you do with certain names.
****
- **Score Review Pop-up:**
Shows which names you got *correct* and which ones you did not. Gives you a hypothetical *grade* based on your performance and allows you to choose from the following options below.

| Element | Description |
| ------ | ------ |
|Redo Button|*Reloads the Challenge session with the same list*|
|Home Button|*Returns you to the **Main Menu***|
|New Names Button|*Brings you to the **Challenge Setup** page to start a new Challenge session.*|
****

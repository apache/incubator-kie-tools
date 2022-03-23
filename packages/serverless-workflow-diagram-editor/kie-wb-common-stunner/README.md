# Stunner Modelling Tool

Stunner is a multi-purpose modelling tool based on [JBoss Uberfire](http://www.uberfireframework.org/). Multiple KIE designer and modeler components are developed atop Stunner.

## Documentation

Various complementary documents related to Stunner development and functionality can be found in a public, shared [Google Docs folder](https://drive.google.com/open?id=0B5LZ7oQ3Bza2Qk1GY1ZPeEN6Q0E).

## Building Stunner Source Code

To compile all Stunner components and install to your local Maven repository, issue the following commands:

    cd kie-tools/packages/stunner-editors/kie-wb-common-stunner/
    mvn clean install -DskipTests

## Running the Application

Execution and demonstration of the various Stunner-based components can be achieved using any of the following showcase:

- [**Stunner BPMN Kogito Showcase**](./kie-wb-common-stunner-sets/kie-wb-common-stunner-bpmn/kie-wb-common-stunner-bpmn-kogito-runtime/README.md)

## PR process

- It is a good practice to use the git branch with actual Jira issue ID, for example, `JBPM-1`. This technique also will help you to eliminate issues with multi-repository PRs (see below).
- During commit add appropriate jira issue ID (KOGITO, JBPM) at the beginning of the commit message.
- Before pushing to Github execute `mvn clean package -Dfull` to execute checkstyle and spotbug plugins locally.
- When creating multi-repository PR ensure that all repositories have the same branch name otherwise our Jenkins will build PRs separately.
- Create [Draft PR](https://github.blog/2019-02-14-introducing-draft-pull-requests/) instead of regular.
- If you need to check some particular points (on the code or runtime level) to finish the task or ensure that everything is fine, ask a particular person in the PR comments before you will perform final runtime tests.
- When PR is created and if you are a part of the [kiegroup](https://github.com/orgs/kiegroup/people) put a comment with content `Jenkins execute full downstream build`.
  - Wait for the results of project Jenkins and Full/Compile downstream build Jenkins results.
  - If Jenkins results are not green, check the results and if the issues are related to your changes, fix them and re-trigger builds.
    - Repository build will be triggered automatically when you push your changes, but Full/Compile downstream build should be re-triggered manually.
  - If Jenkins builds are not green, but you are sure it is not related to your changes, find the author of the failing tests and ping him/her in the PR comments.
  - Ensure that your PR passed Sonar Cloud gates as well. If you do not agree with the issues reported by Sonar, or your tests are running but not measured correctly, put a comment to your PR.
  - If Jenkins builds are green, or you are sure that failed tests are not related to your changes and Sonar Cloud gates are passed, download Business Central from Full downstream build and test it locally against Acceptance Criteria / Steps to reproduce. Sometimes the issue is fixed in the showcase but still reproducible in the product.
  - When everything is done, mark PR as ready for review and ask another developer AND QE to do the review.
- When PR is merged move Jira issues to state `Status: Resolved`, `Resolution: Done`, set next closest release as `Fix version`.

## IDE Environment setup

What follows are the steps needed for setting up the Stunner environment with **IntelliJ IDEA 15.0**. Newer versions of IntelliJ IDEA, up to and including 2018, have known compatibility issues with the Stunner environment. For this reason, it is **strongly** recommended that you use **IntelliJ IDEA Version 15.0** which can be downloaded from the [Previous Releases](https://www.jetbrains.com/idea/download/previous.html) page.

**Prerequisite Steps for LINUX Users**

Change the _inotify_ limits configuration as noted below to avoid further issues within the IDE.

1.  Close the IDE
2.  Edit `/etc/sysctl.conf` and add the following content:

    fs.inotify.max_user_watches = 524288
    fs.inotify.max_user_instances = 524288

3.  Edit `/etc/security/limits.conf` and add the following content:

    <user> soft nofile 4096
    <user> hard nofile 10240

    E.g.:

         roger soft nofile 4096
         roger hard nofile 10240

**Prerequisite Steps for Windows Users**

**[WARNING]** Showcase applications only work in Windows 10 Anniversary Update and above (build 1607)

1. Enable long paths in Windows

- open regedit -> `HKEY_LOCAL_MACHINE\SYSTEM\CurrentControlSet\Control\FileSystem` -> Change `LongPathsEnabled` from `0` to `1`

2. Enable long paths in Git

- Open GitBash -and type `git config --system core.longpaths true`

3. IDEA15 executable

- The default setup creates a shortcut to idea.exe. Be sure to change to idea64.exe, otherwise you'll be running 32-bits version

**Before Working with the IDE**

Prior to importing the project into the IDE, assuming [kie-tools](../) has already been cloned locally, navigate to that directory and issue the following command:

        cd kie-tools/packages/stunner-editors
        mvn clean install -DskipTests

**IDEA Environment Setup**

If you have not run the step from **Before Working with the IDE** above, do so before continuing.

**_Importing the project into IDEA_**

1. From the header menu, select **File** --> **New** --> **Project from Existing Sources...**
2. Using the directory tree, navigate to and select the **pom.xml** file within the **kie-tools/packages/stunner-editors/kie-wb-common-stunner** directory and hit **OK**.
3. At the bottom of the new dialog box, click the **Environment Settings...** button.
4. Use the '**...**' button next to the **Maven home directory** field to locate and select your local Maven (3.3.9+) installation, rather than the "Bundled" prefilled value.
5. Select **OK** to close the Maven environment popup dialog.
6. From hereout, click **Next** 3 times, which should lead to a SDK selection screen. Ensure that your **JDK home path** reflects a Java instance of 1.8 (recommended JDK) or above.
7. Click **Next** once more, then **Finish**.
8. At this point, the project is loading into the IDE. Note that it will take some time to fully load and index.

**_Configure Maven in the IDE_**

1. Open **Preferences** from the **File** (linux/Win) menu or **IntelliJ IDEA** app menu (mac).
2. In the search field, type _maven_.
3. Use the '**...**' button next to the _Maven home directory_ field to locate and select your local Maven (3.3.9+) installation, rather than the "Bundled" prefilled value.
4. In the left-hand navigation tree, select _Importing_ underneath _Maven_.
5. Change the value of _VM options for importer_ to _-Xmx2048m_.
6. Change the _JDK for importer_ field to reflect your local Java JDK installation.
7. (**OPTIONAL**) check the _Import maven projects automatically_ option if desired.
8. Click _Apply_ and/or _OK_.

**_Configure Code Styling_**

1. Download the [KIE code-style](https://github.com/kiegroup/droolsjbpm-build-bootstrap/blob/main/ide-configuration/intellij-configuration/code-style/intellij-code-style_droolsjbpm-java-conventions.xml) XML configuration file.
2. Open **Preferences** from the **File** (linux/Win) menu or **IntelliJ IDEA** app menu (mac).
3. In the search field, type '_code style_'.
4. To the right of the _Scheme_ field, locate and click the _Manage..._ button.
5. Click _Import_.
6. In the _Import From_ popup, select "IntelliJ IDEA code style XML". If this option is not available, proceed to **Manually Importing the Code Style** below at this time.
7. Navigate to the downloaded XML file and select it.
8. Click _Apply_ and/or _OK_.

**_Manually Importing the Code Style_**

If you were able to use the IDE GUI to import the code style successfully above, skip this section.

1. If you were not able to select the "IntelliJ IDEA code style XML" option, close the IDE.
2. Refer to [this support entry](https://intellij-support.jetbrains.com/hc/en-us/articles/206544519-Directories-used-by-the-IDE-to-store-settings-caches-plugins-and-logs) to identify the Configuration directory used by IntelliJ IDEA for your operating system.
3. Copy the downloaded code style XML file to the _[CONFIG_LOCATION]/codestyles_ directory.
4. Start the IDE again, navigate to _Code Styles_ within _Preferences_, and "KIE Java Conventions" should now be available in the _Scheme_ dropdown selector.
5. Select this option, then click _Apply_ and/or _OK_.

**_Configure the File Header Template_**

1. Open **Preferences** from the **File** (linux/Win) menu or **IntelliJ IDEA** app menu (mac).
2. In the search field, type '_file and code templates_'.
3. Select the _Includes_ tab.
4. Select _File Header_ in the left-hand list.
5. Remove any line in the template referencing @author, as KIE convention is to omit this in lieu of Git history information.

**_Configure Copyright Template_**

1. Open **Preferences** from the **File** (linux/Win) menu or **IntelliJ IDEA** app menu (mac).
2. In the search field, type '_copyright_'.
3. Select _Copyright Profiles_, located under _Copyright_.
4. Click the plus (+) sign in the top-left corner, then enter "KIE Copyright" in the name field in the popup.
5. Replace the contents of the _Copyright text_ field with the contents of the [KIE licensing file](https://raw.githubusercontent.com/kiegroup/droolsjbpm-build-bootstrap/main/ide-configuration/LICENSE-ASL-2.0-HEADER.txt).
6. Within the newly pasted text, replace "_${year}_" with "_$today.year_", then click _Apply_.
7. In the left-hand navigation tree, select _Copyright_, the parent node of the currently screen item.
8. Use the _Default project copyright_ dropdown to select the newly created "_KIE Copyright_" value.
9. Click _Apply_ and/or _OK_.

**_Configure GWT Facets_**

1. If you don't already have a local copy of the GWT SDK, download the latest version [from here](http://www.gwtproject.org/download.html) and extract it somewhere safe where you keep other SDK's.
2. From the menu, click _View_, then select _Open Module Settings_.
3. In the left-hand _Project Settings_ list, select _Facets_.
4. In the left-hand facet list, select _GWT_.
5. Ensure the _Defaults_ tab is selected at the top.
6. Change the _Path to GWT installation directory_ to point to your local GWT SDK installation.
7. Change the _JavaScript output style_ option to _Detailed_.
8. Change the _Compiler maximum heap size_ to '4096'.
9. Select the _All Facets_ tab at the top and repeat steps 6-8.

**_Setting up a Showcase Run/Debug Configuration_**

The following assumes that the Standalone Showcase will be utilized. Some evident name/directory adjustments are required in order to run the Project Showcase instead, although doing so is not typically recommended (see above).

1.  From the menu, click _Run_, then select _Edit Configurations..._
2.  Click the plus (+) sign in the upper left-hand corner, then select _GWT Configuration_.
3.  Change _Name_ to '_Standalone Showcase_'.
4.  Use the _Module:_ dropdown to select '_kie-wb-common-stunner-bpmn-kogito-runtime_'.
5.  Click the _Use Super Dev Mode_ checkbox to enable if not already checked.
6.  Change _GWT Modules to load_ to '_org.kie...standalone.FastCompiledStunnerStandaloneShowcase_'.
7.  Paste the following into the _VM Options_ field **after** editing the directory to match the location of your cloned code directory.

        -Xmx6g
        -Xms2g
        -Xss1M
        -Derrai.dynamic_validation.enabled=true
        -Dorg.uberfire.async.executor.safemode=true
        -XX:CompileThreshold=7000
        -Djava.util.prefs.syncInterval=200000
        -Dorg.uberfire.nio.git.dir=/tmp/project/dir
        -Derrai.jboss.home=/[YOUR_DIR_LOCATION]/kie-tools/packages/stunner-editors/kie-wb-common-stunner/kie-wb-common-stunner-sets/kie-wb-common-stunner-bpmn/kie-wb-common-stunner-bpmn-kogito-runtime/target/wildfly-14.0.1.Final

8.  Change _Dev Mode parameters_ to the following:

        -server org.jboss.errai.cdi.server.gwt.EmbeddedWildFlyLauncher

9.  Ensure that the _with JavaScript debugger_ checkbox is **NOT** checked, as Chrome debugger will be used instead.
10. Under _Before launch_, click the plus (+) sign.
11. Select _Run Maven Goal_.
12. Change the working directory to /[YOUR_DIR_LOCATION]/kie-tools/packages/stunner-editors/kie-wb-common-stunner/kie-wb-common-stunner-sets/kie-wb-common-stunner-bpmn/kie-wb-common-stunner-bpmn-kogito-runtime/.
13. Change _Command line_ value to 'clean process-resources', then hit _OK_.
14. Select the newly added Maven entry in the _Before launch_ section, then click the UP arrow underneath so that it moves **above** the _Make_ entry.
15. Click _Apply_ and/or _OK_.

**Running the Showcase Run/Debug Configuration**

**RECOMMENDED DAILY DEVELOPMENT PROCESS**

It is recommended that you use the following daily workflow for development around the standalone showcase application:

1.  When beginning your work day, and only _ONCE_ a day if continuing to work with the same clone/branch, issue the following command to update all necessary Maven dependencies and check for issues:

        mvn clean install -DskipTests -Dgwt.compiler.skip=true

2.  Once complete, if you did not choose _Import Maven Projects Automatically_ in previous steps, then inside of IntelliJ, right-click the project's tree root -> Maven -> Reimport and allow this to finish.
3.  Before running the showcase, you _MUST_ choose _Build_ --> _Rebuild Project_ from the menu, as this step will kick off, among other things, the annotation processors responsible for generating code required by the showcase to start up and run correctly!

**IMPORTANT - EACH TIME THE RUN/DEBUG INSTANCE IS STOPPED, REPEAT THE 'BUILD/REBUILD PROJECT' STEP BEFORE STARTING IT AGAIN!**

4. After rebuild finishes, you can now start a run/debug configuration instance by selecting the "Standalone Showcase" entry in the configuration dropdown list and hitting the green arrow (or bug) button next to it.
5. If start-up occurs successfully, the IntelliJ output window will reach a point where the _127.0.0.1:8080/stunner.html_ is displayed.
6. Open a Chrome Incognito tab and visit the provided URL.
7. Enter the 'admin'/'admin' credentials, then enter.
8. GWT compilation will now take place, after which the rest of the showcase application GUI will load.
9. To debug in Chrome, open the tool with _Ctrl+Shift+i_ (Linux/Win) or _Cmd+Opt+i_ (Mac).
10. When debugging an exception and accompanying stack trace is desired, within the debugger's Sources tab, click the _Pause on caught exceptions_ checkbox on the right-hand side.
11. To set breakpoints, use Ctrl/Cmd+O to open a file by name, then set breakpoints in the left-hand margin similarly to how it's done in IntelliJ.

Additional information on debugging with Chrome Dev Tools can be found [here](https://developers.google.com/web/tools/chrome-devtools/javascript/).

**Troubleshooting**

- If ever the showcase loads and only the top menu bar is visible, no section title/links are added to the page, you will likely also see a JavaScript error about no Home pespective being available. If this occurs, perform a _Build/Rebuild Project_, then start the run/debug process again.
- Oftentimes when working with just client/frontend GWT code, a Run can be performed in lieu of Debug and Javascript runtime compilation/hotswapping will not be affected, yet doing so will save some compilation time.

**[OPTIONAL/ADVANCED] IDEA Multi-module Environment Setup**

Once your Stunner project is setup correctly, you can also import external modules into it, for example some lienzo one, in order to be able to run the application and have all the sources served by the GWT code-server (SDM).

1. [RECOMMENDED] Build the external module from command line before importing into the project

2. File -> New -> Module from existing sources -> choose the module's root path

3. Wait until import/indexing completes

4. - File -> Project Structure
   - Modules tab -> Add a new GWT module for the new external asset imported (eg: lienzo-core) -> just select it, click on the `+` button and add a new GWT module type
   - [TIP] -> Close project preferences and reopen it, there a bug on IDEA15...
   - Artifacts tab -> click on the "exploded WAR" artifact that exists for showcase you want to use. Then on the right panel, expand the module (eg: lienzo-core) and double-click on both "compiled" and "GWT" artifacts that appear as child elements for it, they'll automatically be included in the "exploded WAR" artifact structure to generate

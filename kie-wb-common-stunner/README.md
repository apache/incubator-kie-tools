Stunner Modelling Tool
=======================

Stunner is a multi-purpose modelling tool based on [JBoss Uberfire](http://www.uberfireframework.org/). Multiple KIE designer and modeler components are developed atop Stunner.

Documentation
-------------

Various complementary documents related to Stunner development and functionality can be found in a public, shared [Google Docs folder](https://drive.google.com/open?id=0B5LZ7oQ3Bza2Qk1GY1ZPeEN6Q0E).

Building Stunner Source Code
--------

To compile all Stunner components and install to your local Maven repository, issue the following commands:

    cd kie-wb-common/kie-wb-common-stunner/
	mvn clean install -DskipTests

Running the Application
-----------------------

Execution and demonstration of the various Stunner-based components can be achieved using the provided [Showcase modules](./kie-wb-common-stunner-showcase). There are currently two showcases available, the [Stunner Project Showcase](./kie-wb-common-stunner-showcase/kie-wb-common-stunner-showcase-project) & the [Stunner Standalone Showcase](./kie-wb-common-stunner-showcase/kie-wb-common-stunner-showcase-standalone).

  - **Stunner Standalone Showcase** focuses on presenting basic UberFire (Appformer)/Errai components, such as Process Designer, skipping over the more involved & resource-intensive integration of KIE Workbench/Library and Guvnor project components.

  - **Stunner Project Showcase** is built atop the KIE Workbench, Commons, Guvnor and Library components. This more complex showcase demonstrates integration with different KIE Workbench assets and editors. However, for day-to-day development, it is strongly suggested that the Standalone Showcase be utilized over this more complex showcase when working with Stunner components.

IDE Environment setup
---------------------

What follows are the steps needed for setting up the Stunner environment with **IntelliJ IDEA 15.0**. Newer versions of IntelliJ IDEA, up to and including 2018, have known compatibility issues with the Stunner environment. For this reason, it is **strongly** recommended that you use **IntelliJ IDEA Version 15.0** which can be downloaded from the [Previous Releases](https://www.jetbrains.com/idea/download/previous.html) page.


**Prerequisite Steps for LINUX Users**

Change the _inotify_ limits configuration as noted below to avoid further issues within the IDE.

  1. Close the IDE
  2. Edit `/etc/sysctl.conf` and add the following content:

        fs.inotify.max_user_watches = 524288
        fs.inotify.max_user_instances = 524288

  3. Edit `/etc/security/limits.conf` and add the following content:

        <user> soft nofile 4096
        <user> hard nofile 10240

    E.g.:

        roger soft nofile 4096
        roger hard nofile 10240

**Prerequisite Steps for Windows Users**

__[WARNING]__ Showcase applications only work in Windows 10 Anniversary Update and above (build 1607)

1. Enable long paths in Windows
  - open regedit -> `HKEY_LOCAL_MACHINE\SYSTEM\CurrentControlSet\Control\FileSystem` -> Change `LongPathsEnabled` from `0` to `1`
2. Enable long paths in Git
  - Open GitBash -and type `git config --system core.longpaths true`
3. IDEA15 executable
  - The default setup creates a shortcut to idea.exe. Be sure to change to idea64.exe, otherwise you'll be running 32-bits version


**Before Working with the IDE**

Prior to importing the project into the IDE, assuming [kie-wb-common](../) has already been cloned locally, navigate to that directory and issue the following command:

        cd kie-wb-common/
        mvn clean install -DskipTests

**IDEA Environment Setup**

If you have not run the step from **Before Working with the IDE** above, do so before continuing.

***Importing the project into IDEA***
1. From the header menu, select **File** --> **New** --> **Project from Existing Sources...**
2. Using the directory tree, navigate to and select the **pom.xml** file within the **kie-wb-common/kie-wb-commmon-stunner** directory and hit **OK**.
3. At the bottom of the new dialog box, click the **Environment Settings...** button.
4. Use the '**...**' button next to the **Maven home directory** field to locate and select your local Maven (3.3.9+) installation, rather than the "Bundled" prefilled value.
5. Select **OK** to close the Maven environment popup dialog.
6. From hereout, click **Next** 3 times, which should lead to a SDK selection screen. Ensure that your **JDK home path** reflects a Java instance of 1.8 (recommended JDK) or above.
7. Click **Next** once more, then **Finish**.
8. At this point, the project is loading into the IDE. Note that it will take some time to fully load and index.

***Configure Maven in the IDE***
1. Open **Preferences** from the **File** (linux/Win) menu or **IntelliJ IDEA** app menu (mac).
2. In the search field, type *maven*.
3. Use the '**...**' button next to the *Maven home directory* field to locate and select your local Maven (3.3.9+) installation, rather than the "Bundled" prefilled value.
4. In the left-hand navigation tree, select *Importing* underneath *Maven*.
5. Change the value of *VM options for importer* to *-Xmx2048m*.
6. Change the *JDK for importer* field to reflect your local Java JDK installation.
7. (**OPTIONAL**) check the *Import maven projects automatically* option if desired.
8. Click *Apply* and/or *OK*.

***Configure Code Styling***
1. Download the [KIE code-style](https://github.com/kiegroup/droolsjbpm-build-bootstrap/blob/master/ide-configuration/intellij-configuration/code-style/intellij-code-style_droolsjbpm-java-conventions.xml) XML configuration file.
2. Open **Preferences** from the **File** (linux/Win) menu or **IntelliJ IDEA** app menu (mac).
3. In the search field, type '*code style*'.
4. To the right of the *Scheme* field, locate and click the *Manage...* button.
5. Click *Import*.
6. In the *Import From* popup, select "IntelliJ IDEA code style XML". If this option is not available, proceed to **Manually Importing the Code Style** below at this time.
7. Navigate to the downloaded XML file and select it.
8. Click *Apply* and/or *OK*.

***Manually Importing the Code Style***

If you were able to use the IDE GUI to import the code style successfully above, skip this section.
1. If you were not able to select the "IntelliJ IDEA code style XML" option, close the IDE.
2. Refer to [this support entry](https://intellij-support.jetbrains.com/hc/en-us/articles/206544519-Directories-used-by-the-IDE-to-store-settings-caches-plugins-and-logs) to identify the Configuration directory used by IntelliJ IDEA for your operating system.
3. Copy the downloaded code style XML file to the *[CONFIG_LOCATION]/codestyles* directory.
4. Start the IDE again, navigate to *Code Styles* within *Preferences*, and "KIE Java Conventions" should now be available in the *Scheme* dropdown selector.
5. Select this option, then click *Apply* and/or *OK*.

***Configure the File Header Template***
1. Open **Preferences** from the **File** (linux/Win) menu or **IntelliJ IDEA** app menu (mac).
2. In the search field, type '*file and code templates*'.
3. Select the *Includes* tab.
4. Select *File Header* in the left-hand list.
5. Remove any line in the template referencing @author, as KIE convention is to omit this in lieu of Git history information.

***Configure Copyright Template***
1. Open **Preferences** from the **File** (linux/Win) menu or **IntelliJ IDEA** app menu (mac).
2. In the search field, type '*copyright*'.
3. Select *Copyright Profiles*, located under *Copyright*.
4. Click the plus (+) sign in the top-left corner, then enter "KIE Copyright" in the name field in the popup.
5. Replace the contents of the *Copyright text* field with the contents of the [KIE licensing file](https://raw.githubusercontent.com/kiegroup/droolsjbpm-build-bootstrap/master/ide-configuration/LICENSE-ASL-2.0-HEADER.txt).
6. Within the newly pasted text, replace "*${year}*" with "*$today.year*", then click *Apply*.
7. In the left-hand navigation tree, select *Copyright*, the parent node of the currently screen item.
8. Use the *Default project copyright* dropdown to select the newly created "*KIE Copyright*" value.
9. Click *Apply* and/or *OK*.

***Configure GWT Facets***
1. If you don't already have a local copy of the GWT SDK, download the latest version [from here](http://www.gwtproject.org/download.html) and extract it somewhere safe where you keep other SDK's.
2. From the menu, click *View*, then select *Open Module Settings*.
3. In the left-hand *Project Settings* list, select *Facets*.
4. In the left-hand facet list, select *GWT*.
5. Ensure the *Defaults* tab is selected at the top.
6. Change the *Path to GWT installation directory* to point to your local GWT SDK installation.
7. Change the *JavaScript output style* option to *Detailed*.
8. Change the *Compiler maximum heap size* to '4096'.
9. Select the *All Facets* tab at the top and repeat steps 6-8.

***Setting up a Showcase Run/Debug Configuration***

The following assumes that the Standalone Showcase will be utilized. Some evident name/directory adjustments are required in order to run the Project Showcase instead, although doing so is not typically recommended (see above).
1. From the menu, click *Run*, then select *Edit Configurations...*
2. Click the plus (+) sign in the upper left-hand corner, then select *GWT Configuration*.
3. Change *Name* to '*Standalone Showcase*'.
4. Use the *Module:* dropdown to select '*kie-wb-common-stunner-showcase-standalone*'.
5. Click the *Use Super Dev Mode* checkbox to enable if not already checked.
6. Change *GWT Modules to load* to '*org.kie...standalone.FastCompiledStunnerStandaloneShowcase*'.
7. Paste the following into the *VM Options* field **after** editing the directory to match the location of your cloned code directory.

        -Xmx6g
        -Xms2g
        -Xss1M
        -Derrai.dynamic_validation.enabled=true
        -Dorg.uberfire.async.executor.safemode=true
        -XX:CompileThreshold=7000
        -Djava.util.prefs.syncInterval=200000
        -Dorg.uberfire.nio.git.dir=/tmp/project/dir
        -Derrai.jboss.home=/[YOUR_DIR_LOCATION]/kie-wb-common/kie-wb-common-stunner/kie-wb-common-stunner-showcase/kie-wb-common-stunner-showcase-project/target/wildfly-14.0.1.Final
        -Derrai.jboss.args="-b 0.0.0.0 -bmanagement 0.0.0.0"

8. Change *Dev Mode parameters* to the following:

        -server org.jboss.errai.cdi.server.gwt.EmbeddedWildFlyLauncher

10. Ensure that the *with JavaScript debugger* checkbox is **NOT** checked, as Chrome debugger will be used instead.
11. Under *Before launch*, click the plus (+) sign.
12. Select *Run Maven Goal*.
13. Change the working directory to /[YOUR_DIR_LOCATION]/kie-wb-common/kie-wb-common-stunner/.
14. Change *Command line* value to 'clean process-resources', then hit *OK*.
15. Select the newly added Maven entry in the *Before launch* section, then click the UP arrow underneath so that it moves **above** the *Make* entry.
16. Click *Apply* and/or *OK*.

**Running the Showcase Run/Debug Configuration**


**RECOMMENDED DAILY DEVELOPMENT PROCESS**

It is recommended that you use the following daily workflow for development around the standalone showcase application:

1. When beginning your work day, and only *ONCE* a day if continuing to work with the same clone/branch, issue the following command to update all necessary Maven dependencies and check for issues:

        mvn clean install -DskipTests -Dgwt.compiler.skip=true

2. Once complete, if you did not choose *Import Maven Projects Automatically* in previous steps, then inside of IntelliJ, right-click the project's tree root -> Maven -> Reimport and allow this to finish.
3. Before running the showcase, you *MUST* choose *Build* --> *Rebuild Project* from the menu, as this step will kick off, among other things, the annotation processors responsible for generating code required by the showcase to start up and run correctly!

__IMPORTANT - EACH TIME THE RUN/DEBUG INSTANCE IS STOPPED, REPEAT THE 'BUILD/REBUILD PROJECT' STEP BEFORE STARTING IT AGAIN!__

4. After rebuild finishes, you can now start a run/debug configuration instance by selecting the "Standalone Showcase" entry in the configuration dropdown list and hitting the green arrow (or bug) button next to it.
5. If start-up occurs successfully, the IntelliJ output window will reach a point where the *127.0.0.1:8080/stunner.html* is displayed.
6. Open a Chrome Incognito tab and visit the provided URL.
7. Enter the 'admin'/'admin' credentials, then enter.
8. GWT compilation will now take place, after which the rest of the showcase application GUI will load.
9. To debug in Chrome, open the tool with *Ctrl+Shift+i* (Linux/Win) or *Cmd+Opt+i* (Mac).
10. When debugging an exception and accompanying stack trace is desired, within the debugger's Sources tab, click the *Pause on caught exceptions* checkbox on the right-hand side.
11. To set breakpoints, use Ctrl/Cmd+O to open a file by name, then set breakpoints in the left-hand margin similarly to how it's done in IntelliJ.

Additional information on debugging with Chrome Dev Tools can be found [here](https://developers.google.com/web/tools/chrome-devtools/javascript/).

**Troubleshooting**

  - If ever the showcase loads and only the top menu bar is visible, no section title/links are added to the page, you will likely also see a JavaScript error about no Home pespective being available. If this occurs, perform a *Build/Rebuild Project*, then start the run/debug process again.
  - Oftentimes when working with just client/frontend GWT code, a Run can be performed in lieu of Debug and Javascript runtime compilation/hotswapping will not be affected, yet doing so will save some compilation time.

**[OPTIONAL/ADVANCED] IDEA Multi-module Environment Setup**

Once your Stunner project is setup correctly, you can also import external modules into it, for example some lienzo one, in order to be able to run the application and have all the sources served by the GWT code-server (SDM).

1.- [RECOMMENDED] Build the external module from command line before importing into the project

2.- File -> New -> Module from existing sources -> choose the module's root path

3.- Wait until import/indexing completes

4.- File -> Project Structure
  - Modules tab -> Add a new GWT module for the new external asset imported (eg: lienzo-core) -> just select it, click on the `+` button and add a new GWT module type
  - [TIP] -> Close project preferences and reopen it, there a bug on IDEA15...
  - Artifacts tab ->  click on the "exploded WAR" artifact that exists for showcase you want to use. Then on the right panel, expand the module (eg: lienzo-core) and double click on both "compiled" and "GWT" artifacts that appear as child elements for it, they'll automatically be included in the "exploded WAR" artifact structure to generate



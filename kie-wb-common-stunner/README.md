Stunner Modelling Tool
=======================

A multi-purpose modelling tool based on [JBoss Uberfire](http://www.uberfireframework.org/).                         

Documentation
-------------

All Stunner documents are shared in a public Google Docs folder [here](https://drive.google.com/open?id=0B5LZ7oQ3Bza2Qk1GY1ZPeEN6Q0E).

Building
--------

Follow these instructions for building the application from sources.            

    cd kie-wb-common/kie-wb-common-stunner/
	mvn clean install -DskipTests

Running the application
-----------------------

Use any of the available showcases in [the showcase module](./kie-wb-common-stunner-showcase).                      

IDE Environment setup
---------------------

Here are the instructions for setting up the Stunner environment on **IntelliJ IDEA 2015**.


**Previous Steps**

- For linux users - change the _inotify_ limits configuration to avoid further issues with the IDE
  - Close the IDE
  - Edit `/etc/sysctl.conf` and add the following content:

        fs.inotify.max_user_watches = 524288
        fs.inotify.max_user_instances = 524288

  - Edit `/etc/security/limits.conf` and add the following content:

        <user> soft nofile 4096
        <user> hard nofile 10240

    E.g.:

        roger soft nofile 4096
        roger hard nofile 10240


**IDEA Environment Setup**

1.- Import the project into IDEA
  - File -> New -> Project from existing sources
  - Use [kie-wb-common-stunner](./) as for the project's root folder (instead of the whole [kie-wb-common](../) one)
  - Import using _from Maven_ option
  - Use all default settings in the wizard, so just click on next, next, next and remember to use Java8+
  - Once the wizard is completed, It will take a while to load and index


2.- Open the _Settings_ from the _File_ menu
  - *Maven*:
    - Do not use the built-in JDK given by default, use the one from your system path, which must be version 3.3.9+
    - [TIP] In the Maven -> Importing - Also use your Maven version, instead of the default one, and increase the default given heap size in the `VM options for importer` field
  - *Version control*
    - Configure Version Control (Git) root folder
    - Do not use the built-in JDK given by default, use the one from your system path, which must be version 3.3.9+
  - *Code style*
    - Download the [KIE code-style](https://github.com/kiegroup/droolsjbpm-build-bootstrap/tree/master/ide-configuration) XML file
    - Settings -> Code Style -> Scheme -> Manage -> Import
  - *License (copy-right)*
    - Follow the [IntelliJ installation instructions](https://github.com/kiegroup/droolsjbpm-build-bootstrap#configuring-intellij) at section _Set the correct license header_

3.- Configure the GWT run configurations
  - Choose a showcase and follow [this instructions](https://github.com/kiegroup/kie-wb-common/tree/master/kie-wb-common-stunner/kie-wb-common-stunner-showcase) for creating an application's run configuration


**[OPTIONAL/ADVANCED] IDEA Multi-module Environment Setup**

Once your Stunner project is setup correctly, you can also import external modules into it, for example some lienzo one, in order to be able to run the application and have all the sources served by the GWT code-server (SDM).

1.- [RECOMMENDED] Build the external module from command line before importing into the project

2.- File -> New -> Module from existing sources -> choose the module's root path

3.- Wait until import/indexing completes

4.- File -> Project Structure
  - Modules tab -> Add a new GWT module for the new external asset imported (eg: lienzo-core) -> just select it, click on the `+` button and add a new GWT module type
  - [TIP] -> Close project preferences and reopen it, there a bug on IDEA15...
  - Artifacts tab ->  click on the "exploded WAR" artifact that exists for showcase you want to use. Then on the right panel, expand the module (eg: lienzo-core) and double click on both "compiled" and "GWT" artifacts that appear as child elements for it, they'll automatically be included in the "exploded WAR" artifact structure to generate


**Working with the environment**

This section assumes the Stunner environment is already setup as the above steps describe. At this point, the user is ready for starting the development, so here are a few recommendations about how to successfully build and run the application also using SDM.

[INFO] This section assumes some GWT run configuration has been already created and configured for any of the Stunner's showcases. See [the showcase module](./kie-wb-common-stunner-showcase) for more information.

0.- [RECOMMENDED] Once a day, build from the command line (`mvn clean install -DskipTests`) in order to ensure all snapshots are already downloaded (it's faster than using the IDE)

1.- Open the project in IDEA

2.- Right click on the project's tree root -> Maven -> Reimport (will take a while, although snapshots have already been downloaded on the step 0)

3.- Menu Build -> Rebuild Project

4.- [RECOMMENDED] Before running the app, remove the GWT generated stuff at `~/.IntellijIdeaXX/system/gwt/*`, if present

5.- Run the showcase (or debug)

6.- Test, debug and change code - just refresh the browser to "reload" the client side changed code

7.- Stop the server

8.- Code, code code and once ready to run again, just **go back to step 3 (build, clean caches, etc) and run again**

[TIP] Notice the GWT IDEA plugin does not work good in IDEA15, so it's recommended to disable the `with Javascript debugger` option from the run configuration. Just use the Chrome debugger for client side stuff.

[TIP] Use the `kie-wb-common-showcase-standalone` as much as possible, it's smaller than the `kie-wb-common-showcase-project` one, so faster to work with.


**Setup on Windows environments**

Here are some additional notes if you're using Windows:

- Enable long paths in Windows
  - open regedit -> `HKEY_LOCAL_MACHINE\SYSTEM\CurrentControlSet\Control\FileSystem` -> Change `LongPathsEnabled` from `0` to `1`
- Enable long paths in Git
  - Open GitBash -and type `git config --system core.longpaths true`
- IDEA15 executable
    - The default setup creates a shortcut to idea.exe. Be sure to change to idea64.exe, otherwise you'll be running 32-bits version

[WARNING] It only works in Windows 10 Anniversary Update and above (build 1607)


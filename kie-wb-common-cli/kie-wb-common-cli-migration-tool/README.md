KIE Workbench Migration Tool
============================

This project contains a command-line tool to run different KIE Workbench migrations. This document briefly explains how to build and use the tool.

Included Migration Tools
------------------------

* **Project Migration Tool:** migrates KIE projects from the old project layout (7.4.x and previous) to the new project-oriented structure.

* **System Configuration Migration Tool:** migrates the previously used directory structure into the new one. Requires **Project Migration Tool** to be executed first.

* **Forms Migration Tool:** migrates old jBPM Form Modeler forms into the new Forms format. Requires **Project Migration Tool** and **System Configuration Migration Tool:** to be executed first.

Maven Build
-----------

To build the project, simply run

    mvn clean install

This will generate a zip file in the target directory:

    kie-wb-common-cli-migration-tool-$VERSION-dist.zip

The zip file is a standalone artifact that contains all the necessary dependencies to run the CLI tool.

(NOTE: The tool requires Java to be installed and available on the path.)

Installing the Tool
-------------------

To install the tool, simply unzip it into the desired location, like so:

    unzip -d $INSTALL_DIR kie-wb-common-cli-migration-tool-$VERSION-dist.zip

This should create a directory (`kie-wb-common-cli-migration-tool-$VERSION`) in the `$INSTALL_DIR`.

Using the Tool
--------------

The tool is meant to operate on the `.niogit` directory of a workbench distribution. *IMPORTANT*: there should not be a running workbench using this directory when using the tool.

For the instructions below, these variables are assumed:

* `$NIOGIT` is the path of your `.niogit` directory being migrated.
* `$TOOL_DIR` is the path to the directory installed in the previous section (i.e. `$INSTALL_DIR/kie-wb-common-cli-migration-tool-$VERSION/`).

Simple Usage
------------

The simplest way to invoke the tool is

Linux -
    $TOOL_DIR/bin/migration-tool.sh -t $NIOGIT


Windows -
    $TOOL_DIR\bin\migration-tool.bat -t $NIOGIT


The tool will show a Wizard to choose between:

* Run one of the available migration tools (Project, System Configuration or Forms Migration)
* Run all the available migration tools sequentially.
* Exit the migration tool. 

The tool will do the migration in-place: when the tool finishes a successful run, the `$NIOGIT` directory will be ready for use with the new workbench.

*VERY IMPORTANT*: Because the tool migrates in-place, it's important to make backups before use.

Batch Mode
----------

The simple invocation will prompt the user to confirm before migration is attempted. This is done to warn users about making backups, in case an error occurs while migrating project data.

If you wish to run the tool without this prompt, you can add the `-b` flag like so:

Linux -
    ```
    $TOOL_DIR/bin/migration-tool.sh -b -t $NIOGIT
    ```

Windows -
    ```
    $TOOL_DIR\bin\migration-tool.bat -b -t $NIOGIT
    ```

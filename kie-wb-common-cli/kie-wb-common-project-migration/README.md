Project Migration Tool
======================

This project contains a command-line tool for migrating KIE projects from the old project layout (7.4.x and previous) to the new project-oriented structure. This document briefly explains how to build and use the tool.

Maven Build
-----------

To build the project, simply run

    mvn clean install

This will generate a zip file in the target directory:

    kie-wb-common-project-migration-$VERSION-linux.zip

The zip file is a standalone artifact that contains all the necessary dependencies to run the CLI tool.

(NOTE: The tool requires Java to be installed and available on the path.)

Installing the Tool
-------------------

To install the tool, simply unzip it into the desired location, like so:

    unzip -d $INSTALL_DIR kie-wb-common-project-migration-$VERSION-linux.zip

This should create a directory (`kie-wb-common-project-migration-$VERSION`) in the `$INSTALL_DIR`.

Using the Tool
--------------

The tool is meant to operate on the `.niogit` directory of a workbench distribution. *IMPORTANT*: there should not be a running workbench using this directory when using the tool.

For the instructions below, these variables are assumed:

* `$NIOGIT` is the path of your `.niogit` directory being migrated.
* `$TOOL_DIR` is the path to the directory installed in the previous section (i.e. `$INSTALL_DIR/kie-wb-common-project-migration-$VERSION/`).

Simple Usage
------------

The simplest way to invoke the tool is

    $TOOL_DIR/bin/migration-tool.sh -t $NIOGIT

The tool will migrate projects in-place: when the tool finishes a successful run, the `$NIOGIT` directory will be ready for use with the new workbench.

*IMPORTANT*: Because the tool migrates in-place, it's important to make backups before use.

Batch Mode
----------

The simple invocation will prompt the user to confirm before migration is attempted. This is done to warn users about making backups, in case an error occurs while migrating project data.

If you wish to run the tool without this prompt, you can add the `-b` flag like so:


    $TOOL_DIR/bin/migration-tool.sh -b -t $NIOGIT

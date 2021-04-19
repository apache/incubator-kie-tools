# Contributing to Errai

Errai is a fully open source project (licensed under [Apache Version 2.0](http://www.apache.org/licenses/LICENSE-2.0)). It is being developed with and for the community and sponsored by Red Hat. Community contributions are very important to us and we value all feedback. This document outlines the ways of contributing to Errai and how they are accomplished.

## Reporting Bugs

One of the simplest ways you can help improve Errai is reporting issues you experience. You should file an issue as a bug when there is unexpected behaviour that you think results from an error in framework code. If ever you are uncertain whether some behaviour is intentional or a bug, you can ask first on our [forum](https://developer.jboss.org/en/errai) or on the #errai IRC channel on freenode.

### Creating an Issue

1. Go to the Errai [Issue Tracker](https://issues.jboss.org/projects/ERRAI).
2. Click the green "Create" button in the navigation bar. A modal form will appear.
3. Below are the fields we are most interested in and guidelines for what to enter.
  * Project

    Should be set to *Errai*.

  * Issue Type

    Should be set to *Bug*.

  * Summary

    Try to be as concise and descriptive as possible. For example, "Message bus not initializing in Firefox for Mac" is better than "Message bus issue".

  * Affects Version

    If you experience an issue in multiple versions, please only enter the most recent version.

  * Environment

    Put whatever details of the enivronment where you encountered the bug that might be relevant. This might include:
    * Operating System
    * Web Browser
    * JDK Version
    * GWT Version

  * Description

    In this section describe how to reproduce the bug, and how the actual behaviour differs from the expected behaviour.

  * Steps to Reproduce

    This field can be omitted if steps to reproduce are included in the description.

  * Workaround

    This field is not necessary, but if you have a workaround for an issue you can enter it here.

## Contributing Code

Errai source code is hosted in our [GitHub repo](https://github.com/errai/errai). To contribute code to Errai, you need a GitHub account and a fork of Errai on GitHub so that you can send a [pull request](https://help.github.com/articles/using-pull-requests/).

### Contributing a Bug Fix

Here is the general process of contributing a bug fix to Errai:

1. If one does not already exist, [file an issue](https://issues.jboss.org/projects/ERRAI) for the bug.

2. Let us know you are working on a fix. You can do this with a comment on the JIRA issue or by some other communication with one of the core developers.

3. Submit a pull request (details below).

### Contributing a Feature or Enhancement

As a web framework it is important that the features in Errai provide utility to general or common use cases. As such contributing a new feature involves soliciting more feedback. Here is an outline of the process assuming you have a new feature that you would like to submit:

1. Solicit feedback on your idea. To do this properly, there are a couple things you should do.
  * Create a [feature request](https://issues.jboss.org/projects/ERRAI). This is the primary place where discussion regarding the proposed feature will be documented.
  * Make a [forum post](https://developer.jboss.org/en/errai) describing your proposed feature. (This is optional, but can help you get more feedback.)
  * Discuss the proposed feature with one of the core developers (through one of the mediums above or IRC).

  This step is very important. There are some features that are very useful for a few particular use cases but are not generic enough to belong in a general purpose web framework. By consulting with the core-developers and the community early on, these cases can be identified early on before significant development time has been invested.

2. Let us know you are working on a fix. You can do this with a comment on the JIRA issue or by some other communication with one of the core developers.

3. Submit a pull request (details below).

### Pull Request Guidelines

When submitting a pull request to Errai, please:

* Submit it against the Errai *master* branch whenever possible. We will cherry-pick important changes (such as critical bug fixes) to branches for older releases where appropriate.

* Successfully run a full build (with integration tests) before sending the PR.

  To do this execute this maven command in the parent project folder:
  `mvn clean install -Pintegration-test`.

  To run a single test (for example "testMethodName" in the class "TestClassName"), `cd` into the directory of the project in which the test resides and run the previous command with this additional argument:
  `-Dit.test=TestClassName#testMethodName`

* Squash your changes into a single commit.

* All changes should come with tests. For bug fixes a single regression test (a test reproducing the original bug) should suffice. New features must come with integration tests (they may have unit tests as well, but these cannot replace integration tests).

* Any new or modified public API must have javadoc.

* New features should also come with documentation (see how to contribute documentation below).

* Follow the Errai code style (details below).

After you submit a pull request it will be reviewed by one of the core developers. If changes are required, the reviewer will leave comments with feedback and leave the request open.

The best way to update a pull request with changes is to [amend the commit](https://www.atlassian.com/git/tutorials/rewriting-history). Pushing an amended commit to your Errai fork will update the PR automatically, at which point you can comment on the PR letting the reviewer know. Once the reviewer is satisfied, the PR will be merged and closed.

### Code Style

Eclipse users can import the formatting rules for Errai from `errai_eclipse_format_config.xml` in the project root directory. Otherwise, please ensure your code satisfies the following:

* Use 2-spaces for indentation.
* Maximum 120 characters line-width (for code and javadoc).
* No whitespace between parentheses and arguments in method declarations and calls.

## Contributing Documentation

The documentation for Errai is contained in the `errai-docs` project in the Errai repository. It is written in asciidoc. To add or contribute documentation, simply submit a pull request with changes to this file: [errai-docs/src/main/asciidoc/reference.asciidoc](errai-docs/src/main/asciidoc/reference.asciidoc)

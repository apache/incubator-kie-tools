Developing Drools and jBPM
==========================

**If you want to build or contribute to a droolsjbpm project, [read this document](https://github.com/droolsjbpm/droolsjbpm-build-bootstrap/blob/master/README.md).**

**It will save you and us a lot of time by setting up your development environment correctly.**
It solves all known pitfalls that can disrupt your development.
It also describes all guidelines, tips and tricks.
If you want your pull requests (or patches) to be merged into master, please respect those guidelines.

Working in IntelliJ Idea
========================
Idea tends to pick up the test classes and resources from uberfire-workbench and break the uberfire-showcase build. These resources include Screens that are broken for testing purposes.
The fix for this is to exclude the test resources manually. You can do this in File->Settings->Compiler->Ecludes.

Pulling and fixing translations from Zanata
===========================================

mvn zanata:pull-module

mvn replacer:replace -N

Commit any changes made.
 

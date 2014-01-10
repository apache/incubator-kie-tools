UberFire
========

This is UberFire, a Rich Client Platform for the Web.

UberFire provides declarative APIs for building workbench-style apps,
a rich virtual file system with a jgit backend, full-text search, a
role-based declarative security framework, and more.

For more information about UberFire, please check out our web site at
http://uberfireframework.org/.

Working in IntelliJ Idea
========================
Idea tends to pick up the test classes and resources from uberfire-workbench and break the uberfire-showcase build. These resources include Screens that are broken for testing purposes.
The fix for this is to exclude the test resources manually. You can do this in File->Settings->Compiler->Ecludes.

Pulling and fixing translations from Zanata
===========================================

mvn zanata:pull-module

mvn replacer:replace -N

Commit any changes made.
 

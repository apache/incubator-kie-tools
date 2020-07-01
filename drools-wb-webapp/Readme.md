Maven SDM
=========

The drools-wb-webapp/pom.xml file has been modified to allow hot-reload (so-called SuperDev Mode) of included client code when launched directly from maven.</p>
This would allow to develop independently from any given ide/plugin.</p>
Currently *managed* code are the client submodules of **drools-wb-screens**.</p>
The *trick* used is to just add the actual sources directories as additional *sources* using the **org.codehaus.mojo/build-helper-maven-plugin**.</p>
For the **drools-wb-screens** it has been possible to set a relative directory.
The jars included as additional sources should be set as **provided** in the **dependencies** list and  removed from the **compileSourcesArtifacts** list.

NOTE
----
Be sure you increased your inotify limit before start the project. See [here](https://github.com/kiegroup/kie-wb-common/tree/master/kie-wb-common-stunner#ide-environment-setup) for more detail.



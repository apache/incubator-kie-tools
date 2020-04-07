Kogito Testing (Development) Webapp
==================================

This webapp is meant as testing/developing environment. It **provides** buttons, methods and functions (ex. file retrieval) that should be provided by running environment (ex. vs code).

Testing
--------------
1. Full compilation (i.e. included GWT compilation) of the testing module
2. Launch mvn gwt:run from `drools-wb/drools-wb-screens/drools-wb-scenario-simulation-editor/drools-wb-scenario-simulation-editor-kogito-testing/` directory
3. Open a browser (Chrome or Firefox are supported) and open `http://127.0.0.1:8888/index.html` url
4. A popup with this message `Compiling org.drools.workbench.screens.scenariosimulation.webapp.DroolsWorkbenchScenarioSimulationKogitoTesting` will appear. 
   GWT module is compiling, please wait.
5. An empty scenario simulation testing will appear. Here, you can create a new asset pressing
   `New` button in the menu. You'll be able to create a **RULE** scenario (with mocked data), 
   and a **DMN** scenario. In this last case, you need to upload a DMN file first, using the 
   `Import DMN` button.   
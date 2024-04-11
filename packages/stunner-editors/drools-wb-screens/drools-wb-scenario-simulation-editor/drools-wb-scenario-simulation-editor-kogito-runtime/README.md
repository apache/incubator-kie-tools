# Kogito Runtime Webapp

This webapp is meant as runtime environment. It **does not provides** methods and functions (ex. file retrieval) that should be provided by running environment (ex. vs code).

## Chrome testing

Some minimal tests may be done in the following way:

1. full compilation (i.e. included GWT compilation) of the runtime module
2. open the `drools-wb/drools-wb-screens/drools-wb-scenario-simulation-editor/drools-wb-scenario-simulation-editor-kogito-runtime/target/drools-wb-scenario-simulation-editor-kogito-runtime/index.html` file inside Chrome
3. inside the Chrome dev console, issue the command `$wnd.gwtEditorBeans.get("ScenarioSimulationEditor").get().setContent("", "")`; this will create a new - empty - scesim file.
4. Some tricks: to avoid CORS and other policy-related issues:
   - enable chrome://flags/#temporary-unexpire-flags-m118 flag
   - set chrome://flags/#allow-insecure-localhost for invalid certificates error
   - start chrome from cli with the command `chrome --allow-file-access-from-files` to allow loading from file.
   - Open index.html file:///(path_to_file)/index.html and get no dev compilation error.
5. $wnd.gwtEditorBeans.get("ScenarioSimulationEditor").get().setContent("") inside dev console
6. edit the asset
7. invoke $wnd.gwtEditorBeans.get("ScenarioSimulationEditor").get().getContent() inside dev console and store the returned xml
8. invoke $wnd.gwtEditorBeans.get("ScenarioSimulationEditor").get().setContent("","") method with the stored xml

## VSCODE Integration

1. clone https://github.com/apache/incubator-kie-tools
2. Launch pnpm bootstrap on incubator-kie-tools directory
3. Launch pnpm -r build:dev
4. Launch VS Code (you can find it in a path similar to: `kie-tools/packages/kie-editors-dev-vscode-extension/.vscode-test/vscode-1.43.0/VSCode-linux-x64/bin/code` ) with `--enable-proposed-api kiegroup.kie-editors-dev-vscode-extension` parameter
5. In VS Code, open kie-tools/packages/kie-editors-dev-vscode-extension/ folder
6. Launch DEBUG MODE (F5)
7. Try to open a SCESIM file or to create a new one

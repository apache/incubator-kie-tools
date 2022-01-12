# Kogito DMN Editor - Webapp

## Building

- Before building this webapp, you should build the whole of the Stunner project before
  - change to the `kie-wb-common/kie-wb-common-stunner` root folder
  - run `mvn clean install -DskipTests -Dgwt.compiler.skip=true`

## Running in Wildfly

- Copy the generated file `target/kie-wb-common-dmn-webapp-kogito-runtime.war` into `$WILDFLY_ROOT/standalone/deployments`
- Rename the deployed WAR as `ROOT.war`
- Run the Wildfly instance: `./$WILDFLY_ROOT/bin/standalone.sh`
- Navigate to `http://localhost:8080`

## Running in SDM

Start GWT super dev mode by: `mvn gwt:run`

## Running in IntelliJ

Create a new Run/Debug GWT configuration as:

Module: `kie-wb-common-dmn-webapp-kogito-runtime`

User Super Dev Mode: `true`

VM Options:

        -Xmx8G
        -Xms1024m
        -Xss1M
        -Derrai.dynamic_validation.enabled=true

[OPTIONAL] Dev Mode Parameters:

        -style PRETTY
        -generateJsInteropExports
        -logLevel [ERROR, WARN, INFO, TRACE, DEBUG, SPAM, or ALL]

Start page: `test.html`

## Usage

- Test Page (context path root)

Navigate to the context path root (eg: `http://localhost:8080`)

It provides buttons for creating new diagrams, opening an existing diagram and exporting it

- Also the editor can be used using Javascript API:

        // For creating a diagram
        window.frames.editorFrame.contentWindow.gwtEditorBeans.get("DMNDiagramEditor").get().setContent("", "")

        // For loading a diagram (the raw xml)
        window.frames.editorFrame.contentWindow.gwtEditorBeans.get("DMNDiagramEditor").get().setContent("", raw)

        // Get the actual diagram' content
        window.frames.editorFrame.contentWindow.gwtEditorBeans.get("DMNDiagramEditor").get().getContent()

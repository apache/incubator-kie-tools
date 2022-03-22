# Kogito SW Editor - Webapp

## Building

- Before building this webapp, you should build the whole of the Stunner project before
  - change to the `kogito-tooling/packages/stunner-editors/kie-wb-common-stunner` root folder
  - run `mvn clean install -DskipTests -Dgwt.compiler.skip=true`
- Build the webapp:
  - Production: `mvn -T 8C clean install -DskipTests=true`
  - Dev: `mvn -T 8C clean install -DskipTests=true -Ddev`
  - Dev+SourceMaps: `mvn -T 8C clean install -DskipTests=true -Dsources`

## Running in Wildfly

- Copy the generated file `target/sw-editor-kogito-app.war` into `$WILDFLY_ROOT/standalone/deployments`
- Rename the deployed WAR as `ROOT.war`
- Run the Wildfly instance: `./$WILDFLY_ROOT/bin/standalone.sh`
- Navigate to `http://localhost:8080`

## Running in SDM

Start GWT super dev mode by: `mvn gwt:run`

## Running in IntelliJ

Create a new Run/Debug GWT configuration as:

Module: `sw-editor-kogito-app`

GWT Module: `org.kie.workbench.common.stunner.sw.KogitoSWEditor`

User Super Dev Mode: `true`

VM Options:

        -Xmx8G
        -Xms1024m
        -Xss1M
        -Derrai.dynamic_validation.enabled=true
        -Derrai.ioc.jsinterop.support=true

[OPTIONAL] Dev Mode Parameters:

        -style PRETTY
        -generateJsInteropExports
        -logLevel [ERROR, WARN, INFO, TRACE, DEBUG, SPAM, or ALL]

Start page: `test.html`

## Kogito Editor API

    // For creating a process
    window.frames.editorFrame.contentWindow.gwtEditorBeans.get("BPMNDiagramEditor").get().setContent("", "")

    // For loading a process (the raw xml)
    window.frames.editorFrame.contentWindow.gwtEditorBeans.get("BPMNDiagramEditor").get().setContent("", raw)

    // Get the actual process' content
    window.frames.editorFrame.contentWindow.gwtEditorBeans.get("BPMNDiagramEditor").get().getContent()

## Low level JsInterop API

    // Lienzo JS API
    var jsl = window.frames.editorFrame.contentWindow.canvas
    jsl.getNodeIds() // Get id of all nodes
    jsl.getBackgroundColor('_A9481DBC-3E87-40EE-9925-733B24404BC0')         // gets background color
    jsl.setBackgroundColor('_A9481DBC-3E87-40EE-9925-733B24404BC0', 'blue') // sets background color
    jsl.getBorderColor('_A9481DBC-3E87-40EE-9925-733B24404BC0')             // gets border color
    jsl.setBorderColor('_A9481DBC-3E87-40EE-9925-733B24404BC0', 'red')      // sets border color
    jsl.getLocation('_A9481DBC-3E87-40EE-9925-733B24404BC0', 'red')         // gets location
    jsl.getAbsoluteLocation('_A9481DBC-3E87-40EE-9925-733B24404BC0', 'red') // gets absolute location
    jsl.getDimensions('_A9481DBC-3E87-40EE-9925-733B24404BC0', 'red')       // gets dimensions

    // Lienzo Wires Shapes API
    var jsl = window.frames.editorFrame.contentWindow.canvas
    jsl.log().logWiresShapes()
    var s = jsl.getWiresShape('_A9481DBC-3E87-40EE-9925-733B24404BC0')
    s.getChild(1).fillColor = "red"

    // Lienzo Events API
    jsl.events().click(jsl.getShape('redRectangle'))
    jsl.events().drag(jsl.getShape('redRectangle'), 400, 400, () => console.log('DONE DRAG'))

    // SW Editor API (JsSWDiagramEditor)
    var sweditor = window.frames.editorFrame.contentWindow.sweditor
    sweditor.logNodes();

# Kogito Process Editor - Webapp

For detailed instructions on how to configure your development environment please refer to
the [Stunner directory README documentation](../../../).

## Building

- Before building this webapp, you should build the whole of the Stunner project before
  - change to the `kie-tools/packages/stunner-editors/kie-wb-common-stunner` root folder
  - run `mvn clean install -DskipTests -Dgwt.compiler.skip=true`
- Build the webapp:
  - Production: `mvn -T 8C clean install -DskipTests=true`
  - Dev: `mvn -T 8C clean install -DskipTests=true -Ddev`
  - Dev+SourceMaps: `mvn -T 8C clean install -DskipTests=true -Dsources`

## Running in Wildfly

- Copy the generated file `target/kie-wb-common-stunner-bpmn-kogito-runtime.war` into `$WILDFLY_ROOT/standalone/deployments`
- Rename the deployed WAR as `ROOT.war`
- Run the Wildfly instance: `./$WILDFLY_ROOT/bin/standalone.sh`
- Navigate to `http://localhost:8080`

## Running in SDM

Start GWT super dev mode by: `mvn gwt:run`

## Running in IntelliJ

Create a new Run/Debug GWT configuration as:

Module: `kie-wb-common-stunner-bpmn-kogito-runtime`

GWT Module: `org.kie.workbench.common.stunner.kogito.KogitoBPMNEditor`

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

## Usage

- Test Page (context path root)

Navigate to the context path root (eg: `http://localhost:8080`)

It provides buttons for creating new processes, opening an existing process and exporting the actual process

- Also, the editor can be used using Javascript API:

        // For creating a process
        window.frames.editorFrame.contentWindow.gwtEditorBeans.get("BPMNDiagramEditor").get().setContent("", "")

        // For loading a process (the raw xml)
        window.frames.editorFrame.contentWindow.gwtEditorBeans.get("BPMNDiagramEditor").get().setContent("", raw)

        // Get the actual process' content
        window.frames.editorFrame.contentWindow.gwtEditorBeans.get("BPMNDiagramEditor").get().getContent()

        // Stunner Wires Shapes API
        var jsl = window.frames.editorFrame.contentWindow.canvas
        jsl.log().logWiresShapes()
        var s = jsl.getWiresShape('_A9481DBC-3E87-40EE-9925-733B24404BC0')
        s.getChild(1).fillColor = "red"

        // Events
        jsl.events().click(jsl.getShape('redRectangle'))
        jsl.events().drag(jsl.getShape('redRectangle'), 400, 400, () => console.log('DONE DRAG'))

        // Standalone BPMN Editor
        frames[0].canvas

        // JS API
        var jsl = window.frames.editorFrame.contentWindow.canvas
        jsl.getNodeIds() // Get id of all nodes
        jsl.getBackgroundColor('_A9481DBC-3E87-40EE-9925-733B24404BC0')         // gets background color
        jsl.setBackgroundColor('_A9481DBC-3E87-40EE-9925-733B24404BC0', 'blue') // sets background color
        jsl.getBorderColor('_A9481DBC-3E87-40EE-9925-733B24404BC0')             // gets border color
        jsl.setBorderColor('_A9481DBC-3E87-40EE-9925-733B24404BC0', 'red')      // sets border color
        jsl.getLocation('_A9481DBC-3E87-40EE-9925-733B24404BC0', 'red')         // gets location
        jsl.getAbsoluteLocation('_A9481DBC-3E87-40EE-9925-733B24404BC0', 'red') // gets absolute location
        jsl.getDimensions('_A9481DBC-3E87-40EE-9925-733B24404BC0', 'red')       // gets dimensions
        jsl.applyState('_A9481DBC-3E87-40EE-9925-733B24404BC0', 'invalid')      // applies state (none, highlight, selected, invalid)
        jsl.centerNode('_A9481DBC-3E87-40EE-9925-733B24404BC0')                 // centers node in viewable canvas

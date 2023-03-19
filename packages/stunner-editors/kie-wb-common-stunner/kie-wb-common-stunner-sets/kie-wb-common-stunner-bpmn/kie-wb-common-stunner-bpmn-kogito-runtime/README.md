# BPMN Editor - Kogito webapp

The BPMN editor's webapp bundle for all kogito channels.
It results in a client side web application that can be just deployed in any web server.
Please also take a quick look at the [Stunner README](../../../README.md).

## Building

- Build Stunner and its dependencies:
  - Change to the `kie-tools/packages/stunner-editors/` root folder
  - run `mvn clean install -DskipTests -Dgwt.compiler.skip=true`
- Build the webapp:
  - Change to the `kie-tools/packages/stunner-editors/kie-wb-common-stunner` root folder
  - Dev: `mvn -T 8C clean install -DskipTests=true -Ddev`
  - Dev+SourceMaps: `mvn -T 8C clean install -DskipTests=true -Dsources`
  - Production: `mvn -T 8C clean install -DskipTests=true`

## Running in an application server

Following commands assume the use of Widfly:

- Copy the generated file `target/kie-wb-common-stunner-bpmn-kogito-runtime.war` into `$WILDFLY_ROOT/standalone/deployments`
- Rename the deployed WAR as `ROOT.war`
- Run the Wildfly instance: `./$WILDFLY_ROOT/bin/standalone.sh`
- Navigate to `http://localhost:8080/test.html`

## Running in SDM

Change to the `kie-tools/packages/stunner-editors/kie-wb-common-stunner/kie-wb-common-stunner-sets/kie-wb-common-stunner-bpmn/kie-wb-common-stunner-bpmn-kogito-runtime/` root folder

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

Dev Mode Parameters:

        -generateJsInteropExports
        -style PRETTY // This parameter is optional
        -logLevel [ERROR, WARN, INFO, TRACE, DEBUG, SPAM, or ALL] // This parameter is optional

Start page: `test.html`

## Usage

- Editor's test page

Navigate to the test page in your root context path, eg: `http://localhost:8080/test.html`. The UI provides some buttons for creating new processes, opening an existing process and exporting the actual process.

- Editor's Js API:

        // For creating a process
        window.frames.editorFrame.contentWindow.gwtEditorBeans.get("BPMNDiagramEditor").get().setContent("", "")

        // For loading a process (the raw xml)
        window.frames.editorFrame.contentWindow.gwtEditorBeans.get("BPMNDiagramEditor").get().setContent("", raw)

        // Get the actual process' content
        window.frames.editorFrame.contentWindow.gwtEditorBeans.get("BPMNDiagramEditor").get().getContent()

        // Standalone BPMN Editor
        frames[0].canvas

        // Stunner - Js API
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

        // Stunner - (Wires) Shapes API
        var jsl = window.frames.editorFrame.contentWindow.canvas
        jsl.log().logWiresShapes()
        var s = jsl.getWiresShape('_A9481DBC-3E87-40EE-9925-733B24404BC0')
        s.getChild(1).fillColor = "red"

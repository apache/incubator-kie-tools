# Kogito SW Editor - Webapp

## Building

- run `mvn clean package -Pj2cl-build` on the root of the `serverless-workflow-diagram-editor`

## Running in Wildfly

- Copy the generated file `target/sw-editor-kogito-app.war` into `$WILDFLY_ROOT/standalone/deployments`
- Rename the deployed WAR as `ROOT.war`
- Run the Wildfly instance: `./$WILDFLY_ROOT/bin/standalone.sh`
- Navigate to `http://localhost:8080`

## Running in SDM

Start J2CL dev mode by: `mvn clean package -Pj2cl-build` on the root of the `serverless-workflow-diagram-editor`
Once `----- Build Complete: ready for browser refresh -----` appears, open one more terminal window and run
`http-server -p 8000` or any other light local web servers at
`serverless-workflow-diagram-editor/sw-editor/sw-editor-kogito-app/target/gwt/launcherDir/sw-editor-kogito-app`

Open web browser at 'http://127.0.0.1:8000/test.html'

J2CL-maven-plugin is able to track changes across reactor, so it ll recompile changes in any sub modules.

for detils see: https://blog.kie.org/2022/04/rise-of-j2cl-java-web-development-after-gwt.html

## Kogito Editor API //TODO

    // For creating a process
    window.frames.editorFrame.contentWindow.gwtEditorBeans.get("BPMNDiagramEditor").get().setContent("", "")

    // For loading a process (the raw xml)
    window.frames.editorFrame.contentWindow.gwtEditorBeans.get("BPMNDiagramEditor").get().setContent("", raw)

    // Get the actual process' content
    window.frames.editorFrame.contentWindow.gwtEditorBeans.get("BPMNDiagramEditor").get().getContent()

## Low level JsInterop API //TODO

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

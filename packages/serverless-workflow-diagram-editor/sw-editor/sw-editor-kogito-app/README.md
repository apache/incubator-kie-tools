# SWF Editor - Kogito Webapp

The Serverless Workflow Diagram Editor's webapp bundle for all kogito channels.

It results in a client side web application that can be just deployed in any web server.

## Building

- Build Stunner and its dependencies:
  - Change to the `kie-tools/packages/serverless-workflow-diagram-editor/` root folder
  - run `mvn clean install -DskipTests -Dgwt.compiler.skip=true`
- Build the webapp:
  - Change to the `kie-tools/packages/serverless-workflow-diagram-editor/sw-editor` root folder
  - Dev: `mvn -T 8C clean install -DskipTests=true -Ddev`
  - Dev+SourceMaps: `mvn -T 8C clean install -DskipTests=true -Dsources`
  - Production: `mvn -T 8C clean install -DskipTests=true`

## Running in an application server

Following commands assume the use of Widfly:

- Copy the generated file `sw-editor-kogito-app/target/sw-editor-kogito-app.war` into `$WILDFLY_ROOT/standalone/deployments`
- Rename the deployed WAR as `ROOT.war`
- Run the Wildfly instance: `./$WILDFLY_ROOT/bin/standalone.sh`
- Navigate to `http://localhost:8080/test.html`

## Running in SDM

Change to the `kie-tools/packages/serverless-workflow-diagram-editor/sw-editor/sw-editor-kogito-app` root folder

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

Dev Mode Parameters:

        -generateJsInteropExports
        -style PRETTY // This parameter is optional
        -logLevel [ERROR, WARN, INFO, TRACE, DEBUG, SPAM, or ALL] // This parameter is optional

Start page: `test.html`

## Usage

- Editor's test page

Navigate to the test page in your root context path, eg: `http://localhost:8080/test.html`. The UI provides some buttons for creating new workflows, opening an existing workflow and exporting the actual workflow.

- Editor's Js API:

        // For creating a workflows
        window.frames.editorFrame.contentWindow.gwtEditorBeans.get("SWDiagramEditor").get().setContent("", "")

        // For loading a workflows (the raw xml)
        window.frames.editorFrame.contentWindow.gwtEditorBeans.get("SWDiagramEditor").get().setContent("", raw)

        // Get the actual workflows' content
        window.frames.editorFrame.contentWindow.gwtEditorBeans.get("SWDiagramEditor").get().getContent()

- Editor's low-level Js API

  #### Canvas API (Lienzo)

        // Get canvas instance
        var canvas = window.frames.editorFrame.contentWindow.canvas

  | Function            | Parameters                   |    Return Type    | Description                                                                                                                                                |
  | :------------------ | :--------------------------- | :---------------: | :--------------------------------------------------------------------------------------------------------------------------------------------------------- |
  | getNodeIds          |                              |     String[]      | get all nodes ids                                                                                                                                          |
  | getBackgroundColor  | String id                    |      String       | get background color                                                                                                                                       |
  | setBackgroundColor  | String id, <br/>String color |                   | set background color, <br/> take color name or color Hex code <br/> e.g. 'black' or '#000000'                                                              |
  | getBorderColor      | String id                    |      String       | get border color                                                                                                                                           |
  | setBorderColor      | String id, <br/>string color |                   | set border color, <br/> take color name or color Hex code <br/> e.g. 'black' or '#000000'                                                                  |
  | getLocation         | String id                    |     Number[]      | get location [x, y]                                                                                                                                        |
  | getAbsoluteLocation | String id                    |     Number[]      | get absolute location [x, y]                                                                                                                               |
  | getDimensions       | String id                    |     Number[]      | get dimensions [width, height]                                                                                                                             |
  | applyState          | String id, <br/>String state |                   | apply state {'none', 'highlight', 'selected', 'invalid'}                                                                                                   |
  | center              | String id                    |                   | center node in the viewable canvas, <br/> only applicable to large diagrams where the node is not within the visible area                                  |
  | calculateCenter     | String id                    |     Number[]      | get the coordinates to center a node in the viewable canvas [x, y], <br/> only applicable to large diagrams where the node is not within the viewable area |
  | isConnected         | String id1, <br/>String id2  |      boolean      | check if two nodes are connected                                                                                                                           |
  | isShapeVisible      | String id1                   |      boolean      | check if the node is within the viewable canvas                                                                                                            |
  | translate           | Number x, <br/>Number y      |                   | change canvas viewable area                                                                                                                                |
  | getTranslateX       |                              |      Number       | get X position of the viewable canvas                                                                                                                      |
  | getTranslateY       |                              |      Number       | get Y position of the viewable canvas                                                                                                                      |
  | scale               | Number factor                |                   | scale the diagram (zoom in/out)                                                                                                                            |
  | scaleWithXY         | Number x, <br/>Number y      |                   | scale the diagram (zoom in/out)                                                                                                                            |
  | getScaleX           |                              |      Number       | get X scale factor                                                                                                                                         |
  | getScaleY           |                              |      Number       | get Y scale factor                                                                                                                                         |
  | getLayer            |                              |       Layer       | get HTMLCanvasElement element                                                                                                                              |
  | getCanvas           |                              | HTMLCanvasElement | get canvas Layer instance                                                                                                                                  |
  | getViewport         |                              |     Viewport      | get canvas Viewport instance                                                                                                                               |
  | getNativeContext    |                              |  NativeContext2D  | get canvas NativeContext2D instance                                                                                                                        |
  | draw                |                              |                   | repaint canvas, <br/> apply graphic changes to the shapes in the canvas                                                                                    |
  | add                 | Primitive shape              |                   | add shape to the canvas                                                                                                                                    |
  | getShape            | String id                    |     Primitive     | get shape instance                                                                                                                                         |
  | getWiresManager     |                              |   WiresManager    | get WiresManager instance, <br/> low level API to manage and interact with WiresShapes                                                                     |
  | log                 |                              |   CanvasLogger    | get Canvas WireShape Logger instance, <br/> recover high level information about the elements in the canvas <br/> e.g. canvas.log().logWiresShapes()       |
  | getNodeIdSet        |                              |        Set        | get WireShape ids                                                                                                                                          |
  | getWiresShape       | String id                    |    WiresShape     | get WiresShape instance                                                                                                                                    |
  | close               |                              |                   | close canvas                                                                                                                                               |

  #### Session API (Stunner)

       // Get session instance
       var session = window.frames.editorFrame.contentWindow.editor.session

  | Function               | Parameters        | Return Type | Description                       |
  | :--------------------- | :---------------- | :---------: | :-------------------------------- |
  | getNodeByUUID          | String id         |    Node     | get Node instance by id           |
  | getNodeByName          | String name       |    Node     | get Node instance by name         |
  | getNodeName            | Node node         |   String    | get name from Node instance       |
  | getEdgeByUUID          | String id         |    Edge     | get Edge instance by id           |
  | getSelectedElementUUID |                   |   String    | get selected element id           |
  | selectByUUID           | String id         |             | select element by id              |
  | selectByName           | String name       |             | select element by name            |
  | clearSelection         |                   |             | clear selection                   |
  | getSelectedNode        |                   |    Node     | get selected Node instance        |
  | getSelectedEdge        |                   |    Edge     | get selected Edge instance        |
  | getSelectedDefinition  |                   | Definition  | get selected Definition instance  |
  | getName                | Object definition |   String    | get name from Definition instance |

  #### Domain Definitions API (Stunner)

       // Get definitions instance
       var definitions = window.frames.editorFrame.contentWindow.editor.definitions

  | Function                      | Parameters                                          | Return Type  | Description                                        |
  | :---------------------------- | :-------------------------------------------------- | :----------: | :------------------------------------------------- |
  | initializeDefinitionSet       | Object definitionSet                                |              | initialize domain definitions                      |
  | initializeDefinitionsField    | String definitionsField                             |              | set domain definitions field name                  |
  | initializeCategory            | String definitionId, <br/>String category           |              | set category for a definitionId                    |
  | initializeLabels              | String definitionId, <br/>String[] definitionLabels |              | set labels for a definitionId                      |
  | initializeDefinitionNameField | String definitionId, <br/>String nameField          |              | set field name for a definitionId                  |
  | initializeDomainQualifier     | Annotation domainQualifier                          |              | initialize domain qualifier                        |
  | initializeRules               | RuleSet ruleSet                                     |              | set domain rules                                   |
  | getId                         | Object definition                                   | DefinitionId | get DefinitionId instance from definition instance |
  | getCategory                   | Object definition                                   |    String    | get category from definition instance              |
  | getTitle                      | Object definition                                   |    String    | get title from definition instance                 |
  | getName                       | Object definition                                   |    String    | get name from definition instance                  |
  | getDescription                | Object definition                                   |    String    | get description from definition instance           |
  | getLabels                     | Object definition                                   |   String[]   | get labels from definition instance                |
  | getPropertyFields             | Object definition                                   |   String[]   | get property fields from definition instance       |

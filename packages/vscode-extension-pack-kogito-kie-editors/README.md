BPMN and DMN Editors
--

## Release notes - 0.2.11 (alpha)

### Features and fixes included on this release

> **NOTE:** VSCode has released a new API to improve the experience of Webview-based editors. Refer to [our releases page](https://github.com/kiegroup/kogito-tooling/releases) to download the artifact.
This new API is available on VSCode 1.43.0 or newer through the [proposed API mechanism](https://code.visualstudio.com/api/advanced-topics/using-proposed-api) (at the time of this release, this version is only available on [VSCode Insiders](https://code.visualstudio.com/insiders/)). 

#### General
- [KOGITO-883](https://issues.redhat.com/browse/KOGITO-883): VSCode Integration: Make undo/redo work with existing VSCode commands

#### DMN
- [KOGITO-964](https://issues.redhat.com/browse/KOGITO-964): Run on VSCode all DMN demos published on Kogito examples
- [KOGITO-542](https://issues.redhat.com/browse/KOGITO-542): DMN - Format the output XML
- [KOGITO-295](https://issues.redhat.com/browse/KOGITO-295): [DMN Designer] Kogito - DMNMarshaller - Integrations Tests on gwt-jsonix-marshallers
- [KOGITO-936](https://issues.redhat.com/browse/KOGITO-936): Analyse why editor increased the artifact size
- [KOGITO-778](https://issues.redhat.com/browse/KOGITO-778): [DMN Designer] Client-side marshaling tests
- [KOGITO-841](https://issues.redhat.com/browse/KOGITO-841): Documentation Links popup is not readable in DMN VS Code Extension
- [KOGITO-1155](https://issues.redhat.com/browse/KOGITO-1155): Fix Kogito examples with namespaces from other vendors
- [KOGITO-1156](https://issues.redhat.com/browse/KOGITO-1156): [DMN Designer] Documentation - Buttons (Download, Print) do not work on VSCode
- [KOGITO-1157](https://issues.redhat.com/browse/KOGITO-1157): [DMN Designer] New models must be named with the file name on VSCode
- [KOGITO-1337](https://issues.redhat.com/browse/KOGITO-1337): [DMN Designer] Author and filename are always non defined

#### BPMN
- [KOGITO-631](https://issues.redhat.com/browse/KOGITO-631): BPMN files created in VSCode contain invalid id
- [KOGITO-653](https://issues.redhat.com/browse/KOGITO-653): Support process type (Public/Private) in Stunner
- [KOGITO-257](https://issues.redhat.com/browse/KOGITO-257): Condition Expression should … only "Expression" option
- [KOGITO-444](https://issues.redhat.com/browse/KOGITO-444): SVG generation feature in BPMN Designer
- [KOGITO-980](https://issues.redhat.com/browse/KOGITO-980): Error message adding a condition expression
- [KOGITO-1177](https://issues.redhat.com/browse/KOGITO-1177): Kogito Quick Starts Scripts / Kafka Examples Spring Boot
- [KOGITO-1191](https://issues.redhat.com/browse/KOGITO-1191): Kogito Quick Starts Scripts / Timers issue with cancelsActivity 

### Known issues
- [KOGITO-469](https://issues.jboss.org/browse/KOGITO-469): Fix disabled Save button on VSCode "File > Save" menu
- [KOGITO-437](https://issues.jboss.org/browse/KOGITO-437): Stunner - Zoom widget duplicated after `setContent` calls.
- [KOGITO-342](https://issues.jboss.org/browse/KOGITO-342): Check why BPMN editor shows error on page closing.
- [KOGITO-347](https://issues.jboss.org/browse/KOGITO-347): Error logs happening during BPMN marshaling.
- [AF-2113](https://issues.jboss.org/browse/AF-2113): No indication of a modified BPMN diagram. 
- [AF-2168](https://issues.jboss.org/browse/AF-2168): No confirmation popup when closing a modified BPMN diagram.
- [KOGITO-155](https://issues.jboss.org/browse/KOGITO-155): Flicker when opening or restoring editors. 
- [AF-2167](https://issues.jboss.org/browse/AF-2167): Native editor key bindings for macOS. 
- [AF-2113](https://issues.jboss.org/browse/AF-2113): No indication of a modified BPMN diagram. 
- [KOGITO-157](https://issues.jboss.org/browse/KOGITO-157): Copy/paste between different BPMN diagram. 
- [KOGITO-224](https://issues.jboss.org/browse/KOGITO-224): An error message is displayed if you try to create a new type by pressing `Enter`. 
- [KOGITO-225](https://issues.jboss.org/browse/KOGITO-225): Custom type definitions aren’t re-used within the diagram.   
- [KOGITO-704](https://issues.redhat.com/browse/KOGITO-704): DMN Editor repeated setContent/getContent operations changes the content.
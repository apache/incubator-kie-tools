BPMN and DMN Editors
--

## Release notes - 0.2.10 (alpha)

### Features and fixes included on this release

> **NOTE:** VSCode has released a new API to improve the experience of Webview-based editors. Refer to [our releases page](https://github.com/kiegroup/kogito-tooling/releases) to download the artifact.
This new API is available on VSCode 1.43.0 or newer through the [proposed API mechanism](https://code.visualstudio.com/api/advanced-topics/using-proposed-api) (at the time of this release, this version is only available on [VSCode Insiders](https://code.visualstudio.com/insiders/)). 

#### DMN Editor Improvements/Bug Fixes
- [KOGITO-986](https://issues.redhat.com/browse/KOGITO-986): Display a message showing that DMN1.1 is not supported
- [KOGITO-1014](https://issues.redhat.com/browse/KOGITO-1014): [DMN Designer] ACE Editor is not shown for invalid DMN content
- [KOGITO-1454](https://issues.redhat.com/browse/KOGITO-1454): [DMN Designer] Docks: Rename 'Preview' to 'Explore diagram'
- [DROOLS-5006](https://issues.redhat.com/browse/DROOLS-5006): [DMN Designer] Code Completion - When users leave it opened, it never disappears
- [DROOLS-5125](https://issues.redhat.com/browse/DROOLS-5125): [DMN Designer] Node data type is lost after drag and drop in data type editor
- [DROOLS-5137](https://issues.redhat.com/browse/DROOLS-5137): [DMN Designer] Decision Navigator shows Name.toString() value for diagram
- [DROOLS-5157](https://issues.redhat.com/browse/DROOLS-5157): [DMN Designer] Monaco editor: Scrolling inside LiteralExpression editor breaks users ability to input data
- [DROOLS-5178](https://issues.redhat.com/browse/DROOLS-5178): [DMN Designer] Generate unnecessary typerRef on output tag

#### BPMN Improvements/Bug Fixes
- [KOGITO-1463](https://issues.redhat.com/browse/KOGITO-1463): BPMN editor - Explorer screen - Rename and re-order
- [KOGITO-779](https://issues.redhat.com/browse/KOGITO-779): Make sure that BPMN editor is published to Maven built with production parameters
- [KOGITO-989](https://issues.redhat.com/browse/KOGITO-989): VSCode - BPMN modeler can not delete global variables
- [KOGITO-990](https://issues.redhat.com/browse/KOGITO-990): BPMN saving data assignments does not save process model	
- [KOGITO-1158](https://issues.redhat.com/browse/KOGITO-1158): Zoom controls remain opened on different tabs [Low priority]
- [KOGITO-466](https://issues.redhat.com/browse/KOGITO-466): Stunner - Process with Reusable sub-process print errors to the server
- [KOGITO-437](https://issues.redhat.com/browse/KOGITO-437): Stunner - Zoom widget duplicated after `setContent` calls.
- [KOGITO-444](https://issues.redhat.com/browse/KOGITO-444): SVG generation feature in BPMN Designer

##### VSCode Improvements
- [KOGITO-1172](https://issues.redhat.com/browse/KOGITO-1172): Is Dirty VSCode support.

#### API Improvements
- [KOGITO-1236](https://issues.redhat.com/browse/KOGITO-1236): Create Preview API

####Known Issues 
- Undo/Redo on the online editor is not working until we resolve [KOGITO-766](https://issues.redhat.com/browse/KOGITO-766)
- [KOGITO-469](https://issues.redhat.com/browse/KOGITO-469): Fix disabled Save button on VSCode "File > Save" menu (Fixed on the new API)
- [KOGITO-347](https://issues.redhat.com/browse/KOGITO-347): Error logs happening during BPMN marshaling.
- [AF-2113](https://issues.redhat.com/browse/AF-2113): No indication of a modified BPMN diagram. (Fixed on the new API)
- [AF-2168](https://issues.redhat.com/browse/AF-2168): No confirmation popup when closing a modified BPMN diagram.  (Fixed on the new API)
- [KOGITO-155](https://issues.redhat.com/browse/KOGITO-155): Flicker when opening or restoring editors.  (Fixed on the new API)
- [AF-2167](https://issues.redhat.com/browse/AF-2167): Native editor key bindings for macOS. 
- [AF-2113](https://issues.redhat.com/browse/AF-2113): No indication of a modified BPMN diagram.  (Fixed on the new API)
- [KOGITO-157](https://issues.redhat.com/browse/KOGITO-157): Copy/paste between different BPMN diagrams.  
- [KOGITO-225](https://issues.redhat.com/browse/KOGITO-225): Custom type definitions aren’t re-used within the diagram. 
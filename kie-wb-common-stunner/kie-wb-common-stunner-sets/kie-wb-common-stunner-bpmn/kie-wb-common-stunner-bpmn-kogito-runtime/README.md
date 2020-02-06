Stunner Kogito Showcase
=======================

For detailed instructions on how to configure your development environment and run the Kogito Showcase, please refer to 
the [Stunner directory README documentation](../../../).
 
* Before building Kogito, build whole Stunner project under `kie-wb-common/kie-wb-common-stunner` by command:
  * `mvn clean install -DskipTests -Dgwt.compiler.skip=true`
* Go to this (`kie-wb-common/kie-wb-common-stunner/kie-wb-common-stunner-sets/kie-wb-common-stunner-bpmn/kie-wb-common-stunner-bpmn-kogito-runtime`) directory and compile Kogito project:
  * `mvn -T 8C clean install -DskipTests=true`
* Start GWT super dev mode by: `mvn gwt:run`
* To create new diagram copy/paste this command into the browser console:
  * `gwtEditorBeans.get("BPMNDiagramEditor").get().setContent("", "")` 
  * `window.frames.editorFrame.contentWindow.gwtEditorBeans.get("BPMNDiagramEditor").get().setContent("", "")`
* To get content of the diagram copy/paste this command into the browser console:
  * `gwtEditorBeans.get("BPMNDiagramEditor").get().getContent()`
  * `window.frames.editorFrame.contentWindow.gwtEditorBeans.get("BPMNDiagramEditor").get().getContent()`
* Alternatively you can load file from the disk, change url to `http://127.0.0.1:8888/test.html` and select file from the disk.

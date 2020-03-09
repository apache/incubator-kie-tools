Kogito Process Editor - Webapp
==============================

For detailed instructions on how to configure your development environment please refer to 
the [Stunner directory README documentation](../../../).
 
Building
--------
 
* Before building this webapp, you should build the whole of the Stunner project before
  * change to the `kie-wb-common/kie-wb-common-stunner` root folder
  * run `mvn clean install -DskipTests -Dgwt.compiler.skip=true`
* Build the webapp:
  * Production: `mvn -T 8C clean install -DskipTests=true`
  * Dev: `mvn -T 8C clean install -DskipTests=true -Ddev`
  * Dev+SourceMaps: `mvn -T 8C clean install -DskipTests=true -Dsources`

Running in Wildfly
------------------
* Copy the generated file `target/kie-wb-common-stunner-bpmn-kogito-runtime.war` into `$WILDFLY_ROOT/standalone/deployments`
* Rename the deployed WAR as `ROOT.war`
* Run the Wildfly instance: `./$WILDFLY_ROOT/bin/standalone.sh`
* Navigate to `http://localhost:8080`

Running in SDM
------------------
Start GWT super dev mode by: `mvn gwt:run`

Running in IntelliJ
-------------------
Create a new Run/Debug GWT configuration as:

Module: `kie-wb-common-stunner-bpmn-kogito-runtime`

GWT Module: `org.kie.workbench.common.stunner.kogito.KogitoBPMNEditor`

User Super Dev Mode: `true`

VM Options:

        -Xmx4G
        -Xms1024m
        -Xss1M
        -Derrai.dynamic_validation.enabled=true
    
[OPTIONAL] Dev Mode Parameters:
        
        -style PRETTY
        -logLevel [ERROR, WARN, INFO, TRACE, DEBUG, SPAM, or ALL]

Start page: `test.html`

Usage
-----
* Test Page (context path root)

Navigate to the context path root (eg: `http://localhost:8080`) 

It provides buttons for creating new processes, opening an existing process and exporting the actual process

* Also the editor can be used using Javascript API:

        // For creating a process
        window.frames.editorFrame.contentWindow.gwtEditorBeans.get("BPMNDiagramEditor").get().setContent("", "")      
  
        // For loading a process (the raw xml)
        window.frames.editorFrame.contentWindow.gwtEditorBeans.get("BPMNDiagramEditor").get().setContent("", raw)      

        // Get the actual process' content
        window.frames.editorFrame.contentWindow.gwtEditorBeans.get("BPMNDiagramEditor").get().getContent()

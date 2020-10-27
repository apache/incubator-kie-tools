BPMN Editor Showcase on Business Central 
========================================

Running the application
-----------------------

**Fast compilation profile**

Advantages:
- It is more stable than gwt:run (see next chapter)
- It is faster than gwt:run
- Not so RAM consuming which can be critical for laptop with 16 GB owners

Disadvantages:
- Support only Chrome browser. All browsers after some modifications of StunnerProjectShowcaseWithSourceMap.gwt.xml, for more details see comments inside of file.
- No hot fix recompilation
- Manual deploy of result war file

To compile showcase project with source map execute following commands:
    
    cd kie-wb-common/kie-wb-common-stunner/kie-wb-common-stunner-sets/kie-wb-common-stunner-bpmn/kie-wb-common-stunner-bpmn-project-showcase
    mvn clean package -Psourcemaps

**SuperDevMove - Running from command line**                                          

    cd kie-wb-common/kie-wb-common-stunner/kie-wb-common-stunner-sets/kie-wb-common-stunner-bpmn/kie-wb-common-stunner-bpmn-project-showcase
    mvn clean gwt:run

**SuperDevMove - Running from IntelliJ IDEA**                                          

1.- Open the project using IntelliJ IDEA - Use option "import project from Maven"                 
  
2.- Create a new Run/Debug configuration as:                
  - *Type*: GWT configuration                  
  - *Name*: Stunner Showcase Project                     
  - *Use SDM*: true                  
  - *Module*: stunner-bpmn-project-showcase             
  - *GWT Modules to load*: org.kie.workbench.common.stunner.project.FastCompiledStunnerProjectShowcase             
  - *VM options*: 
        
        -Xmx8g
        -Xms1g
        -Xss1M
        -XX:CompileThreshold=7000
        -Derrai.jboss.home=$PATH_OF_YOUR_CLONED_KIE_WB_COMMON_REPO/kie-wb-common-stunner/kie-wb-common-stunner-sets/kie-wb-common-stunner-bpmn/kie-wb-common-stunner-bpmn-project-showcase/target/wildfly-14.0.1.Final
        -Derrai.marshalling.server.classOutput=$PATH_OF_YOUR_CLONED_KIE_WB_COMMON_REPO/kie-wb-common-stunner/kie-wb-common-stunner-sets/kie-wb-common-stunner-bpmn/kie-wb-common-stunner-bpmn-project-showcase/target
        -Derrai.dynamic_validation.enabled=true
                      
  - *Dev mode parameters*: 
        
        -server org.jboss.errai.cdi.server.gwt.EmbeddedWildFlyLauncher
                      
  - *Start page*: stunner.html                  
  
  - On before launch section - Add a new "Run Maven Goal" BEFORE the existing "Make" item as:                
    - *Working directory*: 
        
            $PATH_OF_YOUR_CLONED_KIE_WB_COMMON_REPO/kie-wb-common-stunner/kie-wb-common-stunner-sets/kie-wb-common-stunner-bpmn/kie-wb-common-stunner-bpmn-project-showcase
                          
    - *Goal*: 
        
            clean process-resources                 
  
3.- Once done, you can run or debug the application using this recently created configuration.     

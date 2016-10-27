Stunner Modelling Tool
=======================

Basic on UF/Errai applications and does not include any KIE workbench or Guvnor project integration.                     

Running the application
-----------------------

**SuperDevMove - Running from command line**                                          

    cd kie-wb-common/kie-wb-common-stunner/kie-wb-common-stunner-showcase/kie-wb-common-stunner-showcase-standalone
    mvn clean gwt:run

**SuperDevMove - Running from IntelliJ IDEA**                                          

1.- Open the project using IntelliJ IDEA - Use option "import project from Maven"                 
  
2.- Create a new Run/Debug configuration as:                
  - *Type*: GWT configuration                  
  - *Name*: Stunner Showcase Standalone                    
  - *Use SDM*: true                  
  - *Module*: Stunner-showcase             
  - *GWT Modules to load*: .kie.workbench.common.stunner.standalone.FastCompiledStunnerStandaloneShowcase             
  - *VM options*: 
        
        -Xmx4g
        -Xms1g
        -Xss1M
        -XX:CompileThreshold=7000
        -Derrai.jboss.home=$PATH_OF_YOUR_CLONED_KIE_WB_COMMON_REPO/kie-wb-common-stunner/kie-wb-common-stunner-showcase/kie-wb-common-stunner-showcase-standalone/target/wildfly-10.0.0.Final
        -Derrai.server.classOutput=$PATH_OF_YOUR_CLONED_KIE_WB_COMMON_REPO/kie-wb-common-stunner/kie-wb-common-stunner-showcase/kie-wb-common-stunner-showcase-standalone/target
        -Djava.util.prefs.syncInterval=2000000
        -Dorg.uberfire.async.executor.safemode=true
        -Dorg.uberfire.nio.git.dir=/tmp/dir

                      
  - *Dev mode parameters*: 
        
        -server org.jboss.errai.cdi.server.gwt.EmbeddedWildFlyLauncher
                      
  - *Start page*: stunner.html                  
  
  - On before launch section - Add a new "Run Maven Goal" BEFORE the existing "Make" item as:                
    - *Working directory*: 
        
            $PATH_OF_YOUR_CLONED_KIE_WB_COMMON_REPO/kie-wb-common-stunner/kie-wb-common-stunner-showcase/kie-wb-common-stunner-showcase-standalone
                          
    - *Goal*: 
        
            clean process-resources                 
  
3.- Once done, you can run or debug the application using this recently created configuration.                   
  
*TIP*: While coding it's a good practice to remove application's old artifacts the GWT idea plugin working's directory. It is usually present on your home directory as `$HOME/.IntelliJIdea15/system/gwt/`. On Macs you can find this under ~//Library/Caches/IntelliJIdea15/gwt.

Requirements
------------
* Java8+          
* Maven 3.2.5+       
* Git 1.8+        

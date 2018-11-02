Stunner Showcase - Project
==========================

Stunner Showcase built on top of KIE workbench, commons and guvnor services and screens. It shows the integration with different assets and editors for the KIE workbench with Stunner.                                            

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
    
    cd kie-wb-common/kie-wb-common-stunner/kie-wb-common-stunner-showcase/kie-wb-common-stunner-showcase-project
    mvn clean package -Psourcemaps

**SuperDevMove - Running from command line**                                          

    cd kie-wb-common/kie-wb-common-stunner/kie-wb-common-stunner-showcase/kie-wb-common-stunner-showcase-project
    mvn clean gwt:run

**SuperDevMove - Running from IntelliJ IDEA**                                          

1.- Open the project using IntelliJ IDEA - Use option "import project from Maven"                 
  
2.- Create a new Run/Debug configuration as:                
  - *Type*: GWT configuration                  
  - *Name*: Stunner Showcase Project                     
  - *Use SDM*: true                  
  - *Module*: stunner-showcase-project             
  - *GWT Modules to load*: org.kie.workbench.common.stunner.project.FastCompiledStunnerProjectShowcase             
  - *VM options*: 
        
        -Xmx4g
        -Xms1g
        -Xss1M
        -XX:CompileThreshold=7000
        -Derrai.jboss.home=$PATH_OF_YOUR_CLONED_KIE_WB_COMMON_REPO/kie-wb-common-stunner/kie-wb-common-stunner-showcase/kie-wb-common-stunner-showcase-project/target/wildfly-14.0.1.Final
        -Derrai.server.classOutput=$PATH_OF_YOUR_CLONED_KIE_WB_COMMON_REPO/kie-wb-common-stunner/kie-wb-common-stunner-showcase/kie-wb-common-stunner-showcase-project/target
        -Djava.util.prefs.syncInterval=2000000
        -Dorg.uberfire.async.executor.safemode=true
        -Dorg.uberfire.nio.git.dir=/tmp/dir
        -Derrai.dynamic_validation.enabled=true
                      
  - *Dev mode parameters*: 
        
        -server org.jboss.errai.cdi.server.gwt.EmbeddedWildFlyLauncher
                      
  - *Start page*: stunner.html                  
  
  - On before launch section - Add a new "Run Maven Goal" BEFORE the existing "Make" item as:                
    - *Working directory*: 
        
            $PATH_OF_YOUR_CLONED_KIE_WB_COMMON_REPO/kie-wb-common-stunner/kie-wb-common-stunner-showcase/kie-wb-common-stunner-showcase-project
                          
    - *Goal*: 
        
            clean process-resources                 
  
3.- Once done, you can run or debug the application using this recently created configuration.                   
  
*TIP*: While coding it's a good practice to remove application's old artifacts the GWT idea plugin working's directory. It is usually present on your home directory as `$HOME/.IntelliJIdeaXXX/system/gwt/`. On Macs you can find this under ~//Library/Caches/IntelliJIdeaXXX/gwt.

Requirements
------------
* Java8+          
* Maven 3.3.9+       
* Git 1.8+        

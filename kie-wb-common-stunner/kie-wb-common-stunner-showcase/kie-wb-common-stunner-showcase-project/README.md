Stunner Showcase - Project
==========================

Stunner Showcase built on top of KIE workbench, commons and guvnor services and screens. It shows the integration with different assets and editors for the KIE workbench with Stunner.                                            

Running the application
-----------------------

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
        -Derrai.jboss.home=$PATH_OF_YOUR_CLONED_KIE_WB_COMMON_REPO/kie-wb-common-stunner/kie-wb-common-stunner-showcase/kie-wb-common-stunner-showcase-project/target/wildfly-10.0.0.Final
        -Derrai.server.classOutput=$PATH_OF_YOUR_CLONED_KIE_WB_COMMON_REPO/kie-wb-common-stunner/kie-wb-common-stunner-showcase/kie-wb-common-stunner-showcase-project/target
        -Djava.util.prefs.syncInterval=2000000
        -Dorg.uberfire.async.executor.safemode=true
        -Dorg.uberfire.nio.git.dir=/tmp/dir

                      
  - *Dev mode parameters*: 
        
        -server org.jboss.errai.cdi.server.gwt.EmbeddedWildFlyLauncher
                      
  - *Start page*: stunner.html                  
  
  - On before launch section - Add a new "Run Maven Goal" BEFORE the existing "Make" item as:                
    - *Working directory*: 
        
            $PATH_OF_YOUR_CLONED_KIE_WB_COMMON_REPO/kie-wb-common-stunner/kie-wb-common-stunner-showcase/kie-wb-common-stunner-showcase-project
                          
    - *Goal*: 
        
            clean process-resources                 
  
3.- Once done, you can run or debug the application using this recently created configuration.                   
  
*TIP*: While coding it's a good practice to remove application's old artifacts the GWT idea plugin working's directory. It is usually present on your home directory as `$HOME/.IntelliJIdea15/system/gwt/`. On Macs you can find this under ~//Library/Caches/IntelliJIdea15/gwt.

Demo repository
---------------

By default this showcase downloads and installs a repository example on the first run:           
              
* The default repository used is the [jbpm-playground](https://github.com/guvnorngtestuser1/jbpm-console-ng-playground-kjar.git).              
* You can use any other repository by setting the following JVM properties:              
- `org.kie.workbench.common.stunner.project.demo.url` - URL for the git repository.               
- `org.kie.workbench.common.stunner.project.demo.username` - The git repository username.               
- `org.kie.workbench.common.stunner.project.demo.password` - The git repository password.              

Requirements
------------
* Java8+          
* Maven 3.2.5+       
* Git 1.8+        

Deployment into JBoss WidlFly 10.X
==================================

Please follow the next steps in order to deploy the application.           

Deploy
------

Run your JBoss WildFly instance using the `full` server profile as:

    $JBOSS_HOME/bin/standalone.sh --server-config=standalone-full.xml

Once server is up and running, get the proper WAR file (e.g. `dashbuilder-<version>-wildfly10.war`) and execute the following command to deploy the application into your JBoss Wildfly instance:              

    $ cd $JBOSS_HOME/bin
    $ ./jboss-cli.sh --connect --command="deploy <path_to_war_file>"
    
    NOTES:
        - <path_to_war_file>: is the local path to the application war file.
        - e.g. $ ./jboss-cli.sh --connect --command="deploy /home/myuser/myfiles/dashbuilder-0.5.0-SNAPSHOT-wildfly10.war" )


User Authentication
--------------------

Once started, open a browser and type the following URL:          
        
        http://localhost:8080/dashbuilder        # A login screen should be displayed.

However, some extra configuration is needed before you can sign in:               

* The application is based on the J2EE container managed authentication  mechanism.
This means that the login itself is delegated to the application server.

* To create users and define the roles use the command line utility provided by JBoss WildFly at `$JBOSS_HOME/bin/add-user.sh`.

* The application roles are defined at [web.xml](./WEB-INF/web.xml) file.
Roles can be used to create access profiles and define custom authorization policies.
There exist a single application role named `admin`. In order to use the application, create a user with role `admin`.               

* The application uses the JBoss' default security domain as you can see [here](./WEB-INF/jboss-web.xml).
Alternatively, you can define your own security domain and use, for instance, an LDAP, a database, or whatever mechanism you want to use as your credential storage.            
There are plenty of examples in the JBoss WildFly documentation about.

Feel free to change any settings regarding the application security and, once finished, to generate a distribution war that fits your needs.          

File System provider
---------------------
Dashbuilder stores all the internal artifacts (such as the data set definition files, the uploaded files, etc) into a GIT repository. You can clone the repository and noddle around with it if you need to.                

By default, the GIT repository is created when the application starts for first time at `$WORKING_DIR/.niogit`, considering `$WORKING_DIR` as the current directory where the application server is started.            

You can specify a custom repository location by setting the following Java system property to your target file system directory:                   
 
        -Dorg.uberfire.nio.git.dir=/home/youruser/some/path
        
If necessary you can make GIT repositories available from outside localhost using the following Java system property:                 
 
        -org.uberfire.nio.git.ssh.host=0.0.0.0
        
You can set this Java system properties permanent by adding the following lines in your `standalone-full.xml` file as:                
 
        <system-properties>
          <!-- Custom repository location. -->
          <property name="org.uberfire.nio.git.dir" value="/home/youruser/some/path"/>
          <!-- Make GIT repositories available from outside localhost. -->
          <property name="org.uberfire.nio.git.ssh.host" value="0.0.0.0"/>
        </system-properties>
        

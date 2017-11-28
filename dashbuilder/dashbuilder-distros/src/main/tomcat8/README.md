Deployment onto Apache Tomcat 8.x
=================================

This module contains all the artifacts to build the distribution for Apache Tomcat 8, in the simplest way possible and using a default configuration with the H2 database.                      

Please follow the next steps in order to deploy the application.

Authentication and authorization
---------------------------------

The dashboard builder module uses container managed authentication and authorization.

The `admin` role must be defined in tomcat's realm, as is the default role configured in the application's `web.xml`. Therefore, you must define this role and create
a user with the `admin` role assigned in order to be able to log in and use the application.                     

In a default Tomcat installation, edit `$CATALINA_HOME/conf/tomcat-users.xml` and customize and add the following lines:

         <role rolename="admin"/>
         <user username="admin"  password="admin"  roles="admin"/>

Deploy the application
--------------------------
Before deploying the WAR artifact for Dashbuilder please follow these steps:              

* Copy the JACC jar into `$CATALINA_HOME/lib`. This JAR has the Maven coordinates `javax.security.jacc:javax.security.jacc-api:jar:1.5` and can be found at the JBoss Maven Repository.             
        
Get the proper war file `dashbuilder-<version>-tomcat8.war` and copy it to `$CATALINA_HOME/webapps` or deploy it using the Tomcat Manager application.

User Authentication
--------------------------

Once started, open a browser and type the following URL:
`http://localhost:8080/dashbuilder-<version>-tomcat8/`. A login page should be displayed.

File System provider
---------------------
Dashbuilder stores all the internal artifacts (such as the data set definition files, the uploaded files, etc) into a GIT repository. You can clone the repository and noddle around with it if you need to.                

By default, the GIT repository is created when the application starts for first time at `$WORKING_DIR/.niogit`, considering `$WORKING_DIR` as the current directory where the application server is started.            

You can specify a custom repository location by setting the following Java system property to your target file system directory:                   
 
        -Dorg.uberfire.nio.git.dir=/home/youruser/some/path
        
If necessary you can make GIT repositories available from outside localhost using the following Java system property:                 
 
        -org.uberfire.nio.git.ssh.host=0.0.0.0


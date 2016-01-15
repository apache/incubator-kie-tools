Tomcat provider for user and group management services
========================================================

Introduction
------------
The classes in this package contain the user and group manager implementations for Apache Tomcat.              

This provider supports realm types based on XML files (the default ones in Tomcat server), such as `tomcat-users.xml`                                 

* The user, group and role implementations are provided by `org.uberfire.ext.security.management.tomcat.TomcatUserManagementService`

* The concrete user manager implementation is `org.uberfire.ext.security.management.tomcat.TomcatUserManager` which maps the Tomcat users for the given realm to application users.

* The concrete group manager implementation is `org.uberfire.ext.security.management.tomcat.TomcatGroupManager` which maps the Tomcat roles for the given realm to application groups or roles. Tomcat's default realm does not have support for both groups and roles. The available roles are the ones statically defined in `org.uberfire.ext.security.server.RolesRegistry`. All other roles defined in Tomcat are considered groups.

Installation notes
------------------

**Apache Tomcat**
If you are deploying the application in an Apache Tomcat, you can exclude the following libraries as they are provided by Tomcat's default library.                                                          

                <dependency>
                        <groupId>org.apache.tomcat</groupId>
                        <artifactId>tomcat-catalina</artifactId>
                </dependency>
                
                <dependency>
                        <groupId>org.apache.tomcat</groupId>
                        <artifactId>tomcat-coyote</artifactId>
                </dependency>
                
                <dependency>
                        <groupId>org.apache.tomcat</groupId>
                        <artifactId>tomcat-util</artifactId>
                </dependency>

**Other containers**
If you are deploying the application in other containers different than Tomcat, please make sure the following dependencies are in your application's classpath:                     

                <dependency>
                        <groupId>org.apache.tomcat</groupId>
                        <artifactId>tomcat-catalina</artifactId>
                        <version>7.0.61</version>
                </dependency>
                
                <dependency>
                        <groupId>org.apache.tomcat</groupId>
                        <artifactId>tomcat-coyote</artifactId>
                        <version>7.0.61</version>
                </dependency>
                
                <dependency>
                        <groupId>org.apache.tomcat</groupId>
                        <artifactId>tomcat-util</artifactId>
                        <version>7.0.61</version>
                </dependency>


Usage
-----
To use this provider implementation for the users and groups management services, please choose one of the following options:               

a) Specify the concrete provider to use by adding a properties file named `security-management.properties` in your web application root classpath. 
(e.g. `src/main/resources/security-management.properties`), with the following keys and your concrete provider name as value:                               

    org.uberfire.ext.security.management.api.userManagementServices=TomcatUserManagementService

b) Specify the following Java system properties at container startup:        

    -Dorg.uberfire.ext.security.management.api.userManagementServices=TomcatUserManagementService

In order to use this provider, the following system properties are required to be present at startup:                 

* `org.uberfire.ext.security.management.tomcat.catalina-base` - The filesystem's path for the Tomcat's base directory (CATALINA_BASE). By default it's read from the system properties, so running tomcat aready provides it. It is mandatory. Defaults to: `/opt/tomcat`.                  
* `org.uberfire.ext.security.management.tomcat.users-file` - The Tomcat's users XML file path relative to the previous given value for `org.uberfire.ext.security.management.tomcat.catalina-base` . Property is mandatory. Defaults to: `conf/tomcat-users.xml`.                  

If you use the default Tomcat realm's configuration, you don't have to specify any of the above system properties, as the default values provided works with Tomcat's defaults.                 

Provider capabilities
---------------------
The Tomcat provider for users and groups management services provides the following features:                   

**User service capabilities**
* User search - Can search or list users. Search by `username`.         
* Read user - Can read a user            
* Create user - Can add new users            
* Update user - Can update a user            
* Delete user - Can delete a user            
* User attributes - Can manage user attributes            
* Group assignment - Can manage groups for a user            
* Role assignment - Can manage roles for a user             
* Change password - Can change user's password            

**Group service capabilities**
* Group search - Can search or list groups. Search by `name` attribute.             
* Read group - Can read a group            
* Create group - Can add new groups            
* Delete group - Can delete a group            

Notes
-----
* Java7+                   
* This implementation has been tested for an Apache Tomcat version `7.0.61`.                

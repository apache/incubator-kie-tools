Tomcat provider for Users and groups management services
========================================================

Introduction
------------
The classes from this package contains the users and groups management service implementations for Apache Tomcat.              

This provider supports realm types based on XML files (the default ones in Tomcat server), such as `tomcat-users.xml.                                 

* The users management services implementations are provided by class `org.uberfire.ext.security.management.tomcat.TomcatUserManagementService`.              
* The concrete users management service implementation is provided by class `org.uberfire.ext.security.management.tomcat.TomcatUserManager`, that **binds the Tomcat users for the given realm into application users**.                   
* The concrete roles management service implementation is provided by class `org.uberfire.ext.security.management.tomcat.TomcatGroupManager`, that **binds the Tomcat roles for the given realm into application groups**.                   


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

* `org.uberfire.ext.security.management.tomcat.users-file-path` - The filesystem directory path for the Tomcat's users XML file. Property is mandatory. Defaults to: `/opt/tomcat/conf`.                  
* `org.uberfire.ext.security.management.tomcat.users-file-name` - The Tomcat's users XML file name. Property is mandatory. Defaults to: `tomcat-users.xml`.                  

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

KeyCloak provider for Users and groups management services
==========================================================

Introduction
------------
The classes from this package contains the users and groups management services implementations for JBoss KeyCloak.              

* The users management services implementations are provided by class `org.uberfire.ext.security.management.keycloak.KeyCloakUserManagementService`.              
* The concrete users management service implementation is provided by class `org.uberfire.ext.security.management.keycloak.KeyCloakUserManager`, that **binds the KeyCloak users for the given realm into application users**.                   
* The concrete roles management service implementation is provided by class `org.uberfire.ext.security.management.keycloak.KeyCloakGroupManager`, that **binds the KeyCloak roles for the given realm into application groups**.                   

Installation notes
------------------

This provider uses the *KeyCloak Admin Client* (which uses KeyCloak's REST API services) to manage the different entities.
 In order to make the *KeyCloak Admin Client* working you have to provide some JBoss Resteasy maven dependencies.                                                   

**JBoss Wildfly / EAP**
If you are deploying the application in a Wildfly 8.x or an EAP 6.4, make sure you add the resteasy base module dependencies (provided by the server) into your application's classpath, 
 by creating file or adding the following module dependencies in *jboss-deployment-descriptor.xml*:                                   

        <jboss-deployment-structure>
            <deployment>
                <dependencies>
                    <module name="org.jboss.resteasy.resteasy-jackson-provider" services="import"/>
                </dependencies>
                <exclusions>
                    <module name="org.jboss.resteasy.resteasy-jackson2-provider"/>
                </exclusions>
            </deployment>
        </jboss-deployment-structure>

And ensure you are excluding resteasy libraries, if any, from your web applications classpath, as are provided by the container.                   

**Other containers**
If you are deploying the application in other containers different than Wildfly or EAP, please make sure the following dependencies are in your application's classpath:                     

        // Ensure you are using JAXRS v3 API, as keycloak admin client requires this version.
        <dependency>
          <groupId>org.jboss.resteasy</groupId>
          <artifactId>jaxrs-api</artifactId>
          <version>3.0.9.Final</version>
        </dependency>
        <dependency>
          <groupId>org.jboss.resteasy</groupId>
          <artifactId>resteasy-jaxrs</artifactId>
          <version>3.0.9.Final</version>
        </dependency>
        
        <dependency>
          <groupId>org.jboss.resteasy</groupId>
          <artifactId>resteasy-jackson-provider</artifactId>
          <version>3.0.9.Final</version>
        </dependency>
    
        <dependency>
          <groupId>org.jboss.resteasy</groupId>
          <artifactId>resteasy-client</artifactId>
          <version>3.0.9.Final</version>
        </dependency>

Usage
-----
To use this provider implementation for the users and groups management services, please choose one of the following options:               

a) Specify the concrete provider to use by adding a properties file named `security-management.properties` in your web application root classpath. 
(e.g. `src/main/resources/security-management.properties`), with the following keys and your concrete provider name as value:                               

    org.uberfire.ext.security.management.api.userManagementServices=KeyCloakUserManagementService


b) Specify the following Java system properties at container startup:        

    -Dorg.uberfire.ext.security.management.api.userManagementServices=KeyCloakUserManagementService

The management services for KeyCloak are provided by a REST API, so in order to connect to an existing KeyCloak instance service, the following system properties are required to be present at startup:                 

* `org.uberfire.ext.security.management.keycloak.authServer` - The URL for the KeyCloak authentication server. Property is mandatory. Defaults to: `http://localhost:8080/auth`.                  
* `org.uberfire.ext.security.management.keycloak.realm` - The name of the realm to use. Property is mandatory. Defaults to `example`.                   
* `org.uberfire.ext.security.management.keycloak.user` - The username. Property is mandatory. Defaults to `examples-admin-client`.                      
* `org.uberfire.ext.security.management.keycloak.password` - The password. Property is mandatory. Defaults to `password`.                             
* `org.uberfire.ext.security.management.keycloak.clientId` - The client identifier for the admin connection. Property is mandatory. Defaults to `examples-admin-client`.                                        
* `org.uberfire.ext.security.management.keycloak.clientSecret` - The client password for the admin connection. Property is mandatory. Defaults to `password`.                  

Provider capabilities
---------------------
The KeyCloak provider for users and groups management services provides the following features:                   

**User service capabilities**
* User search - Can search or list users. Search is delegated to keycloak, so it search for different user attributes as username, first name, etc.         
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
* This implementation has been tested for a KeyCloak version `1.4.0.Final`.                

KeyCloak provider for user and group management services
==========================================================

Introduction
------------
The classes in this package contain the user and group manager implementations for JBoss KeyCloak.              

* The user, group and role manager implementations are provided by `org.uberfire.ext.security.management.keycloak.KeyCloakUserManagementService`

* The concrete user manager implementation is `org.uberfire.ext.security.management.keycloak.KeyCloakUserManager` which maps the KeyCloak users for the given realm to application users

* The concrete group manager implementation is `org.uberfire.ext.security.management.keycloak.KeyCloakGroupManager` which maps the KeyCloak roles for the given realm to application groups. KeyCloak does not have support for both groups and roles. The available roles are the ones statically defined `org.uberfire.ext.security.server.RolesRegistry`). All other roles defined in KeyCloak are considered groups.

Installation notes
------------------

Step 1 - Dependencies
---------------------

This provider uses the *KeyCloak Admin Client* (which uses KeyCloak's REST API services) to manage the different entities, so the following keycloak artifacts are required:                         

        <dependency>
          <groupId>org.keycloak</groupId>
          <artifactId>keycloak-core</artifactId>
          <version>1.4.0.Final</version>
        </dependency>
        
        <dependency>
          <groupId>org.keycloak</groupId>
          <artifactId>keycloak-model-api</artifactId>
          <version>1.4.0.Final</version>
        </dependency>
        
        <dependency>
          <groupId>org.keycloak</groupId>
          <artifactId>keycloak-adapter-core</artifactId>
          <version>1.4.0.Final</version>
        </dependency>
        
        <dependency>
          <groupId>org.keycloak</groupId>
          <artifactId>keycloak-account-api</artifactId>
          <version>1.4.0.Final</version>
        </dependency>
        
        <dependency>
          <groupId>org.keycloak</groupId>
          <artifactId>keycloak-events-api</artifactId>
          <version>1.4.0.Final</version>
        </dependency>
        
        <dependency>
          <groupId>org.keycloak</groupId>
          <artifactId>keycloak-admin-client</artifactId>
          <version>1.4.0.Final</version>
        </dependency>
                                               

In addition, in order to make the *KeyCloak Admin Client* works you have to provide some JBoss Resteasy maven dependencies, configuration for them depends as:                

**For JBoss Wildfly / EAP**
If you are deploying the application in a Wildfly 8.x or an EAP 6.4, make sure you add the resteasy base module dependencies (provided by the server) into your application's classpath, 
 by creating file or adding the following module dependencies in *jboss-deployment-descriptor.xml*:                                   

        <jboss-deployment-structure>
            <deployment>
                <dependencies>
                    <module name="org.jboss.as.controller-client"/>
                    <module name="org.jboss.resteasy.resteasy-jackson-provider" services="import"/>
                </dependencies>
                <exclusions>
                    <module name="org.jboss.resteasy.resteasy-jackson2-provider"/>
                </exclusions>
            </deployment>
        </jboss-deployment-structure>

And ensure you are excluding RestEasy libraries, if any, from your web applications classpath, as are provided by the container.                   

**For Other containers**
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
        
        <dependency>
          <groupId>org.wildfly</groupId>
          <artifactId>wildfly-controller-client</artifactId>
          <version>8.2.0.Final</version>  
        </dependency>

Step 2 - Provider settings
--------------------------

Once dependencies have been specified as above, let's configure the Keycloak provider. To do it, please choose one of the following options:               

a) Specify the concrete provider to use by adding a properties file named `security-management.properties` in your web application root classpath. 
(e.g. `src/main/resources/security-management.properties`), with the following keys and your concrete provider name as value:                               

    org.uberfire.ext.security.management.api.userManagementServices=KeyCloakUserManagementService


b) Specify the following Java system properties at container startup:        

    -Dorg.uberfire.ext.security.management.api.userManagementServices=KeyCloakUserManagementService

The management services for KeyCloak are provided by a REST API, so in order to connect to an existing KeyCloak instance service, the following properties are required to be present at startup either at the `security-management.properties` or as Java system properties:                 

* `org.uberfire.ext.security.management.keycloak.authServer` - The URL for the KeyCloak authentication server. Property is mandatory. Defaults to: `http://localhost:8080/auth`.                  
* `org.uberfire.ext.security.management.keycloak.realm` - The name of the realm to use. Property is mandatory. Defaults to `example`.                   
* `org.uberfire.ext.security.management.keycloak.user` - The username. Property is mandatory. Defaults to `examples-admin-client`.                      
* `org.uberfire.ext.security.management.keycloak.password` - The password. Property is mandatory. Defaults to `password`.                             
* `org.uberfire.ext.security.management.keycloak.clientId` - The client identifier for the admin connection. Property is mandatory. Defaults to `examples-admin-client`.                                        
* `org.uberfire.ext.security.management.keycloak.clientSecret` - The client password for the admin connection. Property is mandatory. Defaults to `password`.                  

NOTE: If the workbench's user system manager feature if not configured for the distribution you are using, please take a look at the [Workbench installation instructions](../uberfire-security-management-client-wb/README.md).                        

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

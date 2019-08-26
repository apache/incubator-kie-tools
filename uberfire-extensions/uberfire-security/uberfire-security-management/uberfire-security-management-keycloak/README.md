KeyCloak provider for user and group management services
==========================================================

Introduction
------------
The classes in this package contain the user and group manager provider implementation for JBoss KeyCloak.              

There are two implementation for this provider:           

**Based on username/password credentials**                

Relies on being username & password credentials in the `security-management.properties` file.         

It's provided by the service `KCCredentialsUserManagementService`.                 

**Based on Keycloak Client Adapter integration (SSO)**              

Relies on considering that the application resides on an application server where any of the KC client adapters has been 
installed and setup, so the KeyCloak security context is available on the environment.                  

It's provided by the service `KCAdapterUserManagementService`.                 

Important considerations about the SSO provider implementation:                
* Only use this provider implementation if any of the KC client adapters is installed and setup on the application server. Usually this happens when integration with Keycloak SSO.                              
* It uses the current access token present in session, so ensure the current user logged has the `realm-management` client role assigned on Keycloak side,
 in order to manage the realm on Keycloak                   

Installation notes
------------------

Step 1 - Dependencies
---------------------

The users and groups management provider for Keycloak, either the username/password credentials and the KC Client Adapter based implementation, 
uses a custom RestEasy client that consumes the Keycloak remote admin services.                     

So the following JAXRS, RestEasy and Keycloak dependencies must be present in the application's classpath:                          

        <dependency>
          <groupId>org.keycloak</groupId>
          <artifactId>keycloak-core</artifactId>
          <version>...</version>
        </dependency>
        
       <dependency>
         <groupId>org.jboss.resteasy</groupId>
         <artifactId>resteasy-jaxrs</artifactId>
         <version>...</version>
         <version>
       </dependency>
   
       <dependency>
         <groupId>org.jboss.resteasy</groupId>
         <artifactId>resteasy-jackson-provider</artifactId>
         <version>...</version>
       </dependency>

Run the following command in order to figure out the versions to use for each of the above artifacts:

    mvn dependency:list

Step 2 - Provider settings
--------------------------

NOTE: If the workbench's user system manager feature is not configured for the distribution you are using, please take a look at the [Workbench installation instructions](../uberfire-security-management-client-wb/README.md).                        

Once dependencies have been specified as above, let's configure the Keycloak provider. To do it, please choose one of the following options:               

**Using the credentials provider**                   

a) Specify the concrete provider to use by setting the Java system property below or by adding a properties file named `security-management.properties` in your web application root classpath
(e.g. `src/main/resources/security-management.properties`), with the following keys and your concrete provider name as value:                               

    org.uberfire.ext.security.management.api.userManagementServices=KCCredentialsUserManagementService

b) Specify the following Java system properties at container startup:        

    -Dorg.uberfire.ext.security.management.api.userManagementServices=KCCredentialsUserManagementService

The following properties are required to be present at startup either at the `security-management.properties` or as Java system properties:                 

* `org.uberfire.ext.security.management.keycloak.authServer` - The URL for the KeyCloak authentication server. Property is mandatory. Defaults to: `http://localhost:8080/auth`.                  
* `org.uberfire.ext.security.management.keycloak.realm` - The name of the realm to use. Property is mandatory. Defaults to `example`.                   
* `org.uberfire.ext.security.management.keycloak.user` - The username. Property is mandatory. Defaults to `examples-admin-client`.                      
* `org.uberfire.ext.security.management.keycloak.password` - The password. Property is mandatory. Defaults to `password`.                             
* `org.uberfire.ext.security.management.keycloak.clientId` - The client identifier for the admin connection. Property is mandatory. Defaults to `examples-admin-client`.                                        
* `org.uberfire.ext.security.management.keycloak.clientSecret` - The client password for the admin connection. Property is mandatory. Defaults to `password`.                  

Example of the contents for the `security-management.properties`:                      

    org.uberfire.ext.security.management.api.userManagementServices=KCCredentialsUserManagementService
    org.uberfire.ext.security.management.keycloak.authServer=http://localhost:8080/auth
    org.uberfire.ext.security.management.keycloak.realm=demo
    org.uberfire.ext.security.management.keycloak.user=admin
    org.uberfire.ext.security.management.keycloak.password=password
    org.uberfire.ext.security.management.keycloak.clientId=kie
    org.uberfire.ext.security.management.keycloak.clientSecret=925f9190-a7c1-4cfd-8a3c-004f9c73dae6
    
**Using the KC adapter provider**                   

Important considerations about the SSO provider implementation:                
* Only use this provider implementation if any of the KC client adapters is installed and setup on the application server. Usually this happens when integration with Keycloak SSO.                              
* It uses the current access token present in session, so ensure the current user logged has the `realm-management` client role assigned in order to manage the realm on Keycloak                   

a) Specify the concrete provider to use by adding a properties file named `security-management.properties` in your web application root classpath. 
(e.g. `src/main/resources/security-management.properties`), with the following keys and your concrete provider name as value:                               

    org.uberfire.ext.security.management.api.userManagementServices=KCAdapterUserManagementService


b) Specify the following Java system properties at container startup:        

    -Dorg.uberfire.ext.security.management.api.userManagementServices=KCAdapterUserManagementService

The following property is required to be present at startup either at the `security-management.properties` or as Java system properties:                 

* `org.uberfire.ext.security.management.keycloak.authServer` - The URL for the KeyCloak authentication server. Property is mandatory. Defaults to: `http://localhost:8080/auth`.                  
* No more configurations required. The rest of settings are provided by the current access token in session.                

Example of the contents for the `security-management.properties`:                      
    
    org.uberfire.ext.security.management.api.userManagementServices=KCAdapterUserManagementService
    org.uberfire.ext.security.management.keycloak.authServer=http://localhost:8080/auth


Installation on an existing WAR file
------------------------------------

Follow these steps in order to update or enable the Keycloak users and group management provider in an Uberfire based packaged application (WAR file):                       

1.- Ensure the following libraries on `WEB-INF/lib`                

* Add if no present the uberfire-security-management-api-X.Y.Z.jar                             
* Add if no present the uberfire-security-management-backend-X.Y.Z.jar                             
* Add if no present the uberfire-security-management-keycloak-X.Y.Z.jar                             
* Add keycloak-core-X.Y.Z.Final.jar
* Add keycloak-common-X.Y.Z.Final.jar
* Remove any existing provider implementation, if any (ex: uberfire-security-management-wildfly-X.Y.Z.jar, remove uberfire-security-management-tomcat-X.Y.Z.jar,etc)                   

2.- Replace the whole content for file `WEB-INF/classes/security-management.properties`, if not present, create it:                    

    # Using the Keycloak Credential provider
    org.uberfire.ext.security.management.api.userManagementServices=KCCredentialsUserManagementService
    org.uberfire.ext.security.management.keycloak.authServer=http://localhost:8080/auth
    org.uberfire.ext.security.management.keycloak.realm=demo
    org.uberfire.ext.security.management.keycloak.user=admin
    org.uberfire.ext.security.management.keycloak.password=password
    org.uberfire.ext.security.management.keycloak.clientId=kie
    org.uberfire.ext.security.management.keycloak.clientSecret=password

    # Using the Keycloak Adapter provider
    org.uberfire.ext.security.management.api.userManagementServices=KCAdapterUserManagementService
    org.uberfire.ext.security.management.keycloak.authServer=http://localhost:8080/auth

Note: Use the concrete values for your environment.           

3.- Ensure on file `/META-INF/jboss-deployment-structure.xml`:

* Dependency to `org.jboss.resteasy.resteasy-jackson-provider` module          
     
        <dependencies>
            ...
            <module name="org.jboss.resteasy.resteasy-jackson-provider" services="import"/>
            ...
        </dependencies>
     
* If deploying into Wildfly 8.2 or newer, add exclusion to `org.jboss.resteasy.resteasy-jackson2-provider` module               

        <exclusions>
            ...
            <module name="org.jboss.resteasy.resteasy-jackson2-provider"/>
            ...
        </exclusions>


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
* Java8+                   
* This implementation has been tested for a KeyCloak version `3.4.0.Final`                

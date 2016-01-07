UberFire User System Management
===============================

Table of contents
------------------

* **[Introduction](#introduction)**
* **[UberFire Users, Roles and Groups](#uberFire-users,-roles-and-groups)**
* **[Project Modules](#project-modules)**
* **[Installation notes](#installation-notes)**

Introduction
------------

This module and its sub-modules provide the users, groups and roles management components, services and  user interface screens and widgets for the entities management of the application's security realm.                

The UberFire security system delegates the authorization and authentication to the underlying container's security environment. 
So the users, groups and roles potentially come from different realms, such as the properties file for JBoss Wildfly's or EAP, or the XML file for Tomcat, etc. 
Due to this fact, the UF users management feature is based on services and their implementations. It defaults with the users, groups and roles services and three concrete implementations for:         
* JBoss Wildfly / EAP - Properties based realms              
* JBoss KeyCloak based realms           
* Tomcat XML based realms            

NOTE: If you are using the application against another security system, you can build you own users, groups and roles service provider implementations.               

In addition, note that each security realm can allow different features, for example, 
when using the properties realm in Wildfly, the user entity does not have name neither address, etc, 
so it does not have more properties than the identifier, but on the other hand, when using JBoss KeyCloak as the security provider, 
it allows more user meta-data. So the different UF users management services and interfaces are taking this into account by adding support for set of <i>capabilities</i>, 
which can be potentially supported by the provider implementation or not,  Please take a look at the concrete service provider implementation documentation to check all the supported capabilities.                   


UberFire Users, Roles and Groups
--------------------------------

UberFire's security system provides an entities model based on users, groups and roles. As many security realms, such as the default for Wildfly, EAP, Tomcat or Keycloak, does either support groups or roles, not both, here is how UF behaves:                    

* The users are the users that come from the security realm                 
* The roles are the groups or roles, depending on what the concrete realm provides, that come from the security realm and are registered in the Roles Registry (see `org.uberfire.ext.security.server.RolesRegistry`)               
* The groups are the groups or roles, depending on what the concrete realm provides, that come from the security realm and are not registered as roles in the Roles Registry                   

So keep in mind that if the security realm being used does not support both groups and roles, 
the user assignments will be performed with no differentiation, so all groups and roles assigned to a given user will be added in the security realm, 
no matter if it's a group or a role. At user load time, UF will check the Roles Registry and will apply the right assignment behaviour.             

Do to this fact, the role management service implementation used by default is just based on having static roles, the roles that are present in the Roles Registry (see `org.uberfire.ext.security.server.RolesRegistry`).                     

Project Modules
---------------

Here is a short summary of the project sub-modules:                  

* [`uberfire-security-management-api`](./uberfire-security-management-api/) - Provides the different classes and interfaces for the users, groups and roles management used in both backend and client side.                         
* [`uberfire-security-management-backend`](./uberfire-security-management-backend/) - Provides the different classes and implementations for the users, groups and roles management backend services and other stuff.                         
* [`uberfire-security-management-client`](./uberfire-security-management-client/) - Provides the different classes and implementations for the users, groups and roles management client stuff.                         
* [`uberfire-security-management-client-wb`](./uberfire-security-management-client-wb/README.md) - Provides the perspectives and screens for the client side integration into the UF workbench.                         
* [`uberfire-security-management-keycloak`](./uberfire-security-management-keycloak/README.md) - Provides the concrete users, groups and roles service provider implementations for JBoss KeyCloak.                          
* [`uberfire-security-management-tomcat`](./uberfire-security-management-tomcat/README.md) - Provides the concrete users, groups and roles service provider implementations for Apache Tomcat.                          
* [`uberfire-security-management-wildfly`](./uberfire-security-management-wildfly/README.md) - Provides the concrete users, groups and roles service provider implementations for JBoss Wildfly or JBoss EAP.                          
* [`uberfire-widgets-security-management`](./uberfire-widgets-security-management/README.md) - Provides the widgets for the users, groups and roles management, such as explorers, editors, etc.                         
* [`uberfire-security-management-webapp`](./uberfire-security-management-webapp/README.md) - A simple web application that defaults with the users and group management perspectives and uses, by default, the embedded wildfly's realm.                         

Installation notes
------------------

Please take a look at the installation notes at the [Client WB module readme](./uberfire-security-management-client-wb/README.md).               


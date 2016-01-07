Users and groups management services - Workbench
================================================

Introduction
------------

This module provides the Uberfire perspectives and screens for the Users and Groups management feature.                               

Please, in order to use this module, use and specify and of the available Users and Groups management service implementations. For more information, please take a look at the [Uberfire Security Management](../../uberfire-security/uberfire-security-management/).                           

It provides the following workbench assets:               
* The users management perspective                           
* The groups management perspective                           
* The users management home screen                           
* The groups management home screen                           
* The users explorer screen                           
* The groups explorer screen                           
* The user editor screen                           
* The group editor screen                           

Note that the perspectives are only available for the role "admin".             

Installation notes
------------------

To use the perspectives, screens and widgets provided by this module, please follow these steps:                   

1.- Add the following dependencies into your webapp project's POM                  

        <!-- Security Management. -->
        <dependency>
          <groupId>org.uberfire</groupId>
          <artifactId>uberfire-security-management-api</artifactId>
        </dependency>
    
        <dependency>
          <groupId>org.uberfire</groupId>
          <artifactId>uberfire-security-management-backend</artifactId>
        </dependency>
        
        <dependency>
          <groupId>org.uberfire</groupId>
          <artifactId>uberfire-security-management-client</artifactId>
          <scope>provided</scope>
        </dependency>
        
        <dependency>
          <groupId>org.uberfire</groupId>
          <artifactId>uberfire-widgets-security-management</artifactId>
          <scope>provided</scope>
        </dependency>
        
        <dependency>
          <groupId>org.uberfire</groupId>
          <artifactId>uberfire-security-management-client-wb</artifactId>
          <scope>provided</scope>
        </dependency>

2.- Add the dependencies for the Users and Groups Management service provider to use (use only ONE of the following ones) into your webapp project's POM                  

        <dependency>
          <groupId>org.uberfire</groupId>
          <artifactId>uberfire-security-management-keycloak</artifactId>
        </dependency>
    
        <dependency>
          <groupId>org.uberfire</groupId>
          <artifactId>uberfire-security-management-wildfly</artifactId>
        </dependency>
    
        <dependency>
          <groupId>org.uberfire</groupId>
          <artifactId>uberfire-security-management-tomcat</artifactId>
        </dependency>

2.1.- Specifying the provider to use using a properties file              

* You can specify the concrete provider to use by adding a properties file named `security-management.properties` in your web application root classpath. (e.g. `src/main/resources/security-management.properties`)                       
* Specify your users management services provider implementation class at runtime using the folowing key `org.uberfire.ext.security.management.api.userManagementServices`               

2.2.- Specifying the provider to use at runtime                 

* You have to specify some system properties for running the service provider implementation given               
* Specify your users management services provider implementation class at runtime using the folowing Java system property `org.uberfire.ext.security.management.api.userManagementServices`                               

*NOTE*: Please read the concrete service provider's documentation for more details about its configuration.                   

3.- Add the following source GWT artifacts to compile in the `org.codehaus.mojo:gwt-maven-plugin`                    
 
        <!-- Security Management -->
        <compileSourcesArtifact>org.uberfire:uberfire-security-management-api</compileSourcesArtifact>
        <compileSourcesArtifact>org.uberfire:uberfire-security-management-client</compileSourcesArtifact>
        <compileSourcesArtifact>org.uberfire:uberfire-widgets-security-management</compileSourcesArtifact>
        <compileSourcesArtifact>org.uberfire:uberfire-security-management-client-wb</compileSourcesArtifact>

4.- Add the GWT module dependency in your webapp's GWT module file                     

        <inherits name="org.uberfire.ext.security.management.UberfireSecurityManagementWorkbench"/>

5.- If deploying on JBoss Wildfly or EAP, please add or update the `jboss-deployment-structure.xml`, if necessary (read each service provider implementation docs)                       

6.- You can use the Users and Groups management perspective on your webapp by adding the perspective menu item as in the following example                   
 
        @Inject
        private WorkbenchMenuBarPresenter menubar;
        
        @Inject
        private ClientUserSystemManager userSystemManager;
        
        ....
        
        @AfterInitialization
        public void startApp() {
        
            final MenuFactory.TopLevelMenusBuilder<MenuFactory.MenuBuilder> builder = ...
            
            ...

            if ( null != userSystemManager ) {
                // Wait for user management services to be initialized, if any.
                userSystemManager.waitForInitialization(new Command() {
                    @Override
                    public void execute() {
                        if (userSystemManager.isActive()) {
                            builder.newTopLevelMenu("Users management").respondsWith(new Command() {
                                @Override
                                public void execute() {
                                    placeManager.goTo(new DefaultPlaceRequest("UsersManagementPerspective"));
                                }
                            }).endMenu().
                                    newTopLevelMenu("Groups management")
                                    .respondsWith(new Command() {
                                        @Override
                                        public void execute() {
                                            placeManager.goTo(new DefaultPlaceRequest("GroupsManagementPerspective"));
                                        }
                                    }).endMenu();
    
                        } else {
                            GWT.log("Users management is NOT ACTIVE.");
                        }
    
                        final Menus menus = builder.build();
                        menubar.addMenus(menus);
                    }
                });
            }
            
            ...
            
        }

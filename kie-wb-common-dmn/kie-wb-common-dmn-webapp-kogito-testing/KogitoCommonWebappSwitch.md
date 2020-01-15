Modifications done to switch to kie-wb-common-kogito-webapp-base
================================================================

kie-wb-common-dmn-webapp-kogito-common
--------------------------------------

**pom.xml**: 

add:

    <dependency>
         <groupId>org.kie.workbench</groupId>
         <artifactId>kie-wb-common-kogito-webapp-base</artifactId>
         <scope>provided</scope>
    </dependency>
    

**DMNWebappKogitoCommon.gwt.xml**:

add:

    <inherits name="org.kie.workbench.common.kogito.webapp.base.KogitoWebappBase" />
    
**Java sources**:

removed:

   *org.kie.workbench.common.dmn.webapp.kogito.common.backend.workarounds.DummyAuthenticationService
    org.kie.workbench.common.dmn.webapp.kogito.common.backend.workarounds.MockWorkspaceProjectService
    org.kie.workbench.common.dmn.webapp.kogito.common.backend.ApplicationScopedProducer
    org.kie.workbench.common.dmn.webapp.kogito.common.client.workarounds.IsKogito
    org.kie.workbench.common.dmn.webapp.kogito.common.client.workarounds.IsKogitoTest*
        
add:

   *org.kie.workbench.common.dmn.webapp.kogito.common.backend.IOServiceNio2WrapperProviderImpl*      
        

kie-wb-common-dmn-webapp-kogito-testing
--------------------------------------

**pom.xml**: 

add:

    <dependency>
      <groupId>org.kie.workbench</groupId>
      <artifactId>kie-wb-common-kogito-webapp-base</artifactId>
      <classifier>sources</classifier>
    </dependency>
    <dependency>
      <groupId>org.kie.workbench</groupId>
      <artifactId>kie-wb-common-kogito-webapp-base</artifactId>
    </dependency>
    
removed:

    <dependency>
      <groupId>org.uberfire</groupId>
      <artifactId>uberfire-preferences-backend</artifactId>
    </dependency>
  
    
    
**Java sources**:

removed:

   *org.kie.workbench.common.dmn.showcase.client.perspectives.AuthoringPerspective*
   *org.kie.workbench.common.dmn.showcase.client.perspectives.AuthoringPerspectiveTest*
        
add:

   *org.kie.workbench.common.dmn.showcase.client.perspectives.TestingPerspectiveConfiguration*
        
modified:

   *org.kie.workbench.common.dmn.showcase.client.navigator.DMNDiagramsNavigatorScreen*
        
    public class DMNDiagramsNavigatorScreen extends BaseDMNDiagramsNavigatorScreen implements KogitoScreen {

        private static final PlaceRequest DMN_KOGITO_TESTING_SCREEN_DEFAULT_REQUEST = new DefaultPlaceRequest(DMNDiagramsNavigatorScreen.SCREEN_ID);
        ...
        @Override
        public PlaceRequest getPlaceRequest() {
            return DMN_KOGITO_TESTING_SCREEN_DEFAULT_REQUEST;
        }
        ...
        
              
**Resources**:

add:
    
  *META-INF/ErraiApp.properties*
  
    errai.ioc.enabled.alternatives=org.kie.workbench.common.dmn.showcase.client.perspectives.TestingPerspectiveConfiguration
    

    
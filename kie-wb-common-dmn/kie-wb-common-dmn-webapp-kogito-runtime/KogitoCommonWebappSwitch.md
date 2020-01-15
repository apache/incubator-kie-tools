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
        

kie-wb-common-dmn-webapp-kogito-runtime
--------------------------------------

**pom.xml**: 

add:

     <dependency>
      <groupId>org.kie.workbench</groupId>
      <artifactId>kie-wb-common-kogito-webapp-base</artifactId>
      <scope>provided</scope>
    </dependency>
    <dependency>
      <groupId>org.kie.workbench</groupId>
      <artifactId>kie-wb-common-kogito-webapp-base</artifactId>
      <classifier>sources</classifier>
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
        
        
modified:

   *org.kie.workbench.common.dmn.showcase.client.editor.DMNDiagramEditor*
        
    
    ...
    @Default
    public class DMNDiagramEditor extends AbstractDMNDiagramEditor implements KogitoScreen {

        private static final PlaceRequest DMN_KOGITO_RUNTIME_SCREEN_DEFAULT_REQUEST = new DefaultPlaceRequest(AbstractDMNDiagramEditor.EDITOR_ID);
        ...
        @Override
        public PlaceRequest getPlaceRequest() {
          return DMN_KOGITO_RUNTIME_SCREEN_DEFAULT_REQUEST;
        }
        ...
        
              
    

    
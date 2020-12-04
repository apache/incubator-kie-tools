package org.jboss.errai.ui.client.local.spi;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ResourcePrototype;

public class TranslationServiceImpl_org_kie_workbench_common_widgets_client_resources_i18n_KieWorkbenchWidgetsConstants_es_properties_default_InlineClientBundleGenerator implements org.jboss.errai.ui.client.local.spi.TranslationServiceImpl.org_kie_workbench_common_widgets_client_resources_i18n_KieWorkbenchWidgetsConstants_es_properties {
  private static TranslationServiceImpl_org_kie_workbench_common_widgets_client_resources_i18n_KieWorkbenchWidgetsConstants_es_properties_default_InlineClientBundleGenerator _instance0 = new TranslationServiceImpl_org_kie_workbench_common_widgets_client_resources_i18n_KieWorkbenchWidgetsConstants_es_properties_default_InlineClientBundleGenerator();
  private void getContentsInitializer() {
    getContents = new com.google.gwt.resources.client.TextResource() {
      // jar:file:/Users/tiagobento/redhat/kiegroup-all/kie-wb-common/kie-wb-common-widgets/kie-wb-common-ui/target/kie-wb-common-ui-7.47.0-SNAPSHOT.jar!/org/kie/workbench/common/widgets/client/resources/i18n/KieWorkbenchWidgetsConstants_es.properties
      public String getText() {
        return "KSessionSelectorViewImpl.KnowledgeBase=Base KIE\nKSessionSelectorViewImpl.KnowledgeSession=Sesión de KIE\nKSessionSelectorViewImpl.SelectedKSessionDoesNotExist=No existe la sesión seleccionada de KIE.\n\nNewResourceViewImpl.popupTitle=Crear nuevo\nNewResourceViewImpl.itemNameSubheading=Nombre:\nNewResourceViewImpl.fileNameIsMandatory=Falta el nombre del nuevo recurso. Introdúzcala.\nNewResourceViewImpl.resourceName=Nombre del recurso\nNewResourceViewImpl.packageName=Paquete\nNewResourceViewImpl.resourceNamePlaceholder=Nombre...\nNewResourceViewImpl.MissingPath=Falta la ruta para crear nuevos recursos. Introdúzcala.\n\nValidationPopup.YesSaveAnyway=Sí, guardar de todos modos\nValidationPopup.YesCopyAnyway=Sí, copiar de todos modos\nValidationPopup.YesDeleteAnyway=Sí, eliminar de todos modos\nValidationPopup.Cancel=Cancelar\nValidationPopupViewImpl.ValidationErrors=Errores de validación\n\nAboutPopupView.Version=Versión\nAboutPopupView.LicenseDescription=es un software de código abierto, publicado bajo la\nAboutPopupView.License=Licencia de software Apache 2.0\n\nKieAssetsDropdownView.Select=Seleccionar\n\nSearchBarComponentView.Find=Buscar...\nSearchBarComponentView.Of=de\n\n";
      }
      public String getName() {
        return "getContents";
      }
    }
    ;
  }
  private static class getContentsInitializer {
    static {
      _instance0.getContentsInitializer();
    }
    static com.google.gwt.resources.client.TextResource get() {
      return getContents;
    }
  }
  public com.google.gwt.resources.client.TextResource getContents() {
    return getContentsInitializer.get();
  }
  private static java.util.HashMap<java.lang.String, com.google.gwt.resources.client.ResourcePrototype> resourceMap;
  private static com.google.gwt.resources.client.TextResource getContents;
  
  public ResourcePrototype[] getResources() {
    return new ResourcePrototype[] {
      getContents(), 
    };
  }
  public ResourcePrototype getResource(String name) {
    if (GWT.isScript()) {
      return getResourceNative(name);
    } else {
      if (resourceMap == null) {
        resourceMap = new java.util.HashMap<java.lang.String, com.google.gwt.resources.client.ResourcePrototype>();
        resourceMap.put("getContents", getContents());
      }
      return resourceMap.get(name);
    }
  }
  private native ResourcePrototype getResourceNative(String name) /*-{
    switch (name) {
      case 'getContents': return this.@org.jboss.errai.ui.client.local.spi.TranslationServiceImpl.org_kie_workbench_common_widgets_client_resources_i18n_KieWorkbenchWidgetsConstants_es_properties::getContents()();
    }
    return null;
  }-*/;
}

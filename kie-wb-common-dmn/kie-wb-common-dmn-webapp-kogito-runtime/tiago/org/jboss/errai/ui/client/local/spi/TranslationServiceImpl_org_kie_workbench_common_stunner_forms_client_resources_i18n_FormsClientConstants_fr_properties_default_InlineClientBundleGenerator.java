package org.jboss.errai.ui.client.local.spi;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ResourcePrototype;

public class TranslationServiceImpl_org_kie_workbench_common_stunner_forms_client_resources_i18n_FormsClientConstants_fr_properties_default_InlineClientBundleGenerator implements org.jboss.errai.ui.client.local.spi.TranslationServiceImpl.org_kie_workbench_common_stunner_forms_client_resources_i18n_FormsClientConstants_fr_properties {
  private static TranslationServiceImpl_org_kie_workbench_common_stunner_forms_client_resources_i18n_FormsClientConstants_fr_properties_default_InlineClientBundleGenerator _instance0 = new TranslationServiceImpl_org_kie_workbench_common_stunner_forms_client_resources_i18n_FormsClientConstants_fr_properties_default_InlineClientBundleGenerator();
  private void getContentsInitializer() {
    getContents = new com.google.gwt.resources.client.TextResource() {
      // jar:file:/Users/tiagobento/redhat/kiegroup-all/kie-wb-common/kie-wb-common-stunner/kie-wb-common-stunner-extensions/kie-wb-common-stunner-forms/kie-wb-common-stunner-forms-client/target/kie-wb-common-stunner-forms-client-7.47.0-SNAPSHOT.jar!/org/kie/workbench/common/stunner/forms/client/resources/i18n/FormsClientConstants_fr.properties
      public String getText() {
        return "forms.notificationTitle=[{0}] Génération de formulaires\nforms.noItemsSelectedForGeneration=Impossible de générer des formulaires, aucun élément n’est sélectionné ou les éléments sélectionnés n’ont aucun rapport avec les formulaires\nforms.generationSuccess=Génération des formulaires effectuée pour [{0}]]\nforms.generationFailure=La génération des formulaires a échoué pour [{0}]\nforms.generateTaskForm=Générer le formulaire de tâches";
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
      case 'getContents': return this.@org.jboss.errai.ui.client.local.spi.TranslationServiceImpl.org_kie_workbench_common_stunner_forms_client_resources_i18n_FormsClientConstants_fr_properties::getContents()();
    }
    return null;
  }-*/;
}

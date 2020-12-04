package org.jboss.errai.ui.client.local.spi;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ResourcePrototype;

public class TranslationServiceImpl_org_kie_workbench_common_widgets_client_resources_i18n_KieWorkbenchWidgetsConstants_fr_properties_default_InlineClientBundleGenerator implements org.jboss.errai.ui.client.local.spi.TranslationServiceImpl.org_kie_workbench_common_widgets_client_resources_i18n_KieWorkbenchWidgetsConstants_fr_properties {
  private static TranslationServiceImpl_org_kie_workbench_common_widgets_client_resources_i18n_KieWorkbenchWidgetsConstants_fr_properties_default_InlineClientBundleGenerator _instance0 = new TranslationServiceImpl_org_kie_workbench_common_widgets_client_resources_i18n_KieWorkbenchWidgetsConstants_fr_properties_default_InlineClientBundleGenerator();
  private void getContentsInitializer() {
    getContents = new com.google.gwt.resources.client.TextResource() {
      // jar:file:/Users/tiagobento/redhat/kiegroup-all/kie-wb-common/kie-wb-common-widgets/kie-wb-common-ui/target/kie-wb-common-ui-7.47.0-SNAPSHOT.jar!/org/kie/workbench/common/widgets/client/resources/i18n/KieWorkbenchWidgetsConstants_fr.properties
      public String getText() {
        return "KSessionSelectorViewImpl.KnowledgeBase=Base KIE\nKSessionSelectorViewImpl.KnowledgeSession=Session KIE\nKSessionSelectorViewImpl.SelectedKSessionDoesNotExist=La session KIE sélectionnée n’existe pas.\n\nNewResourceViewImpl.popupTitle=Nouveau\nNewResourceViewImpl.itemNameSubheading=Nom :\nNewResourceViewImpl.fileNameIsMandatory=Nom manquant pour la nouvelle ressource. Veuillez le spécifier.\nNewResourceViewImpl.resourceName=Nom de la ressource\nNewResourceViewImpl.packageName=Package\nNewResourceViewImpl.resourceNamePlaceholder=Nom…\nNewResourceViewImpl.MissingPath=Le chemin d’accès à utiliser pour la création d’une ressource n’est pas indiqué. Veuillez le spécifier.\n\nValidationPopup.YesSaveAnyway=Oui, enregistrer quand même\nValidationPopup.YesCopyAnyway=Oui, copier quand même\nValidationPopup.YesDeleteAnyway=Oui, supprimer quand même\nValidationPopup.Cancel=Annuler\nValidationPopupViewImpl.ValidationErrors=Erreurs de validation\n\nAboutPopupView.Version=Version\nAboutPopupView.LicenseDescription=est un logiciel Open Source, publié sous la\nAboutPopupView.License=Licence logicielle Apache 2.0\n\nKieAssetsDropdownView.Select=Sélectionner\n\nSearchBarComponentView.Find=Rechercher…\nSearchBarComponentView.Of=de\n\n";
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
      case 'getContents': return this.@org.jboss.errai.ui.client.local.spi.TranslationServiceImpl.org_kie_workbench_common_widgets_client_resources_i18n_KieWorkbenchWidgetsConstants_fr_properties::getContents()();
    }
    return null;
  }-*/;
}

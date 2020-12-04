package org.jboss.errai.ui.client.local.spi;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ResourcePrototype;

public class TranslationServiceImpl_org_kie_workbench_common_stunner_client_widgets_resources_i18n_StunnerWidgetsConstants_fr_properties_default_InlineClientBundleGenerator implements org.jboss.errai.ui.client.local.spi.TranslationServiceImpl.org_kie_workbench_common_stunner_client_widgets_resources_i18n_StunnerWidgetsConstants_fr_properties {
  private static TranslationServiceImpl_org_kie_workbench_common_stunner_client_widgets_resources_i18n_StunnerWidgetsConstants_fr_properties_default_InlineClientBundleGenerator _instance0 = new TranslationServiceImpl_org_kie_workbench_common_stunner_client_widgets_resources_i18n_StunnerWidgetsConstants_fr_properties_default_InlineClientBundleGenerator();
  private void getContentsInitializer() {
    getContents = new com.google.gwt.resources.client.TextResource() {
      // jar:file:/Users/tiagobento/redhat/kiegroup-all/kie-wb-common/kie-wb-common-stunner/kie-wb-common-stunner-client/kie-wb-common-stunner-widgets/target/kie-wb-common-stunner-widgets-7.47.0-SNAPSHOT.jar!/org/kie/workbench/common/stunner/client/widgets/resources/i18n/StunnerWidgetsConstants_fr.properties
      public String getText() {
        return "DefinitionPaletteGroupWidgetViewImpl.showMore=Plus\nDefinitionPaletteGroupWidgetViewImpl.showLess=Moins\nNameEditBoxWidgetViewImpl.save=Enregistrer\nNameEditBoxWidgetViewImpl.close=Fermer\nNameEditBoxWidgetViewImpl.name=Nom\nSessionPresenterView.Error=Erreur\nSessionPresenterView.Warning=Avertissement\nSessionPresenterView.Info=Infos\nSessionPresenterView.Notifications=Détails sur le panneau des alertes\n\nMarshallingResponsePopup.OkAction=OK\nMarshallingResponsePopup.CancelAction=Annuler\nMarshallingResponsePopup.CopyToClipboardActionTitle=Copier les messages dans le presse-papiers\nMarshallingResponsePopup.LevelTableColumnName=Niveau\nMarshallingResponsePopup.MessageTableColumnName=Message\n\nMarshallingResponsePopup.ErrorMessageLabel=Erreur\nMarshallingResponsePopup.WarningMessageLabel=Avertissement\nMarshallingResponsePopup.InfoMessageLabel=Infos\nMarshallingResponsePopup.UnknownMessageLabel=Inconnu\n\nMarshallingMessage.boundaryIgnored=La relation de limite a été ignorée. Élément de limite : {0}, Parent : {1}\nMarshallingMessage.associationIgnored=L’association a été ignorée. Source : {0}, Cible : {1}\nMarshallingMessage.sequenceFlowIgnored=Le flux de séquence a été ignoré. Source : {0}, Cible : {1}\nMarshallingMessage.collapsedElementExpanded=L’élément réduit {0} de type {1} a été développé\nMarshallingMessage.ignoredElement=L’élément {0} de type {1} a été ignoré\nMarshallingMessage.ignoredUnknownElement=L’élément inconnu {0} a été ignoré\nMarshallingMessage.childLaneSetConverted=L’ensemble de voies enfant {0} a été converti en voie {1}\nMarshallingMessage.convertedElement=L’élément {0} de type {1} a été converti en {2}\nMarshallingMessage.elementFailure=Échec sur l’élément {0} de type {1}\nSessionCardinalityStateHandler.EmptyStateCaption=Cliquez sur un nœud, faites-le glisser ou interagissez avec celui-ci\nSessionCardinalityStateHandler.EmptyStateMessage=Pour commencer, cliquez sur un nœud ou faites-le glisser dans la palette de gauche sur le canevas";
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
      case 'getContents': return this.@org.jboss.errai.ui.client.local.spi.TranslationServiceImpl.org_kie_workbench_common_stunner_client_widgets_resources_i18n_StunnerWidgetsConstants_fr_properties::getContents()();
    }
    return null;
  }-*/;
}

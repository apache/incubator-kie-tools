package org.jboss.errai.ui.client.local.spi;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ResourcePrototype;

public class TranslationServiceImpl_org_kie_workbench_common_stunner_client_widgets_resources_i18n_StunnerWidgetsConstants_es_properties_default_InlineClientBundleGenerator implements org.jboss.errai.ui.client.local.spi.TranslationServiceImpl.org_kie_workbench_common_stunner_client_widgets_resources_i18n_StunnerWidgetsConstants_es_properties {
  private static TranslationServiceImpl_org_kie_workbench_common_stunner_client_widgets_resources_i18n_StunnerWidgetsConstants_es_properties_default_InlineClientBundleGenerator _instance0 = new TranslationServiceImpl_org_kie_workbench_common_stunner_client_widgets_resources_i18n_StunnerWidgetsConstants_es_properties_default_InlineClientBundleGenerator();
  private void getContentsInitializer() {
    getContents = new com.google.gwt.resources.client.TextResource() {
      // jar:file:/Users/tiagobento/redhat/kiegroup-all/kie-wb-common/kie-wb-common-stunner/kie-wb-common-stunner-client/kie-wb-common-stunner-widgets/target/kie-wb-common-stunner-widgets-7.47.0-SNAPSHOT.jar!/org/kie/workbench/common/stunner/client/widgets/resources/i18n/StunnerWidgetsConstants_es.properties
      public String getText() {
        return "DefinitionPaletteGroupWidgetViewImpl.showMore=Más\nDefinitionPaletteGroupWidgetViewImpl.showLess=Menos\nNameEditBoxWidgetViewImpl.save=Guardar\nNameEditBoxWidgetViewImpl.close=Cerrar\nNameEditBoxWidgetViewImpl.name=Nombre\nSessionPresenterView.Error=Error\nSessionPresenterView.Warning=Advertencia\nSessionPresenterView.Info=Información\nSessionPresenterView.Notifications=Detalles del panel de alertas\n\nMarshallingResponsePopup.OkAction=Aceptar\nMarshallingResponsePopup.CancelAction=Cancelar\nMarshallingResponsePopup.CopyToClipboardActionTitle=Copiar los mensajes al portapapeles\nMarshallingResponsePopup.LevelTableColumnName=Nivel\nMarshallingResponsePopup.MessageTableColumnName=Mensaje\n\nMarshallingResponsePopup.ErrorMessageLabel=Error\nMarshallingResponsePopup.WarningMessageLabel=Advertencia\nMarshallingResponsePopup.InfoMessageLabel=Información\nMarshallingResponsePopup.UnknownMessageLabel=Desconocido\n\nMarshallingMessage.boundaryIgnored=Se ignoró la relación de límites. Elemento de límite: {0}, Padre: {1}\nMarshallingMessage.associationIgnored=Se ignoró la asociación. Origen: {0}, Destino: {1}\nMarshallingMessage.sequenceFlowIgnored=Se ignoró el flujo de secuencias. Origen: {0}, Destino: {1}\nMarshallingMessage.collapsedElementExpanded=Se expandió el elemento contraído {0} de tipo {1\\}\nMarshallingMessage.ignoredElement=Se ignoró el elemento {0} de tipo {1\\}\nMarshallingMessage.ignoredUnknownElement=Se ignoró el elemento desconocido {0}\nMarshallingMessage.childLaneSetConverted=El conjunto de líneas secundarias {0} se convirtió a la línea {1}\nMarshallingMessage.convertedElement=El elemento {0} de tipo {1} se convirtió a {2}\nMarshallingMessage.elementFailure=Error en el elemento {0} de tipo {1}\nSessionCardinalityStateHandler.EmptyStateCaption=Haga clic en un nodo, arrástrelo o interactúe con él\nSessionCardinalityStateHandler.EmptyStateMessage=Para comenzar, haga clic en un nodo de la paleta de la izquierda y arrástrelo al lienzo";
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
      case 'getContents': return this.@org.jboss.errai.ui.client.local.spi.TranslationServiceImpl.org_kie_workbench_common_stunner_client_widgets_resources_i18n_StunnerWidgetsConstants_es_properties::getContents()();
    }
    return null;
  }-*/;
}

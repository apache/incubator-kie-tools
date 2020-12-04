package org.jboss.errai.ui.client.local.spi;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ResourcePrototype;

public class TranslationServiceImpl_org_kie_workbench_common_stunner_client_widgets_resources_i18n_StunnerWidgetsConstants_default_properties_default_InlineClientBundleGenerator implements org.jboss.errai.ui.client.local.spi.TranslationServiceImpl.org_kie_workbench_common_stunner_client_widgets_resources_i18n_StunnerWidgetsConstants_default_properties {
  private static TranslationServiceImpl_org_kie_workbench_common_stunner_client_widgets_resources_i18n_StunnerWidgetsConstants_default_properties_default_InlineClientBundleGenerator _instance0 = new TranslationServiceImpl_org_kie_workbench_common_stunner_client_widgets_resources_i18n_StunnerWidgetsConstants_default_properties_default_InlineClientBundleGenerator();
  private void getContentsInitializer() {
    getContents = new com.google.gwt.resources.client.TextResource() {
      // jar:file:/Users/tiagobento/redhat/kiegroup-all/kie-wb-common/kie-wb-common-stunner/kie-wb-common-stunner-client/kie-wb-common-stunner-widgets/target/kie-wb-common-stunner-widgets-7.47.0-SNAPSHOT.jar!/org/kie/workbench/common/stunner/client/widgets/resources/i18n/StunnerWidgetsConstants_default.properties
      public String getText() {
        return "DefinitionPaletteGroupWidgetViewImpl.showMore=More\nDefinitionPaletteGroupWidgetViewImpl.showLess=Less\nNameEditBoxWidgetViewImpl.save=Save\nNameEditBoxWidgetViewImpl.close=Close\nNameEditBoxWidgetViewImpl.name=Name\nSessionPresenterView.Error=Error\nSessionPresenterView.Warning=Warning\nSessionPresenterView.Info=Info\nSessionPresenterView.Notifications=Details on the alerts panel\n\nMarshallingResponsePopup.OkAction=Ok\nMarshallingResponsePopup.CancelAction=Cancel\nMarshallingResponsePopup.CopyToClipboardActionTitle=Copy the messages to the clipboard\nMarshallingResponsePopup.LevelTableColumnName=Level\nMarshallingResponsePopup.MessageTableColumnName=Message\n\nMarshallingResponsePopup.ErrorMessageLabel=Error\nMarshallingResponsePopup.WarningMessageLabel=Warning\nMarshallingResponsePopup.InfoMessageLabel=Info\nMarshallingResponsePopup.UnknownMessageLabel=Unknown\n\nMarshallingMessage.boundaryIgnored=Boundary relationship was ignored. Boundary element: {0}, Parent: {1}\nMarshallingMessage.associationIgnored=Association was ignored. Source: {0}, Target: {1}\nMarshallingMessage.sequenceFlowIgnored=Sequence flow was ignored. Source: {0}, Target: {1}\nMarshallingMessage.collapsedElementExpanded=Collapsed element {0} of type {1} was expanded\nMarshallingMessage.ignoredElement=Element {0} of type {1} was ignored\nMarshallingMessage.ignoredUnknownElement=Unknown element {0} was ignored\nMarshallingMessage.childLaneSetConverted=Child Lane Set {0} was converted to Lane {1}\nMarshallingMessage.convertedElement=Element {0} of type {1} was converted to {2}\nMarshallingMessage.elementFailure=Failure on element {0} of type {1}\nMarshallingMessage.dataObjectsSameNameDifferentType=Data Object Exists with Same Name and Different Type - Will be Changed to (Object)\nMarshallingMessage.dataObjectWithName=Data Object-Name\nMarshallingMessage.dataObjectWithIllegalCharacters=contains Illegal Chars ([space], #, :, [quotes]), will be replaced with (-)\nMarshallingMessage.dataObjectWithInvalidName=Data Object with Invalid Name Exists\n\n\nSessionCardinalityStateHandler.EmptyStateCaption=Click, drag or interact with a node\nSessionCardinalityStateHandler.EmptyStateMessage=To start, click or drag a node in the left-hand palette onto the canvas";
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
      case 'getContents': return this.@org.jboss.errai.ui.client.local.spi.TranslationServiceImpl.org_kie_workbench_common_stunner_client_widgets_resources_i18n_StunnerWidgetsConstants_default_properties::getContents()();
    }
    return null;
  }-*/;
}

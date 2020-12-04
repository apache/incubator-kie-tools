package org.jboss.errai.ui.client.local.spi;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ResourcePrototype;

public class TranslationServiceImpl_org_kie_workbench_common_widgets_client_resources_i18n_KieWorkbenchWidgetsConstants_de_properties_default_InlineClientBundleGenerator implements org.jboss.errai.ui.client.local.spi.TranslationServiceImpl.org_kie_workbench_common_widgets_client_resources_i18n_KieWorkbenchWidgetsConstants_de_properties {
  private static TranslationServiceImpl_org_kie_workbench_common_widgets_client_resources_i18n_KieWorkbenchWidgetsConstants_de_properties_default_InlineClientBundleGenerator _instance0 = new TranslationServiceImpl_org_kie_workbench_common_widgets_client_resources_i18n_KieWorkbenchWidgetsConstants_de_properties_default_InlineClientBundleGenerator();
  private void getContentsInitializer() {
    getContents = new com.google.gwt.resources.client.TextResource() {
      // jar:file:/Users/tiagobento/redhat/kiegroup-all/kie-wb-common/kie-wb-common-widgets/kie-wb-common-ui/target/kie-wb-common-ui-7.47.0-SNAPSHOT.jar!/org/kie/workbench/common/widgets/client/resources/i18n/KieWorkbenchWidgetsConstants_de.properties
      public String getText() {
        return "# translation auto-copied from project KIE Workbench - Common, version 6.0.0, document org.kie.workbench.widgets/kie-wb-common-ui/org/kie/workbench/common/widgets/client/resources/i18n/NewItemPopupConstants, author jdimanos\nNewResourceViewImpl.popupTitle=Neu erstellen\n# translation auto-copied from project KIE Workbench - Common, version 6.0.0, document org.kie.workbench.widgets/kie-wb-common-ui/org/kie/workbench/common/widgets/client/resources/i18n/NewItemPopupConstants, author jdimanos\nNewResourceViewImpl.itemNameSubheading=Name\\:\n# translation auto-copied from project KIE Workbench - Common, version 6.0.0, document org.kie.workbench.widgets/kie-wb-common-ui/org/kie/workbench/common/widgets/client/resources/i18n/NewItemPopupConstants, author jdimanos\nNewResourceViewImpl.fileNameIsMandatory=Fehlender Name für neue Ressource. Bitte eingeben.\n# translation auto-copied from project KIE Workbench - Common, version 6.0.0, document org.kie.workbench.widgets/kie-wb-common-ui/org/kie/workbench/common/widgets/client/resources/i18n/NewItemPopupConstants, author jdimanos\nNewResourceViewImpl.resourceName=Ressurcen-Name\n# translation auto-copied from project KIE Workbench - Common, version 6.2.0, document org.kie.workbench.widgets/kie-wb-common-ui/org/kie/workbench/common/widgets/client/resources/i18n/NewItemPopupConstants, author jdimanos\nNewResourceViewImpl.packageName=Paket\n# translation auto-copied from project KIE Workbench - Common, version 6.2.0, document org.kie.workbench.widgets/kie-wb-common-ui/org/kie/workbench/common/widgets/client/resources/i18n/NewItemPopupConstants, author jdimanos\nNewResourceViewImpl.resourceNamePlaceholder=Name...\nNewResourceViewImpl.MissingPath=Pfad, in dem neue Ressource erstellt werden soll, fehlt. Bitte eingeben.\n";
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
      case 'getContents': return this.@org.jboss.errai.ui.client.local.spi.TranslationServiceImpl.org_kie_workbench_common_widgets_client_resources_i18n_KieWorkbenchWidgetsConstants_de_properties::getContents()();
    }
    return null;
  }-*/;
}

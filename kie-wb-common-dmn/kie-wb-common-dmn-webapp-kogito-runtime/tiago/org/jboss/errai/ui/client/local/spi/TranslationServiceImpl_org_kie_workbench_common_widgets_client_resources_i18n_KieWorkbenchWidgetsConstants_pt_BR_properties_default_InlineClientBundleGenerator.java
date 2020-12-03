package org.jboss.errai.ui.client.local.spi;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ResourcePrototype;

public class TranslationServiceImpl_org_kie_workbench_common_widgets_client_resources_i18n_KieWorkbenchWidgetsConstants_pt_BR_properties_default_InlineClientBundleGenerator implements org.jboss.errai.ui.client.local.spi.TranslationServiceImpl.org_kie_workbench_common_widgets_client_resources_i18n_KieWorkbenchWidgetsConstants_pt_BR_properties {
  private static TranslationServiceImpl_org_kie_workbench_common_widgets_client_resources_i18n_KieWorkbenchWidgetsConstants_pt_BR_properties_default_InlineClientBundleGenerator _instance0 = new TranslationServiceImpl_org_kie_workbench_common_widgets_client_resources_i18n_KieWorkbenchWidgetsConstants_pt_BR_properties_default_InlineClientBundleGenerator();
  private void getContentsInitializer() {
    getContents = new com.google.gwt.resources.client.TextResource() {
      // jar:file:/Users/tiagobento/.m2/repository/org/kie/workbench/widgets/kie-wb-common-ui/7.47.0-SNAPSHOT/kie-wb-common-ui-7.47.0-SNAPSHOT.jar!/org/kie/workbench/common/widgets/client/resources/i18n/KieWorkbenchWidgetsConstants_pt_BR.properties
      public String getText() {
        return "# translation auto-copied from project KIE Workbench - Common, version 6.0.0, document org.kie.workbench.widgets/kie-wb-common-ui/org/kie/workbench/common/widgets/client/resources/i18n/NewItemPopupConstants, author ldelima@redhat.com\nNewResourceViewImpl.popupTitle=Criar Novo\n# translation auto-copied from project KIE Workbench - Common, version 6.0.0, document org.kie.workbench.widgets/kie-wb-common-ui/org/kie/workbench/common/widgets/client/resources/i18n/NewItemPopupConstants, author ldelima@redhat.com\nNewResourceViewImpl.itemNameSubheading=Nome\\:\n# translation auto-copied from project KIE Workbench - Common, version 6.0.0, document org.kie.workbench.widgets/kie-wb-common-ui/org/kie/workbench/common/widgets/client/resources/i18n/NewItemPopupConstants, author ldelima@redhat.com\nNewResourceViewImpl.fileNameIsMandatory=Falta nome para o novo recurso. Por insira-o.\n# translation auto-copied from project KIE Workbench - Common, version 6.0.0, document org.kie.workbench.widgets/kie-wb-common-ui/org/kie/workbench/common/widgets/client/resources/i18n/NewItemPopupConstants, author ldelima@redhat.com\nNewResourceViewImpl.resourceName=Nome de Recurso\n# translation auto-copied from project KIE Workbench - Common, version 6.2.0, document org.kie.workbench.widgets/kie-wb-common-ui/org/kie/workbench/common/widgets/client/resources/i18n/NewItemPopupConstants, author ldelima@redhat.com\nNewResourceViewImpl.packageName=Pacote\n# translation auto-copied from project KIE Workbench - Common, version 6.2.0, document org.kie.workbench.widgets/kie-wb-common-ui/org/kie/workbench/common/widgets/client/resources/i18n/NewItemPopupConstants, author ldelima@redhat.com\nNewResourceViewImpl.resourceNamePlaceholder=Nome...\nNewResourceViewImpl.MissingPath=Falta do caminho que cria novo recurso. Por favor insira-o.\n";
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
      case 'getContents': return this.@org.jboss.errai.ui.client.local.spi.TranslationServiceImpl.org_kie_workbench_common_widgets_client_resources_i18n_KieWorkbenchWidgetsConstants_pt_BR_properties::getContents()();
    }
    return null;
  }-*/;
}

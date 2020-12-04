package org.jboss.errai.ui.client.local.spi;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ResourcePrototype;

public class TranslationServiceImpl_org_kie_workbench_common_widgets_client_resources_i18n_KieWorkbenchWidgetsConstants_properties_default_InlineClientBundleGenerator implements org.jboss.errai.ui.client.local.spi.TranslationServiceImpl.org_kie_workbench_common_widgets_client_resources_i18n_KieWorkbenchWidgetsConstants_properties {
  private static TranslationServiceImpl_org_kie_workbench_common_widgets_client_resources_i18n_KieWorkbenchWidgetsConstants_properties_default_InlineClientBundleGenerator _instance0 = new TranslationServiceImpl_org_kie_workbench_common_widgets_client_resources_i18n_KieWorkbenchWidgetsConstants_properties_default_InlineClientBundleGenerator();
  private void getContentsInitializer() {
    getContents = new com.google.gwt.resources.client.TextResource() {
      // jar:file:/Users/tiagobento/redhat/kiegroup-all/kie-wb-common/kie-wb-common-widgets/kie-wb-common-ui/target/kie-wb-common-ui-7.47.0-SNAPSHOT.jar!/org/kie/workbench/common/widgets/client/resources/i18n/KieWorkbenchWidgetsConstants_default.properties
      public String getText() {
        return "KSessionSelectorViewImpl.KnowledgeBase=KIE base\nKSessionSelectorViewImpl.KnowledgeSession=KIE session\nKSessionSelectorViewImpl.SelectedKSessionDoesNotExist=Selected KIE session does not exist.\n\nNewResourceViewImpl.popupTitle=Create new\nNewResourceViewImpl.itemNameSubheading=Name:\nNewResourceViewImpl.fileNameIsMandatory=Missing name for new resource. Please enter.\nNewResourceViewImpl.resourceName=Resource Name\nNewResourceViewImpl.packageName=Package\nNewResourceViewImpl.resourceNamePlaceholder=Name...\nNewResourceViewImpl.MissingPath=Path in which to create new resource is missing. Please enter.\n\nValidationPopup.YesSaveAnyway=Yes, save anyway\nValidationPopup.YesCopyAnyway=Yes, copy anyway\nValidationPopup.YesDeleteAnyway=Yes, delete anyway\nValidationPopup.Cancel=Cancel\nValidationPopupViewImpl.ValidationErrors=Validation errors\n\nAboutPopupView.Version=Version\nAboutPopupView.LicenseDescription=is open source software, released under the\nAboutPopupView.License=Apache Software License 2.0\n\nKieAssetsDropdownView.Select=Select\n\nSearchBarComponentView.Find=Find...\nSearchBarComponentView.Of=of\n";
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
      case 'getContents': return this.@org.jboss.errai.ui.client.local.spi.TranslationServiceImpl.org_kie_workbench_common_widgets_client_resources_i18n_KieWorkbenchWidgetsConstants_properties::getContents()();
    }
    return null;
  }-*/;
}

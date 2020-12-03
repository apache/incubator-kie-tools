package org.jboss.errai.ui.client.local.spi;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ResourcePrototype;

public class TranslationServiceImpl_org_uberfire_ext_preferences_client_resources_i18n_Constants_pt_BR_properties_default_InlineClientBundleGenerator implements org.jboss.errai.ui.client.local.spi.TranslationServiceImpl.org_uberfire_ext_preferences_client_resources_i18n_Constants_pt_BR_properties {
  private static TranslationServiceImpl_org_uberfire_ext_preferences_client_resources_i18n_Constants_pt_BR_properties_default_InlineClientBundleGenerator _instance0 = new TranslationServiceImpl_org_uberfire_ext_preferences_client_resources_i18n_Constants_pt_BR_properties_default_InlineClientBundleGenerator();
  private void getContentsInitializer() {
    getContents = new com.google.gwt.resources.client.TextResource() {
      // jar:file:/Users/tiagobento/.m2/repository/org/uberfire/uberfire-preferences-ui-client/7.47.0-SNAPSHOT/uberfire-preferences-ui-client-7.47.0-SNAPSHOT.jar!/org/uberfire/ext/preferences/client/resources/i18n/Constants_pt_BR.properties
      public String getText() {
        return "# translation auto-copied from project Drools Workbench, version 6.0.0, document org.drools/drools-wb-guided-rule-editor-client/org/drools/workbench/screens/guided/rule/client/resources/i18n/Constants, author ldelima@redhat.com\nAdmin=Admin\n";
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
      case 'getContents': return this.@org.jboss.errai.ui.client.local.spi.TranslationServiceImpl.org_uberfire_ext_preferences_client_resources_i18n_Constants_pt_BR_properties::getContents()();
    }
    return null;
  }-*/;
}

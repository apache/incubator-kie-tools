package org.jboss.errai.ui.client.local.spi;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ResourcePrototype;

public class TranslationServiceImpl_org_uberfire_experimental_client_resources_i18n_UberfireExperimentalConstants_properties_default_InlineClientBundleGenerator implements org.jboss.errai.ui.client.local.spi.TranslationServiceImpl.org_uberfire_experimental_client_resources_i18n_UberfireExperimentalConstants_properties {
  private static TranslationServiceImpl_org_uberfire_experimental_client_resources_i18n_UberfireExperimentalConstants_properties_default_InlineClientBundleGenerator _instance0 = new TranslationServiceImpl_org_uberfire_experimental_client_resources_i18n_UberfireExperimentalConstants_properties_default_InlineClientBundleGenerator();
  private void getContentsInitializer() {
    getContents = new com.google.gwt.resources.client.TextResource() {
      // jar:file:/Users/tiagobento/.m2/repository/org/uberfire/uberfire-experimental-client/7.47.0-SNAPSHOT/uberfire-experimental-client-7.47.0-SNAPSHOT.jar!/org/uberfire/experimental/client/resources/i18n/UberfireExperimentalConstants_default.properties
      public String getText() {
        return "ExperimentalFeaturesEditorScreenViewImpl.experimentalFeaturesTitle=Experimental Features\nExperimentalFeaturesEditorScreenViewImpl.experimentalFeaturesWarning=These are experimental features that are under development, \\\n  using them in production environment may cause instability. Please use them only for demo purposes.\n\nDisabledFeatureComponentViewImpl.header=Attention\nDisabledExperimentalFeature=Cannot display content:  the <b>{0}</b> is an experimental feature that it is currently disabled. Please try going to the Experimental Features Editor and enable it.\nDisabledGlobalExperimentalFeature=Cannot display content:  the <b>{0}</b> is an experimental feature currently disabled. If you want to see it, please contact your administrator.\nDisabledFeatureTitle=Disabled Feature\n\nexperimentalFeatures.generalGroup=General\nexperimentalFeatures.globalGroup=Administration\n\nExperimentalFeaturesGroup.enableAll=Enable all\n\nExperimentalFeaturesGroup.disableAll=Disable all\n\nexperimentalFeatures.global=Global Experimental Features\nexperimentalFeatures.globalHelp=Sets who can Update the Global Experimental Features at the Administration perspective.\nexperimentalFeatures.globalEdit=Edit";
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
      case 'getContents': return this.@org.jboss.errai.ui.client.local.spi.TranslationServiceImpl.org_uberfire_experimental_client_resources_i18n_UberfireExperimentalConstants_properties::getContents()();
    }
    return null;
  }-*/;
}

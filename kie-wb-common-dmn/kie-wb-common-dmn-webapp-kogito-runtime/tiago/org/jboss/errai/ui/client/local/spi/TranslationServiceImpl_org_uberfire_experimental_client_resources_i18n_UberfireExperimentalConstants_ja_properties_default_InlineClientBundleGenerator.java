package org.jboss.errai.ui.client.local.spi;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ResourcePrototype;

public class TranslationServiceImpl_org_uberfire_experimental_client_resources_i18n_UberfireExperimentalConstants_ja_properties_default_InlineClientBundleGenerator implements org.jboss.errai.ui.client.local.spi.TranslationServiceImpl.org_uberfire_experimental_client_resources_i18n_UberfireExperimentalConstants_ja_properties {
  private static TranslationServiceImpl_org_uberfire_experimental_client_resources_i18n_UberfireExperimentalConstants_ja_properties_default_InlineClientBundleGenerator _instance0 = new TranslationServiceImpl_org_uberfire_experimental_client_resources_i18n_UberfireExperimentalConstants_ja_properties_default_InlineClientBundleGenerator();
  private void getContentsInitializer() {
    getContents = new com.google.gwt.resources.client.TextResource() {
      // jar:file:/Users/tiagobento/.m2/repository/org/uberfire/uberfire-experimental-client/7.47.0-SNAPSHOT/uberfire-experimental-client-7.47.0-SNAPSHOT.jar!/org/uberfire/experimental/client/resources/i18n/UberfireExperimentalConstants_ja.properties
      public String getText() {
        return "ExperimentalFeaturesEditorScreenViewImpl.experimentalFeaturesTitle=実験的機能\nExperimentalFeaturesEditorScreenViewImpl.experimentalFeaturesWarning=これらの実験的機能は開発中であり、実稼働環境で使用すると、システムが不安定になる可能性があります。使用する場合は、デモ目的のみとしてください。\n\nDisabledFeatureComponentViewImpl.header=注意\nDisabledExperimentalFeature=コンテンツを表示できません: <b>{0}</b>  は実験的機能であり、現在無効になっています。実験的機能エディターにアクセスして有効にしてください。\nDisabledGlobalExperimentalFeature=コンテンツを表示できません: <b>{0}</b>  は実験的機能であり、現在無効になっています。表示するには、管理者に連絡してください。\nDisabledFeatureTitle=無効な機能\n\nexperimentalFeatures.generalGroup=全般\nexperimentalFeatures.globalGroup=管理\n\nExperimentalFeaturesGroup.enableAll=すべて有効にする\n\nExperimentalFeaturesGroup.disableAll=すべて無効にする\n\nexperimentalFeatures.global=グローバル実験的機能\nexperimentalFeatures.globalHelp=管理パースペクティブでグローバル実験的機能を更新可能なユーザーを設定する\nexperimentalFeatures.globalEdit=編集";
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
      case 'getContents': return this.@org.jboss.errai.ui.client.local.spi.TranslationServiceImpl.org_uberfire_experimental_client_resources_i18n_UberfireExperimentalConstants_ja_properties::getContents()();
    }
    return null;
  }-*/;
}

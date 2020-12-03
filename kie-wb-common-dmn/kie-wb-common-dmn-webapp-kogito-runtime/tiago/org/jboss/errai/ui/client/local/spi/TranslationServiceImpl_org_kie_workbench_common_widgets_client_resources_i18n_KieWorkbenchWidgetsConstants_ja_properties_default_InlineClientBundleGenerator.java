package org.jboss.errai.ui.client.local.spi;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ResourcePrototype;

public class TranslationServiceImpl_org_kie_workbench_common_widgets_client_resources_i18n_KieWorkbenchWidgetsConstants_ja_properties_default_InlineClientBundleGenerator implements org.jboss.errai.ui.client.local.spi.TranslationServiceImpl.org_kie_workbench_common_widgets_client_resources_i18n_KieWorkbenchWidgetsConstants_ja_properties {
  private static TranslationServiceImpl_org_kie_workbench_common_widgets_client_resources_i18n_KieWorkbenchWidgetsConstants_ja_properties_default_InlineClientBundleGenerator _instance0 = new TranslationServiceImpl_org_kie_workbench_common_widgets_client_resources_i18n_KieWorkbenchWidgetsConstants_ja_properties_default_InlineClientBundleGenerator();
  private void getContentsInitializer() {
    getContents = new com.google.gwt.resources.client.TextResource() {
      // jar:file:/Users/tiagobento/.m2/repository/org/kie/workbench/widgets/kie-wb-common-ui/7.47.0-SNAPSHOT/kie-wb-common-ui-7.47.0-SNAPSHOT.jar!/org/kie/workbench/common/widgets/client/resources/i18n/KieWorkbenchWidgetsConstants_ja.properties
      public String getText() {
        return "KSessionSelectorViewImpl.KnowledgeBase=Kie ベース\nKSessionSelectorViewImpl.KnowledgeSession=Kie セッション\nKSessionSelectorViewImpl.SelectedKSessionDoesNotExist=選択した KIE セッション は存在しません。\n\nNewResourceViewImpl.popupTitle=新規作成\nNewResourceViewImpl.itemNameSubheading=名前:\nNewResourceViewImpl.fileNameIsMandatory=新しいリソースの名前がありません。入力してください。\nNewResourceViewImpl.resourceName=リソース名\nNewResourceViewImpl.packageName=パッケージ\nNewResourceViewImpl.resourceNamePlaceholder=名前...\nNewResourceViewImpl.MissingPath=新しいリソースを作成するパスがありません。入力してください。\n\nValidationPopup.YesSaveAnyway=はい、保存します\nValidationPopup.YesCopyAnyway=はい、コピーします\nValidationPopup.YesDeleteAnyway=はい、削除します\nValidationPopup.Cancel=キャンセル\nValidationPopupViewImpl.ValidationErrors=検証エラー\n\nAboutPopupView.Version=バージョン\nAboutPopupView.LicenseDescription=以下でリリースされたオープンソースソフトウェアです\nAboutPopupView.License=Apache Software License 2.0\n\nKieAssetsDropdownView.Select=選択\n\nSearchBarComponentView.Find=検索...\nSearchBarComponentView.Of=/\n\n";
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
      case 'getContents': return this.@org.jboss.errai.ui.client.local.spi.TranslationServiceImpl.org_kie_workbench_common_widgets_client_resources_i18n_KieWorkbenchWidgetsConstants_ja_properties::getContents()();
    }
    return null;
  }-*/;
}

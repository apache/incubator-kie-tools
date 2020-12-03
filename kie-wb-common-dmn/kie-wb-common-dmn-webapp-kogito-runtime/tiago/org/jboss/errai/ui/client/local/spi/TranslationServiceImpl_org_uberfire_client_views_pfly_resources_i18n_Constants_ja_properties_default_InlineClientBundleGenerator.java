package org.jboss.errai.ui.client.local.spi;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ResourcePrototype;

public class TranslationServiceImpl_org_uberfire_client_views_pfly_resources_i18n_Constants_ja_properties_default_InlineClientBundleGenerator implements org.jboss.errai.ui.client.local.spi.TranslationServiceImpl.org_uberfire_client_views_pfly_resources_i18n_Constants_ja_properties {
  private static TranslationServiceImpl_org_uberfire_client_views_pfly_resources_i18n_Constants_ja_properties_default_InlineClientBundleGenerator _instance0 = new TranslationServiceImpl_org_uberfire_client_views_pfly_resources_i18n_Constants_ja_properties_default_InlineClientBundleGenerator();
  private void getContentsInitializer() {
    getContents = new com.google.gwt.resources.client.TextResource() {
      // jar:file:/Users/tiagobento/.m2/repository/org/uberfire/uberfire-workbench-client-views-patternfly/7.47.0-SNAPSHOT/uberfire-workbench-client-views-patternfly-7.47.0-SNAPSHOT.jar!/org/uberfire/client/views/pfly/resources/i18n/Constants_ja.properties
      public String getText() {
        return "Actions=アクション\nApplyLabel=適用\nCancelLabel=キャンセル\nFromLabel=から\nToLabel=終了\nCustomRangeLabel=カスタム\nWeekLabel=週\nSundayShort=日\nMondayShort=月\nTuesdayShort=火\nWednesdayShort=水\nThursdayShort=木\nFridayShort=金\nSaturdayShort=土\nJanuary=1月\nFebruary=2 月\nMarch=3 月\nApril=4 月\nMay=5 月\nJune=6 月\nJuly=7 月\nAugust=8月\nSeptember=9月\nOctober=10月\nNovember=11月\nDecember=12月\nMenu=メニュー\nHome=ホーム\n\nErrorPopupView.PopupTitle=エラー\nErrorPopupView.ShowDetailLabel=詳細表示\nErrorPopupView.CloseDetailLabel=詳細を閉じる\nErrorPopupView.Close=閉じる\n\nConfirmPopup.Cancel=キャンセル";
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
      case 'getContents': return this.@org.jboss.errai.ui.client.local.spi.TranslationServiceImpl.org_uberfire_client_views_pfly_resources_i18n_Constants_ja_properties::getContents()();
    }
    return null;
  }-*/;
}

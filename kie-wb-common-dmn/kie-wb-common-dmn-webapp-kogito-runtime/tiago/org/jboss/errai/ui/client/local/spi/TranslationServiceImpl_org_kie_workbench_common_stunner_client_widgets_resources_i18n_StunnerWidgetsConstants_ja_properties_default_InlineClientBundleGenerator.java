package org.jboss.errai.ui.client.local.spi;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ResourcePrototype;

public class TranslationServiceImpl_org_kie_workbench_common_stunner_client_widgets_resources_i18n_StunnerWidgetsConstants_ja_properties_default_InlineClientBundleGenerator implements org.jboss.errai.ui.client.local.spi.TranslationServiceImpl.org_kie_workbench_common_stunner_client_widgets_resources_i18n_StunnerWidgetsConstants_ja_properties {
  private static TranslationServiceImpl_org_kie_workbench_common_stunner_client_widgets_resources_i18n_StunnerWidgetsConstants_ja_properties_default_InlineClientBundleGenerator _instance0 = new TranslationServiceImpl_org_kie_workbench_common_stunner_client_widgets_resources_i18n_StunnerWidgetsConstants_ja_properties_default_InlineClientBundleGenerator();
  private void getContentsInitializer() {
    getContents = new com.google.gwt.resources.client.TextResource() {
      // jar:file:/Users/tiagobento/redhat/kiegroup-all/kie-wb-common/kie-wb-common-stunner/kie-wb-common-stunner-client/kie-wb-common-stunner-widgets/target/kie-wb-common-stunner-widgets-7.47.0-SNAPSHOT.jar!/org/kie/workbench/common/stunner/client/widgets/resources/i18n/StunnerWidgetsConstants_ja.properties
      public String getText() {
        return "DefinitionPaletteGroupWidgetViewImpl.showMore=詳細表示\nDefinitionPaletteGroupWidgetViewImpl.showLess=簡易表示\nNameEditBoxWidgetViewImpl.save=保存\nNameEditBoxWidgetViewImpl.close=閉じる\nNameEditBoxWidgetViewImpl.name=Name\nSessionPresenterView.Error=エラー\nSessionPresenterView.Warning=警告\nSessionPresenterView.Info=情報\nSessionPresenterView.Notifications=警告パネルの詳細\n\nMarshallingResponsePopup.OkAction=OK\nMarshallingResponsePopup.CancelAction=キャンセル\nMarshallingResponsePopup.CopyToClipboardActionTitle=メッセージをクリップボードにコピーする\nMarshallingResponsePopup.LevelTableColumnName=レベル\nMarshallingResponsePopup.MessageTableColumnName=メッセージ\n\nMarshallingResponsePopup.ErrorMessageLabel=エラー\nMarshallingResponsePopup.WarningMessageLabel=警告\nMarshallingResponsePopup.InfoMessageLabel=情報\nMarshallingResponsePopup.UnknownMessageLabel=不明\n\nMarshallingMessage.boundaryIgnored=境界関係は無視されました。境界要素: {0}、親: {1}\nMarshallingMessage.associationIgnored=関連付けが無視されました。ソース: {0}、ターゲット: {1}\nMarshallingMessage.sequenceFlowIgnored=シーケンスフローが無視されました。ソース: {0}、ターゲット: {1}\nMarshallingMessage.collapsedElementExpanded=タイプ {1} の折りたたまれていた要素 {0} が展開されました\nMarshallingMessage.ignoredElement=タイプ {1} の要素 {0} が無視されました\nMarshallingMessage.ignoredUnknownElement=不明な要素 {0} が無視されました\nMarshallingMessage.childLaneSetConverted=子のレーンセット {0} がレーン {1} に変換されました\nMarshallingMessage.convertedElement=タイプ {1} の要素 {0} が {2} に変換されました\nMarshallingMessage.elementFailure=タイプ {1} の要素 {0} が失敗しました\nSessionCardinalityStateHandler.EmptyStateCaption=ノードをクリックまたはドラッグするか、もしくはノードと対話します\nSessionCardinalityStateHandler.EmptyStateMessage=開始するには、左側のパレットのノードをクリックするか、キャンバスにドラッグします";
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
      case 'getContents': return this.@org.jboss.errai.ui.client.local.spi.TranslationServiceImpl.org_kie_workbench_common_stunner_client_widgets_resources_i18n_StunnerWidgetsConstants_ja_properties::getContents()();
    }
    return null;
  }-*/;
}

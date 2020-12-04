package org.jboss.errai.ui.client.local.spi;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ResourcePrototype;

public class TranslationServiceImpl_org_kie_workbench_common_forms_dynamic_client_resources_i18n_FormRenderingConstants_ja_properties_default_InlineClientBundleGenerator implements org.jboss.errai.ui.client.local.spi.TranslationServiceImpl.org_kie_workbench_common_forms_dynamic_client_resources_i18n_FormRenderingConstants_ja_properties {
  private static TranslationServiceImpl_org_kie_workbench_common_forms_dynamic_client_resources_i18n_FormRenderingConstants_ja_properties_default_InlineClientBundleGenerator _instance0 = new TranslationServiceImpl_org_kie_workbench_common_forms_dynamic_client_resources_i18n_FormRenderingConstants_ja_properties_default_InlineClientBundleGenerator();
  private void getContentsInitializer() {
    getContents = new com.google.gwt.resources.client.TextResource() {
      // jar:file:/Users/tiagobento/redhat/kiegroup-all/kie-wb-common/kie-wb-common-forms/kie-wb-common-dynamic-forms/kie-wb-common-dynamic-forms-client/target/kie-wb-common-dynamic-forms-client-7.47.0-SNAPSHOT.jar!/org/kie/workbench/common/forms/dynamic/client/resources/i18n/FormRenderingConstants_ja.properties
      public String getText() {
        return "FieldConfigErrorViewImpl.unableToDisplayField=フィールド表示不可:\n\nMultipleSubform.noColumns=カラムが定義されていません。\nMultipleSubform.noCreationForm=作成フォームが選択されていません。\nMultipleSubform.wrongCreationForm=作成フォーム設定に誤りがあります。\nMultipleSubform.noEditionForm=編集フォームが定義されていません。\nMultipleSubform.wrongEditionForm=編集フォーム設定に誤りがあります。\n\nSubForm.noForm=フォームが選択されていません。\nSubForm.wrongForm=誤ったフォームが選択されています。\n\nFieldProperties.label=ラベル\nFieldProperties.readOnly=読み込み専用\nFieldProperties.required=必須\nFieldProperties.validateOnChange=フィールド値の変更時に検証\nFieldProperties.maxLength=最大長さ\nFieldProperties.placeHolder=プレースホルダー\nFieldProperties.helpMessage=ヘルプメッセージ\nFieldProperties.helpMessage.helpMessage=このフィールドにヘルプメッセージ（HTMLフォーマットをサポートします）を設定すると、フィールドラベルの隣に表示されます。\nFieldProperties.defaultValue=デフォルト値\nFieldProperties.rows=可視行\nFieldProperties.addEmptyOption=デフォルトの空のオプションを追加\n\nFieldProperties.showTime=時間表示\n\nFieldProperties.picture.size=画像サイズ\n\nFieldProperties.selector.options=オプション\nFieldProperties.selector.options.value=値\nFieldProperties.selector.options.text=テキスト\n\nFieldProperties.radios.inline=インラインでオプションを表示\n\nFieldProperties.slider.min=最小値\nFieldProperties.slider.max=最大値\nFieldProperties.slider.step=手順\nFieldProperties.slider.precision=精度\n\nFieldProperties.nestedForm=入れ子フォーム\n\nFieldProperties.mask=値マスク\n\nFieldProperties.multipleSubform.creationForm=作成フォーム\nFieldProperties.multipleSubform.editionForm=編集フォーム\nFieldProperties.multipleSubform.columns=テーブルカラム\nFieldProperties.multipleSubform.columns.label=キャプション\nFieldProperties.multipleSubform.columns.property=プロパティー\n\nListBoxFieldRenderer.emptyOptionText=-- 値を選択 --\n\nFieldProperties.maxDropdownElements=ドロップダウンで表示される要素\nFieldProperties.maxElementsOnTitle=ドロップダウンタイトルで表示される要素\nFieldProperties.allowFilter=検索フィルターの表示\nFieldProperties.allowClearSelection=選択アクションのクリアを表示\n\nFieldProperties.listOfValues=セレクターアイテム\n\nFieldProperties.pageSize=ページサイズ\n\nLOVCreationComponentViewImpl.addButton=新規の追加\nLOVCreationComponentViewImpl.removeButton=選択したアイテムの削除\nLOVCreationComponentViewImpl.moveUp=選択したアイテムを上に移動\nLOVCreationComponentViewImpl.moveDown=選択したアイテムを下に移動\nLOVCreationComponentViewImpl.noItems=アイテムが見つかりません\n\nEditableColumnGenerator.valueHeader=値\n\nCharacterEditableColumnGenerator.validationError=誤った値: 要素長は 1 文字でなければなりません\n\nInvalidInteger=誤った値:  値は整数でなければなりません。\nInvalidIntegerWithRange=誤った値: 値は {0} から {1} の整数でなければなりません。\nInvalidDecimal=誤った値:  値は十進数でなければなりません。\nInvalidDecimalWithRange=誤った値: 値は {0} から {1} の十進数でなければなりません。\n\nDecimalEditableColumnGenerator.invalidNumber=誤った値:  値は十進数でなければなりません。\nBooleanEditableColumnGenerator.yes=True\nBooleanEditableColumnGenerator.no=False\n\nDatePickerWrapperViewImpl.showDateTooltip=表示する\nDatePickerWrapperViewImpl.clearDateTooltip=消去";
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
      case 'getContents': return this.@org.jboss.errai.ui.client.local.spi.TranslationServiceImpl.org_kie_workbench_common_forms_dynamic_client_resources_i18n_FormRenderingConstants_ja_properties::getContents()();
    }
    return null;
  }-*/;
}

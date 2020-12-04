package org.jboss.errai.ui.client.local.spi;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ResourcePrototype;

public class TranslationServiceImpl_org_kie_workbench_common_stunner_core_resources_i18n_StunnerCoreConstants_ja_properties_default_InlineClientBundleGenerator implements org.jboss.errai.ui.client.local.spi.TranslationServiceImpl.org_kie_workbench_common_stunner_core_resources_i18n_StunnerCoreConstants_ja_properties {
  private static TranslationServiceImpl_org_kie_workbench_common_stunner_core_resources_i18n_StunnerCoreConstants_ja_properties_default_InlineClientBundleGenerator _instance0 = new TranslationServiceImpl_org_kie_workbench_common_stunner_core_resources_i18n_StunnerCoreConstants_ja_properties_default_InlineClientBundleGenerator();
  private void getContentsInitializer() {
    getContents = new com.google.gwt.resources.client.TextResource() {
      // jar:file:/Users/tiagobento/redhat/kiegroup-all/kie-wb-common/kie-wb-common-stunner/kie-wb-common-stunner-core/kie-wb-common-stunner-commons/kie-wb-common-stunner-core-common/target/kie-wb-common-stunner-core-common-7.47.0-SNAPSHOT.jar!/org/kie/workbench/common/stunner/core/resources/i18n/StunnerCoreConstants_ja.properties
      public String getText() {
        return "#\n# Copyright 2017 Red Hat, Inc. and/or its affiliates.\n#\n# Licensed under the Apache License, Version 2.0 (the \"License\");\n# you may not use this file except in compliance with the License.\n# You may obtain a copy of the License at\n#\n#   http://www.apache.org/licenses/LICENSE-2.0\n#\n# Unless required by applicable law or agreed to in writing, software\n# distributed under the License is distributed on an \"AS IS\" BASIS,\n# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n# See the License for the specific language governing permissions and\n# limitations under the License.\n#\n\n#######################\n# Core messages\n#######################\norg.kie.workbench.common.stunner.core.error=エラー\norg.kie.workbench.common.stunner.core.warn=警告\norg.kie.workbench.common.stunner.core.info=情報\norg.kie.workbench.common.stunner.core.reason=理由\norg.kie.workbench.common.stunner.core.delete=削除\norg.kie.workbench.common.stunner.core.edit=編集\norg.kie.workbench.common.stunner.core.print=印刷\norg.kie.workbench.common.stunner.core.reset=リセット\norg.kie.workbench.common.stunner.core.increase=増加\norg.kie.workbench.common.stunner.core.decrease=減少\norg.kie.workbench.common.stunner.core.fit=適合\norg.kie.workbench.common.stunner.core.areYouSure=よろしいですか？\norg.kie.workbench.common.stunner.core.element_uuid=識別子\norg.kie.workbench.common.stunner.core.command.success=コマンドの実行が成功しました\norg.kie.workbench.common.stunner.core.command.fail=コマンドの実行が失敗しました\norg.kie.workbench.common.stunner.core.rule.success=検証が成功しました\norg.kie.workbench.common.stunner.core.rule.fail=検証が失敗しました\norg.kie.workbench.common.stunner.core.rule.element=[{0}]\\n {1}\norg.kie.workbench.common.stunner.core.rule.property=プロパティー ''{0}'' {1}。\norg.kie.workbench.common.stunner.core.client.mediator.zoomArea=ジャンプ先のエリアを選択\n\n\n#######################\n# Rule Violation Types\n#######################\norg.kie.workbench.common.stunner.core.rule.violations.ContainmentRuleViolation=''{0}'' は、候補のロール ''{1}'' に対する含有を容認できません\norg.kie.workbench.common.stunner.core.rule.violations.DockingRuleViolation=''{0}'' は、候補のロール ''{1}'' のドッキングを容認しません\norg.kie.workbench.common.stunner.core.rule.violations.ConnectionRuleViolation=ロール ''{0}'' の接線への接続は許可されていません。許可されている接続は ''{1}'' です\norg.kie.workbench.common.stunner.core.rule.violations.CardinalityMinRuleViolation=ダイアグラムには、以下のいずれかのロール ''{0}'' に対して、少なくとも {1} 個のオカレンスが含まれる必要があります。現在、{2} 個のオカレンスが見つかりました\norg.kie.workbench.common.stunner.core.rule.violations.CardinalityMaxRuleViolation=ダイアグラムには、以下のいずれかのロール ''{0}'' に対して、最大 {1} 個のオカレンスが含まれる必要があります。現在、{2} 個のオカレンスが見つかりました\norg.kie.workbench.common.stunner.core.rule.violations.EdgeCardinalityMinRuleViolation=ロール ''{0}'' のノードは、''{2}'' 方向の {1} 接線に対して、少なくとも {3} 個のオカレンスを持つことができます。現在、{4} 個のオカレンスが見つかりました\norg.kie.workbench.common.stunner.core.rule.violations.EdgeCardinalityMaxRuleViolation=ロール ''{0}'' のノードは、''{2}'' 方向の {1} 接線に対して、最大 {3} 個のオカレンスを持つことができます。現在、{4} 個のオカレンスが見つかりました\norg.kie.workbench.common.stunner.core.rule.violations.ContextOperationNotAllowedViolation={0} 操作は許可されていません\norg.kie.workbench.common.stunner.core.rule.violations.BoundsExceededViolation=候補 ''{0}''の境界を超えました。座標の最大許容値は [{1}、{2}] です\norg.kie.workbench.common.stunner.core.rule.violations.EmptyConnectionViolation=コネクター ''{0}'' には両方の接続が必要です。実際は [source=''{1}''、target=''{2}'']\n\n#######################\n# Toolbox\n#######################\norg.kie.workbench.common.stunner.core.client.toolbox.createNewConnector=作成\norg.kie.workbench.common.stunner.core.client.toolbox.createNewNode=作成\norg.kie.workbench.common.stunner.core.client.toolbox.morphInto=変更・\n\n#######################\n# Toolbar\n#######################\norg.kie.workbench.common.stunner.core.client.toolbox.CopySelection=選択範囲をコピー\norg.kie.workbench.common.stunner.core.client.toolbox.CutSelection=選択範囲を切り取り\norg.kie.workbench.common.stunner.core.client.toolbox.PasteSelection=選択範囲を貼り付け\norg.kie.workbench.common.stunner.core.client.toolbox.ClearShapes=シェイプの状態をクリア\norg.kie.workbench.common.stunner.core.client.toolbox.ClearDiagram=ダイアグラムのクリア\norg.kie.workbench.common.stunner.core.client.toolbox.DeleteSelection=選択範囲を削除 [DEL]\norg.kie.workbench.common.stunner.core.client.toolbox.ExportJPG=JPG のダウンロード\norg.kie.workbench.common.stunner.core.client.toolbox.ExportPDF=PDF のダウンロード\norg.kie.workbench.common.stunner.core.client.toolbox.ExportPNG=PNG のダウンロード\norg.kie.workbench.common.stunner.core.client.toolbox.ExportSVG=SVG のダウンロード\norg.kie.workbench.common.stunner.core.client.toolbox.ExportBPMN=BPMN のダウンロード\norg.kie.workbench.common.stunner.core.client.toolbox.Redo=やり直す [Ctrl+Shift+z]\norg.kie.workbench.common.stunner.core.client.toolbox.Save=保存\norg.kie.workbench.common.stunner.core.client.toolbox.SwitchGrid=グリッドへの切り替え\norg.kie.workbench.common.stunner.core.client.toolbox.Undo=元に戻す [Ctrl+z]\norg.kie.workbench.common.stunner.core.client.toolbox.Validate=検証\norg.kie.workbench.common.stunner.core.client.toolbox.VisitGraph=プレイ\norg.kie.workbench.common.stunner.core.client.toolbox.PerformAutomaticLayout=自動レイアウトの実行\n\norg.kie.workbench.common.stunner.core.client.toolbox.ConfirmClearDiagram=ダイアグラムをクリアすると、キャンバスからすべてのオブジェクトが永久に削除されます。このアクションを元に戻すことはできません。注意して進めてください。\n\n########################\n#ClientDiagramService\n########################\norg.kie.workbench.common.stunner.core.client.diagram.load.fail.unsupported=ダイアグラムをロードできません。サポートされていない要素があります: ''{0}''\norg.kie.workbench.common.stunner.core.client.diagram.automatic.layout.performed=ダイアグラムにレイアウト情報がなかったため、自動的にレイアウトされています。このレイアウトを保持するには、変更を保存してください。";
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
      case 'getContents': return this.@org.jboss.errai.ui.client.local.spi.TranslationServiceImpl.org_kie_workbench_common_stunner_core_resources_i18n_StunnerCoreConstants_ja_properties::getContents()();
    }
    return null;
  }-*/;
}

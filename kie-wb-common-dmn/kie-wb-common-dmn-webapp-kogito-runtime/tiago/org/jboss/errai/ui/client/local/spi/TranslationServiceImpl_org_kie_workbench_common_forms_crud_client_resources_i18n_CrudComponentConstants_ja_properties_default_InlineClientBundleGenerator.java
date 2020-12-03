package org.jboss.errai.ui.client.local.spi;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ResourcePrototype;

public class TranslationServiceImpl_org_kie_workbench_common_forms_crud_client_resources_i18n_CrudComponentConstants_ja_properties_default_InlineClientBundleGenerator implements org.jboss.errai.ui.client.local.spi.TranslationServiceImpl.org_kie_workbench_common_forms_crud_client_resources_i18n_CrudComponentConstants_ja_properties {
  private static TranslationServiceImpl_org_kie_workbench_common_forms_crud_client_resources_i18n_CrudComponentConstants_ja_properties_default_InlineClientBundleGenerator _instance0 = new TranslationServiceImpl_org_kie_workbench_common_forms_crud_client_resources_i18n_CrudComponentConstants_ja_properties_default_InlineClientBundleGenerator();
  private void getContentsInitializer() {
    getContents = new com.google.gwt.resources.client.TextResource() {
      // jar:file:/Users/tiagobento/.m2/repository/org/kie/workbench/forms/kie-wb-common-forms-crud-component/7.47.0-SNAPSHOT/kie-wb-common-forms-crud-component-7.47.0-SNAPSHOT.jar!/org/kie/workbench/common/forms/crud/client/resources/i18n/CrudComponentConstants_ja.properties
      public String getText() {
        return "CrudComponentViewImpl.newInstanceButton=新規インスタンス\nCrudComponentViewImpl.newInstanceTitle=新規インスタンス作成\nCrudComponentViewImpl.editInstanceButton=編集\nCrudComponentViewImpl.editInstanceTitle=インスタンスを編集\nCrudComponentViewImpl.deleteInstance=削除\nCrudComponentViewImpl.deleteBody=よろしいですか？\n\nModalFormDisplayerViewImpl.accept=承認\nModalFormDisplayerViewImpl.cancel=キャンセル\n\n";
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
      case 'getContents': return this.@org.jboss.errai.ui.client.local.spi.TranslationServiceImpl.org_kie_workbench_common_forms_crud_client_resources_i18n_CrudComponentConstants_ja_properties::getContents()();
    }
    return null;
  }-*/;
}

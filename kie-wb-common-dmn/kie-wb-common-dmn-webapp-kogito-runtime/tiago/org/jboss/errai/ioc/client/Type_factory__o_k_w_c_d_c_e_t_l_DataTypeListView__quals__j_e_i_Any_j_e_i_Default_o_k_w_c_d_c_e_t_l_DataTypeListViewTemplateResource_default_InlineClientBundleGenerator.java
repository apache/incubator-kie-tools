package org.jboss.errai.ioc.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ResourcePrototype;

public class Type_factory__o_k_w_c_d_c_e_t_l_DataTypeListView__quals__j_e_i_Any_j_e_i_Default_o_k_w_c_d_c_e_t_l_DataTypeListViewTemplateResource_default_InlineClientBundleGenerator implements org.jboss.errai.ioc.client.Type_factory__o_k_w_c_d_c_e_t_l_DataTypeListView__quals__j_e_i_Any_j_e_i_Default.o_k_w_c_d_c_e_t_l_DataTypeListViewTemplateResource {
  private static Type_factory__o_k_w_c_d_c_e_t_l_DataTypeListView__quals__j_e_i_Any_j_e_i_Default_o_k_w_c_d_c_e_t_l_DataTypeListViewTemplateResource_default_InlineClientBundleGenerator _instance0 = new Type_factory__o_k_w_c_d_c_e_t_l_DataTypeListView__quals__j_e_i_Any_j_e_i_Default_o_k_w_c_d_c_e_t_l_DataTypeListViewTemplateResource_default_InlineClientBundleGenerator();
  private void getContentsInitializer() {
    getContents = new com.google.gwt.resources.client.TextResource() {
      // jar:file:/Users/tiagobento/.m2/repository/org/kie/workbench/kie-wb-common-dmn-client/7.47.0-SNAPSHOT/kie-wb-common-dmn-client-7.47.0-SNAPSHOT-sources.jar!/org/kie/workbench/common/dmn/client/editors/types/listview/DataTypeListView.html
      public String getText() {
        return "<!--\n  ~ Copyright 2018 Red Hat, Inc. and/or its affiliates.\n  ~\n  ~ Licensed under the Apache License, Version 2.0 (the \"License\");\n  ~ you may not use this file except in compliance with the License.\n  ~ You may obtain a copy of the License at\n  ~\n  ~     http://www.apache.org/licenses/LICENSE-2.0\n  ~\n  ~ Unless required by applicable law or agreed to in writing, software\n  ~ distributed under the License is distributed on an \"AS IS\" BASIS,\n  ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n  ~ See the License for the specific language governing permissions and\n  ~ limitations under the License.\n  -->\n\n<div>\n    <h3 data-i18n-key=\"Title\"></h3>\n    <div data-field=\"data-type-button\" class=\"data-type-button hidden\">\n        <div class=\"expand-collapse\">\n            <a data-field=\"expand-all\" href=\"#\" data-i18n-key=\"ExpandAll\"></a>\n            <span>|</span>\n            <a data-field=\"collapse-all\" href=\"#\" data-i18n-key=\"CollapseAll\"></a>\n        </div>\n        <div class=\"search-bar-container\" data-field=\"search-bar-container\"></div>\n        <div class=\"action-button\">\n            <button data-field=\"add-button\" class=\"btn btn-default\">\n                <span data-i18n-key=\"NewDataType\"></span>\n            </button>\n            <button data-field=\"import-data-object-button\" class=\"btn btn-default\">\n                <span data-i18n-key=\"ImportDataObject\"></span>\n            </button>\n        </div>\n    </div>\n\n    <div data-field=\"placeholder\" class=\"hidden kie-data-types-placeholder\">\n        <div>\n            <i class=\"fa fa-object-group\"></i>\n            <br/>\n            <h1 data-i18n-key=\"NoCustomDataTitle\"></h1>\n            <p data-i18n-key=\"NoCustomData1\"></p>\n            <p data-i18n-key=\"NoCustomData2\"></p>\n            <button data-field=\"add-button-placeholder\" class=\"btn btn-primary\">\n                <i class=\"fa fa-plus-circle\"></i>\n                <span data-i18n-key=\"AddACustomDataType\"></span>\n            </button>\n        </div>\n    </div>\n    <div data-field=\"no-data-types-found\" class=\"hidden kie-data-types-placeholder\">\n        <div>\n            <i class=\"fa fa-search-minus\"></i>\n            <br/>\n            <h1 data-i18n-key=\"NoCustomDataTypesFound1\"></h1>\n            <p data-i18n-key=\"NoCustomDataTypesFound2\"></p>\n        </div>\n    </div>\n    <div data-field=\"read-only-message\" class=\"alert alert-info alert-dismissable opened hidden\">\n        <button data-field=\"read-only-message-close-button\" type=\"button\" class=\"close\">\n            <span class=\"pficon pficon-close\"></span>\n        </button>\n        <span class=\"pficon pficon-info\"></span>\n        <strong data-i18n-key=\"ReadOnlyMessage1\"></strong>\n        <span data-i18n-key=\"ReadOnlyMessage2\"></span>\n    </div>\n    <div data-field=\"list-items\" class=\"hidden pf-list-compound-expansion\">\n    </div>\n</div>\n";
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
      case 'getContents': return this.@org.jboss.errai.ioc.client.Type_factory__o_k_w_c_d_c_e_t_l_DataTypeListView__quals__j_e_i_Any_j_e_i_Default.o_k_w_c_d_c_e_t_l_DataTypeListViewTemplateResource::getContents()();
    }
    return null;
  }-*/;
}

package org.jboss.errai.ioc.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ResourcePrototype;

public class Type_factory__o_k_w_c_d_c_e_t_l_DataTypeListItemView__quals__j_e_i_Any_j_e_i_Default_o_k_w_c_d_c_e_t_l_DataTypeListItemViewTemplateResource_default_InlineClientBundleGenerator implements org.jboss.errai.ioc.client.Type_factory__o_k_w_c_d_c_e_t_l_DataTypeListItemView__quals__j_e_i_Any_j_e_i_Default.o_k_w_c_d_c_e_t_l_DataTypeListItemViewTemplateResource {
  private static Type_factory__o_k_w_c_d_c_e_t_l_DataTypeListItemView__quals__j_e_i_Any_j_e_i_Default_o_k_w_c_d_c_e_t_l_DataTypeListItemViewTemplateResource_default_InlineClientBundleGenerator _instance0 = new Type_factory__o_k_w_c_d_c_e_t_l_DataTypeListItemView__quals__j_e_i_Any_j_e_i_Default_o_k_w_c_d_c_e_t_l_DataTypeListItemViewTemplateResource_default_InlineClientBundleGenerator();
  private void getContentsInitializer() {
    getContents = new com.google.gwt.resources.client.TextResource() {
      // jar:file:/Users/tiagobento/.m2/repository/org/kie/workbench/kie-wb-common-dmn-client/7.47.0-SNAPSHOT/kie-wb-common-dmn-client-7.47.0-SNAPSHOT-sources.jar!/org/kie/workbench/common/dmn/client/editors/types/listview/DataTypeListItemView.html
      public String getText() {
        return "<!--\n  ~ Copyright 2018 Red Hat, Inc. and/or its affiliates.\n  ~\n  ~ Licensed under the Apache License, Version 2.0 (the \"License\");\n  ~ you may not use this file except in compliance with the License.\n  ~ You may obtain a copy of the License at\n  ~\n  ~     http://www.apache.org/licenses/LICENSE-2.0\n  ~\n  ~ Unless required by applicable law or agreed to in writing, software\n  ~ distributed under the License is distributed on an \"AS IS\" BASIS,\n  ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n  ~ See the License for the specific language governing permissions and\n  ~ limitations under the License.\n  -->\n\n<div data-field=\"view\" class=\"list-group-item\">\n    <div class=\"list-view-pf-main-info\">\n        <div class=\"list-view-pf-body\">\n            <span data-type-field=\"arrow-button\" class=\"icon expand-icon fa fa-chevron-down\" data-toggle=\"tooltip\" data-container=\"[data-field='list-items']\" data-delay=\"500\" data-placement=\"right\"></span>\n            <div>\n                <span class=\"data-type-label hidden\" data-i18n-key=\"Name\"></span>\n                <input class=\"name-input\" data-type-field=\"name-input\"/>\n                <span class=\"name-text\" data-type-field=\"name-text\"></span>\n            </div>\n            <div>\n                <span class=\"data-type-label hidden\" data-i18n-key=\"Type\"></span>\n                <span data-type-field=\"type\"></span>\n            </div>\n            <div class=\"hidden collection-component\" data-type-field=\"list-container\">\n                <div class=\"kie-data-type-list\">\n                    <i class=\"fa fa-th-list\"></i>\n                    <span data-i18n-key=\"List\"></span>\n                </div>\n                <div data-type-field=\"list-checkbox-container\"></div>\n            </div>\n            <div class=\"hidden collection-component\" data-type-field=\"list-yes\">\n                <div class=\"kie-data-type-list\">\n                    <i class=\"fa fa-th-list\"></i>\n                    <span data-i18n-key=\"List\"></span>\n                </div>\n                <div>\n                    <i class=\"fa fa-check\"></i>\n                    <span data-i18n-key=\"Yes\"></span>\n                </div>\n            </div>\n            <div class=\"constraint-component\" data-type-field=\"constraint-container\"></div>\n        </div>\n    </div>\n    <div class=\"list-view-pf-actions\">\n        <button data-type-field=\"edit-button\" class=\"btn\" data-toggle=\"tooltip\" data-container=\"[data-field='list-items']\" data-delay=\"500\" data-placement=\"bottom\">\n            <i class=\"fa fa-pencil\"></i>\n        </button>\n        <button data-type-field=\"add-data-type-row-button\" class=\"btn\" data-toggle=\"tooltip\" data-container=\"[data-field='list-items']\" data-delay=\"500\" data-placement=\"bottom\">\n            <i class=\"fa fa-plus-circle\"></i>\n        </button>\n        <button data-type-field=\"remove-button\" class=\"btn\" data-toggle=\"tooltip\" data-container=\"[data-field='list-items']\" data-delay=\"500\" data-placement=\"bottom\">\n            <i class=\"fa fa-trash\"></i>\n        </button>\n        <button data-type-field=\"save-button\" class=\"btn\" data-toggle=\"tooltip\" data-container=\"[data-field='list-items']\" data-delay=\"500\" data-placement=\"bottom\">\n            <i class=\"fa fa-check\"></i>\n        </button>\n        <button data-type-field=\"close-button\" class=\"btn\" data-toggle=\"tooltip\" data-container=\"[data-field='list-items']\" data-delay=\"500\" data-placement=\"bottom\">\n            <i class=\"fa fa-times\"></i>\n        </button>\n    </div>\n</div>\n";
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
      case 'getContents': return this.@org.jboss.errai.ioc.client.Type_factory__o_k_w_c_d_c_e_t_l_DataTypeListItemView__quals__j_e_i_Any_j_e_i_Default.o_k_w_c_d_c_e_t_l_DataTypeListItemViewTemplateResource::getContents()();
    }
    return null;
  }-*/;
}

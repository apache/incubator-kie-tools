package org.jboss.errai.ioc.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ResourcePrototype;

public class Type_factory__o_k_w_c_w_c_s_c_SearchBarComponentView__quals__j_e_i_Any_j_e_i_Default_o_k_w_c_w_c_s_c_SearchBarComponentViewTemplateResource_default_InlineClientBundleGenerator implements org.jboss.errai.ioc.client.Type_factory__o_k_w_c_w_c_s_c_SearchBarComponentView__quals__j_e_i_Any_j_e_i_Default.o_k_w_c_w_c_s_c_SearchBarComponentViewTemplateResource {
  private static Type_factory__o_k_w_c_w_c_s_c_SearchBarComponentView__quals__j_e_i_Any_j_e_i_Default_o_k_w_c_w_c_s_c_SearchBarComponentViewTemplateResource_default_InlineClientBundleGenerator _instance0 = new Type_factory__o_k_w_c_w_c_s_c_SearchBarComponentView__quals__j_e_i_Any_j_e_i_Default_o_k_w_c_w_c_s_c_SearchBarComponentViewTemplateResource_default_InlineClientBundleGenerator();
  private void getContentsInitializer() {
    getContents = new com.google.gwt.resources.client.TextResource() {
      // jar:file:/Users/tiagobento/.m2/repository/org/kie/workbench/widgets/kie-wb-common-ui/7.47.0-SNAPSHOT/kie-wb-common-ui-7.47.0-SNAPSHOT-sources.jar!/org/kie/workbench/common/widgets/client/search/component/SearchBarComponentView.html
      public String getText() {
        return "<!--\n  ~ Copyright 2019 Red Hat, Inc. and/or its affiliates.\n  ~\n  ~ Licensed under the Apache License, Version 2.0 (the \"License\");\n  ~ you may not use this file except in compliance with the License.\n  ~ You may obtain a copy of the License at\n  ~\n  ~       http://www.apache.org/licenses/LICENSE-2.0\n  ~\n  ~ Unless required by applicable law or agreed to in writing, software\n  ~ distributed under the License is distributed on an \"AS IS\" BASIS,\n  ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n  ~ See the License for the specific language governing permissions and\n  ~ limitations under the License.\n  -->\n\n<div>\n    <div class=\"form-group toolbar-pf-find kie-toolbar-find\">\n\n        <button data-field=\"search-button\" class=\"btn btn-link btn-find\" type=\"button\">\n            <span class=\"fa fa-search\"></span>\n        </button>\n\n        <div data-field=\"search-container\" class=\"hidden find-pf-dropdown-container kie-find-pf-dropdown-container\">\n\n            <input data-field=\"search-input\" type=\"text\" class=\"form-control\">\n            <span class=\"find-pf-nums\">\n                <span data-field=\"current-result\">0</span>\n                <span data-i18n-key=\"Of\"></span>\n                <span data-field=\"total-of-results\">0</span>\n            </span>\n\n            <div class=\"find-pf-buttons\">\n                <button data-field=\"prev-element\" class=\"btn btn-link\" type=\"button\">\n                    <span class=\"fa fa-angle-up\"></span>\n                </button>\n                <button data-field=\"next-element\" class=\"btn btn-link\" type=\"button\">\n                    <span class=\"fa fa-angle-down\"></span>\n                </button>\n                <div class=\"find-splitter\"></div>\n                <button data-field=\"close-search\" class=\"btn btn-link btn-find-close\" type=\"button\">\n                    <span class=\"pficon pficon-close\"></span>\n                </button>\n            </div>\n        </div>\n    </div>\n</div>\n";
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
      case 'getContents': return this.@org.jboss.errai.ioc.client.Type_factory__o_k_w_c_w_c_s_c_SearchBarComponentView__quals__j_e_i_Any_j_e_i_Default.o_k_w_c_w_c_s_c_SearchBarComponentViewTemplateResource::getContents()();
    }
    return null;
  }-*/;
}

package org.jboss.errai.ioc.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ResourcePrototype;

public class Type_factory__o_k_w_c_d_c_e_t_i_ImportDataObjectModalView__quals__j_e_i_Any_j_e_i_Default_o_k_w_c_d_c_e_t_i_ImportDataObjectModalViewTemplateResource_default_InlineClientBundleGenerator implements org.jboss.errai.ioc.client.Type_factory__o_k_w_c_d_c_e_t_i_ImportDataObjectModalView__quals__j_e_i_Any_j_e_i_Default.o_k_w_c_d_c_e_t_i_ImportDataObjectModalViewTemplateResource {
  private static Type_factory__o_k_w_c_d_c_e_t_i_ImportDataObjectModalView__quals__j_e_i_Any_j_e_i_Default_o_k_w_c_d_c_e_t_i_ImportDataObjectModalViewTemplateResource_default_InlineClientBundleGenerator _instance0 = new Type_factory__o_k_w_c_d_c_e_t_i_ImportDataObjectModalView__quals__j_e_i_Any_j_e_i_Default_o_k_w_c_d_c_e_t_i_ImportDataObjectModalViewTemplateResource_default_InlineClientBundleGenerator();
  private void getContentsInitializer() {
    getContents = new com.google.gwt.resources.client.TextResource() {
      // jar:file:/Users/tiagobento/.m2/repository/org/kie/workbench/kie-wb-common-dmn-client/7.47.0-SNAPSHOT/kie-wb-common-dmn-client-7.47.0-SNAPSHOT-sources.jar!/org/kie/workbench/common/dmn/client/editors/types/imported/ImportDataObjectModalView.html
      public String getText() {
        return "<!--\n  ~ Copyright 2019 Red Hat, Inc. and/or its affiliates.\n  ~\n  ~ Licensed under the Apache License, Version 2.0 (the \"License\");\n  ~ you may not use this file except in compliance with the License.\n  ~ You may obtain a copy of the License at\n  ~\n  ~     http://www.apache.org/licenses/LICENSE-2.0\n  ~\n  ~ Unless required by applicable law or agreed to in writing, software\n  ~ distributed under the License is distributed on an \"AS IS\" BASIS,\n  ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n  ~ See the License for the specific language governing permissions and\n  ~ limitations under the License.\n  -->\n<div>\n    <div data-field=\"view\">\n        <div data-field=\"header\" data-i18n-key=\"Header\"></div>\n        <div data-field=\"body\" class=\"kie-import-data-object-modal-view-body\">\n            <div data-field=\"warning-container\" class=\"alert alert-warning\">\n                <span class=\"pficon pficon-warning-triangle-o\"></span>\n                <strong data-i18n-key=\"DataTypeWithSameNameFound\"></strong>\n            </div>\n            <div data-field=\"top-elements-container\">\n                <span data-i18n-key=\"ProjectDataObjects\"></span>\n                <div><a data-field=\"clear-selection\" href=\"#\" data-i18n-key=\"ClearSelection\"></a></div>\n            </div>\n            <div data-field=\"items-container\" id=\"data-object-items-container\"></div>\n            <div data-field=\"note-container\" class=\"row\">\n                <div class=\"col-md-1\">\n                    <i class=\"fa fa-info-circle\"></i>\n                </div>\n                <div class=\"col-md-11\">\n                    <label data-field=\"note-label\" data-i18n-key=\"Note\"></label>\n                    <span data-field=\"note-text\" data-i18n-key=\"NoteText\"></span>\n                </div>\n            </div>\n        </div>\n        <div data-field=\"footer\">\n            <div class=\"kie-import-data-object-modal-view-footer\">\n                <button data-field=\"button-cancel\" class=\"btn btn-sm btn-default\" data-i18n-key=\"Cancel\"></button>\n                <button data-field=\"button-import\" class=\"btn btn-sm btn-primary\" data-i18n-key=\"Import\"></button>\n            </div>\n        </div>\n    </div>\n</div>";
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
      case 'getContents': return this.@org.jboss.errai.ioc.client.Type_factory__o_k_w_c_d_c_e_t_i_ImportDataObjectModalView__quals__j_e_i_Any_j_e_i_Default.o_k_w_c_d_c_e_t_i_ImportDataObjectModalViewTemplateResource::getContents()();
    }
    return null;
  }-*/;
}

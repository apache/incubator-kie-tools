package org.jboss.errai.ioc.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ResourcePrototype;

public class Type_factory__o_k_w_c_d_c_e_t_l_c_DataTypeConstraintModalView__quals__j_e_i_Any_j_e_i_Default_o_k_w_c_d_c_e_t_l_c_DataTypeConstraintModalViewTemplateResource_default_InlineClientBundleGenerator implements org.jboss.errai.ioc.client.Type_factory__o_k_w_c_d_c_e_t_l_c_DataTypeConstraintModalView__quals__j_e_i_Any_j_e_i_Default.o_k_w_c_d_c_e_t_l_c_DataTypeConstraintModalViewTemplateResource {
  private static Type_factory__o_k_w_c_d_c_e_t_l_c_DataTypeConstraintModalView__quals__j_e_i_Any_j_e_i_Default_o_k_w_c_d_c_e_t_l_c_DataTypeConstraintModalViewTemplateResource_default_InlineClientBundleGenerator _instance0 = new Type_factory__o_k_w_c_d_c_e_t_l_c_DataTypeConstraintModalView__quals__j_e_i_Any_j_e_i_Default_o_k_w_c_d_c_e_t_l_c_DataTypeConstraintModalViewTemplateResource_default_InlineClientBundleGenerator();
  private void getContentsInitializer() {
    getContents = new com.google.gwt.resources.client.TextResource() {
      // jar:file:/Users/tiagobento/.m2/repository/org/kie/workbench/kie-wb-common-dmn-client/7.47.0-SNAPSHOT/kie-wb-common-dmn-client-7.47.0-SNAPSHOT-sources.jar!/org/kie/workbench/common/dmn/client/editors/types/listview/constraint/DataTypeConstraintModalView.html
      public String getText() {
        return "<!--\n  ~ Copyright 2019 Red Hat, Inc. and/or its affiliates.\n  ~\n  ~ Licensed under the Apache License, Version 2.0 (the \"License\");\n  ~ you may not use this file except in compliance with the License.\n  ~ You may obtain a copy of the License at\n  ~\n  ~       http://www.apache.org/licenses/LICENSE-2.0\n  ~\n  ~ Unless required by applicable law or agreed to in writing, software\n  ~ distributed under the License is distributed on an \"AS IS\" BASIS,\n  ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n  ~ See the License for the specific language governing permissions and\n  ~ limitations under the License.\n  -->\n\n<div>\n    <div data-field=\"view\">\n\n        <div data-field=\"header\" data-i18n-key=\"Header\"></div>\n\n        <div data-field=\"body\">\n            <div class=\"constraint-modal-body\">\n\n                <div class=\"alert alert-warning hidden\" data-field=\"constraint-warning-message\">\n                    <button type=\"button\" class=\"close\" data-field=\"close-constraint-warning-message\">\n                        <span class=\"pficon pficon-close\"></span>\n                    </button>\n                    <span class=\"pficon pficon-warning-triangle-o\"></span>\n                    <strong data-i18n-key=\"StrongWarningParserMessage\"></strong>\n                    <span data-i18n-key=\"RegularWarningParserMessage\"></span>\n                </div>\n\n                <p>\n                    <span data-i18n-key=\"BodyParagraph1\"></span>\n                    <span data-field=\"type\"></span>\n                    <span data-i18n-key=\"BodyParagraph2\"></span>\n                </p>\n                <p data-i18n-key=\"BodyParagraph3\"></p>\n                <select class=\"selectpicker\">\n                    <option value=\"\" data-i18n-key=\"Select\" selected disabled hidden></option>\n                    <option value=\"ENUMERATION\" data-i18n-key=\"Enumeration\"></option>\n                    <option value=\"EXPRESSION\" data-i18n-key=\"Expression\"></option>\n                    <option value=\"RANGE\" data-i18n-key=\"Range\"></option>\n                </select>\n\n                <div data-field=\"constraint-component-container\">\n                </div>\n            </div>\n        </div>\n\n        <div data-field=\"footer\">\n            <div class=\"constraint-modal-footer\">\n                <a data-field=\"clear-all-anchor\" href=\"#\" data-i18n-key=\"ClearAll\"></a>\n                <button data-field=\"ok-button\" class=\"btn btn-primary\" data-i18n-key=\"Ok\"></button>\n                <button data-field=\"cancel-button\" class=\"btn btn-default\" data-i18n-key=\"Cancel\"></button>\n            </div>\n        </div>\n    </div>\n\n    <div data-field=\"select-constraint\" class=\"select-constraint\">\n        <i class=\"fa fa-info-circle\"></i>\n        <span data-i18n-key=\"SelectConstraintType\"></span>\n    </div>\n</div>\n";
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
      case 'getContents': return this.@org.jboss.errai.ioc.client.Type_factory__o_k_w_c_d_c_e_t_l_c_DataTypeConstraintModalView__quals__j_e_i_Any_j_e_i_Default.o_k_w_c_d_c_e_t_l_c_DataTypeConstraintModalViewTemplateResource::getContents()();
    }
    return null;
  }-*/;
}

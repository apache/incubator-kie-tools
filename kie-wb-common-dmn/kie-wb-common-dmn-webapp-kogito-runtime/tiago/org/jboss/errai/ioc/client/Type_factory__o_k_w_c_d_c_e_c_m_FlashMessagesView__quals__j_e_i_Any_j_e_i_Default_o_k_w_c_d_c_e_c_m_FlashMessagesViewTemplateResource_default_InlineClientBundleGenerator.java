package org.jboss.errai.ioc.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ResourcePrototype;

public class Type_factory__o_k_w_c_d_c_e_c_m_FlashMessagesView__quals__j_e_i_Any_j_e_i_Default_o_k_w_c_d_c_e_c_m_FlashMessagesViewTemplateResource_default_InlineClientBundleGenerator implements org.jboss.errai.ioc.client.Type_factory__o_k_w_c_d_c_e_c_m_FlashMessagesView__quals__j_e_i_Any_j_e_i_Default.o_k_w_c_d_c_e_c_m_FlashMessagesViewTemplateResource {
  private static Type_factory__o_k_w_c_d_c_e_c_m_FlashMessagesView__quals__j_e_i_Any_j_e_i_Default_o_k_w_c_d_c_e_c_m_FlashMessagesViewTemplateResource_default_InlineClientBundleGenerator _instance0 = new Type_factory__o_k_w_c_d_c_e_c_m_FlashMessagesView__quals__j_e_i_Any_j_e_i_Default_o_k_w_c_d_c_e_c_m_FlashMessagesViewTemplateResource_default_InlineClientBundleGenerator();
  private void getContentsInitializer() {
    getContents = new com.google.gwt.resources.client.TextResource() {
      // jar:file:/Users/tiagobento/.m2/repository/org/kie/workbench/kie-wb-common-dmn-client/7.47.0-SNAPSHOT/kie-wb-common-dmn-client-7.47.0-SNAPSHOT-sources.jar!/org/kie/workbench/common/dmn/client/editors/common/messages/FlashMessagesView.html
      public String getText() {
        return "<!--\n  ~ Copyright 2018 Red Hat, Inc. and/or its affiliates.\n  ~\n  ~ Licensed under the Apache License, Version 2.0 (the \"License\");\n  ~ you may not use this file except in compliance with the License.\n  ~ You may obtain a copy of the License at\n  ~\n  ~       http://www.apache.org/licenses/LICENSE-2.0\n  ~\n  ~ Unless required by applicable law or agreed to in writing, software\n  ~ distributed under the License is distributed on an \"AS IS\" BASIS,\n  ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n  ~ See the License for the specific language governing permissions and\n  ~ limitations under the License.\n  -->\n\n<div>\n    <div data-field=\"success-container\" class=\"alert alert-success\">\n        <i class=\"pficon pficon-ok\"></i>\n        <strong data-field=\"strong-success-message\"></strong>\n        <span data-field=\"regular-success-message\"></span>\n        <div class=\"action-buttons\">\n            <button data-field=\"close-success-message-button\" class=\"fa fa-times\"></button>\n        </div>\n    </div>\n\n    <div data-field=\"error-container\" class=\"alert alert-danger\">\n        <span class=\"pficon pficon-error-circle-o\"></span>\n        <strong data-field=\"strong-error-message\"></strong>\n        <span data-field=\"regular-error-message\"></span>\n    </div>\n\n    <div data-field=\"warning-container\" class=\"alert alert-warning\">\n        <span class=\"pficon pficon-warning-triangle-o\"></span>\n        <strong data-field=\"strong-warning-message\"></strong>\n        <span data-field=\"regular-warning-message\"></span>\n        <div class=\"action-buttons\">\n            <button class=\"btn btn-default btn-sm\" data-field=\"cancel-warning-button\" type=\"button\" data-i18n-key=\"Cancel\"></button>\n            <button class=\"btn btn-primary btn-sm\" data-field=\"ok-warning-button\" type=\"button\" data-i18n-key=\"Ok\"></button>\n        </div>\n    </div>\n</div>\n";
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
      case 'getContents': return this.@org.jboss.errai.ioc.client.Type_factory__o_k_w_c_d_c_e_c_m_FlashMessagesView__quals__j_e_i_Any_j_e_i_Default.o_k_w_c_d_c_e_c_m_FlashMessagesViewTemplateResource::getContents()();
    }
    return null;
  }-*/;
}

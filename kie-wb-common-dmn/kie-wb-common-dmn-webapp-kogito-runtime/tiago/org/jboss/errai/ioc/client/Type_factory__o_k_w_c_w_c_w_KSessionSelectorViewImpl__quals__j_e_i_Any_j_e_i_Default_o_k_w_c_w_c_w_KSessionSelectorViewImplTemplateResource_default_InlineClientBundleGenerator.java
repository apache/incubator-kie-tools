package org.jboss.errai.ioc.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ResourcePrototype;

public class Type_factory__o_k_w_c_w_c_w_KSessionSelectorViewImpl__quals__j_e_i_Any_j_e_i_Default_o_k_w_c_w_c_w_KSessionSelectorViewImplTemplateResource_default_InlineClientBundleGenerator implements org.jboss.errai.ioc.client.Type_factory__o_k_w_c_w_c_w_KSessionSelectorViewImpl__quals__j_e_i_Any_j_e_i_Default.o_k_w_c_w_c_w_KSessionSelectorViewImplTemplateResource {
  private static Type_factory__o_k_w_c_w_c_w_KSessionSelectorViewImpl__quals__j_e_i_Any_j_e_i_Default_o_k_w_c_w_c_w_KSessionSelectorViewImplTemplateResource_default_InlineClientBundleGenerator _instance0 = new Type_factory__o_k_w_c_w_c_w_KSessionSelectorViewImpl__quals__j_e_i_Any_j_e_i_Default_o_k_w_c_w_c_w_KSessionSelectorViewImplTemplateResource_default_InlineClientBundleGenerator();
  private void getContentsInitializer() {
    getContents = new com.google.gwt.resources.client.TextResource() {
      // jar:file:/Users/tiagobento/.m2/repository/org/kie/workbench/widgets/kie-wb-common-ui/7.47.0-SNAPSHOT/kie-wb-common-ui-7.47.0-SNAPSHOT-sources.jar!/org/kie/workbench/common/widgets/client/widget/KSessionSelectorViewImpl.html
      public String getText() {
        return "<!DOCTYPE html>\n<!--\n  ~ Copyright 2017 Red Hat, Inc. and/or its affiliates.\n  ~\n  ~ Licensed under the Apache License, Version 2.0 (the \"License\");\n  ~ you may not use this file except in compliance with the License.\n  ~ You may obtain a copy of the License at\n  ~\n  ~      http://www.apache.org/licenses/LICENSE-2.0\n  ~\n  ~ Unless required by applicable law or agreed to in writing, software\n  ~ distributed under the License is distributed on an \"AS IS\" BASIS,\n  ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n  ~ See the License for the specific language governing permissions and\n  ~ limitations under the License.\n  -->\n<div class=\"container-fluid\">\n    <form>\n        <div class=\"form-group ksession-form-group row\">\n            <label for=\"kbaseSelectContainer\" class=\"control-label col-md-3\" data-i18n-key=\"KnowledgeBase\"></label>\n            <div class=\"col-md-9\" id=\"kbaseSelectContainer\">\n            </div>\n        </div>\n        <div class=\"form-group ksession-form-group row\">\n            <label for=\"ksessionSelectContainer\" class=\"control-label col-md-3\" data-i18n-key=\"KnowledgeSession\"></label>\n            <div class=\"col-md-9\" id=\"ksessionSelectContainer\">\n            </div>\n        </div>\n    </form>\n    <label id=\"warningLabel\" class=\"label label-warning\" data-i18n-key=\"SelectedKSessionDoesNotExist\" style=\"visibility: hidden;\"></label>\n</div>";
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
  private void getStyleInitializer() {
    getStyle = new com.google.gwt.resources.client.CssResource() {
      private boolean injected;
      public boolean ensureInjected() {
        if (!injected) {
          injected = true;
          com.google.gwt.dom.client.StyleInjector.inject(getText());
          return true;
        }
        return false;
      }
      public String getName() {
        return "getStyle";
      }
      public String getText() {
        return (".ksession-form-group{padding:" + ("5px")  + ";}");
      }
    }
    ;
  }
  private static class getStyleInitializer {
    static {
      _instance0.getStyleInitializer();
    }
    static com.google.gwt.resources.client.CssResource get() {
      return getStyle;
    }
  }
  public com.google.gwt.resources.client.CssResource getStyle() {
    return getStyleInitializer.get();
  }
  private static java.util.HashMap<java.lang.String, com.google.gwt.resources.client.ResourcePrototype> resourceMap;
  private static com.google.gwt.resources.client.TextResource getContents;
  private static com.google.gwt.resources.client.CssResource getStyle;
  
  public ResourcePrototype[] getResources() {
    return new ResourcePrototype[] {
      getContents(), 
      getStyle(), 
    };
  }
  public ResourcePrototype getResource(String name) {
    if (GWT.isScript()) {
      return getResourceNative(name);
    } else {
      if (resourceMap == null) {
        resourceMap = new java.util.HashMap<java.lang.String, com.google.gwt.resources.client.ResourcePrototype>();
        resourceMap.put("getContents", getContents());
        resourceMap.put("getStyle", getStyle());
      }
      return resourceMap.get(name);
    }
  }
  private native ResourcePrototype getResourceNative(String name) /*-{
    switch (name) {
      case 'getContents': return this.@org.jboss.errai.ioc.client.Type_factory__o_k_w_c_w_c_w_KSessionSelectorViewImpl__quals__j_e_i_Any_j_e_i_Default.o_k_w_c_w_c_w_KSessionSelectorViewImplTemplateResource::getContents()();
      case 'getStyle': return this.@org.jboss.errai.ioc.client.Type_factory__o_k_w_c_w_c_w_KSessionSelectorViewImpl__quals__j_e_i_Any_j_e_i_Default.o_k_w_c_w_c_w_KSessionSelectorViewImplTemplateResource::getStyle()();
    }
    return null;
  }-*/;
}

package org.jboss.errai.ioc.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ResourcePrototype;

public class Type_factory__o_k_w_c_w_c_p_a_AboutPopupView__quals__j_e_i_Any_j_e_i_Default_o_k_w_c_w_c_p_a_AboutPopupViewTemplateResource_default_InlineClientBundleGenerator implements org.jboss.errai.ioc.client.Type_factory__o_k_w_c_w_c_p_a_AboutPopupView__quals__j_e_i_Any_j_e_i_Default.o_k_w_c_w_c_p_a_AboutPopupViewTemplateResource {
  private static Type_factory__o_k_w_c_w_c_p_a_AboutPopupView__quals__j_e_i_Any_j_e_i_Default_o_k_w_c_w_c_p_a_AboutPopupViewTemplateResource_default_InlineClientBundleGenerator _instance0 = new Type_factory__o_k_w_c_w_c_p_a_AboutPopupView__quals__j_e_i_Any_j_e_i_Default_o_k_w_c_w_c_p_a_AboutPopupViewTemplateResource_default_InlineClientBundleGenerator();
  private void getContentsInitializer() {
    getContents = new com.google.gwt.resources.client.TextResource() {
      // jar:file:/Users/tiagobento/redhat/kiegroup-all/kie-wb-common/kie-wb-common-widgets/kie-wb-common-ui/target/kie-wb-common-ui-7.47.0-SNAPSHOT-sources.jar!/org/kie/workbench/common/widgets/client/popups/about/AboutPopupView.html
      public String getText() {
        return "<!--\n  ~ Copyright 2017 Red Hat, Inc. and/or its affiliates.\n  ~\n  ~ Licensed under the Apache License, Version 2.0 (the \"License\");\n  ~ you may not use this file except in compliance with the License.\n  ~ You may obtain a copy of the License at\n  ~\n  ~       http://www.apache.org/licenses/LICENSE-2.0\n  ~\n  ~ Unless required by applicable law or agreed to in writing, software\n  ~ distributed under the License is distributed on an \"AS IS\" BASIS,\n  ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n  ~ See the License for the specific language governing permissions and\n  ~ limitations under the License.\n  -->\n<div class=\"modal fade in\" id=\"about\" tabindex=\"-1\" role=\"dialog\" aria-labelledby=\"myModalLabel\" aria-hidden=\"true\" style=\"display: block;\">\n    <div class=\"modal-dialog\">\n        <div data-field=\"modal-content\" class=\"modal-content about-modal-pf\">\n            <div class=\"modal-header\">\n                <button type=\"button\" class=\"close\" data-dismiss=\"modal\" aria-hidden=\"true\">\n                    <span class=\"pficon pficon-close\"></span>\n                </button>\n            </div>\n            <div class=\"modal-body\">\n                <h1>\n                    <img data-field=\"product-image\" />\n                </h1>\n                <div class=\"product-versions-pf\">\n                    <ul class=\"list-unstyled\">\n                        <li>\n                            <strong data-i18n-key=\"Version\"></strong>\n                            <span data-field=\"version\"></span>\n                        </li>\n                    </ul>\n                </div>\n                <div class=\"trademark-pf\">\n                    <span data-field=\"trademark-product-name\"></span>\n                    <span data-i18n-key=\"LicenseDescription\"></span>\n                    <br>\n                    <strong><span data-field=\"trademark2\"></span></strong>\n                </div>\n            </div>\n        </div>\n    </div>\n</div>";
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
        return (".modal-dialog .modal-content.about-modal-pf{min-height:" + ("30em")  + ";background-size:" + ("cover")  + ";}");
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
      case 'getContents': return this.@org.jboss.errai.ioc.client.Type_factory__o_k_w_c_w_c_p_a_AboutPopupView__quals__j_e_i_Any_j_e_i_Default.o_k_w_c_w_c_p_a_AboutPopupViewTemplateResource::getContents()();
      case 'getStyle': return this.@org.jboss.errai.ioc.client.Type_factory__o_k_w_c_w_c_p_a_AboutPopupView__quals__j_e_i_Any_j_e_i_Default.o_k_w_c_w_c_p_a_AboutPopupViewTemplateResource::getStyle()();
    }
    return null;
  }-*/;
}

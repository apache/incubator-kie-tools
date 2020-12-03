package org.jboss.errai.ioc.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ResourcePrototype;

public class Type_factory__o_u_e_p_c_a_i_AdminPageItemView__quals__j_e_i_Any_j_e_i_Default_o_u_e_p_c_a_i_AdminPageItemViewTemplateResource_default_InlineClientBundleGenerator implements org.jboss.errai.ioc.client.Type_factory__o_u_e_p_c_a_i_AdminPageItemView__quals__j_e_i_Any_j_e_i_Default.o_u_e_p_c_a_i_AdminPageItemViewTemplateResource {
  private static Type_factory__o_u_e_p_c_a_i_AdminPageItemView__quals__j_e_i_Any_j_e_i_Default_o_u_e_p_c_a_i_AdminPageItemViewTemplateResource_default_InlineClientBundleGenerator _instance0 = new Type_factory__o_u_e_p_c_a_i_AdminPageItemView__quals__j_e_i_Any_j_e_i_Default_o_u_e_p_c_a_i_AdminPageItemViewTemplateResource_default_InlineClientBundleGenerator();
  private void getContentsInitializer() {
    getContents = new com.google.gwt.resources.client.TextResource() {
      // jar:file:/Users/tiagobento/.m2/repository/org/uberfire/uberfire-preferences-ui-client/7.47.0-SNAPSHOT/uberfire-preferences-ui-client-7.47.0-SNAPSHOT-sources.jar!/org/uberfire/ext/preferences/client/admin/item/AdminPageItemView.html
      public String getText() {
        return "<!--\n  ~ Copyright 2016 Red Hat, Inc. and/or its affiliates.\n  ~\n  ~ Licensed under the Apache License, Version 2.0 (the \"License\");\n  ~ you may not use this file except in compliance with the License.\n  ~ You may obtain a copy of the License at\n  ~\n  ~       http://www.apache.org/licenses/LICENSE-2.0\n  ~\n  ~ Unless required by applicable law or agreed to in writing, software\n  ~ distributed under the License is distributed on an \"AS IS\" BASIS,\n  ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n  ~ See the License for the specific language governing permissions and\n  ~ limitations under the License.\n  -->\n\n<div data-field=\"item\" class=\"admin-page-item\">\n    <span><i data-field=\"item-icon\" aria-hidden=\"true\"></i></span>\n    <div data-field=\"item-text\"></div>\n    <div class=\"admin-page-item-counter\" data-field=\"item-counter-container\">\n        <div>\n            <p data-field=\"item-counter\"></p>\n        </div>\n    </div>\n</div>";
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
        return (".admin-page-item{display:" + ("inline-block")  + ";text-align:" + ("center")  + ";padding:" + ("30px")  + ";vertical-align:" + ("top")  + ";width:" + ("290px")  + ";height:" + ("285px")  + ";margin:" + ("11px")  + ";background-color:" + ("#fff")  + ";cursor:" + ("pointer")  + ";}.admin-page-item>span{font-size:" + ("72px")  + ";}.admin-page-item>div{font-size:") + (("18px")  + ";}.admin-page-item .admin-page-item-counter{text-align:" + ("center")  + ";vertical-align:" + ("middle")  + ";margin:" + ("20px")  + ";}.admin-page-item .admin-page-item-counter div{width:" + ("39px")  + ";height:" + ("39px")  + ";line-height:" + ("39px")  + ";border-radius:" + ("50%")  + ";background-color:" + ("#d8d8d8")  + ";font-weight:" + ("bold")  + ";display:" + ("table") ) + (";margin:" + ("auto")  + ";}.admin-page-item .admin-page-item-counter div p{display:" + ("table-cell")  + ";}");
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
      case 'getContents': return this.@org.jboss.errai.ioc.client.Type_factory__o_u_e_p_c_a_i_AdminPageItemView__quals__j_e_i_Any_j_e_i_Default.o_u_e_p_c_a_i_AdminPageItemViewTemplateResource::getContents()();
      case 'getStyle': return this.@org.jboss.errai.ioc.client.Type_factory__o_u_e_p_c_a_i_AdminPageItemView__quals__j_e_i_Any_j_e_i_Default.o_u_e_p_c_a_i_AdminPageItemViewTemplateResource::getStyle()();
    }
    return null;
  }-*/;
}

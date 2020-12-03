package org.jboss.errai.ioc.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ResourcePrototype;

public class Type_factory__o_u_e_p_c_c_t_TreeHierarchyInternalItemView__quals__j_e_i_Any_j_e_i_Default_o_u_e_p_c_c_t_TreeHierarchyInternalItemViewTemplateResource_default_InlineClientBundleGenerator implements org.jboss.errai.ioc.client.Type_factory__o_u_e_p_c_c_t_TreeHierarchyInternalItemView__quals__j_e_i_Any_j_e_i_Default.o_u_e_p_c_c_t_TreeHierarchyInternalItemViewTemplateResource {
  private static Type_factory__o_u_e_p_c_c_t_TreeHierarchyInternalItemView__quals__j_e_i_Any_j_e_i_Default_o_u_e_p_c_c_t_TreeHierarchyInternalItemViewTemplateResource_default_InlineClientBundleGenerator _instance0 = new Type_factory__o_u_e_p_c_c_t_TreeHierarchyInternalItemView__quals__j_e_i_Any_j_e_i_Default_o_u_e_p_c_c_t_TreeHierarchyInternalItemViewTemplateResource_default_InlineClientBundleGenerator();
  private void getContentsInitializer() {
    getContents = new com.google.gwt.resources.client.TextResource() {
      // jar:file:/Users/tiagobento/.m2/repository/org/uberfire/uberfire-preferences-ui-client/7.47.0-SNAPSHOT/uberfire-preferences-ui-client-7.47.0-SNAPSHOT-sources.jar!/org/uberfire/ext/preferences/client/central/tree/TreeHierarchyInternalItemView.html
      public String getText() {
        return "<!--\n  ~ Copyright 2016 Red Hat, Inc. and/or its affiliates.\n  ~\n  ~ Licensed under the Apache License, Version 2.0 (the \"License\");\n  ~ you may not use this file except in compliance with the License.\n  ~ You may obtain a copy of the License at\n  ~\n  ~       http://www.apache.org/licenses/LICENSE-2.0\n  ~\n  ~ Unless required by applicable law or agreed to in writing, software\n  ~ distributed under the License is distributed on an \"AS IS\" BASIS,\n  ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n  ~ See the License for the specific language governing permissions and\n  ~ limitations under the License.\n  -->\n\n<div class=\"preference-tree-internal-item\">\n    <div class=\"preference-tree-internal-item-node\">\n        <i class=\"fa fa-chevron-right hidden\" aria-hidden=\"true\" data-field=\"preference-tree-internal-item-expand-icon\"></i>\n        <i class=\"fa fa-chevron-down\" aria-hidden=\"true\" data-field=\"preference-tree-internal-item-contract-icon\"></i>\n        <label data-field=\"preference-tree-internal-item-label\"></label>\n    </div>\n    <div class=\"preference-tree-internal-item-children\" data-field=\"preference-tree-internal-item-children\"></div>\n</div>";
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
        return com.google.gwt.i18n.client.LocaleInfo.getCurrentLocale().isRTL() ? ((".preference-tree-internal-item i[data-field=\"preference-tree-internal-item-expand-icon\"]{padding-right:" + ("3px")  + ";}.preference-tree-internal-item label{padding:" + ("4px")  + ";margin:" + ("0")  + ";cursor:" + ("pointer")  + ";}.preference-tree-internal-item-node{margin-left:" + ("1px")  + ";}.preference-tree-internal-item .selected{background-color:" + ("#1699d3")  + ";color:" + ("#fff")  + ";}")) : ((".preference-tree-internal-item i[data-field=\"preference-tree-internal-item-expand-icon\"]{padding-left:" + ("3px")  + ";}.preference-tree-internal-item label{padding:" + ("4px")  + ";margin:" + ("0")  + ";cursor:" + ("pointer")  + ";}.preference-tree-internal-item-node{margin-right:" + ("1px")  + ";}.preference-tree-internal-item .selected{background-color:" + ("#1699d3")  + ";color:" + ("#fff")  + ";}"));
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
      case 'getContents': return this.@org.jboss.errai.ioc.client.Type_factory__o_u_e_p_c_c_t_TreeHierarchyInternalItemView__quals__j_e_i_Any_j_e_i_Default.o_u_e_p_c_c_t_TreeHierarchyInternalItemViewTemplateResource::getContents()();
      case 'getStyle': return this.@org.jboss.errai.ioc.client.Type_factory__o_u_e_p_c_c_t_TreeHierarchyInternalItemView__quals__j_e_i_Any_j_e_i_Default.o_u_e_p_c_c_t_TreeHierarchyInternalItemViewTemplateResource::getStyle()();
    }
    return null;
  }-*/;
}

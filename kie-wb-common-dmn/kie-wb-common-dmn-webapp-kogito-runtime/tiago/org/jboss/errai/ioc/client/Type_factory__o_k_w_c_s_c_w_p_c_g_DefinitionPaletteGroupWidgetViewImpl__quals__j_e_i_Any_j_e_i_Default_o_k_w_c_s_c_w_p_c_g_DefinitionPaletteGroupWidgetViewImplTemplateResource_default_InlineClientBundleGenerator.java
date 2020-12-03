package org.jboss.errai.ioc.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ResourcePrototype;

public class Type_factory__o_k_w_c_s_c_w_p_c_g_DefinitionPaletteGroupWidgetViewImpl__quals__j_e_i_Any_j_e_i_Default_o_k_w_c_s_c_w_p_c_g_DefinitionPaletteGroupWidgetViewImplTemplateResource_default_InlineClientBundleGenerator implements org.jboss.errai.ioc.client.Type_factory__o_k_w_c_s_c_w_p_c_g_DefinitionPaletteGroupWidgetViewImpl__quals__j_e_i_Any_j_e_i_Default.o_k_w_c_s_c_w_p_c_g_DefinitionPaletteGroupWidgetViewImplTemplateResource {
  private static Type_factory__o_k_w_c_s_c_w_p_c_g_DefinitionPaletteGroupWidgetViewImpl__quals__j_e_i_Any_j_e_i_Default_o_k_w_c_s_c_w_p_c_g_DefinitionPaletteGroupWidgetViewImplTemplateResource_default_InlineClientBundleGenerator _instance0 = new Type_factory__o_k_w_c_s_c_w_p_c_g_DefinitionPaletteGroupWidgetViewImpl__quals__j_e_i_Any_j_e_i_Default_o_k_w_c_s_c_w_p_c_g_DefinitionPaletteGroupWidgetViewImplTemplateResource_default_InlineClientBundleGenerator();
  private void getContentsInitializer() {
    getContents = new com.google.gwt.resources.client.TextResource() {
      // jar:file:/Users/tiagobento/.m2/repository/org/kie/workbench/stunner/kie-wb-common-stunner-widgets/7.47.0-SNAPSHOT/kie-wb-common-stunner-widgets-7.47.0-SNAPSHOT-sources.jar!/org/kie/workbench/common/stunner/client/widgets/palette/categories/group/DefinitionPaletteGroupWidgetViewImpl.html
      public String getText() {
        return "<ul class=\"list-group\">\n    <li class=\"list-group-item\" data-field=\"moreAnchor\">\n        <a href=\"#\">\n            <span class=\"list-group-item-value kie-list-group-item-value-more\" data-i18n-key=\"showMore\"></span>\n        </a>\n    </li>\n    <li class=\"list-group-item\" data-field=\"lessAnchor\">\n        <a href=\"#\">\n            <span class=\"list-group-item-value kie-list-group-item-value-more\" data-i18n-key=\"showLess\"></span>\n        </a>\n    </li>\n</ul>\n";
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
      case 'getContents': return this.@org.jboss.errai.ioc.client.Type_factory__o_k_w_c_s_c_w_p_c_g_DefinitionPaletteGroupWidgetViewImpl__quals__j_e_i_Any_j_e_i_Default.o_k_w_c_s_c_w_p_c_g_DefinitionPaletteGroupWidgetViewImplTemplateResource::getContents()();
    }
    return null;
  }-*/;
}

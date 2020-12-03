package org.jboss.errai.ioc.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ResourcePrototype;

public class Type_factory__o_k_w_c_s_c_w_p_c_i_DefinitionPaletteItemWidgetViewImpl__quals__j_e_i_Any_j_e_i_Default_o_k_w_c_s_c_w_p_c_i_DefinitionPaletteItemWidgetViewImplTemplateResource_default_InlineClientBundleGenerator implements org.jboss.errai.ioc.client.Type_factory__o_k_w_c_s_c_w_p_c_i_DefinitionPaletteItemWidgetViewImpl__quals__j_e_i_Any_j_e_i_Default.o_k_w_c_s_c_w_p_c_i_DefinitionPaletteItemWidgetViewImplTemplateResource {
  private static Type_factory__o_k_w_c_s_c_w_p_c_i_DefinitionPaletteItemWidgetViewImpl__quals__j_e_i_Any_j_e_i_Default_o_k_w_c_s_c_w_p_c_i_DefinitionPaletteItemWidgetViewImplTemplateResource_default_InlineClientBundleGenerator _instance0 = new Type_factory__o_k_w_c_s_c_w_p_c_i_DefinitionPaletteItemWidgetViewImpl__quals__j_e_i_Any_j_e_i_Default_o_k_w_c_s_c_w_p_c_i_DefinitionPaletteItemWidgetViewImplTemplateResource_default_InlineClientBundleGenerator();
  private void getContentsInitializer() {
    getContents = new com.google.gwt.resources.client.TextResource() {
      // jar:file:/Users/tiagobento/.m2/repository/org/kie/workbench/stunner/kie-wb-common-stunner-widgets/7.47.0-SNAPSHOT/kie-wb-common-stunner-widgets-7.47.0-SNAPSHOT-sources.jar!/org/kie/workbench/common/stunner/client/widgets/palette/categories/items/DefinitionPaletteItemWidgetViewImpl.html
      public String getText() {
        return "<li class=\"list-group-item kie-palette-item-list-item\">\n    <a data-field=\"itemAnchor\" class=\"kie-palette-item-anchor-spacer\" >\n        <span data-field=\"icon\" class=\"kie-palette-item-icon-spacer\"></span>\n        <span class=\"list-group-item-value kie-palette-item-value-spacer\" data-field=\"name\"></span>\n    </a>\n</li>\n";
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
        return com.google.gwt.i18n.client.LocaleInfo.getCurrentLocale().isRTL() ? ((".kie-palette-item-list-item{height:" + ("24px")  + ";}.kie-palette-item-anchor-spacer{display:" + ("inline-block")  + " !important;}.kie-palette-item-icon-spacer{vertical-align:" + ("super")  + ";display:" + ("inline-block")  + ";padding-left:" + ("1em")  + ";margin:" + ("auto")  + ";}.kie-palette-item-value-spacer{display:" + ("inline-block")  + " !important;}")) : ((".kie-palette-item-list-item{height:" + ("24px")  + ";}.kie-palette-item-anchor-spacer{display:" + ("inline-block")  + " !important;}.kie-palette-item-icon-spacer{vertical-align:" + ("super")  + ";display:" + ("inline-block")  + ";padding-right:" + ("1em")  + ";margin:" + ("auto")  + ";}.kie-palette-item-value-spacer{display:" + ("inline-block")  + " !important;}"));
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
      case 'getContents': return this.@org.jboss.errai.ioc.client.Type_factory__o_k_w_c_s_c_w_p_c_i_DefinitionPaletteItemWidgetViewImpl__quals__j_e_i_Any_j_e_i_Default.o_k_w_c_s_c_w_p_c_i_DefinitionPaletteItemWidgetViewImplTemplateResource::getContents()();
      case 'getStyle': return this.@org.jboss.errai.ioc.client.Type_factory__o_k_w_c_s_c_w_p_c_i_DefinitionPaletteItemWidgetViewImpl__quals__j_e_i_Any_j_e_i_Default.o_k_w_c_s_c_w_p_c_i_DefinitionPaletteItemWidgetViewImplTemplateResource::getStyle()();
    }
    return null;
  }-*/;
}

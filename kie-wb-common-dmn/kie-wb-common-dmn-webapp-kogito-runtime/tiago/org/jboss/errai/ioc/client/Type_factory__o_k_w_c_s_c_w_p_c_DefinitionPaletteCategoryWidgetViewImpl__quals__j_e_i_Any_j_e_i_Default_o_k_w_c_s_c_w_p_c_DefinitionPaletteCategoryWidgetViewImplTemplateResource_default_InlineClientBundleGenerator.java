package org.jboss.errai.ioc.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ResourcePrototype;

public class Type_factory__o_k_w_c_s_c_w_p_c_DefinitionPaletteCategoryWidgetViewImpl__quals__j_e_i_Any_j_e_i_Default_o_k_w_c_s_c_w_p_c_DefinitionPaletteCategoryWidgetViewImplTemplateResource_default_InlineClientBundleGenerator implements org.jboss.errai.ioc.client.Type_factory__o_k_w_c_s_c_w_p_c_DefinitionPaletteCategoryWidgetViewImpl__quals__j_e_i_Any_j_e_i_Default.o_k_w_c_s_c_w_p_c_DefinitionPaletteCategoryWidgetViewImplTemplateResource {
  private static Type_factory__o_k_w_c_s_c_w_p_c_DefinitionPaletteCategoryWidgetViewImpl__quals__j_e_i_Any_j_e_i_Default_o_k_w_c_s_c_w_p_c_DefinitionPaletteCategoryWidgetViewImplTemplateResource_default_InlineClientBundleGenerator _instance0 = new Type_factory__o_k_w_c_s_c_w_p_c_DefinitionPaletteCategoryWidgetViewImpl__quals__j_e_i_Any_j_e_i_Default_o_k_w_c_s_c_w_p_c_DefinitionPaletteCategoryWidgetViewImplTemplateResource_default_InlineClientBundleGenerator();
  private void getContentsInitializer() {
    getContents = new com.google.gwt.resources.client.TextResource() {
      // jar:file:/Users/tiagobento/redhat/kiegroup-all/kie-wb-common/kie-wb-common-stunner/kie-wb-common-stunner-client/kie-wb-common-stunner-widgets/target/kie-wb-common-stunner-widgets-7.47.0-SNAPSHOT-sources.jar!/org/kie/workbench/common/stunner/client/widgets/palette/categories/DefinitionPaletteCategoryWidgetViewImpl.html
      public String getText() {
        return "<li class=\"list-group-item\" data-field=\"listGroupItem\">\n    <button data-field=\"categoryIcon\" class=\"btn btn-default kie-palette-category-widget-button\">\n    </button>\n    <div data-field=\"floatingPanel\" class=\"kie-palette-flyout\">\n        <div class=\"kie-palette-flyout__close\">\n            <button data-field=\"closeCategoryButton\" class=\"btn btn-link btn-large kie-palette-flyout__btn-link--close\">\n                <span class=\"pficon pficon-close\"></span>\n            </button>\n        </div>\n    </div>\n</li>\n";
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
        return (".kie-palette-category-widget-button .glyph-icon-svg-position{position:" + ("absolute")  + ";}");
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
      case 'getContents': return this.@org.jboss.errai.ioc.client.Type_factory__o_k_w_c_s_c_w_p_c_DefinitionPaletteCategoryWidgetViewImpl__quals__j_e_i_Any_j_e_i_Default.o_k_w_c_s_c_w_p_c_DefinitionPaletteCategoryWidgetViewImplTemplateResource::getContents()();
      case 'getStyle': return this.@org.jboss.errai.ioc.client.Type_factory__o_k_w_c_s_c_w_p_c_DefinitionPaletteCategoryWidgetViewImpl__quals__j_e_i_Any_j_e_i_Default.o_k_w_c_s_c_w_p_c_DefinitionPaletteCategoryWidgetViewImplTemplateResource::getStyle()();
    }
    return null;
  }-*/;
}

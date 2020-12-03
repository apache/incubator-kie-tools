package org.jboss.errai.ioc.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ResourcePrototype;

public class Type_factory__o_u_e_l_e_c_w_LayoutEditorPropertiesView__quals__j_e_i_Any_j_e_i_Default_o_u_e_l_e_c_w_LayoutEditorPropertiesViewTemplateResource_default_InlineClientBundleGenerator implements org.jboss.errai.ioc.client.Type_factory__o_u_e_l_e_c_w_LayoutEditorPropertiesView__quals__j_e_i_Any_j_e_i_Default.o_u_e_l_e_c_w_LayoutEditorPropertiesViewTemplateResource {
  private static Type_factory__o_u_e_l_e_c_w_LayoutEditorPropertiesView__quals__j_e_i_Any_j_e_i_Default_o_u_e_l_e_c_w_LayoutEditorPropertiesViewTemplateResource_default_InlineClientBundleGenerator _instance0 = new Type_factory__o_u_e_l_e_c_w_LayoutEditorPropertiesView__quals__j_e_i_Any_j_e_i_Default_o_u_e_l_e_c_w_LayoutEditorPropertiesViewTemplateResource_default_InlineClientBundleGenerator();
  private void getContentsInitializer() {
    getContents = new com.google.gwt.resources.client.TextResource() {
      // jar:file:/Users/tiagobento/.m2/repository/org/uberfire/uberfire-layout-editor-client/7.47.0-SNAPSHOT/uberfire-layout-editor-client-7.47.0-SNAPSHOT.jar!/org/uberfire/ext/layout/editor/client/widgets/LayoutEditorPropertiesView.html
      public String getText() {
        return "<div class=\"af-lep-main-panel\">\n    <h4><span data-field=\"selectorLabel\">Element:</span></h4>\n    <div data-field=\"selectorDiv\">\n    </div>\n    <div data-field=\"partsFieldContainer\">\n        <h4><span>Element Parts:</span></h4>\n        <select class=\"form-control\" data-field=\"partsSelect\">\n            <option data-field=\"partOption\"></option>\n        </select>\n    </div>\n    <div>\n        <h4><span data-field=\"propertiesLabel\">Properties:</span></h4>\n        <a data-field=\"clearAllAnchor\" href=\"#\" class=\"pull-right af-lep-clear-anchor\"> Clear All</a>\n    </div>\n    <div data-field=\"elementDiv\">\n    </div>\n</div>";
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
        return com.google.gwt.i18n.client.LocaleInfo.getCurrentLocale().isRTL() ? ((".af-lep-main-panel{padding:" + ("0"+ " " +"10px"+ " " +"0"+ " " +"10px")  + ";overflow:" + ("auto")  + ";height:" + ("100%")  + ";}.af-lep-clear-anchor{margin-top:" + ("-27px")  + ";margin-left:" + ("10px")  + ";}")) : ((".af-lep-main-panel{padding:" + ("0"+ " " +"10px"+ " " +"0"+ " " +"10px")  + ";overflow:" + ("auto")  + ";height:" + ("100%")  + ";}.af-lep-clear-anchor{margin-top:" + ("-27px")  + ";margin-right:" + ("10px")  + ";}"));
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
      case 'getContents': return this.@org.jboss.errai.ioc.client.Type_factory__o_u_e_l_e_c_w_LayoutEditorPropertiesView__quals__j_e_i_Any_j_e_i_Default.o_u_e_l_e_c_w_LayoutEditorPropertiesViewTemplateResource::getContents()();
      case 'getStyle': return this.@org.jboss.errai.ioc.client.Type_factory__o_u_e_l_e_c_w_LayoutEditorPropertiesView__quals__j_e_i_Any_j_e_i_Default.o_u_e_l_e_c_w_LayoutEditorPropertiesViewTemplateResource::getStyle()();
    }
    return null;
  }-*/;
}

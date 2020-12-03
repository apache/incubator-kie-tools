package org.jboss.errai.ioc.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ResourcePrototype;

public class Type_factory__o_u_e_w_c_c_d_i_LiveSearchSelectorDropDownItemViewImpl__quals__j_e_i_Any_j_e_i_Default_o_u_e_w_c_c_d_i_LiveSearchSelectorDropDownItemViewImplTemplateResource_default_InlineClientBundleGenerator implements org.jboss.errai.ioc.client.Type_factory__o_u_e_w_c_c_d_i_LiveSearchSelectorDropDownItemViewImpl__quals__j_e_i_Any_j_e_i_Default.o_u_e_w_c_c_d_i_LiveSearchSelectorDropDownItemViewImplTemplateResource {
  private static Type_factory__o_u_e_w_c_c_d_i_LiveSearchSelectorDropDownItemViewImpl__quals__j_e_i_Any_j_e_i_Default_o_u_e_w_c_c_d_i_LiveSearchSelectorDropDownItemViewImplTemplateResource_default_InlineClientBundleGenerator _instance0 = new Type_factory__o_u_e_w_c_c_d_i_LiveSearchSelectorDropDownItemViewImpl__quals__j_e_i_Any_j_e_i_Default_o_u_e_w_c_c_d_i_LiveSearchSelectorDropDownItemViewImplTemplateResource_default_InlineClientBundleGenerator();
  private void getContentsInitializer() {
    getContents = new com.google.gwt.resources.client.TextResource() {
      // jar:file:/Users/tiagobento/.m2/repository/org/uberfire/uberfire-widgets-commons/7.47.0-SNAPSHOT/uberfire-widgets-commons-7.47.0-SNAPSHOT.jar!/org/uberfire/ext/widgets/common/client/dropdown/items/LiveSearchSelectorDropDownItemViewImpl.html
      public String getText() {
        return "<li role=\"presentation\" data-field=\"item\" class=\"appformer-live-search-selector-dditem\">\n    <a data-field=\"itemAnchor\" role=\"menuitem\" tabindex=\"-1\" href=\"#\">\n        <span data-field=\"itemText\" class=\"text\"></span>\n        <span data-field=\"itemIcon\" class=\"glyphicon glyphicon-ok check-mark\"></span>\n    </a>\n</li>\n";
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
        return com.google.gwt.i18n.client.LocaleInfo.getCurrentLocale().isRTL() ? ((".appformer-live-search-selector-dditem{cursor:" + ("pointer")  + ";}.appformer-live-search-selector-dditem-icon-hidden{display:" + ("none")  + ";}.appformer-live-search-selector-dditem-icon-visible{margin-right:" + ("5px")  + ";cursor:" + ("pointer")  + ";float:" + ("left")  + ";display:" + ("block")  + " !important;}")) : ((".appformer-live-search-selector-dditem{cursor:" + ("pointer")  + ";}.appformer-live-search-selector-dditem-icon-hidden{display:" + ("none")  + ";}.appformer-live-search-selector-dditem-icon-visible{margin-left:" + ("5px")  + ";cursor:" + ("pointer")  + ";float:" + ("right")  + ";display:" + ("block")  + " !important;}"));
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
      case 'getContents': return this.@org.jboss.errai.ioc.client.Type_factory__o_u_e_w_c_c_d_i_LiveSearchSelectorDropDownItemViewImpl__quals__j_e_i_Any_j_e_i_Default.o_u_e_w_c_c_d_i_LiveSearchSelectorDropDownItemViewImplTemplateResource::getContents()();
      case 'getStyle': return this.@org.jboss.errai.ioc.client.Type_factory__o_u_e_w_c_c_d_i_LiveSearchSelectorDropDownItemViewImpl__quals__j_e_i_Any_j_e_i_Default.o_u_e_w_c_c_d_i_LiveSearchSelectorDropDownItemViewImplTemplateResource::getStyle()();
    }
    return null;
  }-*/;
}

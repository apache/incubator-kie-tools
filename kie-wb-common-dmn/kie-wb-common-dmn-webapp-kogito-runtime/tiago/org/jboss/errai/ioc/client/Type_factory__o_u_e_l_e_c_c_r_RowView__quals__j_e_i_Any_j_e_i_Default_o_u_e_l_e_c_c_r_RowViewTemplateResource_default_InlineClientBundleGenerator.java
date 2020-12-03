package org.jboss.errai.ioc.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ResourcePrototype;

public class Type_factory__o_u_e_l_e_c_c_r_RowView__quals__j_e_i_Any_j_e_i_Default_o_u_e_l_e_c_c_r_RowViewTemplateResource_default_InlineClientBundleGenerator implements org.jboss.errai.ioc.client.Type_factory__o_u_e_l_e_c_c_r_RowView__quals__j_e_i_Any_j_e_i_Default.o_u_e_l_e_c_c_r_RowViewTemplateResource {
  private static Type_factory__o_u_e_l_e_c_c_r_RowView__quals__j_e_i_Any_j_e_i_Default_o_u_e_l_e_c_c_r_RowViewTemplateResource_default_InlineClientBundleGenerator _instance0 = new Type_factory__o_u_e_l_e_c_c_r_RowView__quals__j_e_i_Any_j_e_i_Default_o_u_e_l_e_c_c_r_RowViewTemplateResource_default_InlineClientBundleGenerator();
  private void getContentsInitializer() {
    getContents = new com.google.gwt.resources.client.TextResource() {
      // jar:file:/Users/tiagobento/.m2/repository/org/uberfire/uberfire-layout-editor-client/7.47.0-SNAPSHOT/uberfire-layout-editor-client-7.47.0-SNAPSHOT.jar!/org/uberfire/ext/layout/editor/client/components/rows/RowView.html
      public String getText() {
        return "<div data-field=\"mainrow\">\n    <div data-field=\"upper\" class=\"row rowCss uf-row-upper rowCssMove\">\n        <div data-field=\"upper-left\" class=\"uf-row-upper-left\">\n\n        </div>\n        <div data-field=\"upper-center\" class=\"uf-row-upper-center\">\n\n        </div>\n        <div data-field=\"upper-right\" class=\"uf-row-upper-right\">\n\n        </div>\n    </div>\n    <div data-field=\"row\"  class=\"row rowCss\">\n    </div>\n    <div data-field=\"bottom\" class=\"row rowCss uf-row-bottom rowCssMove\">\n        <div data-field=\"bottom-left\" class=\"uf-row-bottom-left\">\n\n        </div>\n        <div data-field=\"bottom-center\" class=\"uf-row-bottom-center\">\n\n        </div>\n        <div data-field=\"bottom-right\" class=\"uf-row-bottom-right\">\n\n        </div>\n    </div>\n</div>\n";
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
        return com.google.gwt.i18n.client.LocaleInfo.getCurrentLocale().isRTL() ? ((".rowDropPreview{background-color:" + ("rgba(" + "204"+ ","+ " " +"204"+ ","+ " " +"204"+ ","+ " " +"0.5" + ")")  + ";height:" + ("20px")  + " !important;}.rowCss{padding-right:" + ("0")  + " !important;padding-left:" + ("0")  + " !important;padding-top:" + ("1px")  + " !important;padding-bottom:" + ("1px")  + " !important;}.rowDndPreview{opacity:" + ("0.1")  + ";}.rowMovePreview{background-color:" + ("rgba(" + "204"+ ","+ " " +"204"+ ","+ " " +"204"+ ","+ " " +"0.5" + ")")  + ";cursor:" + ("move")  + ";}.uf-row-bottom{height:" + ("10px")  + ";display:") + (("flex")  + ";}.uf-row-bottom-left{height:" + ("100%")  + ";width:" + ("40%")  + ";}.uf-row-bottom-center{height:" + ("100%")  + ";width:" + ("20%")  + ";}.uf-row-bottom-right{height:" + ("100%")  + ";width:" + ("40%")  + ";}.uf-row-upper{height:" + ("10px")  + ";display:" + ("flex")  + ";}.uf-row-upper-left{height:" + ("100%")  + ";width:" + ("40%") ) + (";}.uf-row-upper-center{height:" + ("100%")  + ";width:" + ("20%")  + ";}.uf-row-upper-right{height:" + ("100%")  + ";width:" + ("40%")  + ";}.rowResizeUp{cursor:" + ("n-resize")  + ";}.rowResizeDown{cursor:" + ("s-resize")  + ";}")) : ((".rowDropPreview{background-color:" + ("rgba(" + "204"+ ","+ " " +"204"+ ","+ " " +"204"+ ","+ " " +"0.5" + ")")  + ";height:" + ("20px")  + " !important;}.rowCss{padding-left:" + ("0")  + " !important;padding-right:" + ("0")  + " !important;padding-top:" + ("1px")  + " !important;padding-bottom:" + ("1px")  + " !important;}.rowDndPreview{opacity:" + ("0.1")  + ";}.rowMovePreview{background-color:" + ("rgba(" + "204"+ ","+ " " +"204"+ ","+ " " +"204"+ ","+ " " +"0.5" + ")")  + ";cursor:" + ("move")  + ";}.uf-row-bottom{height:" + ("10px")  + ";display:") + (("flex")  + ";}.uf-row-bottom-left{height:" + ("100%")  + ";width:" + ("40%")  + ";}.uf-row-bottom-center{height:" + ("100%")  + ";width:" + ("20%")  + ";}.uf-row-bottom-right{height:" + ("100%")  + ";width:" + ("40%")  + ";}.uf-row-upper{height:" + ("10px")  + ";display:" + ("flex")  + ";}.uf-row-upper-left{height:" + ("100%")  + ";width:" + ("40%") ) + (";}.uf-row-upper-center{height:" + ("100%")  + ";width:" + ("20%")  + ";}.uf-row-upper-right{height:" + ("100%")  + ";width:" + ("40%")  + ";}.rowResizeUp{cursor:" + ("n-resize")  + ";}.rowResizeDown{cursor:" + ("s-resize")  + ";}"));
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
      case 'getContents': return this.@org.jboss.errai.ioc.client.Type_factory__o_u_e_l_e_c_c_r_RowView__quals__j_e_i_Any_j_e_i_Default.o_u_e_l_e_c_c_r_RowViewTemplateResource::getContents()();
      case 'getStyle': return this.@org.jboss.errai.ioc.client.Type_factory__o_u_e_l_e_c_c_r_RowView__quals__j_e_i_Any_j_e_i_Default.o_u_e_l_e_c_c_r_RowViewTemplateResource::getStyle()();
    }
    return null;
  }-*/;
}

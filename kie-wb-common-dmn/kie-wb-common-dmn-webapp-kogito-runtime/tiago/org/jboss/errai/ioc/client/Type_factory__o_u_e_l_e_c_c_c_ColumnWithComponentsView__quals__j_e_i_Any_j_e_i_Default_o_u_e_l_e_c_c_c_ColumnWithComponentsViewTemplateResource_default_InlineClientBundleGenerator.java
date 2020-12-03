package org.jboss.errai.ioc.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ResourcePrototype;

public class Type_factory__o_u_e_l_e_c_c_c_ColumnWithComponentsView__quals__j_e_i_Any_j_e_i_Default_o_u_e_l_e_c_c_c_ColumnWithComponentsViewTemplateResource_default_InlineClientBundleGenerator implements org.jboss.errai.ioc.client.Type_factory__o_u_e_l_e_c_c_c_ColumnWithComponentsView__quals__j_e_i_Any_j_e_i_Default.o_u_e_l_e_c_c_c_ColumnWithComponentsViewTemplateResource {
  private static Type_factory__o_u_e_l_e_c_c_c_ColumnWithComponentsView__quals__j_e_i_Any_j_e_i_Default_o_u_e_l_e_c_c_c_ColumnWithComponentsViewTemplateResource_default_InlineClientBundleGenerator _instance0 = new Type_factory__o_u_e_l_e_c_c_c_ColumnWithComponentsView__quals__j_e_i_Any_j_e_i_Default_o_u_e_l_e_c_c_c_ColumnWithComponentsViewTemplateResource_default_InlineClientBundleGenerator();
  private void getContentsInitializer() {
    getContents = new com.google.gwt.resources.client.TextResource() {
      // jar:file:/Users/tiagobento/.m2/repository/org/uberfire/uberfire-layout-editor-client/7.47.0-SNAPSHOT/uberfire-layout-editor-client-7.47.0-SNAPSHOT.jar!/org/uberfire/ext/layout/editor/client/components/columns/ColumnWithComponentsView.html
      public String getText() {
        return "<div data-field=\"colWithComponents\" class=\"colWithComponents\">\n    <div data-field=\"inner-col-colwithComponents\" class=\"col-md-12\">\n        <div data-field=\"row\" class=\"row eq-height\">\n            <div data-field=\"left\" class=\"left\">\n            </div>\n            <div data-field=\"content\" class=\"center\">\n            </div>\n            <div data-field=\"right\" class=\"right\">\n            </div>\n        </div>\n    </div>\n</div>\n";
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
        return com.google.gwt.i18n.client.LocaleInfo.getCurrentLocale().isRTL() ? ((".left{float:" + ("right")  + ";z-index:" + ("1")  + ";}.right{float:" + ("left")  + ";z-index:" + ("1")  + ";}.colUp{height:" + ("5px")  + ";}.colPreview{height:" + ("15px")  + ";margin-right:" + ("5px")  + ";margin-left:" + ("5px")  + ";}.colDown{height:" + ("5px")  + ";}.center{display:" + ("inline-block")  + ";width:") + (("90%")  + ";border:" + ("1px"+ " " +"solid"+ " " +"lightgray")  + ";}.centerPreview,.dropPreview{width:" + ("50%")  + " !important;}.colWithComponents{padding-right:" + ("0")  + ";padding-left:" + ("0")  + ";}.colResizeLeft{cursor:" + ("e-resize")  + ";}.colResizeRight{cursor:" + ("w-resize")  + ";}")) : ((".left{float:" + ("left")  + ";z-index:" + ("1")  + ";}.right{float:" + ("right")  + ";z-index:" + ("1")  + ";}.colUp{height:" + ("5px")  + ";}.colPreview{height:" + ("15px")  + ";margin-left:" + ("5px")  + ";margin-right:" + ("5px")  + ";}.colDown{height:" + ("5px")  + ";}.center{display:" + ("inline-block")  + ";width:") + (("90%")  + ";border:" + ("1px"+ " " +"solid"+ " " +"lightgray")  + ";}.centerPreview,.dropPreview{width:" + ("50%")  + " !important;}.colWithComponents{padding-left:" + ("0")  + ";padding-right:" + ("0")  + ";}.colResizeLeft{cursor:" + ("w-resize")  + ";}.colResizeRight{cursor:" + ("e-resize")  + ";}"));
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
      case 'getContents': return this.@org.jboss.errai.ioc.client.Type_factory__o_u_e_l_e_c_c_c_ColumnWithComponentsView__quals__j_e_i_Any_j_e_i_Default.o_u_e_l_e_c_c_c_ColumnWithComponentsViewTemplateResource::getContents()();
      case 'getStyle': return this.@org.jboss.errai.ioc.client.Type_factory__o_u_e_l_e_c_c_c_ColumnWithComponentsView__quals__j_e_i_Any_j_e_i_Default.o_u_e_l_e_c_c_c_ColumnWithComponentsViewTemplateResource::getStyle()();
    }
    return null;
  }-*/;
}

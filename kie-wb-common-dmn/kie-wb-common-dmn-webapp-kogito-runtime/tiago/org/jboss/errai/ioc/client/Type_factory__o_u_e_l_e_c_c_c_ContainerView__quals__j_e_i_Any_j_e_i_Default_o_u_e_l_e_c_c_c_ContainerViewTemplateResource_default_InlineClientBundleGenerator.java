package org.jboss.errai.ioc.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ResourcePrototype;

public class Type_factory__o_u_e_l_e_c_c_c_ContainerView__quals__j_e_i_Any_j_e_i_Default_o_u_e_l_e_c_c_c_ContainerViewTemplateResource_default_InlineClientBundleGenerator implements org.jboss.errai.ioc.client.Type_factory__o_u_e_l_e_c_c_c_ContainerView__quals__j_e_i_Any_j_e_i_Default.o_u_e_l_e_c_c_c_ContainerViewTemplateResource {
  private static Type_factory__o_u_e_l_e_c_c_c_ContainerView__quals__j_e_i_Any_j_e_i_Default_o_u_e_l_e_c_c_c_ContainerViewTemplateResource_default_InlineClientBundleGenerator _instance0 = new Type_factory__o_u_e_l_e_c_c_c_ContainerView__quals__j_e_i_Any_j_e_i_Default_o_u_e_l_e_c_c_c_ContainerViewTemplateResource_default_InlineClientBundleGenerator();
  private void getContentsInitializer() {
    getContents = new com.google.gwt.resources.client.TextResource() {
      // jar:file:/Users/tiagobento/.m2/repository/org/uberfire/uberfire-layout-editor-client/7.47.0-SNAPSHOT/uberfire-layout-editor-client-7.47.0-SNAPSHOT.jar!/org/uberfire/ext/layout/editor/client/components/container/ContainerView.html
      public String getText() {
        return "<div id=\"container\" class=\"le-full-height\">\n    <div id=\"layout\" class=\"container-fluid container-canvas\">\n        <div id=\"header\" class=\"container-header\"></div>\n    </div>\n</div>";
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
        return com.google.gwt.i18n.client.LocaleInfo.getCurrentLocale().isRTL() ? ((".container-canvas{background-color:" + ("white")  + ";border:" + ("1px"+ " " +"solid"+ " " +"#b1caf2")  + ";-webkit-box-shadow:" + ("2px"+ " " +"4px"+ " " +"6px"+ " " +"1px"+ " " +"rgba(" + "211"+ ","+ " " +"211"+ ","+ " " +"211"+ ","+ " " +"0.25" + ")")  + " !important;-moz-box-shadow:" + ("2px"+ " " +"4px"+ " " +"6px"+ " " +"1px"+ " " +"rgba(" + "211"+ ","+ " " +"211"+ ","+ " " +"211"+ ","+ " " +"0.25" + ")")  + " !important;box-shadow:" + ("2px"+ " " +"4px"+ " " +"6px"+ " " +"1px"+ " " +"rgba(" + "211"+ ","+ " " +"211"+ ","+ " " +"211"+ ","+ " " +"0.25" + ")")  + " !important;padding-right:" + ("25px")  + ";padding-left:" + ("25px")  + ";padding-top:" + ("7px")  + ";padding-bottom:" + ("7px")  + ";}.page-container{height:" + ("100%")  + ";}.container-empty{padding:") + (("0")  + ";height:" + ("100%")  + ";}.container-header{height:" + ("15px")  + ";cursor:" + ("pointer")  + ";margin-top:" + ("-15px")  + ";}.container-selected{border-color:" + ("#33a6cc")  + " !important;}.resolution{text-align:" + ("center")  + ";font-size:" + ("20px")  + ";}.simulate-xs .col-md-1,.simulate-xs .col-md-2,.simulate-xs .col-md-3,.simulate-xs .col-md-4,.simulate-xs .col-md-5,.simulate-xs .col-md-6,.simulate-xs .col-md-7,.simulate-xs .col-md-8,.simulate-xs .col-md-9,.simulate-xs .col-md-10,.simulate-xs .col-md-11,.simulate-xs .col-md-12{width:" + ("100%")  + ";}.simulate-sm .col-md-2{width:" + ("20%")  + ";}.simulate-sm .col-md-4{width:" + ("50%") ) + (";}.simulate-sm .col-md-6{width:" + ("100%")  + ";}")) : ((".container-canvas{background-color:" + ("white")  + ";border:" + ("1px"+ " " +"solid"+ " " +"#b1caf2")  + ";-webkit-box-shadow:" + ("2px"+ " " +"4px"+ " " +"6px"+ " " +"1px"+ " " +"rgba(" + "211"+ ","+ " " +"211"+ ","+ " " +"211"+ ","+ " " +"0.25" + ")")  + " !important;-moz-box-shadow:" + ("2px"+ " " +"4px"+ " " +"6px"+ " " +"1px"+ " " +"rgba(" + "211"+ ","+ " " +"211"+ ","+ " " +"211"+ ","+ " " +"0.25" + ")")  + " !important;box-shadow:" + ("2px"+ " " +"4px"+ " " +"6px"+ " " +"1px"+ " " +"rgba(" + "211"+ ","+ " " +"211"+ ","+ " " +"211"+ ","+ " " +"0.25" + ")")  + " !important;padding-left:" + ("25px")  + ";padding-right:" + ("25px")  + ";padding-top:" + ("7px")  + ";padding-bottom:" + ("7px")  + ";}.page-container{height:" + ("100%")  + ";}.container-empty{padding:") + (("0")  + ";height:" + ("100%")  + ";}.container-header{height:" + ("15px")  + ";cursor:" + ("pointer")  + ";margin-top:" + ("-15px")  + ";}.container-selected{border-color:" + ("#33a6cc")  + " !important;}.resolution{text-align:" + ("center")  + ";font-size:" + ("20px")  + ";}.simulate-xs .col-md-1,.simulate-xs .col-md-2,.simulate-xs .col-md-3,.simulate-xs .col-md-4,.simulate-xs .col-md-5,.simulate-xs .col-md-6,.simulate-xs .col-md-7,.simulate-xs .col-md-8,.simulate-xs .col-md-9,.simulate-xs .col-md-10,.simulate-xs .col-md-11,.simulate-xs .col-md-12{width:" + ("100%")  + ";}.simulate-sm .col-md-2{width:" + ("20%")  + ";}.simulate-sm .col-md-4{width:" + ("50%") ) + (";}.simulate-sm .col-md-6{width:" + ("100%")  + ";}"));
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
      case 'getContents': return this.@org.jboss.errai.ioc.client.Type_factory__o_u_e_l_e_c_c_c_ContainerView__quals__j_e_i_Any_j_e_i_Default.o_u_e_l_e_c_c_c_ContainerViewTemplateResource::getContents()();
      case 'getStyle': return this.@org.jboss.errai.ioc.client.Type_factory__o_u_e_l_e_c_c_c_ContainerView__quals__j_e_i_Any_j_e_i_Default.o_u_e_l_e_c_c_c_ContainerViewTemplateResource::getStyle()();
    }
    return null;
  }-*/;
}

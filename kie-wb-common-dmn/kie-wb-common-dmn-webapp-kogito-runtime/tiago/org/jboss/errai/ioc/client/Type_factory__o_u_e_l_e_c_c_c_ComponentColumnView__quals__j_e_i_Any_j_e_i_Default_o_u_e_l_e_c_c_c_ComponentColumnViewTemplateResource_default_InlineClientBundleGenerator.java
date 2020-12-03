package org.jboss.errai.ioc.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ResourcePrototype;

public class Type_factory__o_u_e_l_e_c_c_c_ComponentColumnView__quals__j_e_i_Any_j_e_i_Default_o_u_e_l_e_c_c_c_ComponentColumnViewTemplateResource_default_InlineClientBundleGenerator implements org.jboss.errai.ioc.client.Type_factory__o_u_e_l_e_c_c_c_ComponentColumnView__quals__j_e_i_Any_j_e_i_Default.o_u_e_l_e_c_c_c_ComponentColumnViewTemplateResource {
  private static Type_factory__o_u_e_l_e_c_c_c_ComponentColumnView__quals__j_e_i_Any_j_e_i_Default_o_u_e_l_e_c_c_c_ComponentColumnViewTemplateResource_default_InlineClientBundleGenerator _instance0 = new Type_factory__o_u_e_l_e_c_c_c_ComponentColumnView__quals__j_e_i_Any_j_e_i_Default_o_u_e_l_e_c_c_c_ComponentColumnViewTemplateResource_default_InlineClientBundleGenerator();
  private void getContentsInitializer() {
    getContents = new com.google.gwt.resources.client.TextResource() {
      // jar:file:/Users/tiagobento/.m2/repository/org/uberfire/uberfire-layout-editor-client/7.47.0-SNAPSHOT/uberfire-layout-editor-client-7.47.0-SNAPSHOT.jar!/org/uberfire/ext/layout/editor/client/components/columns/ComponentColumnView.html
      public String getText() {
        return "<div data-field=\"col\">\n    <div data-field=\"row\" class=\"row el-component-row\">\n        <div class=\"col-md-12 el-content-area\">\n            <div data-field=\"colUp\">\n            </div>\n        </div>\n        <div data-field=\"content-area\" class=\"col-md-12 eq-height el-content-area\">\n            <div data-field=\"left\" class=\"left\">\n            </div>\n            <div draggable=\"true\" data-field=\"content\" class=\"center el-content\"></div>\n            <div data-field=\"right\" class=\"right\">\n            </div>\n        </div>\n        <div class=\"col-md-12 el-content-area\">\n            <div data-field=\"colDown\">\n\n            </div>\n        </div>\n    </div>\n</div>\n";
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
        return com.google.gwt.i18n.client.LocaleInfo.getCurrentLocale().isRTL() ? ((".no-padding{padding-left:" + ("0")  + ";padding-right:" + ("3px")  + ";}.eq-height{display:" + ("-webkit-box")  + ";display:" + ("-webkit-flex")  + ";display:" + ("-ms-flexbox")  + ";display:" + ("flex")  + ";}.page-col{height:" + ("100%")  + ";}.page-col-inner{padding-bottom:" + ("7px")  + " !important;padding-top:" + ("7px")  + " !important;}.left{float:" + ("right")  + ";}.right{float:") + (("left")  + ";}.el-resize-button{padding:" + ("0")  + ";}.colUp{height:" + ("5px")  + ";}.componentDropInColumnPreview{height:" + ("15px")  + ";margin-right:" + ("15px")  + ";margin-left:" + ("20px")  + ";background:" + ("#dde0e2")  + ";background:" + ("-moz-linear-gradient(" + "top"+ ","+ " " +"#eee"+ " " +"0"+ ","+ " " +"#dde0e2"+ " " +"100%" + ")")  + ";background:" + ("-webkit-linear-gradient(" + "top"+ ","+ " " +"#eee"+ " " +"0"+ ","+ " " +"#dde0e2"+ " " +"100%" + ")")  + ";background:" + ("linear-gradient(" + "to"+ " " +"bottom"+ ","+ " " +"#eee"+ " " +"0"+ ","+ " " +"#dde0e2"+ " " +"100%" + ")")  + ";filter:" + ("progid") ) + (";}.colDown{height:" + ("5px")  + ";}.center{display:" + ("inline-block")  + ";width:" + ("90%")  + ";position:" + ("relative")  + ";}.centerPreview{width:" + ("50%")  + " !important;}.dropPreview{width:" + ("50%")  + " !important;background:" + ("#dde0e2")  + ";background:" + ("-moz-linear-gradient(" + "top"+ ","+ " " +"#eee"+ " " +"0"+ ","+ " " +"#dde0e2"+ " " +"100%" + ")")  + ";background:" + ("-webkit-linear-gradient(" + "top"+ ","+ " " +"#eee"+ " " +"0"+ ","+ " " +"#dde0e2"+ " " +"100%" + ")")  + ";background:" + ("linear-gradient(" + "to"+ " " +"bottom"+ ","+ " " +"#eee"+ " " +"0"+ ","+ " " +"#dde0e2"+ " " +"100%" + ")")  + ";filter:") + (("progid")  + ";}.columnDropPreview{z-index:" + ("1")  + ";}.buttonsPanel{text-align:" + ("left")  + ";}.el-content{border:" + ("1px"+ " " +"solid"+ " " +"#b1caf2")  + ";-webkit-box-shadow:" + ("2px"+ " " +"4px"+ " " +"6px"+ " " +"1px"+ " " +"rgba(" + "211"+ ","+ " " +"211"+ ","+ " " +"211"+ ","+ " " +"0.25" + ")")  + " !important;-moz-box-shadow:" + ("2px"+ " " +"4px"+ " " +"6px"+ " " +"1px"+ " " +"rgba(" + "211"+ ","+ " " +"211"+ ","+ " " +"211"+ ","+ " " +"0.25" + ")")  + " !important;box-shadow:" + ("2px"+ " " +"4px"+ " " +"6px"+ " " +"1px"+ " " +"rgba(" + "211"+ ","+ " " +"211"+ ","+ " " +"211"+ ","+ " " +"0.25" + ")")  + " !important;padding-left:" + ("7px")  + ";padding-right:" + ("15px")  + ";padding-top:" + ("10px")  + ";padding-bottom:" + ("10px") ) + (";}.el-content-area{padding:" + ("0")  + ";}.el-component-row{margin-right:" + ("-3px")  + " !important;margin-left:" + ("-3px")  + " !important;}.componentMovePreview{border-color:" + ("#33a6cc")  + " !important;cursor:" + ("move")  + ";}.colResizeLeft{cursor:" + ("e-resize")  + ";}.colResizeRight{cursor:" + ("w-resize")  + ";}")) : ((".no-padding{padding-right:" + ("0")  + ";padding-left:" + ("3px")  + ";}.eq-height{display:" + ("-webkit-box")  + ";display:" + ("-webkit-flex")  + ";display:" + ("-ms-flexbox")  + ";display:" + ("flex")  + ";}.page-col{height:" + ("100%")  + ";}.page-col-inner{padding-bottom:" + ("7px")  + " !important;padding-top:" + ("7px")  + " !important;}.left{float:" + ("left")  + ";}.right{float:") + (("right")  + ";}.el-resize-button{padding:" + ("0")  + ";}.colUp{height:" + ("5px")  + ";}.componentDropInColumnPreview{height:" + ("15px")  + ";margin-left:" + ("15px")  + ";margin-right:" + ("20px")  + ";background:" + ("#dde0e2")  + ";background:" + ("-moz-linear-gradient(" + "top"+ ","+ " " +"#eee"+ " " +"0"+ ","+ " " +"#dde0e2"+ " " +"100%" + ")")  + ";background:" + ("-webkit-linear-gradient(" + "top"+ ","+ " " +"#eee"+ " " +"0"+ ","+ " " +"#dde0e2"+ " " +"100%" + ")")  + ";background:" + ("linear-gradient(" + "to"+ " " +"bottom"+ ","+ " " +"#eee"+ " " +"0"+ ","+ " " +"#dde0e2"+ " " +"100%" + ")")  + ";filter:" + ("progid") ) + (";}.colDown{height:" + ("5px")  + ";}.center{display:" + ("inline-block")  + ";width:" + ("90%")  + ";position:" + ("relative")  + ";}.centerPreview{width:" + ("50%")  + " !important;}.dropPreview{width:" + ("50%")  + " !important;background:" + ("#dde0e2")  + ";background:" + ("-moz-linear-gradient(" + "top"+ ","+ " " +"#eee"+ " " +"0"+ ","+ " " +"#dde0e2"+ " " +"100%" + ")")  + ";background:" + ("-webkit-linear-gradient(" + "top"+ ","+ " " +"#eee"+ " " +"0"+ ","+ " " +"#dde0e2"+ " " +"100%" + ")")  + ";background:" + ("linear-gradient(" + "to"+ " " +"bottom"+ ","+ " " +"#eee"+ " " +"0"+ ","+ " " +"#dde0e2"+ " " +"100%" + ")")  + ";filter:") + (("progid")  + ";}.columnDropPreview{z-index:" + ("1")  + ";}.buttonsPanel{text-align:" + ("right")  + ";}.el-content{border:" + ("1px"+ " " +"solid"+ " " +"#b1caf2")  + ";-webkit-box-shadow:" + ("2px"+ " " +"4px"+ " " +"6px"+ " " +"1px"+ " " +"rgba(" + "211"+ ","+ " " +"211"+ ","+ " " +"211"+ ","+ " " +"0.25" + ")")  + " !important;-moz-box-shadow:" + ("2px"+ " " +"4px"+ " " +"6px"+ " " +"1px"+ " " +"rgba(" + "211"+ ","+ " " +"211"+ ","+ " " +"211"+ ","+ " " +"0.25" + ")")  + " !important;box-shadow:" + ("2px"+ " " +"4px"+ " " +"6px"+ " " +"1px"+ " " +"rgba(" + "211"+ ","+ " " +"211"+ ","+ " " +"211"+ ","+ " " +"0.25" + ")")  + " !important;padding-right:" + ("7px")  + ";padding-left:" + ("15px")  + ";padding-top:" + ("10px")  + ";padding-bottom:" + ("10px") ) + (";}.el-content-area{padding:" + ("0")  + ";}.el-component-row{margin-left:" + ("-3px")  + " !important;margin-right:" + ("-3px")  + " !important;}.componentMovePreview{border-color:" + ("#33a6cc")  + " !important;cursor:" + ("move")  + ";}.colResizeLeft{cursor:" + ("w-resize")  + ";}.colResizeRight{cursor:" + ("e-resize")  + ";}"));
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
      case 'getContents': return this.@org.jboss.errai.ioc.client.Type_factory__o_u_e_l_e_c_c_c_ComponentColumnView__quals__j_e_i_Any_j_e_i_Default.o_u_e_l_e_c_c_c_ComponentColumnViewTemplateResource::getContents()();
      case 'getStyle': return this.@org.jboss.errai.ioc.client.Type_factory__o_u_e_l_e_c_c_c_ComponentColumnView__quals__j_e_i_Any_j_e_i_Default.o_u_e_l_e_c_c_c_ComponentColumnViewTemplateResource::getStyle()();
    }
    return null;
  }-*/;
}

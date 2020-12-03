package org.jboss.errai.ioc.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ResourcePrototype;

public class Type_factory__o_u_e_l_e_c_LayoutEditorView__quals__j_e_i_Any_j_e_i_Default_o_u_e_l_e_c_LayoutEditorViewTemplateResource_default_InlineClientBundleGenerator implements org.jboss.errai.ioc.client.Type_factory__o_u_e_l_e_c_LayoutEditorView__quals__j_e_i_Any_j_e_i_Default.o_u_e_l_e_c_LayoutEditorViewTemplateResource {
  private static Type_factory__o_u_e_l_e_c_LayoutEditorView__quals__j_e_i_Any_j_e_i_Default_o_u_e_l_e_c_LayoutEditorViewTemplateResource_default_InlineClientBundleGenerator _instance0 = new Type_factory__o_u_e_l_e_c_LayoutEditorView__quals__j_e_i_Any_j_e_i_Default_o_u_e_l_e_c_LayoutEditorViewTemplateResource_default_InlineClientBundleGenerator();
  private void getContentsInitializer() {
    getContents = new com.google.gwt.resources.client.TextResource() {
      // jar:file:/Users/tiagobento/.m2/repository/org/uberfire/uberfire-layout-editor-client/7.47.0-SNAPSHOT/uberfire-layout-editor-client-7.47.0-SNAPSHOT.jar!/org/uberfire/ext/layout/editor/client/LayoutEditorView.html
      public String getText() {
        return "<div data-field=\"mainDiv\">\n    <div data-field=\"tabsDiv\">\n        <ul class=\"nav nav-tabs nav-tabs-pf\">\n            <li data-field=\"designTab\" class=\"active\" style=\"margin-left: 10px\"><a href=\"#\" data-field=\"designAnchor\">Editor!</a></li>\n            <li data-field=\"previewTab\"><a href=\"#\" data-field=\"previewAnchor\">Preview!</a></li>\n        </ul>\n    </div>\n    <div data-field=\"designDiv\" class=\"le-design-container\">\n        <div class=\"container-fluid el-container\">\n            <div class=\"row le-full-height\">\n                <div data-field=\"container\" class=\"col-md-12 le-full-height\">\n\n                </div>\n            </div>\n        </div>\n    </div>\n    <div data-field=\"previewDiv\" class=\"le-preview-container\">\n\n    </div>\n</div>\n\n";
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
        return com.google.gwt.i18n.client.LocaleInfo.getCurrentLocale().isRTL() ? ((".el-container{height:" + ("100%")  + ";padding:" + ("0"+ " " +"0"+ " " +"0"+ " " +"5px")  + ";}.le-component{padding-top:" + ("10px")  + ";padding-right:" + ("10px")  + ";padding-left:" + ("10px")  + ";padding-bottom:" + ("3px")  + ";border:" + ("0")  + " !important;}.le-heading{border:" + ("1px")  + " !important;border-style:" + ("solid")  + " !important;border-color:" + ("#ddd")  + " !important;background:") + (("white")  + " !important;-webkit-box-shadow:" + ("2px"+ " " +"4px"+ " " +"6px"+ " " +"1px"+ " " +"rgba(" + "211"+ ","+ " " +"211"+ ","+ " " +"211"+ ","+ " " +"0.25" + ")")  + " !important;-moz-box-shadow:" + ("2px"+ " " +"4px"+ " " +"6px"+ " " +"1px"+ " " +"rgba(" + "211"+ ","+ " " +"211"+ ","+ " " +"211"+ ","+ " " +"0.25" + ")")  + " !important;box-shadow:" + ("2px"+ " " +"4px"+ " " +"6px"+ " " +"1px"+ " " +"rgba(" + "211"+ ","+ " " +"211"+ ","+ " " +"211"+ ","+ " " +"0.25" + ")")  + " !important;}.le-title{font-family:" + ("\"Open Sans\"")  + ";font-size:" + ("12px")  + ";}.le-icon{font-size:" + ("16px")  + ";margin-right:" + ("10px")  + ";margin-left:" + ("10px")  + ";}.le-dndcomponent{background-color:" + ("rgba(" + "204"+ ","+ " " +"204"+ ","+ " " +"204"+ ","+ " " +"0.1" + ")")  + ";height:" + ("35px") ) + (";margin-right:" + ("10px")  + ";margin-top:" + ("5px")  + ";margin-bottom:" + ("5px")  + ";border:" + ("1px"+ " " +"solid"+ " " +"#ddd")  + " !important;-webkit-box-shadow:" + ("2px"+ " " +"4px"+ " " +"6px"+ " " +"1px"+ " " +"rgba(" + "211"+ ","+ " " +"211"+ ","+ " " +"211"+ ","+ " " +"0.25" + ")")  + " !important;-moz-box-shadow:" + ("2px"+ " " +"4px"+ " " +"6px"+ " " +"1px"+ " " +"rgba(" + "211"+ ","+ " " +"211"+ ","+ " " +"211"+ ","+ " " +"0.25" + ")")  + " !important;box-shadow:" + ("2px"+ " " +"4px"+ " " +"6px"+ " " +"1px"+ " " +"rgba(" + "211"+ ","+ " " +"211"+ ","+ " " +"211"+ ","+ " " +"0.25" + ")")  + " !important;cursor:" + ("move")  + ";}.le-dndcomponent-selected{box-shadow:" + ("0"+ " " +"0"+ " " +"5px"+ " " +"#b1caf2")  + " !important;border:" + ("1px"+ " " +"solid"+ " " +"#b1caf2")  + " !important;}.le-dndcomponent-inner{position:") + (("relative")  + ";top:" + ("50%")  + ";-webkit-transform:" + ("translateY(" + "-50%" + ")")  + ";-ms-transform:" + ("translateY(" + "-50%" + ")")  + ";transform:" + ("translateY(" + "-50%" + ")")  + ";}.le-design-fluid{min-height:" + ("100%")  + ";}.le-design-page{height:" + ("100%")  + ";}.le-design-container{padding:" + ("16px")  + ";background:" + ("linear-gradient(" + "#f6f6f6"+ " " +"18px"+ ","+ " " +"transparent"+ " " +"18px" + ")"+ " " +"0"+ " " +"-2px"+ ","+ " " +"linear-gradient(" + "90deg"+ ","+ " " +"#f6f6f6"+ " " +"18px"+ ","+ " " +"transparent"+ " " +"0" + ")"+ " " +"-2px"+ " " +"0")  + ";background-color:" + ("#bbb")  + ";background-size:" + ("20px"+ " " +"20px") ) + (";}.le-preview-container{padding:" + ("16px")  + ";height:" + ("100%")  + ";}.le-empty{text-align:" + ("center")  + ";background-color:" + ("rgba(" + "244"+ ","+ " " +"244"+ ","+ " " +"244"+ ","+ " " +"1" + ")")  + ";padding-top:" + ("75px")  + ";padding-bottom:" + ("100px")  + ";padding-right:" + ("100px")  + ";padding-left:" + ("100px")  + ";height:" + ("100%")  + ";}.le-empty-border{border:" + ("1px"+ " " +"solid"+ " " +"rgba(" + "244"+ ","+ " " +"244"+ ","+ " " +"244"+ ","+ " " +"1" + ")")  + ";}.le-empty-preview-drop{border:") + (("1px"+ " " +"solid"+ " " +"#b1caf2")  + " !important;}.le-empty-inner-preview-drop{opacity:" + ("0.1")  + ";}.le-empty-icon{font-size:" + ("57.6px")  + ";color:" + ("#9c9c9c")  + ";line-height:" + ("57.6px")  + ";}.le-kebab{width:" + ("10px")  + ";float:" + ("left")  + ";z-index:" + ("10")  + ";}.le-kebab-button{color:" + ("#878787")  + " !important;}.le-widget{cursor:" + ("default")  + ";}.le-full-height{height:" + ("100%") ) + (";}")) : ((".el-container{height:" + ("100%")  + ";padding:" + ("0"+ " " +"5px"+ " " +"0"+ " " +"0")  + ";}.le-component{padding-top:" + ("10px")  + ";padding-left:" + ("10px")  + ";padding-right:" + ("10px")  + ";padding-bottom:" + ("3px")  + ";border:" + ("0")  + " !important;}.le-heading{border:" + ("1px")  + " !important;border-style:" + ("solid")  + " !important;border-color:" + ("#ddd")  + " !important;background:") + (("white")  + " !important;-webkit-box-shadow:" + ("2px"+ " " +"4px"+ " " +"6px"+ " " +"1px"+ " " +"rgba(" + "211"+ ","+ " " +"211"+ ","+ " " +"211"+ ","+ " " +"0.25" + ")")  + " !important;-moz-box-shadow:" + ("2px"+ " " +"4px"+ " " +"6px"+ " " +"1px"+ " " +"rgba(" + "211"+ ","+ " " +"211"+ ","+ " " +"211"+ ","+ " " +"0.25" + ")")  + " !important;box-shadow:" + ("2px"+ " " +"4px"+ " " +"6px"+ " " +"1px"+ " " +"rgba(" + "211"+ ","+ " " +"211"+ ","+ " " +"211"+ ","+ " " +"0.25" + ")")  + " !important;}.le-title{font-family:" + ("\"Open Sans\"")  + ";font-size:" + ("12px")  + ";}.le-icon{font-size:" + ("16px")  + ";margin-left:" + ("10px")  + ";margin-right:" + ("10px")  + ";}.le-dndcomponent{background-color:" + ("rgba(" + "204"+ ","+ " " +"204"+ ","+ " " +"204"+ ","+ " " +"0.1" + ")")  + ";height:" + ("35px") ) + (";margin-left:" + ("10px")  + ";margin-top:" + ("5px")  + ";margin-bottom:" + ("5px")  + ";border:" + ("1px"+ " " +"solid"+ " " +"#ddd")  + " !important;-webkit-box-shadow:" + ("2px"+ " " +"4px"+ " " +"6px"+ " " +"1px"+ " " +"rgba(" + "211"+ ","+ " " +"211"+ ","+ " " +"211"+ ","+ " " +"0.25" + ")")  + " !important;-moz-box-shadow:" + ("2px"+ " " +"4px"+ " " +"6px"+ " " +"1px"+ " " +"rgba(" + "211"+ ","+ " " +"211"+ ","+ " " +"211"+ ","+ " " +"0.25" + ")")  + " !important;box-shadow:" + ("2px"+ " " +"4px"+ " " +"6px"+ " " +"1px"+ " " +"rgba(" + "211"+ ","+ " " +"211"+ ","+ " " +"211"+ ","+ " " +"0.25" + ")")  + " !important;cursor:" + ("move")  + ";}.le-dndcomponent-selected{box-shadow:" + ("0"+ " " +"0"+ " " +"5px"+ " " +"#b1caf2")  + " !important;border:" + ("1px"+ " " +"solid"+ " " +"#b1caf2")  + " !important;}.le-dndcomponent-inner{position:") + (("relative")  + ";top:" + ("50%")  + ";-webkit-transform:" + ("translateY(" + "-50%" + ")")  + ";-ms-transform:" + ("translateY(" + "-50%" + ")")  + ";transform:" + ("translateY(" + "-50%" + ")")  + ";}.le-design-fluid{min-height:" + ("100%")  + ";}.le-design-page{height:" + ("100%")  + ";}.le-design-container{padding:" + ("16px")  + ";background:" + ("linear-gradient(" + "#f6f6f6"+ " " +"18px"+ ","+ " " +"transparent"+ " " +"18px" + ")"+ " " +"0"+ " " +"-2px"+ ","+ " " +"linear-gradient(" + "90deg"+ ","+ " " +"#f6f6f6"+ " " +"18px"+ ","+ " " +"transparent"+ " " +"0" + ")"+ " " +"-2px"+ " " +"0")  + ";background-color:" + ("#bbb")  + ";background-size:" + ("20px"+ " " +"20px") ) + (";}.le-preview-container{padding:" + ("16px")  + ";height:" + ("100%")  + ";}.le-empty{text-align:" + ("center")  + ";background-color:" + ("rgba(" + "244"+ ","+ " " +"244"+ ","+ " " +"244"+ ","+ " " +"1" + ")")  + ";padding-top:" + ("75px")  + ";padding-bottom:" + ("100px")  + ";padding-left:" + ("100px")  + ";padding-right:" + ("100px")  + ";height:" + ("100%")  + ";}.le-empty-border{border:" + ("1px"+ " " +"solid"+ " " +"rgba(" + "244"+ ","+ " " +"244"+ ","+ " " +"244"+ ","+ " " +"1" + ")")  + ";}.le-empty-preview-drop{border:") + (("1px"+ " " +"solid"+ " " +"#b1caf2")  + " !important;}.le-empty-inner-preview-drop{opacity:" + ("0.1")  + ";}.le-empty-icon{font-size:" + ("57.6px")  + ";color:" + ("#9c9c9c")  + ";line-height:" + ("57.6px")  + ";}.le-kebab{width:" + ("10px")  + ";float:" + ("right")  + ";z-index:" + ("10")  + ";}.le-kebab-button{color:" + ("#878787")  + " !important;}.le-widget{cursor:" + ("default")  + ";}.le-full-height{height:" + ("100%") ) + (";}"));
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
      case 'getContents': return this.@org.jboss.errai.ioc.client.Type_factory__o_u_e_l_e_c_LayoutEditorView__quals__j_e_i_Any_j_e_i_Default.o_u_e_l_e_c_LayoutEditorViewTemplateResource::getContents()();
      case 'getStyle': return this.@org.jboss.errai.ioc.client.Type_factory__o_u_e_l_e_c_LayoutEditorView__quals__j_e_i_Any_j_e_i_Default.o_u_e_l_e_c_LayoutEditorViewTemplateResource::getStyle()();
    }
    return null;
  }-*/;
}

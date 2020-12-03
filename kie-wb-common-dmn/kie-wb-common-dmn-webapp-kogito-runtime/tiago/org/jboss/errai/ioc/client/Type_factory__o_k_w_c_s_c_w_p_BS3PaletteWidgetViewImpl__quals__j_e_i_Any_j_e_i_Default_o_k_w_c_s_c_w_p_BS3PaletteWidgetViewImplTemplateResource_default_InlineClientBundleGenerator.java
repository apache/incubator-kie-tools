package org.jboss.errai.ioc.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ResourcePrototype;

public class Type_factory__o_k_w_c_s_c_w_p_BS3PaletteWidgetViewImpl__quals__j_e_i_Any_j_e_i_Default_o_k_w_c_s_c_w_p_BS3PaletteWidgetViewImplTemplateResource_default_InlineClientBundleGenerator implements org.jboss.errai.ioc.client.Type_factory__o_k_w_c_s_c_w_p_BS3PaletteWidgetViewImpl__quals__j_e_i_Any_j_e_i_Default.o_k_w_c_s_c_w_p_BS3PaletteWidgetViewImplTemplateResource {
  private static Type_factory__o_k_w_c_s_c_w_p_BS3PaletteWidgetViewImpl__quals__j_e_i_Any_j_e_i_Default_o_k_w_c_s_c_w_p_BS3PaletteWidgetViewImplTemplateResource_default_InlineClientBundleGenerator _instance0 = new Type_factory__o_k_w_c_s_c_w_p_BS3PaletteWidgetViewImpl__quals__j_e_i_Any_j_e_i_Default_o_k_w_c_s_c_w_p_BS3PaletteWidgetViewImplTemplateResource_default_InlineClientBundleGenerator();
  private void getContentsInitializer() {
    getContents = new com.google.gwt.resources.client.TextResource() {
      // jar:file:/Users/tiagobento/.m2/repository/org/kie/workbench/stunner/kie-wb-common-stunner-widgets/7.47.0-SNAPSHOT/kie-wb-common-stunner-widgets-7.47.0-SNAPSHOT-sources.jar!/org/kie/workbench/common/stunner/client/widgets/palette/BS3PaletteWidgetViewImpl.html
      public String getText() {
        return "<div class=\"kie-palette\" data-field=\"kie-palette\">\n    <ul class=\"list-group\">\n    </ul>\n</div>";
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
        return com.google.gwt.i18n.client.LocaleInfo.getCurrentLocale().isRTL() ? ((".kie-palette{border-collapse:" + ("collapse")  + ";overflow:" + ("visible")  + ";position:" + ("absolute")  + ";right:" + ("0")  + ";top:" + ("0")  + ";width:" + ("44px")  + ";z-index:" + ("1030")  + ";}.kie-palette .list-group{position:" + ("relative")  + ";border-top:" + ("none")  + ";box-shadow:" + ("0"+ " " +"2px"+ " " +"6px"+ " " +"rgba(" + "3"+ ","+ " " +"3"+ ","+ " " +"3"+ ","+ " " +"0.2" + ")")  + ";}.kie-palette .list-group-item{border-width:") + (("0")  + ";display:" + ("inline-block")  + ";padding:" + ("0")  + ";position:" + ("static")  + ";}.kie-palette .list-group-item>button{color:" + ("#030303")  + ";cursor:" + ("pointer")  + ";font-size:" + ("12px")  + ";font-weight:" + ("400")  + ";height:" + ("44px")  + ";padding:" + ("0")  + ";text-decoration:" + ("none") ) + (";white-space:" + ("nowrap")  + ";width:" + ("44px")  + ";box-shadow:" + ("none")  + ";}.kie-palette .list-group-item>a{cursor:" + ("pointer")  + ";}.kie-palette .list-group-item.kie-palette-show-flyout>.kie-palette-flyout{display:" + ("block")  + ";}.kie-palette .list-group-item.kie-palette-show-flyout>button{background-color:" + ("#fff")  + ";background-image:" + ("none")  + ";}.kie-palette .list-group-item .list-group-item-value{display:" + ("block")  + ";line-height:" + ("2em")  + ";max-width:" + ("13em")  + ";overflow:") + (("hidden")  + ";text-overflow:" + ("ellipsis")  + ";}@supports (display: flex) {\n    .kie-palette .list-group-item .list-group-item-value {\n        flex: 1;\n        max-width: none;\n    }\n}.kie-palette-flyout{background-color:" + ("#fff")  + ";border-top:" + ("1px"+ " " +"solid"+ " " +"#bbb")  + ";border-left:" + ("1px"+ " " +"solid"+ " " +"#bbb")  + ";border-bottom:" + ("1px"+ " " +"solid"+ " " +"#bbb")  + ";box-shadow:" + ("0"+ " " +"2px"+ " " +"6px"+ " " +"rgba(" + "3"+ ","+ " " +"3"+ ","+ " " +"3"+ ","+ " " +"0.2" + ")")  + ";display:" + ("none")  + ";right:" + ("44px")  + ";max-height:" + ("100%")  + ";min-height:" + ("100%") ) + (";overflow-x:" + ("visible")  + ";overflow-y:" + ("auto")  + ";padding:" + ("15px"+ " " +"10px"+ " " +"15px"+ " " +"15px")  + ";position:" + ("absolute")  + ";top:" + ("0")  + ";}.kie-palette-flyout h5{color:" + ("#72767b")  + ";cursor:" + ("default")  + ";font-size:" + ("12px")  + ";margin-top:" + ("0")  + ";padding-right:" + ("10px")  + ";text-transform:") + (("uppercase")  + ";margin-left:" + ("25px")  + ";}.kie-palette-flyout .list-group{box-shadow:" + ("none")  + ";}.kie-palette-flyout .list-group-item{background-color:" + ("#fff")  + ";float:" + ("none")  + ";border-width:" + ("0")  + ";padding-right:" + ("10px")  + ";margin-bottom:" + ("0")  + ";overflow:" + ("hidden")  + ";text-overflow:" + ("ellipsis")  + ";}.kie-palette-flyout .list-group-item>a{color:" + ("#030303") ) + (";font-size:" + ("12px")  + ";font-weight:" + ("400")  + ";height:" + ("auto")  + ";padding:" + ("0")  + ";text-decoration:" + ("none")  + ";width:" + ("190px")  + ";white-space:" + ("nowrap")  + ";display:" + ("inline-block")  + ";}@supports (display: flex) {\n    .kie-palette-flyout .list-group-item > a {\n        display: flex;\n    }\n}.kie-palette-flyout .list-group-item>a img{margin-left:" + ("1rem")  + ";margin-top:" + ("auto")  + ";margin-bottom:") + (("auto")  + ";}.kie-palette-flyout .list-group-item:hover{background-color:" + ("#def3ff")  + ";}.kie-palette-flyout .list-group-item:hover>a{color:" + ("#030303")  + ";font-weight:" + ("400")  + ";}.kie-palette-icon{height:" + ("21px")  + ";}.kie-palette-flyout-icon{width:" + ("16px")  + ";height:" + ("16px")  + ";}.kie-palette-flyout__close{float:" + ("left")  + ";position:" + ("absolute")  + ";left:" + ("0")  + ";top:" + ("0") ) + (";}.kie-palette-flyout__btn-link--close{color:" + ("#030303")  + ";min-width:" + ("40px")  + ";min-height:" + ("40px")  + ";}")) : ((".kie-palette{border-collapse:" + ("collapse")  + ";overflow:" + ("visible")  + ";position:" + ("absolute")  + ";left:" + ("0")  + ";top:" + ("0")  + ";width:" + ("44px")  + ";z-index:" + ("1030")  + ";}.kie-palette .list-group{position:" + ("relative")  + ";border-top:" + ("none")  + ";box-shadow:" + ("0"+ " " +"2px"+ " " +"6px"+ " " +"rgba(" + "3"+ ","+ " " +"3"+ ","+ " " +"3"+ ","+ " " +"0.2" + ")")  + ";}.kie-palette .list-group-item{border-width:") + (("0")  + ";display:" + ("inline-block")  + ";padding:" + ("0")  + ";position:" + ("static")  + ";}.kie-palette .list-group-item>button{color:" + ("#030303")  + ";cursor:" + ("pointer")  + ";font-size:" + ("12px")  + ";font-weight:" + ("400")  + ";height:" + ("44px")  + ";padding:" + ("0")  + ";text-decoration:" + ("none") ) + (";white-space:" + ("nowrap")  + ";width:" + ("44px")  + ";box-shadow:" + ("none")  + ";}.kie-palette .list-group-item>a{cursor:" + ("pointer")  + ";}.kie-palette .list-group-item.kie-palette-show-flyout>.kie-palette-flyout{display:" + ("block")  + ";}.kie-palette .list-group-item.kie-palette-show-flyout>button{background-color:" + ("#fff")  + ";background-image:" + ("none")  + ";}.kie-palette .list-group-item .list-group-item-value{display:" + ("block")  + ";line-height:" + ("2em")  + ";max-width:" + ("13em")  + ";overflow:") + (("hidden")  + ";text-overflow:" + ("ellipsis")  + ";}@supports (display: flex) {\n    .kie-palette .list-group-item .list-group-item-value {\n        flex: 1;\n        max-width: none;\n    }\n}.kie-palette-flyout{background-color:" + ("#fff")  + ";border-top:" + ("1px"+ " " +"solid"+ " " +"#bbb")  + ";border-right:" + ("1px"+ " " +"solid"+ " " +"#bbb")  + ";border-bottom:" + ("1px"+ " " +"solid"+ " " +"#bbb")  + ";box-shadow:" + ("0"+ " " +"2px"+ " " +"6px"+ " " +"rgba(" + "3"+ ","+ " " +"3"+ ","+ " " +"3"+ ","+ " " +"0.2" + ")")  + ";display:" + ("none")  + ";left:" + ("44px")  + ";max-height:" + ("100%")  + ";min-height:" + ("100%") ) + (";overflow-x:" + ("visible")  + ";overflow-y:" + ("auto")  + ";padding:" + ("15px"+ " " +"15px"+ " " +"15px"+ " " +"10px")  + ";position:" + ("absolute")  + ";top:" + ("0")  + ";}.kie-palette-flyout h5{color:" + ("#72767b")  + ";cursor:" + ("default")  + ";font-size:" + ("12px")  + ";margin-top:" + ("0")  + ";padding-left:" + ("10px")  + ";text-transform:") + (("uppercase")  + ";margin-right:" + ("25px")  + ";}.kie-palette-flyout .list-group{box-shadow:" + ("none")  + ";}.kie-palette-flyout .list-group-item{background-color:" + ("#fff")  + ";float:" + ("none")  + ";border-width:" + ("0")  + ";padding-left:" + ("10px")  + ";margin-bottom:" + ("0")  + ";overflow:" + ("hidden")  + ";text-overflow:" + ("ellipsis")  + ";}.kie-palette-flyout .list-group-item>a{color:" + ("#030303") ) + (";font-size:" + ("12px")  + ";font-weight:" + ("400")  + ";height:" + ("auto")  + ";padding:" + ("0")  + ";text-decoration:" + ("none")  + ";width:" + ("190px")  + ";white-space:" + ("nowrap")  + ";display:" + ("inline-block")  + ";}@supports (display: flex) {\n    .kie-palette-flyout .list-group-item > a {\n        display: flex;\n    }\n}.kie-palette-flyout .list-group-item>a img{margin-right:" + ("1rem")  + ";margin-top:" + ("auto")  + ";margin-bottom:") + (("auto")  + ";}.kie-palette-flyout .list-group-item:hover{background-color:" + ("#def3ff")  + ";}.kie-palette-flyout .list-group-item:hover>a{color:" + ("#030303")  + ";font-weight:" + ("400")  + ";}.kie-palette-icon{height:" + ("21px")  + ";}.kie-palette-flyout-icon{width:" + ("16px")  + ";height:" + ("16px")  + ";}.kie-palette-flyout__close{float:" + ("right")  + ";position:" + ("absolute")  + ";right:" + ("0")  + ";top:" + ("0") ) + (";}.kie-palette-flyout__btn-link--close{color:" + ("#030303")  + ";min-width:" + ("40px")  + ";min-height:" + ("40px")  + ";}"));
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
      case 'getContents': return this.@org.jboss.errai.ioc.client.Type_factory__o_k_w_c_s_c_w_p_BS3PaletteWidgetViewImpl__quals__j_e_i_Any_j_e_i_Default.o_k_w_c_s_c_w_p_BS3PaletteWidgetViewImplTemplateResource::getContents()();
      case 'getStyle': return this.@org.jboss.errai.ioc.client.Type_factory__o_k_w_c_s_c_w_p_BS3PaletteWidgetViewImpl__quals__j_e_i_Any_j_e_i_Default.o_k_w_c_s_c_w_p_BS3PaletteWidgetViewImplTemplateResource::getStyle()();
    }
    return null;
  }-*/;
}

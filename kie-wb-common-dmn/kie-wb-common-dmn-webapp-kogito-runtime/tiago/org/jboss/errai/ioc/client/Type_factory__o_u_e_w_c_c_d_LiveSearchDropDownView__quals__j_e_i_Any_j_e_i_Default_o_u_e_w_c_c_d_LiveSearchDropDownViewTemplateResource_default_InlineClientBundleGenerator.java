package org.jboss.errai.ioc.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ResourcePrototype;

public class Type_factory__o_u_e_w_c_c_d_LiveSearchDropDownView__quals__j_e_i_Any_j_e_i_Default_o_u_e_w_c_c_d_LiveSearchDropDownViewTemplateResource_default_InlineClientBundleGenerator implements org.jboss.errai.ioc.client.Type_factory__o_u_e_w_c_c_d_LiveSearchDropDownView__quals__j_e_i_Any_j_e_i_Default.o_u_e_w_c_c_d_LiveSearchDropDownViewTemplateResource {
  private static Type_factory__o_u_e_w_c_c_d_LiveSearchDropDownView__quals__j_e_i_Any_j_e_i_Default_o_u_e_w_c_c_d_LiveSearchDropDownViewTemplateResource_default_InlineClientBundleGenerator _instance0 = new Type_factory__o_u_e_w_c_c_d_LiveSearchDropDownView__quals__j_e_i_Any_j_e_i_Default_o_u_e_w_c_c_d_LiveSearchDropDownViewTemplateResource_default_InlineClientBundleGenerator();
  private void getContentsInitializer() {
    getContents = new com.google.gwt.resources.client.TextResource() {
      // jar:file:/Users/tiagobento/.m2/repository/org/uberfire/uberfire-widgets-commons/7.47.0-SNAPSHOT/uberfire-widgets-commons-7.47.0-SNAPSHOT.jar!/org/uberfire/ext/widgets/common/client/dropdown/LiveSearchDropDownView.html
      public String getText() {
        return "<div data-field=\"mainPanel\" class=\"dropdown appformer-live-search-spinner-select btn-group\">\n    <button data-field=\"dropDownButton\" class=\"btn btn-default dropdown-toggle\" type=\"button\" data-toggle=\"dropdown\" aria-expanded=\"false\">\n            <span id=\"dropDownText\" class=\"filter-option pull-left appformer-live-search-button-text\"></span>&nbsp;<span class=\"bs-caret\"><span class=\"caret\"></span></span>\n    </button>\n    <div data-field=\"dropDownPanel\" class=\"dropdown-menu open\">\n        <div data-field=\"searchPanel\" class=\"bs-searchbox\">\n            <input id=\"searchInput\" type=\"text\" class=\"form-control\" autofocus autocomplete=\"off\">\n        </div>\n        <div data-field=\"spinnerPanel\" class=\"appformer-live-search-spinner-panel\">\n            <div class=\"spinner spinner-lg appformer-live-search-spinner\"></div>\n            <span id=\"spinnerText\" class=\"appformer-live-search-spinner-text\"></span>\n        </div>\n        <div data-field=\"noItems\"></div>\n        <ul data-field=\"dropDownMenu\" class=\"dropdown-menu inner appformer-live-search-dropdown-menu\" role=\"menu\">\n        </ul>\n        <div data-field=\"liveSearchFooter\"></div>\n    </div>\n</div>";
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
        return com.google.gwt.i18n.client.LocaleInfo.getCurrentLocale().isRTL() ? ((".appformer-live-search-button-text{white-space:" + ("nowrap")  + ";overflow:" + ("hidden")  + ";text-overflow:" + ("ellipsis")  + ";}.appformer-live-search-spinner-panel{display:" + ("none")  + ";margin-right:" + ("10px")  + ";height:" + ("30px")  + ";}.appformer-live-search-spinner{display:" + ("table-cell")  + ";}.appformer-live-search-spinner-text{display:" + ("table-cell")  + ";padding-right:" + ("10px")  + ";vertical-align:" + ("middle")  + ";}.appformer-live-search-dropdown-menu{max-height:") + (("250px")  + ";width:" + ("100%")  + ";overflow-y:" + ("auto")  + ";}.appformer-live-search-spinner-select{width:" + ("220px")  + ";vertical-align:" + ("middle")  + ";}.appformer-live-search-spinner-select>.dropdown-toggle{position:" + ("relative")  + ";width:" + ("100%")  + ";text-align:" + ("left")  + ";white-space:" + ("nowrap")  + ";display:" + ("-webkit-inline-box")  + ";display:" + ("-webkit-inline-flex") ) + (";display:" + ("-ms-inline-flexbox")  + ";display:" + ("inline-flex")  + ";-webkit-box-align:" + ("center")  + ";-webkit-align-items:" + ("center")  + ";-ms-flex-align:" + ("center")  + ";align-items:" + ("center")  + ";-webkit-box-pack:" + ("justify")  + ";-webkit-justify-content:" + ("space-between")  + ";-ms-flex-pack:" + ("justify")  + ";justify-content:" + ("space-between")  + ";}.appformer-live-search-spinner-select>.dropdown-toggle:after{margin-top:") + (("-1px")  + ";}.appformer-live-search-spinner-select>.dropdown-toggle.bs-placeholder,.appformer-live-search-spinner-select>.dropdown-toggle.bs-placeholder:hover,.appformer-live-search-spinner-select>.dropdown-toggle.bs-placeholder:focus,.appformer-live-search-spinner-select>.dropdown-toggle.bs-placeholder:active{color:" + ("#999")  + ";}.appformer-live-search-spinner-select>.dropdown-toggle.bs-placeholder.btn-default,.appformer-live-search-spinner-select>.dropdown-toggle.bs-placeholder.btn-default:hover,.appformer-live-search-spinner-select>.dropdown-toggle.bs-placeholder.btn-default:focus,.appformer-live-search-spinner-select>.dropdown-toggle.bs-placeholder.btn-default:active{color:" + ("rgba(" + "255"+ ","+ " " +"255"+ ","+ " " +"255"+ ","+ " " +"0.5" + ")")  + ";}.appformer-live-search-spinner-select .dropdown-toggle:focus{outline:" + ("thin"+ " " +"dotted"+ " " +"#333")  + " !important;outline:" + ("5px"+ " " +"auto"+ " " +"-webkit-focus-ring-color")  + " !important;outline-offset:" + ("-2px")  + ";}.appformer-live-search-spinner-select.form-control{margin-bottom:" + ("0")  + ";padding:" + ("0")  + ";border:" + ("none")  + ";height:" + ("auto")  + ";}.appformer-live-search-spinner-select .dropdown-toggle .caret{position:" + ("absolute") ) + (";top:" + ("50%")  + ";left:" + ("12px")  + ";margin-top:" + ("-2px")  + ";vertical-align:" + ("middle")  + ";}.appformer-live-search-spinner-select .dropdown-menu{min-width:" + ("100%")  + ";max-width:" + ("100%")  + ";-webkit-box-sizing:" + ("border-box")  + ";-moz-box-sizing:" + ("border-box")  + ";box-sizing:" + ("border-box")  + ";}.appformer-live-search-spinner-select .dropdown-menu>.inner:focus{outline:" + ("none")  + " !important;}.appformer-live-search-spinner-select .dropdown-menu.inner{position:") + (("static")  + ";float:" + ("none")  + ";border:" + ("0")  + ";padding:" + ("0")  + ";margin:" + ("0")  + ";border-radius:" + ("0")  + ";-webkit-box-shadow:" + ("none")  + ";box-shadow:" + ("none")  + ";}.appformer-live-search-spinner-select .dropdown-menu li{position:" + ("relative")  + ";}.appformer-live-search-spinner-select .dropdown-menu li.active small{color:" + ("rgba(" + "255"+ ","+ " " +"255"+ ","+ " " +"255"+ ","+ " " +"0.5" + ")")  + " !important;}.appformer-live-search-spinner-select .dropdown-menu li.disabled a{cursor:" + ("not-allowed") ) + (";}.appformer-live-search-spinner-select .dropdown-menu li a{cursor:" + ("pointer")  + ";-webkit-user-select:" + ("none")  + ";-moz-user-select:" + ("none")  + ";-ms-user-select:" + ("none")  + ";user-select:" + ("none")  + ";}.appformer-live-search-spinner-select .dropdown-menu li a.opt{position:" + ("relative")  + ";padding-right:" + ("2.25em")  + ";}.appformer-live-search-spinner-select .dropdown-menu li a span.check-mark{display:" + ("none")  + ";}.appformer-live-search-spinner-select .dropdown-menu li a span.text{display:" + ("inline-block")  + ";}.appformer-live-search-spinner-select .dropdown-menu li small{padding-right:" + ("0.5em")  + ";}.bs-searchbox{padding:") + (("4px"+ " " +"8px")  + ";}.bs-searchbox .form-control{margin-bottom:" + ("0")  + ";width:" + ("100%")  + ";float:" + ("none")  + ";}")) : ((".appformer-live-search-button-text{white-space:" + ("nowrap")  + ";overflow:" + ("hidden")  + ";text-overflow:" + ("ellipsis")  + ";}.appformer-live-search-spinner-panel{display:" + ("none")  + ";margin-left:" + ("10px")  + ";height:" + ("30px")  + ";}.appformer-live-search-spinner{display:" + ("table-cell")  + ";}.appformer-live-search-spinner-text{display:" + ("table-cell")  + ";padding-left:" + ("10px")  + ";vertical-align:" + ("middle")  + ";}.appformer-live-search-dropdown-menu{max-height:") + (("250px")  + ";width:" + ("100%")  + ";overflow-y:" + ("auto")  + ";}.appformer-live-search-spinner-select{width:" + ("220px")  + ";vertical-align:" + ("middle")  + ";}.appformer-live-search-spinner-select>.dropdown-toggle{position:" + ("relative")  + ";width:" + ("100%")  + ";text-align:" + ("right")  + ";white-space:" + ("nowrap")  + ";display:" + ("-webkit-inline-box")  + ";display:" + ("-webkit-inline-flex") ) + (";display:" + ("-ms-inline-flexbox")  + ";display:" + ("inline-flex")  + ";-webkit-box-align:" + ("center")  + ";-webkit-align-items:" + ("center")  + ";-ms-flex-align:" + ("center")  + ";align-items:" + ("center")  + ";-webkit-box-pack:" + ("justify")  + ";-webkit-justify-content:" + ("space-between")  + ";-ms-flex-pack:" + ("justify")  + ";justify-content:" + ("space-between")  + ";}.appformer-live-search-spinner-select>.dropdown-toggle:after{margin-top:") + (("-1px")  + ";}.appformer-live-search-spinner-select>.dropdown-toggle.bs-placeholder,.appformer-live-search-spinner-select>.dropdown-toggle.bs-placeholder:hover,.appformer-live-search-spinner-select>.dropdown-toggle.bs-placeholder:focus,.appformer-live-search-spinner-select>.dropdown-toggle.bs-placeholder:active{color:" + ("#999")  + ";}.appformer-live-search-spinner-select>.dropdown-toggle.bs-placeholder.btn-default,.appformer-live-search-spinner-select>.dropdown-toggle.bs-placeholder.btn-default:hover,.appformer-live-search-spinner-select>.dropdown-toggle.bs-placeholder.btn-default:focus,.appformer-live-search-spinner-select>.dropdown-toggle.bs-placeholder.btn-default:active{color:" + ("rgba(" + "255"+ ","+ " " +"255"+ ","+ " " +"255"+ ","+ " " +"0.5" + ")")  + ";}.appformer-live-search-spinner-select .dropdown-toggle:focus{outline:" + ("thin"+ " " +"dotted"+ " " +"#333")  + " !important;outline:" + ("5px"+ " " +"auto"+ " " +"-webkit-focus-ring-color")  + " !important;outline-offset:" + ("-2px")  + ";}.appformer-live-search-spinner-select.form-control{margin-bottom:" + ("0")  + ";padding:" + ("0")  + ";border:" + ("none")  + ";height:" + ("auto")  + ";}.appformer-live-search-spinner-select .dropdown-toggle .caret{position:" + ("absolute") ) + (";top:" + ("50%")  + ";right:" + ("12px")  + ";margin-top:" + ("-2px")  + ";vertical-align:" + ("middle")  + ";}.appformer-live-search-spinner-select .dropdown-menu{min-width:" + ("100%")  + ";max-width:" + ("100%")  + ";-webkit-box-sizing:" + ("border-box")  + ";-moz-box-sizing:" + ("border-box")  + ";box-sizing:" + ("border-box")  + ";}.appformer-live-search-spinner-select .dropdown-menu>.inner:focus{outline:" + ("none")  + " !important;}.appformer-live-search-spinner-select .dropdown-menu.inner{position:") + (("static")  + ";float:" + ("none")  + ";border:" + ("0")  + ";padding:" + ("0")  + ";margin:" + ("0")  + ";border-radius:" + ("0")  + ";-webkit-box-shadow:" + ("none")  + ";box-shadow:" + ("none")  + ";}.appformer-live-search-spinner-select .dropdown-menu li{position:" + ("relative")  + ";}.appformer-live-search-spinner-select .dropdown-menu li.active small{color:" + ("rgba(" + "255"+ ","+ " " +"255"+ ","+ " " +"255"+ ","+ " " +"0.5" + ")")  + " !important;}.appformer-live-search-spinner-select .dropdown-menu li.disabled a{cursor:" + ("not-allowed") ) + (";}.appformer-live-search-spinner-select .dropdown-menu li a{cursor:" + ("pointer")  + ";-webkit-user-select:" + ("none")  + ";-moz-user-select:" + ("none")  + ";-ms-user-select:" + ("none")  + ";user-select:" + ("none")  + ";}.appformer-live-search-spinner-select .dropdown-menu li a.opt{position:" + ("relative")  + ";padding-left:" + ("2.25em")  + ";}.appformer-live-search-spinner-select .dropdown-menu li a span.check-mark{display:" + ("none")  + ";}.appformer-live-search-spinner-select .dropdown-menu li a span.text{display:" + ("inline-block")  + ";}.appformer-live-search-spinner-select .dropdown-menu li small{padding-left:" + ("0.5em")  + ";}.bs-searchbox{padding:") + (("4px"+ " " +"8px")  + ";}.bs-searchbox .form-control{margin-bottom:" + ("0")  + ";width:" + ("100%")  + ";float:" + ("none")  + ";}"));
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
      case 'getContents': return this.@org.jboss.errai.ioc.client.Type_factory__o_u_e_w_c_c_d_LiveSearchDropDownView__quals__j_e_i_Any_j_e_i_Default.o_u_e_w_c_c_d_LiveSearchDropDownViewTemplateResource::getContents()();
      case 'getStyle': return this.@org.jboss.errai.ioc.client.Type_factory__o_u_e_w_c_c_d_LiveSearchDropDownView__quals__j_e_i_Any_j_e_i_Default.o_u_e_w_c_c_d_LiveSearchDropDownViewTemplateResource::getStyle()();
    }
    return null;
  }-*/;
}

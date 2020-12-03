package org.jboss.errai.ioc.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ResourcePrototype;

public class Type_factory__o_u_e_c_e_g_ExperimentalFeaturesGroupViewImpl__quals__j_e_i_Any_j_e_i_Default_o_u_e_c_e_g_ExperimentalFeaturesGroupViewImplTemplateResource_default_InlineClientBundleGenerator implements org.jboss.errai.ioc.client.Type_factory__o_u_e_c_e_g_ExperimentalFeaturesGroupViewImpl__quals__j_e_i_Any_j_e_i_Default.o_u_e_c_e_g_ExperimentalFeaturesGroupViewImplTemplateResource {
  private static Type_factory__o_u_e_c_e_g_ExperimentalFeaturesGroupViewImpl__quals__j_e_i_Any_j_e_i_Default_o_u_e_c_e_g_ExperimentalFeaturesGroupViewImplTemplateResource_default_InlineClientBundleGenerator _instance0 = new Type_factory__o_u_e_c_e_g_ExperimentalFeaturesGroupViewImpl__quals__j_e_i_Any_j_e_i_Default_o_u_e_c_e_g_ExperimentalFeaturesGroupViewImplTemplateResource_default_InlineClientBundleGenerator();
  private void getContentsInitializer() {
    getContents = new com.google.gwt.resources.client.TextResource() {
      // jar:file:/Users/tiagobento/.m2/repository/org/uberfire/uberfire-experimental-client/7.47.0-SNAPSHOT/uberfire-experimental-client-7.47.0-SNAPSHOT-sources.jar!/org/uberfire/experimental/client/editor/group/ExperimentalFeaturesGroupViewImpl.html
      public String getText() {
        return "<div class=\"panel-group appformer_experimental_features_group_collapse_panel_group\">\n    <div class=\"panel appformer_experimental_features_group_collapse_panel\">\n        <div class=\"panel-heading appformer_experimental_features_group_collapse_panel_heading\">\n            <div class=\"panel-title\">\n                <div class=\"row\">\n                    <div class=\"col-md-6\">\n                        <div class=\"appformer_experimental_features_group_anchor_container\">\n                            <span class=\"appformer_experimental_features_group_caret_margin\"><span data-field=\"caret\" class=\"fa fa-caret-right\"></span></span><label data-field=\"header\" data-toggle=\"collapse\" aria-expanded=\"false\" class=\"collapsed appformer_experimental_features_group_anchor\"></label>\n                        </div>\n                    </div>\n                    <div class=\"col-md-6\">\n                        <div class=\"appformer_experimental_features_group_anchor_container text-right\">\n                            <a data-field=\"enableAll\" class=\"appformer_experimental_features_group_anchor\"></a>\n                        </div>\n                    </div>\n                </div>\n            </div>\n        </div>\n        <div data-field=\"panel\" class=\"panel-collapse collapse\" aria-expanded=\"false\">\n            <div class=\"panel-body appformer_experimental_features_group_collapse_panel_body\">\n                <ul class=\"list-group appformer_experimental_features_group_list_group\" data-field=\"featuresContainer\">\n                </ul>\n            </div>\n        </div>\n    </div>\n</div>";
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
        return com.google.gwt.i18n.client.LocaleInfo.getCurrentLocale().isRTL() ? ((".appformer_experimental_features_group_collapse_panel_group{margin-bottom:" + ("0")  + ";}.appformer_experimental_features_group_collapse_panel_heading{padding:" + ("0")  + ";margin-bottom:" + ("15px")  + ";background-image:" + ("none")  + " !important;}.appformer_experimental_features_group_collapse_panel{background-color:" + ("transparent")  + ";box-shadow:" + ("none")  + ";margin-bottom:" + ("0")  + ";padding-top:" + ("0")  + ";}.appformer_experimental_features_group_collapse_panel_body{border:" + ("none")  + " !important;padding:" + ("0")  + " !important;}.appformer_experimental_features_group_caret_margin{margin-left:") + (("5px")  + ";}.appformer_experimental_features_group_anchor_container{margin-top:" + ("15px")  + ";padding-left:" + ("20px")  + ";}.appformer_experimental_features_group_anchor{cursor:" + ("pointer")  + " !important;}.appformer_experimental_features_group_list_group{margin-bottom:" + ("0")  + ";}")) : ((".appformer_experimental_features_group_collapse_panel_group{margin-bottom:" + ("0")  + ";}.appformer_experimental_features_group_collapse_panel_heading{padding:" + ("0")  + ";margin-bottom:" + ("15px")  + ";background-image:" + ("none")  + " !important;}.appformer_experimental_features_group_collapse_panel{background-color:" + ("transparent")  + ";box-shadow:" + ("none")  + ";margin-bottom:" + ("0")  + ";padding-top:" + ("0")  + ";}.appformer_experimental_features_group_collapse_panel_body{border:" + ("none")  + " !important;padding:" + ("0")  + " !important;}.appformer_experimental_features_group_caret_margin{margin-right:") + (("5px")  + ";}.appformer_experimental_features_group_anchor_container{margin-top:" + ("15px")  + ";padding-right:" + ("20px")  + ";}.appformer_experimental_features_group_anchor{cursor:" + ("pointer")  + " !important;}.appformer_experimental_features_group_list_group{margin-bottom:" + ("0")  + ";}"));
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
      case 'getContents': return this.@org.jboss.errai.ioc.client.Type_factory__o_u_e_c_e_g_ExperimentalFeaturesGroupViewImpl__quals__j_e_i_Any_j_e_i_Default.o_u_e_c_e_g_ExperimentalFeaturesGroupViewImplTemplateResource::getContents()();
      case 'getStyle': return this.@org.jboss.errai.ioc.client.Type_factory__o_u_e_c_e_g_ExperimentalFeaturesGroupViewImpl__quals__j_e_i_Any_j_e_i_Default.o_u_e_c_e_g_ExperimentalFeaturesGroupViewImplTemplateResource::getStyle()();
    }
    return null;
  }-*/;
}

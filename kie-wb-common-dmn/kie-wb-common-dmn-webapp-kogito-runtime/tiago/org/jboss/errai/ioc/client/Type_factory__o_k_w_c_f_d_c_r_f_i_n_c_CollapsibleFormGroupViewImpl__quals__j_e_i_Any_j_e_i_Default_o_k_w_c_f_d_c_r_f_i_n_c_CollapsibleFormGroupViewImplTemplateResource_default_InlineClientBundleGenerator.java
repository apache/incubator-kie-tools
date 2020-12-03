package org.jboss.errai.ioc.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ResourcePrototype;

public class Type_factory__o_k_w_c_f_d_c_r_f_i_n_c_CollapsibleFormGroupViewImpl__quals__j_e_i_Any_j_e_i_Default_o_k_w_c_f_d_c_r_f_i_n_c_CollapsibleFormGroupViewImplTemplateResource_default_InlineClientBundleGenerator implements org.jboss.errai.ioc.client.Type_factory__o_k_w_c_f_d_c_r_f_i_n_c_CollapsibleFormGroupViewImpl__quals__j_e_i_Any_j_e_i_Default.o_k_w_c_f_d_c_r_f_i_n_c_CollapsibleFormGroupViewImplTemplateResource {
  private static Type_factory__o_k_w_c_f_d_c_r_f_i_n_c_CollapsibleFormGroupViewImpl__quals__j_e_i_Any_j_e_i_Default_o_k_w_c_f_d_c_r_f_i_n_c_CollapsibleFormGroupViewImplTemplateResource_default_InlineClientBundleGenerator _instance0 = new Type_factory__o_k_w_c_f_d_c_r_f_i_n_c_CollapsibleFormGroupViewImpl__quals__j_e_i_Any_j_e_i_Default_o_k_w_c_f_d_c_r_f_i_n_c_CollapsibleFormGroupViewImplTemplateResource_default_InlineClientBundleGenerator();
  private void getContentsInitializer() {
    getContents = new com.google.gwt.resources.client.TextResource() {
      // jar:file:/Users/tiagobento/.m2/repository/org/kie/workbench/forms/kie-wb-common-dynamic-forms-client/7.47.0-SNAPSHOT/kie-wb-common-dynamic-forms-client-7.47.0-SNAPSHOT-sources.jar!/org/kie/workbench/common/forms/dynamic/client/rendering/formGroups/impl/nestedForm/collapse/CollapsibleFormGroupViewImpl.html
      public String getText() {
        return "<div class=\"panel-group kie-wb-common-forms-groups-collapse-panel-group\">\n    <div class=\"panel kie-wb-common-forms-groups-collapse-panel\">\n        <div class=\"panel-heading kie-wb-common-forms-groups-collapse-panel-heading\">\n            <div class=\"panel-title\">\n                <a data-field=\"anchor\" data-toggle=\"collapse\" aria-expanded=\"false\" class=\"collapsed\">\n                    <span data-field=\"anchorText\" class=\"kie-wb-common-forms-groups-collapse-panel-anchor\"></span>\n                </a>\n            </div>\n        </div>\n        <div data-field=\"panel\" class=\"panel-collapse collapse\" aria-expanded=\"false\">\n            <div class=\"panel-body kie-wb-common-forms-groups-collapse-panel-body\">\n                <div data-field=\"container\"></div>\n                <div>\n                    <div data-field=\"formGroup\" class=\"form-group\">\n                        <div data-field=\"helpBlock\" class=\"help-block\"></div>\n                    </div>\n                </div>\n            </div>\n        </div>\n    </div>\n</div>";
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
        return (".kie-wb-common-forms-groups-collapse-panel-group{margin-bottom:" + ("0")  + ";}.kie-wb-common-forms-groups-collapse-panel{background-color:" + ("transparent")  + ";border-bottom:" + ("1px"+ " " +"solid"+ " " +"#d1d1d1")  + ";box-shadow:" + ("none")  + ";margin-bottom:" + ("0")  + ";padding-top:" + ("0")  + ";}.kie-wb-common-forms-groups-collapse-panel-anchor{cursor:" + ("pointer")  + ";}.kie-wb-common-forms-groups-collapse-panel-heading{padding:" + ("0")  + ";margin-top:" + ("15px")  + ";margin-bottom:" + ("15px")  + ";background-image:") + (("none")  + " !important;}.kie-wb-common-forms-groups-collapse-panel-body{border:" + ("none")  + " !important;}");
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
      case 'getContents': return this.@org.jboss.errai.ioc.client.Type_factory__o_k_w_c_f_d_c_r_f_i_n_c_CollapsibleFormGroupViewImpl__quals__j_e_i_Any_j_e_i_Default.o_k_w_c_f_d_c_r_f_i_n_c_CollapsibleFormGroupViewImplTemplateResource::getContents()();
      case 'getStyle': return this.@org.jboss.errai.ioc.client.Type_factory__o_k_w_c_f_d_c_r_f_i_n_c_CollapsibleFormGroupViewImpl__quals__j_e_i_Any_j_e_i_Default.o_k_w_c_f_d_c_r_f_i_n_c_CollapsibleFormGroupViewImplTemplateResource::getStyle()();
    }
    return null;
  }-*/;
}

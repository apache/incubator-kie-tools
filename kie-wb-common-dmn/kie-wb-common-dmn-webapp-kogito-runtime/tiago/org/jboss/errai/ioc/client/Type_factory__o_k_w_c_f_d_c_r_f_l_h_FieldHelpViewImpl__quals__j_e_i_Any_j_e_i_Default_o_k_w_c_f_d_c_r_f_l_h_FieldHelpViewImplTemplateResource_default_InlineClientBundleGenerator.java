package org.jboss.errai.ioc.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ResourcePrototype;

public class Type_factory__o_k_w_c_f_d_c_r_f_l_h_FieldHelpViewImpl__quals__j_e_i_Any_j_e_i_Default_o_k_w_c_f_d_c_r_f_l_h_FieldHelpViewImplTemplateResource_default_InlineClientBundleGenerator implements org.jboss.errai.ioc.client.Type_factory__o_k_w_c_f_d_c_r_f_l_h_FieldHelpViewImpl__quals__j_e_i_Any_j_e_i_Default.o_k_w_c_f_d_c_r_f_l_h_FieldHelpViewImplTemplateResource {
  private static Type_factory__o_k_w_c_f_d_c_r_f_l_h_FieldHelpViewImpl__quals__j_e_i_Any_j_e_i_Default_o_k_w_c_f_d_c_r_f_l_h_FieldHelpViewImplTemplateResource_default_InlineClientBundleGenerator _instance0 = new Type_factory__o_k_w_c_f_d_c_r_f_l_h_FieldHelpViewImpl__quals__j_e_i_Any_j_e_i_Default_o_k_w_c_f_d_c_r_f_l_h_FieldHelpViewImplTemplateResource_default_InlineClientBundleGenerator();
  private void getContentsInitializer() {
    getContents = new com.google.gwt.resources.client.TextResource() {
      // jar:file:/Users/tiagobento/redhat/kiegroup-all/kie-wb-common/kie-wb-common-forms/kie-wb-common-dynamic-forms/kie-wb-common-dynamic-forms-client/target/kie-wb-common-dynamic-forms-client-7.47.0-SNAPSHOT-sources.jar!/org/kie/workbench/common/forms/dynamic/client/rendering/formGroups/labels/help/FieldHelpViewImpl.html
      public String getText() {
        return "<span class=\"kie-wb-common-forms-field-help\">\n    <a data-field=\"helpMessage\" role=\"button\" data-toggle=\"popover\" data-trigger=\"hover\"\n       data-html=\"true\" title=\"\" data-placement=\"bottom\" data-container=\"body\">\n        <i class=\"fa fa-info-circle\"></i>\n    </a>\n</span>";
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
        return (".kie-wb-common-forms-field-help{font-size:" + ("small")  + " !important;}");
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
      case 'getContents': return this.@org.jboss.errai.ioc.client.Type_factory__o_k_w_c_f_d_c_r_f_l_h_FieldHelpViewImpl__quals__j_e_i_Any_j_e_i_Default.o_k_w_c_f_d_c_r_f_l_h_FieldHelpViewImplTemplateResource::getContents()();
      case 'getStyle': return this.@org.jboss.errai.ioc.client.Type_factory__o_k_w_c_f_d_c_r_f_l_h_FieldHelpViewImpl__quals__j_e_i_Any_j_e_i_Default.o_k_w_c_f_d_c_r_f_l_h_FieldHelpViewImplTemplateResource::getStyle()();
    }
    return null;
  }-*/;
}

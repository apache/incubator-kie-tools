package org.jboss.errai.ioc.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ResourcePrototype;

public class Type_factory__o_k_w_c_f_d_c_r_r_l_c_i_w_i_DateTimePickerPresenterViewImpl__quals__j_e_i_Any_j_e_i_Default_o_k_w_c_f_d_c_r_r_l_c_i_w_i_DateTimePickerPresenterViewImplTemplateResource_default_InlineClientBundleGenerator implements org.jboss.errai.ioc.client.Type_factory__o_k_w_c_f_d_c_r_r_l_c_i_w_i_DateTimePickerPresenterViewImpl__quals__j_e_i_Any_j_e_i_Default.o_k_w_c_f_d_c_r_r_l_c_i_w_i_DateTimePickerPresenterViewImplTemplateResource {
  private static Type_factory__o_k_w_c_f_d_c_r_r_l_c_i_w_i_DateTimePickerPresenterViewImpl__quals__j_e_i_Any_j_e_i_Default_o_k_w_c_f_d_c_r_r_l_c_i_w_i_DateTimePickerPresenterViewImplTemplateResource_default_InlineClientBundleGenerator _instance0 = new Type_factory__o_k_w_c_f_d_c_r_r_l_c_i_w_i_DateTimePickerPresenterViewImpl__quals__j_e_i_Any_j_e_i_Default_o_k_w_c_f_d_c_r_r_l_c_i_w_i_DateTimePickerPresenterViewImplTemplateResource_default_InlineClientBundleGenerator();
  private void getContentsInitializer() {
    getContents = new com.google.gwt.resources.client.TextResource() {
      // jar:file:/Users/tiagobento/.m2/repository/org/kie/workbench/forms/kie-wb-common-dynamic-forms-client/7.47.0-SNAPSHOT/kie-wb-common-dynamic-forms-client-7.47.0-SNAPSHOT-sources.jar!/org/kie/workbench/common/forms/dynamic/client/rendering/renderers/lov/creator/input/widget/impl/DateTimePickerPresenterViewImpl.html
      public String getText() {
        return "<div class=\"kie-wb-common-forms-lov-creator-datetimepickercell\" data-field=\"container\">\n    <input type=\"text\" data-field=\"dateTimePicker\" class=\"form-control kie-wb-common-forms-lov-creator-datetimepickercell\">\n</div>";
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
        return (".kie-wb-common-forms-lov-creator-datetimepickercell{width:" + ("0")  + ";height:" + ("0")  + ";border:" + ("0")  + ";padding:" + ("0")  + ";margin:" + ("0")  + ";}");
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
      case 'getContents': return this.@org.jboss.errai.ioc.client.Type_factory__o_k_w_c_f_d_c_r_r_l_c_i_w_i_DateTimePickerPresenterViewImpl__quals__j_e_i_Any_j_e_i_Default.o_k_w_c_f_d_c_r_r_l_c_i_w_i_DateTimePickerPresenterViewImplTemplateResource::getContents()();
      case 'getStyle': return this.@org.jboss.errai.ioc.client.Type_factory__o_k_w_c_f_d_c_r_r_l_c_i_w_i_DateTimePickerPresenterViewImpl__quals__j_e_i_Any_j_e_i_Default.o_k_w_c_f_d_c_r_r_l_c_i_w_i_DateTimePickerPresenterViewImplTemplateResource::getStyle()();
    }
    return null;
  }-*/;
}

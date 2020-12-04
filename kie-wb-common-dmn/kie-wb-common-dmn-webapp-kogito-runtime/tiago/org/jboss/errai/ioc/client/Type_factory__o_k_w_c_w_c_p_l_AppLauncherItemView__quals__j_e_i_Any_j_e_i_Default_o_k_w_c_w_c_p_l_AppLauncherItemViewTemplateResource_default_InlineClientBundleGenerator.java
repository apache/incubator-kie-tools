package org.jboss.errai.ioc.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ResourcePrototype;

public class Type_factory__o_k_w_c_w_c_p_l_AppLauncherItemView__quals__j_e_i_Any_j_e_i_Default_o_k_w_c_w_c_p_l_AppLauncherItemViewTemplateResource_default_InlineClientBundleGenerator implements org.jboss.errai.ioc.client.Type_factory__o_k_w_c_w_c_p_l_AppLauncherItemView__quals__j_e_i_Any_j_e_i_Default.o_k_w_c_w_c_p_l_AppLauncherItemViewTemplateResource {
  private static Type_factory__o_k_w_c_w_c_p_l_AppLauncherItemView__quals__j_e_i_Any_j_e_i_Default_o_k_w_c_w_c_p_l_AppLauncherItemViewTemplateResource_default_InlineClientBundleGenerator _instance0 = new Type_factory__o_k_w_c_w_c_p_l_AppLauncherItemView__quals__j_e_i_Any_j_e_i_Default_o_k_w_c_w_c_p_l_AppLauncherItemViewTemplateResource_default_InlineClientBundleGenerator();
  private void getContentsInitializer() {
    getContents = new com.google.gwt.resources.client.TextResource() {
      // jar:file:/Users/tiagobento/redhat/kiegroup-all/kie-wb-common/kie-wb-common-widgets/kie-wb-common-ui/target/kie-wb-common-ui-7.47.0-SNAPSHOT-sources.jar!/org/kie/workbench/common/widgets/client/popups/launcher/AppLauncherItemView.html
      public String getText() {
        return "<!DOCTYPE html>\n<a class=\"btn btn-link btn-lg text-center kie-app-launcher\" target=\"_blank\">\n    <span class=\"fa fa-3x\"></span><br>\n    <span class=\"kie-app-launcher-text\" data-field=\"text\"></span>\n</a>";
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
        return (".kie-app-launcher span.fa{padding-bottom:" + ("5px")  + ";}.kie-launcher a.kie-app-launcher:hover{color:" + ("#0099d3")  + ";text-decoration:" + ("none")  + ";}.kie-app-launcher-text{font-size:" + ("12px")  + ";white-space:" + ("normal")  + ";}");
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
      case 'getContents': return this.@org.jboss.errai.ioc.client.Type_factory__o_k_w_c_w_c_p_l_AppLauncherItemView__quals__j_e_i_Any_j_e_i_Default.o_k_w_c_w_c_p_l_AppLauncherItemViewTemplateResource::getContents()();
      case 'getStyle': return this.@org.jboss.errai.ioc.client.Type_factory__o_k_w_c_w_c_p_l_AppLauncherItemView__quals__j_e_i_Any_j_e_i_Default.o_k_w_c_w_c_p_l_AppLauncherItemViewTemplateResource::getStyle()();
    }
    return null;
  }-*/;
}

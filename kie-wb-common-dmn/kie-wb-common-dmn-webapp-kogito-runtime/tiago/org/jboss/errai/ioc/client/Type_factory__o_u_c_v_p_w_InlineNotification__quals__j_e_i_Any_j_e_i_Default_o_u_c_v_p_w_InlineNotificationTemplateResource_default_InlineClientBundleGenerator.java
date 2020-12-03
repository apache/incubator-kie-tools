package org.jboss.errai.ioc.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ResourcePrototype;

public class Type_factory__o_u_c_v_p_w_InlineNotification__quals__j_e_i_Any_j_e_i_Default_o_u_c_v_p_w_InlineNotificationTemplateResource_default_InlineClientBundleGenerator implements org.jboss.errai.ioc.client.Type_factory__o_u_c_v_p_w_InlineNotification__quals__j_e_i_Any_j_e_i_Default.o_u_c_v_p_w_InlineNotificationTemplateResource {
  private static Type_factory__o_u_c_v_p_w_InlineNotification__quals__j_e_i_Any_j_e_i_Default_o_u_c_v_p_w_InlineNotificationTemplateResource_default_InlineClientBundleGenerator _instance0 = new Type_factory__o_u_c_v_p_w_InlineNotification__quals__j_e_i_Any_j_e_i_Default_o_u_c_v_p_w_InlineNotificationTemplateResource_default_InlineClientBundleGenerator();
  private void getContentsInitializer() {
    getContents = new com.google.gwt.resources.client.TextResource() {
      // jar:file:/Users/tiagobento/.m2/repository/org/uberfire/uberfire-workbench-client-views-patternfly/7.47.0-SNAPSHOT/uberfire-workbench-client-views-patternfly-7.47.0-SNAPSHOT-sources.jar!/org/uberfire/client/views/pfly/widgets/InlineNotification.html
      public String getText() {
        return "<div class=\"alert\">\n    <button data-field=\"dismiss\" type=\"button\" class=\"close hidden\" data-dismiss=\"alert\" aria-hidden=\"true\">\n        <span class=\"pficon pficon-close\"></span>\n    </button>\n    <span data-field=\"icon\" class=\"pficon\"></span>\n    <span data-field=\"message\" style=\"word-wrap:break-word;\"></span>\n</div>";
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
        return (".list-unstyled{margin-bottom:" + ("0")  + ";}");
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
      case 'getContents': return this.@org.jboss.errai.ioc.client.Type_factory__o_u_c_v_p_w_InlineNotification__quals__j_e_i_Any_j_e_i_Default.o_u_c_v_p_w_InlineNotificationTemplateResource::getContents()();
      case 'getStyle': return this.@org.jboss.errai.ioc.client.Type_factory__o_u_c_v_p_w_InlineNotification__quals__j_e_i_Any_j_e_i_Default.o_u_c_v_p_w_InlineNotificationTemplateResource::getStyle()();
    }
    return null;
  }-*/;
}

package org.jboss.errai.ioc.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ResourcePrototype;

public class Type_factory__o_u_c_v_p_w_KebabMenuItem__quals__j_e_i_Any_j_e_i_Default_o_u_c_v_p_w_KebabMenuItemTemplateResource_default_InlineClientBundleGenerator implements org.jboss.errai.ioc.client.Type_factory__o_u_c_v_p_w_KebabMenuItem__quals__j_e_i_Any_j_e_i_Default.o_u_c_v_p_w_KebabMenuItemTemplateResource {
  private static Type_factory__o_u_c_v_p_w_KebabMenuItem__quals__j_e_i_Any_j_e_i_Default_o_u_c_v_p_w_KebabMenuItemTemplateResource_default_InlineClientBundleGenerator _instance0 = new Type_factory__o_u_c_v_p_w_KebabMenuItem__quals__j_e_i_Any_j_e_i_Default_o_u_c_v_p_w_KebabMenuItemTemplateResource_default_InlineClientBundleGenerator();
  private void getContentsInitializer() {
    getContents = new com.google.gwt.resources.client.TextResource() {
      // jar:file:/Users/tiagobento/.m2/repository/org/uberfire/uberfire-workbench-client-views-patternfly/7.47.0-SNAPSHOT/uberfire-workbench-client-views-patternfly-7.47.0-SNAPSHOT-sources.jar!/org/uberfire/client/views/pfly/widgets/KebabMenuItem.html
      public String getText() {
        return "<li data-field=\"kebab-item\" ><a href=\"#\" data-field=\"anchor\"></a></li>";
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
  private static java.util.HashMap<java.lang.String, com.google.gwt.resources.client.ResourcePrototype> resourceMap;
  private static com.google.gwt.resources.client.TextResource getContents;
  
  public ResourcePrototype[] getResources() {
    return new ResourcePrototype[] {
      getContents(), 
    };
  }
  public ResourcePrototype getResource(String name) {
    if (GWT.isScript()) {
      return getResourceNative(name);
    } else {
      if (resourceMap == null) {
        resourceMap = new java.util.HashMap<java.lang.String, com.google.gwt.resources.client.ResourcePrototype>();
        resourceMap.put("getContents", getContents());
      }
      return resourceMap.get(name);
    }
  }
  private native ResourcePrototype getResourceNative(String name) /*-{
    switch (name) {
      case 'getContents': return this.@org.jboss.errai.ioc.client.Type_factory__o_u_c_v_p_w_KebabMenuItem__quals__j_e_i_Any_j_e_i_Default.o_u_c_v_p_w_KebabMenuItemTemplateResource::getContents()();
    }
    return null;
  }-*/;
}

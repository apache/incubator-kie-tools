package org.jboss.errai.ioc.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ResourcePrototype;

public class Type_factory__o_u_c_v_p_w_ConfirmPopup__quals__j_e_i_Any_j_e_i_Default_o_u_c_v_p_w_ConfirmPopupTemplateResource_default_InlineClientBundleGenerator implements org.jboss.errai.ioc.client.Type_factory__o_u_c_v_p_w_ConfirmPopup__quals__j_e_i_Any_j_e_i_Default.o_u_c_v_p_w_ConfirmPopupTemplateResource {
  private static Type_factory__o_u_c_v_p_w_ConfirmPopup__quals__j_e_i_Any_j_e_i_Default_o_u_c_v_p_w_ConfirmPopupTemplateResource_default_InlineClientBundleGenerator _instance0 = new Type_factory__o_u_c_v_p_w_ConfirmPopup__quals__j_e_i_Any_j_e_i_Default_o_u_c_v_p_w_ConfirmPopupTemplateResource_default_InlineClientBundleGenerator();
  private void getContentsInitializer() {
    getContents = new com.google.gwt.resources.client.TextResource() {
      // jar:file:/Users/tiagobento/.m2/repository/org/uberfire/uberfire-workbench-client-views-patternfly/7.47.0-SNAPSHOT/uberfire-workbench-client-views-patternfly-7.47.0-SNAPSHOT-sources.jar!/org/uberfire/client/views/pfly/widgets/ConfirmPopup.html
      public String getText() {
        return "<div class=\"modal fade\" data-field=\"modal\" tabindex=\"-1\" role=\"dialog\" aria-hidden=\"true\">\n    <div class=\"modal-dialog\">\n        <div class=\"modal-content\">\n            <div class=\"modal-header\">\n                <button type=\"button\" class=\"close\" data-field=\"confirm-close\" data-dismiss=\"modal\" aria-hidden=\"true\">\n                    <span class=\"pficon pficon-close\"></span>\n                </button>\n                <h4 class=\"modal-title\"><span data-field=\"confirm-title\"></span></h4>\n            </div>\n            <div class=\"modal-body\">\n                <form class=\"form-horizontal\">\n                    <fieldset>\n                        <div data-field=\"confirm-inline-notification\" style=\"display: none\"></div>\n                        <span data-field=\"confirm-message\"></span>\n                    </fieldset>\n                </form>\n            </div>\n            <div class=\"modal-footer\">\n                <button type=\"button\" class=\"btn btn-default\" data-field=\"confirm-cancel\" data-dismiss=\"modal\"></button>\n                <button type=\"button\" class=\"btn btn-danger\" data-field=\"confirm-ok\"></button>\n            </div>\n        </div>\n    </div>\n</div>";
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
      case 'getContents': return this.@org.jboss.errai.ioc.client.Type_factory__o_u_c_v_p_w_ConfirmPopup__quals__j_e_i_Any_j_e_i_Default.o_u_c_v_p_w_ConfirmPopupTemplateResource::getContents()();
    }
    return null;
  }-*/;
}

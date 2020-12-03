package org.jboss.errai.ioc.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ResourcePrototype;

public class Type_factory__o_u_c_v_p_m_m_WorkbenchMegaMenuView__quals__j_e_i_Any_j_e_i_Default_o_u_c_v_p_m_m_WorkbenchMegaMenuViewTemplateResource_default_InlineClientBundleGenerator implements org.jboss.errai.ioc.client.Type_factory__o_u_c_v_p_m_m_WorkbenchMegaMenuView__quals__j_e_i_Any_j_e_i_Default.o_u_c_v_p_m_m_WorkbenchMegaMenuViewTemplateResource {
  private static Type_factory__o_u_c_v_p_m_m_WorkbenchMegaMenuView__quals__j_e_i_Any_j_e_i_Default_o_u_c_v_p_m_m_WorkbenchMegaMenuViewTemplateResource_default_InlineClientBundleGenerator _instance0 = new Type_factory__o_u_c_v_p_m_m_WorkbenchMegaMenuView__quals__j_e_i_Any_j_e_i_Default_o_u_c_v_p_m_m_WorkbenchMegaMenuViewTemplateResource_default_InlineClientBundleGenerator();
  private void getContentsInitializer() {
    getContents = new com.google.gwt.resources.client.TextResource() {
      // jar:file:/Users/tiagobento/.m2/repository/org/uberfire/uberfire-workbench-client-views-patternfly/7.47.0-SNAPSHOT/uberfire-workbench-client-views-patternfly-7.47.0-SNAPSHOT-sources.jar!/org/uberfire/client/views/pfly/menu/megamenu/WorkbenchMegaMenuView.html
      public String getText() {
        return "<!--\n  ~ Copyright 2017 Red Hat, Inc. and/or its affiliates.\n  ~\n  ~ Licensed under the Apache License, Version 2.0 (the \"License\");\n  ~ you may not use this file except in compliance with the License.\n  ~ You may obtain a copy of the License at\n  ~\n  ~       http://www.apache.org/licenses/LICENSE-2.0\n  ~\n  ~ Unless required by applicable law or agreed to in writing, software\n  ~ distributed under the License is distributed on an \"AS IS\" BASIS,\n  ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n  ~ See the License for the specific language governing permissions and\n  ~ limitations under the License.\n  -->\n\n<!-- Masthead -->\n<div id=\"mega-menu\">\n    <nav class=\"uf-page__masthead navbar navbar-default navbar-pf\" role=\"navigation\">\n        <div data-field=\"brand\" class=\"navbar-header\">\n            <button type=\"button\" class=\"navbar-toggle\" data-toggle=\"collapse\" data-target=\".navbar-collapse-1\">\n                <span class=\"sr-only\">Toggle navigation</span>\n                <span class=\"icon-bar\"></span>\n                <span class=\"icon-bar\"></span>\n                <span class=\"icon-bar\"></span>\n            </button>\n            <a class=\"navbar-brand\">\n                <img data-field=\"brand-image\" class=\"uf-brand-icon\">\n            </a>\n        </div>\n        <div class=\"collapse navbar-collapse navbar-collapse-1\">\n\n            <ul class=\"nav navbar-nav uf-yamm\">\n                <li>\n                    <a data-field=\"home-link\" class=\"nav-item-iconic\"><i class=\"pficon pficon-home\"></i></a>\n                </li>\n                <li class=\"dropdown uf-yamm--fw\">\n                    <a class=\"dropdown-toggle nav-item-iconic\" id=\"mega-menu-dropdown\" data-toggle=\"dropdown\"\n                       aria-haspopup=\"true\" aria-expanded=\"false\" tabindex=\"0\">\n                        <span data-field=\"menu-accessor-text\"></span>\n                        <span class=\"caret\"></span>\n                    </a>\n                    <ul data-field=\"left-menu-items\" class=\"dropdown-menu uf-yamm__dropdown-menu\" aria-labelledby=\"mega-menu-dropdown\">\n                        <li data-field=\"single-menu-items-container\" class=\"uf-yamm__section uf-yamm__section--top col-xs-12 empty\">\n                            <ul data-field=\"single-menu-items\" class=\"uf-yamm__subsection\"></ul>\n                        </li>\n                    </ul>\n                </li>\n            </ul>\n\n            <ul data-field=\"right-menu-items\" class=\"nav navbar-nav navbar-right navbar-iconic\"></ul>\n\n        </div>\n    </nav>\n\n    <div class=\"navbar-pf navbar-context-menu\">\n        <ul class=\"navbar-primary persistent-secondary\">\n            <li data-field=\"context-menu-items-container\">\n                <ul data-field=\"context-menu-items\" class=\"nav navbar-nav navbar-persistent uf-perspective-context-menu\"></ul>\n            </li>\n        </ul>\n    </div>\n</div>\n";
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
      case 'getContents': return this.@org.jboss.errai.ioc.client.Type_factory__o_u_c_v_p_m_m_WorkbenchMegaMenuView__quals__j_e_i_Any_j_e_i_Default.o_u_c_v_p_m_m_WorkbenchMegaMenuViewTemplateResource::getContents()();
    }
    return null;
  }-*/;
}

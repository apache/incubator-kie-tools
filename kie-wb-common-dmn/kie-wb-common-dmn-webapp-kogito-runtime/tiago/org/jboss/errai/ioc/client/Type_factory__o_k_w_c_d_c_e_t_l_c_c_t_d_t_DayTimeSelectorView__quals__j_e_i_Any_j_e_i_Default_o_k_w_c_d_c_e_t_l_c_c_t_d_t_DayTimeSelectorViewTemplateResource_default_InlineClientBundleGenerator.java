package org.jboss.errai.ioc.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ResourcePrototype;

public class Type_factory__o_k_w_c_d_c_e_t_l_c_c_t_d_t_DayTimeSelectorView__quals__j_e_i_Any_j_e_i_Default_o_k_w_c_d_c_e_t_l_c_c_t_d_t_DayTimeSelectorViewTemplateResource_default_InlineClientBundleGenerator implements org.jboss.errai.ioc.client.Type_factory__o_k_w_c_d_c_e_t_l_c_c_t_d_t_DayTimeSelectorView__quals__j_e_i_Any_j_e_i_Default.o_k_w_c_d_c_e_t_l_c_c_t_d_t_DayTimeSelectorViewTemplateResource {
  private static Type_factory__o_k_w_c_d_c_e_t_l_c_c_t_d_t_DayTimeSelectorView__quals__j_e_i_Any_j_e_i_Default_o_k_w_c_d_c_e_t_l_c_c_t_d_t_DayTimeSelectorViewTemplateResource_default_InlineClientBundleGenerator _instance0 = new Type_factory__o_k_w_c_d_c_e_t_l_c_c_t_d_t_DayTimeSelectorView__quals__j_e_i_Any_j_e_i_Default_o_k_w_c_d_c_e_t_l_c_c_t_d_t_DayTimeSelectorViewTemplateResource_default_InlineClientBundleGenerator();
  private void getContentsInitializer() {
    getContents = new com.google.gwt.resources.client.TextResource() {
      // jar:file:/Users/tiagobento/redhat/kiegroup-all/kie-wb-common/kie-wb-common-dmn/kie-wb-common-dmn-client/target/kie-wb-common-dmn-client-7.47.0-SNAPSHOT-sources.jar!/org/kie/workbench/common/dmn/client/editors/types/listview/constraint/common/typed/day/time/DayTimeSelectorView.html
      public String getText() {
        return "<!--\n  ~ Copyright 2019 Red Hat, Inc. and/or its affiliates.\n  ~\n  ~ Licensed under the Apache License, Version 2.0 (the \"License\");\n  ~ you may not use this file except in compliance with the License.\n  ~ You may obtain a copy of the License at\n  ~\n  ~       http://www.apache.org/licenses/LICENSE-2.0\n  ~\n  ~ Unless required by applicable law or agreed to in writing, software\n  ~ distributed under the License is distributed on an \"AS IS\" BASIS,\n  ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n  ~ See the License for the specific language governing permissions and\n  ~ limitations under the License.\n  -->\n\n<div>\n    <div>\n        <label><input class=\"form-control input-field\" max=\"30\" type=\"number\" placeholder=\"dd\" data-field=\"days-input\"/></label>\n        <span data-i18n-key=\"Days\"></span>\n    </div>\n    <div>\n        <label><input class=\"form-control input-field\" max=\"23\" type=\"number\" placeholder=\"hh\" data-field=\"hours-input\"/></label>\n        <span data-i18n-key=\"Hours\"></span>\n    </div>\n    <div>\n        <label><input class=\"form-control input-field\" max=\"59\" type=\"number\" placeholder=\"mm\" data-field=\"minutes-input\"/></label>\n        <span data-i18n-key=\"Minutes\"></span>\n    </div>\n    <div>\n        <label><input class=\"form-control input-field\" max=\"59\" type=\"number\" placeholder=\"ss\" data-field=\"seconds-input\"/></label>\n        <span data-i18n-key=\"Seconds\"></span>\n    </div>\n</div>\n";
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
      case 'getContents': return this.@org.jboss.errai.ioc.client.Type_factory__o_k_w_c_d_c_e_t_l_c_c_t_d_t_DayTimeSelectorView__quals__j_e_i_Any_j_e_i_Default.o_k_w_c_d_c_e_t_l_c_c_t_d_t_DayTimeSelectorViewTemplateResource::getContents()();
    }
    return null;
  }-*/;
}

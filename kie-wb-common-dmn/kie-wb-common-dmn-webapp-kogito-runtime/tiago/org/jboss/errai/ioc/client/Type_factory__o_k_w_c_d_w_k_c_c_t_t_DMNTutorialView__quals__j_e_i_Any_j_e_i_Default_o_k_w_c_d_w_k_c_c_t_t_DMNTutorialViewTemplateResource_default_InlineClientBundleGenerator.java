package org.jboss.errai.ioc.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ResourcePrototype;

public class Type_factory__o_k_w_c_d_w_k_c_c_t_t_DMNTutorialView__quals__j_e_i_Any_j_e_i_Default_o_k_w_c_d_w_k_c_c_t_t_DMNTutorialViewTemplateResource_default_InlineClientBundleGenerator implements org.jboss.errai.ioc.client.Type_factory__o_k_w_c_d_w_k_c_c_t_t_DMNTutorialView__quals__j_e_i_Any_j_e_i_Default.o_k_w_c_d_w_k_c_c_t_t_DMNTutorialViewTemplateResource {
  private static Type_factory__o_k_w_c_d_w_k_c_c_t_t_DMNTutorialView__quals__j_e_i_Any_j_e_i_Default_o_k_w_c_d_w_k_c_c_t_t_DMNTutorialViewTemplateResource_default_InlineClientBundleGenerator _instance0 = new Type_factory__o_k_w_c_d_w_k_c_c_t_t_DMNTutorialView__quals__j_e_i_Any_j_e_i_Default_o_k_w_c_d_w_k_c_c_t_t_DMNTutorialViewTemplateResource_default_InlineClientBundleGenerator();
  private void getContentsInitializer() {
    getContents = new com.google.gwt.resources.client.TextResource() {
      // jar:file:/Users/tiagobento/.m2/repository/org/kie/workbench/kie-wb-common-dmn-webapp-kogito-common/7.47.0-SNAPSHOT/kie-wb-common-dmn-webapp-kogito-common-7.47.0-SNAPSHOT-sources.jar!/org/kie/workbench/common/dmn/webapp/kogito/common/client/tour/tutorial/DMNTutorialView.html
      public String getText() {
        return "<!--\n  ~ Copyright 2020 Red Hat, Inc. and/or its affiliates.\n  ~\n  ~ Licensed under the Apache License, Version 2.0 (the \"License\");\n  ~ you may not use this file except in compliance with the License.\n  ~ You may obtain a copy of the License at\n  ~\n  ~       http://www.apache.org/licenses/LICENSE-2.0\n  ~\n  ~ Unless required by applicable law or agreed to in writing, software\n  ~ distributed under the License is distributed on an \"AS IS\" BASIS,\n  ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n  ~ See the License for the specific language governing permissions and\n  ~ limitations under the License.\n  -->\n\n<div>\n    <!-- Step 0 -->\n    <div class=\"step\">\n        <div class=\"pf-c-content\">\n            <h3 class=\"pf-c-title pf-m-xl\">Interact with the canvas</h3>\n            <p data-pf-content=\"true\">\n                Let's create a decision node by dragging the rectangle onto the canvas.\n            </p>\n        </div>\n    </div>\n\n    <!-- Step 1 -->\n    <div class=\"step\">\n        <div class=\"pf-c-content\">\n            <h3 class=\"pf-c-title pf-m-xl\">Rename our decision node</h3>\n            <p data-pf-content=\"true\">\n                The decision nodes are the most important kind of node in our DMN model.\n                Their output value depends on their decision logic and input data (input\n                nodes or the output value from other decisions).\n            </p>\n            <p data-pf-content=\"true\">\n                Now, let's double-click our recently created decision node to rename\n                it to <b>Can drive?</b> (yes, with the question mark) ;-)\n            </p>\n        </div>\n    </div>\n\n    <!-- Step 2 -->\n    <div class=\"step\">\n        <div class=\"pf-c-content\">\n            <h3 class=\"pf-c-title pf-m-xl\">Create an input data node</h3>\n            <p data-pf-content=\"true\">Great stuff!</p>\n            <p data-pf-content=\"true\">\n                Now, let's create an input data node by dragging the rounded-corner\n                rectangle onto the canvas.\n            </p>\n        </div>\n    </div>\n\n    <!-- Step 3 -->\n    <div class=\"step\">\n        <div class=\"pf-c-content\">\n            <h3 class=\"pf-c-title pf-m-xl\">Rename the input data node</h3>\n            <p data-pf-content=\"true\">\n                Input data elements denote information used as input for one or more\n                decisions.\n            </p>\n            <p data-pf-content=\"true\">\n                Now, rename our input to <b>Age</b>. Remember, you can double-click\n                our node to rename.\n            </p>\n        </div>\n    </div>\n\n    <!-- Step 4 -->\n    <div class=\"step\">\n        <div class=\"pf-c-content\">\n            <h3 class=\"pf-c-title pf-m-xl\">Edit node properties</h3>\n            <p data-pf-content=\"true\">Excellent!</p>\n            <p data-pf-content=\"true\">\n                You can also define other properties of our nodes. Toggle the properties\n                panel by clicking the pencil icon.\n            </p>\n        </div>\n    </div>\n\n    <!-- Step 5 -->\n    <div class=\"step\">\n        <div class=\"pf-c-content\">\n            <h3 class=\"pf-c-title pf-m-xl\">Editor components</h3>\n            <p data-pf-content=\"true\">\n                The properties panel shows all properties related to the currently\n                selected node. You can update our input data (to set its\n                type to <i>number</i>, for example), but don't worry about that right now.\n            </p>\n            <p data-pf-content=\"true\">\n                Let's check another powerful and essential tool, the boxed expression\n                editor, which is responsible for the defining decision logic for\n                decision nodes. Open it by selecting our decision node\n                <b>Can drive?</b>, and then clicking the pencil icon.\n            </p>\n        </div>\n    </div>\n\n    <!-- Step 6 -->\n    <div class=\"step\">\n        <div class=\"pf-c-content\">\n            <h3 class=\"pf-c-title pf-m-xl\">Boxed expression editor</h3>\n            <p data-pf-content=\"true\">\n                Here's where we define the decision logic for our decision node. This\n                logic will determine the output value for the decision.\n            </p>\n            <p data-pf-content=\"true\">\n                Right now, you don't have any boxed expression defined for our node, so\n                click <b>Select expression</b> to select one of the available boxed\n                expressions.\n            </p>\n        </div>\n    </div>\n\n    <!-- Step 7 -->\n    <div class=\"step\">\n        <div class=\"pf-c-content\">\n            <h3 class=\"pf-c-title pf-m-xl\">Boxed expression</h3>\n            <p data-pf-content=\"true\">\n                Excellent! If you want to learn more about each kind of boxed expression, check this:\n                <a target=\"_blank\" href=\"http://learn-dmn-in-15-minutes.com/learn/decision-logic\">\n                    learn-dmn-in-15-minutes.com/learn/decision-logic\n                </a>.\n            </p>\n            <p data-pf-content=\"true\">Now, let's go back to our canvas by clicking this link!</p>\n        </div>\n    </div>\n</div>\n";
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
      case 'getContents': return this.@org.jboss.errai.ioc.client.Type_factory__o_k_w_c_d_w_k_c_c_t_t_DMNTutorialView__quals__j_e_i_Any_j_e_i_Default.o_k_w_c_d_w_k_c_c_t_t_DMNTutorialViewTemplateResource::getContents()();
    }
    return null;
  }-*/;
}

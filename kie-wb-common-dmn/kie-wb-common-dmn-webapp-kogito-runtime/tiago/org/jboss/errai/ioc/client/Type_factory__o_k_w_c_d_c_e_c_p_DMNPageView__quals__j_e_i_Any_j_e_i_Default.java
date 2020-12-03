package org.jboss.errai.ioc.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.StyleInjector;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ClientBundle.Source;
import com.google.gwt.resources.client.TextResource;
import com.google.gwt.user.client.ui.Widget;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ui.shared.DataFieldMeta;
import org.jboss.errai.ui.shared.Template;
import org.jboss.errai.ui.shared.TemplateUtil;
import org.jboss.errai.ui.shared.api.style.StyleBindingsRegistry;
import org.kie.workbench.common.dmn.client.editors.common.page.DMNPageView;

public class Type_factory__o_k_w_c_d_c_e_c_p_DMNPageView__quals__j_e_i_Any_j_e_i_Default extends Factory<DMNPageView> { public interface o_k_w_c_d_c_e_c_p_DMNPageViewTemplateResource extends Template, ClientBundle { @Source("org/kie/workbench/common/dmn/client/editors/common/page/DMNPageView.html") public TextResource getContents(); }
  public Type_factory__o_k_w_c_d_c_e_c_p_DMNPageView__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DMNPageView.class, "Type_factory__o_k_w_c_d_c_e_c_p_DMNPageView__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DMNPageView.class, Object.class });
  }

  public void init(final Context context) {
    StyleInjector.inject("/*\n * Copyright 2019 Red Hat, Inc. and/or its affiliates.\n *\n * Licensed under the Apache License, Version 2.0 (the \"License\");\n * you may not use this file except in compliance with the License.\n * You may obtain a copy of the License at\n *\n *       http://www.apache.org/licenses/LICENSE-2.0\n *\n * Unless required by applicable law or agreed to in writing, software\n * distributed under the License is distributed on an \"AS IS\" BASIS,\n * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n * See the License for the specific language governing permissions and\n * limitations under the License.\n */\n.tab-content .kie-dmn-page {\n  /* \"overflow: auto\" is set inline by UberFire/AppFormer since since most of editors have scroll bars.\n     * However the DMN Data Types has a fixed header with internal scrollbars. */\n  overflow: hidden !important;\n  display: flex;\n  height: 100%;\n  flex: 1 1 auto;\n  flex-direction: column;\n}\n.tab-content .kie-dmn-page > div {\n  height: 100%;\n  overflow-x: auto;\n}\n.tab-content .kie-dmn-page > div > div {\n  height: calc(100% - 20px);\n}\n.tab-content .kie-dmn-page h3 {\n  font-weight: 600;\n  font-size: 14px;\n  margin: 5px 0 10px;\n}\n/* Applies only on kie-wb-common-dmn-webapp */\n.qe-list-bar-content-Authoring-Screen .kie-dmn-page > div {\n  overflow: hidden;\n}\n\n");
  }

  public DMNPageView createInstance(final ContextManager contextManager) {
    final DMNPageView instance = new DMNPageView();
    setIncompleteInstance(instance);
    o_k_w_c_d_c_e_c_p_DMNPageViewTemplateResource templateForDMNPageView = GWT.create(o_k_w_c_d_c_e_c_p_DMNPageViewTemplateResource.class);
    Element parentElementForTemplateOfDMNPageView = TemplateUtil.getRootTemplateParentElement(templateForDMNPageView.getContents().getText(), "org/kie/workbench/common/dmn/client/editors/common/page/DMNPageView.html", "");
    TemplateUtil.translateTemplate("org/kie/workbench/common/dmn/client/editors/common/page/DMNPageView.html", TemplateUtil.getRootTemplateElement(parentElementForTemplateOfDMNPageView));
    Map<String, Element> dataFieldElements = TemplateUtil.getDataFieldElements(TemplateUtil.getRootTemplateElement(parentElementForTemplateOfDMNPageView));
    final Map<String, DataFieldMeta> dataFieldMetas = new HashMap<String, DataFieldMeta>(0);
    Map<String, Widget> templateFieldsMap = new LinkedHashMap<String, Widget>();
    TemplateUtil.initTemplated(instance, TemplateUtil.getRootTemplateElement(parentElementForTemplateOfDMNPageView), templateFieldsMap.values());
    StyleBindingsRegistry.get().updateStyles(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((DMNPageView) instance, contextManager);
  }

  public void destroyInstanceHelper(final DMNPageView instance, final ContextManager contextManager) {
    TemplateUtil.cleanupTemplated(instance);
  }
}
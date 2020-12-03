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
import org.jboss.errai.ui.client.local.api.elemental2.IsElement;
import org.jboss.errai.ui.shared.DataFieldMeta;
import org.jboss.errai.ui.shared.Template;
import org.jboss.errai.ui.shared.TemplateUtil;
import org.jboss.errai.ui.shared.api.style.StyleBindingsRegistry;
import org.kie.workbench.common.dmn.client.editors.included.grid.empty.DMNCardsEmptyStateView;

public class Type_factory__o_k_w_c_d_c_e_i_g_e_DMNCardsEmptyStateView__quals__j_e_i_Any_j_e_i_Default extends Factory<DMNCardsEmptyStateView> { public interface o_k_w_c_d_c_e_i_g_e_DMNCardsEmptyStateViewTemplateResource extends Template, ClientBundle { @Source("org/kie/workbench/common/dmn/client/editors/included/grid/empty/DMNCardsEmptyStateView.html") public TextResource getContents(); }
  public Type_factory__o_k_w_c_d_c_e_i_g_e_DMNCardsEmptyStateView__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DMNCardsEmptyStateView.class, "Type_factory__o_k_w_c_d_c_e_i_g_e_DMNCardsEmptyStateView__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DMNCardsEmptyStateView.class, Object.class, IsElement.class, org.jboss.errai.common.client.api.elemental2.IsElement.class });
  }

  public void init(final Context context) {
    StyleInjector.inject("/*\n * Copyright 2019 Red Hat, Inc. and/or its affiliates.\n *\n * Licensed under the Apache License, Version 2.0 (the \"License\");\n * you may not use this file except in compliance with the License.\n * You may obtain a copy of the License at\n *\n *       http://www.apache.org/licenses/LICENSE-2.0\n *\n * Unless required by applicable law or agreed to in writing, software\n * distributed under the License is distributed on an \"AS IS\" BASIS,\n * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n * See the License for the specific language governing permissions and\n * limitations under the License.\n */\n[data-i18n-prefix=\"DMNCardsEmptyStateView.\"] {\n  height: calc(100% - 100px);\n  width: 100%;\n  display: flex;\n  text-align: center;\n  justify-content: center;\n  align-items: center;\n}\n[data-i18n-prefix=\"DMNCardsEmptyStateView.\"] i {\n  color: #999999;\n  font-size: 5em;\n}\n\n");
  }

  public DMNCardsEmptyStateView createInstance(final ContextManager contextManager) {
    final DMNCardsEmptyStateView instance = new DMNCardsEmptyStateView();
    setIncompleteInstance(instance);
    o_k_w_c_d_c_e_i_g_e_DMNCardsEmptyStateViewTemplateResource templateForDMNCardsEmptyStateView = GWT.create(o_k_w_c_d_c_e_i_g_e_DMNCardsEmptyStateViewTemplateResource.class);
    Element parentElementForTemplateOfDMNCardsEmptyStateView = TemplateUtil.getRootTemplateParentElement(templateForDMNCardsEmptyStateView.getContents().getText(), "org/kie/workbench/common/dmn/client/editors/included/grid/empty/DMNCardsEmptyStateView.html", "");
    TemplateUtil.translateTemplate("org/kie/workbench/common/dmn/client/editors/included/grid/empty/DMNCardsEmptyStateView.html", TemplateUtil.getRootTemplateElement(parentElementForTemplateOfDMNCardsEmptyStateView));
    Map<String, Element> dataFieldElements = TemplateUtil.getDataFieldElements(TemplateUtil.getRootTemplateElement(parentElementForTemplateOfDMNCardsEmptyStateView));
    final Map<String, DataFieldMeta> dataFieldMetas = new HashMap<String, DataFieldMeta>(0);
    Map<String, Widget> templateFieldsMap = new LinkedHashMap<String, Widget>();
    TemplateUtil.initTemplated(instance, TemplateUtil.getRootTemplateElement(parentElementForTemplateOfDMNCardsEmptyStateView), templateFieldsMap.values());
    StyleBindingsRegistry.get().updateStyles(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((DMNCardsEmptyStateView) instance, contextManager);
  }

  public void destroyInstanceHelper(final DMNCardsEmptyStateView instance, final ContextManager contextManager) {
    TemplateUtil.cleanupTemplated(instance);
  }
}
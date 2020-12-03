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
import java.util.function.Supplier;
import javax.enterprise.context.Dependent;
import org.jboss.errai.common.client.api.elemental2.IsElement;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ui.shared.DataFieldMeta;
import org.jboss.errai.ui.shared.Template;
import org.jboss.errai.ui.shared.TemplateUtil;
import org.jboss.errai.ui.shared.api.style.StyleBindingsRegistry;
import org.kie.workbench.common.dmn.client.editors.contextmenu.ContextMenu.View;
import org.kie.workbench.common.dmn.client.editors.contextmenu.ContextMenuView;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.HasListSelectorControl;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.ListSelector;
import org.uberfire.client.mvp.HasPresenter;
import org.uberfire.client.mvp.UberElemental;

public class Type_factory__o_k_w_c_d_c_e_c_ContextMenuView__quals__j_e_i_Any_j_e_i_Default extends Factory<ContextMenuView> { public interface o_k_w_c_d_c_e_c_ContextMenuViewTemplateResource extends Template, ClientBundle { @Source("org/kie/workbench/common/dmn/client/editors/contextmenu/ContextMenuView.html") public TextResource getContents(); }
  public Type_factory__o_k_w_c_d_c_e_c_ContextMenuView__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(ContextMenuView.class, "Type_factory__o_k_w_c_d_c_e_c_ContextMenuView__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { ContextMenuView.class, Object.class, View.class, UberElemental.class, IsElement.class, HasPresenter.class, org.jboss.errai.ui.client.local.api.elemental2.IsElement.class, HasListSelectorControl.class });
  }

  public void init(final Context context) {
    StyleInjector.inject("/*\n * Copyright 2020 Red Hat, Inc. and/or its affiliates.\n *\n * Licensed under the Apache License, Version 2.0 (the \"License\");\n * you may not use this file except in compliance with the License.\n * You may obtain a copy of the License at\n *\n *     http://www.apache.org/licenses/LICENSE-2.0\n *\n * Unless required by applicable law or agreed to in writing, software\n * distributed under the License is distributed on an \"AS IS\" BASIS,\n * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n * See the License for the specific language governing permissions and\n * limitations under the License.\n */\n[data-i18n-prefix=\"ContextMenuView.\"] .dropdown-menu .dropdown-header i {\n  margin-right: 3px;\n}\n[data-i18n-prefix=\"ContextMenuView.\"] .dropdown-menu .dropdown-header span {\n  font-weight: 700;\n}\n[data-i18n-prefix=\"ContextMenuView.\"] .dropdown-menu li a:hover {\n  font-weight: 700;\n}\n\n");
  }

  public ContextMenuView createInstance(final ContextManager contextManager) {
    final ListSelector _listSelector_0 = (ListSelector) contextManager.getInstance("Type_factory__o_k_w_c_d_c_w_g_c_l_ListSelector__quals__j_e_i_Any_j_e_i_Default");
    final ContextMenuView instance = new ContextMenuView(_listSelector_0);
    registerDependentScopedReference(instance, _listSelector_0);
    setIncompleteInstance(instance);
    o_k_w_c_d_c_e_c_ContextMenuViewTemplateResource templateForContextMenuView = GWT.create(o_k_w_c_d_c_e_c_ContextMenuViewTemplateResource.class);
    Element parentElementForTemplateOfContextMenuView = TemplateUtil.getRootTemplateParentElement(templateForContextMenuView.getContents().getText(), "org/kie/workbench/common/dmn/client/editors/contextmenu/ContextMenuView.html", "");
    TemplateUtil.translateTemplate("org/kie/workbench/common/dmn/client/editors/contextmenu/ContextMenuView.html", TemplateUtil.getRootTemplateElement(parentElementForTemplateOfContextMenuView));
    Map<String, Element> dataFieldElements = TemplateUtil.getDataFieldElements(TemplateUtil.getRootTemplateElement(parentElementForTemplateOfContextMenuView));
    final Map<String, DataFieldMeta> dataFieldMetas = new HashMap<String, DataFieldMeta>(1);
    dataFieldMetas.put("list-selector", new DataFieldMeta());
    Map<String, Widget> templateFieldsMap = new LinkedHashMap<String, Widget>();
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.editors.contextmenu.ContextMenuView", "org/kie/workbench/common/dmn/client/editors/contextmenu/ContextMenuView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(ContextMenuView_ListSelector_listSelector(instance).getElement());
      }
    }, dataFieldElements, dataFieldMetas, "list-selector");
    templateFieldsMap.put("list-selector", ElementWrapperWidget.getWidget(ContextMenuView_ListSelector_listSelector(instance).getElement()));
    TemplateUtil.initTemplated(instance, TemplateUtil.getRootTemplateElement(parentElementForTemplateOfContextMenuView), templateFieldsMap.values());
    StyleBindingsRegistry.get().updateStyles(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((ContextMenuView) instance, contextManager);
  }

  public void destroyInstanceHelper(final ContextMenuView instance, final ContextManager contextManager) {
    ContextMenuView_removeDOMEventListeners(instance);
    TemplateUtil.cleanupTemplated(instance);
  }

  native static ListSelector ContextMenuView_ListSelector_listSelector(ContextMenuView instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.editors.contextmenu.ContextMenuView::listSelector;
  }-*/;

  native static void ContextMenuView_ListSelector_listSelector(ContextMenuView instance, ListSelector value) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.contextmenu.ContextMenuView::listSelector = value;
  }-*/;

  public native static void ContextMenuView_removeDOMEventListeners(ContextMenuView instance) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.contextmenu.ContextMenuView::removeDOMEventListeners()();
  }-*/;
}
package org.jboss.errai.ioc.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.StyleInjector;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ClientBundle.Source;
import com.google.gwt.resources.client.TextResource;
import com.google.gwt.user.client.ui.Widget;
import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLParagraphElement;
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
import org.kie.workbench.common.dmn.client.editors.included.grid.BaseCardComponent.ContentView;
import org.kie.workbench.common.dmn.client.editors.included.grid.BaseCardComponentContentView;
import org.kie.workbench.common.dmn.client.editors.included.grid.DefaultCardComponentContentView;
import org.uberfire.client.mvp.HasPresenter;
import org.uberfire.client.mvp.UberElemental;

public class Type_factory__o_k_w_c_d_c_e_i_g_DefaultCardComponentContentView__quals__j_e_i_Any_j_e_i_Default extends Factory<DefaultCardComponentContentView> { public interface o_k_w_c_d_c_e_i_g_DefaultCardComponentContentViewTemplateResource extends Template, ClientBundle { @Source("org/kie/workbench/common/dmn/client/editors/included/grid/DefaultCardComponentContentView.html") public TextResource getContents(); }
  public Type_factory__o_k_w_c_d_c_e_i_g_DefaultCardComponentContentView__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DefaultCardComponentContentView.class, "Type_factory__o_k_w_c_d_c_e_i_g_DefaultCardComponentContentView__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DefaultCardComponentContentView.class, BaseCardComponentContentView.class, Object.class, ContentView.class, UberElemental.class, IsElement.class, HasPresenter.class, org.jboss.errai.ui.client.local.api.elemental2.IsElement.class });
  }

  public void init(final Context context) {
    StyleInjector.inject("/*\n * Copyright 2019 Red Hat, Inc. and/or its affiliates.\n *\n * Licensed under the Apache License, Version 2.0 (the \"License\");\n * you may not use this file except in compliance with the License.\n * You may obtain a copy of the License at\n *\n *       http://www.apache.org/licenses/LICENSE-2.0\n *\n * Unless required by applicable law or agreed to in writing, software\n * distributed under the License is distributed on an \"AS IS\" BASIS,\n * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n * See the License for the specific language governing permissions and\n * limitations under the License.\n */\n[data-i18n-prefix=\"DefaultCardComponentContentView.\"] {\n  text-align: center;\n  margin: 5px auto;\n  max-width: 250px;\n  height: 100%;\n}\n[data-i18n-prefix=\"DefaultCardComponentContentView.\"] p {\n  overflow-wrap: break-word;\n}\n[data-i18n-prefix=\"DefaultCardComponentContentView.\"] [data-field=\"remove-button\"] {\n  margin-top: 15px;\n  padding: 2px 10px;\n}\n[data-i18n-prefix=\"DefaultCardComponentContentView.\"] [data-field=\"remove-button\"]:hover {\n  outline: none;\n}\n\n");
  }

  public DefaultCardComponentContentView createInstance(final ContextManager contextManager) {
    final HTMLButtonElement _removeButton_1 = (HTMLButtonElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLButtonElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final HTMLParagraphElement _path_0 = (HTMLParagraphElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLParagraphElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final DefaultCardComponentContentView instance = new DefaultCardComponentContentView(_path_0, _removeButton_1);
    registerDependentScopedReference(instance, _removeButton_1);
    registerDependentScopedReference(instance, _path_0);
    setIncompleteInstance(instance);
    o_k_w_c_d_c_e_i_g_DefaultCardComponentContentViewTemplateResource templateForDefaultCardComponentContentView = GWT.create(o_k_w_c_d_c_e_i_g_DefaultCardComponentContentViewTemplateResource.class);
    Element parentElementForTemplateOfDefaultCardComponentContentView = TemplateUtil.getRootTemplateParentElement(templateForDefaultCardComponentContentView.getContents().getText(), "org/kie/workbench/common/dmn/client/editors/included/grid/DefaultCardComponentContentView.html", "");
    TemplateUtil.translateTemplate("org/kie/workbench/common/dmn/client/editors/included/grid/DefaultCardComponentContentView.html", TemplateUtil.getRootTemplateElement(parentElementForTemplateOfDefaultCardComponentContentView));
    Map<String, Element> dataFieldElements = TemplateUtil.getDataFieldElements(TemplateUtil.getRootTemplateElement(parentElementForTemplateOfDefaultCardComponentContentView));
    final Map<String, DataFieldMeta> dataFieldMetas = new HashMap<String, DataFieldMeta>(2);
    dataFieldMetas.put("path", new DataFieldMeta());
    dataFieldMetas.put("remove-button", new DataFieldMeta());
    Map<String, Widget> templateFieldsMap = new LinkedHashMap<String, Widget>();
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.editors.included.grid.DefaultCardComponentContentView", "org/kie/workbench/common/dmn/client/editors/included/grid/DefaultCardComponentContentView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(BaseCardComponentContentView_HTMLParagraphElement_path(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "path");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.editors.included.grid.DefaultCardComponentContentView", "org/kie/workbench/common/dmn/client/editors/included/grid/DefaultCardComponentContentView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(BaseCardComponentContentView_HTMLButtonElement_removeButton(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "remove-button");
    templateFieldsMap.put("path", ElementWrapperWidget.getWidget(TemplateUtil.asElement(BaseCardComponentContentView_HTMLParagraphElement_path(instance))));
    templateFieldsMap.put("remove-button", ElementWrapperWidget.getWidget(TemplateUtil.asElement(BaseCardComponentContentView_HTMLButtonElement_removeButton(instance))));
    TemplateUtil.initTemplated(instance, TemplateUtil.getRootTemplateElement(parentElementForTemplateOfDefaultCardComponentContentView), templateFieldsMap.values());
    TemplateUtil.setupWrappedElementEventHandler(templateFieldsMap.get("remove-button"), new ClickHandler() {
      public void onClick(ClickEvent event) {
        instance.onRemoveButtonClick(event);
      }
    }, ClickEvent.getType());
    StyleBindingsRegistry.get().updateStyles(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((DefaultCardComponentContentView) instance, contextManager);
  }

  public void destroyInstanceHelper(final DefaultCardComponentContentView instance, final ContextManager contextManager) {
    TemplateUtil.cleanupTemplated(instance);
  }

  native static HTMLParagraphElement BaseCardComponentContentView_HTMLParagraphElement_path(BaseCardComponentContentView instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.editors.included.grid.BaseCardComponentContentView::path;
  }-*/;

  native static void BaseCardComponentContentView_HTMLParagraphElement_path(BaseCardComponentContentView instance, HTMLParagraphElement value) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.included.grid.BaseCardComponentContentView::path = value;
  }-*/;

  native static HTMLButtonElement BaseCardComponentContentView_HTMLButtonElement_removeButton(BaseCardComponentContentView instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.editors.included.grid.BaseCardComponentContentView::removeButton;
  }-*/;

  native static void BaseCardComponentContentView_HTMLButtonElement_removeButton(BaseCardComponentContentView instance, HTMLButtonElement value) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.included.grid.BaseCardComponentContentView::removeButton = value;
  }-*/;
}
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
import elemental2.dom.HTMLDivElement;
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
import org.kie.workbench.common.dmn.client.api.ReadOnlyProviderImpl;
import org.kie.workbench.common.dmn.client.editors.included.IncludedModelsPagePresenter.View;
import org.kie.workbench.common.dmn.client.editors.included.IncludedModelsPageView;
import org.kie.workbench.common.stunner.core.client.ReadOnlyProvider;
import org.uberfire.client.mvp.HasPresenter;
import org.uberfire.client.mvp.UberElemental;

public class Type_factory__o_k_w_c_d_c_e_i_IncludedModelsPageView__quals__j_e_i_Any_j_e_i_Default extends Factory<IncludedModelsPageView> { public interface o_k_w_c_d_c_e_i_IncludedModelsPageViewTemplateResource extends Template, ClientBundle { @Source("org/kie/workbench/common/dmn/client/editors/included/IncludedModelsPageView.html") public TextResource getContents(); }
  public Type_factory__o_k_w_c_d_c_e_i_IncludedModelsPageView__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(IncludedModelsPageView.class, "Type_factory__o_k_w_c_d_c_e_i_IncludedModelsPageView__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { IncludedModelsPageView.class, Object.class, View.class, UberElemental.class, IsElement.class, HasPresenter.class, org.jboss.errai.ui.client.local.api.elemental2.IsElement.class });
  }

  public void init(final Context context) {
    StyleInjector.inject("/*\n * Copyright 2019 Red Hat, Inc. and/or its affiliates.\n *\n * Licensed under the Apache License, Version 2.0 (the \"License\");\n * you may not use this file except in compliance with the License.\n * You may obtain a copy of the License at\n *\n *       http://www.apache.org/licenses/LICENSE-2.0\n *\n * Unless required by applicable law or agreed to in writing, software\n * distributed under the License is distributed on an \"AS IS\" BASIS,\n * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n * See the License for the specific language governing permissions and\n * limitations under the License.\n */\n[data-i18n-prefix=\"IncludedModelsPageView.\"] {\n  height: 100%;\n  display: flex;\n  flex-direction: column;\n}\n[data-i18n-prefix=\"IncludedModelsPageView.\"] [data-i18n-key=\"Title\"],\n[data-i18n-prefix=\"IncludedModelsPageView.\"] [data-i18n-key=\"Description\"] {\n  padding: 0 20px;\n}\n[data-i18n-prefix=\"IncludedModelsPageView.\"] .content {\n  position: relative;\n  padding-top: 25px;\n}\n[data-i18n-prefix=\"IncludedModelsPageView.\"] .content [data-field=\"include-model\"] {\n  position: absolute;\n  right: 15px;\n  top: 0;\n}\n[data-i18n-prefix=\"IncludedModelsPageView.\"] .content [data-field=\"grid\"] {\n  border-top: 1px solid #DEDEDE;\n  margin-top: 15px;\n}\n[data-i18n-prefix=\"IncludedModelsPageView.\"] .content,\n[data-i18n-prefix=\"IncludedModelsPageView.\"] .content [data-field=\"grid\"] {\n  display: flex;\n  overflow-y: auto;\n}\n[data-i18n-prefix=\"IncludedModelsPageView.\"] .content,\n[data-i18n-prefix=\"IncludedModelsPageView.\"] .content > div,\n[data-i18n-prefix=\"IncludedModelsPageView.\"] .content > div > div {\n  flex: 1;\n}\n\n");
  }

  public IncludedModelsPageView createInstance(final ContextManager contextManager) {
    final ReadOnlyProvider _readOnlyProvider_2 = (ReadOnlyProviderImpl) contextManager.getInstance("Type_factory__o_k_w_c_d_c_a_ReadOnlyProviderImpl__quals__j_e_i_Any_o_k_w_c_d_a_q_DMNEditor");
    final HTMLDivElement _grid_0 = (HTMLDivElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLDivElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final HTMLButtonElement _includeModelButton_1 = (HTMLButtonElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLButtonElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final IncludedModelsPageView instance = new IncludedModelsPageView(_grid_0, _includeModelButton_1, _readOnlyProvider_2);
    registerDependentScopedReference(instance, _grid_0);
    registerDependentScopedReference(instance, _includeModelButton_1);
    setIncompleteInstance(instance);
    o_k_w_c_d_c_e_i_IncludedModelsPageViewTemplateResource templateForIncludedModelsPageView = GWT.create(o_k_w_c_d_c_e_i_IncludedModelsPageViewTemplateResource.class);
    Element parentElementForTemplateOfIncludedModelsPageView = TemplateUtil.getRootTemplateParentElement(templateForIncludedModelsPageView.getContents().getText(), "org/kie/workbench/common/dmn/client/editors/included/IncludedModelsPageView.html", "");
    TemplateUtil.translateTemplate("org/kie/workbench/common/dmn/client/editors/included/IncludedModelsPageView.html", TemplateUtil.getRootTemplateElement(parentElementForTemplateOfIncludedModelsPageView));
    Map<String, Element> dataFieldElements = TemplateUtil.getDataFieldElements(TemplateUtil.getRootTemplateElement(parentElementForTemplateOfIncludedModelsPageView));
    final Map<String, DataFieldMeta> dataFieldMetas = new HashMap<String, DataFieldMeta>(2);
    dataFieldMetas.put("grid", new DataFieldMeta());
    dataFieldMetas.put("include-model", new DataFieldMeta());
    Map<String, Widget> templateFieldsMap = new LinkedHashMap<String, Widget>();
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.editors.included.IncludedModelsPageView", "org/kie/workbench/common/dmn/client/editors/included/IncludedModelsPageView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(IncludedModelsPageView_HTMLDivElement_grid(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "grid");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.editors.included.IncludedModelsPageView", "org/kie/workbench/common/dmn/client/editors/included/IncludedModelsPageView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(IncludedModelsPageView_HTMLButtonElement_includeModelButton(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "include-model");
    templateFieldsMap.put("grid", ElementWrapperWidget.getWidget(TemplateUtil.asElement(IncludedModelsPageView_HTMLDivElement_grid(instance))));
    templateFieldsMap.put("include-model", ElementWrapperWidget.getWidget(TemplateUtil.asElement(IncludedModelsPageView_HTMLButtonElement_includeModelButton(instance))));
    TemplateUtil.initTemplated(instance, TemplateUtil.getRootTemplateElement(parentElementForTemplateOfIncludedModelsPageView), templateFieldsMap.values());
    TemplateUtil.setupWrappedElementEventHandler(templateFieldsMap.get("include-model"), new ClickHandler() {
      public void onClick(ClickEvent event) {
        instance.onIncludeModelButtonClick(event);
      }
    }, ClickEvent.getType());
    StyleBindingsRegistry.get().updateStyles(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((IncludedModelsPageView) instance, contextManager);
  }

  public void destroyInstanceHelper(final IncludedModelsPageView instance, final ContextManager contextManager) {
    TemplateUtil.cleanupTemplated(instance);
  }

  native static HTMLDivElement IncludedModelsPageView_HTMLDivElement_grid(IncludedModelsPageView instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.editors.included.IncludedModelsPageView::grid;
  }-*/;

  native static void IncludedModelsPageView_HTMLDivElement_grid(IncludedModelsPageView instance, HTMLDivElement value) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.included.IncludedModelsPageView::grid = value;
  }-*/;

  native static HTMLButtonElement IncludedModelsPageView_HTMLButtonElement_includeModelButton(IncludedModelsPageView instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.editors.included.IncludedModelsPageView::includeModelButton;
  }-*/;

  native static void IncludedModelsPageView_HTMLButtonElement_includeModelButton(IncludedModelsPageView instance, HTMLButtonElement value) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.included.IncludedModelsPageView::includeModelButton = value;
  }-*/;
}
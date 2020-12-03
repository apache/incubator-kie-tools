package org.jboss.errai.ioc.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.StyleInjector;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ClientBundle.Source;
import com.google.gwt.resources.client.TextResource;
import com.google.gwt.user.client.ui.Widget;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLInputElement;
import elemental2.dom.HTMLSelectElement;
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
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.DataFieldMeta;
import org.jboss.errai.ui.shared.Template;
import org.jboss.errai.ui.shared.TemplateUtil;
import org.jboss.errai.ui.shared.api.style.StyleBindingsRegistry;
import org.kie.workbench.common.dmn.client.docks.navigator.included.components.DecisionComponents.View;
import org.kie.workbench.common.dmn.client.docks.navigator.included.components.DecisionComponentsView;
import org.uberfire.client.mvp.HasPresenter;
import org.uberfire.client.mvp.UberElemental;

public class Type_factory__o_k_w_c_d_c_d_n_i_c_DecisionComponentsView__quals__j_e_i_Any_j_e_i_Default extends Factory<DecisionComponentsView> { public interface o_k_w_c_d_c_d_n_i_c_DecisionComponentsViewTemplateResource extends Template, ClientBundle { @Source("org/kie/workbench/common/dmn/client/docks/navigator/included/components/DecisionComponentsView.html") public TextResource getContents(); }
  public Type_factory__o_k_w_c_d_c_d_n_i_c_DecisionComponentsView__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DecisionComponentsView.class, "Type_factory__o_k_w_c_d_c_d_n_i_c_DecisionComponentsView__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DecisionComponentsView.class, Object.class, View.class, UberElemental.class, IsElement.class, HasPresenter.class, org.jboss.errai.ui.client.local.api.elemental2.IsElement.class });
  }

  public void init(final Context context) {
    StyleInjector.inject("/*\n * Copyright 2019 Red Hat, Inc. and/or its affiliates.\n *\n * Licensed under the Apache License, Version 2.0 (the \"License\");\n * you may not use this file except in compliance with the License.\n * You may obtain a copy of the License at\n *\n *       http://www.apache.org/licenses/LICENSE-2.0\n *\n * Unless required by applicable law or agreed to in writing, software\n * distributed under the License is distributed on an \"AS IS\" BASIS,\n * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n * See the License for the specific language governing permissions and\n * limitations under the License.\n */\n[data-i18n-prefix=\"DecisionComponentsView.\"] {\n  position: relative;\n}\n[data-i18n-prefix=\"DecisionComponentsView.\"] .components-counter {\n  position: absolute;\n  top: -43px;\n  right: 0;\n  background-color: #676B70;\n  padding: 0 12px 1px;\n  border-radius: 12px;\n  color: #ECEEF1;\n  font-size: 12px;\n  text-align: center;\n}\n[data-i18n-prefix=\"DecisionComponentsView.\"] > label {\n  width: 100%;\n}\n[data-i18n-prefix=\"DecisionComponentsView.\"] > label > .bootstrap-select:not([class*=col-]):not([class*=form-control]):not(.input-group-btn) {\n  width: 100%;\n}\n[data-i18n-prefix=\"DecisionComponentsView.\"] .form-control {\n  margin-bottom: 15px;\n}\n[data-i18n-prefix=\"DecisionComponentsView.\"] .spinner {\n  margin: 40px auto;\n}\n[data-i18n-prefix=\"DecisionComponentsView.\"] [data-field=\"empty-state\"] {\n  opacity: .75;\n  padding-top: 5px;\n}\n\n");
  }

  public DecisionComponentsView createInstance(final ContextManager contextManager) {
    final HTMLDivElement _componentsCounter_5 = (HTMLDivElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLDivElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final TranslationService _translationService_6 = (TranslationService) contextManager.getInstance("Provider_factory__o_j_e_u_c_l_s_TranslationService__quals__j_e_i_Any_j_e_i_Default");
    final HTMLSelectElement _drgElementFilter_0 = (HTMLSelectElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLSelectElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final HTMLInputElement _termFilter_1 = (HTMLInputElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLInputElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final HTMLDivElement _emptyState_3 = (HTMLDivElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLDivElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final HTMLDivElement _loading_4 = (HTMLDivElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLDivElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final HTMLDivElement _list_2 = (HTMLDivElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLDivElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final DecisionComponentsView instance = new DecisionComponentsView(_drgElementFilter_0, _termFilter_1, _list_2, _emptyState_3, _loading_4, _componentsCounter_5, _translationService_6);
    registerDependentScopedReference(instance, _componentsCounter_5);
    registerDependentScopedReference(instance, _translationService_6);
    registerDependentScopedReference(instance, _drgElementFilter_0);
    registerDependentScopedReference(instance, _termFilter_1);
    registerDependentScopedReference(instance, _emptyState_3);
    registerDependentScopedReference(instance, _loading_4);
    registerDependentScopedReference(instance, _list_2);
    setIncompleteInstance(instance);
    o_k_w_c_d_c_d_n_i_c_DecisionComponentsViewTemplateResource templateForDecisionComponentsView = GWT.create(o_k_w_c_d_c_d_n_i_c_DecisionComponentsViewTemplateResource.class);
    Element parentElementForTemplateOfDecisionComponentsView = TemplateUtil.getRootTemplateParentElement(templateForDecisionComponentsView.getContents().getText(), "org/kie/workbench/common/dmn/client/docks/navigator/included/components/DecisionComponentsView.html", "");
    TemplateUtil.translateTemplate("org/kie/workbench/common/dmn/client/docks/navigator/included/components/DecisionComponentsView.html", TemplateUtil.getRootTemplateElement(parentElementForTemplateOfDecisionComponentsView));
    Map<String, Element> dataFieldElements = TemplateUtil.getDataFieldElements(TemplateUtil.getRootTemplateElement(parentElementForTemplateOfDecisionComponentsView));
    final Map<String, DataFieldMeta> dataFieldMetas = new HashMap<String, DataFieldMeta>(6);
    dataFieldMetas.put("drg-element-filter", new DataFieldMeta());
    dataFieldMetas.put("term-filter", new DataFieldMeta());
    dataFieldMetas.put("list", new DataFieldMeta());
    dataFieldMetas.put("empty-state", new DataFieldMeta());
    dataFieldMetas.put("loading", new DataFieldMeta());
    dataFieldMetas.put("components-counter", new DataFieldMeta());
    Map<String, Widget> templateFieldsMap = new LinkedHashMap<String, Widget>();
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.docks.navigator.included.components.DecisionComponentsView", "org/kie/workbench/common/dmn/client/docks/navigator/included/components/DecisionComponentsView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(DecisionComponentsView_HTMLSelectElement_drgElementFilter(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "drg-element-filter");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.docks.navigator.included.components.DecisionComponentsView", "org/kie/workbench/common/dmn/client/docks/navigator/included/components/DecisionComponentsView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(DecisionComponentsView_HTMLInputElement_termFilter(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "term-filter");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.docks.navigator.included.components.DecisionComponentsView", "org/kie/workbench/common/dmn/client/docks/navigator/included/components/DecisionComponentsView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(DecisionComponentsView_HTMLDivElement_list(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "list");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.docks.navigator.included.components.DecisionComponentsView", "org/kie/workbench/common/dmn/client/docks/navigator/included/components/DecisionComponentsView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(DecisionComponentsView_HTMLDivElement_emptyState(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "empty-state");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.docks.navigator.included.components.DecisionComponentsView", "org/kie/workbench/common/dmn/client/docks/navigator/included/components/DecisionComponentsView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(DecisionComponentsView_HTMLDivElement_loading(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "loading");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.docks.navigator.included.components.DecisionComponentsView", "org/kie/workbench/common/dmn/client/docks/navigator/included/components/DecisionComponentsView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(DecisionComponentsView_HTMLDivElement_componentsCounter(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "components-counter");
    templateFieldsMap.put("drg-element-filter", ElementWrapperWidget.getWidget(TemplateUtil.asElement(DecisionComponentsView_HTMLSelectElement_drgElementFilter(instance))));
    templateFieldsMap.put("term-filter", ElementWrapperWidget.getWidget(TemplateUtil.asElement(DecisionComponentsView_HTMLInputElement_termFilter(instance))));
    templateFieldsMap.put("list", ElementWrapperWidget.getWidget(TemplateUtil.asElement(DecisionComponentsView_HTMLDivElement_list(instance))));
    templateFieldsMap.put("empty-state", ElementWrapperWidget.getWidget(TemplateUtil.asElement(DecisionComponentsView_HTMLDivElement_emptyState(instance))));
    templateFieldsMap.put("loading", ElementWrapperWidget.getWidget(TemplateUtil.asElement(DecisionComponentsView_HTMLDivElement_loading(instance))));
    templateFieldsMap.put("components-counter", ElementWrapperWidget.getWidget(TemplateUtil.asElement(DecisionComponentsView_HTMLDivElement_componentsCounter(instance))));
    TemplateUtil.initTemplated(instance, TemplateUtil.getRootTemplateElement(parentElementForTemplateOfDecisionComponentsView), templateFieldsMap.values());
    TemplateUtil.setupWrappedElementEventHandler(templateFieldsMap.get("term-filter"), new KeyUpHandler() {
      public void onKeyUp(KeyUpEvent event) {
        instance.onTermFilterChange(event);
      }
    }, KeyUpEvent.getType());
    StyleBindingsRegistry.get().updateStyles(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((DecisionComponentsView) instance, contextManager);
  }

  public void destroyInstanceHelper(final DecisionComponentsView instance, final ContextManager contextManager) {
    TemplateUtil.cleanupTemplated(instance);
  }

  public void invokePostConstructs(final DecisionComponentsView instance) {
    instance.init();
  }

  native static HTMLDivElement DecisionComponentsView_HTMLDivElement_emptyState(DecisionComponentsView instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.docks.navigator.included.components.DecisionComponentsView::emptyState;
  }-*/;

  native static void DecisionComponentsView_HTMLDivElement_emptyState(DecisionComponentsView instance, HTMLDivElement value) /*-{
    instance.@org.kie.workbench.common.dmn.client.docks.navigator.included.components.DecisionComponentsView::emptyState = value;
  }-*/;

  native static HTMLSelectElement DecisionComponentsView_HTMLSelectElement_drgElementFilter(DecisionComponentsView instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.docks.navigator.included.components.DecisionComponentsView::drgElementFilter;
  }-*/;

  native static void DecisionComponentsView_HTMLSelectElement_drgElementFilter(DecisionComponentsView instance, HTMLSelectElement value) /*-{
    instance.@org.kie.workbench.common.dmn.client.docks.navigator.included.components.DecisionComponentsView::drgElementFilter = value;
  }-*/;

  native static HTMLDivElement DecisionComponentsView_HTMLDivElement_componentsCounter(DecisionComponentsView instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.docks.navigator.included.components.DecisionComponentsView::componentsCounter;
  }-*/;

  native static void DecisionComponentsView_HTMLDivElement_componentsCounter(DecisionComponentsView instance, HTMLDivElement value) /*-{
    instance.@org.kie.workbench.common.dmn.client.docks.navigator.included.components.DecisionComponentsView::componentsCounter = value;
  }-*/;

  native static HTMLDivElement DecisionComponentsView_HTMLDivElement_loading(DecisionComponentsView instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.docks.navigator.included.components.DecisionComponentsView::loading;
  }-*/;

  native static void DecisionComponentsView_HTMLDivElement_loading(DecisionComponentsView instance, HTMLDivElement value) /*-{
    instance.@org.kie.workbench.common.dmn.client.docks.navigator.included.components.DecisionComponentsView::loading = value;
  }-*/;

  native static HTMLDivElement DecisionComponentsView_HTMLDivElement_list(DecisionComponentsView instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.docks.navigator.included.components.DecisionComponentsView::list;
  }-*/;

  native static void DecisionComponentsView_HTMLDivElement_list(DecisionComponentsView instance, HTMLDivElement value) /*-{
    instance.@org.kie.workbench.common.dmn.client.docks.navigator.included.components.DecisionComponentsView::list = value;
  }-*/;

  native static HTMLInputElement DecisionComponentsView_HTMLInputElement_termFilter(DecisionComponentsView instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.docks.navigator.included.components.DecisionComponentsView::termFilter;
  }-*/;

  native static void DecisionComponentsView_HTMLInputElement_termFilter(DecisionComponentsView instance, HTMLInputElement value) /*-{
    instance.@org.kie.workbench.common.dmn.client.docks.navigator.included.components.DecisionComponentsView::termFilter = value;
  }-*/;
}
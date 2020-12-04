package org.jboss.errai.ioc.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.StyleInjector;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ClientBundle.Source;
import com.google.gwt.resources.client.TextResource;
import com.google.gwt.user.client.ui.Widget;
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
import org.kie.workbench.common.dmn.client.docks.navigator.DecisionNavigatorPresenter.View;
import org.kie.workbench.common.dmn.client.docks.navigator.DecisionNavigatorView;
import org.uberfire.client.mvp.HasPresenter;
import org.uberfire.client.mvp.UberElemental;

public class Type_factory__o_k_w_c_d_c_d_n_DecisionNavigatorView__quals__j_e_i_Any_j_e_i_Default extends Factory<DecisionNavigatorView> { public interface o_k_w_c_d_c_d_n_DecisionNavigatorViewTemplateResource extends Template, ClientBundle { @Source("org/kie/workbench/common/dmn/client/docks/navigator/DecisionNavigatorView.html") public TextResource getContents(); }
  public Type_factory__o_k_w_c_d_c_d_n_DecisionNavigatorView__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DecisionNavigatorView.class, "Type_factory__o_k_w_c_d_c_d_n_DecisionNavigatorView__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DecisionNavigatorView.class, Object.class, View.class, UberElemental.class, IsElement.class, HasPresenter.class, org.jboss.errai.ui.client.local.api.elemental2.IsElement.class });
  }

  public void init(final Context context) {
    StyleInjector.inject("/*\n * Copyright 2019 Red Hat, Inc. and/or its affiliates.\n *\n * Licensed under the Apache License, Version 2.0 (the \"License\");\n * you may not use this file except in compliance with the License.\n * You may obtain a copy of the License at\n *\n *       http://www.apache.org/licenses/LICENSE-2.0\n *\n * Unless required by applicable law or agreed to in writing, software\n * distributed under the License is distributed on an \"AS IS\" BASIS,\n * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n * See the License for the specific language governing permissions and\n * limitations under the License.\n */\n[data-i18n-prefix=\"DecisionNavigatorView.\"] {\n  height: 100%;\n  overflow: auto;\n}\n[data-i18n-prefix=\"DecisionNavigatorView.\"] .panel-group {\n  margin: 5px 5px 15px 0;\n  border-left: none;\n}\n[data-i18n-prefix=\"DecisionNavigatorView.\"] .panel-group .panel-title > a:before {\n  content: \"\\f0d7\";\n}\n[data-i18n-prefix=\"DecisionNavigatorView.\"] .panel-group .panel-title > a.collapsed:before {\n  content: \"\\f0da\";\n}\n[data-i18n-prefix=\"DecisionNavigatorView.\"] .panel-group .panel-default {\n  border-color: #DFDFDF;\n}\n[data-i18n-prefix=\"DecisionNavigatorView.\"] .panel-group .panel-heading {\n  background-image: none;\n  background-color: #FFFFFF;\n  padding: 15px;\n}\n[data-i18n-prefix=\"DecisionNavigatorView.\"] .panel-group .panel-body {\n  background-image: linear-gradient(to bottom, #efefef 0, #ffffff 2%);\n}\n[data-i18n-prefix=\"DecisionNavigatorView.\"] #decision-component-content .panel-body {\n  padding: 10px 8px;\n  background: #F7F7F7;\n}\n[data-i18n-prefix=\"DecisionNavigatorView.\"] [data-field=\"main-tree\"],\n[data-i18n-prefix=\"DecisionNavigatorView.\"] [data-field=\"decision-components\"] {\n  min-height: 40vh;\n  position: relative;\n}\n\n");
  }

  public DecisionNavigatorView createInstance(final ContextManager contextManager) {
    final HTMLDivElement _mainTree_0 = (HTMLDivElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLDivElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final HTMLDivElement _decisionComponentsContainer_1 = (HTMLDivElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLDivElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final HTMLDivElement _decisionComponents_2 = (HTMLDivElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLDivElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final DecisionNavigatorView instance = new DecisionNavigatorView(_mainTree_0, _decisionComponentsContainer_1, _decisionComponents_2);
    registerDependentScopedReference(instance, _mainTree_0);
    registerDependentScopedReference(instance, _decisionComponentsContainer_1);
    registerDependentScopedReference(instance, _decisionComponents_2);
    setIncompleteInstance(instance);
    o_k_w_c_d_c_d_n_DecisionNavigatorViewTemplateResource templateForDecisionNavigatorView = GWT.create(o_k_w_c_d_c_d_n_DecisionNavigatorViewTemplateResource.class);
    Element parentElementForTemplateOfDecisionNavigatorView = TemplateUtil.getRootTemplateParentElement(templateForDecisionNavigatorView.getContents().getText(), "org/kie/workbench/common/dmn/client/docks/navigator/DecisionNavigatorView.html", "");
    TemplateUtil.translateTemplate("org/kie/workbench/common/dmn/client/docks/navigator/DecisionNavigatorView.html", TemplateUtil.getRootTemplateElement(parentElementForTemplateOfDecisionNavigatorView));
    Map<String, Element> dataFieldElements = TemplateUtil.getDataFieldElements(TemplateUtil.getRootTemplateElement(parentElementForTemplateOfDecisionNavigatorView));
    final Map<String, DataFieldMeta> dataFieldMetas = new HashMap<String, DataFieldMeta>(3);
    dataFieldMetas.put("main-tree", new DataFieldMeta());
    dataFieldMetas.put("decision-components-container", new DataFieldMeta());
    dataFieldMetas.put("decision-components", new DataFieldMeta());
    Map<String, Widget> templateFieldsMap = new LinkedHashMap<String, Widget>();
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.docks.navigator.DecisionNavigatorView", "org/kie/workbench/common/dmn/client/docks/navigator/DecisionNavigatorView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(DecisionNavigatorView_HTMLDivElement_mainTree(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "main-tree");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.docks.navigator.DecisionNavigatorView", "org/kie/workbench/common/dmn/client/docks/navigator/DecisionNavigatorView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(DecisionNavigatorView_HTMLDivElement_decisionComponentsContainer(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "decision-components-container");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.docks.navigator.DecisionNavigatorView", "org/kie/workbench/common/dmn/client/docks/navigator/DecisionNavigatorView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(DecisionNavigatorView_HTMLDivElement_decisionComponents(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "decision-components");
    templateFieldsMap.put("main-tree", ElementWrapperWidget.getWidget(TemplateUtil.asElement(DecisionNavigatorView_HTMLDivElement_mainTree(instance))));
    templateFieldsMap.put("decision-components-container", ElementWrapperWidget.getWidget(TemplateUtil.asElement(DecisionNavigatorView_HTMLDivElement_decisionComponentsContainer(instance))));
    templateFieldsMap.put("decision-components", ElementWrapperWidget.getWidget(TemplateUtil.asElement(DecisionNavigatorView_HTMLDivElement_decisionComponents(instance))));
    TemplateUtil.initTemplated(instance, TemplateUtil.getRootTemplateElement(parentElementForTemplateOfDecisionNavigatorView), templateFieldsMap.values());
    StyleBindingsRegistry.get().updateStyles(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((DecisionNavigatorView) instance, contextManager);
  }

  public void destroyInstanceHelper(final DecisionNavigatorView instance, final ContextManager contextManager) {
    TemplateUtil.cleanupTemplated(instance);
  }

  native static HTMLDivElement DecisionNavigatorView_HTMLDivElement_mainTree(DecisionNavigatorView instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.docks.navigator.DecisionNavigatorView::mainTree;
  }-*/;

  native static void DecisionNavigatorView_HTMLDivElement_mainTree(DecisionNavigatorView instance, HTMLDivElement value) /*-{
    instance.@org.kie.workbench.common.dmn.client.docks.navigator.DecisionNavigatorView::mainTree = value;
  }-*/;

  native static HTMLDivElement DecisionNavigatorView_HTMLDivElement_decisionComponentsContainer(DecisionNavigatorView instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.docks.navigator.DecisionNavigatorView::decisionComponentsContainer;
  }-*/;

  native static void DecisionNavigatorView_HTMLDivElement_decisionComponentsContainer(DecisionNavigatorView instance, HTMLDivElement value) /*-{
    instance.@org.kie.workbench.common.dmn.client.docks.navigator.DecisionNavigatorView::decisionComponentsContainer = value;
  }-*/;

  native static HTMLDivElement DecisionNavigatorView_HTMLDivElement_decisionComponents(DecisionNavigatorView instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.docks.navigator.DecisionNavigatorView::decisionComponents;
  }-*/;

  native static void DecisionNavigatorView_HTMLDivElement_decisionComponents(DecisionNavigatorView instance, HTMLDivElement value) /*-{
    instance.@org.kie.workbench.common.dmn.client.docks.navigator.DecisionNavigatorView::decisionComponents = value;
  }-*/;
}
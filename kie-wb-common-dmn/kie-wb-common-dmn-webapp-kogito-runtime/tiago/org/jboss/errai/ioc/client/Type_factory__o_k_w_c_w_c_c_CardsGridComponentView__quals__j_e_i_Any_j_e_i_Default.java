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
import org.kie.workbench.common.widgets.client.cards.CardsGridComponent.View;
import org.kie.workbench.common.widgets.client.cards.CardsGridComponentView;
import org.uberfire.client.mvp.HasPresenter;
import org.uberfire.client.mvp.UberElemental;

public class Type_factory__o_k_w_c_w_c_c_CardsGridComponentView__quals__j_e_i_Any_j_e_i_Default extends Factory<CardsGridComponentView> { public interface o_k_w_c_w_c_c_CardsGridComponentViewTemplateResource extends Template, ClientBundle { @Source("org/kie/workbench/common/widgets/client/cards/CardsGridComponentView.html") public TextResource getContents(); }
  public Type_factory__o_k_w_c_w_c_c_CardsGridComponentView__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(CardsGridComponentView.class, "Type_factory__o_k_w_c_w_c_c_CardsGridComponentView__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { CardsGridComponentView.class, Object.class, View.class, UberElemental.class, IsElement.class, HasPresenter.class, org.jboss.errai.ui.client.local.api.elemental2.IsElement.class });
  }

  public void init(final Context context) {
    StyleInjector.inject("/*\n * Copyright 2019 Red Hat, Inc. and/or its affiliates.\n *\n * Licensed under the Apache License, Version 2.0 (the \"License\");\n * you may not use this file except in compliance with the License.\n * You may obtain a copy of the License at\n *\n *       http://www.apache.org/licenses/LICENSE-2.0\n *\n * Unless required by applicable law or agreed to in writing, software\n * distributed under the License is distributed on an \"AS IS\" BASIS,\n * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n * See the License for the specific language governing permissions and\n * limitations under the License.\n */\n[data-i18n-prefix=\"CardsGridComponentView.\"] {\n  height: 100%;\n}\n[data-i18n-prefix=\"CardsGridComponentView.\"] .row.row-cards-pf {\n  padding: 20px 20px 5px;\n  margin-right: 0;\n}\n[data-i18n-prefix=\"CardsGridComponentView.\"] .row.row-cards-pf.container-cards-pf {\n  margin-top: 0;\n  height: 100%;\n}\n\n");
  }

  public CardsGridComponentView createInstance(final ContextManager contextManager) {
    final HTMLDivElement _cardGrid_0 = (HTMLDivElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLDivElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final CardsGridComponentView instance = new CardsGridComponentView(_cardGrid_0);
    registerDependentScopedReference(instance, _cardGrid_0);
    setIncompleteInstance(instance);
    o_k_w_c_w_c_c_CardsGridComponentViewTemplateResource templateForCardsGridComponentView = GWT.create(o_k_w_c_w_c_c_CardsGridComponentViewTemplateResource.class);
    Element parentElementForTemplateOfCardsGridComponentView = TemplateUtil.getRootTemplateParentElement(templateForCardsGridComponentView.getContents().getText(), "org/kie/workbench/common/widgets/client/cards/CardsGridComponentView.html", "");
    TemplateUtil.translateTemplate("org/kie/workbench/common/widgets/client/cards/CardsGridComponentView.html", TemplateUtil.getRootTemplateElement(parentElementForTemplateOfCardsGridComponentView));
    Map<String, Element> dataFieldElements = TemplateUtil.getDataFieldElements(TemplateUtil.getRootTemplateElement(parentElementForTemplateOfCardsGridComponentView));
    final Map<String, DataFieldMeta> dataFieldMetas = new HashMap<String, DataFieldMeta>(1);
    dataFieldMetas.put("card-grid", new DataFieldMeta());
    Map<String, Widget> templateFieldsMap = new LinkedHashMap<String, Widget>();
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.widgets.client.cards.CardsGridComponentView", "org/kie/workbench/common/widgets/client/cards/CardsGridComponentView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(CardsGridComponentView_HTMLDivElement_cardGrid(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "card-grid");
    templateFieldsMap.put("card-grid", ElementWrapperWidget.getWidget(TemplateUtil.asElement(CardsGridComponentView_HTMLDivElement_cardGrid(instance))));
    TemplateUtil.initTemplated(instance, TemplateUtil.getRootTemplateElement(parentElementForTemplateOfCardsGridComponentView), templateFieldsMap.values());
    StyleBindingsRegistry.get().updateStyles(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((CardsGridComponentView) instance, contextManager);
  }

  public void destroyInstanceHelper(final CardsGridComponentView instance, final ContextManager contextManager) {
    TemplateUtil.cleanupTemplated(instance);
  }

  native static HTMLDivElement CardsGridComponentView_HTMLDivElement_cardGrid(CardsGridComponentView instance) /*-{
    return instance.@org.kie.workbench.common.widgets.client.cards.CardsGridComponentView::cardGrid;
  }-*/;

  native static void CardsGridComponentView_HTMLDivElement_cardGrid(CardsGridComponentView instance, HTMLDivElement value) /*-{
    instance.@org.kie.workbench.common.widgets.client.cards.CardsGridComponentView::cardGrid = value;
  }-*/;
}
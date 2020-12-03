package org.jboss.errai.ioc.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.StyleInjector;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ClientBundle.Source;
import com.google.gwt.resources.client.TextResource;
import com.google.gwt.user.client.ui.Widget;
import elemental2.dom.HTMLInputElement;
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
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.years.months.YearsMonthsSelector.View;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.years.months.YearsMonthsSelectorView;
import org.uberfire.client.mvp.HasPresenter;
import org.uberfire.client.mvp.UberElemental;

public class Type_factory__o_k_w_c_d_c_e_t_l_c_c_t_y_m_YearsMonthsSelectorView__quals__j_e_i_Any_j_e_i_Default extends Factory<YearsMonthsSelectorView> { public interface o_k_w_c_d_c_e_t_l_c_c_t_y_m_YearsMonthsSelectorViewTemplateResource extends Template, ClientBundle { @Source("org/kie/workbench/common/dmn/client/editors/types/listview/constraint/common/typed/years/months/YearsMonthsSelectorView.html") public TextResource getContents(); }
  public Type_factory__o_k_w_c_d_c_e_t_l_c_c_t_y_m_YearsMonthsSelectorView__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(YearsMonthsSelectorView.class, "Type_factory__o_k_w_c_d_c_e_t_l_c_c_t_y_m_YearsMonthsSelectorView__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { YearsMonthsSelectorView.class, Object.class, View.class, UberElemental.class, IsElement.class, HasPresenter.class, org.jboss.errai.ui.client.local.api.elemental2.IsElement.class });
  }

  public void init(final Context context) {
    StyleInjector.inject("/*\n * Copyright 2019 Red Hat, Inc. and/or its affiliates.\n *\n * Licensed under the Apache License, Version 2.0 (the \"License\");\n * you may not use this file except in compliance with the License.\n * You may obtain a copy of the License at\n *\n *     http://www.apache.org/licenses/LICENSE-2.0\n *\n * Unless required by applicable law or agreed to in writing, software\n * distributed under the License is distributed on an \"AS IS\" BASIS,\n * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n * See the License for the specific language governing permissions and\n * limitations under the License.\n */\n[data-i18n-prefix=\"YearsMonthsSelectorView.\"] .input-field {\n  width: 100px;\n  display: inline;\n}\n[data-i18n-prefix=\"YearsMonthsSelectorView.\"] .col-md-5 {\n  padding-left: 0;\n  padding-right: 0;\n}\n[data-i18n-prefix=\"YearsMonthsSelectorView.\"] .col-md-7 {\n  padding-left: 10px;\n  padding-right: 0;\n}\n[data-i18n-prefix=\"YearsMonthsSelectorView.\"] span {\n  display: inline;\n  opacity: .75;\n}\n[data-i18n-prefix=\"YearsMonthsSelectorView.\"] .yearsMos {\n  display: inline;\n}\n[data-i18n-prefix=\"YearsMonthsSelectorView.\"] .row {\n  padding: 0;\n}\n\n");
  }

  public YearsMonthsSelectorView createInstance(final ContextManager contextManager) {
    final HTMLInputElement _monthInput_1 = (HTMLInputElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLInputElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final HTMLInputElement _yearInput_0 = (HTMLInputElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLInputElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final YearsMonthsSelectorView instance = new YearsMonthsSelectorView(_yearInput_0, _monthInput_1);
    registerDependentScopedReference(instance, _monthInput_1);
    registerDependentScopedReference(instance, _yearInput_0);
    setIncompleteInstance(instance);
    o_k_w_c_d_c_e_t_l_c_c_t_y_m_YearsMonthsSelectorViewTemplateResource templateForYearsMonthsSelectorView = GWT.create(o_k_w_c_d_c_e_t_l_c_c_t_y_m_YearsMonthsSelectorViewTemplateResource.class);
    Element parentElementForTemplateOfYearsMonthsSelectorView = TemplateUtil.getRootTemplateParentElement(templateForYearsMonthsSelectorView.getContents().getText(), "org/kie/workbench/common/dmn/client/editors/types/listview/constraint/common/typed/years/months/YearsMonthsSelectorView.html", "");
    TemplateUtil.translateTemplate("org/kie/workbench/common/dmn/client/editors/types/listview/constraint/common/typed/years/months/YearsMonthsSelectorView.html", TemplateUtil.getRootTemplateElement(parentElementForTemplateOfYearsMonthsSelectorView));
    Map<String, Element> dataFieldElements = TemplateUtil.getDataFieldElements(TemplateUtil.getRootTemplateElement(parentElementForTemplateOfYearsMonthsSelectorView));
    final Map<String, DataFieldMeta> dataFieldMetas = new HashMap<String, DataFieldMeta>(2);
    dataFieldMetas.put("years-input", new DataFieldMeta());
    dataFieldMetas.put("months-input", new DataFieldMeta());
    Map<String, Widget> templateFieldsMap = new LinkedHashMap<String, Widget>();
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.years.months.YearsMonthsSelectorView", "org/kie/workbench/common/dmn/client/editors/types/listview/constraint/common/typed/years/months/YearsMonthsSelectorView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(YearsMonthsSelectorView_HTMLInputElement_yearInput(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "years-input");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.years.months.YearsMonthsSelectorView", "org/kie/workbench/common/dmn/client/editors/types/listview/constraint/common/typed/years/months/YearsMonthsSelectorView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(YearsMonthsSelectorView_HTMLInputElement_monthInput(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "months-input");
    templateFieldsMap.put("years-input", ElementWrapperWidget.getWidget(TemplateUtil.asElement(YearsMonthsSelectorView_HTMLInputElement_yearInput(instance))));
    templateFieldsMap.put("months-input", ElementWrapperWidget.getWidget(TemplateUtil.asElement(YearsMonthsSelectorView_HTMLInputElement_monthInput(instance))));
    TemplateUtil.initTemplated(instance, TemplateUtil.getRootTemplateElement(parentElementForTemplateOfYearsMonthsSelectorView), templateFieldsMap.values());
    TemplateUtil.setupWrappedElementEventHandler(templateFieldsMap.get("months-input"), new BlurHandler() {
      public void onBlur(BlurEvent event) {
        instance.onMonthsInputBlur(event);
      }
    }, BlurEvent.getType());
    TemplateUtil.setupWrappedElementEventHandler(templateFieldsMap.get("years-input"), new BlurHandler() {
      public void onBlur(BlurEvent event) {
        instance.onYearsInputBlur(event);
      }
    }, BlurEvent.getType());
    StyleBindingsRegistry.get().updateStyles(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((YearsMonthsSelectorView) instance, contextManager);
  }

  public void destroyInstanceHelper(final YearsMonthsSelectorView instance, final ContextManager contextManager) {
    TemplateUtil.cleanupTemplated(instance);
  }

  native static HTMLInputElement YearsMonthsSelectorView_HTMLInputElement_monthInput(YearsMonthsSelectorView instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.years.months.YearsMonthsSelectorView::monthInput;
  }-*/;

  native static void YearsMonthsSelectorView_HTMLInputElement_monthInput(YearsMonthsSelectorView instance, HTMLInputElement value) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.years.months.YearsMonthsSelectorView::monthInput = value;
  }-*/;

  native static HTMLInputElement YearsMonthsSelectorView_HTMLInputElement_yearInput(YearsMonthsSelectorView instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.years.months.YearsMonthsSelectorView::yearInput;
  }-*/;

  native static void YearsMonthsSelectorView_HTMLInputElement_yearInput(YearsMonthsSelectorView instance, HTMLInputElement value) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.years.months.YearsMonthsSelectorView::yearInput = value;
  }-*/;
}
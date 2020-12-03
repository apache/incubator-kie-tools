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
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.date.DateSelector.View;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.date.DateSelectorView;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.date.DateValueFormatter;
import org.uberfire.client.mvp.HasPresenter;
import org.uberfire.client.mvp.UberElemental;

public class Type_factory__o_k_w_c_d_c_e_t_l_c_c_t_d_DateSelectorView__quals__j_e_i_Any_j_e_i_Default extends Factory<DateSelectorView> { public interface o_k_w_c_d_c_e_t_l_c_c_t_d_DateSelectorViewTemplateResource extends Template, ClientBundle { @Source("org/kie/workbench/common/dmn/client/editors/types/listview/constraint/common/typed/date/DateSelectorView.html") public TextResource getContents(); }
  public Type_factory__o_k_w_c_d_c_e_t_l_c_c_t_d_DateSelectorView__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DateSelectorView.class, "Type_factory__o_k_w_c_d_c_e_t_l_c_c_t_d_DateSelectorView__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DateSelectorView.class, Object.class, View.class, UberElemental.class, IsElement.class, HasPresenter.class, org.jboss.errai.ui.client.local.api.elemental2.IsElement.class });
  }

  public void init(final Context context) {
    StyleInjector.inject("/*\n * Copyright 2019 Red Hat, Inc. and/or its affiliates.\n *\n * Licensed under the Apache License, Version 2.0 (the \"License\");\n * you may not use this file except in compliance with the License.\n * You may obtain a copy of the License at\n *\n *     http://www.apache.org/licenses/LICENSE-2.0\n *\n * Unless required by applicable law or agreed to in writing, software\n * distributed under the License is distributed on an \"AS IS\" BASIS,\n * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n * See the License for the specific language governing permissions and\n * limitations under the License.\n */\n[data-i18n-prefix=\"DateSelectorView.\"] [data-field=\"calendar-date-icon\"] {\n  z-index: 1;\n  left: 10px;\n  color: #7B7B7B;\n  cursor: pointer;\n  width: 0;\n}\n[data-i18n-prefix=\"DateSelectorView.\"] .date-input-field {\n  padding-left: 21px;\n}\n\n");
  }

  public DateSelectorView createInstance(final ContextManager contextManager) {
    final DateValueFormatter _valueFormatter_1 = (DateValueFormatter) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_t_l_c_c_t_d_DateValueFormatter__quals__j_e_i_Any_j_e_i_Default");
    final HTMLInputElement _dateInput_0 = (HTMLInputElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLInputElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final DateSelectorView instance = new DateSelectorView(_dateInput_0, _valueFormatter_1);
    registerDependentScopedReference(instance, _valueFormatter_1);
    registerDependentScopedReference(instance, _dateInput_0);
    setIncompleteInstance(instance);
    o_k_w_c_d_c_e_t_l_c_c_t_d_DateSelectorViewTemplateResource templateForDateSelectorView = GWT.create(o_k_w_c_d_c_e_t_l_c_c_t_d_DateSelectorViewTemplateResource.class);
    Element parentElementForTemplateOfDateSelectorView = TemplateUtil.getRootTemplateParentElement(templateForDateSelectorView.getContents().getText(), "org/kie/workbench/common/dmn/client/editors/types/listview/constraint/common/typed/date/DateSelectorView.html", "");
    TemplateUtil.translateTemplate("org/kie/workbench/common/dmn/client/editors/types/listview/constraint/common/typed/date/DateSelectorView.html", TemplateUtil.getRootTemplateElement(parentElementForTemplateOfDateSelectorView));
    Map<String, Element> dataFieldElements = TemplateUtil.getDataFieldElements(TemplateUtil.getRootTemplateElement(parentElementForTemplateOfDateSelectorView));
    final Map<String, DataFieldMeta> dataFieldMetas = new HashMap<String, DataFieldMeta>(1);
    dataFieldMetas.put("date-input", new DataFieldMeta());
    Map<String, Widget> templateFieldsMap = new LinkedHashMap<String, Widget>();
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.date.DateSelectorView", "org/kie/workbench/common/dmn/client/editors/types/listview/constraint/common/typed/date/DateSelectorView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(DateSelectorView_HTMLInputElement_dateInput(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "date-input");
    templateFieldsMap.put("date-input", ElementWrapperWidget.getWidget(TemplateUtil.asElement(DateSelectorView_HTMLInputElement_dateInput(instance))));
    TemplateUtil.initTemplated(instance, TemplateUtil.getRootTemplateElement(parentElementForTemplateOfDateSelectorView), templateFieldsMap.values());
    TemplateUtil.setupWrappedElementEventHandler(templateFieldsMap.get("date-input"), new BlurHandler() {
      public void onBlur(BlurEvent event) {
        instance.onDateInputBlur(event);
      }
    }, BlurEvent.getType());
    StyleBindingsRegistry.get().updateStyles(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((DateSelectorView) instance, contextManager);
  }

  public void destroyInstanceHelper(final DateSelectorView instance, final ContextManager contextManager) {
    TemplateUtil.cleanupTemplated(instance);
  }

  public void invokePostConstructs(final DateSelectorView instance) {
    DateSelectorView_init(instance);
  }

  native static HTMLInputElement DateSelectorView_HTMLInputElement_dateInput(DateSelectorView instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.date.DateSelectorView::dateInput;
  }-*/;

  native static void DateSelectorView_HTMLInputElement_dateInput(DateSelectorView instance, HTMLInputElement value) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.date.DateSelectorView::dateInput = value;
  }-*/;

  public native static void DateSelectorView_init(DateSelectorView instance) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.date.DateSelectorView::init()();
  }-*/;
}
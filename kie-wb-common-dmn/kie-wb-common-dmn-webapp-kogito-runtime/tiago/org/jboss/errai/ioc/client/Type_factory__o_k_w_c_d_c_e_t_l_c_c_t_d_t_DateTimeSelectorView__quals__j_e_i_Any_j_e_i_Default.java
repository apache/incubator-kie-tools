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
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.ConstraintPlaceholderHelper;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.date.DateSelector;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.date.time.DateTimeSelector.View;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.date.time.DateTimeSelectorView;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.time.TimeSelector;
import org.uberfire.client.mvp.HasPresenter;
import org.uberfire.client.mvp.UberElemental;

public class Type_factory__o_k_w_c_d_c_e_t_l_c_c_t_d_t_DateTimeSelectorView__quals__j_e_i_Any_j_e_i_Default extends Factory<DateTimeSelectorView> { public interface o_k_w_c_d_c_e_t_l_c_c_t_d_t_DateTimeSelectorViewTemplateResource extends Template, ClientBundle { @Source("org/kie/workbench/common/dmn/client/editors/types/listview/constraint/common/typed/date/time/DateTimeSelectorView.html") public TextResource getContents(); }
  public Type_factory__o_k_w_c_d_c_e_t_l_c_c_t_d_t_DateTimeSelectorView__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DateTimeSelectorView.class, "Type_factory__o_k_w_c_d_c_e_t_l_c_c_t_d_t_DateTimeSelectorView__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DateTimeSelectorView.class, Object.class, View.class, UberElemental.class, IsElement.class, HasPresenter.class, org.jboss.errai.ui.client.local.api.elemental2.IsElement.class });
  }

  public void init(final Context context) {
    StyleInjector.inject("/*\n * Copyright 2019 Red Hat, Inc. and/or its affiliates.\n *\n * Licensed under the Apache License, Version 2.0 (the \"License\");\n * you may not use this file except in compliance with the License.\n * You may obtain a copy of the License at\n *\n *     http://www.apache.org/licenses/LICENSE-2.0\n *\n * Unless required by applicable law or agreed to in writing, software\n * distributed under the License is distributed on an \"AS IS\" BASIS,\n * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n * See the License for the specific language governing permissions and\n * limitations under the License.\n */\n[data-i18n-prefix=\"DateTimeSelectorView.\"] #date-input {\n  width: 110px;\n  height: 26px;\n}\n[data-i18n-prefix=\"DateTimeSelectorView.\"] #time-input {\n  width: 65px;\n  height: 26px;\n}\n[data-i18n-prefix=\"DateTimeSelectorView.\"] #time-selector-container {\n  width: 110px;\n}\n[data-i18n-prefix=\"DateTimeSelectorView.\"] .col-md-4 {\n  padding-left: 0;\n  padding-right: 0;\n  width: 29%;\n}\n[data-i18n-prefix=\"DateTimeSelectorView.\"] .col-md-8 {\n  padding-left: 0;\n  padding-right: 0;\n}\n\n");
  }

  public DateTimeSelectorView createInstance(final ContextManager contextManager) {
    final HTMLDivElement _timeSelectorContainer_1 = (HTMLDivElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLDivElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final TimeSelector _timeSelector_3 = (TimeSelector) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_t_l_c_c_t_t_TimeSelector__quals__j_e_i_Any_j_e_i_Default");
    final ConstraintPlaceholderHelper _placeholderHelper_4 = (ConstraintPlaceholderHelper) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_t_l_c_c_ConstraintPlaceholderHelper__quals__j_e_i_Any_j_e_i_Default");
    final HTMLDivElement _dateSelectorContainer_0 = (HTMLDivElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLDivElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final DateSelector _dateSelector_2 = (DateSelector) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_t_l_c_c_t_d_DateSelector__quals__j_e_i_Any_j_e_i_Default");
    final DateTimeSelectorView instance = new DateTimeSelectorView(_dateSelectorContainer_0, _timeSelectorContainer_1, _dateSelector_2, _timeSelector_3, _placeholderHelper_4);
    registerDependentScopedReference(instance, _timeSelectorContainer_1);
    registerDependentScopedReference(instance, _timeSelector_3);
    registerDependentScopedReference(instance, _placeholderHelper_4);
    registerDependentScopedReference(instance, _dateSelectorContainer_0);
    registerDependentScopedReference(instance, _dateSelector_2);
    setIncompleteInstance(instance);
    o_k_w_c_d_c_e_t_l_c_c_t_d_t_DateTimeSelectorViewTemplateResource templateForDateTimeSelectorView = GWT.create(o_k_w_c_d_c_e_t_l_c_c_t_d_t_DateTimeSelectorViewTemplateResource.class);
    Element parentElementForTemplateOfDateTimeSelectorView = TemplateUtil.getRootTemplateParentElement(templateForDateTimeSelectorView.getContents().getText(), "org/kie/workbench/common/dmn/client/editors/types/listview/constraint/common/typed/date/time/DateTimeSelectorView.html", "");
    TemplateUtil.translateTemplate("org/kie/workbench/common/dmn/client/editors/types/listview/constraint/common/typed/date/time/DateTimeSelectorView.html", TemplateUtil.getRootTemplateElement(parentElementForTemplateOfDateTimeSelectorView));
    Map<String, Element> dataFieldElements = TemplateUtil.getDataFieldElements(TemplateUtil.getRootTemplateElement(parentElementForTemplateOfDateTimeSelectorView));
    final Map<String, DataFieldMeta> dataFieldMetas = new HashMap<String, DataFieldMeta>(2);
    dataFieldMetas.put("date-selector-container", new DataFieldMeta());
    dataFieldMetas.put("time-selector-container", new DataFieldMeta());
    Map<String, Widget> templateFieldsMap = new LinkedHashMap<String, Widget>();
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.date.time.DateTimeSelectorView", "org/kie/workbench/common/dmn/client/editors/types/listview/constraint/common/typed/date/time/DateTimeSelectorView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(DateTimeSelectorView_HTMLDivElement_dateSelectorContainer(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "date-selector-container");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.date.time.DateTimeSelectorView", "org/kie/workbench/common/dmn/client/editors/types/listview/constraint/common/typed/date/time/DateTimeSelectorView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(DateTimeSelectorView_HTMLDivElement_timeSelectorContainer(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "time-selector-container");
    templateFieldsMap.put("date-selector-container", ElementWrapperWidget.getWidget(TemplateUtil.asElement(DateTimeSelectorView_HTMLDivElement_dateSelectorContainer(instance))));
    templateFieldsMap.put("time-selector-container", ElementWrapperWidget.getWidget(TemplateUtil.asElement(DateTimeSelectorView_HTMLDivElement_timeSelectorContainer(instance))));
    TemplateUtil.initTemplated(instance, TemplateUtil.getRootTemplateElement(parentElementForTemplateOfDateTimeSelectorView), templateFieldsMap.values());
    StyleBindingsRegistry.get().updateStyles(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((DateTimeSelectorView) instance, contextManager);
  }

  public void destroyInstanceHelper(final DateTimeSelectorView instance, final ContextManager contextManager) {
    TemplateUtil.cleanupTemplated(instance);
  }

  public void invokePostConstructs(final DateTimeSelectorView instance) {
    DateTimeSelectorView_init(instance);
  }

  native static HTMLDivElement DateTimeSelectorView_HTMLDivElement_dateSelectorContainer(DateTimeSelectorView instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.date.time.DateTimeSelectorView::dateSelectorContainer;
  }-*/;

  native static void DateTimeSelectorView_HTMLDivElement_dateSelectorContainer(DateTimeSelectorView instance, HTMLDivElement value) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.date.time.DateTimeSelectorView::dateSelectorContainer = value;
  }-*/;

  native static HTMLDivElement DateTimeSelectorView_HTMLDivElement_timeSelectorContainer(DateTimeSelectorView instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.date.time.DateTimeSelectorView::timeSelectorContainer;
  }-*/;

  native static void DateTimeSelectorView_HTMLDivElement_timeSelectorContainer(DateTimeSelectorView instance, HTMLDivElement value) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.date.time.DateTimeSelectorView::timeSelectorContainer = value;
  }-*/;

  public native static void DateTimeSelectorView_init(DateTimeSelectorView instance) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.date.time.DateTimeSelectorView::init()();
  }-*/;
}
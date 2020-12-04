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
import elemental2.dom.HTMLAnchorElement;
import elemental2.dom.HTMLElement;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;
import javax.enterprise.context.Dependent;
import javax.inject.Named;
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
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.time.picker.TimePicker.View;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.time.picker.TimePickerView;
import org.uberfire.client.mvp.HasPresenter;
import org.uberfire.client.mvp.UberElemental;

public class Type_factory__o_k_w_c_d_c_e_t_l_c_c_t_t_p_TimePickerView__quals__j_e_i_Any_j_e_i_Default extends Factory<TimePickerView> { public interface o_k_w_c_d_c_e_t_l_c_c_t_t_p_TimePickerViewTemplateResource extends Template, ClientBundle { @Source("org/kie/workbench/common/dmn/client/editors/types/listview/constraint/common/typed/time/picker/TimePickerView.html") public TextResource getContents(); }
  public Type_factory__o_k_w_c_d_c_e_t_l_c_c_t_t_p_TimePickerView__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(TimePickerView.class, "Type_factory__o_k_w_c_d_c_e_t_l_c_c_t_t_p_TimePickerView__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { TimePickerView.class, Object.class, View.class, UberElemental.class, IsElement.class, HasPresenter.class, org.jboss.errai.ui.client.local.api.elemental2.IsElement.class });
  }

  public void init(final Context context) {
    StyleInjector.inject("/*\n * Copyright 2019 Red Hat, Inc. and/or its affiliates.\n *\n * Licensed under the Apache License, Version 2.0 (the \"License\");\n * you may not use this file except in compliance with the License.\n * You may obtain a copy of the License at\n *\n *     http://www.apache.org/licenses/LICENSE-2.0\n *\n * Unless required by applicable law or agreed to in writing, software\n * distributed under the License is distributed on an \"AS IS\" BASIS,\n * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n * See the License for the specific language governing permissions and\n * limitations under the License.\n */\n[data-i18n-prefix=\"TimePickerView.\"].time-picker {\n  background-color: white;\n  height: 110px;\n  width: 220px;\n  position: fixed;\n  border: 1px solid #CCC;\n  box-shadow: 3px 3px 5px #888888;\n}\n[data-i18n-prefix=\"TimePickerView.\"] .col-md-1,\n[data-i18n-prefix=\"TimePickerView.\"] .col-md-3 {\n  vertical-align: middle;\n  text-align: center;\n  padding: 0;\n}\n[data-i18n-prefix=\"TimePickerView.\"] .arrow {\n  padding: 10px;\n  color: #676b70;\n}\n[data-i18n-prefix=\"TimePickerView.\"] .colon {\n  color: #676b70;\n  height: 100px;\n  line-height: 95px;\n  font-size: 26px;\n}\n[data-i18n-prefix=\"TimePickerView.\"] .number {\n  color: #676b70;\n  vertical-align: middle;\n  text-align: center;\n  font-size: 22px;\n  padding: 15px;\n  font-weight: 600;\n}\n\n");
  }

  public TimePickerView createInstance(final ContextManager contextManager) {
    final HTMLElement _hours_6 = (HTMLElement) contextManager.getContextualInstance("ContextualProvider_factory__e_d_HTMLElement__quals__Universal", new Class[] { }, new Annotation[] { new Named() {
        public Class annotationType() {
          return Named.class;
        }
        public String toString() {
          return "@javax.inject.Named(value=span)";
        }
        public String value() {
          return "span";
        }
    } });
    final HTMLElement _minutes_7 = (HTMLElement) contextManager.getContextualInstance("ContextualProvider_factory__e_d_HTMLElement__quals__Universal", new Class[] { }, new Annotation[] { new Named() {
        public Class annotationType() {
          return Named.class;
        }
        public String toString() {
          return "@javax.inject.Named(value=span)";
        }
        public String value() {
          return "span";
        }
    } });
    final HTMLAnchorElement _increaseHours_0 = (HTMLAnchorElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLAnchorElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final HTMLAnchorElement _increaseMinutes_2 = (HTMLAnchorElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLAnchorElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final HTMLElement _seconds_8 = (HTMLElement) contextManager.getContextualInstance("ContextualProvider_factory__e_d_HTMLElement__quals__Universal", new Class[] { }, new Annotation[] { new Named() {
        public Class annotationType() {
          return Named.class;
        }
        public String toString() {
          return "@javax.inject.Named(value=span)";
        }
        public String value() {
          return "span";
        }
    } });
    final HTMLAnchorElement _decreaseHours_1 = (HTMLAnchorElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLAnchorElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final HTMLAnchorElement _decreaseSeconds_5 = (HTMLAnchorElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLAnchorElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final HTMLAnchorElement _decreaseMinutes_3 = (HTMLAnchorElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLAnchorElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final HTMLAnchorElement _increaseSeconds_4 = (HTMLAnchorElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLAnchorElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final TimePickerView instance = new TimePickerView(_increaseHours_0, _decreaseHours_1, _increaseMinutes_2, _decreaseMinutes_3, _increaseSeconds_4, _decreaseSeconds_5, _hours_6, _minutes_7, _seconds_8);
    registerDependentScopedReference(instance, _hours_6);
    registerDependentScopedReference(instance, _minutes_7);
    registerDependentScopedReference(instance, _increaseHours_0);
    registerDependentScopedReference(instance, _increaseMinutes_2);
    registerDependentScopedReference(instance, _seconds_8);
    registerDependentScopedReference(instance, _decreaseHours_1);
    registerDependentScopedReference(instance, _decreaseSeconds_5);
    registerDependentScopedReference(instance, _decreaseMinutes_3);
    registerDependentScopedReference(instance, _increaseSeconds_4);
    setIncompleteInstance(instance);
    o_k_w_c_d_c_e_t_l_c_c_t_t_p_TimePickerViewTemplateResource templateForTimePickerView = GWT.create(o_k_w_c_d_c_e_t_l_c_c_t_t_p_TimePickerViewTemplateResource.class);
    Element parentElementForTemplateOfTimePickerView = TemplateUtil.getRootTemplateParentElement(templateForTimePickerView.getContents().getText(), "org/kie/workbench/common/dmn/client/editors/types/listview/constraint/common/typed/time/picker/TimePickerView.html", "");
    TemplateUtil.translateTemplate("org/kie/workbench/common/dmn/client/editors/types/listview/constraint/common/typed/time/picker/TimePickerView.html", TemplateUtil.getRootTemplateElement(parentElementForTemplateOfTimePickerView));
    Map<String, Element> dataFieldElements = TemplateUtil.getDataFieldElements(TemplateUtil.getRootTemplateElement(parentElementForTemplateOfTimePickerView));
    final Map<String, DataFieldMeta> dataFieldMetas = new HashMap<String, DataFieldMeta>(9);
    dataFieldMetas.put("increase-hours", new DataFieldMeta());
    dataFieldMetas.put("decrease-hours", new DataFieldMeta());
    dataFieldMetas.put("increase-minutes", new DataFieldMeta());
    dataFieldMetas.put("decrease-minutes", new DataFieldMeta());
    dataFieldMetas.put("increase-seconds", new DataFieldMeta());
    dataFieldMetas.put("decrease-seconds", new DataFieldMeta());
    dataFieldMetas.put("hours", new DataFieldMeta());
    dataFieldMetas.put("minutes", new DataFieldMeta());
    dataFieldMetas.put("seconds", new DataFieldMeta());
    Map<String, Widget> templateFieldsMap = new LinkedHashMap<String, Widget>();
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.time.picker.TimePickerView", "org/kie/workbench/common/dmn/client/editors/types/listview/constraint/common/typed/time/picker/TimePickerView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(TimePickerView_HTMLAnchorElement_increaseHours(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "increase-hours");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.time.picker.TimePickerView", "org/kie/workbench/common/dmn/client/editors/types/listview/constraint/common/typed/time/picker/TimePickerView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(TimePickerView_HTMLAnchorElement_decreaseHours(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "decrease-hours");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.time.picker.TimePickerView", "org/kie/workbench/common/dmn/client/editors/types/listview/constraint/common/typed/time/picker/TimePickerView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(TimePickerView_HTMLAnchorElement_increaseMinutes(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "increase-minutes");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.time.picker.TimePickerView", "org/kie/workbench/common/dmn/client/editors/types/listview/constraint/common/typed/time/picker/TimePickerView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(TimePickerView_HTMLAnchorElement_decreaseMinutes(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "decrease-minutes");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.time.picker.TimePickerView", "org/kie/workbench/common/dmn/client/editors/types/listview/constraint/common/typed/time/picker/TimePickerView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(TimePickerView_HTMLAnchorElement_increaseSeconds(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "increase-seconds");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.time.picker.TimePickerView", "org/kie/workbench/common/dmn/client/editors/types/listview/constraint/common/typed/time/picker/TimePickerView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(TimePickerView_HTMLAnchorElement_decreaseSeconds(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "decrease-seconds");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.time.picker.TimePickerView", "org/kie/workbench/common/dmn/client/editors/types/listview/constraint/common/typed/time/picker/TimePickerView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(TimePickerView_HTMLElement_hours(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "hours");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.time.picker.TimePickerView", "org/kie/workbench/common/dmn/client/editors/types/listview/constraint/common/typed/time/picker/TimePickerView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(TimePickerView_HTMLElement_minutes(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "minutes");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.time.picker.TimePickerView", "org/kie/workbench/common/dmn/client/editors/types/listview/constraint/common/typed/time/picker/TimePickerView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(TimePickerView_HTMLElement_seconds(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "seconds");
    templateFieldsMap.put("increase-hours", ElementWrapperWidget.getWidget(TemplateUtil.asElement(TimePickerView_HTMLAnchorElement_increaseHours(instance))));
    templateFieldsMap.put("decrease-hours", ElementWrapperWidget.getWidget(TemplateUtil.asElement(TimePickerView_HTMLAnchorElement_decreaseHours(instance))));
    templateFieldsMap.put("increase-minutes", ElementWrapperWidget.getWidget(TemplateUtil.asElement(TimePickerView_HTMLAnchorElement_increaseMinutes(instance))));
    templateFieldsMap.put("decrease-minutes", ElementWrapperWidget.getWidget(TemplateUtil.asElement(TimePickerView_HTMLAnchorElement_decreaseMinutes(instance))));
    templateFieldsMap.put("increase-seconds", ElementWrapperWidget.getWidget(TemplateUtil.asElement(TimePickerView_HTMLAnchorElement_increaseSeconds(instance))));
    templateFieldsMap.put("decrease-seconds", ElementWrapperWidget.getWidget(TemplateUtil.asElement(TimePickerView_HTMLAnchorElement_decreaseSeconds(instance))));
    templateFieldsMap.put("hours", ElementWrapperWidget.getWidget(TemplateUtil.asElement(TimePickerView_HTMLElement_hours(instance))));
    templateFieldsMap.put("minutes", ElementWrapperWidget.getWidget(TemplateUtil.asElement(TimePickerView_HTMLElement_minutes(instance))));
    templateFieldsMap.put("seconds", ElementWrapperWidget.getWidget(TemplateUtil.asElement(TimePickerView_HTMLElement_seconds(instance))));
    TemplateUtil.initTemplated(instance, TemplateUtil.getRootTemplateElement(parentElementForTemplateOfTimePickerView), templateFieldsMap.values());
    TemplateUtil.setupWrappedElementEventHandler(templateFieldsMap.get("increase-seconds"), new ClickHandler() {
      public void onClick(ClickEvent event) {
        instance.onIncreaseSecondsClick(event);
      }
    }, ClickEvent.getType());
    TemplateUtil.setupWrappedElementEventHandler(templateFieldsMap.get("decrease-hours"), new ClickHandler() {
      public void onClick(ClickEvent event) {
        instance.onDecreaseHoursClick(event);
      }
    }, ClickEvent.getType());
    TemplateUtil.setupWrappedElementEventHandler(templateFieldsMap.get("increase-hours"), new ClickHandler() {
      public void onClick(ClickEvent event) {
        instance.onIncreaseHoursClick(event);
      }
    }, ClickEvent.getType());
    TemplateUtil.setupWrappedElementEventHandler(templateFieldsMap.get("decrease-seconds"), new ClickHandler() {
      public void onClick(ClickEvent event) {
        instance.onDecreaseSecondsClick(event);
      }
    }, ClickEvent.getType());
    TemplateUtil.setupWrappedElementEventHandler(templateFieldsMap.get("increase-minutes"), new ClickHandler() {
      public void onClick(ClickEvent event) {
        instance.onIncreaseMinutesClick(event);
      }
    }, ClickEvent.getType());
    TemplateUtil.setupWrappedElementEventHandler(templateFieldsMap.get("decrease-minutes"), new ClickHandler() {
      public void onClick(ClickEvent event) {
        instance.onDecreaseMinutesClick(event);
      }
    }, ClickEvent.getType());
    StyleBindingsRegistry.get().updateStyles(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((TimePickerView) instance, contextManager);
  }

  public void destroyInstanceHelper(final TimePickerView instance, final ContextManager contextManager) {
    TemplateUtil.cleanupTemplated(instance);
  }

  native static HTMLAnchorElement TimePickerView_HTMLAnchorElement_decreaseSeconds(TimePickerView instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.time.picker.TimePickerView::decreaseSeconds;
  }-*/;

  native static void TimePickerView_HTMLAnchorElement_decreaseSeconds(TimePickerView instance, HTMLAnchorElement value) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.time.picker.TimePickerView::decreaseSeconds = value;
  }-*/;

  native static HTMLAnchorElement TimePickerView_HTMLAnchorElement_decreaseMinutes(TimePickerView instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.time.picker.TimePickerView::decreaseMinutes;
  }-*/;

  native static void TimePickerView_HTMLAnchorElement_decreaseMinutes(TimePickerView instance, HTMLAnchorElement value) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.time.picker.TimePickerView::decreaseMinutes = value;
  }-*/;

  native static HTMLElement TimePickerView_HTMLElement_minutes(TimePickerView instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.time.picker.TimePickerView::minutes;
  }-*/;

  native static void TimePickerView_HTMLElement_minutes(TimePickerView instance, HTMLElement value) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.time.picker.TimePickerView::minutes = value;
  }-*/;

  native static HTMLElement TimePickerView_HTMLElement_seconds(TimePickerView instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.time.picker.TimePickerView::seconds;
  }-*/;

  native static void TimePickerView_HTMLElement_seconds(TimePickerView instance, HTMLElement value) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.time.picker.TimePickerView::seconds = value;
  }-*/;

  native static HTMLElement TimePickerView_HTMLElement_hours(TimePickerView instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.time.picker.TimePickerView::hours;
  }-*/;

  native static void TimePickerView_HTMLElement_hours(TimePickerView instance, HTMLElement value) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.time.picker.TimePickerView::hours = value;
  }-*/;

  native static HTMLAnchorElement TimePickerView_HTMLAnchorElement_increaseHours(TimePickerView instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.time.picker.TimePickerView::increaseHours;
  }-*/;

  native static void TimePickerView_HTMLAnchorElement_increaseHours(TimePickerView instance, HTMLAnchorElement value) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.time.picker.TimePickerView::increaseHours = value;
  }-*/;

  native static HTMLAnchorElement TimePickerView_HTMLAnchorElement_increaseMinutes(TimePickerView instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.time.picker.TimePickerView::increaseMinutes;
  }-*/;

  native static void TimePickerView_HTMLAnchorElement_increaseMinutes(TimePickerView instance, HTMLAnchorElement value) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.time.picker.TimePickerView::increaseMinutes = value;
  }-*/;

  native static HTMLAnchorElement TimePickerView_HTMLAnchorElement_increaseSeconds(TimePickerView instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.time.picker.TimePickerView::increaseSeconds;
  }-*/;

  native static void TimePickerView_HTMLAnchorElement_increaseSeconds(TimePickerView instance, HTMLAnchorElement value) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.time.picker.TimePickerView::increaseSeconds = value;
  }-*/;

  native static HTMLAnchorElement TimePickerView_HTMLAnchorElement_decreaseHours(TimePickerView instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.time.picker.TimePickerView::decreaseHours;
  }-*/;

  native static void TimePickerView_HTMLAnchorElement_decreaseHours(TimePickerView instance, HTMLAnchorElement value) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.time.picker.TimePickerView::decreaseHours = value;
  }-*/;
}
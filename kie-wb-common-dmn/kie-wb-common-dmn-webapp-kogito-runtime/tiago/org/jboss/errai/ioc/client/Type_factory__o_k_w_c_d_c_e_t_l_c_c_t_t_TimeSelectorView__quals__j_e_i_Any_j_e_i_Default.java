package org.jboss.errai.ioc.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.StyleInjector;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ClientBundle.Source;
import com.google.gwt.resources.client.TextResource;
import com.google.gwt.user.client.ui.Widget;
import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLInputElement;
import elemental2.dom.HTMLOptionElement;
import elemental2.dom.HTMLSelectElement;
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
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.time.TimeSelector.View;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.time.TimeSelectorView;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.time.picker.TimePicker;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.time.picker.TimeValueFormatter;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.time.picker.TimeZoneProvider;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;
import org.uberfire.client.mvp.HasPresenter;
import org.uberfire.client.mvp.UberElemental;

public class Type_factory__o_k_w_c_d_c_e_t_l_c_c_t_t_TimeSelectorView__quals__j_e_i_Any_j_e_i_Default extends Factory<TimeSelectorView> { public interface o_k_w_c_d_c_e_t_l_c_c_t_t_TimeSelectorViewTemplateResource extends Template, ClientBundle { @Source("org/kie/workbench/common/dmn/client/editors/types/listview/constraint/common/typed/time/TimeSelectorView.html") public TextResource getContents(); }
  public Type_factory__o_k_w_c_d_c_e_t_l_c_c_t_t_TimeSelectorView__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(TimeSelectorView.class, "Type_factory__o_k_w_c_d_c_e_t_l_c_c_t_t_TimeSelectorView__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { TimeSelectorView.class, Object.class, View.class, UberElemental.class, IsElement.class, HasPresenter.class, org.jboss.errai.ui.client.local.api.elemental2.IsElement.class });
  }

  public void init(final Context context) {
    StyleInjector.inject("/*\n * Copyright 2019 Red Hat, Inc. and/or its affiliates.\n *\n * Licensed under the Apache License, Version 2.0 (the \"License\");\n * you may not use this file except in compliance with the License.\n * You may obtain a copy of the License at\n *\n *     http://www.apache.org/licenses/LICENSE-2.0\n *\n * Unless required by applicable law or agreed to in writing, software\n * distributed under the License is distributed on an \"AS IS\" BASIS,\n * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n * See the License for the specific language governing permissions and\n * limitations under the License.\n */\n[data-i18n-prefix=\"TimeSelectorView.\"] .dropdown-toggle {\n  width: 160px;\n}\n[data-i18n-prefix=\"TimeSelectorView.\"] .btn-group {\n  width: 160px;\n  display: inline-block !important;\n}\n[data-i18n-prefix=\"TimeSelectorView.\"] .bootstrap-select {\n  width: 160px;\n  display: inline-block !important;\n}\n[data-i18n-prefix=\"TimeSelectorView.\"] .toggle-icon {\n  margin: 0 4px 0 4px;\n  padding: 0;\n  color: #0088ce;\n}\n[data-i18n-prefix=\"TimeSelectorView.\"] .selector-container {\n  width: 160px;\n  display: inline-block;\n  vertical-align: top;\n}\n[data-i18n-prefix=\"TimeSelectorView.\"] .btn-toggle-timezone {\n  margin-left: 5px;\n  padding: 2px 0px;\n  font-size: 16px;\n  line-height: 16px;\n  display: inline-block;\n}\n[data-i18n-prefix=\"TimeSelectorView.\"] .time-zone-selector {\n  width: 160px;\n  display: inline-block;\n}\n[data-i18n-prefix=\"TimeSelectorView.\"] .time-input-field {\n  width: 100px;\n}\n\n");
  }

  public TimeSelectorView createInstance(final ContextManager contextManager) {
    final HTMLInputElement _timeInput_0 = (HTMLInputElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLInputElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final HTMLOptionElement _typeSelectOption_8 = (HTMLOptionElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLOptionElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final TimeValueFormatter _formatter_3 = (TimeValueFormatter) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_t_l_c_c_t_t_p_TimeValueFormatter__quals__j_e_i_Any_j_e_i_Default");
    final TimePicker _picker_1 = (TimePicker) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_t_l_c_c_t_t_p_TimePicker__quals__j_e_i_Any_j_e_i_Default");
    final TimeZoneProvider _timeZoneProvider_2 = (TimeZoneProvider) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_t_l_c_c_t_t_p_TimeZoneProvider__quals__j_e_i_Any_j_e_i_Default");
    final HTMLElement _toggleTimeZoneIcon_4 = (HTMLElement) contextManager.getContextualInstance("ContextualProvider_factory__e_d_HTMLElement__quals__Universal", new Class[] { }, new Annotation[] { new Named() {
        public Class annotationType() {
          return Named.class;
        }
        public String toString() {
          return "@javax.inject.Named(value=i)";
        }
        public String value() {
          return "i";
        }
    } });
    final ClientTranslationService _translationService_6 = (ClientTranslationService) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_i_ClientTranslationService__quals__j_e_i_Any_j_e_i_Default");
    final HTMLButtonElement _toggleTimeZoneButton_5 = (HTMLButtonElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLButtonElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final HTMLSelectElement _timeZoneSelector_7 = (HTMLSelectElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLSelectElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final TimeSelectorView instance = new TimeSelectorView(_timeInput_0, _picker_1, _timeZoneProvider_2, _formatter_3, _toggleTimeZoneIcon_4, _toggleTimeZoneButton_5, _translationService_6, _timeZoneSelector_7, _typeSelectOption_8);
    registerDependentScopedReference(instance, _timeInput_0);
    registerDependentScopedReference(instance, _typeSelectOption_8);
    registerDependentScopedReference(instance, _formatter_3);
    registerDependentScopedReference(instance, _picker_1);
    registerDependentScopedReference(instance, _toggleTimeZoneIcon_4);
    registerDependentScopedReference(instance, _toggleTimeZoneButton_5);
    registerDependentScopedReference(instance, _timeZoneSelector_7);
    setIncompleteInstance(instance);
    o_k_w_c_d_c_e_t_l_c_c_t_t_TimeSelectorViewTemplateResource templateForTimeSelectorView = GWT.create(o_k_w_c_d_c_e_t_l_c_c_t_t_TimeSelectorViewTemplateResource.class);
    Element parentElementForTemplateOfTimeSelectorView = TemplateUtil.getRootTemplateParentElement(templateForTimeSelectorView.getContents().getText(), "org/kie/workbench/common/dmn/client/editors/types/listview/constraint/common/typed/time/TimeSelectorView.html", "");
    TemplateUtil.translateTemplate("org/kie/workbench/common/dmn/client/editors/types/listview/constraint/common/typed/time/TimeSelectorView.html", TemplateUtil.getRootTemplateElement(parentElementForTemplateOfTimeSelectorView));
    Map<String, Element> dataFieldElements = TemplateUtil.getDataFieldElements(TemplateUtil.getRootTemplateElement(parentElementForTemplateOfTimeSelectorView));
    final Map<String, DataFieldMeta> dataFieldMetas = new HashMap<String, DataFieldMeta>(5);
    dataFieldMetas.put("toggle-timezone-button", new DataFieldMeta());
    dataFieldMetas.put("toggle-timezone-icon", new DataFieldMeta());
    dataFieldMetas.put("time-input", new DataFieldMeta());
    dataFieldMetas.put("time-zone-selector", new DataFieldMeta());
    dataFieldMetas.put("time-zone-select-option", new DataFieldMeta());
    Map<String, Widget> templateFieldsMap = new LinkedHashMap<String, Widget>();
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.time.TimeSelectorView", "org/kie/workbench/common/dmn/client/editors/types/listview/constraint/common/typed/time/TimeSelectorView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(TimeSelectorView_HTMLButtonElement_toggleTimeZoneButton(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "toggle-timezone-button");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.time.TimeSelectorView", "org/kie/workbench/common/dmn/client/editors/types/listview/constraint/common/typed/time/TimeSelectorView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(TimeSelectorView_HTMLElement_toggleTimeZoneIcon(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "toggle-timezone-icon");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.time.TimeSelectorView", "org/kie/workbench/common/dmn/client/editors/types/listview/constraint/common/typed/time/TimeSelectorView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(TimeSelectorView_HTMLInputElement_timeInput(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "time-input");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.time.TimeSelectorView", "org/kie/workbench/common/dmn/client/editors/types/listview/constraint/common/typed/time/TimeSelectorView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(TimeSelectorView_HTMLSelectElement_timeZoneSelector(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "time-zone-selector");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.time.TimeSelectorView", "org/kie/workbench/common/dmn/client/editors/types/listview/constraint/common/typed/time/TimeSelectorView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(TimeSelectorView_HTMLOptionElement_typeSelectOption(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "time-zone-select-option");
    templateFieldsMap.put("toggle-timezone-button", ElementWrapperWidget.getWidget(TemplateUtil.asElement(TimeSelectorView_HTMLButtonElement_toggleTimeZoneButton(instance))));
    templateFieldsMap.put("toggle-timezone-icon", ElementWrapperWidget.getWidget(TemplateUtil.asElement(TimeSelectorView_HTMLElement_toggleTimeZoneIcon(instance))));
    templateFieldsMap.put("time-input", ElementWrapperWidget.getWidget(TemplateUtil.asElement(TimeSelectorView_HTMLInputElement_timeInput(instance))));
    templateFieldsMap.put("time-zone-selector", ElementWrapperWidget.getWidget(TemplateUtil.asElement(TimeSelectorView_HTMLSelectElement_timeZoneSelector(instance))));
    templateFieldsMap.put("time-zone-select-option", ElementWrapperWidget.getWidget(TemplateUtil.asElement(TimeSelectorView_HTMLOptionElement_typeSelectOption(instance))));
    TemplateUtil.initTemplated(instance, TemplateUtil.getRootTemplateElement(parentElementForTemplateOfTimeSelectorView), templateFieldsMap.values());
    TemplateUtil.setupWrappedElementEventHandler(templateFieldsMap.get("toggle-timezone-button"), new ClickHandler() {
      public void onClick(ClickEvent event) {
        instance.onToggleTimeZoneButtonClick(event);
      }
    }, ClickEvent.getType());
    TemplateUtil.setupWrappedElementEventHandler(templateFieldsMap.get("time-input"), new BlurHandler() {
      public void onBlur(BlurEvent event) {
        instance.onTimeInputBlur(event);
      }
    }, BlurEvent.getType());
    StyleBindingsRegistry.get().updateStyles(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((TimeSelectorView) instance, contextManager);
  }

  public void destroyInstanceHelper(final TimeSelectorView instance, final ContextManager contextManager) {
    TemplateUtil.cleanupTemplated(instance);
  }

  public void invokePostConstructs(final TimeSelectorView instance) {
    TimeSelectorView_init(instance);
  }

  native static HTMLOptionElement TimeSelectorView_HTMLOptionElement_typeSelectOption(TimeSelectorView instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.time.TimeSelectorView::typeSelectOption;
  }-*/;

  native static void TimeSelectorView_HTMLOptionElement_typeSelectOption(TimeSelectorView instance, HTMLOptionElement value) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.time.TimeSelectorView::typeSelectOption = value;
  }-*/;

  native static HTMLInputElement TimeSelectorView_HTMLInputElement_timeInput(TimeSelectorView instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.time.TimeSelectorView::timeInput;
  }-*/;

  native static void TimeSelectorView_HTMLInputElement_timeInput(TimeSelectorView instance, HTMLInputElement value) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.time.TimeSelectorView::timeInput = value;
  }-*/;

  native static HTMLElement TimeSelectorView_HTMLElement_toggleTimeZoneIcon(TimeSelectorView instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.time.TimeSelectorView::toggleTimeZoneIcon;
  }-*/;

  native static void TimeSelectorView_HTMLElement_toggleTimeZoneIcon(TimeSelectorView instance, HTMLElement value) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.time.TimeSelectorView::toggleTimeZoneIcon = value;
  }-*/;

  native static HTMLSelectElement TimeSelectorView_HTMLSelectElement_timeZoneSelector(TimeSelectorView instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.time.TimeSelectorView::timeZoneSelector;
  }-*/;

  native static void TimeSelectorView_HTMLSelectElement_timeZoneSelector(TimeSelectorView instance, HTMLSelectElement value) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.time.TimeSelectorView::timeZoneSelector = value;
  }-*/;

  native static HTMLButtonElement TimeSelectorView_HTMLButtonElement_toggleTimeZoneButton(TimeSelectorView instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.time.TimeSelectorView::toggleTimeZoneButton;
  }-*/;

  native static void TimeSelectorView_HTMLButtonElement_toggleTimeZoneButton(TimeSelectorView instance, HTMLButtonElement value) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.time.TimeSelectorView::toggleTimeZoneButton = value;
  }-*/;

  public native static void TimeSelectorView_init(TimeSelectorView instance) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.time.TimeSelectorView::init()();
  }-*/;
}
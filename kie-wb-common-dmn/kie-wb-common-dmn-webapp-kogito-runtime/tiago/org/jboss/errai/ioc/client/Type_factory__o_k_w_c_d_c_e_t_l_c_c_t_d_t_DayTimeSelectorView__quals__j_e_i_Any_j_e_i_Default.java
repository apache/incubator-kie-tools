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
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.day.time.DayTimeSelector.View;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.day.time.DayTimeSelectorView;
import org.uberfire.client.mvp.HasPresenter;
import org.uberfire.client.mvp.UberElemental;

public class Type_factory__o_k_w_c_d_c_e_t_l_c_c_t_d_t_DayTimeSelectorView__quals__j_e_i_Any_j_e_i_Default extends Factory<DayTimeSelectorView> { public interface o_k_w_c_d_c_e_t_l_c_c_t_d_t_DayTimeSelectorViewTemplateResource extends Template, ClientBundle { @Source("org/kie/workbench/common/dmn/client/editors/types/listview/constraint/common/typed/day/time/DayTimeSelectorView.html") public TextResource getContents(); }
  public Type_factory__o_k_w_c_d_c_e_t_l_c_c_t_d_t_DayTimeSelectorView__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DayTimeSelectorView.class, "Type_factory__o_k_w_c_d_c_e_t_l_c_c_t_d_t_DayTimeSelectorView__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DayTimeSelectorView.class, Object.class, View.class, UberElemental.class, IsElement.class, HasPresenter.class, org.jboss.errai.ui.client.local.api.elemental2.IsElement.class });
  }

  public void init(final Context context) {
    StyleInjector.inject("/*\n * Copyright 2019 Red Hat, Inc. and/or its affiliates.\n *\n * Licensed under the Apache License, Version 2.0 (the \"License\");\n * you may not use this file except in compliance with the License.\n * You may obtain a copy of the License at\n *\n *     http://www.apache.org/licenses/LICENSE-2.0\n *\n * Unless required by applicable law or agreed to in writing, software\n * distributed under the License is distributed on an \"AS IS\" BASIS,\n * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n * See the License for the specific language governing permissions and\n * limitations under the License.\n */\n[data-i18n-prefix=\"DayTimeSelectorView.\"] > div {\n  width: 80px;\n  display: inline-block;\n}\n[data-i18n-prefix=\"DayTimeSelectorView.\"] > div label {\n  margin: 0;\n}\n[data-i18n-prefix=\"DayTimeSelectorView.\"] > div span {\n  opacity: .75;\n}\n[data-i18n-prefix=\"DayTimeSelectorView.\"] > div input.input-field {\n  width: 70px;\n  margin: 0;\n}\n\n");
  }

  public DayTimeSelectorView createInstance(final ContextManager contextManager) {
    final HTMLInputElement _daysInput_0 = (HTMLInputElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLInputElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final HTMLInputElement _secondsInput_3 = (HTMLInputElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLInputElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final HTMLInputElement _minutesInput_2 = (HTMLInputElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLInputElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final HTMLInputElement _hoursInput_1 = (HTMLInputElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLInputElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final DayTimeSelectorView instance = new DayTimeSelectorView(_daysInput_0, _hoursInput_1, _minutesInput_2, _secondsInput_3);
    registerDependentScopedReference(instance, _daysInput_0);
    registerDependentScopedReference(instance, _secondsInput_3);
    registerDependentScopedReference(instance, _minutesInput_2);
    registerDependentScopedReference(instance, _hoursInput_1);
    setIncompleteInstance(instance);
    o_k_w_c_d_c_e_t_l_c_c_t_d_t_DayTimeSelectorViewTemplateResource templateForDayTimeSelectorView = GWT.create(o_k_w_c_d_c_e_t_l_c_c_t_d_t_DayTimeSelectorViewTemplateResource.class);
    Element parentElementForTemplateOfDayTimeSelectorView = TemplateUtil.getRootTemplateParentElement(templateForDayTimeSelectorView.getContents().getText(), "org/kie/workbench/common/dmn/client/editors/types/listview/constraint/common/typed/day/time/DayTimeSelectorView.html", "");
    TemplateUtil.translateTemplate("org/kie/workbench/common/dmn/client/editors/types/listview/constraint/common/typed/day/time/DayTimeSelectorView.html", TemplateUtil.getRootTemplateElement(parentElementForTemplateOfDayTimeSelectorView));
    Map<String, Element> dataFieldElements = TemplateUtil.getDataFieldElements(TemplateUtil.getRootTemplateElement(parentElementForTemplateOfDayTimeSelectorView));
    final Map<String, DataFieldMeta> dataFieldMetas = new HashMap<String, DataFieldMeta>(4);
    dataFieldMetas.put("days-input", new DataFieldMeta());
    dataFieldMetas.put("hours-input", new DataFieldMeta());
    dataFieldMetas.put("minutes-input", new DataFieldMeta());
    dataFieldMetas.put("seconds-input", new DataFieldMeta());
    Map<String, Widget> templateFieldsMap = new LinkedHashMap<String, Widget>();
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.day.time.DayTimeSelectorView", "org/kie/workbench/common/dmn/client/editors/types/listview/constraint/common/typed/day/time/DayTimeSelectorView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(DayTimeSelectorView_HTMLInputElement_daysInput(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "days-input");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.day.time.DayTimeSelectorView", "org/kie/workbench/common/dmn/client/editors/types/listview/constraint/common/typed/day/time/DayTimeSelectorView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(DayTimeSelectorView_HTMLInputElement_hoursInput(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "hours-input");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.day.time.DayTimeSelectorView", "org/kie/workbench/common/dmn/client/editors/types/listview/constraint/common/typed/day/time/DayTimeSelectorView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(DayTimeSelectorView_HTMLInputElement_minutesInput(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "minutes-input");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.day.time.DayTimeSelectorView", "org/kie/workbench/common/dmn/client/editors/types/listview/constraint/common/typed/day/time/DayTimeSelectorView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(DayTimeSelectorView_HTMLInputElement_secondsInput(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "seconds-input");
    templateFieldsMap.put("days-input", ElementWrapperWidget.getWidget(TemplateUtil.asElement(DayTimeSelectorView_HTMLInputElement_daysInput(instance))));
    templateFieldsMap.put("hours-input", ElementWrapperWidget.getWidget(TemplateUtil.asElement(DayTimeSelectorView_HTMLInputElement_hoursInput(instance))));
    templateFieldsMap.put("minutes-input", ElementWrapperWidget.getWidget(TemplateUtil.asElement(DayTimeSelectorView_HTMLInputElement_minutesInput(instance))));
    templateFieldsMap.put("seconds-input", ElementWrapperWidget.getWidget(TemplateUtil.asElement(DayTimeSelectorView_HTMLInputElement_secondsInput(instance))));
    TemplateUtil.initTemplated(instance, TemplateUtil.getRootTemplateElement(parentElementForTemplateOfDayTimeSelectorView), templateFieldsMap.values());
    TemplateUtil.setupWrappedElementEventHandler(templateFieldsMap.get("minutes-input"), new BlurHandler() {
      public void onBlur(BlurEvent event) {
        instance.onMinutesInputBlurEvent(event);
      }
    }, BlurEvent.getType());
    TemplateUtil.setupWrappedElementEventHandler(templateFieldsMap.get("hours-input"), new BlurHandler() {
      public void onBlur(BlurEvent event) {
        instance.onHoursInputBlurEvent(event);
      }
    }, BlurEvent.getType());
    TemplateUtil.setupWrappedElementEventHandler(templateFieldsMap.get("days-input"), new BlurHandler() {
      public void onBlur(BlurEvent event) {
        instance.onDaysInputBlurEvent(event);
      }
    }, BlurEvent.getType());
    TemplateUtil.setupWrappedElementEventHandler(templateFieldsMap.get("seconds-input"), new BlurHandler() {
      public void onBlur(BlurEvent event) {
        instance.onSecondsInputBlurEvent(event);
      }
    }, BlurEvent.getType());
    StyleBindingsRegistry.get().updateStyles(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((DayTimeSelectorView) instance, contextManager);
  }

  public void destroyInstanceHelper(final DayTimeSelectorView instance, final ContextManager contextManager) {
    TemplateUtil.cleanupTemplated(instance);
  }

  public void invokePostConstructs(final DayTimeSelectorView instance) {
    instance.setupEventHandlers();
  }

  native static HTMLInputElement DayTimeSelectorView_HTMLInputElement_secondsInput(DayTimeSelectorView instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.day.time.DayTimeSelectorView::secondsInput;
  }-*/;

  native static void DayTimeSelectorView_HTMLInputElement_secondsInput(DayTimeSelectorView instance, HTMLInputElement value) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.day.time.DayTimeSelectorView::secondsInput = value;
  }-*/;

  native static HTMLInputElement DayTimeSelectorView_HTMLInputElement_daysInput(DayTimeSelectorView instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.day.time.DayTimeSelectorView::daysInput;
  }-*/;

  native static void DayTimeSelectorView_HTMLInputElement_daysInput(DayTimeSelectorView instance, HTMLInputElement value) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.day.time.DayTimeSelectorView::daysInput = value;
  }-*/;

  native static HTMLInputElement DayTimeSelectorView_HTMLInputElement_minutesInput(DayTimeSelectorView instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.day.time.DayTimeSelectorView::minutesInput;
  }-*/;

  native static void DayTimeSelectorView_HTMLInputElement_minutesInput(DayTimeSelectorView instance, HTMLInputElement value) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.day.time.DayTimeSelectorView::minutesInput = value;
  }-*/;

  native static HTMLInputElement DayTimeSelectorView_HTMLInputElement_hoursInput(DayTimeSelectorView instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.day.time.DayTimeSelectorView::hoursInput;
  }-*/;

  native static void DayTimeSelectorView_HTMLInputElement_hoursInput(DayTimeSelectorView instance, HTMLInputElement value) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.day.time.DayTimeSelectorView::hoursInput = value;
  }-*/;
}
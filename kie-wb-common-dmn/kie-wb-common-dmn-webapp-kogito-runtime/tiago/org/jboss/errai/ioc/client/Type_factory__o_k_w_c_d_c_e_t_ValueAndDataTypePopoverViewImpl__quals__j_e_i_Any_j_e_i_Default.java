package org.jboss.errai.ioc.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.StyleInjector;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ClientBundle.Source;
import com.google.gwt.resources.client.TextResource;
import com.google.gwt.user.client.ui.Widget;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;
import javax.enterprise.context.Dependent;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.Input;
import org.jboss.errai.common.client.dom.Span;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.DataFieldMeta;
import org.jboss.errai.ui.shared.Template;
import org.jboss.errai.ui.shared.TemplateUtil;
import org.jboss.errai.ui.shared.api.style.StyleBindingsRegistry;
import org.kie.workbench.common.dmn.client.editors.types.CanBeClosedByKeyboard;
import org.kie.workbench.common.dmn.client.editors.types.DataTypePickerWidget;
import org.kie.workbench.common.dmn.client.editors.types.ValueAndDataTypePopoverView;
import org.kie.workbench.common.dmn.client.editors.types.ValueAndDataTypePopoverViewImpl;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.popover.AbstractPopoverViewImpl;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.popover.PopoverView;
import org.uberfire.client.mvp.HasPresenter;
import org.uberfire.client.mvp.UberElement;
import org.uberfire.client.views.pfly.widgets.JQueryProducer.JQuery;
import org.uberfire.client.views.pfly.widgets.Popover;

public class Type_factory__o_k_w_c_d_c_e_t_ValueAndDataTypePopoverViewImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<ValueAndDataTypePopoverViewImpl> { public interface o_k_w_c_d_c_e_t_ValueAndDataTypePopoverViewImplTemplateResource extends Template, ClientBundle { @Source("org/kie/workbench/common/dmn/client/editors/types/ValueAndDataTypePopoverViewImpl.html") public TextResource getContents(); }
  public Type_factory__o_k_w_c_d_c_e_t_ValueAndDataTypePopoverViewImpl__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(ValueAndDataTypePopoverViewImpl.class, "Type_factory__o_k_w_c_d_c_e_t_ValueAndDataTypePopoverViewImpl__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { ValueAndDataTypePopoverViewImpl.class, AbstractPopoverViewImpl.class, Object.class, PopoverView.class, IsElement.class, org.jboss.errai.common.client.api.IsElement.class, CanBeClosedByKeyboard.class, ValueAndDataTypePopoverView.class, UberElement.class, HasPresenter.class });
  }

  public void init(final Context context) {
    StyleInjector.inject("/*\n * Copyright 2018 Red Hat, Inc. and/or its affiliates.\n *\n * Licensed under the Apache License, Version 2.0 (the \"License\");\n * you may not use this file except in compliance with the License.\n * You may obtain a copy of the License at\n *\n *     http://www.apache.org/licenses/LICENSE-2.0\n *\n * Unless required by applicable law or agreed to in writing, software\n * distributed under the License is distributed on an \"AS IS\" BASIS,\n * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n * See the License for the specific language governing permissions and\n * limitations under the License.\n */\n[data-i18n-prefix=\"ValueAndDataTypePopoverViewImpl.\"] #popover-container {\n  display: inline;\n}\n[data-i18n-prefix=\"ValueAndDataTypePopoverViewImpl.\"] #popover-container .popover {\n  min-width: 300px;\n}\n[data-i18n-prefix=\"ValueAndDataTypePopoverViewImpl.\"] .kie-dmn-value-and-data-type-container #kieValue {\n  width: 100%;\n}\n[data-i18n-prefix=\"ValueAndDataTypePopoverViewImpl.\"] .kie-dmn-value-and-data-type-container #kieDataType {\n  width: 100%;\n}\n\n");
  }

  public ValueAndDataTypePopoverViewImpl createInstance(final ContextManager contextManager) {
    final Input _valueEditor_0 = (Input) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Input__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final JQuery<Popover> _jQueryPopover_6 = (JQuery) contextManager.getInstance("Producer_factory__o_u_c_v_p_w_JQueryProducer_JQuery__quals__j_e_i_Any_j_e_i_Default");
    final Div _popoverContentElement_3 = (Div) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Div__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final TranslationService _translationService_7 = (TranslationService) contextManager.getInstance("Provider_factory__o_j_e_u_c_l_s_TranslationService__quals__j_e_i_Any_j_e_i_Default");
    final Span _dataTypeLabel_5 = (Span) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Span__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final Span _valueLabel_4 = (Span) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Span__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final DataTypePickerWidget _typeRefEditor_1 = (DataTypePickerWidget) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_t_DataTypePickerWidget__quals__j_e_i_Any_j_e_i_Default");
    final Div _popoverElement_2 = (Div) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Div__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final ValueAndDataTypePopoverViewImpl instance = new ValueAndDataTypePopoverViewImpl(_valueEditor_0, _typeRefEditor_1, _popoverElement_2, _popoverContentElement_3, _valueLabel_4, _dataTypeLabel_5, _jQueryPopover_6, _translationService_7);
    registerDependentScopedReference(instance, _valueEditor_0);
    registerDependentScopedReference(instance, _jQueryPopover_6);
    registerDependentScopedReference(instance, _popoverContentElement_3);
    registerDependentScopedReference(instance, _translationService_7);
    registerDependentScopedReference(instance, _dataTypeLabel_5);
    registerDependentScopedReference(instance, _valueLabel_4);
    registerDependentScopedReference(instance, _typeRefEditor_1);
    registerDependentScopedReference(instance, _popoverElement_2);
    setIncompleteInstance(instance);
    o_k_w_c_d_c_e_t_ValueAndDataTypePopoverViewImplTemplateResource templateForValueAndDataTypePopoverViewImpl = GWT.create(o_k_w_c_d_c_e_t_ValueAndDataTypePopoverViewImplTemplateResource.class);
    Element parentElementForTemplateOfValueAndDataTypePopoverViewImpl = TemplateUtil.getRootTemplateParentElement(templateForValueAndDataTypePopoverViewImpl.getContents().getText(), "org/kie/workbench/common/dmn/client/editors/types/ValueAndDataTypePopoverViewImpl.html", "");
    TemplateUtil.translateTemplate("org/kie/workbench/common/dmn/client/editors/types/ValueAndDataTypePopoverViewImpl.html", TemplateUtil.getRootTemplateElement(parentElementForTemplateOfValueAndDataTypePopoverViewImpl));
    Map<String, Element> dataFieldElements = TemplateUtil.getDataFieldElements(TemplateUtil.getRootTemplateElement(parentElementForTemplateOfValueAndDataTypePopoverViewImpl));
    final Map<String, DataFieldMeta> dataFieldMetas = new HashMap<String, DataFieldMeta>(6);
    dataFieldMetas.put("popover", new DataFieldMeta());
    dataFieldMetas.put("popover-content", new DataFieldMeta());
    dataFieldMetas.put("valueEditor", new DataFieldMeta());
    dataFieldMetas.put("typeRefSelector", new DataFieldMeta());
    dataFieldMetas.put("valueLabel", new DataFieldMeta());
    dataFieldMetas.put("dataTypeLabel", new DataFieldMeta());
    Map<String, Widget> templateFieldsMap = new LinkedHashMap<String, Widget>();
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.editors.types.ValueAndDataTypePopoverViewImpl", "org/kie/workbench/common/dmn/client/editors/types/ValueAndDataTypePopoverViewImpl.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(AbstractPopoverViewImpl_Div_popoverElement(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "popover");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.editors.types.ValueAndDataTypePopoverViewImpl", "org/kie/workbench/common/dmn/client/editors/types/ValueAndDataTypePopoverViewImpl.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(AbstractPopoverViewImpl_Div_popoverContentElement(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "popover-content");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.editors.types.ValueAndDataTypePopoverViewImpl", "org/kie/workbench/common/dmn/client/editors/types/ValueAndDataTypePopoverViewImpl.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(ValueAndDataTypePopoverViewImpl_Input_valueEditor(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "valueEditor");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.editors.types.ValueAndDataTypePopoverViewImpl", "org/kie/workbench/common/dmn/client/editors/types/ValueAndDataTypePopoverViewImpl.html", new Supplier<Widget>() {
      public Widget get() {
        return ValueAndDataTypePopoverViewImpl_DataTypePickerWidget_typeRefEditor(instance).asWidget();
      }
    }, dataFieldElements, dataFieldMetas, "typeRefSelector");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.editors.types.ValueAndDataTypePopoverViewImpl", "org/kie/workbench/common/dmn/client/editors/types/ValueAndDataTypePopoverViewImpl.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(ValueAndDataTypePopoverViewImpl_Span_valueLabel(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "valueLabel");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.editors.types.ValueAndDataTypePopoverViewImpl", "org/kie/workbench/common/dmn/client/editors/types/ValueAndDataTypePopoverViewImpl.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(ValueAndDataTypePopoverViewImpl_Span_dataTypeLabel(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "dataTypeLabel");
    templateFieldsMap.put("popover", ElementWrapperWidget.getWidget(TemplateUtil.asElement(AbstractPopoverViewImpl_Div_popoverElement(instance))));
    templateFieldsMap.put("popover-content", ElementWrapperWidget.getWidget(TemplateUtil.asElement(AbstractPopoverViewImpl_Div_popoverContentElement(instance))));
    templateFieldsMap.put("valueEditor", ElementWrapperWidget.getWidget(TemplateUtil.asElement(ValueAndDataTypePopoverViewImpl_Input_valueEditor(instance))));
    templateFieldsMap.put("typeRefSelector", ValueAndDataTypePopoverViewImpl_DataTypePickerWidget_typeRefEditor(instance).asWidget());
    templateFieldsMap.put("valueLabel", ElementWrapperWidget.getWidget(TemplateUtil.asElement(ValueAndDataTypePopoverViewImpl_Span_valueLabel(instance))));
    templateFieldsMap.put("dataTypeLabel", ElementWrapperWidget.getWidget(TemplateUtil.asElement(ValueAndDataTypePopoverViewImpl_Span_dataTypeLabel(instance))));
    TemplateUtil.initTemplated(instance, TemplateUtil.getRootTemplateElement(parentElementForTemplateOfValueAndDataTypePopoverViewImpl), templateFieldsMap.values());
    TemplateUtil.setupWrappedElementEventHandler(templateFieldsMap.get("valueEditor"), new BlurHandler() {
      public void onBlur(BlurEvent event) {
        ValueAndDataTypePopoverViewImpl_onValueChange_BlurEvent(instance, event);
      }
    }, BlurEvent.getType());
    TemplateUtil.setupWrappedElementEventHandler(templateFieldsMap.get("valueEditor"), new KeyDownHandler() {
      public void onKeyDown(KeyDownEvent event) {
        instance.onValueEditorKeyDown(event);
      }
    }, KeyDownEvent.getType());
    StyleBindingsRegistry.get().updateStyles(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((ValueAndDataTypePopoverViewImpl) instance, contextManager);
  }

  public void destroyInstanceHelper(final ValueAndDataTypePopoverViewImpl instance, final ContextManager contextManager) {
    TemplateUtil.cleanupTemplated(instance);
  }

  native static Div AbstractPopoverViewImpl_Div_popoverElement(AbstractPopoverViewImpl instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.widgets.grid.controls.popover.AbstractPopoverViewImpl::popoverElement;
  }-*/;

  native static void AbstractPopoverViewImpl_Div_popoverElement(AbstractPopoverViewImpl instance, Div value) /*-{
    instance.@org.kie.workbench.common.dmn.client.widgets.grid.controls.popover.AbstractPopoverViewImpl::popoverElement = value;
  }-*/;

  native static Span ValueAndDataTypePopoverViewImpl_Span_dataTypeLabel(ValueAndDataTypePopoverViewImpl instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.editors.types.ValueAndDataTypePopoverViewImpl::dataTypeLabel;
  }-*/;

  native static void ValueAndDataTypePopoverViewImpl_Span_dataTypeLabel(ValueAndDataTypePopoverViewImpl instance, Span value) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.types.ValueAndDataTypePopoverViewImpl::dataTypeLabel = value;
  }-*/;

  native static Input ValueAndDataTypePopoverViewImpl_Input_valueEditor(ValueAndDataTypePopoverViewImpl instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.editors.types.ValueAndDataTypePopoverViewImpl::valueEditor;
  }-*/;

  native static void ValueAndDataTypePopoverViewImpl_Input_valueEditor(ValueAndDataTypePopoverViewImpl instance, Input value) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.types.ValueAndDataTypePopoverViewImpl::valueEditor = value;
  }-*/;

  native static Span ValueAndDataTypePopoverViewImpl_Span_valueLabel(ValueAndDataTypePopoverViewImpl instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.editors.types.ValueAndDataTypePopoverViewImpl::valueLabel;
  }-*/;

  native static void ValueAndDataTypePopoverViewImpl_Span_valueLabel(ValueAndDataTypePopoverViewImpl instance, Span value) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.types.ValueAndDataTypePopoverViewImpl::valueLabel = value;
  }-*/;

  native static Div AbstractPopoverViewImpl_Div_popoverContentElement(AbstractPopoverViewImpl instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.widgets.grid.controls.popover.AbstractPopoverViewImpl::popoverContentElement;
  }-*/;

  native static void AbstractPopoverViewImpl_Div_popoverContentElement(AbstractPopoverViewImpl instance, Div value) /*-{
    instance.@org.kie.workbench.common.dmn.client.widgets.grid.controls.popover.AbstractPopoverViewImpl::popoverContentElement = value;
  }-*/;

  native static DataTypePickerWidget ValueAndDataTypePopoverViewImpl_DataTypePickerWidget_typeRefEditor(ValueAndDataTypePopoverViewImpl instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.editors.types.ValueAndDataTypePopoverViewImpl::typeRefEditor;
  }-*/;

  native static void ValueAndDataTypePopoverViewImpl_DataTypePickerWidget_typeRefEditor(ValueAndDataTypePopoverViewImpl instance, DataTypePickerWidget value) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.types.ValueAndDataTypePopoverViewImpl::typeRefEditor = value;
  }-*/;

  public native static void ValueAndDataTypePopoverViewImpl_onValueChange_BlurEvent(ValueAndDataTypePopoverViewImpl instance, BlurEvent a0) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.types.ValueAndDataTypePopoverViewImpl::onValueChange(Lcom/google/gwt/event/dom/client/BlurEvent;)(a0);
  }-*/;
}
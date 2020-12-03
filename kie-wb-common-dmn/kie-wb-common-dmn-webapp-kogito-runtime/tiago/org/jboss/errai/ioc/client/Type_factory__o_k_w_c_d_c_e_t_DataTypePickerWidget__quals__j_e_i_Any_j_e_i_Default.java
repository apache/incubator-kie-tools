package org.jboss.errai.ioc.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.StyleInjector;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.HasAttachHandlers;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.shared.HasHandlers;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ClientBundle.Source;
import com.google.gwt.resources.client.TextResource;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.TakesValue;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HasVisibility;
import com.google.gwt.user.client.ui.IsRenderable;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.Widget;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import org.gwtbootstrap3.extras.select.client.ui.Select;
import org.jboss.errai.common.client.dom.Anchor;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.Span;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.jboss.errai.enterprise.client.cdi.AbstractCDIEventCallback;
import org.jboss.errai.enterprise.client.cdi.api.CDI;
import org.jboss.errai.enterprise.client.cdi.api.Subscription;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.DataFieldMeta;
import org.jboss.errai.ui.shared.Template;
import org.jboss.errai.ui.shared.TemplateUtil;
import org.jboss.errai.ui.shared.api.style.StyleBindingsRegistry;
import org.kie.workbench.common.dmn.client.api.ReadOnlyProviderImpl;
import org.kie.workbench.common.dmn.client.editors.types.DataTypeChangedEvent;
import org.kie.workbench.common.dmn.client.editors.types.DataTypePageTabActiveEvent;
import org.kie.workbench.common.dmn.client.editors.types.DataTypePickerWidget;
import org.kie.workbench.common.dmn.client.editors.types.QNameConverter;
import org.kie.workbench.common.dmn.client.editors.types.common.ItemDefinitionUtils;
import org.kie.workbench.common.dmn.client.graph.DMNGraphUtils;
import org.kie.workbench.common.stunner.core.client.ReadOnlyProvider;

public class Type_factory__o_k_w_c_d_c_e_t_DataTypePickerWidget__quals__j_e_i_Any_j_e_i_Default extends Factory<DataTypePickerWidget> { public interface o_k_w_c_d_c_e_t_DataTypePickerWidgetTemplateResource extends Template, ClientBundle { @Source("org/kie/workbench/common/dmn/client/editors/types/DataTypePickerWidget.html") public TextResource getContents(); }
  public Type_factory__o_k_w_c_d_c_e_t_DataTypePickerWidget__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DataTypePickerWidget.class, "Type_factory__o_k_w_c_d_c_e_t_DataTypePickerWidget__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DataTypePickerWidget.class, Composite.class, Widget.class, UIObject.class, Object.class, HasVisibility.class, EventListener.class, HasAttachHandlers.class, HasHandlers.class, IsWidget.class, IsRenderable.class, HasValue.class, TakesValue.class, HasValueChangeHandlers.class, HasEnabled.class });
  }

  public void init(final Context context) {
    StyleInjector.inject("/*\n * Copyright 2018 Red Hat, Inc. and/or its affiliates.\n *\n * Licensed under the Apache License, Version 2.0 (the \"License\");\n * you may not use this file except in compliance with the License.\n * You may obtain a copy of the License at\n *\n *     http://www.apache.org/licenses/LICENSE-2.0\n *\n * Unless required by applicable law or agreed to in writing, software\n * distributed under the License is distributed on an \"AS IS\" BASIS,\n * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n * See the License for the specific language governing permissions and\n * limitations under the License.\n */\n[data-i18n-prefix=\"DataTypePickerWidget.\"] .kie-data-type-container {\n  width: 100%;\n}\n[data-i18n-prefix=\"DataTypePickerWidget.\"] .kie-data-type-container #manageContainer {\n  position: absolute;\n  top: -24px;\n  right: 0px;\n}\n[data-i18n-prefix=\"DataTypePickerWidget.\"] .read-only {\n  pointer-events: none;\n  cursor: not-allowed;\n  filter: grayscale(100%);\n}\n\n");
  }

  public DataTypePickerWidget createInstance(final ContextManager contextManager) {
    final Anchor _typeButton_0 = (Anchor) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Anchor__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final DMNGraphUtils _dmnGraphUtils_5 = (DMNGraphUtils) contextManager.getInstance("Type_factory__o_k_w_c_d_c_g_DMNGraphUtils__quals__j_e_i_Any_j_e_i_Default");
    final Event<DataTypePageTabActiveEvent> _dataTypePageActiveEvent_6 = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { DataTypePageTabActiveEvent.class }, new Annotation[] { });
    final TranslationService _translationService_3 = (TranslationService) contextManager.getInstance("Provider_factory__o_j_e_u_c_l_s_TranslationService__quals__j_e_i_Any_j_e_i_Default");
    final ItemDefinitionUtils _itemDefinitionUtils_7 = (ItemDefinitionUtils) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_t_c_ItemDefinitionUtils__quals__j_e_i_Any_j_e_i_Default");
    final Div _manageContainer_1 = (Div) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Div__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final Span _manageLabel_2 = (Span) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Span__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final ReadOnlyProvider _readOnlyProvider_8 = (ReadOnlyProviderImpl) contextManager.getInstance("Type_factory__o_k_w_c_d_c_a_ReadOnlyProviderImpl__quals__j_e_i_Any_o_k_w_c_d_a_q_DMNEditor");
    final QNameConverter _qNameConverter_4 = (QNameConverter) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_t_QNameConverter__quals__j_e_i_Any_j_e_i_Default");
    final DataTypePickerWidget instance = new DataTypePickerWidget(_typeButton_0, _manageContainer_1, _manageLabel_2, _translationService_3, _qNameConverter_4, _dmnGraphUtils_5, _dataTypePageActiveEvent_6, _itemDefinitionUtils_7, _readOnlyProvider_8);
    registerDependentScopedReference(instance, _typeButton_0);
    registerDependentScopedReference(instance, _dmnGraphUtils_5);
    registerDependentScopedReference(instance, _dataTypePageActiveEvent_6);
    registerDependentScopedReference(instance, _translationService_3);
    registerDependentScopedReference(instance, _itemDefinitionUtils_7);
    registerDependentScopedReference(instance, _manageContainer_1);
    registerDependentScopedReference(instance, _manageLabel_2);
    registerDependentScopedReference(instance, _qNameConverter_4);
    setIncompleteInstance(instance);
    thisInstance.setReference(instance, "onDataTypePageNavTabActiveEventSubscription", CDI.subscribeLocal("org.kie.workbench.common.dmn.client.editors.types.DataTypeChangedEvent", new AbstractCDIEventCallback<DataTypeChangedEvent>() {
      public void fireEvent(final DataTypeChangedEvent event) {
        instance.onDataTypePageNavTabActiveEvent(event);
      }
      public String toString() {
        return "Observer: org.kie.workbench.common.dmn.client.editors.types.DataTypeChangedEvent []";
      }
    }));
    o_k_w_c_d_c_e_t_DataTypePickerWidgetTemplateResource templateForDataTypePickerWidget = GWT.create(o_k_w_c_d_c_e_t_DataTypePickerWidgetTemplateResource.class);
    Element parentElementForTemplateOfDataTypePickerWidget = TemplateUtil.getRootTemplateParentElement(templateForDataTypePickerWidget.getContents().getText(), "org/kie/workbench/common/dmn/client/editors/types/DataTypePickerWidget.html", "");
    TemplateUtil.translateTemplate("org/kie/workbench/common/dmn/client/editors/types/DataTypePickerWidget.html", TemplateUtil.getRootTemplateElement(parentElementForTemplateOfDataTypePickerWidget));
    Map<String, Element> dataFieldElements = TemplateUtil.getDataFieldElements(TemplateUtil.getRootTemplateElement(parentElementForTemplateOfDataTypePickerWidget));
    final Map<String, DataFieldMeta> dataFieldMetas = new HashMap<String, DataFieldMeta>(4);
    dataFieldMetas.put("typeButton", new DataFieldMeta());
    dataFieldMetas.put("manageContainer", new DataFieldMeta());
    dataFieldMetas.put("manageLabel", new DataFieldMeta());
    dataFieldMetas.put("typeSelector", new DataFieldMeta());
    Map<String, Widget> templateFieldsMap = new LinkedHashMap<String, Widget>();
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.editors.types.DataTypePickerWidget", "org/kie/workbench/common/dmn/client/editors/types/DataTypePickerWidget.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(DataTypePickerWidget_Anchor_typeButton(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "typeButton");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.editors.types.DataTypePickerWidget", "org/kie/workbench/common/dmn/client/editors/types/DataTypePickerWidget.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(DataTypePickerWidget_Div_manageContainer(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "manageContainer");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.editors.types.DataTypePickerWidget", "org/kie/workbench/common/dmn/client/editors/types/DataTypePickerWidget.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(DataTypePickerWidget_Span_manageLabel(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "manageLabel");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.editors.types.DataTypePickerWidget", "org/kie/workbench/common/dmn/client/editors/types/DataTypePickerWidget.html", new Supplier<Widget>() {
      public Widget get() {
        return DataTypePickerWidget_Select_typeSelector(instance).asWidget();
      }
    }, dataFieldElements, dataFieldMetas, "typeSelector");
    templateFieldsMap.put("typeButton", ElementWrapperWidget.getWidget(TemplateUtil.asElement(DataTypePickerWidget_Anchor_typeButton(instance))));
    templateFieldsMap.put("manageContainer", ElementWrapperWidget.getWidget(TemplateUtil.asElement(DataTypePickerWidget_Div_manageContainer(instance))));
    templateFieldsMap.put("manageLabel", ElementWrapperWidget.getWidget(TemplateUtil.asElement(DataTypePickerWidget_Span_manageLabel(instance))));
    templateFieldsMap.put("typeSelector", DataTypePickerWidget_Select_typeSelector(instance).asWidget());
    TemplateUtil.initWidget(instance, TemplateUtil.getRootTemplateElement(parentElementForTemplateOfDataTypePickerWidget), templateFieldsMap.values());
    TemplateUtil.setupWrappedElementEventHandler(templateFieldsMap.get("typeButton"), new ClickHandler() {
      public void onClick(ClickEvent event) {
        instance.onClickTypeButton(event);
      }
    }, ClickEvent.getType());
    StyleBindingsRegistry.get().updateStyles(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((DataTypePickerWidget) instance, contextManager);
  }

  public void destroyInstanceHelper(final DataTypePickerWidget instance, final ContextManager contextManager) {
    ((Subscription) thisInstance.getReferenceAs(instance, "onDataTypePageNavTabActiveEventSubscription", Subscription.class)).remove();
    TemplateUtil.cleanupWidget(instance);
  }

  public void invokePostConstructs(final DataTypePickerWidget instance) {
    instance.init();
  }

  native static Span DataTypePickerWidget_Span_manageLabel(DataTypePickerWidget instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.editors.types.DataTypePickerWidget::manageLabel;
  }-*/;

  native static void DataTypePickerWidget_Span_manageLabel(DataTypePickerWidget instance, Span value) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.types.DataTypePickerWidget::manageLabel = value;
  }-*/;

  native static Select DataTypePickerWidget_Select_typeSelector(DataTypePickerWidget instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.editors.types.DataTypePickerWidget::typeSelector;
  }-*/;

  native static void DataTypePickerWidget_Select_typeSelector(DataTypePickerWidget instance, Select value) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.types.DataTypePickerWidget::typeSelector = value;
  }-*/;

  native static Anchor DataTypePickerWidget_Anchor_typeButton(DataTypePickerWidget instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.editors.types.DataTypePickerWidget::typeButton;
  }-*/;

  native static void DataTypePickerWidget_Anchor_typeButton(DataTypePickerWidget instance, Anchor value) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.types.DataTypePickerWidget::typeButton = value;
  }-*/;

  native static Div DataTypePickerWidget_Div_manageContainer(DataTypePickerWidget instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.editors.types.DataTypePickerWidget::manageContainer;
  }-*/;

  native static void DataTypePickerWidget_Div_manageContainer(DataTypePickerWidget instance, Div value) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.types.DataTypePickerWidget::manageContainer = value;
  }-*/;
}
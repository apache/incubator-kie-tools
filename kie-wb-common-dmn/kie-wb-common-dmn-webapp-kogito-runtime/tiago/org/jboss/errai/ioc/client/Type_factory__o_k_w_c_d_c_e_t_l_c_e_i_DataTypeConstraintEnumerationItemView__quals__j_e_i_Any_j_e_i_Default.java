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
import elemental2.dom.HTMLDivElement;
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
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.DataFieldMeta;
import org.jboss.errai.ui.shared.Template;
import org.jboss.errai.ui.shared.TemplateUtil;
import org.jboss.errai.ui.shared.api.style.StyleBindingsRegistry;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.TypedValueComponentSelector;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.enumeration.item.DataTypeConstraintEnumerationItem.View;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.enumeration.item.DataTypeConstraintEnumerationItemView;
import org.uberfire.client.mvp.HasPresenter;
import org.uberfire.client.mvp.UberElemental;

public class Type_factory__o_k_w_c_d_c_e_t_l_c_e_i_DataTypeConstraintEnumerationItemView__quals__j_e_i_Any_j_e_i_Default extends Factory<DataTypeConstraintEnumerationItemView> { public interface o_k_w_c_d_c_e_t_l_c_e_i_DataTypeConstraintEnumerationItemViewTemplateResource extends Template, ClientBundle { @Source("org/kie/workbench/common/dmn/client/editors/types/listview/constraint/enumeration/item/DataTypeConstraintEnumerationItemView.html") public TextResource getContents(); }
  public Type_factory__o_k_w_c_d_c_e_t_l_c_e_i_DataTypeConstraintEnumerationItemView__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DataTypeConstraintEnumerationItemView.class, "Type_factory__o_k_w_c_d_c_e_t_l_c_e_i_DataTypeConstraintEnumerationItemView__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DataTypeConstraintEnumerationItemView.class, Object.class, View.class, UberElemental.class, IsElement.class, HasPresenter.class, org.jboss.errai.ui.client.local.api.elemental2.IsElement.class });
  }

  public void init(final Context context) {
    StyleInjector.inject("/*\n * Copyright 2019 Red Hat, Inc. and/or its affiliates.\n *\n * Licensed under the Apache License, Version 2.0 (the \"License\");\n * you may not use this file except in compliance with the License.\n * You may obtain a copy of the License at\n *\n *     http://www.apache.org/licenses/LICENSE-2.0\n *\n * Unless required by applicable law or agreed to in writing, software\n * distributed under the License is distributed on an \"AS IS\" BASIS,\n * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n * See the License for the specific language governing permissions and\n * limitations under the License.\n */\n[data-i18n-prefix=\"DataTypeConstraintEnumerationItemView.\"] {\n  border-bottom: 1px solid #DDD;\n  padding: 6px 10px 0;\n}\n[data-i18n-prefix=\"DataTypeConstraintEnumerationItemView.\"].highlighted {\n  background-color: #E8F4FB;\n}\n[data-i18n-prefix=\"DataTypeConstraintEnumerationItemView.\"] [data-field=\"value-text\"],\n[data-i18n-prefix=\"DataTypeConstraintEnumerationItemView.\"] [data-field=\"value-input-container\"] {\n  width: 400px;\n  display: inline-block;\n}\n[data-i18n-prefix=\"DataTypeConstraintEnumerationItemView.\"] [data-field=\"value-text\"] {\n  cursor: pointer;\n  white-space: nowrap;\n  text-overflow: ellipsis;\n  height: 20px;\n}\n[data-i18n-prefix=\"DataTypeConstraintEnumerationItemView.\"] [data-field=\"value-text\"].none {\n  opacity: .75;\n  font-style: italic;\n}\n[data-i18n-prefix=\"DataTypeConstraintEnumerationItemView.\"] [data-field=\"remove-anchor\"],\n[data-i18n-prefix=\"DataTypeConstraintEnumerationItemView.\"] [data-field=\"clear-field-anchor\"],\n[data-i18n-prefix=\"DataTypeConstraintEnumerationItemView.\"] [data-field=\"save-anchor\"] {\n  text-align: center;\n  position: absolute;\n  right: 0;\n  top: 0;\n  font-size: 20px;\n  width: 30px;\n  height: 100%;\n}\n[data-i18n-prefix=\"DataTypeConstraintEnumerationItemView.\"] [data-field=\"remove-anchor\"] .fa,\n[data-i18n-prefix=\"DataTypeConstraintEnumerationItemView.\"] [data-field=\"clear-field-anchor\"] .fa,\n[data-i18n-prefix=\"DataTypeConstraintEnumerationItemView.\"] [data-field=\"save-anchor\"] .fa {\n  position: absolute;\n  width: 20px;\n  height: 20px;\n  top: calc(50% - 10px);\n  left: calc(50% - 10px);\n}\n[data-i18n-prefix=\"DataTypeConstraintEnumerationItemView.\"] [data-field=\"remove-anchor\"],\n[data-i18n-prefix=\"DataTypeConstraintEnumerationItemView.\"] [data-field=\"clear-field-anchor\"] {\n  color: #777777;\n}\n[data-i18n-prefix=\"DataTypeConstraintEnumerationItemView.\"] [data-field=\"save-anchor\"] {\n  right: 30px;\n}\n[data-i18n-prefix=\"DataTypeConstraintEnumerationItemView.\"] .dropdown-kebab-container {\n  float: right;\n  margin-top: -1px;\n}\n[data-i18n-prefix=\"DataTypeConstraintEnumerationItemView.\"] .drag-grabber {\n  color: #777777;\n  position: absolute;\n  cursor: pointer;\n  height: 100%;\n  width: 10px;\n  left: 5px;\n  top: 0;\n}\n[data-i18n-prefix=\"DataTypeConstraintEnumerationItemView.\"] .drag-grabber .fa {\n  position: absolute;\n  top: calc(50% - 5px);\n}\n[data-i18n-prefix=\"DataTypeConstraintEnumerationItemView.\"] .drag-grabber .fa + .fa {\n  left: 5px;\n}\n[data-i18n-prefix=\"DataTypeConstraintEnumerationItemView.\"] .drag-grabber:hover {\n  cursor: grab;\n}\n[data-i18n-prefix=\"DataTypeConstraintEnumerationItemView.\"] .drag-grabber:active {\n  cursor: grabbing;\n}\n.kie-data-type-constraints-enumeration.dropdown-menu {\n  min-width: 100px;\n  padding: 5px;\n}\n.kie-data-type-constraints-enumeration.dropdown-menu .divider {\n  background-color: #DDD;\n}\n\n");
  }

  public DataTypeConstraintEnumerationItemView createInstance(final ContextManager contextManager) {
    final HTMLDivElement _valueInput_1 = (HTMLDivElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLDivElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final TypedValueComponentSelector _valueComponentSelector_6 = (TypedValueComponentSelector) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_t_l_c_c_t_TypedValueComponentSelector__quals__j_e_i_Any_j_e_i_Default");
    final HTMLAnchorElement _removeAnchor_3 = (HTMLAnchorElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLAnchorElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final HTMLAnchorElement _saveAnchor_2 = (HTMLAnchorElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLAnchorElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final HTMLAnchorElement _clearFieldAnchor_4 = (HTMLAnchorElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLAnchorElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final TranslationService _translationService_5 = (TranslationService) contextManager.getInstance("Provider_factory__o_j_e_u_c_l_s_TranslationService__quals__j_e_i_Any_j_e_i_Default");
    final HTMLElement _valueText_0 = (HTMLElement) contextManager.getContextualInstance("ContextualProvider_factory__e_d_HTMLElement__quals__Universal", new Class[] { }, new Annotation[] { new Named() {
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
    final DataTypeConstraintEnumerationItemView instance = new DataTypeConstraintEnumerationItemView(_valueText_0, _valueInput_1, _saveAnchor_2, _removeAnchor_3, _clearFieldAnchor_4, _translationService_5, _valueComponentSelector_6);
    registerDependentScopedReference(instance, _valueInput_1);
    registerDependentScopedReference(instance, _valueComponentSelector_6);
    registerDependentScopedReference(instance, _removeAnchor_3);
    registerDependentScopedReference(instance, _saveAnchor_2);
    registerDependentScopedReference(instance, _clearFieldAnchor_4);
    registerDependentScopedReference(instance, _translationService_5);
    registerDependentScopedReference(instance, _valueText_0);
    setIncompleteInstance(instance);
    o_k_w_c_d_c_e_t_l_c_e_i_DataTypeConstraintEnumerationItemViewTemplateResource templateForDataTypeConstraintEnumerationItemView = GWT.create(o_k_w_c_d_c_e_t_l_c_e_i_DataTypeConstraintEnumerationItemViewTemplateResource.class);
    Element parentElementForTemplateOfDataTypeConstraintEnumerationItemView = TemplateUtil.getRootTemplateParentElement(templateForDataTypeConstraintEnumerationItemView.getContents().getText(), "org/kie/workbench/common/dmn/client/editors/types/listview/constraint/enumeration/item/DataTypeConstraintEnumerationItemView.html", "");
    TemplateUtil.translateTemplate("org/kie/workbench/common/dmn/client/editors/types/listview/constraint/enumeration/item/DataTypeConstraintEnumerationItemView.html", TemplateUtil.getRootTemplateElement(parentElementForTemplateOfDataTypeConstraintEnumerationItemView));
    Map<String, Element> dataFieldElements = TemplateUtil.getDataFieldElements(TemplateUtil.getRootTemplateElement(parentElementForTemplateOfDataTypeConstraintEnumerationItemView));
    final Map<String, DataFieldMeta> dataFieldMetas = new HashMap<String, DataFieldMeta>(5);
    dataFieldMetas.put("value-text", new DataFieldMeta());
    dataFieldMetas.put("value-input-container", new DataFieldMeta());
    dataFieldMetas.put("save-anchor", new DataFieldMeta());
    dataFieldMetas.put("remove-anchor", new DataFieldMeta());
    dataFieldMetas.put("clear-field-anchor", new DataFieldMeta());
    Map<String, Widget> templateFieldsMap = new LinkedHashMap<String, Widget>();
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.editors.types.listview.constraint.enumeration.item.DataTypeConstraintEnumerationItemView", "org/kie/workbench/common/dmn/client/editors/types/listview/constraint/enumeration/item/DataTypeConstraintEnumerationItemView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(DataTypeConstraintEnumerationItemView_HTMLElement_valueText(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "value-text");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.editors.types.listview.constraint.enumeration.item.DataTypeConstraintEnumerationItemView", "org/kie/workbench/common/dmn/client/editors/types/listview/constraint/enumeration/item/DataTypeConstraintEnumerationItemView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(DataTypeConstraintEnumerationItemView_HTMLDivElement_valueInputContainer(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "value-input-container");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.editors.types.listview.constraint.enumeration.item.DataTypeConstraintEnumerationItemView", "org/kie/workbench/common/dmn/client/editors/types/listview/constraint/enumeration/item/DataTypeConstraintEnumerationItemView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(DataTypeConstraintEnumerationItemView_HTMLAnchorElement_saveAnchor(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "save-anchor");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.editors.types.listview.constraint.enumeration.item.DataTypeConstraintEnumerationItemView", "org/kie/workbench/common/dmn/client/editors/types/listview/constraint/enumeration/item/DataTypeConstraintEnumerationItemView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(DataTypeConstraintEnumerationItemView_HTMLAnchorElement_removeAnchor(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "remove-anchor");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.editors.types.listview.constraint.enumeration.item.DataTypeConstraintEnumerationItemView", "org/kie/workbench/common/dmn/client/editors/types/listview/constraint/enumeration/item/DataTypeConstraintEnumerationItemView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(DataTypeConstraintEnumerationItemView_HTMLAnchorElement_clearFieldAnchor(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "clear-field-anchor");
    templateFieldsMap.put("value-text", ElementWrapperWidget.getWidget(TemplateUtil.asElement(DataTypeConstraintEnumerationItemView_HTMLElement_valueText(instance))));
    templateFieldsMap.put("value-input-container", ElementWrapperWidget.getWidget(TemplateUtil.asElement(DataTypeConstraintEnumerationItemView_HTMLDivElement_valueInputContainer(instance))));
    templateFieldsMap.put("save-anchor", ElementWrapperWidget.getWidget(TemplateUtil.asElement(DataTypeConstraintEnumerationItemView_HTMLAnchorElement_saveAnchor(instance))));
    templateFieldsMap.put("remove-anchor", ElementWrapperWidget.getWidget(TemplateUtil.asElement(DataTypeConstraintEnumerationItemView_HTMLAnchorElement_removeAnchor(instance))));
    templateFieldsMap.put("clear-field-anchor", ElementWrapperWidget.getWidget(TemplateUtil.asElement(DataTypeConstraintEnumerationItemView_HTMLAnchorElement_clearFieldAnchor(instance))));
    TemplateUtil.initTemplated(instance, TemplateUtil.getRootTemplateElement(parentElementForTemplateOfDataTypeConstraintEnumerationItemView), templateFieldsMap.values());
    TemplateUtil.setupWrappedElementEventHandler(templateFieldsMap.get("clear-field-anchor"), new ClickHandler() {
      public void onClick(ClickEvent event) {
        instance.onClearFieldAnchorClick(event);
      }
    }, ClickEvent.getType());
    TemplateUtil.setupWrappedElementEventHandler(templateFieldsMap.get("value-text"), new ClickHandler() {
      public void onClick(ClickEvent event) {
        instance.onValueTextClick(event);
      }
    }, ClickEvent.getType());
    TemplateUtil.setupWrappedElementEventHandler(templateFieldsMap.get("remove-anchor"), new ClickHandler() {
      public void onClick(ClickEvent event) {
        instance.onRemoveAnchorClick(event);
      }
    }, ClickEvent.getType());
    TemplateUtil.setupWrappedElementEventHandler(templateFieldsMap.get("save-anchor"), new ClickHandler() {
      public void onClick(ClickEvent event) {
        instance.onSaveAnchorClick(event);
      }
    }, ClickEvent.getType());
    StyleBindingsRegistry.get().updateStyles(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((DataTypeConstraintEnumerationItemView) instance, contextManager);
  }

  public void destroyInstanceHelper(final DataTypeConstraintEnumerationItemView instance, final ContextManager contextManager) {
    TemplateUtil.cleanupTemplated(instance);
  }

  native static HTMLAnchorElement DataTypeConstraintEnumerationItemView_HTMLAnchorElement_clearFieldAnchor(DataTypeConstraintEnumerationItemView instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.editors.types.listview.constraint.enumeration.item.DataTypeConstraintEnumerationItemView::clearFieldAnchor;
  }-*/;

  native static void DataTypeConstraintEnumerationItemView_HTMLAnchorElement_clearFieldAnchor(DataTypeConstraintEnumerationItemView instance, HTMLAnchorElement value) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.types.listview.constraint.enumeration.item.DataTypeConstraintEnumerationItemView::clearFieldAnchor = value;
  }-*/;

  native static HTMLElement DataTypeConstraintEnumerationItemView_HTMLElement_valueText(DataTypeConstraintEnumerationItemView instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.editors.types.listview.constraint.enumeration.item.DataTypeConstraintEnumerationItemView::valueText;
  }-*/;

  native static void DataTypeConstraintEnumerationItemView_HTMLElement_valueText(DataTypeConstraintEnumerationItemView instance, HTMLElement value) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.types.listview.constraint.enumeration.item.DataTypeConstraintEnumerationItemView::valueText = value;
  }-*/;

  native static HTMLAnchorElement DataTypeConstraintEnumerationItemView_HTMLAnchorElement_removeAnchor(DataTypeConstraintEnumerationItemView instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.editors.types.listview.constraint.enumeration.item.DataTypeConstraintEnumerationItemView::removeAnchor;
  }-*/;

  native static void DataTypeConstraintEnumerationItemView_HTMLAnchorElement_removeAnchor(DataTypeConstraintEnumerationItemView instance, HTMLAnchorElement value) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.types.listview.constraint.enumeration.item.DataTypeConstraintEnumerationItemView::removeAnchor = value;
  }-*/;

  native static HTMLDivElement DataTypeConstraintEnumerationItemView_HTMLDivElement_valueInputContainer(DataTypeConstraintEnumerationItemView instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.editors.types.listview.constraint.enumeration.item.DataTypeConstraintEnumerationItemView::valueInputContainer;
  }-*/;

  native static void DataTypeConstraintEnumerationItemView_HTMLDivElement_valueInputContainer(DataTypeConstraintEnumerationItemView instance, HTMLDivElement value) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.types.listview.constraint.enumeration.item.DataTypeConstraintEnumerationItemView::valueInputContainer = value;
  }-*/;

  native static HTMLAnchorElement DataTypeConstraintEnumerationItemView_HTMLAnchorElement_saveAnchor(DataTypeConstraintEnumerationItemView instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.editors.types.listview.constraint.enumeration.item.DataTypeConstraintEnumerationItemView::saveAnchor;
  }-*/;

  native static void DataTypeConstraintEnumerationItemView_HTMLAnchorElement_saveAnchor(DataTypeConstraintEnumerationItemView instance, HTMLAnchorElement value) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.types.listview.constraint.enumeration.item.DataTypeConstraintEnumerationItemView::saveAnchor = value;
  }-*/;
}
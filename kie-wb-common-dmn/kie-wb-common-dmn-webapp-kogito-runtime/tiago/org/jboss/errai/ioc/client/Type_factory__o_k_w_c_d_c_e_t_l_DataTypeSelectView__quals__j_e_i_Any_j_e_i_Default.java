package org.jboss.errai.ioc.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.StyleInjector;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ClientBundle.Source;
import com.google.gwt.resources.client.TextResource;
import com.google.gwt.user.client.ui.Widget;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLOptGroupElement;
import elemental2.dom.HTMLOptionElement;
import elemental2.dom.HTMLSelectElement;
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
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.DataFieldMeta;
import org.jboss.errai.ui.shared.Template;
import org.jboss.errai.ui.shared.TemplateUtil;
import org.jboss.errai.ui.shared.api.style.StyleBindingsRegistry;
import org.kie.workbench.common.dmn.client.editors.types.listview.DataTypeSelect.View;
import org.kie.workbench.common.dmn.client.editors.types.listview.DataTypeSelectView;
import org.uberfire.client.mvp.HasPresenter;
import org.uberfire.client.mvp.UberElemental;

public class Type_factory__o_k_w_c_d_c_e_t_l_DataTypeSelectView__quals__j_e_i_Any_j_e_i_Default extends Factory<DataTypeSelectView> { public interface o_k_w_c_d_c_e_t_l_DataTypeSelectViewTemplateResource extends Template, ClientBundle { @Source("org/kie/workbench/common/dmn/client/editors/types/listview/DataTypeSelectView.html") public TextResource getContents(); }
  public Type_factory__o_k_w_c_d_c_e_t_l_DataTypeSelectView__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DataTypeSelectView.class, "Type_factory__o_k_w_c_d_c_e_t_l_DataTypeSelectView__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DataTypeSelectView.class, Object.class, View.class, UberElemental.class, IsElement.class, HasPresenter.class, org.jboss.errai.ui.client.local.api.elemental2.IsElement.class });
  }

  public void init(final Context context) {
    StyleInjector.inject("/*\n * Copyright 2018 Red Hat, Inc. and/or its affiliates.\n *\n * Licensed under the Apache License, Version 2.0 (the \"License\");\n * you may not use this file except in compliance with the License.\n * You may obtain a copy of the License at\n *\n *     http://www.apache.org/licenses/LICENSE-2.0\n *\n * Unless required by applicable law or agreed to in writing, software\n * distributed under the License is distributed on an \"AS IS\" BASIS,\n * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n * See the License for the specific language governing permissions and\n * limitations under the License.\n */\n[data-i18n-prefix=\"DataTypeSelectView.\"] button,\n[data-i18n-prefix=\"DataTypeSelectView.\"] .dropdown-menu {\n  font-style: normal;\n}\n.dropdown-menu.open a.opt.option-structure,\n.dropdown-menu.open a.opt.option-structure:hover {\n  color: #0c8fd1;\n}\n.dropdown-menu.open a.opt.option-structure .text,\n.dropdown-menu.open a.opt.option-structure:hover .text {\n  font-family: \"Open Sans\", Helvetica, Arial, sans-serif;\n  padding-left: 8px;\n  font-weight: 600;\n}\n\n");
  }

  public DataTypeSelectView createInstance(final ContextManager contextManager) {
    final HTMLOptGroupElement _typeSelectStructureOptGroup_4 = (HTMLOptGroupElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLOptGroupElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final HTMLOptionElement _typeSelectOption_3 = (HTMLOptionElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLOptionElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final TranslationService _translationService_5 = (TranslationService) contextManager.getInstance("Provider_factory__o_j_e_u_c_l_s_TranslationService__quals__j_e_i_Any_j_e_i_Default");
    final HTMLSelectElement _typeSelect_1 = (HTMLSelectElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLSelectElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final HTMLDivElement _typeText_0 = (HTMLDivElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLDivElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final HTMLOptGroupElement _typeSelectOptGroup_2 = (HTMLOptGroupElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLOptGroupElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final DataTypeSelectView instance = new DataTypeSelectView(_typeText_0, _typeSelect_1, _typeSelectOptGroup_2, _typeSelectOption_3, _typeSelectStructureOptGroup_4, _translationService_5);
    registerDependentScopedReference(instance, _typeSelectStructureOptGroup_4);
    registerDependentScopedReference(instance, _typeSelectOption_3);
    registerDependentScopedReference(instance, _translationService_5);
    registerDependentScopedReference(instance, _typeSelect_1);
    registerDependentScopedReference(instance, _typeText_0);
    registerDependentScopedReference(instance, _typeSelectOptGroup_2);
    setIncompleteInstance(instance);
    o_k_w_c_d_c_e_t_l_DataTypeSelectViewTemplateResource templateForDataTypeSelectView = GWT.create(o_k_w_c_d_c_e_t_l_DataTypeSelectViewTemplateResource.class);
    Element parentElementForTemplateOfDataTypeSelectView = TemplateUtil.getRootTemplateParentElement(templateForDataTypeSelectView.getContents().getText(), "org/kie/workbench/common/dmn/client/editors/types/listview/DataTypeSelectView.html", "");
    TemplateUtil.translateTemplate("org/kie/workbench/common/dmn/client/editors/types/listview/DataTypeSelectView.html", TemplateUtil.getRootTemplateElement(parentElementForTemplateOfDataTypeSelectView));
    Map<String, Element> dataFieldElements = TemplateUtil.getDataFieldElements(TemplateUtil.getRootTemplateElement(parentElementForTemplateOfDataTypeSelectView));
    final Map<String, DataFieldMeta> dataFieldMetas = new HashMap<String, DataFieldMeta>(5);
    dataFieldMetas.put("type-text", new DataFieldMeta());
    dataFieldMetas.put("type-select", new DataFieldMeta());
    dataFieldMetas.put("type-select-optgroup", new DataFieldMeta());
    dataFieldMetas.put("type-select-structure-optgroup", new DataFieldMeta());
    dataFieldMetas.put("type-select-option", new DataFieldMeta());
    Map<String, Widget> templateFieldsMap = new LinkedHashMap<String, Widget>();
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.editors.types.listview.DataTypeSelectView", "org/kie/workbench/common/dmn/client/editors/types/listview/DataTypeSelectView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(DataTypeSelectView_HTMLDivElement_typeText(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "type-text");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.editors.types.listview.DataTypeSelectView", "org/kie/workbench/common/dmn/client/editors/types/listview/DataTypeSelectView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(DataTypeSelectView_HTMLSelectElement_typeSelect(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "type-select");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.editors.types.listview.DataTypeSelectView", "org/kie/workbench/common/dmn/client/editors/types/listview/DataTypeSelectView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(DataTypeSelectView_HTMLOptGroupElement_typeSelectOptGroup(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "type-select-optgroup");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.editors.types.listview.DataTypeSelectView", "org/kie/workbench/common/dmn/client/editors/types/listview/DataTypeSelectView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(DataTypeSelectView_HTMLOptGroupElement_typeSelectStructureOptGroup(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "type-select-structure-optgroup");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.editors.types.listview.DataTypeSelectView", "org/kie/workbench/common/dmn/client/editors/types/listview/DataTypeSelectView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(DataTypeSelectView_HTMLOptionElement_typeSelectOption(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "type-select-option");
    templateFieldsMap.put("type-text", ElementWrapperWidget.getWidget(TemplateUtil.asElement(DataTypeSelectView_HTMLDivElement_typeText(instance))));
    templateFieldsMap.put("type-select", ElementWrapperWidget.getWidget(TemplateUtil.asElement(DataTypeSelectView_HTMLSelectElement_typeSelect(instance))));
    templateFieldsMap.put("type-select-optgroup", ElementWrapperWidget.getWidget(TemplateUtil.asElement(DataTypeSelectView_HTMLOptGroupElement_typeSelectOptGroup(instance))));
    templateFieldsMap.put("type-select-structure-optgroup", ElementWrapperWidget.getWidget(TemplateUtil.asElement(DataTypeSelectView_HTMLOptGroupElement_typeSelectStructureOptGroup(instance))));
    templateFieldsMap.put("type-select-option", ElementWrapperWidget.getWidget(TemplateUtil.asElement(DataTypeSelectView_HTMLOptionElement_typeSelectOption(instance))));
    TemplateUtil.initTemplated(instance, TemplateUtil.getRootTemplateElement(parentElementForTemplateOfDataTypeSelectView), templateFieldsMap.values());
    StyleBindingsRegistry.get().updateStyles(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((DataTypeSelectView) instance, contextManager);
  }

  public void destroyInstanceHelper(final DataTypeSelectView instance, final ContextManager contextManager) {
    TemplateUtil.cleanupTemplated(instance);
  }

  native static HTMLSelectElement DataTypeSelectView_HTMLSelectElement_typeSelect(DataTypeSelectView instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.editors.types.listview.DataTypeSelectView::typeSelect;
  }-*/;

  native static void DataTypeSelectView_HTMLSelectElement_typeSelect(DataTypeSelectView instance, HTMLSelectElement value) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.types.listview.DataTypeSelectView::typeSelect = value;
  }-*/;

  native static HTMLOptGroupElement DataTypeSelectView_HTMLOptGroupElement_typeSelectOptGroup(DataTypeSelectView instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.editors.types.listview.DataTypeSelectView::typeSelectOptGroup;
  }-*/;

  native static void DataTypeSelectView_HTMLOptGroupElement_typeSelectOptGroup(DataTypeSelectView instance, HTMLOptGroupElement value) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.types.listview.DataTypeSelectView::typeSelectOptGroup = value;
  }-*/;

  native static HTMLDivElement DataTypeSelectView_HTMLDivElement_typeText(DataTypeSelectView instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.editors.types.listview.DataTypeSelectView::typeText;
  }-*/;

  native static void DataTypeSelectView_HTMLDivElement_typeText(DataTypeSelectView instance, HTMLDivElement value) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.types.listview.DataTypeSelectView::typeText = value;
  }-*/;

  native static HTMLOptionElement DataTypeSelectView_HTMLOptionElement_typeSelectOption(DataTypeSelectView instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.editors.types.listview.DataTypeSelectView::typeSelectOption;
  }-*/;

  native static void DataTypeSelectView_HTMLOptionElement_typeSelectOption(DataTypeSelectView instance, HTMLOptionElement value) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.types.listview.DataTypeSelectView::typeSelectOption = value;
  }-*/;

  native static HTMLOptGroupElement DataTypeSelectView_HTMLOptGroupElement_typeSelectStructureOptGroup(DataTypeSelectView instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.editors.types.listview.DataTypeSelectView::typeSelectStructureOptGroup;
  }-*/;

  native static void DataTypeSelectView_HTMLOptGroupElement_typeSelectStructureOptGroup(DataTypeSelectView instance, HTMLOptGroupElement value) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.types.listview.DataTypeSelectView::typeSelectStructureOptGroup = value;
  }-*/;
}
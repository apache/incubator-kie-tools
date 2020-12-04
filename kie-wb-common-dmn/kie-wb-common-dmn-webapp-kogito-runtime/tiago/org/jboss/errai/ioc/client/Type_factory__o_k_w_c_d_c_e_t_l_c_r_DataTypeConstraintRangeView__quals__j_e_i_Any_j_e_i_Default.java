package org.jboss.errai.ioc.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.StyleInjector;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ClientBundle.Source;
import com.google.gwt.resources.client.TextResource;
import com.google.gwt.user.client.ui.Widget;
import elemental2.dom.HTMLDivElement;
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
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.TypedValueComponentSelector;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.range.DataTypeConstraintRange.View;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.range.DataTypeConstraintRangeView;
import org.uberfire.client.mvp.HasPresenter;
import org.uberfire.client.mvp.UberElemental;

public class Type_factory__o_k_w_c_d_c_e_t_l_c_r_DataTypeConstraintRangeView__quals__j_e_i_Any_j_e_i_Default extends Factory<DataTypeConstraintRangeView> { public interface o_k_w_c_d_c_e_t_l_c_r_DataTypeConstraintRangeViewTemplateResource extends Template, ClientBundle { @Source("org/kie/workbench/common/dmn/client/editors/types/listview/constraint/range/DataTypeConstraintRangeView.html") public TextResource getContents(); }
  public Type_factory__o_k_w_c_d_c_e_t_l_c_r_DataTypeConstraintRangeView__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DataTypeConstraintRangeView.class, "Type_factory__o_k_w_c_d_c_e_t_l_c_r_DataTypeConstraintRangeView__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DataTypeConstraintRangeView.class, Object.class, View.class, UberElemental.class, IsElement.class, HasPresenter.class, org.jboss.errai.ui.client.local.api.elemental2.IsElement.class });
  }

  public void init(final Context context) {
    StyleInjector.inject("/*\n * Copyright 2019 Red Hat, Inc. and/or its affiliates.\n *\n * Licensed under the Apache License, Version 2.0 (the \"License\");\n * you may not use this file except in compliance with the License.\n * You may obtain a copy of the License at\n *\n *     http://www.apache.org/licenses/LICENSE-2.0\n *\n * Unless required by applicable law or agreed to in writing, software\n * distributed under the License is distributed on an \"AS IS\" BASIS,\n * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n * See the License for the specific language governing permissions and\n * limitations under the License.\n */\n[data-i18n-prefix=\"DataTypeConstraintRangeView.\"] .time-picker {\n  z-index: 2;\n}\n[data-i18n-prefix=\"DataTypeConstraintRangeView.\"] .checkbox-container {\n  padding-top: 10px;\n}\n[data-i18n-prefix=\"DataTypeConstraintRangeView.\"] .col-md-6,\n[data-i18n-prefix=\"DataTypeConstraintRangeView.\"] .col-md-12 {\n  padding-left: 0;\n  padding-right: 0;\n  position: relative;\n  z-index: 1;\n}\n[data-i18n-prefix=\"DataTypeConstraintRangeView.\"] .input-field {\n  display: inline-block;\n  width: 160px;\n  margin-bottom: 5px;\n}\n[data-i18n-prefix=\"DataTypeConstraintRangeView.\"] .btn.dropdown-toggle.btn-default {\n  width: 183px;\n}\n[data-i18n-prefix=\"DataTypeConstraintRangeView.\"] hr {\n  margin: 10px 0;\n}\n[data-i18n-prefix=\"DataTypeConstraintRangeView.\"] .tip {\n  opacity: .75;\n  font-size: .85em;\n  margin-bottom: 7px;\n}\n[data-i18n-prefix=\"DataTypeConstraintRangeView.\"] .tips {\n  padding-top: 0;\n  padding-left: 10px;\n  padding-right: 10px;\n  z-index: 0;\n}\n[data-i18n-prefix=\"DataTypeConstraintRangeView.\"] .controls-container {\n  width: 225px;\n  margin: 0 auto;\n  padding-top: 5px;\n}\n[data-i18n-prefix=\"DataTypeConstraintRangeView.\"] .date-input-field {\n  width: 200px;\n}\n[data-i18n-prefix=\"DataTypeConstraintRangeView.\"] .range-value-box {\n  min-width: 230px;\n  float: left;\n  margin: 10px 10px -5px;\n}\n[data-i18n-prefix=\"DataTypeConstraintRangeView.\"].kie-years-and-months-duration .range-value-box {\n  width: 230px;\n}\n[data-i18n-prefix=\"DataTypeConstraintRangeView.\"].kie-days-and-time-duration .range-value-box {\n  min-width: 495px;\n  margin: 10px;\n}\n[data-i18n-prefix=\"DataTypeConstraintRangeView.\"].kie-days-and-time-duration .range-value-box .checkbox-container {\n  vertical-align: top;\n  padding: 0;\n  margin: 2px 0 0 5px;\n}\n[data-i18n-prefix=\"DataTypeConstraintRangeView.\"].kie-days-and-time-duration .range-value-box .checkbox-container,\n[data-i18n-prefix=\"DataTypeConstraintRangeView.\"].kie-days-and-time-duration .range-value-box [data-field=\"end-value-container\"],\n[data-i18n-prefix=\"DataTypeConstraintRangeView.\"].kie-days-and-time-duration .range-value-box [data-field=\"start-value-container\"] {\n  display: inline-block;\n}\n[data-i18n-prefix=\"DataTypeConstraintRangeView.\"].kie-date-and-time .range-value-box {\n  margin: 10px;\n}\n[data-i18n-prefix=\"DataTypeConstraintRangeView.\"].kie-date-and-time .range-value-box [data-field=\"end-value-container\"],\n[data-i18n-prefix=\"DataTypeConstraintRangeView.\"].kie-date-and-time .range-value-box [data-field=\"start-value-container\"] {\n  display: inline-block;\n  width: 400px;\n}\n[data-i18n-prefix=\"DataTypeConstraintRangeView.\"] input.time-input-field {\n  width: 210px;\n  margin-top: -5px;\n}\n[data-i18n-prefix=\"DataTypeConstraintRangeView.\"] .btn-toggle-timezone {\n  margin-left: 0;\n  display: inline-block;\n}\n[data-i18n-prefix=\"DataTypeConstraintRangeView.\"] .dropdown-toggle {\n  width: 160px;\n}\n[data-i18n-prefix=\"DataTypeConstraintRangeView.\"] .btn-group {\n  width: 160px;\n}\n[data-i18n-prefix=\"DataTypeConstraintRangeView.\"] .bootstrap-select.form-control:not([class*=col-]) {\n  width: 160px;\n  margin-top: -5px;\n  margin-left: -2px;\n}\n\n");
  }

  public DataTypeConstraintRangeView createInstance(final ContextManager contextManager) {
    final TypedValueComponentSelector _startValueComponentSelector_4 = (TypedValueComponentSelector) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_t_l_c_c_t_TypedValueComponentSelector__quals__j_e_i_Any_j_e_i_Default");
    final HTMLInputElement _includeStartValue_2 = (HTMLInputElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLInputElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final HTMLDivElement _startValueContainer_0 = (HTMLDivElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLDivElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final HTMLDivElement _endValueContainer_1 = (HTMLDivElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLDivElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final TypedValueComponentSelector _endValueComponentSelector_5 = (TypedValueComponentSelector) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_t_l_c_c_t_TypedValueComponentSelector__quals__j_e_i_Any_j_e_i_Default");
    final HTMLInputElement _includeEndValue_3 = (HTMLInputElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLInputElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final DataTypeConstraintRangeView instance = new DataTypeConstraintRangeView(_startValueContainer_0, _endValueContainer_1, _includeStartValue_2, _includeEndValue_3, _startValueComponentSelector_4, _endValueComponentSelector_5);
    registerDependentScopedReference(instance, _startValueComponentSelector_4);
    registerDependentScopedReference(instance, _includeStartValue_2);
    registerDependentScopedReference(instance, _startValueContainer_0);
    registerDependentScopedReference(instance, _endValueContainer_1);
    registerDependentScopedReference(instance, _endValueComponentSelector_5);
    registerDependentScopedReference(instance, _includeEndValue_3);
    setIncompleteInstance(instance);
    o_k_w_c_d_c_e_t_l_c_r_DataTypeConstraintRangeViewTemplateResource templateForDataTypeConstraintRangeView = GWT.create(o_k_w_c_d_c_e_t_l_c_r_DataTypeConstraintRangeViewTemplateResource.class);
    Element parentElementForTemplateOfDataTypeConstraintRangeView = TemplateUtil.getRootTemplateParentElement(templateForDataTypeConstraintRangeView.getContents().getText(), "org/kie/workbench/common/dmn/client/editors/types/listview/constraint/range/DataTypeConstraintRangeView.html", "");
    TemplateUtil.translateTemplate("org/kie/workbench/common/dmn/client/editors/types/listview/constraint/range/DataTypeConstraintRangeView.html", TemplateUtil.getRootTemplateElement(parentElementForTemplateOfDataTypeConstraintRangeView));
    Map<String, Element> dataFieldElements = TemplateUtil.getDataFieldElements(TemplateUtil.getRootTemplateElement(parentElementForTemplateOfDataTypeConstraintRangeView));
    final Map<String, DataFieldMeta> dataFieldMetas = new HashMap<String, DataFieldMeta>(4);
    dataFieldMetas.put("end-value-container", new DataFieldMeta());
    dataFieldMetas.put("start-value-container", new DataFieldMeta());
    dataFieldMetas.put("include-end-value", new DataFieldMeta());
    dataFieldMetas.put("include-start-value", new DataFieldMeta());
    Map<String, Widget> templateFieldsMap = new LinkedHashMap<String, Widget>();
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.editors.types.listview.constraint.range.DataTypeConstraintRangeView", "org/kie/workbench/common/dmn/client/editors/types/listview/constraint/range/DataTypeConstraintRangeView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(DataTypeConstraintRangeView_HTMLDivElement_endValueContainer(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "end-value-container");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.editors.types.listview.constraint.range.DataTypeConstraintRangeView", "org/kie/workbench/common/dmn/client/editors/types/listview/constraint/range/DataTypeConstraintRangeView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(DataTypeConstraintRangeView_HTMLDivElement_startValueContainer(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "start-value-container");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.editors.types.listview.constraint.range.DataTypeConstraintRangeView", "org/kie/workbench/common/dmn/client/editors/types/listview/constraint/range/DataTypeConstraintRangeView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(DataTypeConstraintRangeView_HTMLInputElement_includeEndValue(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "include-end-value");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.editors.types.listview.constraint.range.DataTypeConstraintRangeView", "org/kie/workbench/common/dmn/client/editors/types/listview/constraint/range/DataTypeConstraintRangeView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(DataTypeConstraintRangeView_HTMLInputElement_includeStartValue(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "include-start-value");
    templateFieldsMap.put("end-value-container", ElementWrapperWidget.getWidget(TemplateUtil.asElement(DataTypeConstraintRangeView_HTMLDivElement_endValueContainer(instance))));
    templateFieldsMap.put("start-value-container", ElementWrapperWidget.getWidget(TemplateUtil.asElement(DataTypeConstraintRangeView_HTMLDivElement_startValueContainer(instance))));
    templateFieldsMap.put("include-end-value", ElementWrapperWidget.getWidget(TemplateUtil.asElement(DataTypeConstraintRangeView_HTMLInputElement_includeEndValue(instance))));
    templateFieldsMap.put("include-start-value", ElementWrapperWidget.getWidget(TemplateUtil.asElement(DataTypeConstraintRangeView_HTMLInputElement_includeStartValue(instance))));
    TemplateUtil.initTemplated(instance, TemplateUtil.getRootTemplateElement(parentElementForTemplateOfDataTypeConstraintRangeView), templateFieldsMap.values());
    StyleBindingsRegistry.get().updateStyles(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((DataTypeConstraintRangeView) instance, contextManager);
  }

  public void destroyInstanceHelper(final DataTypeConstraintRangeView instance, final ContextManager contextManager) {
    TemplateUtil.cleanupTemplated(instance);
  }

  native static HTMLDivElement DataTypeConstraintRangeView_HTMLDivElement_startValueContainer(DataTypeConstraintRangeView instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.editors.types.listview.constraint.range.DataTypeConstraintRangeView::startValueContainer;
  }-*/;

  native static void DataTypeConstraintRangeView_HTMLDivElement_startValueContainer(DataTypeConstraintRangeView instance, HTMLDivElement value) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.types.listview.constraint.range.DataTypeConstraintRangeView::startValueContainer = value;
  }-*/;

  native static HTMLInputElement DataTypeConstraintRangeView_HTMLInputElement_includeEndValue(DataTypeConstraintRangeView instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.editors.types.listview.constraint.range.DataTypeConstraintRangeView::includeEndValue;
  }-*/;

  native static void DataTypeConstraintRangeView_HTMLInputElement_includeEndValue(DataTypeConstraintRangeView instance, HTMLInputElement value) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.types.listview.constraint.range.DataTypeConstraintRangeView::includeEndValue = value;
  }-*/;

  native static HTMLInputElement DataTypeConstraintRangeView_HTMLInputElement_includeStartValue(DataTypeConstraintRangeView instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.editors.types.listview.constraint.range.DataTypeConstraintRangeView::includeStartValue;
  }-*/;

  native static void DataTypeConstraintRangeView_HTMLInputElement_includeStartValue(DataTypeConstraintRangeView instance, HTMLInputElement value) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.types.listview.constraint.range.DataTypeConstraintRangeView::includeStartValue = value;
  }-*/;

  native static HTMLDivElement DataTypeConstraintRangeView_HTMLDivElement_endValueContainer(DataTypeConstraintRangeView instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.editors.types.listview.constraint.range.DataTypeConstraintRangeView::endValueContainer;
  }-*/;

  native static void DataTypeConstraintRangeView_HTMLDivElement_endValueContainer(DataTypeConstraintRangeView instance, HTMLDivElement value) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.types.listview.constraint.range.DataTypeConstraintRangeView::endValueContainer = value;
  }-*/;
}
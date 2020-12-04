package org.jboss.errai.ioc.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.StyleInjector;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ClientBundle.Source;
import com.google.gwt.resources.client.TextResource;
import com.google.gwt.user.client.ui.Widget;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;
import javax.enterprise.context.Dependent;
import org.jboss.errai.common.client.dom.Button;
import org.jboss.errai.common.client.dom.Input;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.shared.DataFieldMeta;
import org.jboss.errai.ui.shared.Template;
import org.jboss.errai.ui.shared.TemplateUtil;
import org.jboss.errai.ui.shared.api.style.StyleBindingsRegistry;
import org.kie.workbench.common.dmn.client.editors.expressions.types.function.parameters.ParameterView;
import org.kie.workbench.common.dmn.client.editors.expressions.types.function.parameters.ParameterViewImpl;
import org.kie.workbench.common.dmn.client.editors.types.DataTypePickerWidget;

public class Type_factory__o_k_w_c_d_c_e_e_t_f_p_ParameterViewImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<ParameterViewImpl> { public interface o_k_w_c_d_c_e_e_t_f_p_ParameterViewImplTemplateResource extends Template, ClientBundle { @Source("org/kie/workbench/common/dmn/client/editors/expressions/types/function/parameters/ParameterViewImpl.html") public TextResource getContents(); }
  public Type_factory__o_k_w_c_d_c_e_e_t_f_p_ParameterViewImpl__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(ParameterViewImpl.class, "Type_factory__o_k_w_c_d_c_e_e_t_f_p_ParameterViewImpl__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { ParameterViewImpl.class, Object.class, ParameterView.class, IsElement.class, org.jboss.errai.common.client.api.IsElement.class });
  }

  public void init(final Context context) {
    StyleInjector.inject("/*\n * Copyright 2018 Red Hat, Inc. and/or its affiliates.\n *\n * Licensed under the Apache License, Version 2.0 (the \"License\");\n * you may not use this file except in compliance with the License.\n * You may obtain a copy of the License at\n *\n *     http://www.apache.org/licenses/LICENSE-2.0\n *\n * Unless required by applicable law or agreed to in writing, software\n * distributed under the License is distributed on an \"AS IS\" BASIS,\n * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n * See the License for the specific language governing permissions and\n * limitations under the License.\n */\n.kie-dmn-parameter {\n  margin-top: 5px;\n}\n.kie-dmn-parameter #typeRefEditor {\n  margin-left: 5px;\n}\n.kie-dmn-parameter button {\n  margin-left: 5px;\n}\n\n");
  }

  public ParameterViewImpl createInstance(final ContextManager contextManager) {
    final DataTypePickerWidget _typeRefEditor_1 = (DataTypePickerWidget) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_t_DataTypePickerWidget__quals__j_e_i_Any_j_e_i_Default");
    final Input _name_0 = (Input) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Input__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final Button _remove_2 = (Button) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Button__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final ParameterViewImpl instance = new ParameterViewImpl(_name_0, _typeRefEditor_1, _remove_2);
    registerDependentScopedReference(instance, _typeRefEditor_1);
    registerDependentScopedReference(instance, _name_0);
    registerDependentScopedReference(instance, _remove_2);
    setIncompleteInstance(instance);
    o_k_w_c_d_c_e_e_t_f_p_ParameterViewImplTemplateResource templateForParameterViewImpl = GWT.create(o_k_w_c_d_c_e_e_t_f_p_ParameterViewImplTemplateResource.class);
    Element parentElementForTemplateOfParameterViewImpl = TemplateUtil.getRootTemplateParentElement(templateForParameterViewImpl.getContents().getText(), "org/kie/workbench/common/dmn/client/editors/expressions/types/function/parameters/ParameterViewImpl.html", "");
    TemplateUtil.translateTemplate("org/kie/workbench/common/dmn/client/editors/expressions/types/function/parameters/ParameterViewImpl.html", TemplateUtil.getRootTemplateElement(parentElementForTemplateOfParameterViewImpl));
    Map<String, Element> dataFieldElements = TemplateUtil.getDataFieldElements(TemplateUtil.getRootTemplateElement(parentElementForTemplateOfParameterViewImpl));
    final Map<String, DataFieldMeta> dataFieldMetas = new HashMap<String, DataFieldMeta>(3);
    dataFieldMetas.put("name", new DataFieldMeta());
    dataFieldMetas.put("typeRefEditor", new DataFieldMeta());
    dataFieldMetas.put("remove", new DataFieldMeta());
    Map<String, Widget> templateFieldsMap = new LinkedHashMap<String, Widget>();
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.editors.expressions.types.function.parameters.ParameterViewImpl", "org/kie/workbench/common/dmn/client/editors/expressions/types/function/parameters/ParameterViewImpl.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(ParameterViewImpl_Input_name(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "name");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.editors.expressions.types.function.parameters.ParameterViewImpl", "org/kie/workbench/common/dmn/client/editors/expressions/types/function/parameters/ParameterViewImpl.html", new Supplier<Widget>() {
      public Widget get() {
        return ParameterViewImpl_DataTypePickerWidget_typeRefEditor(instance).asWidget();
      }
    }, dataFieldElements, dataFieldMetas, "typeRefEditor");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.editors.expressions.types.function.parameters.ParameterViewImpl", "org/kie/workbench/common/dmn/client/editors/expressions/types/function/parameters/ParameterViewImpl.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(ParameterViewImpl_Button_remove(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "remove");
    templateFieldsMap.put("name", ElementWrapperWidget.getWidget(TemplateUtil.asElement(ParameterViewImpl_Input_name(instance))));
    templateFieldsMap.put("typeRefEditor", ParameterViewImpl_DataTypePickerWidget_typeRefEditor(instance).asWidget());
    templateFieldsMap.put("remove", ElementWrapperWidget.getWidget(TemplateUtil.asElement(ParameterViewImpl_Button_remove(instance))));
    TemplateUtil.initTemplated(instance, TemplateUtil.getRootTemplateElement(parentElementForTemplateOfParameterViewImpl), templateFieldsMap.values());
    StyleBindingsRegistry.get().updateStyles(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((ParameterViewImpl) instance, contextManager);
  }

  public void destroyInstanceHelper(final ParameterViewImpl instance, final ContextManager contextManager) {
    TemplateUtil.cleanupTemplated(instance);
  }

  native static Input ParameterViewImpl_Input_name(ParameterViewImpl instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.editors.expressions.types.function.parameters.ParameterViewImpl::name;
  }-*/;

  native static void ParameterViewImpl_Input_name(ParameterViewImpl instance, Input value) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.expressions.types.function.parameters.ParameterViewImpl::name = value;
  }-*/;

  native static DataTypePickerWidget ParameterViewImpl_DataTypePickerWidget_typeRefEditor(ParameterViewImpl instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.editors.expressions.types.function.parameters.ParameterViewImpl::typeRefEditor;
  }-*/;

  native static void ParameterViewImpl_DataTypePickerWidget_typeRefEditor(ParameterViewImpl instance, DataTypePickerWidget value) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.expressions.types.function.parameters.ParameterViewImpl::typeRefEditor = value;
  }-*/;

  native static Button ParameterViewImpl_Button_remove(ParameterViewImpl instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.editors.expressions.types.function.parameters.ParameterViewImpl::remove;
  }-*/;

  native static void ParameterViewImpl_Button_remove(ParameterViewImpl instance, Button value) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.expressions.types.function.parameters.ParameterViewImpl::remove = value;
  }-*/;
}
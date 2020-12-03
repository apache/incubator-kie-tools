package org.jboss.errai.ioc.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.StyleInjector;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ClientBundle.Source;
import com.google.gwt.resources.client.TextResource;
import com.google.gwt.user.client.ui.Widget;
import elemental2.dom.HTMLTextAreaElement;
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
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.expression.DataTypeConstraintExpression.View;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.expression.DataTypeConstraintExpressionView;
import org.uberfire.client.mvp.HasPresenter;
import org.uberfire.client.mvp.UberElemental;

public class Type_factory__o_k_w_c_d_c_e_t_l_c_e_DataTypeConstraintExpressionView__quals__j_e_i_Any_j_e_i_Default extends Factory<DataTypeConstraintExpressionView> { public interface o_k_w_c_d_c_e_t_l_c_e_DataTypeConstraintExpressionViewTemplateResource extends Template, ClientBundle { @Source("org/kie/workbench/common/dmn/client/editors/types/listview/constraint/expression/DataTypeConstraintExpressionView.html") public TextResource getContents(); }
  public Type_factory__o_k_w_c_d_c_e_t_l_c_e_DataTypeConstraintExpressionView__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DataTypeConstraintExpressionView.class, "Type_factory__o_k_w_c_d_c_e_t_l_c_e_DataTypeConstraintExpressionView__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DataTypeConstraintExpressionView.class, Object.class, View.class, UberElemental.class, IsElement.class, HasPresenter.class, org.jboss.errai.ui.client.local.api.elemental2.IsElement.class });
  }

  public void init(final Context context) {
    StyleInjector.inject("/*\n * Copyright 2019 Red Hat, Inc. and/or its affiliates.\n *\n * Licensed under the Apache License, Version 2.0 (the \"License\");\n * you may not use this file except in compliance with the License.\n * You may obtain a copy of the License at\n *\n *       http://www.apache.org/licenses/LICENSE-2.0\n *\n * Unless required by applicable law or agreed to in writing, software\n * distributed under the License is distributed on an \"AS IS\" BASIS,\n * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n * See the License for the specific language governing permissions and\n * limitations under the License.\n */\n[data-i18n-prefix=\"DataTypeConstraintExpressionView.\"] {\n  padding: 15px;\n}\n[data-i18n-prefix=\"DataTypeConstraintExpressionView.\"] textarea {\n  width: 100%;\n  resize: none;\n  border: 1px solid #CCC;\n  box-shadow: inset 0 1px 3px #DDD;\n  height: 95px;\n}\n[data-i18n-prefix=\"DataTypeConstraintExpressionView.\"] p {\n  opacity: .75;\n  line-height: 1.7em;\n}\n[data-i18n-prefix=\"DataTypeConstraintExpressionView.\"] p .fa {\n  margin-right: 3px;\n}\n\n");
  }

  public DataTypeConstraintExpressionView createInstance(final ContextManager contextManager) {
    final HTMLTextAreaElement _expression_0 = (HTMLTextAreaElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLTextAreaElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final DataTypeConstraintExpressionView instance = new DataTypeConstraintExpressionView(_expression_0);
    registerDependentScopedReference(instance, _expression_0);
    setIncompleteInstance(instance);
    o_k_w_c_d_c_e_t_l_c_e_DataTypeConstraintExpressionViewTemplateResource templateForDataTypeConstraintExpressionView = GWT.create(o_k_w_c_d_c_e_t_l_c_e_DataTypeConstraintExpressionViewTemplateResource.class);
    Element parentElementForTemplateOfDataTypeConstraintExpressionView = TemplateUtil.getRootTemplateParentElement(templateForDataTypeConstraintExpressionView.getContents().getText(), "org/kie/workbench/common/dmn/client/editors/types/listview/constraint/expression/DataTypeConstraintExpressionView.html", "");
    TemplateUtil.translateTemplate("org/kie/workbench/common/dmn/client/editors/types/listview/constraint/expression/DataTypeConstraintExpressionView.html", TemplateUtil.getRootTemplateElement(parentElementForTemplateOfDataTypeConstraintExpressionView));
    Map<String, Element> dataFieldElements = TemplateUtil.getDataFieldElements(TemplateUtil.getRootTemplateElement(parentElementForTemplateOfDataTypeConstraintExpressionView));
    final Map<String, DataFieldMeta> dataFieldMetas = new HashMap<String, DataFieldMeta>(1);
    dataFieldMetas.put("expression", new DataFieldMeta());
    Map<String, Widget> templateFieldsMap = new LinkedHashMap<String, Widget>();
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.editors.types.listview.constraint.expression.DataTypeConstraintExpressionView", "org/kie/workbench/common/dmn/client/editors/types/listview/constraint/expression/DataTypeConstraintExpressionView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(DataTypeConstraintExpressionView_HTMLTextAreaElement_expression(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "expression");
    templateFieldsMap.put("expression", ElementWrapperWidget.getWidget(TemplateUtil.asElement(DataTypeConstraintExpressionView_HTMLTextAreaElement_expression(instance))));
    TemplateUtil.initTemplated(instance, TemplateUtil.getRootTemplateElement(parentElementForTemplateOfDataTypeConstraintExpressionView), templateFieldsMap.values());
    StyleBindingsRegistry.get().updateStyles(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((DataTypeConstraintExpressionView) instance, contextManager);
  }

  public void destroyInstanceHelper(final DataTypeConstraintExpressionView instance, final ContextManager contextManager) {
    TemplateUtil.cleanupTemplated(instance);
  }

  native static HTMLTextAreaElement DataTypeConstraintExpressionView_HTMLTextAreaElement_expression(DataTypeConstraintExpressionView instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.editors.types.listview.constraint.expression.DataTypeConstraintExpressionView::expression;
  }-*/;

  native static void DataTypeConstraintExpressionView_HTMLTextAreaElement_expression(DataTypeConstraintExpressionView instance, HTMLTextAreaElement value) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.types.listview.constraint.expression.DataTypeConstraintExpressionView::expression = value;
  }-*/;
}
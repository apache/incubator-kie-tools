package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.ConstraintPlaceholderHelper;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.DataTypeConstraintComponent;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.expression.DataTypeConstraintExpression;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.expression.DataTypeConstraintExpression.View;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.expression.DataTypeConstraintExpressionView;

public class Type_factory__o_k_w_c_d_c_e_t_l_c_e_DataTypeConstraintExpression__quals__j_e_i_Any_j_e_i_Default extends Factory<DataTypeConstraintExpression> { public Type_factory__o_k_w_c_d_c_e_t_l_c_e_DataTypeConstraintExpression__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DataTypeConstraintExpression.class, "Type_factory__o_k_w_c_d_c_e_t_l_c_e_DataTypeConstraintExpression__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DataTypeConstraintExpression.class, Object.class, DataTypeConstraintComponent.class });
  }

  public DataTypeConstraintExpression createInstance(final ContextManager contextManager) {
    final ConstraintPlaceholderHelper _placeholderHelper_1 = (ConstraintPlaceholderHelper) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_t_l_c_c_ConstraintPlaceholderHelper__quals__j_e_i_Any_j_e_i_Default");
    final View _view_0 = (DataTypeConstraintExpressionView) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_t_l_c_e_DataTypeConstraintExpressionView__quals__j_e_i_Any_j_e_i_Default");
    final DataTypeConstraintExpression instance = new DataTypeConstraintExpression(_view_0, _placeholderHelper_1);
    registerDependentScopedReference(instance, _placeholderHelper_1);
    registerDependentScopedReference(instance, _view_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void invokePostConstructs(final DataTypeConstraintExpression instance) {
    DataTypeConstraintExpression_setup(instance);
  }

  public native static void DataTypeConstraintExpression_setup(DataTypeConstraintExpression instance) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.types.listview.constraint.expression.DataTypeConstraintExpression::setup()();
  }-*/;
}
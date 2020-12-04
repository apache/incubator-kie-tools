package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.DataTypeConstraint;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.DataTypeConstraint.View;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.DataTypeConstraintModal;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.DataTypeConstraintView;

public class Type_factory__o_k_w_c_d_c_e_t_l_c_DataTypeConstraint__quals__j_e_i_Any_j_e_i_Default extends Factory<DataTypeConstraint> { public Type_factory__o_k_w_c_d_c_e_t_l_c_DataTypeConstraint__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DataTypeConstraint.class, "Type_factory__o_k_w_c_d_c_e_t_l_c_DataTypeConstraint__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DataTypeConstraint.class, Object.class });
  }

  public DataTypeConstraint createInstance(final ContextManager contextManager) {
    final ManagedInstance<DataTypeConstraintModal> _constraintModalManagedInstance_1 = (ManagedInstance) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_i_c_a_ManagedInstance__quals__Universal", new Class[] { DataTypeConstraintModal.class }, new Annotation[] { });
    final View _view_0 = (DataTypeConstraintView) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_t_l_c_DataTypeConstraintView__quals__j_e_i_Any_j_e_i_Default");
    final DataTypeConstraint instance = new DataTypeConstraint(_view_0, _constraintModalManagedInstance_1);
    registerDependentScopedReference(instance, _constraintModalManagedInstance_1);
    registerDependentScopedReference(instance, _view_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void invokePostConstructs(final DataTypeConstraint instance) {
    DataTypeConstraint_setup(instance);
  }

  public native static void DataTypeConstraint_setup(DataTypeConstraint instance) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.types.listview.constraint.DataTypeConstraint::setup()();
  }-*/;
}
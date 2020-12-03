package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.TypedValueSelector;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.common.BaseSelector;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.common.BaseSelector.View;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.common.BaseSelectorView;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.generic.GenericSelector;

public class Type_factory__o_k_w_c_d_c_e_t_l_c_c_t_g_GenericSelector__quals__j_e_i_Any_j_e_i_Default extends Factory<GenericSelector> { public Type_factory__o_k_w_c_d_c_e_t_l_c_c_t_g_GenericSelector__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(GenericSelector.class, "Type_factory__o_k_w_c_d_c_e_t_l_c_c_t_g_GenericSelector__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { GenericSelector.class, BaseSelector.class, Object.class, TypedValueSelector.class });
  }

  public GenericSelector createInstance(final ContextManager contextManager) {
    final View _view_0 = (BaseSelectorView) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_t_l_c_c_t_c_BaseSelectorView__quals__j_e_i_Any_j_e_i_Default");
    final GenericSelector instance = new GenericSelector(_view_0);
    registerDependentScopedReference(instance, _view_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void invokePostConstructs(final GenericSelector instance) {
    instance.setupInputType();
  }
}
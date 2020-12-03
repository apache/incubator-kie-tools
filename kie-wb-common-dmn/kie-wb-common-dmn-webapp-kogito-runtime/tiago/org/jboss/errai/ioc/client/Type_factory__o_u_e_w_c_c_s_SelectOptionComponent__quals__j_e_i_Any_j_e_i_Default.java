package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.uberfire.ext.widgets.common.client.select.SelectOptionComponent;
import org.uberfire.ext.widgets.common.client.select.SelectOptionComponent.View;
import org.uberfire.ext.widgets.common.client.select.SelectOptionView;

public class Type_factory__o_u_e_w_c_c_s_SelectOptionComponent__quals__j_e_i_Any_j_e_i_Default extends Factory<SelectOptionComponent> { public Type_factory__o_u_e_w_c_c_s_SelectOptionComponent__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(SelectOptionComponent.class, "Type_factory__o_u_e_w_c_c_s_SelectOptionComponent__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { SelectOptionComponent.class, Object.class });
  }

  public SelectOptionComponent createInstance(final ContextManager contextManager) {
    final View _view_0 = (SelectOptionView) contextManager.getInstance("Type_factory__o_u_e_w_c_c_s_SelectOptionView__quals__j_e_i_Any_j_e_i_Default");
    final SelectOptionComponent instance = new SelectOptionComponent(_view_0);
    registerDependentScopedReference(instance, _view_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}
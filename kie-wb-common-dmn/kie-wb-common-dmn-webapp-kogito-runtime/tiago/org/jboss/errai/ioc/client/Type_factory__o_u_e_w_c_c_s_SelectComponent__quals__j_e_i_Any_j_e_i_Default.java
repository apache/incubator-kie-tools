package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.uberfire.ext.widgets.common.client.select.SelectComponent;
import org.uberfire.ext.widgets.common.client.select.SelectComponent.View;
import org.uberfire.ext.widgets.common.client.select.SelectOptionComponent;
import org.uberfire.ext.widgets.common.client.select.SelectView;

public class Type_factory__o_u_e_w_c_c_s_SelectComponent__quals__j_e_i_Any_j_e_i_Default extends Factory<SelectComponent> { public Type_factory__o_u_e_w_c_c_s_SelectComponent__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(SelectComponent.class, "Type_factory__o_u_e_w_c_c_s_SelectComponent__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { SelectComponent.class, Object.class });
  }

  public SelectComponent createInstance(final ContextManager contextManager) {
    final ManagedInstance<SelectOptionComponent> _selectOptionComponent_1 = (ManagedInstance) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_i_c_a_ManagedInstance__quals__Universal", new Class[] { SelectOptionComponent.class }, new Annotation[] { });
    final View _view_0 = (SelectView) contextManager.getInstance("Type_factory__o_u_e_w_c_c_s_SelectView__quals__j_e_i_Any_j_e_i_Default");
    final SelectComponent instance = new SelectComponent(_view_0, _selectOptionComponent_1);
    registerDependentScopedReference(instance, _selectOptionComponent_1);
    registerDependentScopedReference(instance, _view_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void invokePostConstructs(final SelectComponent instance) {
    instance.init();
  }
}
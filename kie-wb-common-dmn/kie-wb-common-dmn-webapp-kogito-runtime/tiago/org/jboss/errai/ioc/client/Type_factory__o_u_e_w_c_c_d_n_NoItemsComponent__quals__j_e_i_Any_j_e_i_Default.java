package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.common.client.api.IsElement;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.uberfire.ext.widgets.common.client.dropdown.noItems.NoItemsComponent;
import org.uberfire.ext.widgets.common.client.dropdown.noItems.NoItemsComponentView;
import org.uberfire.ext.widgets.common.client.dropdown.noItems.NoItemsComponentViewImpl;

public class Type_factory__o_u_e_w_c_c_d_n_NoItemsComponent__quals__j_e_i_Any_j_e_i_Default extends Factory<NoItemsComponent> { public Type_factory__o_u_e_w_c_c_d_n_NoItemsComponent__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(NoItemsComponent.class, "Type_factory__o_u_e_w_c_c_d_n_NoItemsComponent__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { NoItemsComponent.class, Object.class, IsElement.class });
  }

  public NoItemsComponent createInstance(final ContextManager contextManager) {
    final NoItemsComponentView _view_0 = (NoItemsComponentViewImpl) contextManager.getInstance("Type_factory__o_u_e_w_c_c_d_n_NoItemsComponentViewImpl__quals__j_e_i_Any_j_e_i_Default");
    final NoItemsComponent instance = new NoItemsComponent(_view_0);
    registerDependentScopedReference(instance, _view_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}
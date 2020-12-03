package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.uberfire.ext.widgets.common.client.common.BusyIndicatorView;
import org.uberfire.ext.widgets.common.client.common.HasBusyIndicator;

public class Type_factory__o_u_e_w_c_c_c_BusyIndicatorView__quals__j_e_i_Any_j_e_i_Default extends Factory<BusyIndicatorView> { public Type_factory__o_u_e_w_c_c_c_BusyIndicatorView__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(BusyIndicatorView.class, "Type_factory__o_u_e_w_c_c_c_BusyIndicatorView__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { BusyIndicatorView.class, Object.class, HasBusyIndicator.class });
  }

  public BusyIndicatorView createInstance(final ContextManager contextManager) {
    final BusyIndicatorView instance = new BusyIndicatorView();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((BusyIndicatorView) instance, contextManager);
  }

  public void destroyInstanceHelper(final BusyIndicatorView instance, final ContextManager contextManager) {
    instance.hideBusyIndicator();
  }
}
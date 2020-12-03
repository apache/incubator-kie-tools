package org.jboss.errai.ioc.client;

import org.jboss.errai.ioc.client.api.EntryPoint;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.uberfire.ext.widgets.common.client.CommonsEntryPoint;

public class Type_factory__o_u_e_w_c_c_CommonsEntryPoint__quals__j_e_i_Any_j_e_i_Default extends Factory<CommonsEntryPoint> { public Type_factory__o_u_e_w_c_c_CommonsEntryPoint__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(CommonsEntryPoint.class, "Type_factory__o_u_e_w_c_c_CommonsEntryPoint__quals__j_e_i_Any_j_e_i_Default", EntryPoint.class, true, null, true));
    handle.setAssignableTypes(new Class[] { CommonsEntryPoint.class, Object.class });
  }

  public CommonsEntryPoint createInstance(final ContextManager contextManager) {
    final CommonsEntryPoint instance = new CommonsEntryPoint();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void invokePostConstructs(final CommonsEntryPoint instance) {
    instance.startApp();
  }
}
package org.jboss.errai.ioc.client;

import javax.inject.Singleton;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.lifecycle.api.LifecycleListenerRegistrar;
import org.jboss.errai.ioc.client.lifecycle.impl.LifecycleListenerRegistrarImpl;

public class Type_factory__o_j_e_i_c_l_i_LifecycleListenerRegistrarImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<LifecycleListenerRegistrarImpl> { public Type_factory__o_j_e_i_c_l_i_LifecycleListenerRegistrarImpl__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(LifecycleListenerRegistrarImpl.class, "Type_factory__o_j_e_i_c_l_i_LifecycleListenerRegistrarImpl__quals__j_e_i_Any_j_e_i_Default", Singleton.class, false, null, true));
    handle.setAssignableTypes(new Class[] { LifecycleListenerRegistrarImpl.class, Object.class, LifecycleListenerRegistrar.class });
  }

  public LifecycleListenerRegistrarImpl createInstance(final ContextManager contextManager) {
    final LifecycleListenerRegistrarImpl instance = new LifecycleListenerRegistrarImpl();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}
package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.lifecycle.api.Creation;
import org.jboss.errai.ioc.client.lifecycle.api.LifecycleEvent;
import org.jboss.errai.ioc.client.lifecycle.impl.CreationImpl;
import org.jboss.errai.ioc.client.lifecycle.impl.LifecycleEventImpl;

public class Type_factory__o_j_e_i_c_l_i_CreationImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<CreationImpl> { public Type_factory__o_j_e_i_c_l_i_CreationImpl__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(CreationImpl.class, "Type_factory__o_j_e_i_c_l_i_CreationImpl__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { CreationImpl.class, LifecycleEventImpl.class, Object.class, LifecycleEvent.class, Creation.class, LifecycleEvent.class });
  }

  public CreationImpl createInstance(final ContextManager contextManager) {
    final CreationImpl instance = new CreationImpl();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}
package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.lifecycle.api.Access;
import org.jboss.errai.ioc.client.lifecycle.api.LifecycleEvent;
import org.jboss.errai.ioc.client.lifecycle.impl.AccessImpl;
import org.jboss.errai.ioc.client.lifecycle.impl.LifecycleEventImpl;

public class Type_factory__o_j_e_i_c_l_i_AccessImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<AccessImpl> { public Type_factory__o_j_e_i_c_l_i_AccessImpl__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(AccessImpl.class, "Type_factory__o_j_e_i_c_l_i_AccessImpl__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { AccessImpl.class, LifecycleEventImpl.class, Object.class, LifecycleEvent.class, Access.class, LifecycleEvent.class });
  }

  public AccessImpl createInstance(final ContextManager contextManager) {
    final AccessImpl instance = new AccessImpl();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}
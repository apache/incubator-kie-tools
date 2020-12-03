package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.lifecycle.api.LifecycleEvent;
import org.jboss.errai.ioc.client.lifecycle.api.StateChange;
import org.jboss.errai.ioc.client.lifecycle.impl.LifecycleEventImpl;
import org.jboss.errai.ioc.client.lifecycle.impl.StateChangeImpl;

public class Type_factory__o_j_e_i_c_l_i_StateChangeImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<StateChangeImpl> { public Type_factory__o_j_e_i_c_l_i_StateChangeImpl__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(StateChangeImpl.class, "Type_factory__o_j_e_i_c_l_i_StateChangeImpl__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { StateChangeImpl.class, LifecycleEventImpl.class, Object.class, LifecycleEvent.class, StateChange.class, LifecycleEvent.class });
  }

  public StateChangeImpl createInstance(final ContextManager contextManager) {
    final StateChangeImpl instance = new StateChangeImpl();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}
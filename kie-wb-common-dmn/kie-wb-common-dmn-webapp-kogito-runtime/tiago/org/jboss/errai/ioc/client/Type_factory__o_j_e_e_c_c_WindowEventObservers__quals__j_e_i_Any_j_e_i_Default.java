package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.enterprise.client.cdi.WindowEventObservers;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;

public class Type_factory__o_j_e_e_c_c_WindowEventObservers__quals__j_e_i_Any_j_e_i_Default extends Factory<WindowEventObservers> { public Type_factory__o_j_e_e_c_c_WindowEventObservers__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(WindowEventObservers.class, "Type_factory__o_j_e_e_c_c_WindowEventObservers__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { WindowEventObservers.class, Object.class });
  }

  public WindowEventObservers createInstance(final ContextManager contextManager) {
    final WindowEventObservers instance = new WindowEventObservers();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}
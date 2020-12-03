package org.jboss.errai.ioc.client;

import javax.inject.Singleton;
import org.jboss.errai.enterprise.client.cdi.EventProvider;
import org.jboss.errai.ioc.client.api.ContextualTypeProvider;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;

public class Type_factory__o_j_e_e_c_c_EventProvider__quals__j_e_i_Any_j_e_i_Default extends Factory<EventProvider> { public Type_factory__o_j_e_e_c_c_EventProvider__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(EventProvider.class, "Type_factory__o_j_e_e_c_c_EventProvider__quals__j_e_i_Any_j_e_i_Default", Singleton.class, false, null, true));
    handle.setAssignableTypes(new Class[] { EventProvider.class, Object.class, ContextualTypeProvider.class });
  }

  public EventProvider createInstance(final ContextManager contextManager) {
    final EventProvider instance = new EventProvider();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}
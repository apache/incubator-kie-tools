package org.jboss.errai.ioc.client;

import javax.inject.Singleton;
import org.jboss.errai.enterprise.client.cdi.InstanceProvider;
import org.jboss.errai.ioc.client.api.ContextualTypeProvider;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;

public class Type_factory__o_j_e_e_c_c_InstanceProvider__quals__j_e_i_Any_j_e_i_Default extends Factory<InstanceProvider> { public Type_factory__o_j_e_e_c_c_InstanceProvider__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(InstanceProvider.class, "Type_factory__o_j_e_e_c_c_InstanceProvider__quals__j_e_i_Any_j_e_i_Default", Singleton.class, false, null, true));
    handle.setAssignableTypes(new Class[] { InstanceProvider.class, Object.class, ContextualTypeProvider.class });
  }

  public InstanceProvider createInstance(final ContextManager contextManager) {
    final InstanceProvider instance = new InstanceProvider();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}
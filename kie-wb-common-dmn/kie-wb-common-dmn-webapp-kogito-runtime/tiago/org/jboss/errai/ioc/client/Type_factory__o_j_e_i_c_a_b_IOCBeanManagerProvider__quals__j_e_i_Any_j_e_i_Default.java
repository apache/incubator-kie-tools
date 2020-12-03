package org.jboss.errai.ioc.client;

import javax.inject.Singleton;
import org.jboss.errai.ioc.client.api.builtin.IOCBeanManagerProvider;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;

public class Type_factory__o_j_e_i_c_a_b_IOCBeanManagerProvider__quals__j_e_i_Any_j_e_i_Default extends Factory<IOCBeanManagerProvider> { public Type_factory__o_j_e_i_c_a_b_IOCBeanManagerProvider__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(IOCBeanManagerProvider.class, "Type_factory__o_j_e_i_c_a_b_IOCBeanManagerProvider__quals__j_e_i_Any_j_e_i_Default", Singleton.class, false, null, true));
    handle.setAssignableTypes(new Class[] { IOCBeanManagerProvider.class, Object.class });
  }

  public IOCBeanManagerProvider createInstance(final ContextManager contextManager) {
    final IOCBeanManagerProvider instance = new IOCBeanManagerProvider();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}
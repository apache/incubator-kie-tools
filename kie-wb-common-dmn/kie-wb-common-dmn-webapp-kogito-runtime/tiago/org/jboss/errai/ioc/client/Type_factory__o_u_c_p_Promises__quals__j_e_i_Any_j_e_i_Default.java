package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.uberfire.client.promise.Promises;

public class Type_factory__o_u_c_p_Promises__quals__j_e_i_Any_j_e_i_Default extends Factory<Promises> { public Type_factory__o_u_c_p_Promises__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(Promises.class, "Type_factory__o_u_c_p_Promises__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { Promises.class, Object.class });
  }

  public Promises createInstance(final ContextManager contextManager) {
    final Promises instance = new Promises();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void invokePostConstructs(final Promises instance) {
    instance.init();
  }
}
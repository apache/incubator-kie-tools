package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.uberfire.client.util.Clipboard;

public class Type_factory__o_u_c_u_Clipboard__quals__j_e_i_Any_j_e_i_Default extends Factory<Clipboard> { public Type_factory__o_u_c_u_Clipboard__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(Clipboard.class, "Type_factory__o_u_c_u_Clipboard__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { Clipboard.class, Object.class });
  }

  public Clipboard createInstance(final ContextManager contextManager) {
    final Clipboard instance = new Clipboard();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}
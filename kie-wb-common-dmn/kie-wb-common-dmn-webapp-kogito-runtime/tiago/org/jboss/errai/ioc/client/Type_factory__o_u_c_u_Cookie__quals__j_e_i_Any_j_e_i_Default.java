package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.uberfire.client.util.Cookie;

public class Type_factory__o_u_c_u_Cookie__quals__j_e_i_Any_j_e_i_Default extends Factory<Cookie> { public Type_factory__o_u_c_u_Cookie__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(Cookie.class, "Type_factory__o_u_c_u_Cookie__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { Cookie.class, Object.class });
  }

  public Cookie createInstance(final ContextManager contextManager) {
    final Cookie instance = new Cookie();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}
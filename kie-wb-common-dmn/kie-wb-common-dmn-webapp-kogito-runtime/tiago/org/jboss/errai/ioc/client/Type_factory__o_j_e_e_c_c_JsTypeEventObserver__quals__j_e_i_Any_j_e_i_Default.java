package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.enterprise.client.cdi.JsTypeEventObserver;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;

public class Type_factory__o_j_e_e_c_c_JsTypeEventObserver__quals__j_e_i_Any_j_e_i_Default extends Factory<JsTypeEventObserver> { public Type_factory__o_j_e_e_c_c_JsTypeEventObserver__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(JsTypeEventObserver.class, "Type_factory__o_j_e_e_c_c_JsTypeEventObserver__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { JsTypeEventObserver.class, Object.class });
  }

  public JsTypeEventObserver createInstance(final ContextManager contextManager) {
    final JsTypeEventObserver instance = new JsTypeEventObserver();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}
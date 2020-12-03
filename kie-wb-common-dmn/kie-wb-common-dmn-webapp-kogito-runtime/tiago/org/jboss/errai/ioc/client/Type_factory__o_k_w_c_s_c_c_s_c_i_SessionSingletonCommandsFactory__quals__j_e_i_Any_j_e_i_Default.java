package org.jboss.errai.ioc.client;

import javax.inject.Singleton;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.stunner.core.client.session.command.impl.SessionSingletonCommandsFactory;

public class Type_factory__o_k_w_c_s_c_c_s_c_i_SessionSingletonCommandsFactory__quals__j_e_i_Any_j_e_i_Default extends Factory<SessionSingletonCommandsFactory> { public Type_factory__o_k_w_c_s_c_c_s_c_i_SessionSingletonCommandsFactory__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(SessionSingletonCommandsFactory.class, "Type_factory__o_k_w_c_s_c_c_s_c_i_SessionSingletonCommandsFactory__quals__j_e_i_Any_j_e_i_Default", Singleton.class, false, null, true));
    handle.setAssignableTypes(new Class[] { SessionSingletonCommandsFactory.class, Object.class });
  }

  public SessionSingletonCommandsFactory createInstance(final ContextManager contextManager) {
    final SessionSingletonCommandsFactory instance = new SessionSingletonCommandsFactory();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}
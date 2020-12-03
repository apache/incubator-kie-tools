package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.uberfire.client.mvp.LockDemandDetector;

public class Type_factory__o_u_c_m_LockDemandDetector__quals__j_e_i_Any_j_e_i_Default extends Factory<LockDemandDetector> { public Type_factory__o_u_c_m_LockDemandDetector__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(LockDemandDetector.class, "Type_factory__o_u_c_m_LockDemandDetector__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { LockDemandDetector.class, Object.class });
  }

  public LockDemandDetector createInstance(final ContextManager contextManager) {
    final LockDemandDetector instance = new LockDemandDetector();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}
package org.jboss.errai.ioc.client;

import org.jboss.errai.ioc.client.api.EntryPoint;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.stunner.core.StunnerCoreEntryPoint;

public class Type_factory__o_k_w_c_s_c_StunnerCoreEntryPoint__quals__j_e_i_Any_j_e_i_Default extends Factory<StunnerCoreEntryPoint> { public Type_factory__o_k_w_c_s_c_StunnerCoreEntryPoint__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(StunnerCoreEntryPoint.class, "Type_factory__o_k_w_c_s_c_StunnerCoreEntryPoint__quals__j_e_i_Any_j_e_i_Default", EntryPoint.class, true, null, true));
    handle.setAssignableTypes(new Class[] { StunnerCoreEntryPoint.class, Object.class });
  }

  public StunnerCoreEntryPoint createInstance(final ContextManager contextManager) {
    final StunnerCoreEntryPoint instance = new StunnerCoreEntryPoint();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}
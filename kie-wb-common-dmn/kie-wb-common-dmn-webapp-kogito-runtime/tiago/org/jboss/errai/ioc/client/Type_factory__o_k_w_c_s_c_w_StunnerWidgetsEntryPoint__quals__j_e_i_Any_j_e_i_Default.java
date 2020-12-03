package org.jboss.errai.ioc.client;

import org.jboss.errai.ioc.client.api.EntryPoint;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.stunner.client.widgets.StunnerWidgetsEntryPoint;

public class Type_factory__o_k_w_c_s_c_w_StunnerWidgetsEntryPoint__quals__j_e_i_Any_j_e_i_Default extends Factory<StunnerWidgetsEntryPoint> { public Type_factory__o_k_w_c_s_c_w_StunnerWidgetsEntryPoint__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(StunnerWidgetsEntryPoint.class, "Type_factory__o_k_w_c_s_c_w_StunnerWidgetsEntryPoint__quals__j_e_i_Any_j_e_i_Default", EntryPoint.class, true, null, true));
    handle.setAssignableTypes(new Class[] { StunnerWidgetsEntryPoint.class, Object.class });
  }

  public StunnerWidgetsEntryPoint createInstance(final ContextManager contextManager) {
    final StunnerWidgetsEntryPoint instance = new StunnerWidgetsEntryPoint();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}
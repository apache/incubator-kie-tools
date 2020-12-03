package org.jboss.errai.ioc.client;

import org.jboss.errai.ioc.client.api.EntryPoint;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.dmn.api.DMNAPIEntryPoint;

public class Type_factory__o_k_w_c_d_a_DMNAPIEntryPoint__quals__j_e_i_Any_j_e_i_Default extends Factory<DMNAPIEntryPoint> { public Type_factory__o_k_w_c_d_a_DMNAPIEntryPoint__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DMNAPIEntryPoint.class, "Type_factory__o_k_w_c_d_a_DMNAPIEntryPoint__quals__j_e_i_Any_j_e_i_Default", EntryPoint.class, true, null, true));
    handle.setAssignableTypes(new Class[] { DMNAPIEntryPoint.class, Object.class });
  }

  public DMNAPIEntryPoint createInstance(final ContextManager contextManager) {
    final DMNAPIEntryPoint instance = new DMNAPIEntryPoint();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}
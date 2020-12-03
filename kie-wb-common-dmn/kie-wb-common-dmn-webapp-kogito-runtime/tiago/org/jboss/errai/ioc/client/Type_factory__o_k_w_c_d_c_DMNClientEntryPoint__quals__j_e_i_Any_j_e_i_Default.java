package org.jboss.errai.ioc.client;

import org.jboss.errai.ioc.client.api.EntryPoint;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.dmn.client.DMNClientEntryPoint;

public class Type_factory__o_k_w_c_d_c_DMNClientEntryPoint__quals__j_e_i_Any_j_e_i_Default extends Factory<DMNClientEntryPoint> { public Type_factory__o_k_w_c_d_c_DMNClientEntryPoint__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DMNClientEntryPoint.class, "Type_factory__o_k_w_c_d_c_DMNClientEntryPoint__quals__j_e_i_Any_j_e_i_Default", EntryPoint.class, true, null, true));
    handle.setAssignableTypes(new Class[] { DMNClientEntryPoint.class, Object.class });
  }

  public DMNClientEntryPoint createInstance(final ContextManager contextManager) {
    final DMNClientEntryPoint instance = new DMNClientEntryPoint();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void invokePostConstructs(final DMNClientEntryPoint instance) {
    instance.init();
  }
}
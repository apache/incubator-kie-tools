package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.uberfire.client.workbench.VFSLockServiceProxy;
import org.uberfire.client.workbench.VFSLockServiceProxyClientImpl;

public class Type_factory__o_u_c_w_VFSLockServiceProxyClientImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<VFSLockServiceProxyClientImpl> { public Type_factory__o_u_c_w_VFSLockServiceProxyClientImpl__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(VFSLockServiceProxyClientImpl.class, "Type_factory__o_u_c_w_VFSLockServiceProxyClientImpl__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { VFSLockServiceProxyClientImpl.class, Object.class, VFSLockServiceProxy.class });
  }

  public VFSLockServiceProxyClientImpl createInstance(final ContextManager contextManager) {
    final VFSLockServiceProxyClientImpl instance = new VFSLockServiceProxyClientImpl();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}
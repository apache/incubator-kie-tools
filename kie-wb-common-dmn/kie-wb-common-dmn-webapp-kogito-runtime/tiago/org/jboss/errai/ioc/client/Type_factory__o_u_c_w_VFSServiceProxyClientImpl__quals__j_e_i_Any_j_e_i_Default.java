package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.uberfire.client.workbench.VFSServiceProxy;
import org.uberfire.client.workbench.VFSServiceProxyClientImpl;

public class Type_factory__o_u_c_w_VFSServiceProxyClientImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<VFSServiceProxyClientImpl> { public Type_factory__o_u_c_w_VFSServiceProxyClientImpl__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(VFSServiceProxyClientImpl.class, "Type_factory__o_u_c_w_VFSServiceProxyClientImpl__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { VFSServiceProxyClientImpl.class, Object.class, VFSServiceProxy.class });
  }

  public VFSServiceProxyClientImpl createInstance(final ContextManager contextManager) {
    final VFSServiceProxyClientImpl instance = new VFSServiceProxyClientImpl();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}
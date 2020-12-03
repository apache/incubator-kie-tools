package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.uberfire.client.workbench.WorkbenchServicesProxy;
import org.uberfire.client.workbench.WorkbenchServicesProxyClientImpl;

public class Type_factory__o_u_c_w_WorkbenchServicesProxyClientImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<WorkbenchServicesProxyClientImpl> { public Type_factory__o_u_c_w_WorkbenchServicesProxyClientImpl__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(WorkbenchServicesProxyClientImpl.class, "Type_factory__o_u_c_w_WorkbenchServicesProxyClientImpl__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { WorkbenchServicesProxyClientImpl.class, Object.class, WorkbenchServicesProxy.class });
  }

  public WorkbenchServicesProxyClientImpl createInstance(final ContextManager contextManager) {
    final WorkbenchServicesProxyClientImpl instance = new WorkbenchServicesProxyClientImpl();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}
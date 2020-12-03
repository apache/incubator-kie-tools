package org.jboss.errai.ioc.client;

import org.jboss.errai.ioc.client.api.EntryPoint;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.uberfire.ext.widgets.core.client.CoreEntryPoint;

public class Type_factory__o_u_e_w_c_c_CoreEntryPoint__quals__j_e_i_Any_j_e_i_Default extends Factory<CoreEntryPoint> { public Type_factory__o_u_e_w_c_c_CoreEntryPoint__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(CoreEntryPoint.class, "Type_factory__o_u_e_w_c_c_CoreEntryPoint__quals__j_e_i_Any_j_e_i_Default", EntryPoint.class, true, null, true));
    handle.setAssignableTypes(new Class[] { CoreEntryPoint.class, Object.class });
  }

  public CoreEntryPoint createInstance(final ContextManager contextManager) {
    final CoreEntryPoint instance = new CoreEntryPoint();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void invokePostConstructs(final CoreEntryPoint instance) {
    instance.startApp();
  }
}
package org.jboss.errai.ioc.client;

import org.jboss.errai.ioc.client.api.EntryPoint;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.uberfire.client.DocksEntryPoint;

public class Type_factory__o_u_c_DocksEntryPoint__quals__j_e_i_Any_j_e_i_Default extends Factory<DocksEntryPoint> { public Type_factory__o_u_c_DocksEntryPoint__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DocksEntryPoint.class, "Type_factory__o_u_c_DocksEntryPoint__quals__j_e_i_Any_j_e_i_Default", EntryPoint.class, true, null, true));
    handle.setAssignableTypes(new Class[] { DocksEntryPoint.class, Object.class });
  }

  public DocksEntryPoint createInstance(final ContextManager contextManager) {
    final DocksEntryPoint instance = new DocksEntryPoint();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void invokePostConstructs(final DocksEntryPoint instance) {
    instance.init();
  }
}
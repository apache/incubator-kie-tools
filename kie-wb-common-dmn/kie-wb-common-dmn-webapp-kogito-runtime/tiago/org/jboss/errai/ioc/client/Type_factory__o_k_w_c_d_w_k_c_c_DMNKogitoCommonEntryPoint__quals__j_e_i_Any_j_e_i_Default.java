package org.jboss.errai.ioc.client;

import org.jboss.errai.ioc.client.api.EntryPoint;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.dmn.webapp.kogito.common.client.DMNKogitoCommonEntryPoint;

public class Type_factory__o_k_w_c_d_w_k_c_c_DMNKogitoCommonEntryPoint__quals__j_e_i_Any_j_e_i_Default extends Factory<DMNKogitoCommonEntryPoint> { public Type_factory__o_k_w_c_d_w_k_c_c_DMNKogitoCommonEntryPoint__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DMNKogitoCommonEntryPoint.class, "Type_factory__o_k_w_c_d_w_k_c_c_DMNKogitoCommonEntryPoint__quals__j_e_i_Any_j_e_i_Default", EntryPoint.class, true, null, true));
    handle.setAssignableTypes(new Class[] { DMNKogitoCommonEntryPoint.class, Object.class });
  }

  public DMNKogitoCommonEntryPoint createInstance(final ContextManager contextManager) {
    final DMNKogitoCommonEntryPoint instance = new DMNKogitoCommonEntryPoint();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void invokePostConstructs(final DMNKogitoCommonEntryPoint instance) {
    instance.init();
  }
}
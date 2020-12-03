package org.jboss.errai.ioc.client;

import org.jboss.errai.ioc.client.api.EntryPoint;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.stunner.kogito.client.KogitoClientEntryPoint;

public class Type_factory__o_k_w_c_s_k_c_KogitoClientEntryPoint__quals__j_e_i_Any_j_e_i_Default extends Factory<KogitoClientEntryPoint> { public Type_factory__o_k_w_c_s_k_c_KogitoClientEntryPoint__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(KogitoClientEntryPoint.class, "Type_factory__o_k_w_c_s_k_c_KogitoClientEntryPoint__quals__j_e_i_Any_j_e_i_Default", EntryPoint.class, true, null, true));
    handle.setAssignableTypes(new Class[] { KogitoClientEntryPoint.class, Object.class });
  }

  public KogitoClientEntryPoint createInstance(final ContextManager contextManager) {
    final KogitoClientEntryPoint instance = new KogitoClientEntryPoint();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}
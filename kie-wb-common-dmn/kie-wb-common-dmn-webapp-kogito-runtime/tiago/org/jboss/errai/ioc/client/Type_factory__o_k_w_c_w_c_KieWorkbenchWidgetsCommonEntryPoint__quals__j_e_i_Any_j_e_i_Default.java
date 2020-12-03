package org.jboss.errai.ioc.client;

import org.jboss.errai.ioc.client.api.EntryPoint;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.widgets.client.KieWorkbenchWidgetsCommonEntryPoint;

public class Type_factory__o_k_w_c_w_c_KieWorkbenchWidgetsCommonEntryPoint__quals__j_e_i_Any_j_e_i_Default extends Factory<KieWorkbenchWidgetsCommonEntryPoint> { public Type_factory__o_k_w_c_w_c_KieWorkbenchWidgetsCommonEntryPoint__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(KieWorkbenchWidgetsCommonEntryPoint.class, "Type_factory__o_k_w_c_w_c_KieWorkbenchWidgetsCommonEntryPoint__quals__j_e_i_Any_j_e_i_Default", EntryPoint.class, true, null, true));
    handle.setAssignableTypes(new Class[] { KieWorkbenchWidgetsCommonEntryPoint.class, Object.class });
  }

  public KieWorkbenchWidgetsCommonEntryPoint createInstance(final ContextManager contextManager) {
    final KieWorkbenchWidgetsCommonEntryPoint instance = new KieWorkbenchWidgetsCommonEntryPoint();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void invokePostConstructs(final KieWorkbenchWidgetsCommonEntryPoint instance) {
    instance.startApp();
  }
}
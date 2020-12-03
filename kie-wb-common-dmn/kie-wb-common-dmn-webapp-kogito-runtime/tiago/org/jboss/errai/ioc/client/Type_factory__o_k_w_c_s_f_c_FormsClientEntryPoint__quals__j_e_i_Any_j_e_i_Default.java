package org.jboss.errai.ioc.client;

import org.jboss.errai.ioc.client.api.EntryPoint;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.stunner.forms.client.FormsClientEntryPoint;

public class Type_factory__o_k_w_c_s_f_c_FormsClientEntryPoint__quals__j_e_i_Any_j_e_i_Default extends Factory<FormsClientEntryPoint> { public Type_factory__o_k_w_c_s_f_c_FormsClientEntryPoint__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(FormsClientEntryPoint.class, "Type_factory__o_k_w_c_s_f_c_FormsClientEntryPoint__quals__j_e_i_Any_j_e_i_Default", EntryPoint.class, true, null, true));
    handle.setAssignableTypes(new Class[] { FormsClientEntryPoint.class, Object.class });
  }

  public FormsClientEntryPoint createInstance(final ContextManager contextManager) {
    final FormsClientEntryPoint instance = new FormsClientEntryPoint();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}
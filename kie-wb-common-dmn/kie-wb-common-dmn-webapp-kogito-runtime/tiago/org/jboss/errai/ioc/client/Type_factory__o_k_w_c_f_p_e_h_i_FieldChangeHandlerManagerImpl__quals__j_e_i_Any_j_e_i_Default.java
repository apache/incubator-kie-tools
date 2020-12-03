package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.forms.processing.engine.handling.FieldChangeHandlerManager;
import org.kie.workbench.common.forms.processing.engine.handling.impl.FieldChangeHandlerManagerImpl;

public class Type_factory__o_k_w_c_f_p_e_h_i_FieldChangeHandlerManagerImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<FieldChangeHandlerManagerImpl> { public Type_factory__o_k_w_c_f_p_e_h_i_FieldChangeHandlerManagerImpl__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(FieldChangeHandlerManagerImpl.class, "Type_factory__o_k_w_c_f_p_e_h_i_FieldChangeHandlerManagerImpl__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { FieldChangeHandlerManagerImpl.class, Object.class, FieldChangeHandlerManager.class });
  }

  public FieldChangeHandlerManagerImpl createInstance(final ContextManager contextManager) {
    final FieldChangeHandlerManagerImpl instance = new FieldChangeHandlerManagerImpl();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}
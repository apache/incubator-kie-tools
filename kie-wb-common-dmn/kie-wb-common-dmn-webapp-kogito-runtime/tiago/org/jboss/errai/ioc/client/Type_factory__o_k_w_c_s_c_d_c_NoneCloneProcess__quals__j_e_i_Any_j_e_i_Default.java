package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.stunner.core.definition.clone.CloneProcess;
import org.kie.workbench.common.stunner.core.definition.clone.NoneCloneProcess;

public class Type_factory__o_k_w_c_s_c_d_c_NoneCloneProcess__quals__j_e_i_Any_j_e_i_Default extends Factory<NoneCloneProcess> { public Type_factory__o_k_w_c_s_c_d_c_NoneCloneProcess__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(NoneCloneProcess.class, "Type_factory__o_k_w_c_s_c_d_c_NoneCloneProcess__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { NoneCloneProcess.class, Object.class, CloneProcess.class });
  }

  public NoneCloneProcess createInstance(final ContextManager contextManager) {
    final NoneCloneProcess instance = new NoneCloneProcess();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}
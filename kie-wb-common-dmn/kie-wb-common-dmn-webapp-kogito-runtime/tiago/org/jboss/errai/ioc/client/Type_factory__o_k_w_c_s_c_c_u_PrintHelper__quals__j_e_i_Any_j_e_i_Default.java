package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.stunner.core.client.util.PrintHelper;

public class Type_factory__o_k_w_c_s_c_c_u_PrintHelper__quals__j_e_i_Any_j_e_i_Default extends Factory<PrintHelper> { public Type_factory__o_k_w_c_s_c_c_u_PrintHelper__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(PrintHelper.class, "Type_factory__o_k_w_c_s_c_c_u_PrintHelper__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { PrintHelper.class, Object.class });
  }

  public PrintHelper createInstance(final ContextManager contextManager) {
    final PrintHelper instance = new PrintHelper();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}
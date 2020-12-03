package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.dmn.api.graph.DMNDiagramUtils;

public class Type_factory__o_k_w_c_d_a_g_DMNDiagramUtils__quals__j_e_i_Any_j_e_i_Default extends Factory<DMNDiagramUtils> { public Type_factory__o_k_w_c_d_a_g_DMNDiagramUtils__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DMNDiagramUtils.class, "Type_factory__o_k_w_c_d_a_g_DMNDiagramUtils__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DMNDiagramUtils.class, Object.class });
  }

  public DMNDiagramUtils createInstance(final ContextManager contextManager) {
    final DMNDiagramUtils instance = new DMNDiagramUtils();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}
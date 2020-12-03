package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.stunner.core.graph.util.GraphUtils;

public class Type_factory__o_k_w_c_s_c_g_u_GraphUtils__quals__j_e_i_Any_j_e_i_Default extends Factory<GraphUtils> { public Type_factory__o_k_w_c_s_c_g_u_GraphUtils__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(GraphUtils.class, "Type_factory__o_k_w_c_s_c_g_u_GraphUtils__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { GraphUtils.class, Object.class });
  }

  public GraphUtils createInstance(final ContextManager contextManager) {
    final GraphUtils instance = new GraphUtils();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}
package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.stunner.core.graph.processing.layout.sugiyama.step01.CycleBreaker;
import org.kie.workbench.common.stunner.core.graph.processing.layout.sugiyama.step01.ReverseEdgesCycleBreaker;

public class Type_factory__o_k_w_c_s_c_g_p_l_s_s_ReverseEdgesCycleBreaker__quals__j_e_i_Any_j_e_i_Default extends Factory<ReverseEdgesCycleBreaker> { public Type_factory__o_k_w_c_s_c_g_p_l_s_s_ReverseEdgesCycleBreaker__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(ReverseEdgesCycleBreaker.class, "Type_factory__o_k_w_c_s_c_g_p_l_s_s_ReverseEdgesCycleBreaker__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { ReverseEdgesCycleBreaker.class, Object.class, CycleBreaker.class });
  }

  public ReverseEdgesCycleBreaker createInstance(final ContextManager contextManager) {
    final ReverseEdgesCycleBreaker instance = new ReverseEdgesCycleBreaker();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}
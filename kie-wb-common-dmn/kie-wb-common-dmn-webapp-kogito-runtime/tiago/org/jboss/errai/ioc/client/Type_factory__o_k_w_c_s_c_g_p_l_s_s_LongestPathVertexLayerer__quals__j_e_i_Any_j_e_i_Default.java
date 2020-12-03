package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.stunner.core.graph.processing.layout.sugiyama.step02.LongestPathVertexLayerer;
import org.kie.workbench.common.stunner.core.graph.processing.layout.sugiyama.step02.VertexLayerer;

public class Type_factory__o_k_w_c_s_c_g_p_l_s_s_LongestPathVertexLayerer__quals__j_e_i_Any_j_e_i_Default extends Factory<LongestPathVertexLayerer> { public Type_factory__o_k_w_c_s_c_g_p_l_s_s_LongestPathVertexLayerer__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(LongestPathVertexLayerer.class, "Type_factory__o_k_w_c_s_c_g_p_l_s_s_LongestPathVertexLayerer__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { LongestPathVertexLayerer.class, Object.class, VertexLayerer.class });
  }

  public LongestPathVertexLayerer createInstance(final ContextManager contextManager) {
    final LongestPathVertexLayerer instance = new LongestPathVertexLayerer();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}
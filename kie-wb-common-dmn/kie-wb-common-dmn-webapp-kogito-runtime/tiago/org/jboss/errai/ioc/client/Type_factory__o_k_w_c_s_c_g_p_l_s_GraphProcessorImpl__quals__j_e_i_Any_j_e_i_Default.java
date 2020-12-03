package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.stunner.core.graph.processing.layout.GraphProcessor;
import org.kie.workbench.common.stunner.core.graph.processing.layout.sugiyama.GraphProcessorImpl;

public class Type_factory__o_k_w_c_s_c_g_p_l_s_GraphProcessorImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<GraphProcessorImpl> { public Type_factory__o_k_w_c_s_c_g_p_l_s_GraphProcessorImpl__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(GraphProcessorImpl.class, "Type_factory__o_k_w_c_s_c_g_p_l_s_GraphProcessorImpl__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { GraphProcessorImpl.class, Object.class, GraphProcessor.class });
  }

  public GraphProcessorImpl createInstance(final ContextManager contextManager) {
    final GraphProcessorImpl instance = new GraphProcessorImpl();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}
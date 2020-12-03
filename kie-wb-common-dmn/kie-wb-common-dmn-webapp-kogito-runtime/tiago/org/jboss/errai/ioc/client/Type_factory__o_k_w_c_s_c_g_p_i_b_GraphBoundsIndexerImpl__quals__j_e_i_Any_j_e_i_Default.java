package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.stunner.core.graph.processing.index.bounds.BoundsIndexer;
import org.kie.workbench.common.stunner.core.graph.processing.index.bounds.GraphBoundsIndexer;
import org.kie.workbench.common.stunner.core.graph.processing.index.bounds.GraphBoundsIndexerImpl;
import org.kie.workbench.common.stunner.core.graph.processing.index.bounds.NodeBoundsIndexer;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.content.ChildrenTraverseProcessor;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.content.ChildrenTraverseProcessorImpl;

public class Type_factory__o_k_w_c_s_c_g_p_i_b_GraphBoundsIndexerImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<GraphBoundsIndexerImpl> { public Type_factory__o_k_w_c_s_c_g_p_i_b_GraphBoundsIndexerImpl__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(GraphBoundsIndexerImpl.class, "Type_factory__o_k_w_c_s_c_g_p_i_b_GraphBoundsIndexerImpl__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { GraphBoundsIndexerImpl.class, Object.class, GraphBoundsIndexer.class, NodeBoundsIndexer.class, BoundsIndexer.class });
  }

  public GraphBoundsIndexerImpl createInstance(final ContextManager contextManager) {
    final ChildrenTraverseProcessor _childrenTraverseProcessor_0 = (ChildrenTraverseProcessorImpl) contextManager.getInstance("Type_factory__o_k_w_c_s_c_g_p_t_c_ChildrenTraverseProcessorImpl__quals__j_e_i_Any_j_e_i_Default");
    final GraphBoundsIndexerImpl instance = new GraphBoundsIndexerImpl(_childrenTraverseProcessor_0);
    registerDependentScopedReference(instance, _childrenTraverseProcessor_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}
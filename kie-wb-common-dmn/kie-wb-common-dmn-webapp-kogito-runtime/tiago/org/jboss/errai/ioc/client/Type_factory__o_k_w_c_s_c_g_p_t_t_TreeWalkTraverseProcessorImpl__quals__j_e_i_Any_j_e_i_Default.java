package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.TraverseProcessor;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.tree.TreeTraverseProcessor;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.tree.TreeWalkTraverseProcessor;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.tree.TreeWalkTraverseProcessorImpl;

public class Type_factory__o_k_w_c_s_c_g_p_t_t_TreeWalkTraverseProcessorImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<TreeWalkTraverseProcessorImpl> { public Type_factory__o_k_w_c_s_c_g_p_t_t_TreeWalkTraverseProcessorImpl__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(TreeWalkTraverseProcessorImpl.class, "Type_factory__o_k_w_c_s_c_g_p_t_t_TreeWalkTraverseProcessorImpl__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { TreeWalkTraverseProcessorImpl.class, Object.class, TreeWalkTraverseProcessor.class, TreeTraverseProcessor.class, TraverseProcessor.class });
  }

  public TreeWalkTraverseProcessorImpl createInstance(final ContextManager contextManager) {
    final TreeWalkTraverseProcessorImpl instance = new TreeWalkTraverseProcessorImpl();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}
package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.TraverseProcessor;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.content.ContentTraverseProcessor;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.content.FullContentTraverseProcessor;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.content.FullContentTraverseProcessorImpl;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.tree.TreeWalkTraverseProcessor;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.tree.TreeWalkTraverseProcessorImpl;

public class Type_factory__o_k_w_c_s_c_g_p_t_c_FullContentTraverseProcessorImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<FullContentTraverseProcessorImpl> { public Type_factory__o_k_w_c_s_c_g_p_t_c_FullContentTraverseProcessorImpl__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(FullContentTraverseProcessorImpl.class, "Type_factory__o_k_w_c_s_c_g_p_t_c_FullContentTraverseProcessorImpl__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { FullContentTraverseProcessorImpl.class, Object.class, FullContentTraverseProcessor.class, ContentTraverseProcessor.class, TraverseProcessor.class });
  }

  public FullContentTraverseProcessorImpl createInstance(final ContextManager contextManager) {
    final TreeWalkTraverseProcessor _treeWalkTraverseProcessor_0 = (TreeWalkTraverseProcessorImpl) contextManager.getInstance("Type_factory__o_k_w_c_s_c_g_p_t_t_TreeWalkTraverseProcessorImpl__quals__j_e_i_Any_j_e_i_Default");
    final FullContentTraverseProcessorImpl instance = new FullContentTraverseProcessorImpl(_treeWalkTraverseProcessor_0);
    registerDependentScopedReference(instance, _treeWalkTraverseProcessor_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}
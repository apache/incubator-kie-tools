package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.TraverseProcessor;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.content.AbstractContentTraverseProcessor;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.content.ContentTraverseProcessor;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.content.ViewTraverseProcessor;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.content.ViewTraverseProcessorImpl;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.tree.TreeWalkTraverseProcessor;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.tree.TreeWalkTraverseProcessorImpl;

public class Type_factory__o_k_w_c_s_c_g_p_t_c_ViewTraverseProcessorImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<ViewTraverseProcessorImpl> { public Type_factory__o_k_w_c_s_c_g_p_t_c_ViewTraverseProcessorImpl__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(ViewTraverseProcessorImpl.class, "Type_factory__o_k_w_c_s_c_g_p_t_c_ViewTraverseProcessorImpl__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { ViewTraverseProcessorImpl.class, AbstractContentTraverseProcessor.class, Object.class, ContentTraverseProcessor.class, TraverseProcessor.class, ViewTraverseProcessor.class });
  }

  public ViewTraverseProcessorImpl createInstance(final ContextManager contextManager) {
    final TreeWalkTraverseProcessor _treeWalkTraverseProcessor_0 = (TreeWalkTraverseProcessorImpl) contextManager.getInstance("Type_factory__o_k_w_c_s_c_g_p_t_t_TreeWalkTraverseProcessorImpl__quals__j_e_i_Any_j_e_i_Default");
    final ViewTraverseProcessorImpl instance = new ViewTraverseProcessorImpl(_treeWalkTraverseProcessor_0);
    registerDependentScopedReference(instance, _treeWalkTraverseProcessor_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}
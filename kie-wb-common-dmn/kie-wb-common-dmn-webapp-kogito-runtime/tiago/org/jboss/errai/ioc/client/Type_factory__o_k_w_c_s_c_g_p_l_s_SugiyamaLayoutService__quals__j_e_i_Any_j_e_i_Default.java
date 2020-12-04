package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.stunner.core.graph.processing.layout.AbstractLayoutService;
import org.kie.workbench.common.stunner.core.graph.processing.layout.GraphProcessor;
import org.kie.workbench.common.stunner.core.graph.processing.layout.LayoutService;
import org.kie.workbench.common.stunner.core.graph.processing.layout.sugiyama.GraphProcessorImpl;
import org.kie.workbench.common.stunner.core.graph.processing.layout.sugiyama.SugiyamaLayoutService;
import org.kie.workbench.common.stunner.core.graph.processing.layout.sugiyama.step01.CycleBreaker;
import org.kie.workbench.common.stunner.core.graph.processing.layout.sugiyama.step01.ReverseEdgesCycleBreaker;
import org.kie.workbench.common.stunner.core.graph.processing.layout.sugiyama.step02.LongestPathVertexLayerer;
import org.kie.workbench.common.stunner.core.graph.processing.layout.sugiyama.step02.VertexLayerer;
import org.kie.workbench.common.stunner.core.graph.processing.layout.sugiyama.step03.DefaultVertexOrdering;
import org.kie.workbench.common.stunner.core.graph.processing.layout.sugiyama.step03.VertexOrdering;
import org.kie.workbench.common.stunner.core.graph.processing.layout.sugiyama.step04.DefaultVertexPositioning;
import org.kie.workbench.common.stunner.core.graph.processing.layout.sugiyama.step04.VertexPositioning;

public class Type_factory__o_k_w_c_s_c_g_p_l_s_SugiyamaLayoutService__quals__j_e_i_Any_j_e_i_Default extends Factory<SugiyamaLayoutService> { public Type_factory__o_k_w_c_s_c_g_p_l_s_SugiyamaLayoutService__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(SugiyamaLayoutService.class, "Type_factory__o_k_w_c_s_c_g_p_l_s_SugiyamaLayoutService__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { SugiyamaLayoutService.class, AbstractLayoutService.class, Object.class, LayoutService.class });
  }

  public SugiyamaLayoutService createInstance(final ContextManager contextManager) {
    final GraphProcessor _graphProcessor_4 = (GraphProcessorImpl) contextManager.getInstance("Type_factory__o_k_w_c_s_c_g_p_l_s_GraphProcessorImpl__quals__j_e_i_Any_j_e_i_Default");
    final CycleBreaker _cycleBreaker_0 = (ReverseEdgesCycleBreaker) contextManager.getInstance("Type_factory__o_k_w_c_s_c_g_p_l_s_s_ReverseEdgesCycleBreaker__quals__j_e_i_Any_j_e_i_Default");
    final VertexOrdering _vertexOrdering_2 = (DefaultVertexOrdering) contextManager.getInstance("Type_factory__o_k_w_c_s_c_g_p_l_s_s_DefaultVertexOrdering__quals__j_e_i_Any_j_e_i_Default");
    final VertexPositioning _vertexPositioning_3 = (DefaultVertexPositioning) contextManager.getInstance("Type_factory__o_k_w_c_s_c_g_p_l_s_s_DefaultVertexPositioning__quals__j_e_i_Any_j_e_i_Default");
    final VertexLayerer _vertexLayerer_1 = (LongestPathVertexLayerer) contextManager.getInstance("Type_factory__o_k_w_c_s_c_g_p_l_s_s_LongestPathVertexLayerer__quals__j_e_i_Any_j_e_i_Default");
    final SugiyamaLayoutService instance = new SugiyamaLayoutService(_cycleBreaker_0, _vertexLayerer_1, _vertexOrdering_2, _vertexPositioning_3, _graphProcessor_4);
    registerDependentScopedReference(instance, _graphProcessor_4);
    registerDependentScopedReference(instance, _cycleBreaker_0);
    registerDependentScopedReference(instance, _vertexOrdering_2);
    registerDependentScopedReference(instance, _vertexPositioning_3);
    registerDependentScopedReference(instance, _vertexLayerer_1);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}
package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.stunner.core.graph.processing.layout.sugiyama.step03.DefaultVertexOrdering;
import org.kie.workbench.common.stunner.core.graph.processing.layout.sugiyama.step03.LayerCrossingCount;
import org.kie.workbench.common.stunner.core.graph.processing.layout.sugiyama.step03.MedianVertexLayerPositioning;
import org.kie.workbench.common.stunner.core.graph.processing.layout.sugiyama.step03.VertexLayerPositioning;
import org.kie.workbench.common.stunner.core.graph.processing.layout.sugiyama.step03.VertexOrdering;
import org.kie.workbench.common.stunner.core.graph.processing.layout.sugiyama.step03.VerticesTransposer;

public class Type_factory__o_k_w_c_s_c_g_p_l_s_s_DefaultVertexOrdering__quals__j_e_i_Any_j_e_i_Default extends Factory<DefaultVertexOrdering> { public Type_factory__o_k_w_c_s_c_g_p_l_s_s_DefaultVertexOrdering__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DefaultVertexOrdering.class, "Type_factory__o_k_w_c_s_c_g_p_l_s_s_DefaultVertexOrdering__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DefaultVertexOrdering.class, Object.class, VertexOrdering.class });
  }

  public DefaultVertexOrdering createInstance(final ContextManager contextManager) {
    final LayerCrossingCount _crossingCount_1 = (LayerCrossingCount) contextManager.getInstance("Type_factory__o_k_w_c_s_c_g_p_l_s_s_LayerCrossingCount__quals__j_e_i_Any_j_e_i_Default");
    final VerticesTransposer _verticesTransposer_2 = (VerticesTransposer) contextManager.getInstance("Type_factory__o_k_w_c_s_c_g_p_l_s_s_VerticesTransposer__quals__j_e_i_Any_j_e_i_Default");
    final VertexLayerPositioning _vertexPositioning_0 = (MedianVertexLayerPositioning) contextManager.getInstance("Type_factory__o_k_w_c_s_c_g_p_l_s_s_MedianVertexLayerPositioning__quals__j_e_i_Any_j_e_i_Default");
    final DefaultVertexOrdering instance = new DefaultVertexOrdering(_vertexPositioning_0, _crossingCount_1, _verticesTransposer_2);
    registerDependentScopedReference(instance, _crossingCount_1);
    registerDependentScopedReference(instance, _verticesTransposer_2);
    registerDependentScopedReference(instance, _vertexPositioning_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}
package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.dmn.api.qualifiers.DMNEditor;
import org.kie.workbench.common.dmn.client.widgets.toolbar.DMNGraphProcessor;
import org.kie.workbench.common.dmn.client.widgets.toolbar.DMNSugiyamaLayoutService;
import org.kie.workbench.common.stunner.core.graph.processing.layout.AbstractLayoutService;
import org.kie.workbench.common.stunner.core.graph.processing.layout.LayoutService;
import org.kie.workbench.common.stunner.core.graph.processing.layout.sugiyama.SugiyamaLayoutService;
import org.kie.workbench.common.stunner.core.graph.processing.layout.sugiyama.step01.CycleBreaker;
import org.kie.workbench.common.stunner.core.graph.processing.layout.sugiyama.step01.ReverseEdgesCycleBreaker;
import org.kie.workbench.common.stunner.core.graph.processing.layout.sugiyama.step02.LongestPathVertexLayerer;
import org.kie.workbench.common.stunner.core.graph.processing.layout.sugiyama.step02.VertexLayerer;
import org.kie.workbench.common.stunner.core.graph.processing.layout.sugiyama.step03.DefaultVertexOrdering;
import org.kie.workbench.common.stunner.core.graph.processing.layout.sugiyama.step03.VertexOrdering;
import org.kie.workbench.common.stunner.core.graph.processing.layout.sugiyama.step04.DefaultVertexPositioning;
import org.kie.workbench.common.stunner.core.graph.processing.layout.sugiyama.step04.VertexPositioning;

public class Type_factory__o_k_w_c_d_c_w_t_DMNSugiyamaLayoutService__quals__j_e_i_Any_o_k_w_c_d_a_q_DMNEditor extends Factory<DMNSugiyamaLayoutService> { public Type_factory__o_k_w_c_d_c_w_t_DMNSugiyamaLayoutService__quals__j_e_i_Any_o_k_w_c_d_a_q_DMNEditor() {
    super(new FactoryHandleImpl(DMNSugiyamaLayoutService.class, "Type_factory__o_k_w_c_d_c_w_t_DMNSugiyamaLayoutService__quals__j_e_i_Any_o_k_w_c_d_a_q_DMNEditor", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DMNSugiyamaLayoutService.class, SugiyamaLayoutService.class, AbstractLayoutService.class, Object.class, LayoutService.class });
    handle.setQualifiers(new Annotation[] { QualifierUtil.ANY_ANNOTATION, new DMNEditor() {
        public Class annotationType() {
          return DMNEditor.class;
        }
        public String toString() {
          return "@org.kie.workbench.common.dmn.api.qualifiers.DMNEditor()";
        }
    } });
  }

  public DMNSugiyamaLayoutService createInstance(final ContextManager contextManager) {
    final VertexLayerer _vertexLayerer_1 = (LongestPathVertexLayerer) contextManager.getInstance("Type_factory__o_k_w_c_s_c_g_p_l_s_s_LongestPathVertexLayerer__quals__j_e_i_Any_j_e_i_Default");
    final CycleBreaker _cycleBreaker_0 = (ReverseEdgesCycleBreaker) contextManager.getInstance("Type_factory__o_k_w_c_s_c_g_p_l_s_s_ReverseEdgesCycleBreaker__quals__j_e_i_Any_j_e_i_Default");
    final VertexPositioning _vertexPositioning_3 = (DefaultVertexPositioning) contextManager.getInstance("Type_factory__o_k_w_c_s_c_g_p_l_s_s_DefaultVertexPositioning__quals__j_e_i_Any_j_e_i_Default");
    final DMNGraphProcessor _graphProcessor_4 = (DMNGraphProcessor) contextManager.getInstance("Type_factory__o_k_w_c_d_c_w_t_DMNGraphProcessor__quals__j_e_i_Any_o_k_w_c_d_a_q_DMNEditor");
    final VertexOrdering _vertexOrdering_2 = (DefaultVertexOrdering) contextManager.getInstance("Type_factory__o_k_w_c_s_c_g_p_l_s_s_DefaultVertexOrdering__quals__j_e_i_Any_j_e_i_Default");
    final DMNSugiyamaLayoutService instance = new DMNSugiyamaLayoutService(_cycleBreaker_0, _vertexLayerer_1, _vertexOrdering_2, _vertexPositioning_3, _graphProcessor_4);
    registerDependentScopedReference(instance, _vertexLayerer_1);
    registerDependentScopedReference(instance, _cycleBreaker_0);
    registerDependentScopedReference(instance, _vertexPositioning_3);
    registerDependentScopedReference(instance, _graphProcessor_4);
    registerDependentScopedReference(instance, _vertexOrdering_2);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}
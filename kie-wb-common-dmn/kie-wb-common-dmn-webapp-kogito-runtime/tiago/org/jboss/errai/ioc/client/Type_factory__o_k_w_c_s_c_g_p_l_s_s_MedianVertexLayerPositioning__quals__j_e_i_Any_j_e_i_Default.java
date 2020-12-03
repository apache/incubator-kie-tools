package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.stunner.core.graph.processing.layout.sugiyama.step03.MedianVertexLayerPositioning;
import org.kie.workbench.common.stunner.core.graph.processing.layout.sugiyama.step03.VertexLayerPositioning;

public class Type_factory__o_k_w_c_s_c_g_p_l_s_s_MedianVertexLayerPositioning__quals__j_e_i_Any_j_e_i_Default extends Factory<MedianVertexLayerPositioning> { public Type_factory__o_k_w_c_s_c_g_p_l_s_s_MedianVertexLayerPositioning__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(MedianVertexLayerPositioning.class, "Type_factory__o_k_w_c_s_c_g_p_l_s_s_MedianVertexLayerPositioning__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { MedianVertexLayerPositioning.class, Object.class, VertexLayerPositioning.class });
  }

  public MedianVertexLayerPositioning createInstance(final ContextManager contextManager) {
    final MedianVertexLayerPositioning instance = new MedianVertexLayerPositioning();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}
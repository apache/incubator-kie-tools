package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.stunner.client.lienzo.canvas.index.bounds.CanvasBoundsIndexerImpl;
import org.kie.workbench.common.stunner.core.client.canvas.index.bounds.CanvasBoundsIndexer;
import org.kie.workbench.common.stunner.core.graph.processing.index.bounds.BoundsIndexer;
import org.kie.workbench.common.stunner.core.graph.processing.index.bounds.NodeBoundsIndexer;

public class Type_factory__o_k_w_c_s_c_l_c_i_b_CanvasBoundsIndexerImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<CanvasBoundsIndexerImpl> { public Type_factory__o_k_w_c_s_c_l_c_i_b_CanvasBoundsIndexerImpl__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(CanvasBoundsIndexerImpl.class, "Type_factory__o_k_w_c_s_c_l_c_i_b_CanvasBoundsIndexerImpl__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { CanvasBoundsIndexerImpl.class, Object.class, CanvasBoundsIndexer.class, NodeBoundsIndexer.class, BoundsIndexer.class });
  }

  public CanvasBoundsIndexerImpl createInstance(final ContextManager contextManager) {
    final CanvasBoundsIndexerImpl instance = new CanvasBoundsIndexerImpl();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}
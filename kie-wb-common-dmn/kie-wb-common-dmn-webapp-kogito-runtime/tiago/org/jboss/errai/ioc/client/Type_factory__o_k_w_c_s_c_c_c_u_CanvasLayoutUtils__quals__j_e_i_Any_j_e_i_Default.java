package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.stunner.core.client.canvas.util.CanvasLayoutUtils;
import org.kie.workbench.common.stunner.core.graph.processing.index.bounds.GraphBoundsIndexer;
import org.kie.workbench.common.stunner.core.graph.processing.index.bounds.GraphBoundsIndexerImpl;

public class Type_factory__o_k_w_c_s_c_c_c_u_CanvasLayoutUtils__quals__j_e_i_Any_j_e_i_Default extends Factory<CanvasLayoutUtils> { public Type_factory__o_k_w_c_s_c_c_c_u_CanvasLayoutUtils__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(CanvasLayoutUtils.class, "Type_factory__o_k_w_c_s_c_c_c_u_CanvasLayoutUtils__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { CanvasLayoutUtils.class, Object.class });
  }

  public CanvasLayoutUtils createInstance(final ContextManager contextManager) {
    final GraphBoundsIndexer _graphBoundsIndexer_0 = (GraphBoundsIndexerImpl) contextManager.getInstance("Type_factory__o_k_w_c_s_c_g_p_i_b_GraphBoundsIndexerImpl__quals__j_e_i_Any_j_e_i_Default");
    final CanvasLayoutUtils instance = new CanvasLayoutUtils(_graphBoundsIndexer_0);
    registerDependentScopedReference(instance, _graphBoundsIndexer_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((CanvasLayoutUtils) instance, contextManager);
  }

  public void destroyInstanceHelper(final CanvasLayoutUtils instance, final ContextManager contextManager) {
    instance.destroy();
  }
}
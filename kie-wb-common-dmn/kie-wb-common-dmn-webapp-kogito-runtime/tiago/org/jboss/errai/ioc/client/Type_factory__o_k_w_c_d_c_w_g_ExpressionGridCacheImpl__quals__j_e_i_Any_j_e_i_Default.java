package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.dmn.client.widgets.grid.ExpressionGridCache;
import org.kie.workbench.common.dmn.client.widgets.grid.ExpressionGridCacheImpl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.AbstractCanvasControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.CanvasControl;

public class Type_factory__o_k_w_c_d_c_w_g_ExpressionGridCacheImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<ExpressionGridCacheImpl> { public Type_factory__o_k_w_c_d_c_w_g_ExpressionGridCacheImpl__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(ExpressionGridCacheImpl.class, "Type_factory__o_k_w_c_d_c_w_g_ExpressionGridCacheImpl__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { ExpressionGridCacheImpl.class, AbstractCanvasControl.class, Object.class, CanvasControl.class, ExpressionGridCache.class });
  }

  public ExpressionGridCacheImpl createInstance(final ContextManager contextManager) {
    final ExpressionGridCacheImpl instance = new ExpressionGridCacheImpl();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}
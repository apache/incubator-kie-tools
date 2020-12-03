package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.stunner.client.lienzo.components.views.LienzoTextTooltip;
import org.kie.workbench.common.stunner.core.client.components.views.AbstractCanvasTooltip;
import org.kie.workbench.common.stunner.core.client.components.views.CanvasTooltip;

public class Type_factory__o_k_w_c_s_c_l_c_v_LienzoTextTooltip__quals__j_e_i_Any_j_e_i_Default extends Factory<LienzoTextTooltip> { public Type_factory__o_k_w_c_s_c_l_c_v_LienzoTextTooltip__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(LienzoTextTooltip.class, "Type_factory__o_k_w_c_s_c_l_c_v_LienzoTextTooltip__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { LienzoTextTooltip.class, AbstractCanvasTooltip.class, Object.class, CanvasTooltip.class });
  }

  public LienzoTextTooltip createInstance(final ContextManager contextManager) {
    final LienzoTextTooltip instance = new LienzoTextTooltip();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}
package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.stunner.core.client.components.layout.LayoutHelper;
import org.kie.workbench.common.stunner.core.graph.processing.layout.LayoutService;
import org.kie.workbench.common.stunner.core.graph.processing.layout.sugiyama.SugiyamaLayoutService;

public class Type_factory__o_k_w_c_s_c_c_c_l_LayoutHelper__quals__j_e_i_Any_j_e_i_Default extends Factory<LayoutHelper> { public Type_factory__o_k_w_c_s_c_c_c_l_LayoutHelper__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(LayoutHelper.class, "Type_factory__o_k_w_c_s_c_c_c_l_LayoutHelper__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { LayoutHelper.class, Object.class });
  }

  public LayoutHelper createInstance(final ContextManager contextManager) {
    final LayoutService _layoutService_0 = (SugiyamaLayoutService) contextManager.getInstance("Type_factory__o_k_w_c_s_c_g_p_l_s_SugiyamaLayoutService__quals__j_e_i_Any_j_e_i_Default");
    final LayoutHelper instance = new LayoutHelper(_layoutService_0);
    registerDependentScopedReference(instance, _layoutService_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}
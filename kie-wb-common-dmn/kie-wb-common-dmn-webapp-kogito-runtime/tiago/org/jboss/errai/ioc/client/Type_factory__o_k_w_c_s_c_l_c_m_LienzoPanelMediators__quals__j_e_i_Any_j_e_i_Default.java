package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.stunner.client.lienzo.components.mediators.LienzoCanvasMediators;
import org.kie.workbench.common.stunner.client.lienzo.components.mediators.LienzoPanelMediators;
import org.kie.workbench.common.stunner.client.lienzo.components.mediators.ZoomLevelSelectorPresenter;

public class Type_factory__o_k_w_c_s_c_l_c_m_LienzoPanelMediators__quals__j_e_i_Any_j_e_i_Default extends Factory<LienzoPanelMediators> { public Type_factory__o_k_w_c_s_c_l_c_m_LienzoPanelMediators__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(LienzoPanelMediators.class, "Type_factory__o_k_w_c_s_c_l_c_m_LienzoPanelMediators__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { LienzoPanelMediators.class, Object.class });
  }

  public LienzoPanelMediators createInstance(final ContextManager contextManager) {
    final LienzoCanvasMediators _mediators_0 = (LienzoCanvasMediators) contextManager.getInstance("Type_factory__o_k_w_c_s_c_l_c_m_LienzoCanvasMediators__quals__j_e_i_Any_j_e_i_Default");
    final ZoomLevelSelectorPresenter _selector_1 = (ZoomLevelSelectorPresenter) contextManager.getInstance("Type_factory__o_k_w_c_s_c_l_c_m_ZoomLevelSelectorPresenter__quals__j_e_i_Any_j_e_i_Default");
    final LienzoPanelMediators instance = new LienzoPanelMediators(_mediators_0, _selector_1);
    registerDependentScopedReference(instance, _mediators_0);
    registerDependentScopedReference(instance, _selector_1);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((LienzoPanelMediators) instance, contextManager);
  }

  public void destroyInstanceHelper(final LienzoPanelMediators instance, final ContextManager contextManager) {
    instance.destroy();
  }
}
package org.jboss.errai.ioc.client;

import com.google.gwt.user.client.ui.IsWidget;
import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.stunner.client.lienzo.components.mediators.ZoomLevelSelector;
import org.kie.workbench.common.stunner.client.lienzo.components.mediators.ZoomLevelSelectorPresenter;
import org.kie.workbench.common.stunner.client.widgets.views.AnimatedFloatingWidgetView;
import org.kie.workbench.common.stunner.core.client.components.views.FloatingView;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;

public class Type_factory__o_k_w_c_s_c_l_c_m_ZoomLevelSelectorPresenter__quals__j_e_i_Any_j_e_i_Default extends Factory<ZoomLevelSelectorPresenter> { public Type_factory__o_k_w_c_s_c_l_c_m_ZoomLevelSelectorPresenter__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(ZoomLevelSelectorPresenter.class, "Type_factory__o_k_w_c_s_c_l_c_m_ZoomLevelSelectorPresenter__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { ZoomLevelSelectorPresenter.class, Object.class });
  }

  public ZoomLevelSelectorPresenter createInstance(final ContextManager contextManager) {
    final ClientTranslationService _translationService_0 = (ClientTranslationService) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_i_ClientTranslationService__quals__j_e_i_Any_j_e_i_Default");
    final FloatingView<IsWidget> _floatingView_1 = (AnimatedFloatingWidgetView) contextManager.getInstance("Type_factory__o_k_w_c_s_c_w_v_AnimatedFloatingWidgetView__quals__j_e_i_Any_j_e_i_Default");
    final ZoomLevelSelector _selector_2 = (ZoomLevelSelector) contextManager.getInstance("Type_factory__o_k_w_c_s_c_l_c_m_ZoomLevelSelector__quals__j_e_i_Any_j_e_i_Default");
    final ZoomLevelSelectorPresenter instance = new ZoomLevelSelectorPresenter(_translationService_0, _floatingView_1, _selector_2);
    registerDependentScopedReference(instance, _floatingView_1);
    registerDependentScopedReference(instance, _selector_2);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((ZoomLevelSelectorPresenter) instance, contextManager);
  }

  public void destroyInstanceHelper(final ZoomLevelSelectorPresenter instance, final ContextManager contextManager) {
    instance.destroy();
  }

  public void invokePostConstructs(final ZoomLevelSelectorPresenter instance) {
    instance.construct();
  }
}
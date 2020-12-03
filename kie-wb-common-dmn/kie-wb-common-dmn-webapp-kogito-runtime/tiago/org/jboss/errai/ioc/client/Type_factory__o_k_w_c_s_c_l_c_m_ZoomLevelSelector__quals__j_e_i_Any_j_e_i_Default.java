package org.jboss.errai.ioc.client;

import com.google.gwt.user.client.ui.IsWidget;
import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.stunner.client.lienzo.components.mediators.ZoomLevelSelector;
import org.kie.workbench.common.stunner.client.lienzo.components.mediators.ZoomLevelSelector.View;
import org.kie.workbench.common.stunner.client.lienzo.components.mediators.ZoomLevelSelectorView;

public class Type_factory__o_k_w_c_s_c_l_c_m_ZoomLevelSelector__quals__j_e_i_Any_j_e_i_Default extends Factory<ZoomLevelSelector> { public Type_factory__o_k_w_c_s_c_l_c_m_ZoomLevelSelector__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(ZoomLevelSelector.class, "Type_factory__o_k_w_c_s_c_l_c_m_ZoomLevelSelector__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { ZoomLevelSelector.class, Object.class, IsWidget.class });
  }

  public ZoomLevelSelector createInstance(final ContextManager contextManager) {
    final View _view_0 = (ZoomLevelSelectorView) contextManager.getInstance("Type_factory__o_k_w_c_s_c_l_c_m_ZoomLevelSelectorView__quals__j_e_i_Any_j_e_i_Default");
    final ZoomLevelSelector instance = new ZoomLevelSelector(_view_0);
    registerDependentScopedReference(instance, _view_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((ZoomLevelSelector) instance, contextManager);
  }

  public void destroyInstanceHelper(final ZoomLevelSelector instance, final ContextManager contextManager) {
    instance.destroy();
  }

  public void invokePostConstructs(final ZoomLevelSelector instance) {
    instance.init();
  }
}
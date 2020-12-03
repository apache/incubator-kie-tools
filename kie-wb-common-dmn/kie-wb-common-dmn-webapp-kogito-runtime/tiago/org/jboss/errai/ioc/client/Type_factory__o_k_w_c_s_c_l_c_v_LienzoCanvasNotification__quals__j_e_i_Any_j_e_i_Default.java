package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.stunner.client.lienzo.components.views.LienzoCanvasNotification;
import org.kie.workbench.common.stunner.client.lienzo.components.views.LienzoCanvasNotification.View;
import org.kie.workbench.common.stunner.client.lienzo.components.views.LienzoCanvasNotificationView;

public class Type_factory__o_k_w_c_s_c_l_c_v_LienzoCanvasNotification__quals__j_e_i_Any_j_e_i_Default extends Factory<LienzoCanvasNotification> { public Type_factory__o_k_w_c_s_c_l_c_v_LienzoCanvasNotification__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(LienzoCanvasNotification.class, "Type_factory__o_k_w_c_s_c_l_c_v_LienzoCanvasNotification__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { LienzoCanvasNotification.class, Object.class });
  }

  public LienzoCanvasNotification createInstance(final ContextManager contextManager) {
    final View _view_0 = (LienzoCanvasNotificationView) contextManager.getInstance("Type_factory__o_k_w_c_s_c_l_c_v_LienzoCanvasNotificationView__quals__j_e_i_Any_j_e_i_Default");
    final LienzoCanvasNotification instance = new LienzoCanvasNotification(_view_0);
    registerDependentScopedReference(instance, _view_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((LienzoCanvasNotification) instance, contextManager);
  }

  public void destroyInstanceHelper(final LienzoCanvasNotification instance, final ContextManager contextManager) {
    instance.destroy();
  }
}
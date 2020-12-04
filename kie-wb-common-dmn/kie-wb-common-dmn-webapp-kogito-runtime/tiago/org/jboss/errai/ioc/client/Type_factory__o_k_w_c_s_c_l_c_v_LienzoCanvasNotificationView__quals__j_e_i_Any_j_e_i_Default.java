package org.jboss.errai.ioc.client;

import com.google.gwt.user.client.ui.IsWidget;
import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.stunner.client.lienzo.components.views.AlertView;
import org.kie.workbench.common.stunner.client.lienzo.components.views.LienzoCanvasNotification.View;
import org.kie.workbench.common.stunner.client.lienzo.components.views.LienzoCanvasNotificationView;
import org.kie.workbench.common.stunner.client.widgets.views.AnimatedFloatingWidgetView;
import org.kie.workbench.common.stunner.core.client.components.views.FloatingView;

public class Type_factory__o_k_w_c_s_c_l_c_v_LienzoCanvasNotificationView__quals__j_e_i_Any_j_e_i_Default extends Factory<LienzoCanvasNotificationView> { public Type_factory__o_k_w_c_s_c_l_c_v_LienzoCanvasNotificationView__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(LienzoCanvasNotificationView.class, "Type_factory__o_k_w_c_s_c_l_c_v_LienzoCanvasNotificationView__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { LienzoCanvasNotificationView.class, Object.class, View.class });
  }

  public LienzoCanvasNotificationView createInstance(final ContextManager contextManager) {
    final AlertView _alertView_1 = (AlertView) contextManager.getInstance("Type_factory__o_k_w_c_s_c_l_c_v_AlertView__quals__j_e_i_Any_j_e_i_Default");
    final FloatingView<IsWidget> _floatingView_0 = (AnimatedFloatingWidgetView) contextManager.getInstance("Type_factory__o_k_w_c_s_c_w_v_AnimatedFloatingWidgetView__quals__j_e_i_Any_j_e_i_Default");
    final LienzoCanvasNotificationView instance = new LienzoCanvasNotificationView(_floatingView_0, _alertView_1);
    registerDependentScopedReference(instance, _alertView_1);
    registerDependentScopedReference(instance, _floatingView_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((LienzoCanvasNotificationView) instance, contextManager);
  }

  public void destroyInstanceHelper(final LienzoCanvasNotificationView instance, final ContextManager contextManager) {
    instance.destroy();
  }

  public void invokePostConstructs(final LienzoCanvasNotificationView instance) {
    instance.init();
  }
}